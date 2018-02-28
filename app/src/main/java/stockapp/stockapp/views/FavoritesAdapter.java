package stockapp.stockapp.views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import stockapp.stockapp.R;
import stockapp.stockapp.common.FavoriteUtils;
import stockapp.stockapp.controllers.StockDetailViewModel;
import stockapp.stockapp.interfaces.IFavoriteClickListener;
import stockapp.stockapp.models.StockItem;
import stockapp.stockapp.views.activity.StockDetailsActivity;

/**
 * Created by heenaarora on 11/25/17.
 */

/**
 * Populates data and create corresponding views for section items
 *
 * @author heenaarora
 */

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesViewHolder>
        implements IFavoriteClickListener {
    ArrayList<StockItem> mFavoriteList;
    Context mContext;

    public FavoritesAdapter(Context context) {
        this.mFavoriteList = new ArrayList<>();
        this.mContext = context;
    }

    public FavoritesAdapter(ArrayList<StockItem> stockSymbols, Context context) {
        this(context);
        if (stockSymbols != null) {
            mFavoriteList.addAll(stockSymbols);
        }
    }

    @Override
    public FavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(
                parent.getContext()
        ).inflate(R.layout.favorite_item, parent, false);
        return new FavoritesViewHolder(mContext, this, itemView);
    }

    @Override
    public void onBindViewHolder(final FavoritesViewHolder holder, int position) {
        holder.updateView(mFavoriteList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mFavoriteList.size();
    }

    @Override
    public void onItemClick(StockItem stockItem) {
        Intent intent = new Intent(mContext, StockDetailsActivity.class);
        intent.putExtra(StockDetailsActivity.EXTRA_STOCK, stockItem.getStockSymbol());
        mContext.startActivity(intent);
    }

    public void sortItems(String sort, String order) {
        CustomComparator comparator;
        int column = 0;
        if (order.equalsIgnoreCase("Descending")) {
            comparator = new DescendingComparator();
        }
        else {
            comparator = new CustomComparator();
        }
        if (sort.equalsIgnoreCase(mContext.getString(R.string.symbol))) {
            column = 0;
        }
        else if (sort.equalsIgnoreCase(mContext.getString(R.string.price))) {
            column = 1;
        }
        else {
            column = 2;
        }
        comparator.setColumn(column);
        Collections.sort(mFavoriteList, comparator);
        notifyDataSetChanged();
        FavoriteUtils.getInstance().updateFavorites(mContext, mFavoriteList);
    }

    @Override
    public void onDelete(int position) {
        FavoriteUtils.getInstance().remove_from_preference(
                mFavoriteList.get(position).getStockSymbol(), mContext);
        mFavoriteList.remove(position);
        notifyDataSetChanged();
    }

    static class CustomComparator implements Comparator<StockItem> {
        static final int SYMBOL = 0, PRICE = 1, CHANGE = 2;
        int column;

        public CustomComparator() {
        }

        public void setColumn(int column) {
            this.column = column;
        }

        @Override
        public int compare(StockItem o1, StockItem o2) {
            switch (column) {
                case SYMBOL:
                    return o1.getStockSymbol().compareTo(o2.getStockSymbol());
                case PRICE:
                    return ((Double)o1.getClose()).compareTo((Double)o2.getClose());
                case CHANGE:
                    return ((Double)o1.getChange()).compareTo((Double)o2.getChange());
                default:
                    return 0;
            }
        }
    }

    static class DescendingComparator extends CustomComparator {
        public DescendingComparator() {

        }

        public void setColumn(int column) {
            this.column = column;
        }

        @Override
        public int compare(StockItem o2, StockItem o1) {
            switch (column) {
                case SYMBOL:
                    return o1.getStockSymbol().compareTo(o2.getStockSymbol());
                case PRICE:
                    return ((Double)o1.getClose()).compareTo((Double)o2.getClose());
                case CHANGE:
                    return ((Double)o1.getChange()).compareTo((Double)o2.getChange());
                default:
                    return 0;
            }
        }
    }

}
