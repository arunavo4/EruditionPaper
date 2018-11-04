package in.co.erudition.paper.activity;

import android.app.Dialog;
import android.app.SearchManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import com.erudition.polygonprogressbar.NSidedProgressBar;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.co.erudition.paper.R;
import in.co.erudition.paper.adapter.SearchAdapter;
import in.co.erudition.paper.data.model.SearchResult;
import in.co.erudition.paper.data.remote.BackendService;
import in.co.erudition.paper.network.NetworkUtils;
import in.co.erudition.paper.util.ApiUtils;
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
    private TextView result_count;
    private AdView adView;
    private NetworkUtils mNetworkUtils = new NetworkUtils();

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

        progressBar = (NSidedProgressBar) findViewById(R.id.progressBar_search);
        searchList = (LinearLayout) findViewById(R.id.search_linear_layout);
        result_count = (TextView) findViewById(R.id.result_count);
        View nav_space = (View) findViewById(R.id.nav_spacer_ad);

        progressBar.setVisibility(View.INVISIBLE);

        // To set the background of the activity go below the StatusBar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorBlack25alpha));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorBlack75alpha));
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

                params = (ViewGroup.MarginLayoutParams) nav_space.getLayoutParams();
                params.bottomMargin = insets.getSystemWindowInsetBottom();
                nav_space.invalidate();
                nav_space.requestLayout();

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

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mService = ApiUtils.getBackendService();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_search);
        mAdapter = new SearchAdapter(this, new ArrayList<SearchResult>(), getIntent(), new SearchAdapter.SearchItemListener() {
            @Override
            public void onQuesClick(String id) {

            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
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

    private void search(String query){

          double start = System.currentTimeMillis();
            searchCall = mService.search(query);

            searchCall.enqueue(new Callback<List<SearchResult>>() {
                @Override
                public void onResponse(Call<List<SearchResult>> call, Response<List<SearchResult>> response) {
                    //If successful just display that update was successful
                    Log.d("Call", call.request().toString());
                    if (response.isSuccessful()) {
                        Log.d("Search:","Successful!");
                        double end = System.currentTimeMillis();
                        double responseTime = (end-start)/1000.0;
                        int size = 0;
                        if (response.body() != null) {
                            size = response.body().size();
                        }
                        String str = "About " + String.valueOf(size) + " Results" + " (" + String.valueOf(responseTime) + ") seconds";
                        result_count.setText(str);
                        mAdapter.updateResults(response.body());
                        searchList.setVisibility(View.VISIBLE);
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
                        Log.d("SearchActivity", "call is cancelled");

                    } else if (mNetworkUtils.isOnline(SearchActivity.this)) {
                        Log.d("SearchActivity", "error loading from API");
                        Snackbar.make((CoordinatorLayout) findViewById(R.id.search_layout_main), getString(R.string.error_occurred), Snackbar.LENGTH_LONG)
                                .show();
                    } else {
                        Log.d("SearchActivity", "Check your network connection");
                        Snackbar.make((CoordinatorLayout) findViewById(R.id.search_layout_main), getString(R.string.no_internet_top), Snackbar.LENGTH_LONG)
                                .show();
                    }
                }
            });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (searchCall!=null) {
            if (!searchCall.isExecuted()) {
                searchCall.cancel();
            }
        }
    }

    @Override
    protected void onDestroy() {
        adView.removeAllViews();
        adView.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchMenuItem = menu.findItem( R.id.search ); // get my MenuItem with placeholder submenu
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
                Log.d("Search: ",query);
                searchList.setVisibility(View.GONE);
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



}
