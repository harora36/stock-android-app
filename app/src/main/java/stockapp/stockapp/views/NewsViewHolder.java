package stockapp.stockapp.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import stockapp.stockapp.R;
import stockapp.stockapp.interfaces.INewsClickHandler;
import stockapp.stockapp.models.News;

/**
 * Created by heenaarora on 11/25/17.
 */

/**
 * View holder to speciality item view
 *
 * @author heenaarora
 */

public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.topic_title)
    TextView mTitle;
    @BindView(R.id.topic_desc)
    TextView mAuthor;
    @BindView(R.id.topic_date)
    TextView mPubDate;

    News mnNews;
    View mItemView;
    INewsClickHandler mItemClickListener;
    Context mContext;

    public NewsViewHolder(Context context, INewsClickHandler listener, View itemView) {
        super(itemView);
        this.mItemClickListener = listener;
        mItemView = itemView;
        mContext = context;
        ButterKnife.bind(this, itemView);
    }

    public void updateView(News news) {
        mnNews = news;
        mTitle.setText(news.getTitle());
        mAuthor.setText("Author: " + news.getAuthor());
        mPubDate.setText("Date: " + news.getEstTime());
        mItemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mItemClickListener == null) {
            return;
        }
        mItemClickListener.onItemClick(mnNews);
    }
}
