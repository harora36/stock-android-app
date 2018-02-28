package stockapp.stockapp.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import stockapp.stockapp.views.NewsAdapter;
import stockapp.stockapp.R;
import stockapp.stockapp.controllers.NewsViewModel;
import stockapp.stockapp.interfaces.INewsClickHandler;
import stockapp.stockapp.models.News;


public class NewsFragment extends BaseFragment implements INewsClickHandler {
    @BindView(R.id.topics_list_view)
    RecyclerView mSpecialities;
    @BindView(R.id.progress_bar)
    ProgressBar mProgress;
    @BindView(R.id.error_msg)
    View mErrorLayout;

    private NewsAdapter mNewsAdapter;
    private CompositeSubscription mCompositeSubscription;
    private NewsViewModel mController;
    private String mTitle;
    private String mStockSymbol;

    public NewsFragment() {
        mCompositeSubscription = new CompositeSubscription();
    }

    public static NewsFragment newInstance(String title) {
        NewsFragment fragment = new NewsFragment();
        fragment.mTitle = title;
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
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);
        mController = new NewsViewModel(getContext());
        mSpecialities.setVisibility(View.INVISIBLE);
        mErrorLayout.setVisibility(View.INVISIBLE);
        mSpecialities.setLayoutManager(new LinearLayoutManager(getActivity()));
        initSubscriptions();
        setAdapter();
        return view;
    }

    /**
     * Show list of subspecialities returned by server
     */
    protected void setAdapter() {
        ArrayList<News> news = new ArrayList<>();
        mNewsAdapter = new NewsAdapter(news, getContext(), this);
        mSpecialities.setAdapter(mNewsAdapter);
    }

    /**
     * Subscribe to server response for subspecialities
     */
    private void initSubscriptions() {
        mController.requestNews(mStockSymbol);
        mCompositeSubscription.addAll(
                mController.responseObservable().observeOn(
                        AndroidSchedulers.mainThread()
                ).subscribe(new Subscriber<ArrayList<News>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showError();
                    }

                    @Override
                    public void onNext(ArrayList<News> newses) {
                        setNews(newses);
                    }
                }),
                mController.isLoadingObservable().observeOn(
                        AndroidSchedulers.mainThread()).subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                       setLoading(false);

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        setLoading(aBoolean);
                    }
                })
        );
    }

    public void showError() {
        mProgress.setVisibility(View.INVISIBLE);
        mErrorLayout.setVisibility(View.VISIBLE);
        mSpecialities.setVisibility(View.INVISIBLE);
    }

    public void setLoading(boolean loading) {
        if (loading) {
            mProgress.setVisibility(View.VISIBLE);
        } else {
            mProgress.setVisibility(View.INVISIBLE);
        }
    }

    public void setNews(ArrayList<News> news) {
        if (news == null) {
            showError();
            return;
        }
        mErrorLayout.setVisibility(View.INVISIBLE);
        mSpecialities.setVisibility(View.VISIBLE);
        mNewsAdapter = new NewsAdapter(news, getContext(), this);
        mSpecialities.setAdapter(mNewsAdapter);
        mNewsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCompositeSubscription.clear();
    }

    @Override
    public void onItemClick(News news) {
        String url = news.getLink();
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public String getmTitle() {
        return mTitle;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
