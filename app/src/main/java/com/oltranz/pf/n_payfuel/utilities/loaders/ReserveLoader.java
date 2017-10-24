package com.oltranz.pf.n_payfuel.utilities.loaders;

import android.content.Context;

import com.oltranz.pf.n_payfuel.config.EquipmentServiceGenerator;
import com.oltranz.pf.n_payfuel.config.EquipmentServices;
import com.oltranz.pf.n_payfuel.config.PrimeServices;
import com.oltranz.pf.n_payfuel.config.ServiceGenerator;
import com.oltranz.pf.n_payfuel.models.reserve.ReserveModel;
import com.oltranz.pf.n_payfuel.models.reserve.ReserveResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/9/2017.
 */

public class ReserveLoader {
    private ReserveLoaderInteraction mListener;
    private Context context;
    private List<ReserveModel> mReserve;
    private String message;
    private ReserveResponse reserveResponse = null;

    public ReserveLoader(ReserveLoaderInteraction mListener, Context context, List<ReserveModel> mReserve) {
        this.mListener = mListener;
        this.context = context;
        this.mReserve = mReserve;
    }

    public void startLoading(){
        BackLoading backLoading = new BackLoading();
        backLoading.execute("");
    }

    public interface ReserveLoaderInteraction {
        void onReserveLoader(boolean isLoaded, Object object);
    }

    private class BackLoading{
        void execute(String... parms) {
            try {

                EquipmentServices equipmentServices = EquipmentServiceGenerator.createService(EquipmentServices.class, EquipmentServices.BASE_URL);
                Call<ReserveResponse> callService = equipmentServices.reservePump(mReserve);
                callService.enqueue(new Callback<ReserveResponse>() {
                    @Override
                    public void onResponse(Call<ReserveResponse> call, Response<ReserveResponse> response) {
                        int statusCode = response.code();

                        if(statusCode == 500){
                            message = "Internal server error";
                            onPostExecute(reserveResponse);
                            return;
                        }else if(statusCode != 200){
                            message = response.message();
                            onPostExecute(reserveResponse);
                            return;
                        }

                        if(response.body() == null){
                            message = "Empty server response";
                            onPostExecute(reserveResponse);
                            return;
                        }
                        message = response.body().getMessage() != null ? "("+response.body().getStatusCode()+") "+response.body().getMessage() : "("+response.body().getStatusCode()+") Empty server message";
                        onPostExecute(response.body());
                    }

                    @Override
                    public void onFailure(Call<ReserveResponse> call, Throwable t) {
                        message = "Network failure. "+t.getMessage();
                        onPostExecute(reserveResponse);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                onPostExecute(reserveResponse);
            }
        }


        protected void onPostExecute(ReserveResponse reserveResponse) {
            try{
                if(reserveResponse == null)
                    mListener.onReserveLoader(false, message);
                else{
                    if(reserveResponse.getReserveModelList() != null && reserveResponse.getStatusCode() == 100 && !reserveResponse.getReserveModelList().isEmpty()){
                        mListener.onReserveLoader(true, reserveResponse);
                    }else{
                        mListener.onReserveLoader(false, "Error reserving pumps. "+reserveResponse.getMessage() != null ? reserveResponse.getMessage() : "" );
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                mListener.onReserveLoader(false, "Error: "+e.getMessage());
            }

        }
    }
}
