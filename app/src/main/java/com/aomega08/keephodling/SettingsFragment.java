package com.aomega08.keephodling;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SettingsFragment extends PreferenceFragment {
    Persistence persistence;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        persistence = new Persistence(getActivity().getApplicationContext());

        SharedPreferences.OnSharedPreferenceChangeListener spChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                // If the buy schedule has changed, cancel and recreate the alarms.
                if (key.equals("preference_frequency")) {
                    try {
                        persistence.setLastBuyTime(0);
                        BuyScheduler.setAlarm(getActivity().getApplicationContext());
                    } catch (Exception e) {
                        //
                    }
                }
            }
        };

        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).registerOnSharedPreferenceChangeListener(spChanged);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Settings");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Settings");
        }
    }
}