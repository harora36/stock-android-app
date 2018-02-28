package stockapp.stockapp.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.BehaviorSubject;
import stockapp.stockapp.R;
import stockapp.stockapp.controllers.StockDetailViewModel;
import stockapp.stockapp.models.StockItem;
import stockapp.stockapp.views.FavoritesAdapter;

/**
 * Created by heenaarora on 11/21/17.
 */

public class FavoriteUtils {
    public static final String FAVORITES_PREF = "stock_favorite_pref";

    private static FavoriteUtils favoriteUtils;
    private BehaviorSubject<ArrayList<StockItem>> favoritesSubject;
    private BehaviorSubject<String> unfavoriteTopicSubject;


    private FavoriteUtils() {
        this.favoritesSubject = BehaviorSubject.create(new ArrayList<StockItem>());
        this.unfavoriteTopicSubject = BehaviorSubject.create("");
    }

    public static FavoriteUtils getInstance() {
        if (favoriteUtils == null) {
            favoriteUtils = new FavoriteUtils();
        }
        return favoriteUtils;
    }


    public Observable<ArrayList<StockItem>> getFavoritesObservale() {
        return favoritesSubject.asObservable();
    }

    public Observable<String> getUnfavoriteObservable() {
        return unfavoriteTopicSubject.asObservable();
    }

    public void save_to_preference(StockItem item, Context context) {
        if (isAlreadyFavorite(item.getStockSymbol(), context) != null) {
            return;
        }
        ArrayList<StockItem> items = getFavorites(context);
        items.add(item);
        favoritesSubject.onNext(items);
        save_list_to_preference(context, items);
    }

    public ArrayList<StockItem> getFavorites(Context context) {
        if (favoritesSubject.getValue() != null && !favoritesSubject.getValue().isEmpty()) {
            return favoritesSubject.getValue();
        }
        SharedPreferences appSharedPrefs = context.getSharedPreferences(
                FAVORITES_PREF, Context.MODE_PRIVATE);
        String json = appSharedPrefs.getString(FAVORITES_PREF, null);
        Type type = new TypeToken<ArrayList<StockItem>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<StockItem> topics = gson.fromJson(json, type);
        if (topics == null) {
            topics = new ArrayList<>();
        }
        favoritesSubject.onNext(topics);
        return favoritesSubject.getValue();
    }

    public boolean isFavorites(String stockSymbol) {
        ArrayList<StockItem> favorites = favoritesSubject.getValue();
        for (int i = 0; i < favorites.size(); i++) {
            String title = favorites.get(i).getStockSymbol();
            if (title.equalsIgnoreCase(stockSymbol)) {
                return true;
            }
        }
        return false;
    }

    private StockItem isAlreadyFavorite(String symbol, Context context) {
        ArrayList<StockItem> stocks = getFavorites(context);
        StockItem fav = null;
        for (int i = 0; i < stocks.size(); i++) {
            String title = stocks.get(i).getStockSymbol();
            if (title.equalsIgnoreCase(symbol)) {
                fav = stocks.get(i);
                break;
            }
        }
       return fav;
    }

    public void remove_from_preference(String symbol, Context context) {
        ArrayList<StockItem> stocks = getFavorites(context);
        if (stocks == null || stocks.isEmpty()) {
            return;
        }
        StockItem  item = isAlreadyFavorite(symbol, context);
        if (item == null) {
            return;
        }
        stocks.remove(item);
        save_list_to_preference(context, stocks);
        unfavoriteTopicSubject.onNext(symbol);
        favoritesSubject.onNext(stocks);

    }

    public void save_list_to_preference(Context context, ArrayList<StockItem> stocks) {
        SharedPreferences.Editor prefsEditor = getPrefEditor(context);
        Gson gson = new Gson();
        String json = gson.toJson(stocks);
        prefsEditor.putString(FAVORITES_PREF, json);
        prefsEditor.commit();
    }

    private SharedPreferences.Editor getPrefEditor(Context context) {
        SharedPreferences appSharedPrefs = context.getSharedPreferences(
                FAVORITES_PREF, Context.MODE_PRIVATE);
        return appSharedPrefs.edit();
    }

    public void updateFavorites(Context context, ArrayList<StockItem> favorites) {
        ArrayList<StockItem> newList = new ArrayList<>();
        for (StockItem fav: favorites) {
            if (isFavorites(fav.getStockSymbol())) {
                newList.add(fav);
            }
        }
        favoritesSubject.onNext(newList);
        save_list_to_preference(context, favorites);
    }

    public void sortItems(ArrayList<StockItem> favorites, Context context, String sort, String order) {
        FavoriteUtils.CustomComparator comparator;
        int column = 0;
        if (order.equalsIgnoreCase("Descending")) {
            comparator = new FavoriteUtils.DescendingComparator();
        }
        else {
            comparator = new FavoriteUtils.CustomComparator();
        }
        if (sort.equalsIgnoreCase(context.getString(R.string.symbol))) {
            column = 0;
        }
        else if (sort.equalsIgnoreCase(context.getString(R.string.price))) {
            column = 1;
        }
        else {
            column = 2;
        }
        comparator.setColumn(column);
        Collections.sort(favorites, comparator);
        updateFavorites(context, favorites);
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

    static class DescendingComparator extends FavoriteUtils.CustomComparator {
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
