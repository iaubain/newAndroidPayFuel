package com.oltranz.pf.n_payfuel.utilities.loaders;

import android.content.Context;

import com.oltranz.pf.n_payfuel.config.EquipmentServiceGenerator;
import com.oltranz.pf.n_payfuel.config.EquipmentServices;
import com.oltranz.pf.n_payfuel.config.PrimeServices;
import com.oltranz.pf.n_payfuel.config.ServiceGenerator;
import com.oltranz.pf.n_payfuel.models.commonmodels.CommonBranch;
import com.oltranz.pf.n_payfuel.models.pump.PumpResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 6/8/2017.
 */

public class PumpsLoader {
    private PumpsLoaderInteraction mListener;
    private Context context;
    private String message, branchId;
    private PumpResponse pumpResponse = null;

    public PumpsLoader(PumpsLoaderInteraction mListener, Context context, String branchId) {
        this.mListener = mListener;
        this.context = context;
        this.branchId = branchId;
    }

    public void startLoading(){
        BackLoading backLoading = new BackLoading();
        backLoading.execute(branchId);
    }

    public interface PumpsLoaderInteraction {
        void onPumpsLoader(boolean isLoaded, Object object);
    }

    private class BackLoading{
        void execute(String... parms) {
            String branchId = parms[0];
            try {

                EquipmentServices equipmentServices = EquipmentServiceGenerator.createService(EquipmentServices.class, EquipmentServices.BASE_URL);
                Call<PumpResponse> callService = equipmentServices.getPumps(new CommonBranch(branchId));
                callService.enqueue(new Callback<PumpResponse>() {
                    @Override
                    public void onResponse(Call<PumpResponse> call, Response<PumpResponse> response) {
                        int statusCode = response.code();

                        if(statusCode == 500){
                            message = "internal server error";
                            onPostExecute(pumpResponse);
                            return;
                        }else if(statusCode != 200){
                            message = response.message();
                            onPostExecute(pumpResponse);
                            return;
                        }

                        if(response.body() == null){
                            message = "Empty server response";
                            onPostExecute(pumpResponse);
                            return;
                        }
                        message = response.body().getMessage() != null ? "("+response.body().getStatusCode()+") "+response.body().getMessage() : "("+response.body().getStatusCode()+") Empty server message";
                        onPostExecute(response.body());
                    }

                    @Override
                    public void onFailure(Call<PumpResponse> call, Throwable t) {
                        message = "Network failure. "+t.getMessage();
                        onPostExecute(pumpResponse);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                onPostExecute(pumpResponse);
            }
        }

        protected void onPostExecute(PumpResponse pumpResponse) {
            try{
                if(pumpResponse == null)
                    mListener.onPumpsLoader(false, message);
                else{
                    if(pumpResponse.getPumpModel() != null && !pumpResponse.getPumpModel().isEmpty()){
                        mListener.onPumpsLoader(true, pumpResponse);
                    }else{
                        String errorMessage = pumpResponse.getMessage() != null ? pumpResponse.getMessage() : "";
                        mListener.onPumpsLoader(false, "Error loading pumps. "+ errorMessage);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                mListener.onPumpsLoader(false, "Error: "+e.getMessage());
            }

        }
    }
}
