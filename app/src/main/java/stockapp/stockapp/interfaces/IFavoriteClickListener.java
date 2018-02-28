package stockapp.stockapp.interfaces;

import stockapp.stockapp.models.StockItem;

/**
 * Created by heenaarora on 11/26/17.
 */

public interface IFavoriteClickListener {
    void onItemClick(StockItem stockItem);
    void onDelete(int position);
}
