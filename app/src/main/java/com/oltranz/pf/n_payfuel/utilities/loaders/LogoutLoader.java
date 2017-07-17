package com.oltranz.pf.n_payfuel.utilities.loaders;

import android.content.Context;

import com.oltranz.pf.n_payfuel.config.PrimeServices;
import com.oltranz.pf.n_payfuel.config.ServiceGenerator;
import com.oltranz.pf.n_payfuel.entities.MUser;
import com.oltranz.pf.n_payfuel.models.logout.LogoutRequest;
import com.oltranz.pf.n_payfuel.models.logout.LogoutResponse;
import com.oltranz.pf.n_payfuel.utilities.DbBulk;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/17/2017.
 */

public class LogoutLoader {
    private LogoutLoaderInteraction mListener;
    private Context context;
    private LogoutRequest logoutRequest;
    private String message;
    private LogoutResponse logoutResponse = null;

    public LogoutLoader(LogoutLoaderInteraction mListener, Context context, LogoutRequest logoutRequest) {
        this.mListener = mListener;
        this.context = context;
        this.logoutRequest = logoutRequest;
    }

    public void startLoading(){
        BackLoading backLoading = new BackLoading();
        backLoading.execute("");
    }

    public interface LogoutLoaderInteraction {
        void onLogout(boolean isLoaded, Object object);
    }

    private class BackLoading {
        void execute(String... parms) {
            try {
                //TODO remember to remove logoutRequest.setDeviceId("oFMSTest
                //logoutRequest.setDeviceId("oFMSTest");
                PrimeServices primeServices = ServiceGenerator.createService(PrimeServices.class, PrimeServices.BASE_URL);
                Call<LogoutResponse> callService = primeServices.logout(logoutRequest);
                callService.enqueue(new Callback<LogoutResponse>() {
                    @Override
                    public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                        int statusCode = response.code();

                        if(statusCode == 500){
                            message = "Internal server error";
                            onPostExecute(logoutResponse);
                            return;
                        }else if(statusCode != 200){
                            message = response.message();
                            onPostExecute(logoutResponse);
                            return;
                        }

                        if(response.body() == null){
                            message = "Empty server response";
                            onPostExecute(logoutResponse);
                            return;
                        }
                        message = response.body().getMessage() != null ? "("+response.body().getStatusCode()+") "+response.body().getMessage() : "("+response.body().getStatusCode()+") Empty server message";
                        onPostExecute(response.body());
                    }

                    @Override
                    public void onFailure(Call<LogoutResponse> call, Throwable t) {
                        message = "Network call failure. "+t.getMessage();
                        onPostExecute(logoutResponse);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                onPostExecute(logoutResponse);
            }
        }

        protected void onPostExecute(LogoutResponse logoutResponse) {
            try{
                if(logoutResponse == null)
                    mListener.onLogout(false, message);
                else{
                    if(logoutResponse.getmUser() != null && logoutResponse.getStatusCode() == 100){
                        MUser mUser = DbBulk.getUser(logoutResponse.getmUser().getUserId());
                        if(mUser.delete()){
                            mListener.onLogout(true, logoutResponse);
                        }else{
                            mListener.onLogout(false, "Failed to logout local user");
                        }
                    }else{
                        mListener.onLogout(false, "Logout error. "+message);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                mListener.onLogout(false, "Error: "+e.getMessage());
            }

        }
    }
}
