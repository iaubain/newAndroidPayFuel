package com.oltranz.pf.n_payfuel.utilities.loaders;

import android.os.AsyncTask;

import com.oltranz.pf.n_payfuel.entities.MSales;
import com.oltranz.pf.n_payfuel.utilities.DbBulk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/29/2017.
 */

public class LocalHistoryLoader {
    private OnLocalHistory mListener;
    private String userId;

    public LocalHistoryLoader(OnLocalHistory mListener, String userId) {
        this.mListener = mListener;
        this.userId = userId;
    }
    public void startLoading(){
        new LoadLocalHistory().execute(userId);
    }

    public interface OnLocalHistory{
        void onHistory(boolean isDone, String message, List<MSales> mSalesList);
    }

    private class LoadLocalHistory extends AsyncTask<String, String, List<MSales>> {

        @Override
        protected List<MSales> doInBackground(String... params) {
            String userId = params[0];
            List<MSales> mSalesList = new ArrayList<>();
            try {
                mSalesList = DbBulk.getUserSalesHistory(userId);
                return mSalesList;
            }catch (Exception e){
                e.printStackTrace();
                return mSalesList;
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(List<MSales> result) {
            if(result.isEmpty()){
                mListener.onHistory(false, "Transactions not found.", result);
                return;
            }
            mListener.onHistory(true, "Success", result);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(String... text) {

        }
    }
}
