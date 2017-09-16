package com.aomega08.keephodling;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

class GdaxApi {
    private static final String BASE_URI = "https://api.gdax.com";
    private static final String SANDBOX_URI= "https://api-public.sandbox.gdax.com";

    private RequestQueue requestQueue;
    private Preferences preferences;

    GdaxApi(Context context) {
        preferences = new Preferences(context);
        requestQueue = Volley.newRequestQueue(context);
    }

    interface Listener {
        void onSuccess(JsonNode response);
        void onFailure(String message);
    }

    private void request(int intMethod, String stringMethod, String endpoint, boolean signed, final String body, Listener l) {
        final Map<String, String> headers = new HashMap<>();
        if (signed)
            addSignatureHeaders(headers, stringMethod, endpoint, body);

        if (body != null)
            headers.put("Content-Type", "application/json");

        StringRequest request = new StringRequest(intMethod, getBaseEndpoint() + endpoint, getResponseListener(l), getErrorListener(l)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                if (body != null)
                    return body.getBytes();
                else
                    return null;
            }
        };

        requestQueue.add(request);
    }

    private void get(final String endpoint, final boolean signed, Listener l) {
        request(Request.Method.GET, "GET", endpoint, signed, null, l);
    }

    private void post(final String endpoint, final boolean signed, HashMap<String, String> params, final Listener l) {
        request(Request.Method.POST, "POST", endpoint, signed, jsonBody(params), l);
    }

    void get(String endpoint, Listener l) {
        get(endpoint, false, l);
    }

    void getAccounts(Listener l) {
        get("/accounts", true, l);
    }

    void buy(double funds, Listener l) {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "market");
        params.put("side", "buy");
        params.put("product_id", preferences.getCurrencyPair());
        params.put("funds", "" + funds);

        post("/orders", true, params, l);
    }

    private void addSignatureHeaders(Map<String, String> headers, String method, String endpoint, String body) {
        String timestamp = "" + System.currentTimeMillis() / 1000;
        String toSign = timestamp + method + endpoint + body;

        headers.put("CB-ACCESS-TIMESTAMP", timestamp);
        headers.put("CB-ACCESS-KEY", preferences.getApiKey());
        headers.put("CB-ACCESS-PASSPHRASE", preferences.getApiPassword());

        try {
            String signature = sign(toSign, preferences.getApiSecret());
            headers.put("CB-ACCESS-SIGN", signature);
        } catch (java.security.InvalidKeyException e) {
            Log.e("SIGN", "Sign failed due to invalid key");
        }
    }

    private String sign(String message, String b64Key) throws java.security.InvalidKeyException {
        byte[] secret = Base64.decode(b64Key, Base64.DEFAULT);

        try {
            Mac sha_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret, "HmacSHA256");
            sha_HMAC.init(secret_key);

            return Base64.encodeToString(sha_HMAC.doFinal(message.getBytes()), Base64.DEFAULT);
        } catch (java.security.NoSuchAlgorithmException e) {
            Log.e("SIGN", e.getMessage());
            return "";
        }
    }

    private String getBaseEndpoint() {
        if (preferences.isSandboxEnabled()) {
            return SANDBOX_URI;
        } else {
            return BASE_URI;
        }
    }

    private String jsonBody(HashMap<String, String> params) {
        if (params != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(params);
            } catch (IOException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    private Response.Listener<String> getResponseListener(final Listener l) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode json = mapper.readTree(response);
                    l.onSuccess(json);
                } catch (IOException e) {
                    l.onFailure("Invalid JSON: " + response);
                }
            }
        };
    }

    private Response.ErrorListener getErrorListener(final Listener l) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode == 401 || error.networkResponse.statusCode == 403) {
                    l.onFailure("Invalid credentials or not enough permissions");
                } else if (error.networkResponse.statusCode == 400) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        JsonNode json = mapper.readTree(new String(error.networkResponse.data));
                        l.onFailure("Error: " + json.get("message").asText());
                    } catch (IOException e) {
                        l.onFailure(new String(error.networkResponse.data));
                    }
                } else {
                    l.onFailure(error.getMessage());
                }
            }
        };
    }
}
