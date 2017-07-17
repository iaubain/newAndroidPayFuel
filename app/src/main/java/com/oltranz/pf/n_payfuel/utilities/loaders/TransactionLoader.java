package com.oltranz.pf.n_payfuel.utilities.loaders;

import android.util.Log;

import com.oltranz.pf.n_payfuel.config.PrimeServices;
import com.oltranz.pf.n_payfuel.config.ServiceGenerator;
import com.oltranz.pf.n_payfuel.entities.MSales;
import com.oltranz.pf.n_payfuel.models.sales.SalesRequest;
import com.oltranz.pf.n_payfuel.models.sales.SalesResponse;
import com.oltranz.pf.n_payfuel.utilities.DataFactory;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.System.out;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/20/2017.
 */

public class TransactionLoader {
    private OnTransactionLoader mListener;
    private MSales mSales;
    private String message;

    public TransactionLoader(OnTransactionLoader mListener, MSales mSales) {
        this.mListener = mListener;
        this.mSales = mSales;
    }

    public void startLoading(){
        try {
            final JSONObject jsonObject = new JSONObject(DataFactory.objectToString(mSales));
            jsonObject.remove("id");
            SalesRequest salesRequest = (SalesRequest) DataFactory.stringToObject(SalesRequest.class, jsonObject.toString());
            Log.d("Request", jsonObject.toString());
            PrimeServices primeServices = ServiceGenerator.createService(PrimeServices.class, PrimeServices.BASE_URL);
            Call<SalesResponse> callService = primeServices.postSales(salesRequest);
            callService.enqueue(new Callback<SalesResponse>() {
                @Override
                public void onResponse(Call<SalesResponse> call, Response<SalesResponse> response) {
                    int statusCode = response.code();

                    String serverRequest = jsonObject.toString();
                    out.print(serverRequest);
                    Log.d("Response", "HTTP STATUS: "+statusCode+" "+DataFactory.objectToString(response.body()));

                    if(statusCode == 500){
                        message = "Internal server error";
                        mListener.onTransactionLoader(false, statusCode, message, mSales, null);
                        return;
                    }else if(statusCode != 200){
                        message = response.message();
                        mListener.onTransactionLoader(false, statusCode, message, mSales, null);
                        return;
                    }

                    if(response.body() == null && response.body().getmSales() != null){
                        message = "Empty server response";
                        mListener.onTransactionLoader(false, statusCode, message, mSales, null);
                        return;
                    }
                    message = response.body().getMessage() != null ? "("+response.body().getStatusCode()+") "+response.body().getMessage() : "("+response.body().getStatusCode()+") Empty server message";
                    mListener.onTransactionLoader(true, statusCode, message, mSales, response.body());
                }

                @Override
                public void onFailure(Call<SalesResponse> call, Throwable t) {
                    message = "General failure. "+t.getMessage();
                    mListener.onTransactionLoader(false, -1, message, mSales, null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnTransactionLoader{
        void onTransactionLoader(boolean isDone, int serverStatus, String message, MSales mSales, SalesResponse salesResponse);
    }
}
