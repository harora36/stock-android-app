package stockapp.stockapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by heenaarora on 11/25/17.
 */

public class StockItem implements Parcelable {
    private String stockSymbol;
    private double open;
    private double high;
    private double low;
    private double close;
    private String volume;
    private String timeStamp;

    public StockItem() {

    }

    protected StockItem(Parcel in) {
        stockSymbol = in.readString();
        open = in.readDouble();
        high = in.readDouble();
        low = in.readDouble();
        close = in.readDouble();
        volume = in.readString();
        timeStamp = in.readString();
    }

    public static final Creator<StockItem> CREATOR = new Creator<StockItem>() {
        @Override
        public StockItem createFromParcel(Parcel in) {
            return new StockItem(in);
        }

        @Override
        public StockItem[] newArray(int size) {
            return new StockItem[size];
        }
    };

    public String getStockSymbol() {
        return stockSymbol;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(stockSymbol);
        parcel.writeDouble(open);
        parcel.writeDouble(high);
        parcel.writeDouble(low);
        parcel.writeDouble(close);
        parcel.writeString(volume);
        parcel.writeString(timeStamp);
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getClose() {
        return close;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getOpen() {
        return open;
    }

    public String getVolume() {
        return volume;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public double getChange() {
        return this.getClose() - this.getOpen();
    }

    public String getChangePercent() {
        double changepercent = (getChange()/getOpen()) * 100;
        return String.valueOf(changepercent) + "%";
    }

    @Override
    public String toString() {
        return "StockItem {" +
                "symbol = " + stockSymbol +
                "close = " + close +
                "open = " + open +
                "}";
    }
}
