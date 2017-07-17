package com.oltranz.pf.n_payfuel.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.oltranz.pf.n_payfuel.R;
import com.oltranz.pf.n_payfuel.config.StatusConfig;
import com.oltranz.pf.n_payfuel.entities.MNozzle;
import com.oltranz.pf.n_payfuel.entities.MPayment;
import com.oltranz.pf.n_payfuel.entities.MSales;
import com.oltranz.pf.n_payfuel.models.login.LoginResponse;
import com.oltranz.pf.n_payfuel.utilities.DataFactory;
import com.oltranz.pf.n_payfuel.utilities.DbBulk;
import com.oltranz.pf.n_payfuel.utilities.IdGenerator;
import com.oltranz.pf.n_payfuel.utilities.adapters.GridSpacingItemDecoration;
import com.oltranz.pf.n_payfuel.utilities.adapters.SalesNozzleAdapter;
import com.oltranz.pf.n_payfuel.utilities.functional.ConfirmTransaction;
import com.oltranz.pf.n_payfuel.utilities.functional.FunctionalPayment;
import com.oltranz.pf.n_payfuel.utilities.functional.TransactionValue;
import com.oltranz.pf.n_payfuel.utilities.printing.TransactionPrint;
import com.oltranz.pf.n_payfuel.utilities.services.TransactionRectifier;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnSalesModuleInteraction} interface
 * to handle interaction events.
 * Use the {@link Sales#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Sales extends Fragment implements SalesNozzleAdapter.OnChooseSellingNozzle,
        TransactionValue.OnTransactionValue,
        FunctionalPayment.OnPaymentMethod,
        ConfirmTransaction.OnConfirmTransaction,
        TransactionPrint.OnTransactionPrint{
    private static final String LOGIN_PARAM = "LOGIN_PARAM";
    @BindView(R.id.mNozzleView)
    RecyclerView nozzleView;
    private OnSalesModuleInteraction mListener;
    private LoginResponse loginResponse;
    private List<MNozzle> mNozzles;
    private MSales mSales;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;
    private SalesNozzleAdapter adapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // now call fragments method here
            String action = intent.getAction();
            if(action == null)
                return;
            if(action.equals(TransactionRectifier.NOZZLE_BROADCAST_ACTION)){
                refreshRecycler();
            }
        }
    };
    private TransactionValue transactionValue;
    private FunctionalPayment functionalPayment;
    private ConfirmTransaction confirmTransaction;

    public Sales() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param loginResponseParam Parameter 1.
     * @return A new instance of fragment Sales.
     */
    public static Sales newInstance(String loginResponseParam) {
        Sales fragment = new Sales();
        Bundle args = new Bundle();
        args.putString(LOGIN_PARAM, loginResponseParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loginResponse = (LoginResponse) DataFactory.stringToObject(LoginResponse.class, getArguments().getString(LOGIN_PARAM));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // start listening for refresh local file list in
        try {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver,
                    new IntentFilter(TransactionRectifier.NOZZLE_BROADCAST_FILTER));
        }catch (Exception e){
            e.printStackTrace();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.sales_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        mNozzles = MNozzle.listAll(MNozzle.class);
        if(mNozzles.isEmpty()){
            mListener.onSalesModule(false, "Empty nozzle list.");
            return;
        }

        adapter = new SalesNozzleAdapter(this, getContext(), mNozzles);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        nozzleView.setLayoutManager(mLayoutManager);
        nozzleView.addItemDecoration(new GridSpacingItemDecoration(2, GridSpacingItemDecoration.dpToPx(getContext(), 2), true));
        nozzleView.setItemAnimator(new DefaultItemAnimator());
        nozzleView.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSalesModuleInteraction) {
            mListener = (OnSalesModuleInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onChooseNozzle(boolean isSelected, MNozzle mNozzle) {
        transactionValue = new TransactionValue(Sales.this, this.getContext(), mNozzle);
        transactionValue.setValue();
    }

    @Override
    public void onTransactionValue(boolean isDone, MSales sales) {
        if(!isDone){
            transactionValue = null;
            return;
        }
        mSales = sales;
        List<MPayment> mPayments = MPayment.listAll(MPayment.class);
        functionalPayment = new FunctionalPayment(Sales.this, this.getActivity(), mSales);
        functionalPayment.setValue();
    }

    @Override
    public void onPaymentMethod(boolean isDone, MSales sales) {
        if(!isDone){
            functionalPayment.reset();
            functionalPayment = null;
            return;
        }
        //provide transactionID, userId, branchId and put it on upload queue

        MSales tempSales = sales;
        String txId;
        do{
            String tempId = IdGenerator.GEN(loginResponse.getmUser().getUserId())+"";
            txId = tempId;
            tempSales = DbBulk.getTransaction(tempId);
        }while (tempSales != null);
        sales.setDeviceTransactionId(txId);
        sales.setUserId(loginResponse.getmUser().getUserId());
        sales.setDeviceId(Build.SERIAL);
        try {
            sales.setDeviceTransactionTime(DataFactory.formatDate(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
            sales.setDeviceTransactionTime(new Date().toString());
        }

        confirmTransaction = new ConfirmTransaction(Sales.this, this.getContext(), sales, loginResponse.getmUser().getName());
        confirmTransaction.setValue();
    }

    @Override
    public void onPaymentResetExtra(boolean isReset) {
        if(isReset){
            functionalPayment.reset();
            functionalPayment = new FunctionalPayment(this, getActivity(), mSales);
            functionalPayment.setValue();
        }
    }

    @Override
    public void onConfirmTransaction(boolean isDone, MSales mSales) {
        if(!isDone){
            confirmTransaction.reset();
            confirmTransaction = null;
            return;
        }

        if(functionalPayment != null){
            functionalPayment.reset();
            functionalPayment = null;
        }

        if(transactionValue != null){
            transactionValue.reset();
            transactionValue = null;
        }

        if(confirmTransaction != null)
            confirmTransaction = null;

        MPayment mPayment = DbBulk.getPayment(mSales.getPaymentModeId());
        if(mPayment == null){
            functionalPayment.reset();
            functionalPayment = new FunctionalPayment(this, getActivity(), mSales);
            functionalPayment.setValue();
            Toast.makeText(getContext(), "Can't find payment method.", Toast.LENGTH_SHORT).show();
            return;
        }
        //Set transaction status according to payment and save it for later upload by TransactionRectifier
        if (mPayment.getName().toLowerCase().contains("cash")||
                mPayment.getName().toLowerCase().contains("debt") ||
                mPayment.getName().toLowerCase().contains("voucher") ||
                mPayment.getName().toLowerCase().contains(" card")){
            mSales.setStatus(StatusConfig.SUCCESS_NOT_UPLOADED);
        }else{
            mSales.setStatus(StatusConfig.PENDING);
        }

        long persistResult = mSales.save();
        if(persistResult < 0){
            functionalPayment.reset();
            functionalPayment = new FunctionalPayment(this, getActivity(), mSales);
            functionalPayment.setValue();
            Toast.makeText(getContext(), "Failed to record transaction on local DB.", Toast.LENGTH_SHORT).show();
            return;
        }

        MNozzle mNozzle = DbBulk.getNozzle(mSales.getNozzleId());
        if(mNozzle != null){
            incrementIndex(mNozzle, mSales.getQuantity());
        }
        genReceipt("Do you want to generate receipt?", mSales);
    }

    private void genReceipt(String message, final MSales mSales){
        try {
            try {
                builder = new AlertDialog.Builder(getContext(), R.style.SimpleDialog);
                builder.setMessage(message != null ? message : "NO_MESSAGE")
                        .setTitle(R.string.dialog_title);
                // Add the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if(mSales.getStatus().equals(StatusConfig.PENDING)){
                            mSales.setStatus(StatusConfig.PRINT_AFTER_PENDING);
                            long persistResult = mSales.save();
                            if(persistResult < 0)
                                Toast.makeText(getContext(), "Failed to change transaction status on local DB.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try{
                            if (mSales.getStatus().equals(StatusConfig.SUCCESS) || mSales.getStatus().equals(StatusConfig.SUCCESS_NOT_UPLOADED)){
                                TransactionPrint transactionPrint = new TransactionPrint(Sales.this, getContext(), mSales);
                                transactionPrint.generateReceipt();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Printing Error: "+e.getCause(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
                dialog = builder.create();
                dialog.show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), message != null ? message : "NO_MESSAGE", Toast.LENGTH_SHORT).show();
            }

            refreshRecycler();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void incrementIndex(MNozzle nozzle, String addValue){
        try {
            Double newIndex=Double.valueOf(nozzle.getIndexCount())+Double.valueOf(addValue);
            nozzle.setIndexCount(""+newIndex);
            nozzle.save();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void refreshRecycler(){
        this.mNozzles = MNozzle.listAll(MNozzle.class);
        if(mNozzles.isEmpty()){
            mListener.onSalesModule(false, "Empty nozzle list.");
            return;
        }

        adapter.refreshAdapter(mNozzles);
    }

    @Override
    public void onPrintResult(String printingMessage) {
        Toast.makeText(getContext(), "Printer: "+printingMessage, Toast.LENGTH_SHORT).show();
    }
    public interface OnSalesModuleInteraction {
        void onSalesModule(boolean isModuleFine, Object object);
    }
}
