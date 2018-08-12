package in.co.erudition.paper.activitiy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.github.clans.fab.FloatingActionMenu;

import in.co.erudition.paper.R;

public class SelectionActivity extends AppCompatActivity {

    private FloatingActionMenu fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Selection Buttons
        CardView yearBtn = (CardView) findViewById(R.id.year_btn);
        CardView chapBtn = (CardView) findViewById(R.id.chapter_btn);
        final LinearLayout selection = (LinearLayout) findViewById(R.id.selection_chap_or_year);

        // To set the background of the activity go below the StatusBar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorBlack25alpha));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorBlack75alpha));
        }

        Drawable bg;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            bg = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
            bg.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.MULTIPLY);
        }
        else {
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
        intent_fab.putExtra("FROM","action_fab");

        fab_recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent_fab.putExtra("Title","Recent Papers");
                fab.close(true);
                startActivity(intent_fab);
            }
        });
        fab_offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent_fab.putExtra("Title","Offline");
                fab.close(true);
                startActivity(intent_fab);
            }
        });
        fab_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent_fab.putExtra("Title","Bookmarks");
                fab.close(true);
                startActivity(intent_fab);
            }
        });

        setUpCustomFabMenuAnimation();

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_container);
        try{
            collapsingToolbarLayout.setTitle(getIntent().getStringExtra("CourseActivity.EXTRA_Subject_FULL_NAME"));
        }
        catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e){
            Log.e("Exception",e.toString());
        }

        final Intent intent = new Intent(this,PaperActivity.class);
        intent.putExtras(getIntent());

        chapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("Selection Activity: Selection",0);
                startActivity(intent);
            }
        });

        yearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("Selection Activity: Selection",1);
                startActivity(intent);
            }
        });

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
