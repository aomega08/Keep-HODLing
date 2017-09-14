package com.aomega08.keephodling;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.Calendar;
import android.preference.PreferenceManager;

class BuyScheduler {
    static void setAlarm(Context context) throws Exception {
        if (new Preferences(context).arePreferencesValid()) {
            AlarmManager alarManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            PendingIntent intent = getAlarmIntent(context);

            alarManager.cancel(intent);
            alarManager.setRepeating(AlarmManager.RTC_WAKEUP, getFirstTrigger(context), getPeriod(context), intent);
        }
    }

    private static PendingIntent getAlarmIntent(Context context) {
        Intent i = new Intent(context, BuyEvent.class);
        return PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static int getPeriod(Context context) throws Exception {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        String period = sharedPref.getString("preference_frequency", "");

        switch (period) {
            case "hourly":
                return 3600 * 1000;
            case "daily":
                return 24 * 3600 * 1000;
            case "weekly":
                return 7 * 24 * 3600 * 1000;
            case "monthly":
                return (int) (30.333331 * 24 * 3600 * 1000);
            default:
                throw new Exception("Invalid period");
        }
    }

    private static long getFirstTrigger(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String period = sharedPref.getString("preference_frequency", "");
        Calendar calendar = Calendar.getInstance();

        long lastBuyTime = new Persistence(context).getLastBuyTime();

        if (lastBuyTime > 0) {
            calendar.setTimeInMillis(lastBuyTime);
            switch (period) {
                case "hourly":
                    calendar.add(Calendar.HOUR, 1);
                    break;
                case "daily":
                    calendar.add(Calendar.DATE, 1);
                    break;
                case "monthly":
                    calendar.add(Calendar.MONTH, 1);
                    break;
                case "weekly":
                    calendar.add(Calendar.DATE, 7);
                    break;
            }
        }
        else {
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            switch (period) {
                case "hourly":
                    calendar.add(Calendar.HOUR, 1);
                    break;
                case "daily":
                case "monthly":
                    calendar.set(Calendar.HOUR, 12);
                    break;
                case "weekly":
                    calendar.set(Calendar.HOUR, 12);
                    while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
                        calendar.add(Calendar.DATE, 1);
                    break;
            }
        }

        return calendar.getTimeInMillis();
    }
}
