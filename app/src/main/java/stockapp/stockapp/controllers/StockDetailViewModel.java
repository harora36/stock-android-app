package stockapp.stockapp.controllers;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.BehaviorSubject;
import stockapp.stockapp.common.ApiRequestHandler;
import stockapp.stockapp.common.FavoriteUtils;
import stockapp.stockapp.common.Utils;
import stockapp.stockapp.models.StockItem;

/**
 * Created by heenaarora on 11/25/17.
 */

public class StockDetailViewModel {
    public static String TAG = NewsViewModel.class.getSimpleName();
    public static String REQUEST_TAG = "news.response";

    private ApiRequestHandler apiRequestHandler;
    private BehaviorSubject<StockItem> mStocksSubject = BehaviorSubject.create();
    private BehaviorSubject<Boolean> mIsLoadingSubject = BehaviorSubject.create(false);
    private Observable<Boolean> mIsLoadingObservale =  mIsLoadingSubject.asObservable();
    private Observable<StockItem> mStocksObservable =  mStocksSubject.asObservable();

    public StockDetailViewModel(Context context) {
        apiRequestHandler = ApiRequestHandler.getInstance(context);
    }


    public void requestStockData(String mStockSymbol) {
        if (mIsLoadingSubject.getValue()) {
            return;
        }
        String url = Utils.getStockDetailsUrl(mStockSymbol);
        mIsLoadingSubject.onNext(true);
        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            StockItem stockItem = new StockItem();
                            JSONObject meta = response.getJSONObject("Meta Data");
                            stockItem.setStockSymbol(meta.getString("2. Symbol"));

                            JSONObject data = response.getJSONObject("Time Series (Daily)");
                            String key = data.keys().next();
                            stockItem.setTimeStamp(key);
                            JSONObject item = data.getJSONObject(key);
                            stockItem.setOpen(item.getDouble("1. open"));
                            stockItem.setHigh(item.getDouble("2. high"));
                            stockItem.setLow(item.getDouble("3. low"));
                            stockItem.setClose(item.getDouble("4. close"));
                            stockItem.setVolume(item.getString("5. volume"));
                            mStocksSubject.onNext(stockItem);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mStocksSubject.onNext(null);
                        }
                        mIsLoadingSubject.onNext(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mStocksSubject.onNext(null);
                mIsLoadingSubject.onNext(false);
            }
        });

        apiRequestHandler.addToRequestQueue(jsonObjectReq, REQUEST_TAG);
    }


    public Observable<StockItem> responseObservable() {
        return mStocksObservable;
    }

    public Observable<Boolean> isLoadingObservable() {
        return mIsLoadingObservale;
    }
}
