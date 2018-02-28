package stockapp.stockapp.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import stockapp.stockapp.R;
import stockapp.stockapp.common.FavoriteUtils;
import stockapp.stockapp.controllers.StockDetailViewModel;
import stockapp.stockapp.models.StockItem;
import stockapp.stockapp.views.activity.MainActivity;


public class CurrentFragment extends BaseFragment implements AdapterView.OnItemSelectedListener {
    @BindView(R.id.progress_bar)
    ProgressBar mProgress;
    @BindView(R.id.detail_table)
    View mStockDataList;
    @BindView(R.id.webview)
    WebView mWebview;
    @BindView(R.id.progress_bar2)
    ProgressBar mIndicatorProgress;
    @BindView(R.id.value1)
    TextView textView1;
    @BindView(R.id.value2)
    TextView textView2;
    @BindView(R.id.value3)
    TextView textView3;
    @BindView(R.id.value4)
    TextView textView4;
    @BindView(R.id.value5)
    TextView textView5;
    @BindView(R.id.value6)
    TextView textView6;
    @BindView(R.id.value7)
    TextView textView7;
    @BindView(R.id.value8)
    TextView textView8;
    @BindView(R.id.arrow_img)
    ImageView mArrowImg;

    @BindView(R.id.indicator_spinner)
    Spinner mIndicatorSpinner;
    @BindView(R.id.star_icon)
    ImageView mStarIcon;
    @BindView(R.id.change_text)
    TextView mChangeText;
    @BindView(R.id.error_msg)
    View mErrorLayout;
    @BindView(R.id.indicator_error)
    View mIndicatorError;

    private String[] mIndicatorItems;
    private StockItem mStockItem;
    private CompositeSubscription mCompositeSubscription;
    private StockDetailViewModel mViewModel;
    private JavaScriptInterface mInterface;
    private String mStockSymbol;
    private int currentIndicatorIndex;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    public CurrentFragment() {
        mCompositeSubscription = new CompositeSubscription();
    }

