package in.co.erudition.paper.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.ProgressView;
import com.firebase.ui.auth.ui.email.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import in.co.erudition.paper.Erudition;
import in.co.erudition.paper.LoginActivity;
import in.co.erudition.paper.R;
import in.co.erudition.paper.util.CacheUtils;
import in.co.erudition.paper.util.LoginUtils;
import in.co.erudition.paper.util.PreferenceUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {
    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor mPrefsEdit;
    private boolean insetsApplied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_container);
        collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(), "font/source_sans_pro_semibold.ttf"));
        collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(), "font/source_sans_pro_semibold.ttf"));

        LinearLayout clear_cache_btn = (LinearLayout) findViewById(R.id.clear_cache_btn);
        LinearLayout sign_out_btn = (LinearLayout) findViewById(R.id.sign_out_btn);
        LinearLayout change_pass_btn = (LinearLayout) findViewById(R.id.change_password_btn);
        LinearLayout delete_acc_btn = (LinearLayout) findViewById(R.id.delete_acc_btn);
        LinearLayout base_url_btn = (LinearLayout) findViewById(R.id.base_url_btn);
        CardView card_dev = (CardView) findViewById(R.id.card_dev);
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
        //Make sure you only do it once

        if (Build.VERSION.SDK_INT >= 20) {
            ViewCompat.setOnApplyWindowInsetsListener(appBarLayout, (View v, WindowInsetsCompat insets) -> {
                if (!insetsApplied) {
                    v.getLayoutParams().height -= getResources().getDimensionPixelSize(R.dimen.status_bar_height);
                    v.getLayoutParams().height += insets.getSystemWindowInsetTop();

                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                    params.topMargin = insets.getSystemWindowInsetTop();
                    v.invalidate();
                    v.requestLayout();

                    insetsApplied = true;
                }
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
                            //Delete Shared Pref
                            mPrefs.edit().clear().apply();
                            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
        });

        delete_acc_btn.setOnClickListener(v -> {
            Snackbar.make((CoordinatorLayout) findViewById(R.id.settings_main), getString(R.string.delete_message), Snackbar.LENGTH_LONG).show();

        });

        change_pass_btn.setOnClickListener(v -> {
            final Snackbar loading = Snackbar.make((CoordinatorLayout) findViewById(R.id.settings_main), getString(R.string.sending), Snackbar.LENGTH_INDEFINITE);
            loading.show();
            //Make the forgot Password Api call
            Callback<Login> callback = new Callback<Login>() {
                @Override
                public void onResponse(Call<Login> call, Response<Login> response) {
                    loading.dismiss();
                    if (response.isSuccessful()) {
                        Login login = response.body();
                        String code = login.getCode();

                        if (code.contentEquals("0")) {
                            Snackbar.make((CoordinatorLayout) findViewById(R.id.settings_main), getString(R.string.fui_error_email_does_not_exist), Snackbar.LENGTH_LONG).show();
                        } else if (code.contentEquals("1")) {
                            showEmailSentDialog();
                        } else {
                            Snackbar.make((CoordinatorLayout) findViewById(R.id.settings_main), getString(R.string.fui_error_unknown), Snackbar.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Login> call, Throwable t) {
                    loading.dismiss();
                    Snackbar.make((CoordinatorLayout) findViewById(R.id.settings_main), getString(R.string.error_occurred), Snackbar.LENGTH_LONG).show();
                }
            };

            LoginUtils loginUtils = new LoginUtils();
            loginUtils.forgot_password(PreferenceUtils.readEmail(),callback);

        });

        js_btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mPrefsEdit = mPrefs.edit();

            if (isChecked) {
                //Turn on all javaScript
                mPrefsEdit.putBoolean("JavaScript", true);
                Snackbar.make((CoordinatorLayout) findViewById(R.id.settings_main), getString(R.string.javascript_true), Snackbar.LENGTH_LONG).show();
            } else {
                mPrefsEdit.putBoolean("JavaScript", false);
                Snackbar.make((CoordinatorLayout) findViewById(R.id.settings_main), getString(R.string.javascript_false), Snackbar.LENGTH_LONG).show();
            }
            mPrefsEdit.apply();
            Log.d("JavaScript State:", String.valueOf(isChecked));
        });

        //check if developer
        if (PreferenceUtils.getRole().equalsIgnoreCase("developer")) {
            card_dev.setVisibility(View.VISIBLE);
        } else {
            card_dev.setVisibility(View.INVISIBLE);
        }

        base_url_btn.setOnClickListener(v -> {
            showDialogBaseUrl();
        });

    }

    private void showDialogBaseUrl() {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            View view = getLayoutInflater().inflate(R.layout.dialog_base_url, null);

            Animation view_anim = AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.zoom_in);
            view.startAnimation(view_anim);

            TextInputEditText base_url = (TextInputEditText) view.findViewById(R.id.base_url_edit_text);
            TextInputLayout base_url_layout = (TextInputLayout) view.findViewById(R.id.base_url_text_layout);
            Button url_save = (Button) view.findViewById(R.id.btn_save);

            //Set the base url
            base_url.setText(PreferenceUtils.getBaseUrl());

            builder.setView(view);
            final AlertDialog alertDialog = builder.create();

            url_save.setOnClickListener(v -> {
                base_url_layout.setErrorEnabled(true);
                base_url_layout.setError(null);

                if (base_url.getText().toString().length() != 0) {
                    //Set the new Base Url
                    PreferenceUtils.setBaseUrl(base_url.getText().toString());
                    if (alertDialog.isShowing()) {
                        alertDialog.cancel();
                    }
                } else {
                    base_url_layout.setError("Enter Base Url");
                }
            });

            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showEmailSentDialog() {
        new AlertDialog.Builder(this)
                .setTitle(com.firebase.ui.auth.R.string.fui_title_confirm_recover_password)
                .setMessage(getString(R.string.change_pass_dialog, PreferenceUtils.readEmail()))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

}
