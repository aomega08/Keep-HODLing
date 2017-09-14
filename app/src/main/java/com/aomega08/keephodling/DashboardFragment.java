package com.aomega08.keephodling;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardFragment extends GenericFragment {
    TextView currentPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dashboard_fragment, container, false);

        currentPrice = rootView.findViewById(R.id.current_price);

        return rootView;
    }

    @Override
    public void onResume() {
        GdaxApi.get("/products/BTC-EUR/book", new GdaxApi.Listener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String askPrice = response.getJSONArray("asks").getJSONArray(0).getString(0);
                    currentPrice.setText(askPrice);
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(String message) {

            }
        });

        super.onResume();
    }
}
