package stockapp.stockapp.interfaces;

import stockapp.stockapp.models.News;

/**
 * Created by heenaarora on 11/25/17.
 */

public interface INewsClickHandler {
    void onItemClick(News news);
}
