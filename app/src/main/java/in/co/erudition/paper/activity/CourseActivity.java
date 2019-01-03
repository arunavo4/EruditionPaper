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

import com.erudition.polygonprogressbar.NSidedProgressBar;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.w3c.dom.Text;

import in.co.erudition.paper.R;
import in.co.erudition.paper.adapter.CourseAdapter;
import in.co.erudition.paper.data.model.PresetResponseCode;
import in.co.erudition.paper.data.model.UniversityCourse;
import in.co.erudition.paper.data.remote.BackendService;
import in.co.erudition.paper.misc.ItemOffsetDecoration;
import in.co.erudition.paper.network.NetworkUtils;
import in.co.erudition.paper.util.ApiUtils;
import in.co.erudition.paper.util.ConverterUtils;
import in.co.erudition.paper.util.PreferenceUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseActivity extends AppCompatActivity {

    private CourseAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private BackendService mService;
    private Call<UniversityCourse> call;
    private NetworkUtils mNetworkUtils = new NetworkUtils();

    private NSidedProgressBar mProgressBar;
    private LinearLayout mCourseList;
    private FloatingActionMenu fab;
    private Intent intent;
    private String params[];
    private AdView adView;
    private Dialog dialog;
    private boolean insetsApplied = false;
    private int state_selector;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView title;
    private String TAG = "CourseActivity";
//    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_old);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

//        View ad_spacer = (View) findViewById(R.id.nav_spacer_ad);

        //Load Ads
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        //Init section codes
        params = new String[]{"0", "0", "0", "0"};
        params[0] = getIntent().getStringExtra("UniversityActivity.EXTRA_BoardCode");

        mProgressBar = (NSidedProgressBar) findViewById(R.id.progressBar_course);
        TextView mChooseTV = (TextView) findViewById(R.id.choose_tv);
        title = (TextView) findViewById(R.id.university_name_tv);
//        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh2);
        mCourseList = (LinearLayout) findViewById(R.id.course_list);

//        mSwipeRefreshLayout.setColorSchemeResources(R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4);

        //Swipe to Refresh
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
////                mProgressBar.setVisibility(View.VISIBLE);
//                loadCourses();
////                mSwipeRefreshLayout.setRefreshing(false);
//            }
//        });

        // To set the background of the activity go below the StatusBar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorBlack25alpha));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Log.d("Width",String.valueOf(ConverterUtils.convertPxToDp(this,width)));
        /*
            Adjusting the Status bar margin for Different notches
         */
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        if (Build.VERSION.SDK_INT >= 20) {
            ViewCompat.setOnApplyWindowInsetsListener(appBarLayout, (View v, WindowInsetsCompat insets) -> {
                if (!insetsApplied) {
                    v.getLayoutParams().height -= getResources().getDimensionPixelSize(R.dimen.status_bar_height);
                    v.getLayoutParams().height += insets.getSystemWindowInsetTop();

                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                    params.topMargin = insets.getSystemWindowInsetTop();
                    v.invalidate();
                    v.requestLayout();

                    params = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
                    params.bottomMargin = insets.getSystemWindowInsetBottom();
                    fab.invalidate();
                    fab.requestLayout();

                    params = (ViewGroup.MarginLayoutParams) adView.getLayoutParams();
                    params.bottomMargin = insets.getSystemWindowInsetBottom();
                    adView.invalidate();
                    adView.requestLayout();

                    insetsApplied = true;
                }

//                params = (ViewGroup.MarginLayoutParams) ad_spacer.getLayoutParams();
//                params.bottomMargin = insets.getSystemWindowInsetBottom();
//                ad_spacer.requestLayout();

                return insets.consumeSystemWindowInsets();
            });
        }

        //Check if its in not active state
        state_selector = 0;
        String state = getIntent().getStringExtra("UniversityActivity.EXTRA_State");
        if (state!=null) {
            if (state.contentEquals("Not Active")) {
                state_selector = 1;
            }
        }
        /**
         * instantiate the floating action buttons
         */

        fab = (FloatingActionMenu) findViewById(R.id.fab_menu);

        FloatingActionButton fab_recent = (FloatingActionButton) findViewById(R.id.fab_recent);
        FloatingActionButton fab_offline = (FloatingActionButton) findViewById(R.id.fab_offline);
        FloatingActionButton fab_bookmark = (FloatingActionButton) findViewById(R.id.fab_bookmarks);

        intent = new Intent(this, PaperActivity.class);
        intent.putExtra("FROM", "action_fab");

        fab_recent.setOnClickListener(v -> {
            intent.putExtra("Title", "Recent Papers");
            fab.close(true);
            startActivity(intent);
        });
        fab_offline.setOnClickListener(v -> {
            intent.putExtra("Title", "Offline");
            fab.close(true);
            startActivity(intent);
        });
        fab_bookmark.setOnClickListener(v -> {
            intent.putExtra("Title", "Bookmarks");
            fab.close(true);
            startActivity(intent);
        });

        setUpCustomFabMenuAnimation();


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
//        toolbar.setTitle(getIntent().getStringExtra("UniversityActivity.EXTRA_University_NAME"));
        toolbar.setTitle(R.string.blank);
        title.setText(getIntent().getStringExtra("UniversityActivity.EXTRA_University_FULL_NAME"));
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        try {
            Log.d("ImageView: ", getIntent().getStringExtra("UniversityActivity.EXTRA_PHOTO_URL"));
            Log.d("mUniversityNameTV: ", getIntent().getStringExtra("UniversityActivity.EXTRA_University_NAME"));
        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e) {
            Log.e("Exception", e.toString());
        }

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_container);
        try {
//            collapsingToolbarLayout.setTitle(getIntent().getStringExtra("UniversityActivity.EXTRA_University_FULL_NAME"));
            collapsingToolbarLayout.setTitle(getIntent().getStringExtra("UniversityActivity.EXTRA_University_NAME"));
            collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(),"font/source_sans_pro_semibold.ttf"));
        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e) {
            Log.e("Exception", e.toString());
        }

        //Loading Starts
        mProgressBar.setVisibility(View.VISIBLE);

        mService = ApiUtils.getBackendService();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_university_selected);
        mAdapter = new CourseAdapter(this, new UniversityCourse(), params, mChooseTV, title, collapsingToolbarLayout, new CourseAdapter.CourseItemListener() {
            @Override
            public void onUniversityClick(String id) {
                if (mAdapter.getState()!=1){
                    mProgressBar.setVisibility(View.VISIBLE);
                    mCourseList.setVisibility(View.INVISIBLE);
                    loadCourses();
                }else {
                    showDialogSoon();
//                    loadCourses();
                }
                if (fab.isMenuButtonHidden()) {
                    fab.showMenuButton(true);
                }
            }
        });

        int span = getResources().getInteger(R.integer.grid_span_count);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, span);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        //mRecyclerView.setHasFixedSize(true);
