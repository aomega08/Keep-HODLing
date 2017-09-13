package com.aomega08.keephodling;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class GdaxApi {
    static RequestQueue requestQueue;
    static final String BASE_URI = "https://api.gdax.com";

    static void init(Activity activity) {
        requestQueue = Volley.newRequestQueue(activity.getApplicationContext());
    }

    static void get(String endpoint, final Listener l) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, BASE_URI + endpoint, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                l.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                l.onFailure(error.getMessage());
            }
        });

        requestQueue.add(request);
    }

    interface Listener {
        void onSuccess(JSONObject response);
        void onFailure(String message);
    }
}
