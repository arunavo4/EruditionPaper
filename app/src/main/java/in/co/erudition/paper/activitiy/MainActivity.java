package in.co.erudition.paper.activitiy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.rd.PageIndicatorView;

import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import iammert.com.view.scalinglib.ScalingLayout;
import iammert.com.view.scalinglib.ScalingLayoutListener;
import in.co.erudition.paper.R;
import in.co.erudition.paper.adapter.UniversityAdapter;
import in.co.erudition.paper.carousel.CarouselPicker;
import in.co.erudition.paper.data.model.University;
import in.co.erudition.paper.data.remote.BackendService;
import in.co.erudition.paper.misc.ItemOffsetDecoration;
import in.co.erudition.paper.network.NetworkUtils;
import in.co.erudition.paper.util.ApiUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private UniversityAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private BackendService mService;
    private Call<List<University>> call;
    private NetworkUtils mNetworkUtils = new NetworkUtils();

    private ProgressBar mProgressBar;
    private LinearLayout mimageView;
    private LinearLayout mUniversityList;
    private FloatingActionMenu fab;
    private Toolbar toolbar;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);

        TextView tv = (TextView) findViewById(R.id.app_name_tv_1);
        setUpCustomText(getResources().getString(R.string.app_name),tv);
        toolbar.setTitle("Erudition Paper");
        setSupportActionBar(toolbar);



        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_universities);
        mimageView = (LinearLayout) findViewById(R.id.img_404_not_found);
        mUniversityList = (LinearLayout) findViewById(R.id.university_list);


        //Retry button
        Button btn_retry = (Button) findViewById(R.id.btn_retry);

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRetryLoadUniversities();
            }
        });


        final View space = (View) findViewById(R.id.spacer_top);
        //Search view
        ScalingLayout search_view = (ScalingLayout) findViewById(R.id.scalingLayout);
        search_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchActivity();
            }
        });
        search_view.setListener(new ScalingLayoutListener() {
            @Override
            public void onCollapsed() {
                Log.d("layout","Collapsed");
            }

            @Override
            public void onExpanded() {

            }

            @Override
            public void onProgress(float progress) {
                Log.d("Progress",String.valueOf(progress));
                space.setScaleY(2 + Math.abs(progress));
            }
        });


        /**
         * Set up the Carousel
         */
        CarouselPicker carouselPicker = (CarouselPicker) findViewById(R.id.carousel);
        // Case 1 : To populate the picker with images
        List<CarouselPicker.PickerItem> imageItems = new ArrayList<>();
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.img_2));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.img_1));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.img_3));
        //Create an adapter
        CarouselPicker.CarouselViewAdapter imageAdapter = new CarouselPicker.CarouselViewAdapter(this, imageItems, 0);
        //Set the adapter
        carouselPicker.setAdapter(imageAdapter);

        carouselPicker.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

        //To set item
        carouselPicker.setCurrentItem(1,true);


        /**
         * Setup the pager indicator
         */
        PageIndicatorView pageIndicatorView = (PageIndicatorView) findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setViewPager(carouselPicker);
        pageIndicatorView.setSelection(1);


        /**
         * instantiate the floating action buttons
         */

        fab = (FloatingActionMenu) findViewById(R.id.fab_menu);

        FloatingActionButton fab_recent = (FloatingActionButton) findViewById(R.id.fab_recent);
        FloatingActionButton fab_offline = (FloatingActionButton) findViewById(R.id.fab_offline);
        FloatingActionButton fab_bookmark = (FloatingActionButton) findViewById(R.id.fab_bookmarks);

        intent = new Intent(this, PaperActivity.class);
        intent.putExtra("FROM","action_fab");

        fab_recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("Title","Recent Papers");
                startActivity(intent);
            }
        });
        fab_offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("Title","Offline");
                startActivity(intent);
            }
        });
        fab_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("Title","Bookmarks");
                startActivity(intent);
            }
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Loading Starts
        mProgressBar.setVisibility(View.VISIBLE);

        mService = ApiUtils.getBackendService();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_universities);
        mAdapter = new UniversityAdapter(this, new ArrayList<University>(), new UniversityAdapter.UniversityItemListener() {
            @Override
            public void onUniversityClick(String id) {
                Toast.makeText(MainActivity.this, "Post id is" + id, Toast.LENGTH_SHORT).show();
            }
        });

        int span = getResources().getInteger(R.integer.grid_span_count);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,span);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        //mRecyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.d("MainActivity","done adapter");

        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll_main);
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

        Log.d("MainActivity","loading Universities");
        loadUniversities();
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
        Intent search_intent = new Intent(this,SearchActivity.class);
        startActivity(search_intent);
    }

    private void loadUniversities() {

        Log.d("MainActivity", "loadUniversitiesMethod");
        // starting time
        final long start = System.currentTimeMillis();

        call = mService.getUniversity();
        call.enqueue(new Callback<List<University>>() {

            @Override
            public void onResponse(Call<List<University>> call, Response<List<University>> response) {
                Log.d("Call",call.request().toString());
                if(response.isSuccessful()) {
                    Log.d("MainActivity","issuccess");

                    // starting time
                    final long end = System.currentTimeMillis();
                    String time = String.valueOf(end-start);
                    String str = "S:" + response.message() + "  T: " + time + "ms  S: " + response.headers().get("Content-Length") + "B";

                    Toast.makeText(MainActivity.this,str,Toast.LENGTH_LONG).show();

                    mProgressBar.setVisibility(View.GONE);

                    Log.d("Response Body",response.body().toString());
                    mAdapter.updateUniversities(response.body());
                    Log.d("MainActivity", "API success");
                }else {
                    int statusCode  = response.code();
                    if(statusCode==401){
                        Log.d("StatusCode","Unauthorized");
                    }
                    if (statusCode==200){
                        Log.d("StatusCode","OK");
                    }
                    else {
                        Log.d("StatusCode", String.valueOf(statusCode));
                    }
                    // handle request errors depending on status code
                }
            }

            @Override
            public void onFailure(Call<List<University>> call, Throwable t) {
                if(call.isCanceled()){
                    Log.d("MainActivity", "call is cancelled");

                }
                else {
                    Log.d("MainActivity", "error loading from API");
                }
                String str;
                if(mNetworkUtils.isOnline(MainActivity.this)){
                    str ="Error loading from Api";
                    //Display the error image
                }
                else
                    str ="Check your network connection";

                mProgressBar.setVisibility(View.GONE);

                mimageView.setVisibility(View.VISIBLE);
                mUniversityList.setVisibility(View.GONE);
            }
        });

    }

    private void onRetryLoadUniversities() {
        //call load Universities
        mimageView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mUniversityList.setVisibility(View.VISIBLE);
        Log.d("MainActivity","retrying loading universities");
        loadUniversities();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(drawer.isDrawerOpen(GravityCompat.END)){
            drawer.closeDrawer(GravityCompat.END);
        }else {
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
        }
        else if (id==R.id.nav_order_history)
        {
            Intent intent = new Intent(this, OrderActivity.class);
            startActivity(intent);
        }
        else if (id==R.id.nav_offers)
        {
            Intent intent = new Intent(this, OfferActivity.class);
            startActivity(intent);
        }
        else if (id==R.id.nav_rewards)
        {
            Intent intent = new Intent(this, RewardActivity.class);
            startActivity(intent);
        }
        else if (id==R.id.nav_books)
        {
            Intent intent = new Intent(this, BookActivity.class);
            startActivity(intent);
        }
        else if (id==R.id.nav_help)
        {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

        ObjectAnimator collapseAnimator =  ObjectAnimator.ofFloat(fab.getMenuIconView(),
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

}
