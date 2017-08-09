package com.oltranz.pf.n_payfuel;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.oltranz.pf.n_payfuel.config.InterActiveConfig;
import com.oltranz.pf.n_payfuel.entities.MSales;
import com.oltranz.pf.n_payfuel.fragments.HistorySales;
import com.oltranz.pf.n_payfuel.fragments.Report;
import com.oltranz.pf.n_payfuel.fragments.Sales;
import com.oltranz.pf.n_payfuel.fragments.VoucherModule;
import com.oltranz.pf.n_payfuel.models.login.LoginResponse;
import com.oltranz.pf.n_payfuel.models.logout.LogoutRequest;
import com.oltranz.pf.n_payfuel.utilities.DataFactory;
import com.oltranz.pf.n_payfuel.utilities.DbBulk;
import com.oltranz.pf.n_payfuel.utilities.alarm.MyAlarmManager;
import com.oltranz.pf.n_payfuel.utilities.loaders.LogoutLoader;
import com.oltranz.pf.n_payfuel.utilities.loaders.ResumeLoader;
import com.oltranz.pf.n_payfuel.utilities.services.TransactionRectifier;
import com.oltranz.pf.n_payfuel.utilities.views.MyLabel;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LogoutLoader.LogoutLoaderInteraction, Sales.OnSalesModuleInteraction,
        HistorySales.OnSalesHistory,
        Report.OnReportInteraction,
        ResumeLoader.OnResumeLoader,
        VoucherModule.OnVoucherModule {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.titleName)
    MyLabel title;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    MyLabel userName;
    MyLabel branchName;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;
    private LoginResponse loginResponse;
    private Intent alarmIntent;
    private PendingIntent pendingIntent;
    private AlarmManager alarm;
    private boolean isPaused= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        Bundle bundle = getIntent().getExtras();
        try {
            loginResponse = (LoginResponse) DataFactory.stringToObject(LoginResponse.class, bundle.getString(InterActiveConfig.LOGIN_DATA));
            if(loginResponse == null)
                launchHome();
        }catch (Exception e){
            e.printStackTrace();
            launchHome();
        }
        View header = navigationView.getHeaderView(0);
        userName = (MyLabel) header.findViewById(R.id.userName);
        branchName = (MyLabel) header.findViewById(R.id.branchName);

        userName.setText("Welcome "+loginResponse.getmUser().getName()+"!");
        branchName.setText("Branch: "+loginResponse.getmUser().getBranchName());

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            fragmentHandler(getSalesModule());
            scheduleAlarm();
        }

    }

    private void launchHome(){
        try {
            DbBulk.resetData();
            //MSales.deleteAll(MSales.class);
            cancelAlarm();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1)
                super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            confirm("Do you really want to logout?");
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_home) {
            fragmentHandler(getSalesModule());
        } else if (id == R.id.action_transaction) {
            fragmentHandler(getHistoryModule());
        } else if (id == R.id.action_report) {
            fragmentHandler(getReport());
        } else if (id == R.id.action_pause_session) {
            if(!isPaused){
                ResumeLoader resumeLoader = new ResumeLoader(UserHome.this, UserHome.this, loginResponse);
                resumeLoader.lockDevice();
                isPaused = true;
            }
        } else if (id == R.id.action_end_shift) {
            confirm("Do you really want to logout?");
        } else if (id == R.id.action_voucher) {
            fragmentHandler(getVoucherModule());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
//        if(!isPaused){
//            ResumeLoader resumeLoader = new ResumeLoader(UserHome.this, UserHome.this, loginResponse);
//            resumeLoader.lockDevice();
//            isPaused = true;
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        if(!isPaused){
//            ResumeLoader resumeLoader = new ResumeLoader(UserHome.this, UserHome.this, loginResponse);
//            resumeLoader.lockDevice();
//            isPaused = true;
//        }
    }

    private void popBox(String message){
        try {
            builder = new AlertDialog.Builder(UserHome.this, R.style.SimpleDialog);
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
            Toast.makeText(getApplicationContext(), message != null ? message : "NO_MESSAGE", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirm(String message){
        try {
            int pendingTransactions = postPendingTransaction();
            if(pendingTransactions > 0){
                popBox("Your still have "+pendingTransactions+ " Transaction(s) pending");
                return;
            }
            builder = new AlertDialog.Builder(UserHome.this, R.style.SimpleDialog);
            builder.setMessage(message != null ? message : "NO_MESSAGE")
                    .setTitle(R.string.dialog_title);
            // Add the buttons
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    mProgress("Signing out");
                    LogoutLoader logoutLoader = new LogoutLoader(UserHome.this, getApplicationContext(), new LogoutRequest(Build.SERIAL, loginResponse.getmUser().getUserId()));
                    logoutLoader.startLoading();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                }
            });
            dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), message != null ? message : "NO_MESSAGE", Toast.LENGTH_SHORT).show();
        }
    }

    public void scheduleAlarm() {
        Calendar cal = Calendar.getInstance();
        alarmIntent = new Intent(UserHome.this, TransactionRectifier.class);
        alarmIntent.setAction(TransactionRectifier.POST_TRANSACTIONS);
        pendingIntent = PendingIntent.getService(UserHome.this,
                MyAlarmManager.REQUEST_CODE,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarm = (AlarmManager) UserHome.this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 10 * 1000, pendingIntent);
    }

    public void cancelAlarm(){
        if(alarm != null && pendingIntent != null){
            alarm.cancel(pendingIntent);
        }
    }

    private void mProgress(String message){
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        progressDialog = new ProgressDialog(UserHome.this, R.style.AppTheme_Dark_Dialog);
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

    private void updateMessage(String message){
        if (progressDialog != null)
            if (progressDialog.isShowing())
                progressDialog.setMessage(message != null ? message : "NO_MESSAGE");
    }

    private Sales getSalesModule(){
        return Sales.newInstance(DataFactory.objectToString(loginResponse));
    }

    private VoucherModule getVoucherModule() {
        return VoucherModule.newInstance(DataFactory.objectToString(loginResponse));
    }

    private HistorySales getHistoryModule(){
        return HistorySales.newInstance(DataFactory.objectToString(loginResponse));
    }

    private Report getReport(){
        return Report.newInstance(DataFactory.objectToString(loginResponse));
    }

    private void fragmentHandler(Object object) {
        Fragment fragment = (Fragment) object;
        String backStateName = fragment.getClass().getSimpleName();

        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && fragmentManager.findFragmentByTag(backStateName) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, backStateName);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    private int postPendingTransaction() {
        List<MSales> mSalesList = DbBulk.getPendingTransaction();
        if(mSalesList.isEmpty())
            return 0;
        return mSalesList.size();
    }

    @Override
    public void onLogout(boolean isLoaded, Object object) {
        dismissProgress();
        if(!isLoaded){
            popBox((String) object);
            return;
        }
        DbBulk.deleteOldTransaction();
        launchHome();
    }

    @Override
    public void onSalesModule(boolean isModuleFine, Object object) {
        if(!isModuleFine){
            Toast.makeText(UserHome.this, "Error loading sales module: "+ object, Toast.LENGTH_LONG).show();
            mProgress("Signing out");
            LogoutLoader logoutLoader = new LogoutLoader(UserHome.this, getApplicationContext(), new LogoutRequest(Build.SERIAL, loginResponse.getmUser().getUserId()));
            logoutLoader.startLoading();
        }
    }

    @Override
    public void onSalesHistory(boolean isModuleFine, Object object) {
        if(!isModuleFine)
            fragmentHandler(getSalesModule());
    }

    @Override
    public void onReport(boolean isModuleFine, Object object) {
        if(!isModuleFine)
            fragmentHandler(getSalesModule());
    }

    @Override
    public void onResume(boolean isDone, Object object) {
        if(!isDone)
            popBox((String) object);
        isPaused = false;
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onVoucherModule(boolean isDone, Object object) {
        if (!isDone)
            fragmentHandler(getSalesModule());
    }
}
