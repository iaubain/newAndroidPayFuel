package com.oltranz.pf.n_payfuel.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.oltranz.pf.n_payfuel.R;
import com.oltranz.pf.n_payfuel.entities.MNozzle;
import com.oltranz.pf.n_payfuel.entities.MPump;
import com.oltranz.pf.n_payfuel.entities.MUser;
import com.oltranz.pf.n_payfuel.models.login.LoginRequest;
import com.oltranz.pf.n_payfuel.models.login.LoginResponse;
import com.oltranz.pf.n_payfuel.models.pump.NozzleModel;
import com.oltranz.pf.n_payfuel.models.pump.PumpModel;
import com.oltranz.pf.n_payfuel.models.pump.PumpResponse;
import com.oltranz.pf.n_payfuel.models.reserve.ReserveModel;
import com.oltranz.pf.n_payfuel.utilities.DataFactory;
import com.oltranz.pf.n_payfuel.utilities.DbBulk;
import com.oltranz.pf.n_payfuel.utilities.adapters.ChooseNozzleAdapter;
import com.oltranz.pf.n_payfuel.utilities.adapters.ChoosePumpAdapter;
import com.oltranz.pf.n_payfuel.utilities.adapters.GridSpacingItemDecoration;
import com.oltranz.pf.n_payfuel.utilities.loaders.AuthLoader;
import com.oltranz.pf.n_payfuel.utilities.loaders.PaymentsLoader;
import com.oltranz.pf.n_payfuel.utilities.loaders.PumpsLoader;
import com.oltranz.pf.n_payfuel.utilities.loaders.ReserveLoader;
import com.oltranz.pf.n_payfuel.utilities.views.MyButton;
import com.oltranz.pf.n_payfuel.utilities.views.MyEdit;
import com.oltranz.pf.n_payfuel.utilities.views.MyLabel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLoginInteraction} interface
 * to handle interaction events.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment implements AuthLoader.AuthLoaderInteraction,
        PaymentsLoader.PaymentLoaderInteraction,
        PumpsLoader.PumpsLoaderInteraction,
        ChoosePumpAdapter.OnChoosePumpInteraction,
        ChooseNozzleAdapter.OnChooseNozzleInteraction,
        ReserveLoader.ReserveLoaderInteraction {

    @BindView(R.id.pin)
    MyEdit pin;
    @BindView(R.id.pinRecover)
    MyLabel pinRecover;
    @BindView(R.id.submit)
    MyButton submit;
    private OnLoginInteraction mListener;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;
    private LoginResponse loginResponse;

    private ChoosePumpAdapter pumpAdapter;
    private RecyclerView pumpRecycler;
    private ChooseNozzleAdapter nozzleAdapter;

    private HashMap<PumpModel, List<NozzleModel>> takenNozzle = new LinkedHashMap<>();

    public Login() {
        // Required empty public constructor
    }

    public static Login newInstance() {
        return new Login();
    }

    private void addTaken(NozzleModel nozzleModel, PumpModel pumpModel){
        List<NozzleModel> mNozzles = new ArrayList<>(Collections.singletonList(nozzleModel));
        if(!takenNozzle.containsKey(pumpModel)){
            takenNozzle.put(pumpModel, mNozzles);
            return;
        }

        List<NozzleModel> nozzleModels = takenNozzle.get(pumpModel);
        if(nozzleModels.isEmpty())
            takenNozzle.put(pumpModel, mNozzles);
        else{
            List<NozzleModel> tempNozzles = nozzleModels;
            tempNozzles.add(nozzleModel);
            takenNozzle.put(pumpModel, tempNozzles);
        }
    }

    private void removeTaken(NozzleModel nozzleModel, PumpModel pumpModel){
        List<NozzleModel> mNozzles = takenNozzle.get(pumpModel);
        if(mNozzles.isEmpty())
            return;
        List<NozzleModel> tempNozzles = new ArrayList<>();
        tempNozzles.addAll(mNozzles);
        for(NozzleModel nozzle : mNozzles){
            if(nozzle.equals(nozzleModel))
                tempNozzles.remove(nozzle);
        }
        if(tempNozzles.size() <= 0){
            takenNozzle.remove(pumpModel);
        }else{
            takenNozzle.put(pumpModel, tempNozzles);
        }
    }

    private boolean isPumpAvailable(PumpModel pumpModel){
        return takenNozzle.containsKey(pumpModel);
    }

    private boolean isTaken(NozzleModel nozzleModel, PumpModel pumpModel){
        List<NozzleModel> mNozzles = takenNozzle.get(pumpModel);
        if(mNozzles.isEmpty())
            return false;
        for(NozzleModel nozzle : mNozzles){
            if(nozzle.equals(nozzleModel))
                return true;
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.login_layout, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!DbBulk.isDeviceRegistered()){
                    //show a popup
                    popBox("This device is not registered");
                }else{
                    List<MUser> mUsers = DbBulk.getActiveUser();
                    String userDetails = "Access denied\n\n";
                    boolean isSameUser = false;
                    if(mUsers != null && !mUsers.isEmpty()){
                        //show popup with current user
                        for(MUser mUser : mUsers){
                            userDetails += "Name: "+mUser.getName()+"\n Is already logged in and has status\n";
                            userDetails += "Session: "+mUser.getSessionStatus()+"\n";
                            userDetails += "-------------------\n";
                            if(mUser.getUserPin().equals(pin.getText().toString()))
                                isSameUser = true;
                        }
                    }else{
                        isSameUser = true;
                    }

                    if(!isSameUser)
                        popBox(userDetails+" Please contact your branch manager.");
                    else{
                        //proceed with authenticating
                        if(TextUtils.isEmpty(pin.getText().toString()) || pin.getText().toString().length() < 3){
                            pin.requestFocus();
                            pin.setError("Invalid PIN");
                        }else{
                            //show progress dialog
                            mProgress("Authenticating...");

                            //get device serial number Build.SERIAL
                            AuthLoader authLoader = new AuthLoader(Login.this, getContext(), new LoginRequest(Build.SERIAL, pin.getText().toString()));
                            authLoader.startLoading();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginInteraction) {
            mListener = (OnLoginInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAuthLoader(boolean isLoaded, Object object) {
        dismissProgress();
        pin.setText("");
        pin.requestFocus();
        if(!isLoaded){
            popBox((String) object);
            return;
        }
        //Continue with other chain activities, Load Payments
        try {
            loginResponse = (LoginResponse) object;
            mProgress("Loading payment methods");
            PaymentsLoader paymentsLoader = new PaymentsLoader(this, getContext(), loginResponse.getmUser().getUserId());
            paymentsLoader.startLoading();
            //popBox("Successful login");
        }catch (Exception e){
            e.printStackTrace();
            popBox("Failed to get payment methods due to: "+e.getMessage());
        }
    }

    private void popBox(String message){
        try {
            builder = new AlertDialog.Builder(getContext(), R.style.SimpleDialog);
            builder.setMessage(message != null ? message : "NO_MESSAGE")
                    .setTitle(R.string.dialog_title);
            // Add the buttons
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), message != null ? message : "NO_MESSAGE", Toast.LENGTH_SHORT).show();
        }
    }

    private void mProgress(String message){
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.setMessage(message != null ? message : "NO_MESSAGE");
        progressDialog.show();
    }

    private void dismissProgress(){
        if (progressDialog != null)
            if (progressDialog.isShowing())
                progressDialog.dismiss();
    }

    private void updateMessage(String message){
        if (progressDialog != null)
            if (progressDialog.isShowing())
                progressDialog.setMessage(message != null ? message : "NO_MESSAGE");
    }

    private void choosePumpBox(List<PumpModel> mPumps){
        try {
            final Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.pump_layout);

            final MyLabel boxTitle = (MyLabel) dialog.findViewById(R.id.dialogTitle);
            ImageView close = (ImageView) dialog.findViewById(R.id.icClose);
            pumpRecycler = (RecyclerView) dialog.findViewById(R.id.mPumpView);
            MyButton next = (MyButton) dialog.findViewById(R.id.ok);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(takenNozzle.isEmpty()){
                        popBox("Select at least one pump's nozzle.");
                        return;
                    }
                    List<ReserveModel> myReserve = new ArrayList<ReserveModel>();
                    try {
                        DbBulk.resetPump();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    for(Map.Entry<PumpModel, List<NozzleModel>> entry : takenNozzle.entrySet()){
                        List<NozzleModel> myNozzles = entry.getValue();
                        for(NozzleModel nozzleModel : myNozzles){
                            MPump mPump = DbBulk.savePump(DataFactory.tunePump(entry.getKey()));
                            if(mPump == null){
                                popBox("Failed to record pump to local Database.");
                                try {
                                    DbBulk.resetPump();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return;
                            }
                            MNozzle mNozzle = DbBulk.saveNozzle(DataFactory.tuneNozzle(nozzleModel, mPump));
                            if(mNozzle == null){
                                popBox("Failed to record nozzle to local Database.");
                                try {
                                    DbBulk.resetPump();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return;
                            }

                            myReserve.add(DataFactory.myReserve(loginResponse.getmUser().getUserId(), entry.getKey(), nozzleModel));
                        }
                    }

                    if(myReserve.isEmpty()){
                        popBox("Nozzle reservation failed. Consider restating the app");
                        return;
                    }
                    mProgress("Reserving chosen nozzles");
                    ReserveLoader reserveLoader = new ReserveLoader(Login.this, getContext(), myReserve);
                    reserveLoader.startLoading();
                    dialog.dismiss();
                }
            });

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        MUser mUser = DbBulk.getLastLogin();
                        if(MUser.delete(mUser)){
                            popBox("Action aborted unexpectedly.");
                            dialog.dismiss();
                        }else{
                            Toast.makeText(getContext(), "Can't abort process now", Toast.LENGTH_LONG).show();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Can't abort process now", Toast.LENGTH_LONG).show();
                    }

                }
            });
            boxTitle.setText("CHOOSE PUMP");

            //setting box recycle view content
            pumpAdapter = new ChoosePumpAdapter(this, getContext(), mPumps);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
            pumpRecycler.setLayoutManager(mLayoutManager);
            pumpRecycler.addItemDecoration(new GridSpacingItemDecoration(2, GridSpacingItemDecoration.dpToPx(getContext(),10), true));
            pumpRecycler.setItemAnimator(new DefaultItemAnimator());
            pumpRecycler.setAdapter(pumpAdapter);

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseNozzleDialog(PumpModel pumpModel){
        try {
            final Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.nozzle_layout);

            final MyLabel boxTitle = (MyLabel) dialog.findViewById(R.id.dialogTitle);
            RecyclerView nozzleRecycler = (RecyclerView) dialog.findViewById(R.id.mNozzleView);
            MyButton ok = (MyButton) dialog.findViewById(R.id.ok);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            boxTitle.setText("CHOOSE NOZZLE");

            //setting box recycle view content
            nozzleAdapter = new ChooseNozzleAdapter(this, getContext(), pumpModel.getNozzleList(), pumpModel);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            nozzleRecycler.setLayoutManager(mLayoutManager);
            nozzleRecycler.setHasFixedSize(true);
            //nozzleRecycler.addItemDecoration(new GridSpacingItemDecoration(2, GridSpacingItemDecoration.dpToPx(getContext(),10), true));
            nozzleRecycler.setItemAnimator(new DefaultItemAnimator());
            nozzleRecycler.setAdapter(nozzleAdapter);

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentLoader(boolean isLoaded, Object object) {
        dismissProgress();
        if(!isLoaded){
            popBox((String) object);
            return;
        }
        //Continue with other chain activities, request pumps
        //List<MPayment> mPayments = MPayment.listAll(MPayment.class);
        if(MPump.count(MPump.class) > 0 && MNozzle.count(MNozzle.class) > 0){
            //go to userhome
            mListener.loginInteraction(true, loginResponse);
            return;
        }
        mProgress("Loading pumps");
        PumpsLoader pumpsLoader = new PumpsLoader(this, getContext(), loginResponse.getmUser().getBranchId());
        pumpsLoader.startLoading();
    }

    @Override
    public void onPumpsLoader(boolean isLoaded, Object object) {
        dismissProgress();
        if(!isLoaded){
            popBox((String) object);
            return;
        }
        choosePumpBox(((PumpResponse) object).getPumpModel());
    }

    @Override
    public void onChoosePump(boolean isDetails, PumpModel pumpModel) {
        if(isDetails){
            String pumpDetails = "";
            pumpDetails += "Branch Name: "+pumpModel.getBranchName()+"\n";
            pumpDetails += "Pump Name: "+pumpModel.getPumpName()+"\n";
            pumpDetails += "Pump ID: "+pumpModel.getPumpId()+"\n";
            pumpDetails += "Pump Status: "+pumpModel.getStatus()+"\n\n";
            pumpDetails += "Pump Nozzles: \n";
            String pumpNozzles = "";
            for(NozzleModel nozzleModel : pumpModel.getNozzleList()){
                pumpNozzles += "\t Nozzle Name: "+nozzleModel.getNozzleName()+"\n";
                pumpNozzles += "\t Nozzle Product: "+nozzleModel.getProductName()+"\n";
                pumpNozzles += "\t Nozzle Index: "+nozzleModel.getIndexCount()+"\n";
                pumpNozzles += "\t Nozzle Status: "+nozzleModel.getStatus()+"\n";
                String nozzleHeld = nozzleModel.getUserName() == null || nozzleModel.getUserName().equals("") ? "No_Name" : nozzleModel.getUserName();
                String nozzleAvailable = nozzleModel.getStatus().equals("8") ? "Taken by "+nozzleHeld : nozzleModel.getStatus().equals("9") ? loginResponse.getmUser().getName() != null ? loginResponse.getmUser().getName() : "Taken  by you" : "Available";
                pumpNozzles += "\t Nozzle Availability: "+nozzleAvailable+"\n__________________________\n";
            }
            pumpDetails += pumpNozzles;
            popBox(pumpDetails);

            return;
        }
        chooseNozzleDialog(pumpModel);
    }

    @Override
    public void onChooseNozzle(boolean isAdd, PumpModel pumpModel, NozzleModel nozzleModel) {
        if(!isAdd){
            if(isPumpAvailable(pumpModel) && isTaken(nozzleModel, pumpModel)){
                removeTaken(nozzleModel, pumpModel);
            }
        }else{
            addTaken(nozzleModel, pumpModel);

        }
        pumpAdapter.notifyDataSetChanged();
    }

    @Override
    public void onReserveLoader(boolean isLoaded, Object object) {
        dismissProgress();
        if(!isLoaded){
            popBox((String) object);
            try {
                DbBulk.resetPump();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        //Successful login, and now pass the control to the main activity
        //popBox("Successful Login");
        //List<MPayment> mPayments = MPayment.listAll(MPayment.class);
        mListener.loginInteraction(true, loginResponse);
    }

    public interface OnLoginInteraction {
        void loginInteraction(boolean isSuccess, Object object);
    }
}
