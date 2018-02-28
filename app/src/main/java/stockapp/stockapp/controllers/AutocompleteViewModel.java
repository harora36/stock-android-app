package stockapp.stockapp.controllers;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import stockapp.stockapp.common.ApiRequestHandler;
import stockapp.stockapp.interfaces.IRequestResponseHandler;
import stockapp.stockapp.models.StockSymbol;

/**
 * Created by heenaarora on 11/22/17.
 */

public class AutocompleteViewModel {
    public static String TAG = AutocompleteViewModel.class.getSimpleName();
    public static String REQUEST_TAG = "autocomplete.response";

    private IRequestResponseHandler mHandler;

    public AutocompleteViewModel(IRequestResponseHandler handler) {
        mHandler = handler;
    }


    public void symbolsRequest(String url, Context context) {
        ApiRequestHandler.getInstance(context).cancelPendingRequests(TAG);
        Log.d(TAG, url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0) {
                            mHandler.onError();
                        }
                        ArrayList<StockSymbol> symbols = new ArrayList<>();
                        int length = response.length();
                        if (length > 5) {
                            length = 5;
                        }
                        try {
                            for (int i = 0; i < length; i++) {
                                JSONObject student = response.getJSONObject(i);
                                String symbol = student.getString("Symbol");
                                String name = student.getString("Name");
                                String exchange = student.getString("Exchange");
                                symbols.add(new StockSymbol(symbol, name,exchange));
                                mHandler.OnResponse(symbols);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mHandler.onError();
                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mHandler.onError();
                        Log.d(TAG, "onerrorresponse " + error.toString());
                    }
                }
        );
        ApiRequestHandler.getInstance(context).addToRequestQueue(jsonArrayRequest, REQUEST_TAG);
    }

    public void cancelOngoingRequest(Context context) {
        ApiRequestHandler.getInstance(context).cancelPendingRequests(REQUEST_TAG);
    }

}
