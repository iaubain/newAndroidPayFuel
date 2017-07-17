package com.oltranz.pf.n_payfuel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.oltranz.pf.n_payfuel.config.InterActiveConfig;
import com.oltranz.pf.n_payfuel.fragments.Login;
import com.oltranz.pf.n_payfuel.fragments.Register;
import com.oltranz.pf.n_payfuel.utilities.DataFactory;
import com.oltranz.pf.n_payfuel.utilities.views.MyLabel;

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

    private Register getRegisterModule(){
        return Register.newInstance();
    }
    @Override
    public void onRegister(Object object) {

    }
}
