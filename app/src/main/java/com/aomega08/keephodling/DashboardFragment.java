package com.aomega08.keephodling;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.fasterxml.jackson.databind.JsonNode;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DashboardFragment extends GenericFragment implements FillCumulator.Listener {
    GdaxApi gdaxApi;
    Preferences preferences;
    Persistence persistence;
    TextView currentPrice;
    TextView ownedAmount;
    TextView ownedValue;
    TextView ownedValueBase;
    TextView percentValue;
    ToggleButton toggleAutobuy;

    double currentPriceDbl = 0.0;
    double ownedAmountDbl = 0.0;
    double baseAmountDbl = 0.0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = new Preferences(getActivity().getApplicationContext());
        persistence = new Persistence(getActivity().getApplicationContext());
        gdaxApi = new GdaxApi(getActivity().getApplicationContext());

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Keep HODLing");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dashboard_fragment, container, false);

        currentPrice = rootView.findViewById(R.id.current_price);
        ownedAmount = rootView.findViewById(R.id.owned_amount);
        ownedValue = rootView.findViewById(R.id.owned_value);
        ownedValueBase = rootView.findViewById(R.id.owned_value_base);
        percentValue = rootView.findViewById(R.id.percent_value);

        toggleAutobuy = rootView.findViewById(R.id.toggle_autobuy);
        toggleAutobuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistence.setAutobuyEnabled(toggleAutobuy.isChecked());
                try {
                    BuyScheduler.setAlarm(getActivity().getApplicationContext());
                } catch (Exception e) {
                    //
                }
            }
        });

        toggleAutobuy.setChecked(persistence.getAutobuyEnabled());

        return rootView;
    }

    @Override
    public void onResume() {
        refreshValues();
        (new BuyEvent()).onReceive(getActivity().getApplicationContext(), null);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Keep HODLing");

        super.onResume();
    }

    String getAmountForCurrency(JsonNode balances, String currency, int decimalPlaces) {
        for (JsonNode cur : balances) {
            if (cur.get("currency").asText().equals(currency)) {
                double balance = Double.parseDouble(cur.get("balance").asText());
                NumberFormat formatter = new DecimalFormat("#0.00");
                formatter.setMinimumFractionDigits(decimalPlaces);
                return formatter.format(balance);
            }
        }

        return "---.--";
    }

    void updateValues() {
        double effectiveValue = currentPriceDbl * ownedAmountDbl;

        NumberFormat formatter = new DecimalFormat("#0.00");

        ownedValue.setText(formatter.format(effectiveValue) + " " + preferences.getBaseCurrency());

        if (effectiveValue > 0.0 && persistence.getSpentAmount() > 0.0) {
            double spent = persistence.getSpentAmount();
            double diff = effectiveValue - spent;
            double percent = (diff / spent) * 100.0;

            if (percent >= 0.01) {
                percentValue.setText("▲ " + formatter.format(percent) + " %");
                percentValue.setTextColor(getResources().getColor(R.color.money));
            } else if (percent <= -0.01) {
                percentValue.setText("▼ " + formatter.format(percent) + " %");
                percentValue.setTextColor(getResources().getColor(R.color.red));
            } else {
                percentValue.setText("~ 0.00 %");
                percentValue.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
            }
        } else {
            percentValue.setText("~ 0.00 %");
            percentValue.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
        }
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
                String ownedBase = getAmountForCurrency(response, preferences.getBaseCurrency(), 2);

                NumberFormat formatter = new DecimalFormat("#0.00");

                ownedAmountDbl = Double.parseDouble(owned);
                baseAmountDbl = Double.parseDouble(ownedBase);
                ownedAmount.setText(owned + " " + preferences.getCryptoCurrency());
                ownedValueBase.setText(formatter.format(baseAmountDbl) + " " + preferences.getBaseCurrency());

                updateValues();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        (new FillCumulator(getActivity().getApplicationContext())).run(this);
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

    @Override
    public void onDone() {
        updateValues();
    }
}