    public static CurrentFragment newInstance(String title) {
        CurrentFragment fragment = new CurrentFragment();
        fragment.setmTitle(title);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStockSymbol = getArguments().getString("stockSymbol");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current, container, false);
        ButterKnife.bind(this, view);
        mViewModel = new StockDetailViewModel(getContext());
        mStockDataList.setVisibility(View.INVISIBLE);
        mErrorLayout.setVisibility(View.INVISIBLE);
        mIndicatorError.setVisibility(View.INVISIBLE);
        initSubscriptions();
        initWebView();
        initIndicatorSpinner();
        boolean isFavorites = FavoriteUtils.getInstance().isFavorites(mStockSymbol);
        if (isFavorites) {
            mStarIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.filled));
        } else {
            mStarIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.star));
        }
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(getActivity());
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        return view;
    }

    private void initIndicatorSpinner() {
        mIndicatorItems = getResources().getStringArray(R.array.indicator_items);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_dropdown_item, mIndicatorItems);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mIndicatorSpinner.setOnItemSelectedListener(this);
        mIndicatorSpinner.setAdapter(sortAdapter);
    }

    public void initWebView() {
        mWebview.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && url.contains("android_asset") ){
                    return false;
                }
                if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else {
                    return false;
                }
            }
        });
        mInterface = new CurrentFragment.JavaScriptInterface(getContext(), mStockSymbol, "price");
        mWebview.addJavascriptInterface(mInterface, "chartView");
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        currentIndicatorIndex = 0;
        mWebview.loadUrl("file:///android_asset/indicator.html");
    }


    private void initSubscriptions() {
        mViewModel.requestStockData(mStockSymbol);
        mCompositeSubscription.addAll(
                mViewModel.responseObservable().observeOn(
                        AndroidSchedulers.mainThread()
                ).subscribe(new Subscriber<StockItem>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showError();
                    }

                    @Override
                    public void onNext(StockItem stockItem) {
                        setStockData(stockItem);
                    }
                }),
                mViewModel.isLoadingObservable().observeOn(
                        AndroidSchedulers.mainThread()).subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        setLoading(aBoolean);
                    }
                })
        );
    }

    public void setLoading(boolean loading) {
        if (loading) {
            mProgress.setVisibility(View.VISIBLE);
            mStockDataList.setVisibility(View.INVISIBLE);
        } else {
            mProgress.setVisibility(View.INVISIBLE);
        }
    }

    public void setIndicatorLoading(final boolean loading) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loading) {
                    mIndicatorProgress.setVisibility(View.VISIBLE);
                } else {
                    mIndicatorProgress.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    public void showError() {
        mStockDataList.setVisibility(View.INVISIBLE);
        mErrorLayout.setVisibility(View.VISIBLE);
        mProgress.setVisibility(View.INVISIBLE);
    }

    public void setStockData(StockItem stockItem) {
        if (stockItem == null) {
            showError();
            return;
        }
        mErrorLayout.setVisibility(View.INVISIBLE);
        mStockDataList.setVisibility(View.VISIBLE);
        mStockItem = stockItem;
        textView1.setText(stockItem.getStockSymbol());
        textView2.setText(String.valueOf(stockItem.getClose()));
        double diff = stockItem.getClose() - stockItem.getOpen();
        double change = (diff / stockItem.getOpen()) * 100;
        String changeStr = String.format("%.2f", change);
        String value = String.format("%.2f", diff);
        if (change >= 0) {
            mArrowImg.setImageDrawable(ContextCompat.getDrawable(
                    getContext(), R.drawable.up));
        } else {
            mArrowImg.setImageDrawable(ContextCompat.getDrawable(
                    getContext(), R.drawable.down));
        }
        textView3.setText(value + " (" + changeStr + "%" + ")");
        final Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss z");
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        textView4.setText(sdf.format(currentTime));
        textView5.setText(String.valueOf(stockItem.getOpen()));
        textView6.setText(String.valueOf(stockItem.getClose()));
        textView7.setText(stockItem.getLow() + " - " + stockItem.getHigh());
        textView8.setText(stockItem.getVolume());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCompositeSubscription.clear();
    }

    @Override
    public String getmTitle() {
        return mTitle;
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (currentIndicatorIndex == i) {
            mChangeText.setEnabled(false);
        } else {
            mChangeText.setEnabled(true);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @OnClick(R.id.change_text)
    public void changeIndicatorData() {
        mChangeText.setEnabled(false);
        int position = mIndicatorSpinner.getSelectedItemPosition();
        currentIndicatorIndex = position;
        mInterface.setmIndicator(mIndicatorItems[position].toLowerCase());
        mIndicatorError.setVisibility(View.INVISIBLE);
        mWebview.setVisibility(View.VISIBLE);
        mWebview.loadUrl("file:///android_asset/indicator.html");

    }

    @OnClick(R.id.star_icon)
    public void setFavorites() {
        if (mProgress.getVisibility() == View.VISIBLE) {
            return;
        }
        else if (mErrorLayout.getVisibility() == View.VISIBLE) {
            Toast.makeText(getContext(), getString(R.string.cant_favorite), Toast.LENGTH_SHORT).show();
            return;
        }
        boolean isFavorites = FavoriteUtils.getInstance().isFavorites(mStockSymbol);
        if (isFavorites) {
            FavoriteUtils.getInstance().remove_from_preference(mStockSymbol, getContext());
            mStarIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.star));
        } else {
            FavoriteUtils.getInstance().save_to_preference(mStockItem, getContext());
            mStarIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.filled));
        }

    }

    public void showIndicatorErrorLayout() {
        mWebview.setVisibility(View.INVISIBLE);
        mIndicatorError.setVisibility(View.VISIBLE);
        mIndicatorProgress.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.fb_icon)
    public void shareToFb() {
        mWebview.loadUrl("javascript:downloadChart()");
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public class JavaScriptInterface {
        Context mContext;
        String mSymbol;
        String mIndicator;

        JavaScriptInterface(Context c, String symbol, String indicator) {
            mContext = c;
            mSymbol = symbol;
            mIndicator = indicator;
        }

        public void setmIndicator(String mIndicator) {
            this.mIndicator = mIndicator;
        }

        @JavascriptInterface
        public String getIndicator() {
            return mIndicator;
        }

        @JavascriptInterface
        public void setShowLoading(final boolean showLoading) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setIndicatorLoading(showLoading);
                }
            });
        }

        @JavascriptInterface
        public String getStockSymbol() {
            return mSymbol;
        }

        @JavascriptInterface
        public void shareOnFacebook(String url) {
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(url))
                        .build();
                shareDialog.show(linkContent);
            }
        }

        @JavascriptInterface
        public void showIndicatorError() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showIndicatorErrorLayout();
                }
            });
        }

    }


}
