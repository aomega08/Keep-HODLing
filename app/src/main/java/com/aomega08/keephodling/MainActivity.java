package com.aomega08.keephodling;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GdaxApi.init(this);

        if (arePreferencesValid()) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new DashboardFragment())
                    .commit();
        } else {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new ConfigureMeFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment current = getFragmentManager().findFragmentById(android.R.id.content);

        if (current instanceof SettingsFragment) {
            if (arePreferencesValid()) {
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new DashboardFragment())
                        .commit();
            } else {
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new ConfigureMeFragment())
                        .commit();
            }
        } else {
            super.onBackPressed();
        }
    }

    boolean arePreferencesValid() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String apiKey = sharedPref.getString("preference_api_key", "");
        String apiSecret = sharedPref.getString("preference_api_secret", "");
        String apiPassword = sharedPref.getString("preference_api_password", "");

        return !apiKey.equals("") && !apiSecret.equals("") && !apiPassword.equals("");
    }
}
