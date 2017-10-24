package com.oltranz.pf.n_payfuel;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.IntentCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.oltranz.pf.n_payfuel.config.InterActiveConfig;
import com.oltranz.pf.n_payfuel.fragments.Login;
import com.oltranz.pf.n_payfuel.fragments.Register;
import com.oltranz.pf.n_payfuel.utilities.DataFactory;
import com.oltranz.pf.n_payfuel.utilities.alarm.MyAlarmManager;
import com.oltranz.pf.n_payfuel.utilities.tracker.TrackerService;
import com.oltranz.pf.n_payfuel.utilities.updator.updtservice.UpdateService;
import com.oltranz.pf.n_payfuel.utilities.updator.logic.DeviceProvider;
import com.oltranz.pf.n_payfuel.utilities.updator.logic.DirManager;
import com.oltranz.pf.n_payfuel.utilities.views.MyButton;
import com.oltranz.pf.n_payfuel.utilities.views.MyLabel;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Home extends AppCompatActivity implements Login.OnLoginInteraction, Register.OnRegisterInteraction {
    //views
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.titleName)
    MyLabel title;

    private DirManager dirManager;
    private Dialog dialog;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String action = intent.getAction();
            if(action.equals(UpdateService.ACTION_TRIGGER)){
                Log.d("receiver", "Got action: " + action);
                showWarningDialog();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.home_layout);
        dirManager = new DirManager(Home.this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        //Initiate the tracking service
        //scheduleAlarm();
        //scheduleUpdateAlarm();

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            fragmentHandler(getLoginModule());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            try{
                fragmentHandler(getLoginModule());
            }catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }else if(id == R.id.action_register){
            try{
                fragmentHandler(getRegisterModule());
            }catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1)
            super.onBackPressed();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("Resume", "Application called onResume");
        LocalBroadcastManager.getInstance(Home.this).registerReceiver(
                mMessageReceiver, new IntentFilter(UpdateService.ACTION_TRIGGER));
        scheduleAlarm();
        scheduleUpdateAlarm();
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(Home.this).unregisterReceiver(
                mMessageReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Destroy", "Application called onDestroy");
        cancelAlarm();
        cancelUpdateAlarm();
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

    private Login getLoginModule(){
        return Login.newInstance();
    }

    @Override
    public void loginInteraction(boolean isSuccess, Object object) {
        if(isSuccess){
            //successful login, go to UserHome activity
            String loginData = DataFactory.objectToString(object);
            Bundle bundle = new Bundle();
            bundle.putString(InterActiveConfig.LOGIN_DATA, loginData);

            Intent intent = new Intent(this, UserHome.class);
            intent.putExtras(bundle);
            intent.setFlags(IntentCompat.FLAG_ACTIVITY_TASK_ON_HOME | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }
    }

    @SuppressLint("HardwareIds")
    public void scheduleAlarm() {
        TelephonyManager tManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        Calendar cal = Calendar.getInstance();
        Intent alarmIntent = new Intent(getApplicationContext(), TrackerService.class);
        alarmIntent.setAction(TrackerService.ACTION_PING);
        alarmIntent.putExtra(TrackerService.EXTRA_DEVICE_ID, Build.SERIAL);
        alarmIntent.putExtra(TrackerService.EXTRA_DEVICE_IMEI, tManager.getDeviceId());

        PendingIntent pIntent = PendingIntent.getService(getApplicationContext(),
                MyAlarmManager.REQUEST_CODE,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        cal.add(Calendar.SECOND, 30);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 9 * 1000, pIntent);
    }

    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), TrackerService.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmManager.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }


    public void scheduleUpdateAlarm() {
        Calendar cal = Calendar.getInstance();
        Intent alarmIntent = new Intent(getApplicationContext(), UpdateService.class);
        alarmIntent.setAction(UpdateService.ACTION_CHECK_UPDATES);

        PendingIntent pIntent = PendingIntent.getService(getApplicationContext(),
                MyAlarmManager.REQUEST_CODE,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        cal.add(Calendar.MINUTE, 1);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 9 * 60 * 1000, pIntent);
    }

    public void cancelUpdateAlarm() {
        Intent intent = new Intent(getApplicationContext(), UpdateService.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmManager.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

    private Register getRegisterModule(){
        return Register.newInstance();
    }
    @Override
    public void onRegister(Object object) {

    }

    private void showWarningDialog(){
        if(dialog != null && dialog.isShowing())
            return;
        try {
            dialog = new Dialog(Home.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.update_alert);

            final MyLabel boxTitle = (MyLabel) dialog.findViewById(R.id.dialogTitle);
            MyButton ok = (MyButton) dialog.findViewById(R.id.ok);
            LinearLayout boxContent = (LinearLayout) dialog.findViewById(R.id.dialogContent);
            MyLabel warning = (MyLabel) dialog.findViewById(R.id.warning);

            warning.setText("This application has new update that require to be installed. Kindly click Okay and proceed with the installation.");

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //proceed with other validations
                    try {
                        triggerUpdate();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            });

            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
            try {
                triggerUpdate();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void triggerUpdate() throws IOException {
        try{
            File updateFile = dirManager.getUpdateFile(new DeviceProvider(Home.this).genDevice());
            if(updateFile.length() <= 0){
                Log.d("LocalFile", "Bad update file");
                return;
            }
            double kilobytes = (updateFile.length() / 1024);
            double megabytes = (kilobytes / 1024);
            Log.d("Updater", "Updating file with: "+megabytes+"Mbs from path: "+updateFile.getAbsolutePath());

            try {
                if(!dirManager.isUpdateRequired(new DeviceProvider(Home.this).genDevice())){
                    Log.d("Updater", "No update required");
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(updateFile), "application/vnd.android.package-archive");
            startActivity(intent);
            //finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
