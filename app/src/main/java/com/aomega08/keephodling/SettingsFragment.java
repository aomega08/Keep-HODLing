package com.aomega08.keephodling;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences.OnSharedPreferenceChangeListener spChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                // If the buy schedule has changed, cancel and recreate the alarms.
                if (key.equals("preference_frequency")) {
                    try {
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
}