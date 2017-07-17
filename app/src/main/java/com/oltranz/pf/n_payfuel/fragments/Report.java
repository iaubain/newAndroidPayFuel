package com.oltranz.pf.n_payfuel.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.oltranz.pf.n_payfuel.R;
import com.oltranz.pf.n_payfuel.config.AppFlow;
import com.oltranz.pf.n_payfuel.models.login.LoginResponse;
import com.oltranz.pf.n_payfuel.models.reportmodel.ReportModel;
import com.oltranz.pf.n_payfuel.models.reportmodel.ReportRequest;
import com.oltranz.pf.n_payfuel.utilities.DataFactory;
import com.oltranz.pf.n_payfuel.utilities.loaders.LocalReportLoader;
import com.oltranz.pf.n_payfuel.utilities.printing.ReportPrint;
import com.oltranz.pf.n_payfuel.utilities.views.MyLabel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnReportInteraction} interface
 * to handle interaction events.
 * Use the {@link Report#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Report extends Fragment implements LocalReportLoader.OnReportLoader,
        ReportPrint.OnReportPrint {
    private static final String LOGIN_PARAM = "LOGIN_PARAM";
    @BindView(R.id.printer)
    FloatingActionButton printer;
    @BindView(R.id.reportContainer)
    LinearLayout container;
    @BindView(R.id.dateFrom)
    MyLabel dateFrom;
    @BindView(R.id.dateTo)
    MyLabel dateTo;
    private LoginResponse loginResponse;
    private OnReportInteraction mListener;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private ReportModel reportModel;

    public Report() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param loginResponseParam Parameter 1.
     * @return A new instance of fragment Report.
     */
    public static Report newInstance(String loginResponseParam) {
        Report fragment = new Report();
        Bundle args = new Bundle();
        args.putString(LOGIN_PARAM, loginResponseParam);
        fragment.setArguments(args);
        return fragment;
    }

    private void popBoxExit(String message){
        try {
            builder = new AlertDialog.Builder(getContext(), R.style.SimpleDialog);
            builder.setMessage(message != null ? message : "NO_MESSAGE")
                    .setTitle(R.string.dialog_title);
            // Add the buttons
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mListener.onReport(false, null);
                    dialog.dismiss();
                }
            });
            dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), message != null ? message : "NO_MESSAGE", Toast.LENGTH_SHORT).show();
        }
    }

    private void popBox(String message){
        try {
            builder = new AlertDialog.Builder(getContext(), R.style.SimpleDialog);
            builder.setMessage(message != null ? message : "NO_MESSAGE")
                    .setTitle(R.string.dialog_title);
            // Add the buttons
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), message != null ? message : "NO_MESSAGE", Toast.LENGTH_SHORT).show();
        }
    }

    private void datePopUp(List<String> mDates){
        try {
            final Dialog dialog = new Dialog(getContext(), R.style.SimpleDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.date_layout);

            ListView viewDate = (ListView) dialog.findViewById(R.id.dList);
            ArrayAdapter<String> datesAdapter = new ArrayAdapter<String>(getContext(), R.layout.date_style, mDates);

            viewDate.setAdapter(datesAdapter);
            viewDate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.dismiss();
                    dateFrom.setText("From: "+((MyLabel)view).getText().toString());
                    mProgress("Preparing report");
                    final DateFormat dFomat = new SimpleDateFormat("yyy-MM-dd", Locale.getDefault());
                    Date cDate = new Date();
                    LocalReportLoader reportLoader = new LocalReportLoader(Report.this, new ReportRequest(loginResponse.getmUser(), ((MyLabel)view).getText().toString(), dFomat.format(cDate)));
                    reportLoader.startLoading();
                }
            });

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Internal application error", Toast.LENGTH_SHORT).show();
        }
    }

    private void mProgress(String message){
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.setMessage(message != null ? message : "NO_MESSAGE");
        progressDialog.show();
    }

    private void dismissProgress(){
        if (progressDialog != null)
            if (progressDialog.isShowing())
                progressDialog.dismiss();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loginResponse = (LoginResponse) DataFactory.stringToObject(LoginResponse.class, getArguments().getString(LOGIN_PARAM));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.report_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        mProgress("Preparing report");
        final DateFormat dFomat = new SimpleDateFormat("yyy-MM-dd", Locale.getDefault());
        final Date cDate = new Date();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(cDate);

        final List<String> mDates = new ArrayList<>();

        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDates.clear();
                for(int i = 1; i <= AppFlow.MAX_HISTORY; i++){
                    Calendar tempCalendar = calendar;
                    tempCalendar.add(Calendar.DAY_OF_MONTH, (-1*i));
                    mDates.add(dFomat.format(tempCalendar.getTime()));
                    tempCalendar.setTime(cDate);
                }
                //show popup of date list view
                datePopUp(mDates);
            }
        });

        dateFrom.setText("From: "+dFomat.format(cDate));
        dateTo.setText("To: "+dFomat.format(cDate));
        LocalReportLoader reportLoader = new LocalReportLoader(Report.this, new ReportRequest(loginResponse.getmUser(), dFomat.format(cDate), dFomat.format(cDate)));
        reportLoader.startLoading();

        printer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(reportModel == null){
                    popBox("No report found.");
                    return;
                }
                mProgress("Printing");
                ReportPrint reportPrint = new ReportPrint(Report.this, getContext(), reportModel);
                reportPrint.startPrinting();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnReportInteraction) {
            mListener = (OnReportInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onReport(boolean isDone, String message, ReportModel reportModel) {
        dismissProgress();
        if(!isDone){
            popBoxExit(message);
            return;
        }
        this.reportModel = reportModel;
        prepareUI(reportModel);
    }

    private void prepareUI(ReportModel reportModel){
        container.removeAllViews();
        LinearLayout.LayoutParams pWeight = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        pWeight.weight = 1.0f;


        LinearLayout rowParent = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,15);
        rowParent.setLayoutParams(layoutParams);
        rowParent.setOrientation(LinearLayout.HORIZONTAL);

        MyLabel lbl = new MyLabel(getContext());
        pWeight.setMargins(0,0,4,0);
        lbl.setLayoutParams(pWeight);
        lbl.setText("User ");
        lbl.setTextSize(21);
        lbl.setGravity(Gravity.LEFT);

        MyLabel value = new MyLabel(getContext());
        pWeight.setMargins(4,0,0,0);
        value.setLayoutParams(pWeight);
        value.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        value.setText(" "+reportModel.getUser().getName());
        value.setTextSize(21);
        value.setGravity(Gravity.LEFT);

        rowParent.addView(lbl);
        rowParent.addView(value);
        container.addView(rowParent);

        MyLabel mLabel = new MyLabel(getContext());
        layoutParams.setMargins(0,3,0,15);
        mLabel.setLayoutParams(layoutParams);
        mLabel.setTextSize(18);
        mLabel.setText("\nTRANSACTIONS");
        mLabel.setGravity(Gravity.LEFT);
        container.addView(mLabel);

        for(Map.Entry<String, Long> entry : reportModel.getTransactionCount().entrySet()){
            rowParent = new LinearLayout(getContext());
            layoutParams.setMargins(0,0,0,4);
            rowParent.setLayoutParams(layoutParams);
            rowParent.setOrientation(LinearLayout.HORIZONTAL);

            lbl = new MyLabel(getContext());
            pWeight.setMargins(0,0,4,0);
            lbl.setLayoutParams(pWeight);
            lbl.setText(entry.getKey());
            lbl.setTextSize(15);
            lbl.setGravity(Gravity.LEFT);

            value = new MyLabel(getContext());
            pWeight.setMargins(4,0,0,0);
            value.setLayoutParams(pWeight);
            value.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            value.setText(entry.getValue()+"");
            value.setTextSize(15);
            value.setGravity(Gravity.LEFT);

            rowParent.addView(lbl);
            rowParent.addView(value);
            container.addView(rowParent);
        }

        rowParent = new LinearLayout(getContext());
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,9);
        rowParent.setLayoutParams(layoutParams);
        rowParent.setOrientation(LinearLayout.HORIZONTAL);

        lbl = new MyLabel(getContext());
        pWeight.setMargins(0,0,4,0);
        lbl.setLayoutParams(pWeight);
        lbl.setText("Total transactions ");
        lbl.setTextSize(15);
        lbl.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        lbl.setGravity(Gravity.LEFT);

        value = new MyLabel(getContext());
        pWeight.setMargins(4,0,0,0);
        value.setLayoutParams(pWeight);
        value.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        value.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        value.setText(" "+reportModel.getTotalTransaction());
        value.setTextSize(15);
        value.setGravity(Gravity.LEFT);

        rowParent.addView(lbl);
        rowParent.addView(value);
        container.addView(rowParent);

        mLabel = new MyLabel(getContext());
        layoutParams.setMargins(0,0,0,15);
        mLabel.setLayoutParams(layoutParams);
        mLabel.setTextSize(18);
        mLabel.setText("\nPAYMENTS");
        mLabel.setGravity(Gravity.LEFT);
        container.addView(mLabel);

        for(Map.Entry<String, Double> entry : reportModel.getPaymentCount().entrySet()){
            rowParent = new LinearLayout(getContext());
            layoutParams.setMargins(0,0,0,4);
            rowParent.setLayoutParams(layoutParams);
            rowParent.setOrientation(LinearLayout.HORIZONTAL);

            lbl = new MyLabel(getContext());
            pWeight.setMargins(0,0,4,0);
            lbl.setLayoutParams(pWeight);
            lbl.setText(entry.getKey());
            lbl.setTextSize(15);
            lbl.setGravity(Gravity.LEFT);

            value = new MyLabel(getContext());
            pWeight.setMargins(4,0,0,0);
            value.setLayoutParams(pWeight);
            value.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            value.setText(entry.getValue()+" "+AppFlow.CURRENCY);
            value.setTextSize(15);
            value.setGravity(Gravity.LEFT);

            rowParent.addView(lbl);
            rowParent.addView(value);
            container.addView(rowParent);
        }

        rowParent = new LinearLayout(getContext());
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,9);
        rowParent.setLayoutParams(layoutParams);
        rowParent.setOrientation(LinearLayout.HORIZONTAL);

        lbl = new MyLabel(getContext());
        pWeight.setMargins(0,0,4,0);
        lbl.setLayoutParams(pWeight);
        lbl.setText("Total sold amount ");
        lbl.setTextSize(15);
        lbl.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        lbl.setGravity(Gravity.LEFT);

        value = new MyLabel(getContext());
        pWeight.setMargins(4,0,0,0);
        value.setLayoutParams(pWeight);
        value.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        value.setText(" "+DataFactory.formatDouble(reportModel.getTotalSoldAmount())+" "+AppFlow.CURRENCY );
        value.setTextSize(15);
        value.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        value.setGravity(Gravity.LEFT);

        rowParent.addView(lbl);
        rowParent.addView(value);
        container.addView(rowParent);


        mLabel = new MyLabel(getContext());
        layoutParams.setMargins(0,0,0,15);
        mLabel.setLayoutParams(layoutParams);
        mLabel.setTextSize(18);
        mLabel.setText("\nSOLD QUANTITY");
        mLabel.setGravity(Gravity.LEFT);
        container.addView(mLabel);

        for(Map.Entry<String, Double> entry : reportModel.getQuantityCount().entrySet()){
            rowParent = new LinearLayout(getContext());
            layoutParams.setMargins(0,0,0,4);
            rowParent.setLayoutParams(layoutParams);
            rowParent.setOrientation(LinearLayout.HORIZONTAL);

            lbl = new MyLabel(getContext());
            pWeight.setMargins(0,0,4,0);
            lbl.setLayoutParams(pWeight);
            lbl.setText(entry.getKey());
            lbl.setTextSize(15);
            lbl.setGravity(Gravity.LEFT);

            value = new MyLabel(getContext());
            pWeight.setMargins(4,0,0,0);
            value.setLayoutParams(pWeight);
            value.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            value.setText(entry.getValue()+" "+AppFlow.QUANTITY_MEASURE);
            value.setTextSize(15);
            value.setGravity(Gravity.LEFT);

            rowParent.addView(lbl);
            rowParent.addView(value);
            container.addView(rowParent);
        }

        rowParent = new LinearLayout(getContext());
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,9,0,0);
        rowParent.setLayoutParams(layoutParams);
        rowParent.setOrientation(LinearLayout.HORIZONTAL);

        lbl = new MyLabel(getContext());
        pWeight.setMargins(0,0,4,0);
        lbl.setLayoutParams(pWeight);
        lbl.setText("\nReport date ");
        lbl.setTextSize(15);
        lbl.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        lbl.setGravity(Gravity.LEFT);

        value = new MyLabel(getContext());
        pWeight.setMargins(4,0,0,0);
        value.setLayoutParams(pWeight);
        value.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        value.setText("\n"+reportModel.getDate());
        value.setTextSize(15);
        value.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        value.setGravity(Gravity.LEFT);

        rowParent.addView(lbl);
        rowParent.addView(value);
        container.addView(rowParent);
    }

    @Override
    public void onReportPrint(String printingMessage) {
        dismissProgress();
        try {
            Toast.makeText(getContext(),printingMessage, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public interface OnReportInteraction {
        void onReport(boolean isModuleFine, Object object);
    }
}
