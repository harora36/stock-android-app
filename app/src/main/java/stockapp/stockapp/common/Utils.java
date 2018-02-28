package stockapp.stockapp.common;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by heenaarora on 11/22/17.
 */

public class Utils {
    private static String BASE_URL = "http://webassignment-env.us-east-2.elasticbeanstalk.com/api/";
    public static String getAutocompleteUrl(String symbol) {
        return BASE_URL + "symbol/" + symbol;
    }

    public static String getStockDetailsUrl(String symbol) {
        return BASE_URL + "stockdetails/" + symbol;
    }

    public static String getNewssUrl(String symbol) {
        return BASE_URL + "symbol/" + symbol + "/news";
    }

    public static String getChartApiUrl(String symbol) {
        return BASE_URL + "stock/" + symbol + "/price";
    }

    public static String getIndicatorRequestUrl(String symbol, String indicator) {
        return "";
    }

}
