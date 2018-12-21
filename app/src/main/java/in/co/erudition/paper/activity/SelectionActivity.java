package in.co.erudition.paper.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import in.co.erudition.paper.R;
import in.co.erudition.paper.data.model.PresetResponseCode;
import in.co.erudition.paper.data.remote.BackendService;
import in.co.erudition.paper.network.NetworkUtils;
import in.co.erudition.paper.util.ApiUtils;
import in.co.erudition.paper.util.PreferenceUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectionActivity extends AppCompatActivity {
    private BackendService mService;
    private NetworkUtils mNetworkUtils = new NetworkUtils();
    private FloatingActionMenu fab;
    private AdView mAdView;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_old);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Load Ads
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mService = ApiUtils.getBackendService();

        //Check if its in not active state
        String state = getIntent().getStringExtra("CourseActivity.EXTRA_Subject_State");
        if (state!=null) {
            if (state.contentEquals("Not Active")) {
                showDialogSoon();
            }
        }

        //Selection Buttons
        CardView yearBtn = (CardView) findViewById(R.id.year_btn);
        CardView chapBtn = (CardView) findViewById(R.id.chapter_btn);
        CardView syllabusBtn = (CardView) findViewById(R.id.syllabus_btn);

        //Check if they needs to be made visible
        if (getIntent().getStringExtra("CourseActivity.EXTRA_Subject_YearView").contentEquals("Not Active"))
            yearBtn.setVisibility(View.GONE);
        if (getIntent().getStringExtra("CourseActivity.EXTRA_Subject_ChapView").contentEquals("Not Active"))
            chapBtn.setVisibility(View.GONE);
        if (getIntent().getStringExtra("CourseActivity.EXTRA_Subject_Syllabus").contentEquals(""))
            syllabusBtn.setVisibility(View.GONE);

        final LinearLayout selection = (LinearLayout) findViewById(R.id.selection_chap_or_year);
        TextView title = (TextView) findViewById(R.id.university_name_tv);

        title.setText(getIntent().getStringExtra("CourseActivity.EXTRA_Subject_FULL_NAME"));

        // To set the background of the activity go below the StatusBar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorBlack25alpha));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        /*
            Adjusting the Status bar margin for Different notches
         */
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        if (Build.VERSION.SDK_INT >= 20) {
            ViewCompat.setOnApplyWindowInsetsListener(appBarLayout, (View v, WindowInsetsCompat insets) -> {
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

                params = (ViewGroup.MarginLayoutParams) mAdView.getLayoutParams();
                params.bottomMargin = insets.getSystemWindowInsetBottom();
                mAdView.invalidate();
                mAdView.requestLayout();

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

        /**
         * instantiate the floating action buttons
         */

        fab = (FloatingActionMenu) findViewById(R.id.fab_menu);

        com.github.clans.fab.FloatingActionButton fab_recent = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_recent);
        com.github.clans.fab.FloatingActionButton fab_offline = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_offline);
        final com.github.clans.fab.FloatingActionButton fab_bookmark = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_bookmarks);

        final Intent intent_fab = new Intent(this, PaperActivity.class);
        intent_fab.putExtra("FROM", "action_fab");

        fab_recent.setOnClickListener(v -> {
            intent_fab.putExtra("Title", "Recent Papers");
            fab.close(true);
            startActivity(intent_fab);
        });
        fab_offline.setOnClickListener(v -> {
            intent_fab.putExtra("Title", "Offline");
            fab.close(true);
            startActivity(intent_fab);
        });
        fab_bookmark.setOnClickListener(v -> {
            intent_fab.putExtra("Title", "Bookmarks");
            fab.close(true);
            startActivity(intent_fab);
        });

        setUpCustomFabMenuAnimation();

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_container);
        try {
//            collapsingToolbarLayout.setTitle(getIntent().getStringExtra("UniversityActivity.EXTRA_University_FULL_NAME"));
            collapsingToolbarLayout.setTitle(getIntent().getStringExtra("CourseActivity.EXTRA_Subject_NAME"));
        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e) {
            Log.e("Exception", e.toString());
        }

        final Intent intent = new Intent(this, PaperActivity.class);
        final Intent syllabusIntent = new Intent(this,SyllabusActivity.class);
        syllabusIntent.putExtras(getIntent());
        intent.putExtras(getIntent());

        chapBtn.setOnClickListener(v -> {
            intent.putExtra("Selection Activity: Selection", 0);
            startActivity(intent);
        });

        yearBtn.setOnClickListener(v -> {
            intent.putExtra("Selection Activity: Selection", 1);
            startActivity(intent);
        });

        syllabusBtn.setOnClickListener(v -> startActivity(syllabusIntent));

    }

    private void notifyMeCall(){
        Log.d("CourseActivity", "notifyMeMethod");
        String params[] = getIntent().getStringArrayExtra("CourseActivity.EXTRA_params");
        Call<PresetResponseCode> notifyCall = mService.notifyMe(PreferenceUtils.getEid(),params[0], params[1], params[2], params[3]);;

        Log.d("Call", notifyCall.request().toString());
        notifyCall.enqueue(new Callback<PresetResponseCode>() {
            @Override
            public void onResponse(Call<PresetResponseCode> call, Response<PresetResponseCode> response) {
                if (response.isSuccessful()) {
                    Log.d("SelectionActivity", "issuccess");
                    Log.d("Response Body", response.body().toString());
                    Log.d("SelectionActivity", "API success");

                    //Toast
                    Toast.makeText(SelectionActivity.this,"You will be notified!",Toast.LENGTH_LONG).show();
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
                    Log.d("CourseActivity", "call is cancelled");

                } else if (mNetworkUtils.isOnline(SelectionActivity.this)) {
                    Log.d("MainActivity", "error loading from API");
                } else {
                    Log.d("MainActivity", "Check your network connection");
                }
            }
        });
    }


    private void showDialogSoon() {
        View view = getLayoutInflater().inflate(R.layout.dialog_coming_soon, null);

        Button btn_notify = (Button) view.findViewById(R.id.btn_notify);
        ImageView btn_go_back = (ImageView) view.findViewById(R.id.btn_go_back);

        dialog = new Dialog(SelectionActivity.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        dialog.show();

        btn_go_back.setOnClickListener(v -> {
            if (dialog.isShowing()) {
                dialog.cancel();
                onBackPressed();
            }
        });

        btn_notify.setOnClickListener(v -> {
            //call notify
            notifyMeCall();
//                onBackPressed();
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

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mAdView != null) {
            mAdView.resume();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdView!=null) {
            final ViewGroup viewGroup = (ViewGroup) mAdView.getParent();
            if (viewGroup != null)
            {
                viewGroup.removeView(mAdView);
            }
            mAdView.removeAllViews();
            mAdView.destroy();
        }
    }

    @Override
    public void onBackPressed() {
        if (dialog!=null){
            if (dialog.isShowing()){
                dialog.cancel();
            }
        }
        super.onBackPressed();
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

        final Drawable plusDrawable = ContextCompat.getDrawable(SelectionActivity.this,
                R.drawable.ic_expand_close);
        expandAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fab.getMenuIconView().setImageDrawable(plusDrawable);
                fab.setIconToggleAnimatorSet(mCloseAnimatorSet);
            }
        });

        final Drawable mapDrawable = ContextCompat.getDrawable(SelectionActivity.this,
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
