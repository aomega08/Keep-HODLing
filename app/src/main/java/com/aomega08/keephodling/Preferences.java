package com.aomega08.keephodling;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

class Preferences {
    public SharedPreferences prefs;

    Preferences(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    boolean arePreferencesValid() {
        return !getApiKey().equals("") && !getApiSecret().equals("") && !getApiPassword().equals("") &&
                (!getBaseCurrency().equals("GBP") || getCryptoCurrency().equals("BTC"));
    }

    String getApiKey() {
        return prefs.getString("preference_api_key", "");
    }

    String getApiSecret() {
        return prefs.getString("preference_api_secret", "");
    }

    String getApiPassword() {
        return prefs.getString("preference_api_password", "");
    }

    String getCryptoCurrency() {
        return prefs.getString("preference_cryptocurrency", "");
    }

    String getBaseCurrency() {
        return prefs.getString("preference_basecurrency", "");
    }

    String getCurrencyPair() {
        return getCryptoCurrency() + "-" + getBaseCurrency();
    }

    String getFrequency() {
        return prefs.getString("preference_frequency", "");
    }

    double getAmount() {
        return Double.parseDouble(prefs.getString("preference_amount", ""));
    }

    boolean isSandboxEnabled() {
        return prefs.getBoolean("preference_sandbox", false);
    }
}
