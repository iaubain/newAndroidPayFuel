package com.oltranz.pf.n_payfuel.utilities.loaders;

import android.os.AsyncTask;

import com.oltranz.pf.n_payfuel.entities.MNozzle;
import com.oltranz.pf.n_payfuel.entities.MPayment;
import com.oltranz.pf.n_payfuel.entities.MSales;
import com.oltranz.pf.n_payfuel.models.reportmodel.ReportData;
import com.oltranz.pf.n_payfuel.models.reportmodel.ReportModel;
import com.oltranz.pf.n_payfuel.models.reportmodel.ReportRequest;
import com.oltranz.pf.n_payfuel.utilities.DataFactory;
import com.oltranz.pf.n_payfuel.utilities.DbBulk;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/29/2017.
 */

public class LocalReportLoader {
    private OnReportLoader mListener;
    private ReportRequest reportRequest;

    private LinkedHashMap<String, Long> transactionCount = new LinkedHashMap<>();
    private LinkedHashMap<String, Double> paymentCount = new LinkedHashMap<>();
    private LinkedHashMap<String, Double> quantityCount = new LinkedHashMap<>();
    private Double totalSoldAmount;
    private String date;


    public LocalReportLoader(OnReportLoader mListener, ReportRequest reportRequest) {
        this.mListener = mListener;
        this.reportRequest = reportRequest;
    }
    public void startLoading(){
        new LoadLocalHistory().execute(reportRequest);
    }

    private Double sumDouble(String base, String value){
        try {
            return Double.valueOf(DataFactory.formatDouble(Double.valueOf(base) + Double.valueOf(value)));
        }catch (Exception e){
            e.printStackTrace();
            return 0.0;
        }
    }

    private void addTransaction(String status, long number){
        if(!transactionCount.containsKey(status))
            transactionCount.put(status, number);
        else{
            transactionCount.put(status, transactionCount.get(status) + number);
        }
    }

    private void removeTransaction(String status, long number){
        if(transactionCount.containsKey(status)){
            transactionCount.put(status, transactionCount.get(status) - number);
        }
    }

    private void addPayment(String payment, Double amount){
        if(!paymentCount.containsKey(payment))
            paymentCount.put(payment, amount);
        else{
            paymentCount.put(payment, paymentCount.get(payment) + amount);
        }
    }

    private void removePayment(String payment, Double amount){
        if(paymentCount.containsKey(payment)){
            paymentCount.put(payment, paymentCount.get(payment) - amount);
        }
    }

    private void addProduct(String product, Double quantity){
        if(!quantityCount.containsKey(product))
            quantityCount.put(product, quantity);
        else{
            quantityCount.put(product, quantityCount.get(product) + quantity);
        }
    }

    private void removeProduct(String product, Double quantity){
        if(quantityCount.containsKey(product)){
            quantityCount.put(product, quantityCount.get(product) - quantity);
        }
    }
    public interface OnReportLoader {
        void onReport(boolean isDone, String message, ReportModel reportModel);
    }

    private class LoadLocalHistory extends AsyncTask<ReportRequest, String, ReportData> {

        @Override
        protected ReportData doInBackground(ReportRequest... params) {
            ReportRequest reportRequest = params[0];
            ReportData reportData = new ReportData();
            try {
                DateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd", Locale.getDefault());
                String startDate = dateFormat.format(dateFormat.parse(reportRequest.getStartDate()));
                String endDate = dateFormat.format(dateFormat.parse(reportRequest.getStartDate()));
                List<MSales> mSalesList = DbBulk.getUserSalesReport(reportRequest.getmUser().getUserId(), startDate, endDate);
                reportData.setmSalesList(mSalesList);
                reportData.setmNozzles(MNozzle.listAll(MNozzle.class));
                reportData.setmPayments(MPayment.listAll(MPayment.class));
                return reportData;
            }catch (Exception e){
                e.printStackTrace();
                return reportData;
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(ReportData result) {
            if(result== null){
                mListener.onReport(false, "Failed to generate report.", null);
                return;
            }
            List<MSales> mSalesList = result.getmSalesList();
            if(mSalesList.isEmpty()){
                mListener.onReport(false, "Transactions not found.", null);
                return;
            }
            List<MNozzle> mNozzleList = result.getmNozzles();
            List<MPayment> mPaymentList = result.getmPayments();
            ReportModel reportModel = new ReportModel();
            reportModel.setTotalTransaction(mSalesList.size());
            reportModel.setDate(reportRequest.getStartDate().equals(reportRequest.getEndDate())? reportRequest.getStartDate() :
                    reportRequest.getStartDate()+" - "+reportRequest.getEndDate());
            reportModel.setUser(reportRequest.getmUser());
            reportModel.setTotalSoldAmount(0.0);
            for(MSales mSales : mSalesList){
                if(mSales.getStatus().equals("100") || (mSales.getStatus().equals("105")))
                    reportModel.setTotalSoldAmount(sumDouble(reportModel.getTotalSoldAmount()+"", mSales.getAmount()));
                String status = mSales.getStatus().equals("100") ? "Successful" :
                        (mSales.getStatus().equals("105") || mSales.getStatus().equals("301") || mSales.getStatus().equals("302")) ? "Pending" : "Faillure";
                addTransaction(status, 1);
                boolean isPaymentFound = false;
                for(MPayment mPayment : mPaymentList){
                    if(mPayment.getPaymentModeId().equals(mSales.getPaymentModeId()) && (mSales.getStatus().equals("100") || (mSales.getStatus().equals("105")))){
                        addPayment(mPayment.getName(), Double.valueOf(mSales.getAmount()));
                        isPaymentFound = true;
                        break;
                    }
                }
                if(!isPaymentFound  && (mSales.getStatus().equals("100") || (mSales.getStatus().equals("105"))))
                    addPayment("Payment ("+mSales.getPaymentModeId()+")", Double.valueOf(mSales.getAmount()));

                boolean isNozzleFound = false;
                for(MNozzle mNozzle : mNozzleList){
                    if(mNozzle.getNozzleId().equals(mSales.getNozzleId()) && (mSales.getStatus().equals("100") || (mSales.getStatus().equals("105")))){
                        addProduct(mNozzle.getProductName(), Double.valueOf(mSales.getQuantity()));
                        isNozzleFound = true;
                        break;
                    }
                }
                if(!isNozzleFound  && (mSales.getStatus().equals("100") || (mSales.getStatus().equals("105"))))
                    addProduct("Product ("+mSales.getProductId()+")", Double.valueOf(mSales.getQuantity()));
            }
            reportModel.setTransactionCount(transactionCount);
            reportModel.setPaymentCount(paymentCount);
            reportModel.setQuantityCount(quantityCount);
            mListener.onReport(true, "Success", reportModel);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(String... text) {

        }
    }
}
