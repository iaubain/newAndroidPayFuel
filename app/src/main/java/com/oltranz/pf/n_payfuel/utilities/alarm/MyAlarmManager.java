package com.oltranz.pf.n_payfuel.utilities.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.oltranz.pf.n_payfuel.utilities.services.TransactionRectifier;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/21/2017.
 */

public class MyAlarmManager extends BroadcastReceiver {
    public static final int REQUEST_CODE = 999;
    public MyAlarmManager() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, TransactionRectifier.class);
        i.setAction(TransactionRectifier.POST_TRANSACTIONS);
        context.startService(i);
    }
}
