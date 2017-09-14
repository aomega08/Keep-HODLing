package com.aomega08.keephodling;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = new Preferences(this.getApplicationContext());
        GdaxApi.init(this);

        try {
            BuyScheduler.setAlarm(getApplicationContext());
        } catch (Exception exc) {
        }

        if (preferences.arePreferencesValid()) {
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
            if (preferences.arePreferencesValid()) {
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
}
