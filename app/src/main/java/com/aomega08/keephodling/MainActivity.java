package com.aomega08.keephodling;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {
    Preferences preferences;
    GdaxApi gdaxApi;
    FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = new Preferences(getApplicationContext());
        gdaxApi = new GdaxApi(getApplicationContext());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

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
