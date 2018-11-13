package in.co.erudition.paper.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;

import androidx.annotation.NonNull;
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
import android.widget.LinearLayout;

import in.co.erudition.paper.Erudition;
import in.co.erudition.paper.LoginActivity;
import in.co.erudition.paper.R;
import in.co.erudition.paper.util.CacheUtils;
import in.co.erudition.paper.util.PreferenceUtils;

public class SettingsActivity extends AppCompatActivity {
    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor mPrefsEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        LinearLayout clear_cache_btn = (LinearLayout) findViewById(R.id.clear_cache_btn);
        LinearLayout sign_out_btn = (LinearLayout) findViewById(R.id.sign_out_btn);
        LinearLayout change_pass_btn = (LinearLayout) findViewById(R.id.change_password_btn);
        LinearLayout delete_acc_btn = (LinearLayout) findViewById(R.id.delete_acc_btn);
        SwitchCompat js_btn = (SwitchCompat) findViewById(R.id.js_btn_toggle);

        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

        js_btn.setChecked(PreferenceUtils.getJS());

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

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        clear_cache_btn.setOnClickListener(v -> {
            CacheUtils.deleteCache(SettingsActivity.this);
            //show the snackBar
            final Snackbar snackBar = Snackbar.make((CoordinatorLayout) findViewById(R.id.settings_main), getString(R.string.snackBar_clear_cache), Snackbar.LENGTH_LONG);

            snackBar.setAction(getString(R.string.dismiss), v1 -> snackBar.dismiss()).show();
        });

        sign_out_btn.setOnClickListener(v -> {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            Log.d("SettingsActivity", "Signed Out!");
                            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
        });

        delete_acc_btn.setOnClickListener(v -> {
            Snackbar.make((CoordinatorLayout) findViewById(R.id.settings_main), getString(R.string.delete_message), Snackbar.LENGTH_LONG).show();

        });

        change_pass_btn.setOnClickListener(v -> {

        });

        js_btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mPrefsEdit = mPrefs.edit();

            if (isChecked){
                //Turn on all javaScript
                mPrefsEdit.putBoolean("JavaScript",true);
                Snackbar.make((CoordinatorLayout) findViewById(R.id.settings_main), getString(R.string.javascript_true), Snackbar.LENGTH_LONG).show();
            }
            else {
                mPrefsEdit.putBoolean("JavaScript",false);
                Snackbar.make((CoordinatorLayout) findViewById(R.id.settings_main), getString(R.string.javascript_false), Snackbar.LENGTH_LONG).show();
            }
            mPrefsEdit.apply();
            Log.d("JavaScript State:", String.valueOf(isChecked));
        });

    }

}
