package com.aomega08.keephodling;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;

public class DashboardFragment extends GenericFragment {
    GdaxApi gdaxApi;
    Preferences preferences;
    TextView currentPrice;
    TextView ownedAmount;
    TextView ownedValue;
    TextView ownedValueBase;

    double currentPriceDbl = 0.0;
    double ownedAmountDbl = 0.0;
    double baseAmountDbl = 0.0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences((getActivity().getApplicationContext()));
        gdaxApi = new GdaxApi(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dashboard_fragment, container, false);

        currentPrice = rootView.findViewById(R.id.current_price);
        ownedAmount = rootView.findViewById(R.id.owned_amount);
        ownedValue = rootView.findViewById(R.id.owned_value);
        ownedValueBase = rootView.findViewById(R.id.owned_value_base);
        Button clickButton = (Button) rootView.findViewById(R.id.buy_button);
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gdaxApi.buy(10.0, new GdaxApi.Listener() {
                    @Override
                    public void onSuccess(JsonNode response) {
                        Log.i("BOUGHT", response.toString());
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        refreshValues();

        super.onResume();
    }

    String getAmountForCurrency(JsonNode balances, String currency, int decimalPlaces) {
        for (JsonNode cur : balances) {
            if (cur.get("currency").asText().equals(currency)) {
                String balance = cur.get("balance").asText();
                String[] parts = balance.split("\\.");
                return parts[0] + "." + parts[1].substring(0, decimalPlaces);
            }
        }

        return "---.--";
    }

    void updateValues() {
        double effectiveValue = currentPriceDbl * ownedAmountDbl;

        NumberFormat formatter = new DecimalFormat("#0.00");

        ownedValue.setText(formatter.format(effectiveValue) + " " + preferences.getBaseCurrency());
    }

    private void refreshValues() {
        gdaxApi.get("/products/" + preferences.getCurrencyPair() + "/book", new GdaxApi.Listener() {
            @Override
            public void onSuccess(JsonNode response) {
                String price;
                if (response.get("asks").size() > 0)
                    price = response.get("asks").get(0).get(0).asText();
                else
                    price = response.get("bids").get(0).get(0).asText();

                currentPriceDbl = Double.parseDouble(price);
                currentPrice.setText(preferences.getCryptoCurrency() + " price: " + price + " " + preferences.getBaseCurrency());
                updateValues();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        gdaxApi.getAccounts(new GdaxApi.Listener() {
            @Override
            public void onSuccess(JsonNode response) {
                String owned = getAmountForCurrency(response, preferences.getCryptoCurrency(), 8);
                String ownedBase = getAmountForCurrency(response, preferences.getBaseCurrency(), 8);

                NumberFormat formatter = new DecimalFormat("#0.00");

                ownedAmountDbl = Double.parseDouble(owned);
                baseAmountDbl = Math.round(Double.parseDouble(ownedBase) * 100) / 100;
                ownedAmount.setText(owned + " " + preferences.getCryptoCurrency());
                ownedValueBase.setText(formatter.format(baseAmountDbl) + " " + preferences.getBaseCurrency());

                updateValues();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    int getMenu() {
        return R.menu.dashboard_menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshValues();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
