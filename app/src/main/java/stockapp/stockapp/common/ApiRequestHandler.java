package stockapp.stockapp.common;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ApiRequestHandler {
    public static String TAG = ApiRequestHandler.class.getSimpleName();

    private static ApiRequestHandler mAppSingletonInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private ApiRequestHandler(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();

//        mImageLoader = new ImageLoader(mRequestQueue,
//                new ImageLoader.ImageCache() {
//                    private final LruCache<String, Bitmap>
//                            cache = new LruCache<String, Bitmap>(20);
//
//                    @Override
//                    public Bitmap getBitmap(String url) {
//                        return cache.get(url);
//                    }
//
//                    @Override
//                    public void putBitmap(String url, Bitmap bitmap) {
//                        cache.put(url, bitmap);
//                    }
//                });
    }

    public static synchronized ApiRequestHandler getInstance(Context context) {
        if (mAppSingletonInstance == null) {
            mAppSingletonInstance = new ApiRequestHandler(context);
        }
        return mAppSingletonInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        Log.d(TAG, "addToRequestQueue");
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}