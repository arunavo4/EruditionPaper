package in.co.erudition.paper.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.erudition.polygonprogressbar.NSidedProgressBar;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import iammert.com.view.scalinglib.ProgressOutlineProvider;
import iammert.com.view.scalinglib.ScalingLayout;
import iammert.com.view.scalinglib.ScalingLayoutBehavior;
import iammert.com.view.scalinglib.ScalingLayoutListener;
import in.co.erudition.avatar.AvatarPlaceholder;
import in.co.erudition.avatar.AvatarView;
import in.co.erudition.paper.Erudition;
import in.co.erudition.paper.R;
import in.co.erudition.paper.adapter.UniversityAdapter;
import in.co.erudition.paper.carousel.CarouselPicker;
import in.co.erudition.paper.data.model.University;
import in.co.erudition.paper.data.remote.BackendService;
import in.co.erudition.paper.misc.ItemOffsetDecoration;
import in.co.erudition.paper.network.NetworkUtils;
import in.co.erudition.paper.util.ApiUtils;
import in.co.erudition.paper.util.AvatarGlideLoader;
import in.co.erudition.paper.util.ConverterUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private UniversityAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private BackendService mService;
    private Call<List<University>> call;
    private NetworkUtils mNetworkUtils = new NetworkUtils();

    private InterstitialAd mInterstitialAd;
    private AdCountDownTimer timer;

    private NSidedProgressBar mProgressBar;
    private LinearLayout mUniversityList;
    private FloatingActionMenu fab;
    private Intent intent;
    private boolean nav_set[] = new boolean[2];
    private int insetTop = 0;
    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor mPrefsEdit;

    private ProgressOutlineProvider pop;        //Bug in android pie

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);

//        TextView tv = (TextView) findViewById(R.id.app_name_tv_1);
//        setUpCustomText(getResources().getString(R.string.app_name), tv);
//        toolbar.setTitle("Erudition Paper");
        setSupportActionBar(toolbar);

        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

        mProgressBar = (NSidedProgressBar) findViewById(R.id.progressBar_universities);
        mUniversityList = (LinearLayout) findViewById(R.id.university_list);

        final View space = (View) findViewById(R.id.spacer_top);

