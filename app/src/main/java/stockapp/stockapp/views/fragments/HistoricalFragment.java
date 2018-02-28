package stockapp.stockapp.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import stockapp.stockapp.R;


public class HistoricalFragment extends BaseFragment {
    @BindView(R.id.webview)
    WebView mWebview;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.error_msg)
    View mErrorLayout;

    private String mStockSymbol;

    public HistoricalFragment() {
    }

    public static HistoricalFragment newInstance(String title) {
        HistoricalFragment fragment = new HistoricalFragment();
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
        View view = inflater.inflate(R.layout.fragment_historical, container, false);
        ButterKnife.bind(this, view);
        mErrorLayout.setVisibility(View.INVISIBLE);
        initWebView();
        return view;
    }

    public void showErrorLayout() {
        mWebview.setVisibility(View.INVISIBLE);
        mErrorLayout.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    public void setLoading(boolean loading) {
        if (loading) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
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
        mWebview.addJavascriptInterface(
                new JavaScriptInterface(getContext(), mStockSymbol), "chartView");
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebview.loadUrl("file:///android_asset/index.html");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public class JavaScriptInterface {
        Context mContext;
        String mSymbol;

        JavaScriptInterface(Context c, String symbol) {
            mContext = c;
            mSymbol = symbol.toUpperCase();
        }

        @JavascriptInterface
        public void setShowLoading(final boolean showLoading) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setLoading(showLoading);
                }
            });
        }

        @JavascriptInterface
        public void showError() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showErrorLayout();
                }
            });
        }

        @JavascriptInterface
        public String getStockSymbol() {
            return mSymbol;
        }

    }

}
