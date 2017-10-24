package com.oltranz.pf.n_payfuel.utilities.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.oltranz.pf.n_payfuel.Home;
import com.oltranz.pf.n_payfuel.utilities.updator.logic.DirManager;

public class AutoStart extends BroadcastReceiver {
    public AutoStart() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent i = new Intent(context, Home.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        try{
            DirManager dirManager = new DirManager(context);
            boolean isDeleted = dirManager.deleteJunk();
            if(isDeleted)
                Log.d("DeleteJunk", "Junk file are deleted");
            else
                Log.d("DeleteJunk", "Junk file failed to be deleted");

        }catch (Exception e){
            e.printStackTrace();
            Log.d("DeleteJunk", "Error deleting junk: "+e.getMessage());
        }

        if (intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            Log.d("Restart", "my package: "+context.getApplicationInfo().packageName+" Sending app: "+intent.getData().getSchemeSpecificPart());
            restartTheApp(context);
        }
    }

    private void restartTheApp(Context context){
        Log.w("Restart", "Application is restarting due to change in package. "+context.getPackageName());
        Intent mStartActivity = new Intent(context, Home.class);
        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }
}
