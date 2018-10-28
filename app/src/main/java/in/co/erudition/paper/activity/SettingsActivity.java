package in.co.erudition.paper.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import in.co.erudition.paper.Erudition;
import in.co.erudition.paper.R;

public class SettingsActivity extends AppCompatActivity {
    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor mPrefsEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        CardView clear_cache_btn = (CardView) findViewById(R.id.clear_cache_btn);
        SwitchCompat ad_btn = (SwitchCompat) findViewById(R.id.ad_btn_toggle);

        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

        // To set the background of the activity go below the StatusBar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorBlack25alpha));
//            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorBlack75alpha));
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
//        toolbar.setTitle("Order History");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        clear_cache_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show the snackBar
                final Snackbar snackBar = Snackbar.make((CoordinatorLayout) findViewById(R.id.settings_main), getString(R.string.snackBar_clear_cache), Snackbar.LENGTH_LONG);

                snackBar.setAction(getString(R.string.dismiss), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackBar.dismiss();
                    }
                }).show();
            }
        });

        ad_btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mPrefsEdit = mPrefs.edit();

            if (isChecked){
                //Turn on all adMob Ads
                mPrefsEdit.putBoolean("AdMob",true);
            }
            else {
                mPrefsEdit.putBoolean("AdMob",false);
            }
            mPrefsEdit.apply();
            Log.d("AdMob State:", String.valueOf(isChecked));
        });

    }

}