//        Setup Interstitial Ads --> only once at startup
//        Interstitial video ads
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_ad_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        //load ads in advance
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                //Set the timer Again
                timer = new AdCountDownTimer(600000, 1000);
                timer.start();
            }

        });

        //Set a timer for 10 min
        timer = new AdCountDownTimer(600000, 1000);
        timer.start();


        //Search view
        ScalingLayout search_view = (ScalingLayout) findViewById(R.id.scalingLayout);
        search_view.setOnClickListener(v -> openSearchActivity());
        search_view.setListener(new ScalingLayoutListener() {
            @Override
            public void onCollapsed() {
                Log.d("layout", "Collapsed");
            }

            @Override
            public void onExpanded() {
                Log.d("layout", "Expanded");
            }

            @Override
            public void onProgress(float progress) {
                Log.d("Progress", String.valueOf(progress));
                space.setScaleY(2 + Math.abs(progress));

                // Workaround for a BUG in android 9 (behavior change)
                if (pop != null) {
                    pop.updateProgress(search_view.getSettings().getMaxRadius(), progress);
                }
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pop = new ProgressOutlineProvider();
            search_view.setOutlineProvider(pop);
            search_view.setClipToOutline(true);

            search_view.post(() -> {
                pop.updateProgress(search_view.getSettings().getMaxRadius(), 1);
                search_view.invalidateOutline();
            });
        }

        CardView set_sem_btn = (CardView) findViewById(R.id.set_sem_btn);

        set_sem_btn.setOnClickListener(v -> {
            //Toast
            Toast.makeText(MainActivity.this,getString(R.string.upcoming_feature),Toast.LENGTH_LONG).show();
        });

        /**
         * Set up the Carousel
         */
//        CarouselPicker carouselPicker = (CarouselPicker) findViewById(R.id.carousel);
//        // Case 1 : To populate the picker with images
//        List<CarouselPicker.PickerItem> imageItems = new ArrayList<>();
//        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.img_2));
//        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.img_1));
//        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.img_3));
//        //Create an adapter
//        CarouselPicker.CarouselViewAdapter imageAdapter = new CarouselPicker.CarouselViewAdapter(this, imageItems, 0);
//        //Set the adapter
//        carouselPicker.setAdapter(imageAdapter);
//
//        carouselPicker.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//
//        //To set item
//        carouselPicker.setCurrentItem(1, true);
//
//
//        /**
//         * Setup the pager indicator
//         */
//        PageIndicatorView pageIndicatorView = (PageIndicatorView) findViewById(R.id.pageIndicatorView);
//        pageIndicatorView.setViewPager(carouselPicker);
//        pageIndicatorView.setSelection(1);


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


        // To set the background of the activity go below the StatusBar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorBlack75alpha));
        }

        /*
            If there is a notch then adjust the size of status bar
            and the corresponding views -> nav_right ,nav_main ,app_bar_main
            The nav_headers are being set in the onDrawerOpened method
            Even the SearchView position has been adjusted
         */
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.my_appbar_container);

        if (Build.VERSION.SDK_INT >= 20) {
            ViewCompat.setOnApplyWindowInsetsListener(appBarLayout, (View v, WindowInsetsCompat insets) -> {
                v.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.app_bar_layout_height);
                v.getLayoutParams().height -= getResources().getDimensionPixelSize(R.dimen.status_bar_height);
                v.getLayoutParams().height += insets.getSystemWindowInsetTop();
                insetTop = insets.getSystemWindowInsetTop();

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                Log.d("toolbar top Margin: b", String.valueOf(params.topMargin));
                params.topMargin = insets.getSystemWindowInsetTop();
                Log.d("toolbar top Margin: a", String.valueOf(params.topMargin));
                v.invalidate();
                v.requestLayout();

                float toolbarHeight = getResources().getDimensionPixelSize(R.dimen.app_bar_height);
                toolbarHeight += insets.getSystemWindowInsetTop();

                CoordinatorLayout.LayoutParams search_viewLayoutParams =
                        (CoordinatorLayout.LayoutParams) search_view.getLayoutParams();
                search_viewLayoutParams.setBehavior(new ScalingLayoutBehavior(search_view.getContext(), null, toolbarHeight));
                search_view.requestLayout();

                params = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
                params.bottomMargin = insets.getSystemWindowInsetBottom();
                fab.invalidate();
                fab.requestLayout();

                Log.d("Status Bar height i:", String.valueOf(ConverterUtils.convertPxToDp(this, insets.getSystemWindowInsetTop())));
                return insets.consumeSystemWindowInsets();
            });

        }


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        nav_set[0] = nav_set[1] = false;

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                TextView name_tv = (TextView) drawerView.findViewById(R.id.nav_user_name);
                TextView email_tv = (TextView) drawerView.findViewById(R.id.nav_user_email);
                AvatarView avatar = (AvatarView) drawerView.findViewById(R.id.nav_avatar);
                LinearLayout nav_header_main = (LinearLayout) findViewById(R.id.nav_header_main);
                LinearLayout nav_header_right = (LinearLayout) findViewById(R.id.nav_header_right);
                View fake_status_bar = (View) findViewById(R.id.fake_status_bar);

                if (name_tv != null && email_tv != null) {
                    if (name_tv.getText().length() == 0 || email_tv.getText().length() == 0 || mPrefs.getBoolean("Avatar Updated", false)) {
                        String name = mPrefs.getString("FirstName", "Android") + " " + mPrefs.getString("LastName", "Studio");
                        name_tv.setText(name);

                        String email = mPrefs.getString("Email", "android.studio@android.com");
                        email_tv.setText(email);

                        String image_uri = getProfileAvatar();

                        AvatarPlaceholder placeholder = new AvatarPlaceholder(MainActivity.this, getInitials(name));

                        AvatarGlideLoader imageLoader = new AvatarGlideLoader(getInitials(name));
                        imageLoader.loadImage(avatar, placeholder, image_uri);

                        mPrefsEdit = mPrefs.edit();
                        mPrefsEdit.putBoolean("Avatar Updated", false);
                        mPrefsEdit.apply();

                        avatar.setOnClickListener(v -> {
                            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                            startActivity(intent);
                        });

                        Log.d("On Drawer Opened: ", "Method called");
                    }
                }

                //set the right and main nav_headers
                if (!nav_set[0]) {

                    if (nav_header_main != null) {
                        nav_header_main.getLayoutParams().height -= getResources().getDimensionPixelSize(R.dimen.status_bar_height);
                        nav_header_main.getLayoutParams().height += insetTop;
                        nav_header_main.invalidate();
                        nav_header_main.requestLayout();

                        nav_set[0] = true;
                    }
                }
                if (!nav_set[1]) {
                    if (nav_header_right != null && fake_status_bar != null) {
                        nav_header_right.getLayoutParams().height -= getResources().getDimensionPixelSize(R.dimen.status_bar_height);
                        nav_header_right.getLayoutParams().height += insetTop;

                        fake_status_bar.getLayoutParams().height = insetTop;
                        nav_header_right.invalidate();
                        nav_header_right.requestLayout();

                        nav_set[1] = true;
                    }
                }

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Loading Starts
        mProgressBar.setVisibility(View.VISIBLE);

        mService = ApiUtils.getBackendService();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_universities);
        mAdapter = new UniversityAdapter(this, new ArrayList<University>(), id -> Toast.makeText(MainActivity.this, "Post id is" + id, Toast.LENGTH_SHORT).show());

        int span = getResources().getInteger(R.integer.grid_span_count);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, span);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        //mRecyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.d("MainActivity", "done adapter");

        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll_main);
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY && !fab.isMenuButtonHidden()) {
                //DOWN SCROLL
                fab.hideMenuButton(true);
            }
            if (scrollY < oldScrollY && fab.isMenuButtonHidden()) {
                fab.showMenuButton(true);
            }
        });

        Log.d("MainActivity", "loading Universities");
        if (mNetworkUtils.isOnline(MainActivity.this)) {
            loadUniversities();
        } else {
            showDialogNoNet();
        }

    }

    /*
     * Return user short name
     */
    private static String getInitials(String name) {

        String[] strings = name.split(" ");//no i18n
        String shortName;
        if (strings.length == 1) {
            shortName = strings[0].substring(0, 2);
        } else {
            shortName = strings[0].substring(0, 1) + strings[1].substring(0, 1);
        }
        return shortName.toUpperCase();
    }

    private String getProfileAvatar() {

        String uri_profile_img = mPrefs.getString("ProfileImage", "");
        if (!uri_profile_img.contentEquals("")) {
            return uri_profile_img;
        } else {
            uri_profile_img = mPrefs.getString("Avatar", "");
        }

        return uri_profile_img;
    }

    private void setUpCustomText(String text, TextView textView) {
        // Initialize a new SpannableStringBuilder instance
        SpannableString ssBuilder = new SpannableString(text);

        // Initialize a new TextAppearanceSpan to display custom styled text
        TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(MainActivity.this, R.style.AppNameTextView);

        // Initialize another new TextAppearanceSpan to display custom styled text
        TextAppearanceSpan anotherTextAppearanceSpan = new TextAppearanceSpan(MainActivity.this, R.style.AppNameTextViewBold);

        // Apply the custom text appearance to the span
        ssBuilder.setSpan(
                textAppearanceSpan, // Span to add
                0, // Start of the span (inclusive)
                9, // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
        );

        // Apply another custom text appearance to the span
        ssBuilder.setSpan(
                anotherTextAppearanceSpan,
                10,
                15,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        textView.setText(ssBuilder);
    }

    private void openSearchActivity() {
        Intent search_intent = new Intent(this, SearchActivity.class);
        startActivity(search_intent);
    }

    private void loadUniversities() {

        Log.d("MainActivity", "loadUniversitiesMethod");

        call = mService.getUniversity();
        call.enqueue(new Callback<List<University>>() {

            @Override
            public void onResponse(Call<List<University>> call, Response<List<University>> response) {
                Log.d("Call", call.request().toString());
                if (response.isSuccessful()) {
                    Log.d("MainActivity", "issuccess");

                    mProgressBar.setVisibility(View.INVISIBLE);

                    Log.d("Response Body", response.body().toString());
                    mAdapter.updateUniversities(response.body());
                    Log.d("MainActivity", "API success");
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
            public void onFailure(Call<List<University>> call, Throwable t) {
                if (call.isCanceled()) {
                    Log.d("MainActivity", "call is cancelled");

                } else if (mNetworkUtils.isOnline(MainActivity.this)) {
                    Log.d("MainActivity", "error loading from API");
                    showDialogError();
                } else {
                    Log.d("MainActivity", "Check your network connection");
                    showDialogNoNet();
                }
                mProgressBar.setVisibility(View.INVISIBLE);
                mUniversityList.setVisibility(View.GONE);
            }
        });

    }

    private void onRetryLoadUniversities() {
        //call load Universities
        mProgressBar.setVisibility(View.VISIBLE);
        mUniversityList.setVisibility(View.VISIBLE);
        Log.d("MainActivity", "retrying loading universities");
        if (mNetworkUtils.isOnline(MainActivity.this)) {
            loadUniversities();
        } else {
            showDialogNoNet();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_notification) {
            drawer.openDrawer(GravityCompat.END);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
//        } else if (id == R.id.nav_order_history) {
//            Intent intent = new Intent(this, OrderActivity.class);
//            startActivity(intent);
//        } else if (id == R.id.nav_offers) {
//            Intent intent = new Intent(this, OfferActivity.class);
//            startActivity(intent);
//        } else if (id == R.id.nav_rewards) {
//            Intent intent = new Intent(this, RewardActivity.class);
//            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(this, RewardHistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_books) {
            Intent intent = new Intent(this, BookActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        ///The dialogs and webView Activities
        else if (id == R.id.nav_pp) {
            Intent intent = new Intent(MainActivity.this, WebviewActivity.class);
            intent.putExtra("Webview.Title", getString(R.string.privacy_p));
            intent.putExtra("Webview.Address", getString(R.string.pp_url));
            startActivity(intent);
        } else if (id == R.id.nav_tos) {
            Intent intent = new Intent(MainActivity.this, WebviewActivity.class);
            intent.putExtra("Webview.Title", getString(R.string.terms_of_service));
            intent.putExtra("Webview.Address", getString(R.string.tos_url));
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, WebviewActivity.class);
            intent.putExtra("Webview.Title", getString(R.string.about));
            intent.putExtra("Webview.Address", getString(R.string.about_url));
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Method to inflate the dialog and show it.
     */
    private void showDialogNoNet() {
        View view = getLayoutInflater().inflate(R.layout.dialog_no_internet, null);

        Button btn_retry = (Button) view.findViewById(R.id.btn_retry);

        final Dialog dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        dialog.show();

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retry and close dialogue
                if (dialog.isShowing()) {
                    dialog.cancel();
                    onRetryLoadUniversities();
                }
            }
        });
    }

    private void showDialogError() {
        View view = getLayoutInflater().inflate(R.layout.dialog_error, null);

        Button btn_go_back = (Button) view.findViewById(R.id.btn_go_back);

        final Dialog dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        dialog.show();

        btn_go_back.setOnClickListener(v -> {
            //retry and close dialogue
            if (dialog.isShowing()) {
                dialog.cancel();
                onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            Log.d("Timer","cancelled");
        }
        super.onDestroy();
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

        final Drawable plusDrawable = ContextCompat.getDrawable(MainActivity.this,
                R.drawable.ic_expand_close);
        expandAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fab.getMenuIconView().setImageDrawable(plusDrawable);
                fab.setIconToggleAnimatorSet(mCloseAnimatorSet);
            }
        });

        final Drawable mapDrawable = ContextCompat.getDrawable(MainActivity.this,
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

        }

        @Override
        public void onFinish() {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Log.d("MainActivity", "The interstitial wasn't loaded yet.");
            }
        }
    }

}
