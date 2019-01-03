package in.co.erudition.paper.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.erudition.polygonprogressbar.NSidedProgressBar;
import com.google.android.gms.ads.AdView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.co.erudition.paper.R;
import in.co.erudition.paper.adapter.PaperAdapter;
import in.co.erudition.paper.data.model.Chapter;
import in.co.erudition.paper.data.model.Year;
import in.co.erudition.paper.data.remote.BackendService;
import in.co.erudition.paper.misc.ItemOffsetDecoration;
import in.co.erudition.paper.network.NetworkUtils;
import in.co.erudition.paper.util.ApiUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaperActivity extends AppCompatActivity {

    private PaperAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private BackendService mService;
    private Call<List<Year>> yearCall;
    private Call<List<Chapter>> chapCall;
    private NetworkUtils mNetworkUtils = new NetworkUtils();

    private NSidedProgressBar mProgressBar;
    private LinearLayout mPaperList;
    private LinearLayout mNoPaper;

    private FloatingActionMenu fab;
    private Intent intent;

    private AdView mAdView;
//    private InterstitialAd mInterstitialAd;
//    private AdCountDownTimer timer;

    private TextView mTextView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Dialog dialog;

    private boolean papers = true;
    private int select = 0;

    private String TAG = "PaperActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Toolbar toolbar_textView = (Toolbar) findViewById(R.id.toolbar2);

        mProgressBar = (NSidedProgressBar) findViewById(R.id.progressBar_paper);
        mPaperList = (LinearLayout) findViewById(R.id.paper_list);
        mNoPaper = (LinearLayout) findViewById(R.id.no_paper_found);

        select = getIntent().getIntExtra("Selection Activity: Selection", -1);

        /*
            ON the basis of select change the width to *match parent*
         */
        if (select == 0) {
            mPaperList.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        }

        //Load Ads
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //Setup Interstitial Ads --> only once at startup
        //Interstitial ads
//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//
//        //load ads in advance
//        mInterstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdClosed() {
//                // Load the next interstitial.
//                mInterstitialAd.loadAd(new AdRequest.Builder().build());
//                //Set the timer Again
//                timer = new AdCountDownTimer(600000, 1000);
//                timer.start();
//            }
//
//        });
//
//        //Set a timer for 1 min
//        timer = new AdCountDownTimer(600000, 1000);
//        timer.start();

        /**
         * instantiate the floating action buttons
         */

        fab = (FloatingActionMenu) findViewById(R.id.fab_menu);

        FloatingActionButton fab_recent = (FloatingActionButton) findViewById(R.id.fab_recent);
        FloatingActionButton fab_offline = (FloatingActionButton) findViewById(R.id.fab_offline);
        FloatingActionButton fab_bookmark = (FloatingActionButton) findViewById(R.id.fab_bookmarks);

        intent = new Intent(this, PaperActivity.class);
        intent.putExtra("FROM", "action_fab");

        fab_recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("Title", "Recent Papers");
                fab.close(true);
                startActivity(intent);
//                timer.cancel();
                finish();
            }
        });
        fab_offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("Title", "Offline");
                fab.close(true);
                startActivity(intent);
//                timer.cancel();
                finish();
            }
        });
        fab_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("Title", "Bookmarks");
                fab.close(true);
                startActivity(intent);
