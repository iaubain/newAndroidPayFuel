package com.oltranz.pf.n_payfuel.utilities.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.oltranz.pf.n_payfuel.config.StatusConfig;
import com.oltranz.pf.n_payfuel.entities.MNozzle;
import com.oltranz.pf.n_payfuel.entities.MPayment;
import com.oltranz.pf.n_payfuel.entities.MSales;
import com.oltranz.pf.n_payfuel.models.sales.SalesResponse;
import com.oltranz.pf.n_payfuel.utilities.DbBulk;
import com.oltranz.pf.n_payfuel.utilities.loaders.TransactionLoader;
import com.oltranz.pf.n_payfuel.utilities.printing.TransactionPrint;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class TransactionRectifier extends IntentService implements TransactionLoader.OnTransactionLoader,
        TransactionPrint.OnTransactionPrint {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String POST_TRANSACTIONS = "POST_TRANSACTIONS";
    public static final String NOZZLE_BROADCAST_FILTER = "com.oltranz.pf.n_payfuel.REFRESH_NOZZLES";
    public static final String NOZZLE_BROADCAST_ACTION = "REFRESH_NOZZLES";

    public TransactionRectifier() {
        super("TransactionRectifier");
    }

    /**
     * Starts this service to perform action startPosting with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startPosting(Context context) {
        Intent intent = new Intent(context, TransactionRectifier.class);
        intent.setAction(POST_TRANSACTIONS);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            if (POST_TRANSACTIONS.equals(action)) {
                postPendingTransaction();
            }
        }
    }

    private void postPendingTransaction() {
        List<MSales> mSalesList = DbBulk.getPendingTransaction();
        if(mSalesList.isEmpty())
            return;
        for(MSales mSales : mSalesList){
            TransactionLoader transactionLoader = new TransactionLoader(TransactionRectifier.this, mSales);
            transactionLoader.startLoading();
        }
    }

    @Override
    public void onTransactionLoader(boolean isDone, int serverStatus, String message, MSales mSales, SalesResponse salesResponse) {
        if(!isDone){
            Toast.makeText(getApplicationContext(), "("+serverStatus+") "+message, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            mSales = DbBulk.getTransaction(mSales.getDeviceTransactionId());
            if(mSales == null || mSales.getStatus().isEmpty()){
                Toast.makeText(getApplicationContext(), "Failed to get local transaction", Toast.LENGTH_SHORT).show();
                return;
            }
            MPayment mPayment = DbBulk.getPayment(mSales.getPaymentModeId());
            if(mPayment == null){
                Toast.makeText(getApplicationContext(), "Failed to get local payment methods", Toast.LENGTH_SHORT).show();
                return;
            }

            int serverTx = salesResponse.getStatusCode();
            String localTx = mSales.getStatus();
            if(!(mPayment.getName().toLowerCase().contains("cash")||
                    mPayment.getName().toLowerCase().contains("debt") ||
                    mPayment.getName().toLowerCase().contains("voucher") ||
                    mPayment.getName().toLowerCase().contains(" card"))){
                mSales.setStatus(""+serverTx);
                updateLocalTransaction(mSales);
            }

            if(serverTx == 100){
                mSales.setStatus(""+serverTx);
                updateLocalTransaction(mSales);
                if(localTx.equals(StatusConfig.PRINT_AFTER_PENDING)){
                    generateReceipt(mSales);
                }else if(localTx.equals(StatusConfig.FAILURE)){
                    generateReceipt(mSales);
                }
            }else if(serverTx == 301){
                if(!localTx.equals(StatusConfig.PENDING) && !localTx.equals(StatusConfig.PRINT_AFTER_PENDING)) {
                    mSales.setStatus(""+serverTx);
                    updateLocalTransaction(mSales);
                }
            }else if(serverTx == 500){
                    if(mPayment.getName().toLowerCase().contains("tigo") ||
                            mPayment.getName().toLowerCase().contains("mtn") ||
                            mPayment.getName().toLowerCase().contains("airtel")){
                        mSales.setStatus(""+serverTx);
                        updateLocalTransaction(mSales);
                    }
                decrementIndex(DbBulk.getNozzle(mSales.getNozzleId()), mSales.getQuantity());
            }else{
                Toast.makeText(getApplicationContext(), "("+serverStatus+") "+message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLocalTransaction(MSales mSales){
        long persistResult = mSales.save();
        if(persistResult < 0){
            Toast.makeText(getApplicationContext(), "Local DB error.", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateReceipt(MSales mSales){
        try{
            TransactionPrint transactionPrint = new TransactionPrint(TransactionRectifier.this, getApplicationContext(), mSales);
            transactionPrint.generateReceipt();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Printing Error: "+e.getCause(), Toast.LENGTH_SHORT).show();
        }
    }

    private void decrementIndex(MNozzle nozzle, String value){
        try {
            double currentIndex = Double.valueOf(nozzle.getIndexCount());
            double reduceValue = Double.valueOf(value);
            if(currentIndex > reduceValue){
                Double newIndex=currentIndex - reduceValue;
                nozzle.setIndexCount(""+newIndex);
            }else{
                nozzle.setIndexCount(""+0.0);
            }
            nozzle.save();
            Intent i = new Intent(TransactionRectifier.NOZZLE_BROADCAST_FILTER).setAction(TransactionRectifier.NOZZLE_BROADCAST_ACTION);
            getApplicationContext().sendBroadcast(i);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPrintResult(String printingMessage) {
        Toast.makeText(getApplicationContext(), "Printer: "+printingMessage, Toast.LENGTH_SHORT).show();
    }
}
