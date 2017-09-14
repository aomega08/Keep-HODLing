package com.aomega08.keephodling;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class BuyEvent extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        notify(context);
        new Persistence(context).setLastBuyTime(System.currentTimeMillis());
    }

    public void notify(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Preferences p = new Preferences(context);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                                                    .setContentTitle("Bought some " + p.getCryptoCurrency())
                                                    .setSmallIcon(android.R.drawable.ic_menu_call)
                                                    .setContentText("Spent " + spendAmount(context) + " " + p.getBaseCurrency());

        mNotificationManager.notify((int) (Math.random() * 512), mBuilder.build());
    }

    public double spendAmount(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        String period = sharedPref.getString("preference_frequency", "");
        double monthAmount = Double.parseDouble(sharedPref.getString("preference_amount", ""));

        switch (period) {
            case "hourly":
                return monthAmount / 30.0 / 24.0;
            case "daily":
                return monthAmount / 30.0;
            case "weekly":
                return monthAmount / 4.33333;
            case "monthly":
                return monthAmount;
            default:
                return 0.0;
        }
    }
}