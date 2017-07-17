package com.oltranz.pf.n_payfuel.utilities.printing;

import android.content.Context;
import android.os.AsyncTask;

import com.oltranz.pf.n_payfuel.models.reportmodel.ReportModel;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/29/2017.
 */

public class ReportPrint {
    private OnReportPrint mListener;
    private Context context;
    private ReportModel reportModel;

    public ReportPrint(OnReportPrint mListener, Context context, ReportModel reportModel) {
        this.mListener = mListener;
        this.context = context;
        this.reportModel = reportModel;
    }

    public void startPrinting(){
        new PrintHandler().execute(reportModel);
    }

    public interface OnReportPrint{
        void onReportPrint(String printingMessage);
    }

    private class PrintHandler extends AsyncTask<ReportModel, String, String> {

        ReportModel reportModel;
        @Override
        protected String doInBackground(ReportModel... params) {
            try {
                reportModel = params[0];
                PrinterLoader printerLoader = new PrinterLoader(context, false, reportModel);
                return printerLoader.printOut();
            }catch (Exception e){
                e.printStackTrace();
                return "Printing failed due to: "+e.getMessage();
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            mListener.onReportPrint(result);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(String... text) {

        }
    }
}
