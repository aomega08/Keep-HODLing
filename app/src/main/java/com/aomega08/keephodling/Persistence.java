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
}
