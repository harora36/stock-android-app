package stockapp.stockapp.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import stockapp.stockapp.common.Utils;

/**
 * Created by heenaarora on 11/23/17.
 */

public class News implements Parcelable {
    private String title;
    private String author;
    private String link;
    private String time;
    private String estTime;


    public News() {

    }

    protected News(Parcel in) {
        title = in.readString();
        author = in.readString();
        link = in.readString();
        time = in.readString();
        estTime = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(link);
        dest.writeString(time);
        dest.writeString(estTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    public void setLink(String link) {
        this.link = link;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTime(String time) {
        this.time = time;
        setEstTime();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getTime() {
        return time;
    }

    public void setEstTime() {
        String pattern = "EEE, dd MMM yyyy HH:mm:ss Z";
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat(pattern);
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        simpleDateFormat.setTimeZone(gmt);
        try {
            Date date = simpleDateFormat.parse(time);
            DateFormat gmtFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
            TimeZone estTimeZone = TimeZone.getTimeZone("America/NEW_YORK");
            gmtFormat.setTimeZone(estTimeZone);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            estTime = gmtFormat.format(date) + " EST" ;
        } catch (ParseException e) {
            e.printStackTrace();
            estTime = time;
        }
    }

    public String getEstTime() {
        return estTime;
    }
}
