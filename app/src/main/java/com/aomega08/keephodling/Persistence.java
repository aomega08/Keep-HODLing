package com.aomega08.keephodling;

import android.content.Context;
import android.content.SharedPreferences;

class Persistence {
    SharedPreferences preferences;

    Persistence(Context context) {
        preferences = context.getSharedPreferences("HODL_PREFS", 0);
    }

    long getLastBuyTime() {
        return preferences.getLong("lastBuyTime", 0);
    }

    void setLastBuyTime(long millis) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("lastBuyTime", millis);
        editor.apply();
    }

    boolean getAutobuyEnabled() {
        return preferences.getBoolean("autobuy", false);
    }

    void setAutobuyEnabled(boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("autobuy", value);
        editor.apply();
    }

    double getSpentAmount() {
        return Double.parseDouble(preferences.getString("spentAmount", "0.0"));
    }

    void setSpentAmount(double amount) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("spentAmount", "" + amount);
        editor.apply();
    }

    void addSpentAmount(double diff) {
        setSpentAmount(getSpentAmount() + diff);
    }

    String getLastCumulatedFill() {
        return preferences.getString("lastCumulatedFill", null);
    }

    void setLastCumulatedFill(String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("lastCumulatedFill", value);
        editor.apply();
    }
}
