package com.oltranz.pf.n_payfuel.utilities.functional;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.oltranz.pf.n_payfuel.R;
import com.oltranz.pf.n_payfuel.config.AppFlow;
import com.oltranz.pf.n_payfuel.entities.MNozzle;
import com.oltranz.pf.n_payfuel.entities.MPayment;
import com.oltranz.pf.n_payfuel.entities.MSales;
import com.oltranz.pf.n_payfuel.utilities.DbBulk;
import com.oltranz.pf.n_payfuel.utilities.views.MyButton;
import com.oltranz.pf.n_payfuel.utilities.views.MyLabel;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/20/2017.
 */

public class ConfirmTransaction {
    private OnConfirmTransaction mListener;
    private Context context;
    private MSales mSales;
    private String userName;

    private AlertDialog.Builder builder;
    private AlertDialog auxDialog;
    private ProgressDialog progressDialog;
    private Dialog dialog;

    public ConfirmTransaction(OnConfirmTransaction mListener, Context context, MSales mSales, String userName) {
        this.mListener = mListener;
        this.context = context;
        this.mSales = mSales;
        this.userName = userName;
    }

    public void reset(){
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public void setValue(){
        try {
            dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.confirm_box);

            final MyLabel boxTitle = (MyLabel) dialog.findViewById(R.id.dialogTitle);
            ImageView close = (ImageView) dialog.findViewById(R.id.icClose);
            MyButton cancel = (MyButton) dialog.findViewById(R.id.cancel);
            MyButton ok = (MyButton) dialog.findViewById(R.id.ok);
            LinearLayout boxContent = (LinearLayout) dialog.findViewById(R.id.dialogContent);

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onConfirmTransaction(false, mSales);
                    dialog.dismiss();
                }
            });
            boxTitle.setText("Confirm transaction");

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //proceed with other validations
                    mListener.onConfirmTransaction(true, mSales);
                    dialog.dismiss();
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onConfirmTransaction(false, mSales);
                    dialog.dismiss();
                }
            });

            //setting box content
            LinearLayout detailsHolder = new LinearLayout(context);
            detailsHolder.setId(View.generateViewId());
            detailsHolder.setOrientation(LinearLayout.HORIZONTAL);
            detailsHolder.setPadding(4,4,4,4);
            boxContent.addView(detailsHolder);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);

            MyLabel label = new MyLabel(context);
            label.setLayoutParams(layoutParams);
            label.setPadding(0,0,15,0);
            label.setGravity(Gravity.RIGHT);
            detailsHolder.addView(label);

            MyLabel values = new MyLabel(context);
            values.setLayoutParams(layoutParams);
            values.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            values.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
            detailsHolder.addView(values);

            //Prepare the label
            label.setText("AMOUNT: \n");
            label.append("QUANTITY: \n\n");

            label.append("PAYMENT: \n\n");

            label.append("PRODUCT: \n");
            label.append("PUMP: \n");
            label.append("NOZZLE: \n\n\n");

            label.append("NUMBER PLATE: \n");
            label.append("TIN: \n\n");

            label.append("SERVED BY: \n");
            label.append("PETROL STATION: \n\n");

            //prepare the value
            values.setText(mSales.getAmount()+ AppFlow.CURRENCY+" \n");
            values.append(mSales.getQuantity().length() > 9 ? mSales.getQuantity().substring(0,8)+AppFlow.QUANTITY_MEASURE+"\n\n" : mSales.getQuantity()+AppFlow.QUANTITY_MEASURE+"\n\n");
            MPayment mPayment = DbBulk.getPayment(mSales.getPaymentModeId());
            values.append(mPayment != null ? mPayment.getName()+"\n\n" : mSales.getPaymentModeId() +"\n\n");

            MNozzle mNozzle = DbBulk.getNozzle(mSales.getNozzleId());
            values.append(mNozzle.getProductName() != null ? mNozzle.getProductName()+"\n" : mSales.getProductId()+"\n");
            values.append(mNozzle.getmPump().getPumpName()+"\n");
            values.append(mNozzle.getNozzleName()+"\n\n\n");

            values.append(mSales.getPlateNumber().length() >= 3 ? mSales.getPlateNumber()+"\n" : "N/A\n");
            values.append(mSales.getTin().length() >= 3 ? mSales.getTin()+"\n\n" : "N/A\n\n");

            values.append(userName+"\n");
            values.append(mNozzle.getmPump().getBranchName()+"\n\n");

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            mListener.onConfirmTransaction(false, mSales);
            dialog.dismiss();
        }
    }



    public interface OnConfirmTransaction{
        void onConfirmTransaction(boolean isDone, MSales mSales);
    }
}
