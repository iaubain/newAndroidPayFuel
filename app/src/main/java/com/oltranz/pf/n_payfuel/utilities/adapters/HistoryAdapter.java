package com.oltranz.pf.n_payfuel.utilities.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.oltranz.pf.n_payfuel.R;
import com.oltranz.pf.n_payfuel.config.AppFlow;
import com.oltranz.pf.n_payfuel.config.StatusConfig;
import com.oltranz.pf.n_payfuel.entities.MNozzle;
import com.oltranz.pf.n_payfuel.entities.MPayment;
import com.oltranz.pf.n_payfuel.entities.MSales;
import com.oltranz.pf.n_payfuel.models.sales.SalesResponse;
import com.oltranz.pf.n_payfuel.utilities.DbBulk;
import com.oltranz.pf.n_payfuel.utilities.loaders.LocalHistoryLoader;
import com.oltranz.pf.n_payfuel.utilities.loaders.TransactionLoader;
import com.oltranz.pf.n_payfuel.utilities.printing.TransactionPrint;
import com.oltranz.pf.n_payfuel.utilities.views.MyLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/15/2017.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> implements TransactionPrint.OnTransactionPrint,
        TransactionLoader.OnTransactionLoader, LocalHistoryLoader.OnLocalHistory {
    private OnHistoryInteraction mListener;
    private Context context;
    private List<MSales> mSalesList;
    private List<MSales> tempList;
    private boolean addedList=false;
    private ProgressDialog progressDialog;

    public HistoryAdapter(OnHistoryInteraction mListener, Context context, List<MSales> mSalesList) {
        this.mListener = mListener;
        this.context = context;
        this.mSalesList = mSalesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_style, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final MSales mSales = mSalesList.get(position);
        String displayStatus = mSales.getStatus().equals(StatusConfig.SUCCESS) ||
                mSales.getStatus().equals(StatusConfig.SUCCESS_NOT_UPLOADED) ? "Success" :
                mSales.getStatus().equals(StatusConfig.PENDING) ||
                        mSales.getStatus().equals(StatusConfig.PRINT_AFTER_PENDING) ? "Pending" : "Failed";

        MPayment mPayment = DbBulk.getPayment(mSales.getPaymentModeId());
        String dispPayment = mPayment != null ? mPayment.getName() : "N/A";

        MNozzle mNozzle = DbBulk.getNozzle(mSales.getNozzleId());
        String dispProduct = mNozzle != null ? mNozzle.getProductName() : "N/A";

        holder.amount.setText(mSales.getAmount()+" "+ (mSales.getAmount().length() > 4 ? AppFlow.CURRENCY_SHORT : AppFlow.CURRENCY));
        holder.payment.setText(dispPayment);
        holder.quantity.setText(mSales.getQuantity() +" "+ (mSales.getQuantity().length() > 4 ? AppFlow.QUANTITY_MEASURE_SHORT : AppFlow.QUANTITY_MEASURE));
        holder.product.setText(dispProduct);

        mSales.setSearchField(displayStatus+""+
                mSales.getQuantity()+""+
                mSales.getAmount()+""+
                mSales.getTelephone()+""+
                mSales.getTin()+""+
                mSales.getName()+""+
                mSales.getPlateNumber()+""+
                mSales.getVoucherNumber()+""+
                mSales.getPaymentModeId()+""+
                mSales.getNozzleId()+""+
                mSales.getPumpId()+""+
                mSales.getPumpId()+""+
                mSales.getDeviceTransactionId()+""+
                mSales.getDeviceTransactionTime()+""+
                dispPayment+""+
                dispProduct);

        if(mSales.getStatus().equals("100") || mSales.getStatus().equals("105")){
            holder.printer.setClickable(true);
            holder.printer.setEnabled(true);
            holder.printer.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_printer_green));
            holder.container.setBackground(ContextCompat.getDrawable(context, R.drawable.border_green));
        }else if (mSales.getStatus().equals("301") || mSales.getStatus().equals("302")){
            holder.printer.setClickable(false);
            holder.printer.setEnabled(false);
            holder.printer.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_printer_gray));
            holder.container.setBackground(ContextCompat.getDrawable(context, R.drawable.border_orange));
        }else{
            holder.printer.setClickable(false);
            holder.printer.setEnabled(false);
            holder.printer.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_printer_gray));
            holder.container.setBackground(ContextCompat.getDrawable(context, R.drawable.border_red));
        }

        holder.printer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress("Printing");
                TransactionPrint transactionPrint = new TransactionPrint(HistoryAdapter.this, context, mSales);
                transactionPrint.generateReceipt();
            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildMenu(view, mSales);
            }
        });
    }

    public int getViewPosition (MSales mSales){
        //myRecyclerView.findViewHolderForAdapterPosition(pos);
        return mSalesList.indexOf(mSales);
    }

    @Override
    public int getItemCount() {
        return mSalesList.size();
    }

    public void refreshAdapter(List<MSales> mSalesList){
        this.mSalesList = mSalesList;
        notifyDataSetChanged();
    }

    public void filter(String charText) {
        try {

            if(! addedList){
                this.tempList = new ArrayList<>();
                this.tempList.addAll(mSalesList);
                addedList=true;
            }

            charText = charText.toLowerCase(Locale.getDefault());
            mSalesList.clear();
            if (charText.length() == 0) {
                mSalesList.addAll(tempList);
                addedList=false;
            }
            else
            {
                for (MSales sales : tempList)
                {
                    if(sales.getSearchField() != null){
                        if (sales.getSearchField().toLowerCase(Locale.getDefault()).contains(charText)){
                            mSalesList.add(sales);
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
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

    private void buildMenu(View view, MSales mSales){
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.history_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(mSales));
        popup.show();
    }

    @Override
    public void onPrintResult(String printingMessage) {
        dismissProgress();
        Toast.makeText(context,"Printer: "+printingMessage, Toast.LENGTH_SHORT).show();
    }

    private void refreshTransaction(MSales mSales){
        mProgress("Refreshing transaction");
        TransactionLoader transactionLoader = new TransactionLoader(HistoryAdapter.this, mSales);
        transactionLoader.startLoading();
    }

    @Override
    public void onTransactionLoader(boolean isDone, int serverStatus, String message, MSales mSales, SalesResponse salesResponse) {
        dismissProgress();
        if(!isDone){
            Toast.makeText(context, "("+serverStatus+") "+message, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            mSales = DbBulk.getTransaction(mSales.getDeviceTransactionId());
            if(mSales == null || mSales.getStatus().isEmpty()){
                Toast.makeText(context, "Failed to get local transaction", Toast.LENGTH_SHORT).show();
                return;
            }
            mSales.setStatus(""+salesResponse.getStatusCode());
            long persistResult = mSales.save();
            if(persistResult < 0)
                Toast.makeText(context, "Failed to update local transaction", Toast.LENGTH_SHORT).show();

            mProgress("Reloading transactions");
            LocalHistoryLoader localHistoryLoader = new LocalHistoryLoader(HistoryAdapter.this, mSales.getUserId());
            localHistoryLoader.startLoading();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onHistory(boolean isDone, String message, List<MSales> mSalesList) {
        dismissProgress();
        if(!isDone && (mSalesList == null || mSalesList.isEmpty())){
            //popExit
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            return;
        }
        if(!mSalesList.isEmpty()){
            refreshAdapter(mSalesList);
        }
    }

    public interface OnHistoryInteraction {
        void onHistoryAdpter(boolean isDetails, MSales mSales);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.transactionContainer)
        RelativeLayout container;
        @BindView(R.id.amount)
        MyLabel amount;
        @BindView(R.id.payment)
        MyLabel payment;
        @BindView(R.id.quantity)
        MyLabel quantity;
        @BindView(R.id.product)
        MyLabel product;
        @BindView(R.id.printerImg)
        ImageView printer;
        @BindView(R.id.overflow)
        ImageView overflow;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        MSales mSales;

        MyMenuItemClickListener(MSales mSales) {
            this.mSales=mSales;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.salesDetails:
                    mListener.onHistoryAdpter(true, mSales);
                    return true;
                case R.id.refresh:
                    refreshTransaction(mSales);
                    return true;
                default:
            }
            return false;
        }
    }
}