//        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset2);
//        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.d(TAG, "done adapter");

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && !fab.isMenuButtonHidden()) {
                    fab.hideMenuButton(true);
                } else if (dy < 0 && fab.isMenuButtonHidden()) {
                    fab.showMenuButton(true);
                }
            }
        });

        Log.d(TAG, "loading Courses");
        if (mAdapter.getState()==1 || state_selector==1){
            showDialogSoon();
//            loadCourses();
        }else {
            loadCourses();
        }
    }

    private void loadCourses() {

        Log.d(TAG, "loadCoursesMethod");

        switch (mAdapter.getSelector()) {
            case 0:
                call = mService.getCourses(params[0]);
                break;
            case 1:
                call = mService.getCourses(params[0], params[1]);
                break;
            case 2:
                call = mService.getCourses(params[0], params[1], params[2]);
                break;
        }
        Log.d("Call", call.request().toString());
        call.enqueue(new Callback<UniversityCourse>() {

            @Override
            public void onResponse(Call<UniversityCourse> call, Response<UniversityCourse> response) {
                Log.d("Call", call.request().toString());
                if (response.isSuccessful()) {
                    Log.d(TAG, "issuccess");

                    mProgressBar.setVisibility(View.GONE);
                    mCourseList.setVisibility(View.VISIBLE);
//                    mSwipeRefreshLayout.setRefreshing(false);

                    Log.d("Response Body", response.body().toString());
                    mAdapter.updateUniversitiesFull(response.body());
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
            public void onFailure(Call<UniversityCourse> call, Throwable t) {
                if (call.isCanceled()) {
                    Log.d(TAG, "call is cancelled");

                } else if (mNetworkUtils.isOnline(getApplicationContext())) {
                    Log.d("MainActivity", "error loading from API");
                    showDialogError();
                } else {
                    Log.d("MainActivity", "Check your network connection");
                    showDialogNoNet();
                }

                mProgressBar.setVisibility(View.GONE);
//                mSwipeRefreshLayout.setRefreshing(false);
                mCourseList.setVisibility(View.GONE);
            }
        });

    }

    private void notifyMeCall(){
        Log.d(TAG, "notifyMeMethod");

        Call<PresetResponseCode> notifyCall;
        String eid = PreferenceUtils.getEid();

        switch (mAdapter.getSelector()) {
            case 0:
                notifyCall = mService.notifyMe(eid,params[0]);
                break;
            case 1:
                notifyCall = mService.notifyMe(eid,params[0], params[1]);
                break;
            case 2:
                notifyCall = mService.notifyMe(eid,params[0], params[1], params[2]);
                break;
            default:
                notifyCall = mService.notifyMe(eid,params[0]);
        }
        Log.d("Call", notifyCall.request().toString());
        notifyCall.enqueue(new Callback<PresetResponseCode>() {
            @Override
            public void onResponse(Call<PresetResponseCode> call, Response<PresetResponseCode> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "issuccess");
                    Log.d("Response Body", response.body().toString());
                    Log.d(TAG, "API success");

                    //Toast
                    Toast.makeText(getApplicationContext(),getString(R.string.notify_msg),Toast.LENGTH_LONG).show();
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
                }
            }

            @Override
            public void onFailure(Call<PresetResponseCode> call, Throwable t) {
                if (call.isCanceled()) {
                    Log.d(TAG, "call is cancelled");

                } else if (mNetworkUtils.isOnline(getApplicationContext())) {
                    Log.d("MainActivity", "error loading from API");
                    showDialogError();
                } else {
                    Log.d("MainActivity", "Check your network connection");
                    showDialogNoNet();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (state_selector==0) {
            if (mAdapter.getSelector() == 0) {
                super.onBackPressed();
            } else {
                mAdapter.setSelectorOnBackPressed();
            }
        }
        if (!call.isExecuted()) {
            call.cancel();
        }
        if (dialog!=null){
            if (dialog.isShowing()){
                dialog.cancel();
            }
        }
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }
    @Override
    public void onResume() {
        if (adView != null) {
            adView.resume();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (adView!=null) {
            final ViewGroup viewGroup = (ViewGroup) adView.getParent();
            if (viewGroup != null)
            {
                viewGroup.removeView(adView);
            }
            adView.removeAllViews();
            adView.destroy();
        }
        super.onDestroy();
    }

    private void onRetryLoadCourses() {
        //call load Courses
        mProgressBar.setVisibility(View.VISIBLE);
        mCourseList.setVisibility(View.VISIBLE);
        Log.d(TAG, "retrying loading Courses");
        loadCourses();
    }


    /**
     * Method to inflate the dialog and show it.
     */
    private void showDialogNoNet() {
        View view = getLayoutInflater().inflate(R.layout.dialog_no_internet, null);

        Button btn_retry = (Button) view.findViewById(R.id.btn_retry);

        dialog = new Dialog(CourseActivity.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        dialog.show();

        btn_retry.setOnClickListener(v -> {
            //retry and close dialogue
            if (dialog.isShowing()) {
                dialog.cancel();
                onRetryLoadCourses();
            }
        });
    }

    private void showDialogError() {
        View view = getLayoutInflater().inflate(R.layout.dialog_page_not_found, null);

        Button btn_go_back = (Button) view.findViewById(R.id.btn_go_back);
        ImageView btn_back = (ImageView) view.findViewById(R.id.btn_back);

        dialog = new Dialog(CourseActivity.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor);
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

    private void showDialogSoon() {
        View view = getLayoutInflater().inflate(R.layout.dialog_coming_soon, null);

        Button btn_notify = (Button) view.findViewById(R.id.btn_notify);
        ImageView btn_go_back = (ImageView) view.findViewById(R.id.btn_go_back);

        dialog = new Dialog(CourseActivity.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        dialog.show();

        btn_go_back.setOnClickListener(v -> {
            if (dialog.isShowing()) {
                dialog.cancel();
//                onBackPressed();
            }
        });

        btn_notify.setOnClickListener(v -> {
            //call notify
            notifyMeCall();
            if (dialog.isShowing()) {
                dialog.cancel();
            }
//            onBackPressed();
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
            //close the current Activity
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

        final Drawable plusDrawable = ContextCompat.getDrawable(CourseActivity.this,
                R.drawable.ic_expand_close);
        expandAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fab.getMenuIconView().setImageDrawable(plusDrawable);
                fab.setIconToggleAnimatorSet(mCloseAnimatorSet);
            }
        });

        final Drawable mapDrawable = ContextCompat.getDrawable(CourseActivity.this,
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
}
