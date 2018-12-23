package in.co.erudition.paper.activity;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import in.co.erudition.paper.R;
import in.co.erudition.paper.data.model.QuesAnsSearch;
import in.co.erudition.paper.misc.NestedScrollWebView;
import in.co.erudition.paper.util.PreferenceUtils;

public class SingleAnswerActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ArrayList<QuesAnsSearch> data;
    private AdView adView;
    private StringBuilder str;
    private StringBuilder css_js;
    private NestedScrollWebView ques_tv;
    private NestedScrollWebView ans_tv;
    private int pos;
    private int size;
    private ImageView left_btn;
    private ImageView right_btn;
    private TextView marks_tv;
    private TextView ans_count;
    private TextView ques_no_tv;

    private InterstitialAd mInterstitialAd;
    private AdCountDownTimer timer;

    private String TAG = "SingleAnswer";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_answer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        css_js = new StringBuilder("<html>");

        css_js.append("<head><link rel=\"stylesheet\" href=\"prism.css\"><script src=\"prism.js\"></script></head>");

//        css_js.append("<head>\n    <link rel=\"stylesheet\" href=\"prism.css\"> <link rel=\"stylesheet\" href=\"font.css\">\n");
//        css_js.append("    <style>\n body{font-size:14px;font-family:'Source Sans Pro',sans-serif}p{margin-top:0;margin-bottom:.4rem}");
//        css_js.append("   img {height: auto!important;  width: 100%!important; overflow-x: auto!important; overflow-y: hidden!important;\n");
//        css_js.append("            border: none!important;\n max-width: fit-content;\n vertical-align: middle;\n");
//        css_js.append("        }\n  table { width: 100%!important; background-color: transparent; border-spacing: 0; border-collapse: collapse;}\n");
//        css_js.append("    </style>\n <script src=\"prism.js\"></script>\n</head>");
        css_js.append("<body>\n");

        str = new StringBuilder("<html>");

        str.append("<head><link rel=\"stylesheet\" href=\"font.css\"><link rel=\"stylesheet\" href=\"https://s3.ap-south-1.amazonaws.com/in.co.erudition/test.css\"></head>");

//        str.append("<head>\n<link rel=\"stylesheet\" href=\"font.css\">\n");
//        str.append("    <style>\n body{font-size:14px;font-family:'Source Sans Pro',sans-serif}p{margin-top:0;margin-bottom:.4rem}");
//        str.append("   img {height: auto!important;  width: 100%!important; overflow-x: auto!important; overflow-y: hidden!important;\n");
//        str.append("            border: none!important;\n max-width: fit-content;\n vertical-align: middle;\n");
//        str.append("        }\n  table { width: 100%!important; background-color: transparent; border-spacing: 0; border-collapse: collapse;}\n");
//        str.append("    </style>\n</head>");
        str.append("<body>\n");


        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.my_appbar_container);
        appBarLayout.bringToFront();

        ques_tv = (NestedScrollWebView) findViewById(R.id.question_tv);
        ans_tv = (NestedScrollWebView) findViewById(R.id.answer_tv);

        marks_tv = (TextView) findViewById(R.id.marks_tv);
        ques_no_tv = (TextView) findViewById(R.id.ques_num);
        ans_count = (TextView) findViewById(R.id.tv_count);

        //Load Ads
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        //Setup Interstitial Ads --> only once at startup
        //Interstitial video ads
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
                timer = new AdCountDownTimer(PreferenceUtils.getAdTime(),1000);
                timer.start();
            }

        });

        //Set a timer for 1 min
        timer = new AdCountDownTimer(PreferenceUtils.getAdTime(),1000);
        timer.start();

        View ad_spacer = (View) findViewById(R.id.nav_spacer_ad);
        View nav_spacer = (View) findViewById(R.id.nav_spacer);
        ad_spacer.setVisibility(View.VISIBLE);
        nav_spacer.setVisibility(View.VISIBLE);

        // To set the background of the activity go below the StatusBar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorBlack25alpha));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

//        toggleImmersive();


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

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        data = getIntent().getParcelableArrayListExtra("Search_ADAPTER.parcelData");
        pos = getIntent().getIntExtra("Search_ADAPTER.position", 0);
        size = getIntent().getIntExtra("Search_ADAPTER.size", 0);

        //HAndle the webviews
        ques_tv.getSettings().setJavaScriptEnabled(true);
        ques_tv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        ques_tv.getSettings().setAppCacheEnabled(false);
//        ques_tv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        ques_tv.setOnLongClickListener(v -> true);
        ques_tv.setLongClickable(false);

        ans_tv.getSettings().setJavaScriptEnabled(true);
        ans_tv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        ans_tv.getSettings().setAppCacheEnabled(false);
