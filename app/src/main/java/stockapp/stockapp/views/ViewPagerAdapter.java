package stockapp.stockapp.views;

/**
 * Created by heenaarora on 11/22/17.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import stockapp.stockapp.R;
import stockapp.stockapp.views.fragments.BaseFragment;
import stockapp.stockapp.views.fragments.CurrentFragment;
import stockapp.stockapp.views.fragments.HistoricalFragment;
import stockapp.stockapp.views.fragments.NewsFragment;

/**
 * Initializes tab views and fragments inside viewpager for sub specialities
 *
 * @author heenaarora
 */


public class ViewPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String mStockSymbol;

    public ViewPagerAdapter(Context context, FragmentManager fragmentManager, String stockSymbol) {
        super(fragmentManager);
        this.mContext = context;
        this.mStockSymbol = stockSymbol;
    }

    @Override
    public BaseFragment getItem(int position) {
        switch (position) {
            case 0:
                CurrentFragment currentFragment = CurrentFragment.newInstance(
                        mContext.getString(R.string.current));
                Bundle bundle = new Bundle();
                bundle.putString("stockSymbol", mStockSymbol);
                currentFragment.setArguments(bundle);
                return currentFragment;
            case 1:
                HistoricalFragment historicalFragment = HistoricalFragment.newInstance(
                        mContext.getString(R.string.historical));
                Bundle bundle2 = new Bundle();
                bundle2.putString("stockSymbol", mStockSymbol);
                historicalFragment.setArguments(bundle2);
                return historicalFragment;
            default:
                NewsFragment newsFragment = NewsFragment.newInstance(
                        mContext.getString(R.string.news));
                Bundle bundle3 = new Bundle();
                bundle3.putString("stockSymbol", mStockSymbol);
                newsFragment.setArguments(bundle3);
                return newsFragment;
        }
    }


    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getItem(position).getmTitle();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

