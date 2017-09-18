package com.aomega08.keephodling;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

class FillCumulator implements GdaxApi.Listener {
    GdaxApi gdaxApi;
    Persistence persistence;
    Preferences preferences;
    String currentLowerLimit;
    Listener listener;

    FillCumulator(Context context) {
        gdaxApi = new GdaxApi(context);
        persistence = new Persistence(context);
        preferences = new Preferences(context);

        currentLowerLimit = persistence.getLastCumulatedFill();
    }

    interface Listener {
        void onDone();
    }

    void run(Listener l) {
        listener = l;
        gdaxApi.getFills(preferences.getCurrencyPair(), currentLowerLimit, null, this);
    }

    @Override
    public void onSuccess(JsonNode response) {
        double count = 0.0;

        for (JsonNode item : response) {
            double amount = item.get("price").asDouble() * item.get("size").asDouble() + item.get("fee").asDouble();

            if (item.get("side").asText().equals("buy")) {
                count += amount;
            } else {
                count -= amount;
            }
        }

        persistence.addSpentAmount(count);

        if (response.size() > 0) {
            int firstTradeId = Integer.parseInt(response.get(0).get("trade_id").asText());
            String lastCumulatedFill = persistence.getLastCumulatedFill();
            if (lastCumulatedFill == null || firstTradeId > Integer.parseInt(lastCumulatedFill))
                persistence.setLastCumulatedFill("" + firstTradeId);

            String lastTradeId = response.get(response.size() - 1).get("trade_id").asText();
            gdaxApi.getFills(preferences.getCurrencyPair(), currentLowerLimit, lastTradeId, this);
        } else {
            listener.onDone();
        }
    }

    @Override
    public void onFailure(String message) {

    }
}
