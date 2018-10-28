package in.co.erudition.paper.activity;

import android.app.Dialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

import in.co.erudition.paper.R;
import in.co.erudition.paper.adapter.AnswerAdapter;
import in.co.erudition.paper.adapter.GroupAdapter;
import in.co.erudition.paper.data.model.Paper;
import in.co.erudition.paper.data.model.QuestionAnswer;
import in.co.erudition.paper.data.remote.BackendService;
import in.co.erudition.paper.network.NetworkUtils;
import in.co.erudition.paper.util.ApiUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnswerActivity extends AppCompatActivity {

    private AnswerAdapter mAdapter;
    private RecyclerView recyclerView;
    private BackendService mService;
    private Call<Paper> call;
    private NetworkUtils mNetworkUtils = new NetworkUtils();
    private String TAG = "AnswerActivity";

    private InterstitialAd mInterstitialAd;
    private AdCountDownTimer timer;

    private ArrayList<QuestionAnswer> data;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.my_appbar_container);
        appBarLayout.bringToFront();

        // To set the background of the activity go below the StatusBar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorBlack25alpha));
//            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorBlack75alpha));
        }

        //Setup Interstitial Ads --> only once at startup
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
                timer = new AdCountDownTimer(6000, 1000);
                timer.start();
            }

        });

        //Set a timer for 1 min
        timer = new AdCountDownTimer(6000, 1000);
        timer.start();

        /*
            Adjusting the Status bar margin for Different notches
         */
        if (Build.VERSION.SDK_INT >= 20) {
            ViewCompat.setOnApplyWindowInsetsListener(appBarLayout, (View v, WindowInsetsCompat insets) -> {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                params.topMargin = insets.getSystemWindowInsetTop();
                v.invalidate();
                v.requestLayout();

                return insets.consumeSystemWindowInsets();
            });
        }

        Drawable bg;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            bg = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
            bg.setColorFilter(ContextCompat.getColor(this, R.color.colorBlack75alpha), PorterDuff.Mode.MULTIPLY);
        } else {
            bg = VectorDrawableCompat.create(getResources(), R.drawable.ic_arrow_back_black_24dp, null);
            bg = DrawableCompat.wrap(bg);
            DrawableCompat.setTint(bg, ContextCompat.getColor(this, R.color.colorBlack75alpha));
        }

        toolbar.setNavigationIcon(bg);
//        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        data = getIntent().getParcelableArrayListExtra("QUESTION_ADAPTER.parcelData");

        //Loading Starts
//        mProgressBar.setVisibility(View.VISIBLE);

        mService = ApiUtils.getBackendService();
        recyclerView = (RecyclerView) findViewById(R.id.answers_rv);
        mAdapter = new AnswerAdapter(this, data, toolbar, new GroupAdapter.GroupItemListener() {
            @Override
            public void onGroupClick(String id) {
                Toast.makeText(AnswerActivity.this, "Post id is" + id, Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // add pager behavior
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        Log.d(TAG, "done adapter");

        /**
         * Custom Touch Behaviour
         */

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.onTouchEvent(event)) {
                    return true;
                }
                Log.d("RecyclerView", "Touched");
                return false;
            }

        });

