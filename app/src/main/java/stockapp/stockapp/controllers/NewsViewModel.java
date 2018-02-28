package stockapp.stockapp.controllers;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import stockapp.stockapp.common.ApiRequestHandler;
import stockapp.stockapp.common.Utils;
import stockapp.stockapp.models.News;

/**
 * Created by heenaarora on 11/23/17.
 */

public class NewsViewModel {
    public static String TAG = NewsViewModel.class.getSimpleName();
    public static String REQUEST_TAG = "news.response";

    private ApiRequestHandler apiRequestHandler;
    private BehaviorSubject<ArrayList<News>> mNewsSubject = BehaviorSubject.create();
    private BehaviorSubject<Boolean> mIsLoadingSubject = BehaviorSubject.create(false);
    private Observable<Boolean> mIsLoadingObservale =  mIsLoadingSubject.asObservable();
    private Observable<ArrayList<News>> mNewsObservable =  mNewsSubject.asObservable();

    public NewsViewModel(Context context) {
        apiRequestHandler = ApiRequestHandler.getInstance(context);
    }


    public void requestNews(String mStockSymbol) {
        if (mIsLoadingSubject.getValue()) {
            return;
        }
        String url = Utils.getNewssUrl(mStockSymbol);
        mIsLoadingSubject.onNext(true);
        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            ArrayList<News>  newsList = new ArrayList<>();
                            JSONObject rss = response.getJSONObject("rss");
                            JSONObject channel = rss.getJSONObject("channel");
                            JSONArray itemsArray = channel.optJSONArray("item");
                            for (int i = 0; i < itemsArray.length(); i++) {
                                JSONObject item = itemsArray.getJSONObject(i);
                                News news = new News();
                                news.setTitle(item.getString("title"));
                                news.setLink(item.getString("link"));
                                news.setTime(item.getString("pubDate"));
                                news.setAuthor(item.getString("sa:author_name"));
                                newsList.add(news);
                            }
                            mNewsSubject.onNext(newsList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mNewsSubject.onNext(null);
                        }
                        mIsLoadingSubject.onNext(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mNewsSubject.onNext(null);
                mIsLoadingSubject.onNext(false);
            }
        });

        apiRequestHandler.addToRequestQueue(jsonObjectReq, REQUEST_TAG);
    }


    public Observable<ArrayList<News>> responseObservable() {
        return mNewsObservable;
    }

    public Observable<Boolean> isLoadingObservable() {
        return mIsLoadingObservale;
    }

}
