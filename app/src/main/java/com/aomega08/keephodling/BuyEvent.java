package com.aomega08.keephodling;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class BuyEvent extends BroadcastReceiver {
    Persistence persistence;
    Preferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        persistence = new Persistence(context);
        preferences = new Preferences(context);

        try {
            assertTimePassed();

            notify(context);
            persistence.setLastBuyTime(System.currentTimeMillis());
        } catch (Exception e) {
            Log.e("BUY", e.getMessage());
        }
    }

    public void notify(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Preferences p = new Preferences(context);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                                                    .setContentTitle("Bought some " + p.getCryptoCurrency())
                                                    .setSmallIcon(android.R.drawable.ic_menu_call)
                                                    .setContentText("Spent " + spendAmount() + " " + p.getBaseCurrency());

        mNotificationManager.notify((int) (Math.random() * 512), mBuilder.build());
    }

    public double spendAmount() {
        String period = preferences.getFrequency();
        double monthAmount = preferences.getAmount();

        double amount;

        switch (period) {
            case "hourly":
                amount = monthAmount / 30.0 / 24.0;
                break;
            case "daily":
                amount = monthAmount / 30.0;
                break;
            case "weekly":
                amount = monthAmount / 4.33333;
                break;
            case "monthly":
                return monthAmount;
            default:
                return 0.0;
        }

        return Math.floor(amount * 100) / 100;
    }

    private void assertTimePassed() throws Exception {
        long lastTime = persistence.getLastBuyTime();
        long diff;

        switch (preferences.getFrequency()) {
            case "hourly":
                diff = 3600 * 1000;
                break;
            case "daily":
                diff = 3600 * 24 * 1000;
                break;
            case "weekly":
                diff = 3600 * 24 * 7 * 1000;
                break;
            case "monthly":
                diff = 3600 * 24 * 30 * 1000;
                break;
            default:
                throw new Exception("Invalid frequency");
        }

        // Allow up to 5 minutes of drift
        if (System.currentTimeMillis() - lastTime < diff - 5 * 60 * 1000) {
            throw new Exception("Assertion failed. Not enough time passed since last operation");
        }
    }
}