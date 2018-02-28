package stockapp.stockapp.controllers;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import stockapp.stockapp.common.ApiRequestHandler;
import stockapp.stockapp.common.FavoriteUtils;
import stockapp.stockapp.common.Utils;
import stockapp.stockapp.models.StockItem;

/**
 * Created by heenaarora on 11/25/17.
 */

public class FavoriteViewModel {
    public static String TAG = NewsViewModel.class.getSimpleName();
    public static String REQUEST_TAG = "favorites.response";

    private ApiRequestHandler apiRequestHandler;
    private BehaviorSubject<Boolean> mIsLoadingSubject = BehaviorSubject.create(false);
    private Observable<Boolean> mIsLoadingObservale = mIsLoadingSubject.asObservable();
    private Subscription mAutosubscription;

    public FavoriteViewModel(Context context) {
        apiRequestHandler = ApiRequestHandler.getInstance(context);
    }


    public Observable<JSONObject> requestStockData(String mStockSymbol) {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        Response.Listener<JSONObject> listener = future;
        String url = Utils.getStockDetailsUrl(mStockSymbol);
        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mIsLoadingSubject.onNext(false);
            }
        });

        apiRequestHandler.addToRequestQueue(jsonObjectReq, REQUEST_TAG);
        return Observable.from(future, Schedulers.io());
    }

    private Observable<List<StockItem>> getStocks(final Context context) {
        Observable<List<StockItem>> observable = Observable.from(
                FavoriteUtils.getInstance().getFavorites(context))
                .concatMap(new Func1<StockItem, Observable<StockItem>>() {
                    @Override
                    public Observable<StockItem> call(final StockItem item) {
                        return Observable.zip(Observable.just(item),
                                requestStockData(item.getStockSymbol()),
                                new Func2<StockItem, JSONObject, StockItem>() {
                                    @Override
                                    public StockItem call(StockItem item1, JSONObject response) {
                                        StockItem stockItem = new StockItem();
                                        JSONObject meta = null;
                                        try {
                                            meta = response.getJSONObject("Meta Data");
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
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        item1.setOpen(stockItem.getOpen());
                                        item1.setClose(stockItem.getClose());
                                        return item1;
                                    }
                                });
                    }
                }).toList();
        return observable;
    }

    public void refreshStockData(final Context context) {
        if (mIsLoadingSubject.getValue()) {
            return;
        }
        mIsLoadingSubject.onNext(true);
        getStocks(context)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<StockItem>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<StockItem> stockItems) {
                        ArrayList<StockItem> response = new ArrayList<>();
                        response.addAll(stockItems);
                        FavoriteUtils.getInstance().updateFavorites(context, response);
                        mIsLoadingSubject.onNext(false);
                    }
                });
    }

    public Observable<Boolean> isLoadingObservable() {
        return mIsLoadingObservale;
    }

    public void autoRefresh(final Context context) {
        if (mAutosubscription != null && !mAutosubscription.isUnsubscribed()) {
            return;
        }
        mAutosubscription = Observable
                .interval(5, TimeUnit.SECONDS)
                .doOnNext(new Action1<Long>() {
                    @Override
                    public void call(Long n) {
                        if (context == null) {
                            return;
                        }
                        refreshStockData(context);
                    }
                }).subscribe();
    }

    public void unSubscribe() {
        if (mAutosubscription != null) {
            mAutosubscription.unsubscribe();
        }
        mAutosubscription = null;
    }
}
