package com.aomega08.keephodling;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ConfigureMeFragment extends GenericFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.configure_me_fragment, container, false);

        Button gotoGdax = rootView.findViewById(R.id.goto_gdax_button);
        Button openSettings = rootView.findViewById(R.id.open_settings_button);

        gotoGdax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent websiteBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gdax.com/"));
                startActivity(websiteBrowserIntent);
            }
        });

        openSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new SettingsFragment())
                        .commit();
            }
        });

        return rootView;
    }
}
