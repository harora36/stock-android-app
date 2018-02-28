package stockapp.stockapp.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import stockapp.stockapp.R;
import stockapp.stockapp.interfaces.INewsClickHandler;
import stockapp.stockapp.models.News;
import stockapp.stockapp.views.NewsViewHolder;

/**
 * Created by heenaarora on 11/25/17.
 */

/**
 * Populates data and create corresponding views for section items
 *
 * @author heenaarora
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {
    ArrayList<News> mNewsList;
    INewsClickHandler mListener;
    Context mContext;

    public NewsAdapter(Context context, INewsClickHandler listener) {
        this.mNewsList = new ArrayList<>();
        this.mListener = listener;
        this.mContext = context;
    }

    public NewsAdapter(ArrayList<News> news, Context context,
                       INewsClickHandler listener) {
        this(context, listener);
        if (news != null) {
            mNewsList.addAll(news);
        }
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(
                parent.getContext()
        ).inflate(R.layout.news_row, parent, false);
        return new NewsViewHolder(mContext, mListener, itemView);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        holder.updateView(mNewsList.get(position));
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }
}
