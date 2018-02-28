package stockapp.stockapp.views.activity;

import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.facebook.FacebookSdk;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import stockapp.stockapp.R;
import stockapp.stockapp.common.FavoriteUtils;
import stockapp.stockapp.common.Utils;
import stockapp.stockapp.controllers.AutocompleteViewModel;
import stockapp.stockapp.controllers.FavoriteViewModel;
import stockapp.stockapp.controllers.StockDetailViewModel;
import stockapp.stockapp.interfaces.IRequestResponseHandler;
import stockapp.stockapp.models.StockItem;
import stockapp.stockapp.models.StockSymbol;
import stockapp.stockapp.views.FavoritesAdapter;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        TextWatcher, IRequestResponseHandler, AdapterView.OnItemSelectedListener {
    public static String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.order_spinner)
    Spinner mOrderSpinner;
    @BindView(R.id.sort_spinner)
    Spinner mSortSpinner;
    @BindView(R.id.stock_name)
    AutoCompleteTextView mAutoCompleteTextView;
    @BindView(R.id.progress_bar)
    View mProgessBar;
    @BindView(R.id.favorites_list)
    RecyclerView mFavoritesRecyclerView;
    @BindView(R.id.fav_progress_bar)
    View mFavProgressBar;
    @BindView(R.id.auto_switch)
    Switch mAutoSwitch;

    private AutocompleteViewModel mAutocompleteViewModel;
    private boolean isLoading;
    private String mStockSymbol;
    private ArrayList<StockSymbol> mStockList;
    private CompositeSubscription mCompositeSubscription;
    private FavoritesAdapter mFavoriteAdapter;
    private FavoriteViewModel mFavViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initSortSpinner();
        initOrderSpinner();

        mFavoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCompositeSubscription = new CompositeSubscription();
        mFavViewModel = new FavoriteViewModel(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>());
        mAutoCompleteTextView.setAdapter(adapter);
        mAutoCompleteTextView.setThreshold(1);
        mCompositeSubscription.addAll(FavoriteUtils.getInstance().getFavoritesObservale().observeOn(
                AndroidSchedulers.mainThread()).subscribe(new Subscriber<ArrayList<StockItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ArrayList<StockItem> stockItems) {
                        setFavorites(stockItems);
                    }
                }),
                mFavViewModel.isLoadingObservable().
                        observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        int visibilty = aBoolean ? View.VISIBLE : View.INVISIBLE;
                        mFavProgressBar.setVisibility(visibilty);
                    }
                }));
        ArrayList<StockItem> favorites = FavoriteUtils.getInstance().getFavorites(this);
        setFavorites(favorites);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFavViewModel.refreshStockData(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAutoCompleteTextView.clearFocus();
        initAutocomplete();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getString("stockSymbol") != null) {
            mStockSymbol = savedInstanceState.getString("stockSymbol");
        }
    }

    private void initSortSpinner() {
        String[] sortList = getResources().getStringArray(R.array.sort_items);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(
                this, R.layout.spnner_dropdown_item, sortList);
        sortAdapter.setDropDownViewResource(R.layout.spnner_dropdown_item);
        mSortSpinner.setAdapter(sortAdapter);
        mSortSpinner.setOnItemSelectedListener(this);

    }

    private void initAutocomplete() {
        mAutoCompleteTextView.addTextChangedListener(this);
        mAutoCompleteTextView.setOnItemClickListener(this);
        mAutocompleteViewModel = new AutocompleteViewModel(this);
    }

    public void setFavorites(ArrayList<StockItem> favoriteItems) {
        mFavoriteAdapter = new FavoritesAdapter(favoriteItems, this);
        mFavoritesRecyclerView.setAdapter(mFavoriteAdapter);
    }

    private void initOrderSpinner() {
        String[] orderList = getResources().getStringArray(R.array.order_items);
        ArrayAdapter<String> orderAdapter = new ArrayAdapter<>(
                this, R.layout.spnner_dropdown_item, orderList);
        orderAdapter.setDropDownViewResource(R.layout.spnner_dropdown_item);
        mOrderSpinner.setAdapter(orderAdapter);
        mOrderSpinner.setOnItemSelectedListener(this);
    }


    @OnClick(R.id.quote_btn)
    public void getQuotes() {
        if (mStockSymbol == null) {
            Toast.makeText(this, getString(R.string.validation_msg), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, StockDetailsActivity.class);
        intent.putExtra(StockDetailsActivity.EXTRA_STOCK, mStockSymbol);
        startActivity(intent);
    }

    @OnClick(R.id.clear_btn)
    public void clearQuotes() {
        if (isLoading) {
            return;
        }
        hideKeyboard();
        mAutoCompleteTextView.setText("");
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mStockSymbol = null;
        if (mAutoCompleteTextView.isPerformingCompletion()) {
            return;
        }
    }

    @Override
    public void afterTextChanged(final Editable editable) {
        if (editable.toString() == null || editable.toString().isEmpty()) {
            return;
        }
        if (mAutoCompleteTextView.isPerformingCompletion()) {
            return;
        }
        if (isLoading) {
            mAutocompleteViewModel.cancelOngoingRequest(this);
        }
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgess();
            }
        });
        isLoading = true;
        mAutocompleteViewModel.symbolsRequest(Utils.getAutocompleteUrl(
                editable.toString()), getApplicationContext());
    }

    @Override
    public void showProgess() {
        mProgessBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgessBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAutoCompleteTextView.removeTextChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }

    @Override
    public void OnResponse(ArrayList<StockSymbol> symbols) {
        if (MainActivity.this == null || MainActivity.this.isFinishing()) {
            return;
        }
        mStockList = symbols;
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> names = new ArrayList<>();
                for (int i = 0; i < mStockList.size(); i++) {
                    StockSymbol stockSymbol = mStockList.get(i);
                    names.add(stockSymbol.getSymbol() + " - " + stockSymbol.getName() +
                            "(" + stockSymbol.getExchange() + ")");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, names);
                mAutoCompleteTextView.setThreshold(1);
                mAutoCompleteTextView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                hideProgress();
            }
        });
        isLoading = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mStockSymbol != null)
            outState.putString("stockSymbol", mStockSymbol);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        hideKeyboard();
        mStockSymbol = mStockList.get(position).getSymbol();
    }

    public void hideKeyboard() {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(mAutoCompleteTextView.getApplicationWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void handleSortClick() {
        String sort = (String) mSortSpinner.getSelectedItem();
        String order = (String) mOrderSpinner.getSelectedItem();
        if (sort.equalsIgnoreCase(getString(R.string.sort))
                || order.equalsIgnoreCase(getString(R.string.order_by))) {
            return;
        }
        mFavoriteAdapter.sortItems(sort, order);
    }

    @Override
    public void onError() {
        isLoading = false;
        hideProgress();
    }

    @OnClick(R.id.refresh_img)
    public void refreshFavorites() {
        mFavViewModel.refreshStockData(this);
    }

    @OnClick(R.id.auto_switch)
    public void autoRefreshClick() {
        if (mAutoSwitch.isChecked()) {
            mFavViewModel.autoRefresh(this);
        } else {
            mFavViewModel.unSubscribe();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFavViewModel.unSubscribe();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        handleSortClick();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
