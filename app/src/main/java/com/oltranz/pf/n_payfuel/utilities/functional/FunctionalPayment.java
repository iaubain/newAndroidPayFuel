package com.oltranz.pf.n_payfuel.utilities.functional;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.oltranz.pf.n_payfuel.R;
import com.oltranz.pf.n_payfuel.entities.MPayment;
import com.oltranz.pf.n_payfuel.entities.MSales;
import com.oltranz.pf.n_payfuel.utilities.BarcodeScanner.BarcodeScanner;
import com.oltranz.pf.n_payfuel.utilities.adapters.GridSpacingItemDecoration;
import com.oltranz.pf.n_payfuel.utilities.adapters.PaymentAdapter;
import com.oltranz.pf.n_payfuel.utilities.nfc.NfcCardData;
import com.oltranz.pf.n_payfuel.utilities.nfc.NfcReader;
import com.oltranz.pf.n_payfuel.utilities.views.MyButton;
import com.oltranz.pf.n_payfuel.utilities.views.MyEdit;
import com.oltranz.pf.n_payfuel.utilities.views.MyLabel;

import java.util.List;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/20/2017.
 */

public class FunctionalPayment implements PaymentAdapter.OnPaymentAdapter{
    private OnPaymentMethod mListener;
    private Context context;
    private MSales mSales;
    private List<MPayment> mPayments;
    private NfcCardData mNfc;
    private String globalMessage;

    private ProgressDialog progressDialog;
    private AlertDialog.Builder builder;
    private Dialog mainDialog;
    private MyEdit extValue;

    public FunctionalPayment(OnPaymentMethod mListener, Context context, MSales mSales) {
        this.mListener = mListener;
        this.context = context;
        this.mSales = mSales;
    }
    public void setValue(){
        startProcess();
    }

    public void reset(){
        if(mainDialog != null && mainDialog.isShowing())
            mainDialog.dismiss();
    }

