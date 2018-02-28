package stockapp.stockapp.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import stockapp.stockapp.R;
import stockapp.stockapp.interfaces.IFavoriteClickListener;
import stockapp.stockapp.interfaces.INewsClickHandler;
import stockapp.stockapp.models.News;
import stockapp.stockapp.models.StockItem;

/**
 * Created by heenaarora on 11/25/17.
 */

/**
 * View holder to speciality item view
 *
 * @author heenaarora
 */

public class FavoritesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.symbol)
    TextView mSymbol;
    @BindView(R.id.price)
    TextView mPrice;
    @BindView(R.id.change)
    TextView mChange;

    protected StockItem mStockItem;
    private View mItemView;
    private IFavoriteClickListener mItemClickListener;
    private Context mContext;
    private int mPosition;

    public FavoritesViewHolder(Context context, IFavoriteClickListener listener, View itemView) {
        super(itemView);
        this.mItemClickListener = listener;
        mItemView = itemView;
        mItemView.setOnClickListener(this);
        mContext = context;
        ButterKnife.bind(this, itemView);
    }

    public void updateView(StockItem stockItem, int position) {
        mPosition = position;
        mStockItem = stockItem;
        mSymbol.setText(stockItem.getStockSymbol());
        mPrice.setText(String.format("%.2f", stockItem.getClose()));
        double diff = stockItem.getClose() - stockItem.getOpen();
        double change = (diff / stockItem.getOpen()) * 100;

        String changeStr = String.format("%.2f", change);
        String value = String.format("%.2f", diff);
        if (change >= 0) {
            mChange.setTextColor(ContextCompat.getColor(mContext, R.color.green));
        } else {
            mChange.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }
        mChange.setText(value + " (" + changeStr + "%" + ")");
        mItemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPopup(itemView);
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (mItemClickListener == null) {
            return;
        }
        mItemClickListener.onItemClick(mStockItem);
    }


    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(mContext, v);
        popup.inflate(R.menu.list_delete_popup);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.yes:
                        mItemClickListener.onDelete(mPosition);
                        Toast.makeText(mContext, "Yes selected", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.no:
                        Toast.makeText(mContext, "No selected", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }
}
