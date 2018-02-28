package stockapp.stockapp.models;

/**
 * Created by heenaarora on 11/23/17.
 */

public class StockSymbol {
    private String name;
    private String exchange;
    private String symbol;

    public StockSymbol(String symbol, String name, String exchange) {
        this.symbol = symbol;
        this.name = name;
        this.exchange = exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getExchange() {
        return exchange;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }
}