//                timer.cancel();
                finish();
            }
        });

        setUpCustomFabMenuAnimation();


        // To set the background of the activity go below the StatusBar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorBlack25alpha));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
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

        /*
            Adjusting the Status bar margin for Different notches
         */
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        if (Build.VERSION.SDK_INT >= 20) {
            ViewCompat.setOnApplyWindowInsetsListener(appBarLayout, (View v, WindowInsetsCompat insets) -> {
                v.getLayoutParams().height -= getResources().getDimensionPixelSize(R.dimen.status_bar_height);
                v.getLayoutParams().height += insets.getSystemWindowInsetTop();

                String from = getIntent().getStringExtra("FROM");
                ViewGroup.MarginLayoutParams params;
                if (from.contentEquals("action_fab")) {
                    params = (ViewGroup.MarginLayoutParams) toolbar_textView.getLayoutParams();
                } else {
                    params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                }
                params.topMargin = insets.getSystemWindowInsetTop();
                v.invalidate();
                v.requestLayout();

                params = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
                params.bottomMargin = insets.getSystemWindowInsetBottom();
                fab.invalidate();
                fab.requestLayout();

                return insets.consumeSystemWindowInsets();
            });
        }


        mTextView = (TextView) findViewById(R.id.paper_name_tv);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_container);
        collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(),"font/source_sans_pro_semibold.ttf"));
        collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(),"font/source_sans_pro_semibold.ttf"));
        try {
            String from = getIntent().getStringExtra("FROM");

            if (from.contentEquals("action_fab")) {
                toolbar_textView.setNavigationIcon(bg);
                toolbar_textView.setTitle(getIntent().getStringExtra("UniversityActivity.EXTRA_Subject_NAME"));
                setSupportActionBar(toolbar_textView);

                toolbar_textView.setNavigationOnClickListener(v -> onBackPressed());

                String str = getIntent().getStringExtra("Title");
                if (collapsingToolbarLayout.getVisibility() == View.VISIBLE) {
                    collapsingToolbarLayout.setVisibility(View.GONE);
                    if (mTextView.getVisibility() == View.GONE) {
                        mTextView.setVisibility(View.VISIBLE);
                        toolbar_textView.setVisibility(View.VISIBLE);
                    }
                }

                if (str.contentEquals("Offline")) {
                    mTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_offline, 0, 0, 0);
                } else if (str.contentEquals("Bookmarks")) {
                    mTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bookmark_24px, 0, 0, 0);
                } else if (str.contentEquals("Recent Papers")) {
                    mTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_access_time_black_24dp, 0, 0, 0);
                }
                //TODO: No papers for now
                mTextView.setText(getIntent().getStringExtra("Title"));

                papers = false;
            }
            //For normal selection flow.
            else if (from.contentEquals("Course")) {
                toolbar.setNavigationIcon(bg);
                toolbar.setTitle(getIntent().getStringExtra("UniversityActivity.EXTRA_Subject_NAME"));
                setSupportActionBar(toolbar);

                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
                if (collapsingToolbarLayout.getVisibility() == View.GONE) {
                    collapsingToolbarLayout.setVisibility(View.VISIBLE);
                    if (mTextView.getVisibility() == View.VISIBLE) {
                        mTextView.setVisibility(View.GONE);
                        toolbar_textView.setVisibility(View.GONE);
                    }
                }
//                collapsingToolbarLayout.setTitle(getIntent().getStringExtra("CourseActivity.EXTRA_Subject_NAME"));
                String title;
                switch (select) {
                    case 0:
                        title = "Chapters";
                        break;
                    case 1:
                        title = "Year";
                        break;
                    default:
                        title = getIntent().getStringExtra("CourseActivity.EXTRA_Subject_NAME");
                }
                collapsingToolbarLayout.setTitle(title);
            }
        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e) {
            Log.e("Exception", e.toString());
        }

        //Loading Starts
        mService = ApiUtils.getBackendService();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_paper);
        mAdapter = new PaperAdapter(this, new ArrayList<Year>(), new ArrayList<Chapter>(), select, getIntent(), new PaperAdapter.PaperItemListener() {
            @Override
            public void onPaperClick(String id) {
                Toast.makeText(getApplicationContext(), "Post id is" + id, Toast.LENGTH_SHORT).show();
            }
        });

        /*
            Check if the display has more than 360dp width
            if so add offset to the papers
         */
        int span = getResources().getInteger(R.integer.grid_span_count);
        RecyclerView.LayoutManager layoutManager;
        if (select == 0) {
            layoutManager = new LinearLayoutManager(this);
        } else {
            layoutManager = new GridLayoutManager(this, span);
            ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset_paper);
            mRecyclerView.addItemDecoration(itemDecoration);
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        //mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.d(TAG, "done adapter");

        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll_paper);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY && !fab.isMenuButtonHidden()) {
                    //DOWN SCROLL
                    fab.hideMenuButton(true);
                }
                if (scrollY < oldScrollY && fab.isMenuButtonHidden()) {
                    fab.showMenuButton(true);
                }
            }
        });

        Log.d(TAG, "loading papers");
        if (papers) {
            if (select == 0) {
                mProgressBar.setVisibility(View.VISIBLE);
                loadChaps();
            } else if (select == 1) {
                mProgressBar.setVisibility(View.VISIBLE);
                loadYears();
            }
        } else {
            loadNoPapers();
        }
    }

    private void loadNoPapers() {
        Log.d(TAG, "loadNoPapersMethod");

        mProgressBar.setVisibility(View.GONE);
        if (mPaperList.getVisibility() == View.VISIBLE) {
            mPaperList.setVisibility(View.GONE);
        }
        mNoPaper.setVisibility(View.VISIBLE);
    }

    private void loadChaps() {

        Log.d(TAG, "loadChaptersMethod");
        String params[] = getIntent().getStringArrayExtra("CourseActivity.EXTRA_params");

        Log.d("Params", Arrays.toString(params));
        chapCall = mService.getChapter(params[0], params[1], params[2], params[3]);
        chapCall.enqueue(new Callback<List<Chapter>>() {
            @Override
            public void onResponse(Call<List<Chapter>> call, Response<List<Chapter>> response) {
                Log.d("Call", call.request().toString());
                if (response.isSuccessful()) {
                    Log.d(TAG, "issuccess");

                    mProgressBar.setVisibility(View.GONE);

                    Log.d("Response Body", response.body().toString());
                    mAdapter.updateChapters(response.body(), select);
                    Log.d(TAG, "API success");
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
            public void onFailure(Call<List<Chapter>> call, Throwable t) {
                if (call.isCanceled()) {
                    Log.d(TAG, "call is cancelled");

                } else if (mNetworkUtils.isOnline(getApplicationContext())) {
                    Log.d(TAG, "error loading from API");
                    showDialogError();
                } else {
                    Log.d(TAG, "Check your network connection");
                    showDialogNoNet();
                }

                mProgressBar.setVisibility(View.GONE);
                mPaperList.setVisibility(View.GONE);
            }
        });
    }

    private void loadYears() {

        Log.d(TAG, "loadYearsMethod");
        String params[] = getIntent().getStringArrayExtra("CourseActivity.EXTRA_params");
        Log.d("Params", Arrays.toString(params));

        yearCall = mService.getYear(params[0], params[1], params[2], params[3]);
        yearCall.enqueue(new Callback<List<Year>>() {

            @Override
            public void onResponse(Call<List<Year>> call, Response<List<Year>> response) {
                Log.d("Call", call.request().toString());
                if (response.isSuccessful()) {
                    Log.d(TAG, "issuccess");

                    mProgressBar.setVisibility(View.GONE);

                    Log.d("Response Body", response.body().toString());
                    mAdapter.updateYears(response.body(), select);
                    Log.d(TAG, "API success");
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
            public void onFailure(Call<List<Year>> call, Throwable t) {
                if (call.isCanceled()) {
                    Log.d(TAG, "call is cancelled");

                } else if (mNetworkUtils.isOnline(getApplicationContext())) {
                    Log.d(TAG, "error loading from API");
                    showDialogError();
                } else {
                    Log.d(TAG, "Check your network connection");
                    showDialogNoNet();
                }

                mProgressBar.setVisibility(View.GONE);
                mPaperList.setVisibility(View.GONE);
            }
        });
    }

    private void onRetryLoadPapers() {
        //call load Papers
        mProgressBar.setVisibility(View.VISIBLE);
        mPaperList.setVisibility(View.VISIBLE);
        Log.d(TAG, "retrying loading Papers");
        if (select == 0) {
            loadChaps();
        } else if (select == 1) {
            loadYears();
        } else {
            loadNoPapers();
        }
    }

    @Override
    public void onBackPressed() {
        if (dialog!=null){
            if (dialog.isShowing()){
                dialog.cancel();
            }
        }
        if (select == 0) {
            if (chapCall != null) {
                if (!chapCall.isExecuted()) {
                    chapCall.cancel();
                }
            }
        } else if (select == 1) {
            if (yearCall != null) {
                if (!yearCall.isExecuted()) {
                    yearCall.cancel();
                }
            }
        }

        super.onBackPressed();
//        timer.cancel();
    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
//        timer.cancel();
    }

    @Override
    protected void onResume() {
        if (mAdView != null) {
            mAdView.resume();
        }
        super.onResume();
        //restart the timer
//        timer.start();
    }

    @Override
    protected void onDestroy() {
        if (mAdView!=null) {
            final ViewGroup viewGroup = (ViewGroup) mAdView.getParent();
            if (viewGroup != null)
            {
                viewGroup.removeView(mAdView);
            }
            mAdView.removeAllViews();
            mAdView.destroy();
        }
        super.onDestroy();
    }

    /**
     * Method to inflate the dialog and show it.
     */
    private void showDialogNoNet() {
        View view = getLayoutInflater().inflate(R.layout.dialog_no_internet, null);

        Button btn_retry = (Button) view.findViewById(R.id.btn_retry);

        dialog = new Dialog(PaperActivity.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        dialog.show();

        btn_retry.setOnClickListener(v -> {
            //retry and close dialogue
            if (dialog.isShowing()) {
                dialog.cancel();
                onRetryLoadPapers();
            }
        });
    }

    private void showDialogError() {
        View view = getLayoutInflater().inflate(R.layout.dialog_page_not_found, null);

        Button btn_go_back = (Button) view.findViewById(R.id.btn_go_back);
        ImageView btn_back = (ImageView) view.findViewById(R.id.btn_back);

        dialog = new Dialog(PaperActivity.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        dialog.show();

        btn_back.setOnClickListener(v -> {
            if (dialog.isShowing()) {
                dialog.cancel();
                onBackPressed();
            }
        });

        btn_go_back.setOnClickListener(v -> {
            //retry and close dialogue
            if (dialog.isShowing()) {
                dialog.cancel();
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_courses_papers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close) {
            //close Activity
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Custom Animator for the fab
     */

    private static final int ANIMATION_DURATION = 200;
    private static final float ROTATION_ANGLE = -45f;
    private AnimatorSet mOpenAnimatorSet;
    private AnimatorSet mCloseAnimatorSet;

    private void setUpCustomFabMenuAnimation() {
        mOpenAnimatorSet = new AnimatorSet();
        mCloseAnimatorSet = new AnimatorSet();

        ObjectAnimator collapseAnimator = ObjectAnimator.ofFloat(fab.getMenuIconView(),
                "rotation",
                -90f + ROTATION_ANGLE, 0f);
        ObjectAnimator expandAnimator = ObjectAnimator.ofFloat(fab.getMenuIconView(),
                "rotation",
                0f, -90f + ROTATION_ANGLE);

        final Drawable plusDrawable = ContextCompat.getDrawable(PaperActivity.this,
                R.drawable.ic_expand_close);
        expandAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fab.getMenuIconView().setImageDrawable(plusDrawable);
                fab.setIconToggleAnimatorSet(mCloseAnimatorSet);
            }
        });

        final Drawable mapDrawable = ContextCompat.getDrawable(PaperActivity.this,
                R.drawable.ic_info);
        collapseAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fab.getMenuIconView().setImageDrawable(mapDrawable);
                fab.setIconToggleAnimatorSet(mOpenAnimatorSet);
            }
        });

        mOpenAnimatorSet.play(expandAnimator);
        mCloseAnimatorSet.play(collapseAnimator);

        mOpenAnimatorSet.setDuration(ANIMATION_DURATION);
        mCloseAnimatorSet.setDuration(ANIMATION_DURATION);

        fab.setIconToggleAnimatorSet(mOpenAnimatorSet);
    }

//    private class AdCountDownTimer extends CountDownTimer {
//        /**
//         * @param millisInFuture    The number of millis in the future from the call
//         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
//         *                          is called.
//         * @param countDownInterval The interval along the way to receive
//         *                          {@link #onTick(long)} callbacks.
//         */
//        public AdCountDownTimer(long millisInFuture, long countDownInterval) {
//            super(millisInFuture, countDownInterval);
//        }
//
//        @Override
//        public void onTick(long millisUntilFinished) {
//
//        }
//
//        @Override
//        public void onFinish() {
//            if (mInterstitialAd.isLoaded()) {
//                mInterstitialAd.show();
//            } else {
//                Log.d(TAG, "The interstitial wasn't loaded yet.");
//            }
//        }
//    }
}
