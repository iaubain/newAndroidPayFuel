package com.oltranz.pf.n_payfuel.utilities.printing;

import android.content.Context;
import android.os.AsyncTask;

import com.oltranz.pf.n_payfuel.entities.MDevice;
import com.oltranz.pf.n_payfuel.entities.MNozzle;
import com.oltranz.pf.n_payfuel.entities.MPayment;
import com.oltranz.pf.n_payfuel.entities.MSales;
import com.oltranz.pf.n_payfuel.entities.MUser;
import com.oltranz.pf.n_payfuel.utilities.DbBulk;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/21/2017.
 */

public class TransactionPrint {
    private Context context;
    private MSales sellingTransaction;
    private OnTransactionPrint mListener;

    public TransactionPrint(OnTransactionPrint mListener, Context context, MSales sellingTransaction) {
        this.mListener = mListener;
        this.context = context;
        this.sellingTransaction = sellingTransaction;
    }

    public void generateReceipt(){
        if(sellingTransaction != null){
            new PrintHandler().execute(sellingTransaction);
        }

    }

    public interface OnTransactionPrint {
        void onPrintResult(String printingMessage);
    }

    private class PrintHandler extends AsyncTask<MSales, String, String> {

        MSales mSales;
        @Override
        protected String doInBackground(MSales... params) {
            mSales = params[0];
            TransactionPrintModel tp = new TransactionPrintModel();

            MNozzle nozzle = DbBulk.getNozzle(mSales.getNozzleId());

            MDevice mDevice = DbBulk.getDevice();
            String deviceName = mDevice != null ? mDevice.getDeviceName() : "N/A";

            MUser mUser = DbBulk.getUser(mSales.getUserId());
            String userName = mUser != null ? mUser.getName() : "N/A";
            String branchName = mUser != null ? mUser.getBranchName() : "N/A";

            MPayment mPayment = DbBulk.getPayment(mSales.getPaymentModeId());
            String paymentMode = mPayment != null ? mPayment.getName() : "N/A";

            tp.setAmount(mSales.getAmount());
            tp.setQuantity(mSales.getQuantity());
            tp.setBranchName(branchName);
            tp.setDeviceId(deviceName);
            tp.setUserName(userName);
            tp.setDeviceTransactionId(mSales.getDeviceTransactionId()+"");
            tp.setDeviceTransactionTime(mSales.getDeviceTransactionTime());
            tp.setNozzleName(nozzle != null ? nozzle.getNozzleName() : "N/A");
            tp.setPaymentMode(paymentMode);

            if (mSales.getPlateNumber() != null)
                tp.setPlateNumber(mSales.getPlateNumber());
            else
                tp.setPlateNumber("N/A");

            tp.setProductName(nozzle != null ? nozzle.getProductName() : "N/A");
            tp.setPumpName(nozzle != null ? nozzle.getmPump().getPumpName() != null ? nozzle.getmPump().getPumpName() : "N/A" : "N/A");

            if (mSales.getTelephone() != null)
                tp.setTelephone(mSales.getTelephone());
            else
                tp.setTelephone("N/A");

            if (mSales.getTin() != null)
                tp.setTin(mSales.getTin());
            else
                tp.setTin("N/A");

            if (mSales.getVoucherNumber() != null)
                tp.setVoucherNumber(mSales.getVoucherNumber());
            else
                tp.setVoucherNumber("N/A");

            if (mSales.getName() != null)
                tp.setCompanyName(mSales.getName());
            else
                tp.setCompanyName("N/A");


            tp.setPaymentStatus("Success");


            //launch printing procedure
            try{
                PrinterLoader printerLoader = new PrinterLoader(context, true, tp);
                return printerLoader.printOut();
            }catch (Exception e){
                e.printStackTrace();
                return "Error: "+e.getLocalizedMessage();
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            mListener.onPrintResult(result);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(String... text) {

        }
    }
}