    private void startProcess(){
        try {
            mainDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            mainDialog.setCancelable(false);
            mainDialog.setCanceledOnTouchOutside(false);
            mainDialog.setContentView(R.layout.payment_layout);
            final MyLabel boxTitle = (MyLabel) mainDialog.findViewById(R.id.dialogTitle);
            ImageView close = (ImageView) mainDialog.findViewById(R.id.icClose);
            MyButton finish = (MyButton) mainDialog.findViewById(R.id.ok);
            RecyclerView pRecycler = (RecyclerView) mainDialog.findViewById(R.id.mPaymentView);
            LinearLayout boxContent = (LinearLayout) mainDialog.findViewById(R.id.dialogContent);

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reset();
                    mListener.onPaymentMethod(false, null);
                }
            });
            boxTitle.setText("Payment methods");

            finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //proceed with other validations
                    if(mSales.getPaymentModeId() == null){
                        Toast.makeText(context, "No payment method is selected", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if (mSales.getPaymentModeId().equals("") || mSales.getPaymentModeId().length() <= 0){
                        Toast.makeText(context, "No payment method is selected", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mListener.onPaymentMethod(true, mSales);
                }
            });

            mPayments = MPayment.listAll(MPayment.class);
            if(mPayments.isEmpty()){
                popBox("No payment mode found, logout and login again");
                return;
            }

            //setting box content
            PaymentAdapter paymentAdapter = new PaymentAdapter(this, context, mPayments);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 2);
            pRecycler.setLayoutManager(mLayoutManager);
            pRecycler.addItemDecoration(new GridSpacingItemDecoration(2, GridSpacingItemDecoration.dpToPx(context,10), true));
            pRecycler.setItemAnimator(new DefaultItemAnimator());
            pRecycler.setAdapter(paymentAdapter);

            mainDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            mListener.onPaymentMethod(false, null);
        }
    }

    private void mProgress(String message){
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
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

    private void popBox(final String message){
        try {
            builder = new AlertDialog.Builder(context, R.style.SimpleDialog);
            builder.setMessage(message != null ? message : "NO_MESSAGE")
                    .setTitle(R.string.dialog_title);
            // Add the buttons
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    reset();
                    mListener.onPaymentMethod(false, mSales);
                    dialog.dismiss();
                }
            });
            Dialog dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            reset();
            mListener.onPaymentMethod(false, null);
        }
    }

    private void setExtraVale(final int layoutToInflate, final MPayment mPayment){
        try {
            if (layoutToInflate == 999){
                mSales.setPaymentModeId(mPayment.getPaymentModeId());
                mListener.onPaymentMethod(true, mSales);
                return;
            }
            final Dialog extraDialog = new Dialog(context);
            extraDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            extraDialog.setContentView(layoutToInflate);
            extraDialog.setCancelable(false);
            extraDialog.setCanceledOnTouchOutside(false);

            final MyButton ok = (MyButton) extraDialog.findViewById(R.id.ok);
            final MyButton cancel = (MyButton) extraDialog.findViewById(R.id.cancel);
            MyEdit tel = null;
            MyEdit voucher = null;
            MyEdit numberPlate = null;
            FloatingActionButton tap = (FloatingActionButton) extraDialog.findViewById(R.id.tap);

            if(layoutToInflate == R.layout.sales_customer_voucher){
                voucher = (MyEdit) extraDialog.findViewById(R.id.voucher);
                numberPlate = (MyEdit) extraDialog.findViewById(R.id.numberPlate);
                numberPlate.setAllCaps(true);
                numberPlate.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

                final MyEdit finalVoucher1 = voucher;
                tap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProgress("Reading the voucher");
                        BarcodeScanner barcodeScanner = new BarcodeScanner(new BarcodeScanner.OnBarcodeScan() {
                            @Override
                            public void onBarcode(boolean isDone, String barcodeValue) {
                                dismissProgress();
                                if(!isDone){
                                    Toast.makeText(context, "Error: "+barcodeValue, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                finalVoucher1.setText(barcodeValue);
                            }
                        }, context);
                        barcodeScanner.startReading();
                    }
                });

            }else if(layoutToInflate == R.layout.sales_customer_tel){
                tel = (MyEdit) extraDialog.findViewById(R.id.tel);

                final MyEdit finalTel1 = tel;
                if (mPayment.getName().toLowerCase().contains("tigo")){
                    tel.setText("");
                    tel.append("072");
                    tap.setVisibility(View.GONE);
                }else if (mPayment.getName().toLowerCase().contains("mtn")){
                    tel.setText("");
                    tel.append("078");
                }else if (mPayment.getName().toLowerCase().contains("airtel")){
                    tel.setText("");
                    tel.append("073");
                    tap.setVisibility(View.GONE);
                }
                tap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            finalTel1.setText("");
                            finalTel1.append("078");
                            extValue = finalTel1;

                            mProgress("Reading tag");
                            NfcReader draftNfc = new NfcReader(new NfcReader.OnNfcDraftInteraction() {
                                @Override
                                public void onNfcDraft(boolean isDone, Object nfcData) {
                                    dismissProgress();
                                    if(!isDone){
                                        Toast.makeText(context, (String) nfcData, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    String msisdn = ((NfcCardData)nfcData).getSerialNumber();
                                    String serialNumber = msisdn;
                                    //TODO remember to remove those serial number and msisdn for live application
                                    switch (serialNumber){
                                        case "040f87f2a44881":
                                            msisdn = "0786367970";
                                            break;
                                        case "046f27ba9e3380":
                                            msisdn = "0788251119";
                                            break;
                                        case "da6075fa":
                                            msisdn = "0785534672";
                                    }
                                    finalTel1.setText("");
                                    finalTel1.append(msisdn);
                                    Toast.makeText(context, nfcData.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            draftNfc.startReading();

                    }
                });
            }

            final MyEdit finalVoucher = voucher;
            final MyEdit finalNumberPlate = numberPlate;
            final MyEdit finalTel = tel;
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(layoutToInflate == R.layout.sales_customer_voucher){
                        if(TextUtils.isEmpty(finalVoucher.getText().toString())){
                            finalVoucher.setError("Invalid voucher");
                            return;
                        }
                        if (TextUtils.isEmpty(finalNumberPlate.getText().toString())){
                            finalNumberPlate.setError("Invalid number plate");
                            return;
                        }

                        mSales.setPlateNumber(finalNumberPlate.getText().toString());
                        mSales.setVoucherNumber(finalVoucher.getText().toString());
                        mSales.setPaymentModeId(mPayment.getPaymentModeId());
                        mListener.onPaymentMethod(true, mSales);
                        extraDialog.dismiss();
                    }else if(layoutToInflate == R.layout.sales_customer_tel){
                        if (TextUtils.isEmpty(finalTel.getText().toString()) || finalTel.getText().toString().length() < 10){
                            finalTel.setError("Invalid telephone");
                            return;
                        }
                        String msisdn = finalTel.getText().toString().replace("+","");
                        if(!msisdn.substring(0,2).contains("250")){
                            msisdn = "25"+msisdn;
                        }
                        mSales.setTelephone(msisdn);
                        mSales.setPaymentModeId(mPayment.getPaymentModeId());
                        mListener.onPaymentMethod(true, mSales);
                        extraDialog.dismiss();
                    }
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSales.setPlateNumber("");
                    mSales.setVoucherNumber("");
                    mSales.setPaymentModeId("");
                    mSales.setTelephone("");

                    extraDialog.dismiss();
                }
            });

            extraDialog.show();

        } catch (Exception e) {
            Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPayment(boolean isDone, MPayment mPayment) {
        if(!isDone){
            return;
        }
        if (mPayment.getName().toLowerCase().contains("tigo")||
                mPayment.getName().toLowerCase().contains("mtn") ||
                mPayment.getName().toLowerCase().contains("airtel")){
            setExtraVale(R.layout.sales_customer_tel, mPayment);
        }else if (mPayment.getName().toLowerCase().contains("voucher")){
            setExtraVale(R.layout.sales_customer_voucher, mPayment);
        }else{
            setExtraVale(999, mPayment);
        }
    }

    public interface OnPaymentMethod {
        void onPaymentMethod(boolean isDone, MSales sales);
        void onPaymentResetExtra(boolean isReset);
    }
}
