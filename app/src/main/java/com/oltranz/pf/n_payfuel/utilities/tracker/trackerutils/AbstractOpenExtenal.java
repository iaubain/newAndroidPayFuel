package com.oltranz.pf.n_payfuel.utilities.tracker.trackerutils;

import android.util.Log;

import com.oltranz.pf.n_payfuel.utilities.DataFactory;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackerclient.TrackerClientServices;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackerclient.TrackerServerClient;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels.CommandReport;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels.PingRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 4/26/2017.
 */

public abstract class AbstractOpenExtenal<T> {
    private Class<T> resultClass;
    private T returnT;
    private OnOpenExternal mListener;

    public AbstractOpenExtenal(Class<T> resultClass) {
        this.resultClass = resultClass;
    }

    public T pingMonitor(String cmd, String devicetype, PingRequest pingRequest) {

        try {
            Log.d("Server request", DataFactory.objectToString(pingRequest));
            TrackerClientServices clientServices = TrackerServerClient.createService(TrackerClientServices.class, TrackerClientServices.BASE_URL, null);
            Call<T> callService = clientServices.makePing(cmd, devicetype, pingRequest);
            callService.enqueue(new Callback<T>() {
                @Override
                public void onResponse(Call<T> call, Response<T> response) {
                    //HTTP status code
                    int statusCode = response.code();
                    if (statusCode == 500) {
                        Log.d("Server response", "Got from server HTTP Status: " + statusCode);
                        returnT = null;
//                        mListener.onOpenExternalResults(statusCode, response.message());
//                        return;
                    }
                    if (statusCode != 200) {
                        Log.d("Server response", "Got from server HTTP Status: " + statusCode);
                        returnT = null;
//                        mListener.onOpenExternalResults(statusCode, response.message());
//                        return;
                    }

                    if (response.body() != null) {
                        returnT = response.body();
//                        mListener.onOpenExternalResults(statusCode, response.body());
//                        return;
                    }
                    Log.d("Server response: ", "Message: " + response.message() + "Body:" + new DataFactory().objectToString(response.body()));
//                    mListener.onOpenExternalResults(statusCode, null);
                    returnT = null;
                }

                @Override
                public void onFailure(Call<T> call, Throwable t) {
                    // Log error here since request failed
                    Log.d("Server response", "Error during the process: " + t.getMessage());
//                    mListener.onOpenExternalResults(501, t.getMessage());
                    returnT = null;
                }
            });

            return returnT;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public T reportCommand(String cmd, String devicetype, String deviceId, String reportId, CommandReport commandReport) {
        try {
            Log.d("Server request", new DataFactory().objectToString(commandReport));
            TrackerClientServices clientServices = TrackerServerClient.createService(TrackerClientServices.class, TrackerClientServices.BASE_URL, null);
            Call<T> callService = clientServices.reportCommand(cmd, devicetype, deviceId, reportId, commandReport);
            callService.enqueue(new Callback<T>() {
                @Override
                public void onResponse(Call<T> call, Response<T> response) {

                    //HTTP status code
                    int statusCode = response.code();
                    if (statusCode == 500) {
                        Log.d("Server response", "Got from server HTTP Status: " + statusCode);
                        returnT = null;
//                        mListener.onOpenExternalResults(statusCode, response.message());
//                        return;
                    }
                    if (statusCode != 200) {
                        Log.d("Server response", "Got from server HTTP Status: " + statusCode);
                        returnT = null;
//                        mListener.onOpenExternalResults(statusCode, response.message());
//                        return;
                    }

                    if (response.body() != null) {
                        returnT = response.body();
//                        mListener.onOpenExternalResults(statusCode, response.body());
//                        return;
                    }
                    Log.d("Server response: ", "Message: " + response.message() + "Body:" + new DataFactory().objectToString(response.body()));
//                    mListener.onOpenExternalResults(statusCode, null);
                    returnT = null;
                }

                @Override
                public void onFailure(Call<T> call, Throwable t) {
                    // Log error here since request failed
                    Log.d("Server response", "Error during the process: " + t.getMessage());
//                    mListener.onOpenExternalResults(501, t.getMessage());
                    returnT = null;
                }
            });

            return returnT;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface OnOpenExternal {
        void onOpenExternalResults(int httpStatus, Object httpResult);
    }
}
