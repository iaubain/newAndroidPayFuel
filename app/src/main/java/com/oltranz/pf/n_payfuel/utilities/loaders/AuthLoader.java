package com.oltranz.pf.n_payfuel.utilities.loaders;

import android.content.Context;

import com.oltranz.pf.n_payfuel.config.PrimeServices;
import com.oltranz.pf.n_payfuel.config.ServiceGenerator;
import com.oltranz.pf.n_payfuel.config.SessionStatusConfig;
import com.oltranz.pf.n_payfuel.entities.MUser;
import com.oltranz.pf.n_payfuel.models.login.LoginRequest;
import com.oltranz.pf.n_payfuel.models.login.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/9/2017.
 */

public class AuthLoader {
    private AuthLoaderInteraction mListener;
    private Context context;
    private LoginRequest loginRequest;
    private String message;
    private LoginResponse loginResponse = null;

    public AuthLoader(AuthLoaderInteraction mListener, Context context, LoginRequest loginRequest) {
        this.mListener = mListener;
        this.context = context;
        this.loginRequest = loginRequest;
    }

    public void startLoading(){
        BackLoading backLoading = new BackLoading();
        backLoading.execute("");
    }

    public interface AuthLoaderInteraction{
        void onAuthLoader(boolean isLoaded, Object object);
    }

    private class BackLoading {
        void execute(String... parms) {
            try {
                PrimeServices primeServices = ServiceGenerator.createService(PrimeServices.class, PrimeServices.BASE_URL);
                Call<LoginResponse> callService = primeServices.login(loginRequest);
                callService.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        int statusCode = response.code();

                        if(statusCode == 500){
                            message = "Internal server error";
                            onPostExecute(loginResponse);
                            return;
                        }else if(statusCode != 200){
                            message = response.message();
                            onPostExecute(loginResponse);
                            return;
                        }

                        if(response.body() == null){
                            message = "Empty server response";
                            onPostExecute(loginResponse);
                            return;
                        }
                        message = response.body().getMessage() != null ? "("+response.body().getStatusCode()+") "+response.body().getMessage() : "("+response.body().getStatusCode()+") Empty server message";
                        onPostExecute(response.body());
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        message = "Network call failure. "+t.getMessage();
                        onPostExecute(loginResponse);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                onPostExecute(loginResponse);
            }
        }

        protected void onPostExecute(LoginResponse loginResponse) {
            try{
                if(loginResponse == null)
                    mListener.onAuthLoader(false, message);
                else{
                    if(loginResponse.getmUser() != null && loginResponse.getStatusCode() == 100){
                        MUser.deleteAll(MUser.class);
                        MUser mUser = loginResponse.getmUser();
                        mUser.setUserPin(loginRequest.getUserPin());
                        mUser.setSessionStatus(SessionStatusConfig.STARTING);
                        long persistResult = mUser.save();
                        if(persistResult < 0 ){
                            mListener.onAuthLoader(false, "Failed to load user into local database. DB result: "+persistResult);
                            return;
                        }
                        mListener.onAuthLoader(true, loginResponse);
                    }else{
                        mListener.onAuthLoader(false, "Authentication error. "+message);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                mListener.onAuthLoader(false, "Error: "+e.getMessage());
            }

        }
    }
}
