package com.aomega08.keephodling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver  {
    private static final String TAG = "HODL_BootReceiver";

    public void onReceive(Context context, Intent intent) {
        try {
            BuyScheduler.setAlarm(context);
        } catch (Exception exc) {

        }
    }
}