package in.co.erudition.paper.activity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.erudition.polygonprogressbar.NSidedProgressBar;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import in.co.erudition.paper.R;
import in.co.erudition.paper.adapter.SearchAdapter;
import in.co.erudition.paper.data.model.SearchResult;
import in.co.erudition.paper.data.remote.BackendService;
import in.co.erudition.paper.network.NetworkUtils;
import in.co.erudition.paper.util.ApiUtils;
import in.co.erudition.paper.util.PreferenceUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private BackendService mService;
    private RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;
    private Call<List<SearchResult>> searchCall;
    private NSidedProgressBar progressBar;
    private LinearLayout searchList;
    private ImageView no_results;
//    private TextView result_count;
//    private TextView end_msg;
    private AdView adView;
    private NetworkUtils mNetworkUtils = new NetworkUtils();

    private InterstitialAd mInterstitialAd;
    private AdCountDownTimer timer;

    private String TAG = "SearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

//        SearchView searchView = (SearchView) findViewById(R.id.search);

        //Load Ads
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        //Setup Interstitial Ads --> only once at startup
        //Interstitial video ads
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/8691691433");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        //load ads in advance
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                //Set the timer Again
                timer = new AdCountDownTimer(PreferenceUtils.getAdTime(),1000);
                timer.start();
            }

        });

        //Set a timer
        timer = new AdCountDownTimer(PreferenceUtils.getAdTime(),1000);
        timer.start();

        progressBar = (NSidedProgressBar) findViewById(R.id.progressBar_search);
        searchList = (LinearLayout) findViewById(R.id.search_linear_layout);
        no_results = (ImageView) findViewById(R.id.no_results_img);
//        result_count = (TextView) findViewById(R.id.result_count);
//        end_msg = (TextView) findViewById(R.id.empty_view);
//        View nav_space = (View) findViewById(R.id.nav_spacer_ad);

        progressBar.setVisibility(View.INVISIBLE);

        // To set the background of the activity go below the StatusBar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorBlack25alpha));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorMaterialBlack_no_alpha));
        }

        /*
            Adjusting the Status bar margin for Different notches
         */
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        if (Build.VERSION.SDK_INT >= 20) {
            ViewCompat.setOnApplyWindowInsetsListener(appBarLayout, (View v, WindowInsetsCompat insets) -> {
//                v.getLayoutParams().height -= getResources().getDimensionPixelSize(R.dimen.status_bar_height);
//                v.getLayoutParams().height += insets.getSystemWindowInsetTop();

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                params.topMargin = insets.getSystemWindowInsetTop();
                v.invalidate();
                v.requestLayout();

                params = (ViewGroup.MarginLayoutParams) adView.getLayoutParams();
                params.bottomMargin = insets.getSystemWindowInsetBottom();
                adView.invalidate();
                adView.requestLayout();

//                params = (ViewGroup.MarginLayoutParams) nav_space.getLayoutParams();
//                params.bottomMargin = insets.getSystemWindowInsetBottom();
//                nav_space.invalidate();
//                nav_space.requestLayout();

                return insets.consumeSystemWindowInsets();
            });
        }

        Drawable bg;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            bg = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
            bg.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.MULTIPLY);
        } else {
            bg = VectorDrawableCompat.create(getResources(), R.drawable.ic_arrow_back_black_24dp, null);
            bg = DrawableCompat.wrap(bg);
            DrawableCompat.setTint(bg, ContextCompat.getColor(this, R.color.colorWhite));
        }

        toolbar.setNavigationIcon(bg);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        mService = ApiUtils.getBackendService();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_search);
        mAdapter = new SearchAdapter(this, new ArrayList<SearchResult>(), getIntent(), id -> {
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.d("Search", "done adapter");


        /**
         * To make the SearchView expanded by default, call setIconifiedByDefault(false) on it when you
         * initialise it (e.g. in onCreateOptionsMenu(..) or onPrepareOptionsMenu(..)). I've found in most cases
         * this will give it focus automatically, but if not simply call requestFocus() on it too.
         */
//        searchView.setFocusable(true);
//        searchView.setIconified(false);
//        searchView.requestFocusFromTouch();

    }

    private void search(String query) {

        double start = System.currentTimeMillis();
        searchCall = mService.search(query);

        searchCall.enqueue(new Callback<List<SearchResult>>() {
            @Override
            public void onResponse(Call<List<SearchResult>> call, Response<List<SearchResult>> response) {
                //If successful just display that update was successful
                Log.d("Call", call.request().toString());
                if (response.isSuccessful()) {
                    Log.d("Search:", "Successful!");
                    double end = System.currentTimeMillis();
                    double responseTime = (end - start) / 1000.0;
                    int size = 0;
                    if (response.body() != null) {
                        size = response.body().size();
                    }
                    String str = "About " + String.valueOf(size) + " Results" + " (" + String.valueOf(responseTime) + ") seconds";
//                    result_count.setText(str);
                    mAdapter.setSearchTime(str);
                    mAdapter.updateResults(response.body());
                    searchList.setVisibility(View.VISIBLE);
                    no_results.setVisibility(View.GONE);
//                    end_msg.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    int statusCode = response.code();
                    if (statusCode == 401) {
                        Log.d("StatusCode", "Unauthorized");
                    }
                    if (statusCode == 200) {
                        Log.d("StatusCode", "OK");
                    } else {
                        Log.d("StatusCode", String.valueOf(statusCode));
                    }
                    // handle request errors depending on status code
                }
            }

            @Override
            public void onFailure(Call<List<SearchResult>> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                if (call.isCanceled()) {
                    Log.d(TAG, "call is cancelled");

                } else if (mNetworkUtils.isOnline(SearchActivity.this)) {
                    Log.d(TAG, "error loading from API");
                    no_results.setVisibility(View.VISIBLE);
                    Snackbar.make((CoordinatorLayout) findViewById(R.id.search_layout_main), getString(R.string.Search_no_records), Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    Log.d(TAG, "Check your network connection");
                    Snackbar.make((CoordinatorLayout) findViewById(R.id.search_layout_main), getString(R.string.no_internet_top), Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (searchCall != null) {
            if (!searchCall.isExecuted()) {
                searchCall.cancel();
            }
        }
        //cancel the timer
        timer.cancel();
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
        //cancel the timer
        timer.cancel();
    }
    @Override
    public void onResume() {
        if (adView != null) {
            adView.resume();
        }
        super.onResume();
        //retrieve the remaining time
        timer = new AdCountDownTimer(PreferenceUtils.getAdTime(),1000);
        //restart the timer
        timer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adView!=null) {
            final ViewGroup viewGroup = (ViewGroup) adView.getParent();
            if (viewGroup != null)
            {
                viewGroup.removeView(adView);
            }
            adView.removeAllViews();
            adView.destroy();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.search); // get my MenuItem with placeholder submenu
        searchMenuItem.expandActionView(); // Expand the search menu item in order to show by default the query
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                onBackPressed();
                finish();
                return true;
            }
        });
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint(getResources().getString(R.string.search_text));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("Search: ", query);
                searchList.setVisibility(View.GONE);
                no_results.setVisibility(View.GONE);
//                end_msg.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }


    private class AdCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public AdCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //callback for every tick interval
            PreferenceUtils.setAdTime(millisUntilFinished);
        }

        @Override
        public void onFinish() {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Log.d(TAG, "The interstitial wasn't loaded yet.");
            }
        }
    }

}
