package com.oltranz.pf.n_payfuel.utilities.functional;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.oltranz.pf.n_payfuel.R;
import com.oltranz.pf.n_payfuel.config.AppFlow;
import com.oltranz.pf.n_payfuel.entities.MNozzle;
import com.oltranz.pf.n_payfuel.entities.MSales;
import com.oltranz.pf.n_payfuel.utilities.DataFactory;
import com.oltranz.pf.n_payfuel.utilities.views.MyButton;
import com.oltranz.pf.n_payfuel.utilities.views.MyEdit;
import com.oltranz.pf.n_payfuel.utilities.views.MyLabel;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/19/2017.
 */

public class TransactionValue {
    private OnTransactionValue mListener;
    private Context context;
    private MNozzle mNozzle;

    private AlertDialog.Builder builder;
    private Dialog mainDialog;
    private TextWatcher textWatcher;
    private TextWatcher quantityWatcher;

    public TransactionValue(OnTransactionValue mListener, Context context, MNozzle mNozzle) {
        this.mListener = mListener;
        this.context = context;
        this.mNozzle = mNozzle;
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
            mainDialog.setContentView(R.layout.sales_values);
            final MyLabel boxTitle = (MyLabel) mainDialog.findViewById(R.id.dialogTitle);
            ImageView close = (ImageView) mainDialog.findViewById(R.id.icClose);
            MyButton next = (MyButton) mainDialog.findViewById(R.id.ok);
            LinearLayout boxContent = (LinearLayout) mainDialog.findViewById(R.id.dialogContent);

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainDialog.dismiss();
                    mListener.onTransactionValue(false, null);
                }
            });
            boxTitle.setText("Amount or Quantity");

            //setting box content
            MyLabel label = (MyLabel) mainDialog.findViewById(R.id.label);
            MyLabel labelValue = (MyLabel) mainDialog.findViewById(R.id.labelValue);
            final MyLabel more = (MyLabel) mainDialog.findViewById(R.id.more);
            final MyLabel qty = (MyLabel) mainDialog.findViewById(R.id.qty);
            final MyEdit amount = (MyEdit) mainDialog.findViewById(R.id.amount);
            amount.setHint("Amount ("+AppFlow.CURRENCY+")");
            final MyEdit nPlate = (MyEdit) mainDialog.findViewById(R.id.numberPlate);
            nPlate.setAllCaps(true);
            nPlate.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            final MyEdit tin = (MyEdit) mainDialog.findViewById(R.id.tin);
            final MyEdit company = (MyEdit) mainDialog.findViewById(R.id.company);
            final ScrollView scrollView = (ScrollView) mainDialog.findViewById(R.id.moreContent);

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(scrollView.getVisibility() == View.VISIBLE){
                        scrollView.setVisibility(View.GONE);
                        more.setText("View more");
                    }else{
                        scrollView.setVisibility(View.VISIBLE);
                        more.setText("View less");
                    }
                }
            });

            qty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //pop a dialog for setting quantity
                    setQtyBox();
                }
            });

            label.setText("Pump: \n"+"Nozzle: \n"+"Product: \n"+"Unit price: ");
            labelValue.setText(mNozzle.getmPump().getPumpName()+"\n"+mNozzle.getNozzleName()+"\n"+mNozzle.getProductName()+"\n"+mNozzle.getUnitPrice()+" "+AppFlow.CURRENCY);
            final Double unitPrice;
            try {
                unitPrice = Double.parseDouble(mNozzle.getUnitPrice());
            }catch (Exception e){
                e.printStackTrace();
                popBox("Invalid fuel amount");
                return;
            }

            textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    qty.setText("");
                    try{
                        double cAmount = 0;
                        if(amount.getText().toString().length() <= 0){
                            qty.setText("Quantity");
                            return;
                        }

                        cAmount = Double.parseDouble(amount.getText().toString().replace(",",""));
                        if(cAmount <= AppFlow.MAX_AMOUNT){
                            qty.setText(DataFactory.formatDouble(cAmount / unitPrice) +" "+AppFlow.QUANTITY_MEASURE_SHORT);
                        }else{
                            Toast.makeText(context, "Exceeded allowed amount", Toast.LENGTH_SHORT).show();
                            int length = charSequence.length();
                            //amount.removeTextChangedListener(textWatcher);
                            if(length > 1)
                                amount.setText(charSequence.subSequence(0, length-1));

                            amount.setSelection(amount.getText().toString().length());
                            qty.setText(DataFactory.formatDouble(cAmount / unitPrice) +" "+AppFlow.QUANTITY_MEASURE_SHORT);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        mListener.onTransactionValue(false, null);
                        mainDialog.dismiss();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            };

            amount.addTextChangedListener(textWatcher);

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //proceed with other validations
                    if(TextUtils.isEmpty(amount.getText().toString())){
                        Toast.makeText(context, "Invalid amount", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //DataFactory.formatDouble(Double.valueOf(amount.getText().toString()+"")).replace(",","")
                    if(Double.valueOf(amount.getText().toString().replace(",","")) > AppFlow.MAX_AMOUNT){
                        Toast.makeText(context, "Exceeded allowed amount", Toast.LENGTH_SHORT).show();
                        amount.setText("");
                        return;
                    }
                    MSales mSales = new MSales();
                    mSales.setAmount(amount.getText().toString());
                    String[] quantity = DataFactory.splitString(qty.getText().toString(), " ");
                    mSales.setQuantity(quantity[0]);
                    mSales.setTin(tin.getText().toString());
                    mSales.setPlateNumber(nPlate.getText().toString());
                    mSales.setName(company.getText().toString());
                    mSales.setNozzleId(mNozzle.getNozzleId());
                    mSales.setPumpId(mNozzle.getmPump().getPumpId());
                    mSales.setProductId(mNozzle.getProductId());
                    mSales.setBranchId(mNozzle.getmPump().getBranchId());
                    mListener.onTransactionValue(true, mSales);
                }
            });

            mainDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            mListener.onTransactionValue(false, null);
        }
    }

    private void setQtyBox(){
        try {

            final Dialog qtyDialog = new Dialog(context);
            qtyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            qtyDialog.setContentView(R.layout.sales_quantity_box);
            qtyDialog.setCancelable(false);
            qtyDialog.setCanceledOnTouchOutside(false);

            final MyButton ok = (MyButton) qtyDialog.findViewById(R.id.ok);
            final MyButton cancel = (MyButton) qtyDialog.findViewById(R.id.cancel);
            final MyEdit userQty = (MyEdit) qtyDialog.findViewById(R.id.quantity);
            userQty.setHint("Quantity "+AppFlow.QUANTITY_MEASURE_SHORT);

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(TextUtils.isEmpty(userQty.getText().toString())){
                        qtyDialog.dismiss();
                        return;
                    }
                    double quantity = Double.valueOf(userQty.getText().toString());
                    double total = quantity*Double.valueOf(mNozzle.getUnitPrice());
                    if(total > AppFlow.MAX_AMOUNT){
                        userQty.setText("");
                        Toast.makeText(context, "Exceeded allowed amount", Toast.LENGTH_SHORT).show();
                        return;
                    }
                        if(mainDialog != null)
                            if(mainDialog.isShowing()){
                                final MyLabel qty = (MyLabel) mainDialog.findViewById(R.id.qty);
                                final MyEdit amount = (MyEdit) mainDialog.findViewById(R.id.amount);
                                qty.setText(DataFactory.formatDouble(Double.valueOf(userQty.getText().toString())) +" "+AppFlow.QUANTITY_MEASURE);
                                amount.setText(DataFactory.formatDouble(Double.valueOf(total+"")).replace(",",""));
                                amount.setSelection(amount.getText().toString().length());
                                qtyDialog.dismiss();
                            }else{
                                Toast.makeText(context, "Internal Application error", Toast.LENGTH_SHORT).show();
                                qtyDialog.dismiss();
                            }
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    qtyDialog.dismiss();
                }
            });

            qtyDialog.show();

        } catch (Exception e) {
            Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void popBox(final String message){
        try {
            builder = new AlertDialog.Builder(context, R.style.SimpleDialog);
            builder.setMessage(message != null ? message : "NO_MESSAGE")
                    .setTitle(R.string.dialog_title);
            // Add the buttons
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    mListener.onTransactionValue(false, null);
                }
            });
            Dialog dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            mListener.onTransactionValue(false, null);
        }
    }

    public interface OnTransactionValue {
        void onTransactionValue(boolean isDone, MSales sales);
    }
}
