package stockapp.stockapp.views.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.BindView;
import butterknife.ButterKnife;
import stockapp.stockapp.R;
import stockapp.stockapp.views.ViewPagerAdapter;

public class StockDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_STOCK = "stock_name";

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private ViewPagerAdapter mAdapter;
    private String mStockSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra(EXTRA_STOCK) != null) {
            mStockSymbol = intent.getStringExtra(EXTRA_STOCK);
        }
        initActionBar();
        initViews();
    }

    private void initActionBar() {
        getSupportActionBar().setTitle(mStockSymbol);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        mTabLayout.setupWithViewPager(mViewPager);
        mAdapter = new ViewPagerAdapter(this, getSupportFragmentManager(), mStockSymbol);
        setUpViewPager();
    }

    private void setUpViewPager() {
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
