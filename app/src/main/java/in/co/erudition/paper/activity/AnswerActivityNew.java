package in.co.erudition.paper.activity;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import in.co.erudition.paper.R;
import in.co.erudition.paper.data.model.QuesAnsSearch;
import in.co.erudition.paper.data.model.QuestionAnswer;
import in.co.erudition.paper.misc.NestedScrollWebView;
import in.co.erudition.paper.network.NetworkUtils;
import in.co.erudition.paper.util.PreferenceUtils;

public class AnswerActivityNew extends AppCompatActivity {

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

    private AdView adView;
    private NetworkUtils mNetworkUtils = new NetworkUtils();
    private String TAG = "AnswerActivity";

    private InterstitialAd mInterstitialAd;
    private AdCountDownTimer timer;

    private ArrayList<QuestionAnswer> data;
    private Toolbar toolbar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        str = new StringBuilder("<html>");

        str.append("<head>\n <link rel=\"stylesheet\" href=\"font.css\"><style>body{font-size:14px;font-family:'Source Sans Pro',sans-serif}p{margin-top:0;");
        str.append("    margin-bottom:.4rem}img{height:auto!important;overflow-x:auto!important;overflow-y:hidden!important;border:none!important;");
        str.append("   max-width:fit-content;vertical-align:middle}table{width:100%!important;background-color:transparent;border-spacing:0;border-collapse:collapse}</style>");

        css_js = str;
        css_js.append("<link rel=\"stylesheet\" href=\"prism.css\"><script src=\"prism.js\"></script>");
        css_js.append("</head><body>\n");

        str.append("</head><body>\n");

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.my_appbar_container);
        appBarLayout.bringToFront();

        ques_tv = (NestedScrollWebView) findViewById(R.id.question_tv);
        ans_tv = (NestedScrollWebView) findViewById(R.id.answer_tv);

        marks_tv = (TextView) findViewById(R.id.marks_tv);
        ques_no_tv = (TextView) findViewById(R.id.ques_num);
        ans_count = (TextView) findViewById(R.id.tv_count);

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

        //Load Ads
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        View ad_spacer = (View) findViewById(R.id.nav_spacer_ad);
        View nav_spacer = (View) findViewById(R.id.nav_spacer);
        ad_spacer.setVisibility(View.VISIBLE);
        nav_spacer.setVisibility(View.VISIBLE);

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
                timer = new AdCountDownTimer(PreferenceUtils.getAdTime(), 1000);
                timer.start();
            }

        });

        //Set a timer for 1 min
        timer = new AdCountDownTimer(PreferenceUtils.getAdTime(), 1000);
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
        toolbar.setTitle(getIntent().getStringExtra("QUESTION_ADAPTER.group_name"));
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        data = getIntent().getParcelableArrayListExtra("QUESTION_ADAPTER.parcelData");
        pos = getIntent().getIntExtra("QUESTION_ADAPTER.position", 0);
        size = data.size();

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


        //Setup btn
        left_btn = (ImageView) findViewById(R.id.btn_left);
        right_btn = (ImageView) findViewById(R.id.btn_right);

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

        toolbar.setTitle(data.get(pos).getGroupName());

        marks_tv.setText(data.get(pos).getMarks());
        ques_no_tv.setText(data.get(pos).getQuestionNo() + ".");
        int no = pos + 1;
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
            Log.i("SingleAnswer", "Turning immersive mode mode off. ");
        } else {
            Log.i("SingleAnswer", "Turning immersive mode mode on.");
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        timer.cancel();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
        }
        timer.cancel();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
        //retrieve the remaining time
        timer = new AdCountDownTimer(PreferenceUtils.getAdTime(),1000);
        //restart the timer
        timer.start();

        //Turn immersive if not already done
        toggleImmersive();
    }

    @Override
    protected void onDestroy() {
        if (adView!=null) {
            final ViewGroup viewGroup = (ViewGroup) adView.getParent();
            if (viewGroup != null)
            {
                viewGroup.removeView(adView);
            }
            adView.removeAllViews();
            adView.destroy();
        }
        super.onDestroy();
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

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(AnswerActivityNew.this);
            View view = getLayoutInflater().inflate(R.layout.dialog_info, null);

            Animation view_anim = AnimationUtils.loadAnimation(AnswerActivityNew.this, R.anim.zoom_in);
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

            btn_contd.setOnClickListener(v -> {
                //cancel the dialogue
                if (alertDialog.isShowing()) {
                    alertDialog.cancel();
                }
            });
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
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
