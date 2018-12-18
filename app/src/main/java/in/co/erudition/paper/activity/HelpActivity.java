package in.co.erudition.paper.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.erudition.morphingbutton.impl.MorphingButton;
import com.google.android.material.appbar.AppBarLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import in.co.erudition.paper.R;
import in.co.erudition.paper.data.model.PresetResponseCode;
import in.co.erudition.paper.data.remote.BackendService;
import in.co.erudition.paper.network.NetworkUtils;
import in.co.erudition.paper.util.ApiUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HelpActivity extends AppCompatActivity {
    private BackendService mService;
    private NetworkUtils mNetworkUtils = new NetworkUtils();
    private MorphingButton submit_btn;
    private FloatingActionMenu fab;
    private boolean insetsApplied = false;
    private boolean btn_pressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        CardView like_btn = (CardView) findViewById(R.id.like_erudition_paper);
        CardView faq_btn = (CardView) findViewById(R.id.faq_btn);
        TextInputEditText subject = (TextInputEditText) findViewById(R.id.subject_feedback);
        TextInputLayout subject_layout = (TextInputLayout) findViewById(R.id.subject_feedback_layout);
        TextInputLayout issue_layout = (TextInputLayout) findViewById(R.id.issue_feedback_layout);
        TextInputEditText issue = (TextInputEditText) findViewById(R.id.issue_feedback);
        submit_btn = (MorphingButton) findViewById(R.id.submit_btn);

        morphToSquare(submit_btn, 0);

        // To set the background of the activity go below the StatusBar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorBlack25alpha));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorBlack75alpha));
        }

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
        mService = ApiUtils.getBackendService();

        fab = (FloatingActionMenu) findViewById(R.id.fab_menu);

        setUpCustomFabMenuAnimation();

        like_btn.setOnClickListener(v -> {
            //Show the dialog
            showDialogPlay();
        });

        faq_btn.setOnClickListener(v -> {
            //Open the Webview with the given address
            Intent intent = new Intent(HelpActivity.this, WebviewActivity.class);
            intent.putExtra("Webview.Title", getString(R.string.faq));
            intent.putExtra("Webview.Address", getString(R.string.faq_url));
            startActivity(intent);
        });

        submit_btn.setOnClickListener(v -> {
            //check if its not empty
            subject_layout.setError(null);
            issue_layout.setError(null);
            if (btn_pressed){
                btn_pressed = false;
                morphToSquare(submit_btn,getResources().getInteger(R.integer.mb_animation));
            }
            else if (subject.getText().toString().length()!=0 && issue.getText().toString().length()!=0 ){
                if (!btn_pressed) {
                    btn_pressed = true;
                    submit_feedback(subject.getText().toString(),issue.getText().toString());
                }
            }else{
                if (subject.getText().toString().length()==0){
//                    subject.setError(null);
                    subject_layout.setError("Cannot be Empty!");
                }
                if (issue.getText().toString().length()==0){
//                    issue.setError(null);
                    issue_layout.setError("Cannot be Empty!");
                }
            }
        });
    }

    private void submit_feedback(String subject, String issue) {

        Log.d("HelpActivity","Submit Feedback");
        Call<PresetResponseCode> feedbackCall = mService.submitFeedback(subject,issue);
        feedbackCall.enqueue(new Callback<PresetResponseCode>() {
            @Override
            public void onResponse(Call<PresetResponseCode> call, Response<PresetResponseCode> response) {
                //If successful just display that update was successful
                Log.d("Call", call.request().toString());
                if (response.isSuccessful()) {
                    Log.d("Api CAll","Successful!");
                    if (response.body() != null) {
                        Log.d("Message:",response.body().getMsg());
                    }
                    morphToSuccess(submit_btn);
                    Toast.makeText(getApplicationContext(),getString(R.string.feedback_success),Toast.LENGTH_LONG).show();

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
            public void onFailure(Call<PresetResponseCode> call, Throwable t) {
                morphToFailure(submit_btn);
                Toast.makeText(getApplicationContext(),getString(R.string.error_occurred),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void morphToSuccess(final MorphingButton btnMorph) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(getResources().getInteger(R.integer.mb_animation))
                .cornerRadius(getResources().getDimensionPixelSize(R.dimen.submit_btn_height))
                .width(getResources().getDimensionPixelSize(R.dimen.submit_btn_height))
                .height(getResources().getDimensionPixelSize(R.dimen.submit_btn_height))
                .color(getResources().getColor(R.color.green_light))
                .colorPressed(getResources().getColor(R.color.green_sap))
                .icon(R.drawable.ic_done_all_white_24dp);
        btnMorph.morph(circle);
    }

    private void morphToFailure(final MorphingButton btnMorph) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(getResources().getInteger(R.integer.mb_animation))
                .cornerRadius(getResources().getDimensionPixelSize(R.dimen.submit_btn_height))
                .width(getResources().getDimensionPixelSize(R.dimen.submit_btn_height))
                .height(getResources().getDimensionPixelSize(R.dimen.submit_btn_height))
                .color(getResources().getColor(R.color.red_fab_help))
                .colorPressed(getResources().getColor(R.color.red_fav))
                .icon(R.drawable.ic_close_black_24dp);
        btnMorph.morph(circle);
    }

    private void morphToSquare(final MorphingButton btnMorph, int duration) {
        MorphingButton.Params square = MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius(getResources().getDimensionPixelSize(R.dimen.submit_btn_height))
                .width(getResources().getDimensionPixelSize(R.dimen.submit_btn_width))
                .height(getResources().getDimensionPixelSize(R.dimen.submit_btn_height))
                .color(getResources().getColor(R.color.colorMaterialBlack_no_alpha))
                .colorPressed(getResources().getColor(R.color.colorMaterialBlack))
                .text(getString(R.string.submit_btn));
        btnMorph.morph(square);
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

        final Drawable plusDrawable = ContextCompat.getDrawable(HelpActivity.this,
                R.drawable.ic_expand_close);
        expandAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fab.getMenuIconView().setImageDrawable(plusDrawable);
                fab.setIconToggleAnimatorSet(mCloseAnimatorSet);
            }
        });

        final Drawable mapDrawable = ContextCompat.getDrawable(HelpActivity.this,
                R.drawable.ic_chat_black_24dp);
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

    /**
     * Method to inflate the dialog and show it.
     */
    private void showDialogPlay() {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(HelpActivity.this);
            View view = LayoutInflater.from(HelpActivity.this).inflate(R.layout.dialog_google_play, null);

            Animation view_anim = AnimationUtils.loadAnimation(HelpActivity.this, R.anim.zoom_in);
            view.startAnimation(view_anim);

            Button btn_later = (Button) view.findViewById(R.id.btn_later);
            Button btn_cont = (Button) view.findViewById(R.id.btn_cont);

            builder.setView(view);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();

            btn_cont.setOnClickListener(v -> {
                String appPackage = "in.co.erudition.paper";
                String url = "market://details?id=" + appPackage;
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackage)));
                }
            });

            btn_later.setOnClickListener(v -> {
                //cancel the dialogue
                if (alertDialog.isShowing()) {
                    alertDialog.cancel();
                }
            });
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

}
