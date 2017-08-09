package com.oltranz.pf.n_payfuel.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.oltranz.pf.n_payfuel.R;
import com.oltranz.pf.n_payfuel.config.StatusConfig;
import com.oltranz.pf.n_payfuel.entities.MNozzle;
import com.oltranz.pf.n_payfuel.entities.MPayment;
import com.oltranz.pf.n_payfuel.entities.MSales;
import com.oltranz.pf.n_payfuel.models.login.LoginResponse;
import com.oltranz.pf.n_payfuel.models.vouchers.Voucher;
import com.oltranz.pf.n_payfuel.utilities.DataFactory;
import com.oltranz.pf.n_payfuel.utilities.DbBulk;
import com.oltranz.pf.n_payfuel.utilities.IdGenerator;
import com.oltranz.pf.n_payfuel.utilities.adapters.GridSpacingItemDecoration;
import com.oltranz.pf.n_payfuel.utilities.adapters.SalesNozzleAdapter;
import com.oltranz.pf.n_payfuel.utilities.loaders.VoucherLoader;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnVoucherModule} interface
 * to handle interaction events.
 * Use the {@link VoucherModule#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VoucherModule extends Fragment implements SalesNozzleAdapter.OnChooseSellingNozzle,
        VoucherLoader.OnVoucherLoader {
    private static final String LOGIN_PARAM = "LOGIN_PARAM";
    @BindView(R.id.mNozzleView)
    RecyclerView nozzleView;
    private LoginResponse loginResponse;
    private OnVoucherModule mListener;
    private List<MNozzle> mNozzles;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;
    private SalesNozzleAdapter adapter;
    private MNozzle mNozzle;
    private String codeResult;
    private Voucher voucher;

    public VoucherModule() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param loginResponseParam Parameter 1.
     * @return A new instance of fragment VoucherModule.
     */
    public static VoucherModule newInstance(String loginResponseParam) {
        VoucherModule fragment = new VoucherModule();
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
        return inflater.inflate(R.layout.voucher_module, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mNozzles = new ArrayList<>();
        adapter = new SalesNozzleAdapter(this, getContext(), mNozzles);
        mNozzles = MNozzle.listAll(MNozzle.class);
        if (mNozzles.isEmpty()) {
            mListener.onVoucherModule(false, "Empty nozzle list.");
            return;
        }
        adapter.refreshAdapter(mNozzles);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        nozzleView.setLayoutManager(mLayoutManager);
        nozzleView.addItemDecoration(new GridSpacingItemDecoration(2, GridSpacingItemDecoration.dpToPx(getContext(), 2), true));
        nozzleView.setItemAnimator(new DefaultItemAnimator());
        nozzleView.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnVoucherModule) {
            mListener = (OnVoucherModule) context;
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
    public void onChooseNozzle(boolean isSelected, MNozzle mNozzle) {
        this.mNozzle = mNozzle;
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(VoucherModule.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan Code and Bars");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (data == null || resultCode != Activity.RESULT_OK) {
            mNozzle = null;
            return;
        }
        this.codeResult = result.getContents();
        mProgress("Validating voucher: " + codeResult);
        VoucherLoader voucherLoader = new VoucherLoader(getContext(), VoucherModule.this);
        voucherLoader.validateVoucher(codeResult);
    }

    private void canRedeem(String dispMessage) {
        String message = "Warning";
        try {
            builder = new AlertDialog.Builder(getContext(), R.style.SimpleDialog);
            builder.setMessage(dispMessage != null ? dispMessage : "NO_MESSAGE");

            // Add the buttons
            builder.setPositiveButton("REDEEM", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    if (voucher.getIsValid()) {
                        mProgress("Redeeming the voucher: " + codeResult);
                        VoucherLoader voucherLoader = new VoucherLoader(getContext(), VoucherModule.this);
                        voucherLoader.redeemVoucher(codeResult);
                    } else {
                        popInfo("Voucher: " + codeResult + "\n" + "Is not valid");
                    }
                }
            });

            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    codeResult = "";
                    mNozzle = null;
                }
            });
            dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), message + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void mProgress(String message) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.setMessage(message != null ? message : "NO_MESSAGE");
        progressDialog.show();
    }

    private void dismissProgress() {
        if (progressDialog != null)
            if (progressDialog.isShowing())
                progressDialog.dismiss();
    }

    private void popInfo(String message) {
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

    @Override
    public void onValidateVoucher(boolean isDone, Object resultObject) {
        dismissProgress();
        if (!isDone) {
            popInfo((String) resultObject);
            mNozzle = null;
            codeResult = "";
            return;
        }
        this.voucher = (Voucher) resultObject;
        String valid = voucher.getIsValid() == true ? "Yes" : "No";
        String customer = "N/A";
        String provided = "N/A";
        String amount = "0";
        if (voucher.getVoucherAmount() != null) {
            amount = voucher.getVoucherAmount() + "";
        }
        if (voucher.getCustomerMerchant() != null) {
            if (voucher.getCustomerMerchant().getCustomer() != null)
                customer = voucher.getCustomerMerchant().getCustomer().getName();

            if (voucher.getCustomerMerchant().getMerchant() != null)
                provided = voucher.getCustomerMerchant().getMerchant().getName();
        }

        DateFormat dFormat = new java.text.SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.getDefault());
        String generated = voucher.getRecordDate() != null ? voucher.getRecordDate() : dFormat.format(new Date());
        String info = "QR number: " + codeResult + "\n" +
                "Amount: " + amount + "\n" +
                "Valid: " + valid + "\n" +
                "Customer: " + customer + "\n" +
                "Provided by: " + provided + "\n" +
                "Generated: " + generated + "\n" +

                "\n If you wish to consume it click REDEEM button";
        canRedeem(info);
    }

    @Override
    public void onRedeemVoucher(boolean isDone, String redeemResult) {
        dismissProgress();
        if (isDone) {
            //save local transaction

            try {
                MSales tempSales;
                String txId;
                do {
                    String tempId = IdGenerator.GEN(loginResponse.getmUser().getUserId()) + "";
                    txId = tempId;
                    tempSales = DbBulk.getTransaction(tempId);
                } while (tempSales != null);
                tempSales = new MSales();
                tempSales.setDeviceTransactionId(txId);
                tempSales.setUserId(loginResponse.getmUser().getUserId());
                tempSales.setDeviceId(Build.SERIAL);
                try {
                    tempSales.setDeviceTransactionTime(DataFactory.formatDate(new Date()));
                } catch (Exception e) {
                    e.printStackTrace();
                    tempSales.setDeviceTransactionTime(new Date().toString());
                }
                tempSales.setAmount(voucher.getVoucherAmount() + "");
                try {
                    tempSales.setQuantity(DataFactory.formatDouble(Double.valueOf(voucher.getVoucherAmount()) / Double.valueOf(mNozzle.getUnitPrice())));
                } catch (Exception e) {
                    e.printStackTrace();
                    tempSales.setQuantity("0");
                }

                tempSales.setTin("");
                tempSales.setPlateNumber("");
                try {
                    tempSales.setName(voucher.getCustomerMerchant().getCustomer().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                    tempSales.setName("");
                }

                tempSales.setNozzleId(mNozzle.getNozzleId());
                tempSales.setPumpId(mNozzle.getmPump().getPumpId());
                tempSales.setProductId(mNozzle.getProductId());
                tempSales.setBranchId(mNozzle.getmPump().getBranchId());
                tempSales.setVoucherNumber(codeResult);
                tempSales.setStatus(StatusConfig.SUCCESS_NOT_UPLOADED);

                List<MPayment> mPayments = MPayment.listAll(MPayment.class);
                if (!mPayments.isEmpty()) {
                    for (MPayment mPayment : mPayments) {
                        if (mPayment.getName().toLowerCase().equals("voucher")) {
                            tempSales.setPaymentModeId(mPayment.getPaymentModeId());
                        }
                    }
                }
                long persistReslt = tempSales.save();
                if (persistReslt < 0) {
                    Toast.makeText(getContext(), "Failed to record local transaction", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to record local transaction: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        mNozzle = null;
        codeResult = "";
        popInfo(redeemResult);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnVoucherModule {
        void onVoucherModule(boolean isDone, Object object);
    }
}
