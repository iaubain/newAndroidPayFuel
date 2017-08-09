package com.oltranz.pf.n_payfuel.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.oltranz.pf.n_payfuel.R;
import com.oltranz.pf.n_payfuel.config.AppFlow;
import com.oltranz.pf.n_payfuel.config.StatusConfig;
import com.oltranz.pf.n_payfuel.entities.MNozzle;
import com.oltranz.pf.n_payfuel.entities.MSales;
import com.oltranz.pf.n_payfuel.models.login.LoginResponse;
import com.oltranz.pf.n_payfuel.utilities.DataFactory;
import com.oltranz.pf.n_payfuel.utilities.DbBulk;
import com.oltranz.pf.n_payfuel.utilities.adapters.HistoryAdapter;
import com.oltranz.pf.n_payfuel.utilities.loaders.LocalHistoryLoader;
import com.oltranz.pf.n_payfuel.utilities.views.MyEdit;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnSalesHistory} interface
 * to handle interaction events.
 * Use the {@link HistorySales#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistorySales extends Fragment implements HistoryAdapter.OnHistoryInteraction,LocalHistoryLoader.OnLocalHistory {
    private static final String LOGIN_PARAM = "LOGIN_PARAM";
    @BindView(R.id.search)
    MyEdit search;
    @BindView(R.id.mTransactions)
    RecyclerView historyView;
    private LoginResponse loginResponse;
    private OnSalesHistory mListener;
    private List<MSales> mSalesList;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;
    private HistoryAdapter adapter;

    public HistorySales() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param loginResponseParam Parameter 1.
     * @return A new instance of fragment HistorySales.
     */
    public static HistorySales newInstance(String loginResponseParam) {
        HistorySales fragment = new HistorySales();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.history_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        if (loginResponse == null) {
            mListener.onSalesHistory(false, null);
            return;
        }
        mSalesList = new ArrayList<>();
        adapter = new HistoryAdapter(HistorySales.this, getContext(), mSalesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        historyView.setLayoutManager(mLayoutManager);
        historyView.setHasFixedSize(true);
        historyView.setItemAnimator(new DefaultItemAnimator());
        historyView.setAdapter(adapter);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mProgress("Loading history");
        LocalHistoryLoader localHistoryLoader = new LocalHistoryLoader(HistorySales.this, loginResponse.getmUser().getUserId());
        localHistoryLoader.startLoading();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSalesHistory) {
            mListener = (OnSalesHistory) context;
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

    private void popBoxExit(String message){
        try {
            builder = new AlertDialog.Builder(getContext(), R.style.SimpleDialog);
            builder.setMessage(message != null ? message : "NO_MESSAGE")
                    .setTitle(R.string.dialog_title);
            // Add the buttons
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mListener.onSalesHistory(false, null);
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

    @Override
    public void onHistoryAdpter(boolean isDetails, MSales mSales) {
        if (isDetails){
            //Show transaction details pobBox()
            String displayStatus = mSales.getStatus().equals(StatusConfig.SUCCESS) ||
                    mSales.getStatus().equals(StatusConfig.SUCCESS_NOT_UPLOADED) ? "Success" :
                    mSales.getStatus().equals(StatusConfig.PENDING) ||
                            mSales.getStatus().equals(StatusConfig.PRINT_AFTER_PENDING) ? "Pending" : "Failed";
            String details = "Transaction details\n\n";
            details += "ID: "+mSales.getDeviceTransactionId()+"\n";
            details += "Amount: "+mSales.getAmount()+" "+ AppFlow.CURRENCY+"\n";
            details += "Quantity: "+mSales.getQuantity()+" "+ AppFlow.QUANTITY_MEASURE+"\n";
            MNozzle mNozzle = DbBulk.getNozzle(mSales.getNozzleId());
            String product = mNozzle != null ? mNozzle.getProductName() : "N/A";
            details += "Product: "+product+"\n";
            details += "Status: "+displayStatus+"\n";
            details += "Date: "+mSales.getDeviceTransactionTime()+"\n";
            details += "Code: "+mSales.getStatus();
            popBox(details);
        }
    }

    @Override
    public void onHistory(boolean isDone, String message, List<MSales> mSalesList) {
        dismissProgress();
        if(!isDone && (mSalesList == null || mSalesList.isEmpty())){
            //popExit
            popBoxExit(message);
            return;
        }
        if(mSalesList.isEmpty()){
            popBoxExit("Empty transaction list");
            return;
        }
        adapter.refreshAdapter(mSalesList);
    }

    public interface OnSalesHistory {
        void onSalesHistory(boolean isModuleFine, Object object);
    }
}
