package com.oltranz.pf.n_payfuel.utilities.loaders;

import android.content.Context;

import com.oltranz.pf.n_payfuel.config.PrimeServices;
import com.oltranz.pf.n_payfuel.config.ServiceGenerator;
import com.oltranz.pf.n_payfuel.entities.MDevice;
import com.oltranz.pf.n_payfuel.models.register.RegisterRequest;
import com.oltranz.pf.n_payfuel.models.register.RegisterResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/9/2017.
 */

public class RegisterLoader {
    private RegisterLoaderInteraction mListener;
    private Context context;
    private RegisterRequest registerRequest;
    private String message;
    private RegisterResponse registerResponse = null;

    public RegisterLoader(RegisterLoaderInteraction mListener, Context context, RegisterRequest registerRequest) {
        this.mListener = mListener;
        this.context = context;
        this.registerRequest = registerRequest;
    }

    public void startLoading(){
        BackLoading backLoading = new BackLoading();
        backLoading.execute("");
    }

    public interface RegisterLoaderInteraction{
        void onRegisterLoader(boolean isRegistered, Object object);
    }

    private class BackLoading{
        private void execute(String... parms) {
            try {
                PrimeServices primeServices = ServiceGenerator.createService(PrimeServices.class, PrimeServices.BASE_URL);
                Call<RegisterResponse> callService = primeServices.enrolDevice(registerRequest);
                callService.enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                        int statusCode = response.code();

                        if(statusCode == 500){
                            message = "Internal server error";
                            onPostExecute(registerResponse);
                            return;
                        }else if(statusCode != 200){
                            message = response.message();
                            onPostExecute(registerResponse);
                            return;
                        }

                        if(response.body() == null){
                            message = "Empty server response";
                            onPostExecute(registerResponse);
                            return;
                        }
                        message = response.body().getMessage() != null ? "("+response.body().getStatusCode()+") "+response.body().getMessage() : "("+response.body().getStatusCode()+") Empty server message";
                        onPostExecute(response.body());
                    }

                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        message = "Network failure. "+t.getMessage();
                        onPostExecute(registerResponse);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                onPostExecute(registerResponse);
            }
        }

        void onPostExecute(RegisterResponse reserveResponse) {
            try{
                if(reserveResponse == null)
                    mListener.onRegisterLoader(false, message);
                else{
                    if(reserveResponse.getEnrolData() != null && reserveResponse.getStatusCode() == 100){
                        MDevice.deleteAll(MDevice.class);
                        MDevice mDevice = new MDevice(registerRequest.getDeviceId(), registerRequest.getSerialNumber(), true);
                        long persistResult = mDevice.save();
                        if(persistResult < 0)
                            mListener.onRegisterLoader(false, "Creating local credential for this device failed");
                        else
                            mListener.onRegisterLoader(true, reserveResponse);
                    }else{
                        mListener.onRegisterLoader(false, "Error reserving pumps. "+reserveResponse.getMessage() != null ? reserveResponse.getMessage() : "" );
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                mListener.onRegisterLoader(false, "Error: "+e.getMessage());
            }

        }
    }
}
