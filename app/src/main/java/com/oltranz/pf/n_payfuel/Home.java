package com.oltranz.pf.n_payfuel;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.oltranz.pf.n_payfuel.config.InterActiveConfig;
import com.oltranz.pf.n_payfuel.fragments.Login;
import com.oltranz.pf.n_payfuel.fragments.Register;
import com.oltranz.pf.n_payfuel.utilities.DataFactory;
import com.oltranz.pf.n_payfuel.utilities.alarm.MyAlarmManager;
import com.oltranz.pf.n_payfuel.utilities.tracker.TrackerService;
import com.oltranz.pf.n_payfuel.utilities.views.MyLabel;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Home extends AppCompatActivity implements Login.OnLoginInteraction, Register.OnRegisterInteraction {
    //views
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.titleName)
    MyLabel title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.home_layout);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        //Initiate the tracking service
        scheduleAlarm();

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
        scheduleAlarm();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Destroy", "Application called onDestroy");
        cancelAlarm();
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
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 9 * 1000, pIntent);
    }

    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), TrackerService.class);
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
}
