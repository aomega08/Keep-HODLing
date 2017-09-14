package com.aomega08.keephodling;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

class Preferences {
    SharedPreferences prefs;

    public Preferences(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    boolean arePreferencesValid() {
        String apiKey = prefs.getString("preference_api_key", "");
        String apiSecret = prefs.getString("preference_api_secret", "");
        String apiPassword = prefs.getString("preference_api_password", "");

        return !apiKey.equals("") && !apiSecret.equals("") && !apiPassword.equals("");
    }

    String getCryptoCurrency() {
        return prefs.getString("preference_cryptocurrency", "");
    }

    String getBaseCurrency() {
        return prefs.getString("preference_basecurrency", "");
    }

    String getFrequency() {
        return prefs.getString("preference_frequency", "");
    }

    double getAmount() {
        return Double.parseDouble(prefs.getString("preference_amount", ""));
    }
}
