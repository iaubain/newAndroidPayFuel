package com.oltranz.pf.n_payfuel.utilities.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.oltranz.pf.n_payfuel.Home;

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
    }
}
