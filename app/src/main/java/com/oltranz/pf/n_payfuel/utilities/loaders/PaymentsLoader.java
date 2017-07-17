package com.oltranz.pf.n_payfuel.utilities.loaders;

import android.content.Context;

import com.oltranz.pf.n_payfuel.config.PrimeServices;
import com.oltranz.pf.n_payfuel.config.ServiceGenerator;
import com.oltranz.pf.n_payfuel.entities.MPayment;
import com.oltranz.pf.n_payfuel.models.payments.PaymentsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 6/8/2017.
 */

public class PaymentsLoader {
    private PaymentLoaderInteraction mListener;
    private Context context;
    private String message, userId;
    private PaymentsResponse paymentsResponse = null;

    public PaymentsLoader(PaymentLoaderInteraction mListener, Context context, String userId) {
        this.mListener = mListener;
        this.context = context;
        this.userId = userId;
    }

    public void startLoading(){
        BackLoading backLoading = new BackLoading();
        backLoading.execute(userId);
    }

    public interface PaymentLoaderInteraction{
        void onPaymentLoader(boolean isLoaded, Object object);
    }

    private class BackLoading{
        void execute(String... parms) {
            String userId = parms[0];
            try {

                PrimeServices primeServices = ServiceGenerator.createService(PrimeServices.class, PrimeServices.BASE_URL);
                Call<PaymentsResponse> callService = primeServices.getPaymentsMode(userId);
                callService.enqueue(new Callback<PaymentsResponse>() {
                    @Override
                    public void onResponse(Call<PaymentsResponse> call, Response<PaymentsResponse> response) {
                        int statusCode = response.code();

                        if(statusCode == 500){
                            message = "internal server error";
                            onPostExecute(paymentsResponse);
                            return;
                        }else if(statusCode != 200){
                            message = response.message();
                            onPostExecute(paymentsResponse);
                            return;
                        }

                        if(response.body() == null){
                            message = "Empty server response";
                            onPostExecute(paymentsResponse);
                            return;
                        }
                        message = response.body().getMessage() != null ? "("+response.body().getStatusCode()+") "+response.body().getMessage() : "("+response.body().getStatusCode()+") Empty server message";
                        onPostExecute(response.body());
                    }

                    @Override
                    public void onFailure(Call<PaymentsResponse> call, Throwable t) {
                        message = "Network failure. "+t.getMessage();
                        onPostExecute(paymentsResponse);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                onPostExecute(paymentsResponse);
            }
        }

        protected void onPostExecute(PaymentsResponse paymentsResponse) {
            try{
                if(paymentsResponse == null)
                    mListener.onPaymentLoader(false, message);
                else{
                    if(paymentsResponse.getmPayments() != null && !paymentsResponse.getmPayments().isEmpty()){
                        MPayment.deleteAll(MPayment.class);
                        for(MPayment mPayment : paymentsResponse.getmPayments()){
                            long dbResult = mPayment.save();
                            if(dbResult < 0){
                                MPayment.deleteAll(MPayment.class);
                                mListener.onPaymentLoader(false, "Failed to load payment modes into local database. DB result: "+dbResult);
                                return;
                            }
                        }

                        //List<MPayment> mPayments = MPayment.listAll(MPayment.class);
                        mListener.onPaymentLoader(true, paymentsResponse);
                    }else{
                        mListener.onPaymentLoader(false, "Error loading payment modes. "+paymentsResponse.getMessage() != null ? paymentsResponse.getMessage() : "" );
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                mListener.onPaymentLoader(false, "Error: "+e.getMessage());
            }

        }
    }
}