//        Log.d(TAG,"loading paper groups");
//        loadPaperAnswers();


        //TODO: Need to fix the inner scroll problem by using the Nested prescroll
        recyclerView.setNestedScrollingEnabled(true);

        recyclerView.scrollToPosition(getIntent().getIntExtra("QUESTION_ADAPTER.position", 0));

    }

    /**
     * Currently using parcel data from the last activity
     */
    private void loadPaperAnswers() {

        Log.d(TAG, "loadPaperAnswersMethod");

        call = mService.getPaper(getIntent().getStringExtra("CourseActivity.EXTRA_University_Key"));

        call.enqueue(new Callback<Paper>() {

            @Override
            public void onResponse(Call<Paper> call, Response<Paper> response) {
                Log.d("Call", call.request().toString());
                if (response.isSuccessful()) {
                    Log.d(TAG, "issuccess");

//                    mProgressBar.setVisibility(View.GONE);
                    Log.d("Response Body", response.body().toString());
                    mAdapter.updateAnswers(response.body());
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
            public void onFailure(Call<Paper> call, Throwable t) {
                String str = "Failed";
                if (call.isCanceled()) {
                    Log.d(TAG, "call is cancelled");

                } else if (mNetworkUtils.isOnline(AnswerActivity.this)) {
                    Log.d("MainActivity", "error loading from API");
                    str = "error loading from API";
                    showDialogError();
                } else {
                    Log.d("MainActivity", "Check your network connection");
                    str = "Check your network connection";
                    showDialogNoNet();
                }

//                mProgressBar.setVisibility(View.GONE);
                Snackbar.make((CoordinatorLayout) findViewById(R.id.app_bar_main4_layout), str, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onRetryLoadPaperGroups();
                            }
                        }).show();
            }
        });
    }

    private void onRetryLoadPaperGroups() {
        //call load Papers
//        mProgressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "retrying loading Paper Answers");
        loadPaperAnswers();
    }

    @Override
    public void onBackPressed() {
        if (call != null) {
            if (!call.isExecuted()) {
                call.cancel();
            }
        }
        super.onBackPressed();
    }


    /**
     * Menu Handling
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            showDialogInfo();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to inflate the dialog and show it.
     */
    private void showDialogInfo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AnswerActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_info, null);

        Animation view_anim = AnimationUtils.loadAnimation(AnswerActivity.this, R.anim.zoom_in);
        view.startAnimation(view_anim);

        //set all the details
        TextView g_tv = (TextView) view.findViewById(R.id.dialogue_group_tv);
        TextView g_desc_tv_1 = (TextView) view.findViewById(R.id.dialogue_group_desc_1);
        TextView g_desc_tv_2 = (TextView) view.findViewById(R.id.dialogue_group_desc_2);
        Button btn_contd = (Button) view.findViewById(R.id.btn_cont);

        //check for the current title of the toolbar
        String title = toolbar.getTitle().toString();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getGroupName().contentEquals(title)) {
                //now update all the views
                if (!data.get(i).getGroupName().contentEquals(" ")) {
                    if (g_tv.getVisibility() == View.GONE)
                        g_tv.setVisibility(View.VISIBLE);
                    g_tv.setText(data.get(i).getGroupName());
                }
                if (!data.get(i).getGroupDesc1().contentEquals(" ")) {
                    if (g_desc_tv_1.getVisibility() == View.GONE)
                        g_desc_tv_1.setVisibility(View.VISIBLE);
                    g_desc_tv_1.setText(data.get(i).getGroupDesc1());
                }
                if (!data.get(i).getGroupDesc2().contentEquals(" ")) {
                    if (g_desc_tv_2.getVisibility() == View.GONE)
                        g_desc_tv_2.setVisibility(View.VISIBLE);
                    g_desc_tv_2.setText(data.get(i).getGroupDesc2());
                }
                break;
            }
        }
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        btn_contd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel the dialogue
                if (alertDialog.isShowing()) {
                    alertDialog.cancel();
                }
            }
        });
    }

    private void showDialogNoNet() {
        View view = getLayoutInflater().inflate(R.layout.dialog_no_internet, null);

        Button btn_retry = (Button) view.findViewById(R.id.btn_retry);

        final Dialog dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        dialog.show();

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retry and close dialogue
                if (dialog.isShowing()) {
                    dialog.cancel();
                    onRetryLoadPaperGroups();
                }
            }
        });
    }

    private void showDialogError() {
        View view = getLayoutInflater().inflate(R.layout.dialog_error, null);

        Button btn_go_back = (Button) view.findViewById(R.id.btn_go_back);

        final Dialog dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        dialog.show();

        btn_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retry and close dialogue
                if (dialog.isShowing()) {
                    dialog.cancel();
                    onBackPressed();
                }
            }
        });
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
                Log.d("PaperActivity", "The interstitial wasn't loaded yet.");
            }
        }
    }
}
