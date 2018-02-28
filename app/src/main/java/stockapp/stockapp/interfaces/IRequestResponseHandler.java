package stockapp.stockapp.interfaces;

import java.util.ArrayList;

import stockapp.stockapp.models.StockSymbol;

/**
 * Created by heenaarora on 11/22/17.
 */

public interface IRequestResponseHandler {
    void showProgess();

    void hideProgress();

    void OnResponse(ArrayList<StockSymbol> data);

    void onError();

}
