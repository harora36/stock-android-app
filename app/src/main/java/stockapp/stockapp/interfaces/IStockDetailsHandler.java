package stockapp.stockapp.interfaces;

import java.util.ArrayList;

import stockapp.stockapp.models.News;

/**
 * Created by heenaarora on 11/23/17.
 */

public interface IStockDetailsHandler {
    void onResposeStockData();

    void onResponseNews(ArrayList<News> newsList);

    void onErrorNews();
}
