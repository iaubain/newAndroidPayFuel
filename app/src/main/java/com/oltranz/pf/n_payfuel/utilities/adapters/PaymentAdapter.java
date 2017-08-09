package com.oltranz.pf.n_payfuel.utilities.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.oltranz.pf.n_payfuel.R;
import com.oltranz.pf.n_payfuel.config.AppFlow;
import com.oltranz.pf.n_payfuel.entities.MPayment;
import com.oltranz.pf.n_payfuel.utilities.views.MyLabel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/15/2017.
 */

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.MyViewHolder> {
    private OnPaymentAdapter mListener;
    private Context context;
    private List<MPayment> mPayments;

    public PaymentAdapter(OnPaymentAdapter mListener, Context context, List<MPayment> mPayments) {
        this.mListener = mListener;
        this.context = context;
        this.mPayments = mPayments;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payment_style, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final MPayment mPayment = mPayments.get(position);
        holder.pName.setText(mPayment.getName());
        holder.pImg.setClickable(true);

        switch (mPayment.getName().toLowerCase()){
            case "cash":
                holder.pImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_payment_cash));
                break;
            case "voucher":
                holder.pImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_payment_voucher));
                break;
            case "mtn":
                holder.pImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_payment_mtn));
                break;
            case "tigo":
                holder.pImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_payment_tigo));
                break;
            case "airtel":
                holder.pImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_payment_airtel));
                break;
            case "visa":
                holder.pImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_payment_visa));
                break;
            case "master":
                holder.pImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_payment_mastercard));
                break;
            case "debt":
                holder.pImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_payment_debt));
                break;
            case AppFlow.PROPRIETARY_CARD:
                holder.pImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_proprietary_card));
                break;
            default:
                holder.pImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_payment));
                break;
        }



        holder.pImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPayment(true, mPayment);
            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popBox("Payment : "+mPayment.getName()+"\n"+
                "ID: "+mPayment.getPaymentModeId()+"\n"+
                "Description: "+mPayment.getDescr()+"\n"+
                "Status: "+mPayment.getStatus());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPayments.size();
    }

    private void popBox(String message){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.SimpleDialog);
            builder.setMessage(message != null ? message : "NO_MESSAGE")
                    .setTitle(R.string.dialog_title);
            // Add the buttons
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            Dialog dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, message != null ? message : "NO_MESSAGE", Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnPaymentAdapter {
        void onPayment(boolean isDone, MPayment mPayment);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.paymentName)
        MyLabel pName;
        @BindView(R.id.paymentImg)
        ImageView pImg;
        @BindView(R.id.overflow)
        ImageView overflow;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