//        ans_tv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        ans_tv.setOnLongClickListener(v -> true);
        ans_tv.setLongClickable(false);

        //Setup Zoom Controls
        ques_tv.getSettings().setSupportZoom(true);
        ques_tv.getSettings().setBuiltInZoomControls(true);
        ques_tv.getSettings().setDisplayZoomControls(false);

        ans_tv.getSettings().setSupportZoom(true);
        ans_tv.getSettings().setBuiltInZoomControls(true);
        ans_tv.getSettings().setDisplayZoomControls(false);


        marks_tv.setText(data.get(pos).getMarks());
        int no = pos + 1;
        ques_no_tv.setText(no + ".");

        //Setup btn
        left_btn = (ImageView) findViewById(R.id.btn_left);
        right_btn = (ImageView) findViewById(R.id.btn_right);

        String str = no + " of " + size;
        ans_count.setText(str);

        left_btn.setOnClickListener(v -> {
            //Change the data
            if (pos!=0) {
                pos -=1;
                LoadDataInWebView();
            }
        });

        right_btn.setOnClickListener(v -> {
            if (pos<size-1) {
                pos +=1;
                LoadDataInWebView();
            }
        });

        LoadDataInWebView();

    }

    /**
     * Detects and toggles immersive mode.
     */
    public void toggleImmersive() {
        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode off. ");
        } else {
            Log.i(TAG, "Turning immersive mode mode on.");
        }

        // Immersive mode: Backward compatible to KitKat (API 19).
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // This sample uses the "sticky" form of immersive mode, which will let the user swipe
        // the bars back in again, but will automatically make them disappear a few seconds later.
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }

    private void callOnClick() {
        //Show and hide buttons
        if (left_btn.getVisibility() == View.VISIBLE && right_btn.getVisibility() == View.VISIBLE){
            left_btn.setVisibility(View.INVISIBLE);
            right_btn.setVisibility(View.INVISIBLE);
        }
        else if (left_btn.getVisibility() != View.VISIBLE && right_btn.getVisibility() != View.VISIBLE){
            left_btn.setVisibility(View.VISIBLE);
            right_btn.setVisibility(View.VISIBLE);
        }
    }

    private void LoadDataInWebView(){
        //Make the left right btn visible and invisible
        if (pos==0 && left_btn.getVisibility()==View.VISIBLE){
            left_btn.setVisibility(View.INVISIBLE);
        }
        else if (pos==1 && left_btn.getVisibility()==View.INVISIBLE){
            left_btn.setVisibility(View.VISIBLE);
        }
        else if (pos==size-1 && right_btn.getVisibility()==View.VISIBLE){
            right_btn.setVisibility(View.INVISIBLE);
        }else if (pos==size-2 && right_btn.getVisibility()==View.INVISIBLE){
            right_btn.setVisibility(View.VISIBLE);
        }

        marks_tv.setText(data.get(pos).getMarks());
        int no = pos + 1;
        ques_no_tv.setText(no + ".");
        String str = no + " of " + size;
        ans_count.setText(str);

//        ques_tv.loadDataWithBaseURL("file:///android_asset/", getHtmlData(data.get(pos).getQuestion()), "text/html", "UTF-8", null);
//        ans_tv.loadDataWithBaseURL("file:///android_asset/", getHtmlData(data.get(pos).getAnswer()), "text/html", "UTF-8", null);

        if (data.get(pos).getJavascript().contentEquals("10") || data.get(pos).getJavascript().contentEquals("11")) {
            ques_tv.loadDataWithBaseURL("file:///android_asset/", getHtmlDataWithJs(data.get(pos).getQuestion()), "text/html", "UTF-8", null);
        } else {
            ques_tv.loadDataWithBaseURL("file:///android_asset/", getHtmlData(data.get(pos).getQuestion()), "text/html", "UTF-8", null);
        }

        if (data.get(pos).getJavascript().contentEquals("01") || data.get(pos).getJavascript().contentEquals("11")) {
            ans_tv.loadDataWithBaseURL("file:///android_asset/", getHtmlDataWithJs(data.get(pos).getAnswer()), "text/html", "UTF-8", null);
        } else {
            ans_tv.loadDataWithBaseURL("file:///android_asset/", getHtmlData(data.get(pos).getAnswer()), "text/html", "UTF-8", null);
        }
    }


    private String getHtmlData(String data) {
        return str.toString() + data + "</body>\n</html>";
    }

    private String getHtmlDataWithJs(String data) {
        return css_js.toString() + data + "</body>\n</html>";
    }


    /**
     * Menu Handling
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_ans, menu);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //cancel the timer
        timer.cancel();
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
        //cancel the timer
        timer.cancel();
    }

    @Override
    public void onResume() {
        if (adView != null) {
            adView.resume();
        }
        //retrieve the remaining time
        timer = new AdCountDownTimer(PreferenceUtils.getAdTime(),1000);
        //restart the timer
        timer.start();

        //Toggle immersive
        toggleImmersive();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            final ViewGroup viewGroup = (ViewGroup) adView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(adView);
            }
            adView.removeAllViews();
            adView.destroy();
        }
        super.onDestroy();
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
            //callback for every tick interval
            PreferenceUtils.setAdTime(millisUntilFinished);
        }

        @Override
        public void onFinish() {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Log.d(TAG, "The interstitial wasn't loaded yet.");
            }
        }
    }

}
