package in.co.erudition.paper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import in.co.erudition.paper.R;
import in.co.erudition.paper.data.model.QuesAnsSearch;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;

public class SingleAnswerActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ArrayList<QuesAnsSearch> data;
    private AdView adView;
    private StringBuilder str;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_answer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        str = new StringBuilder("<html>\n");

        str.append("<head>\n    <link rel=\"stylesheet\" href=\"prism.css\">\n");
        str.append("    <style>\n    img {height: auto!important;  width: 100%!important; overflow-x: auto!important; overflow-y: hidden!important;\n");
        str.append("            border: none!important;\n max-width: fit-content;\n vertical-align: middle;\n");
        str.append("        }\n  table { width: 100%!important; background-color: transparent; border-spacing: 0; border-collapse: collapse;}\n");
        str.append("    </style>\n <script src=\"prism.js\"></script>\n</head>");
        str.append("<body>\n");

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.my_appbar_container);
        appBarLayout.bringToFront();

        WebView ques_tv = (WebView) findViewById(R.id.question_tv);
        WebView ans_tv = (WebView) findViewById(R.id.answer_tv);

        TextView marks_tv = (TextView) findViewById(R.id.marks_tv);
        TextView ques_no_tv = (TextView) findViewById(R.id.ques_num);

        //Load Ads
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

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

        data = getIntent().getParcelableArrayListExtra("Search_ADAPTER.parcelData");
        int pos = getIntent().getIntExtra("Search_ADAPTER.position",0);

        //HAndle the webviews
        ques_tv.getSettings().setJavaScriptEnabled(true);
        ques_tv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        ques_tv.getSettings().setAppCacheEnabled(true);
        ques_tv.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        ques_tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        ques_tv.setLongClickable(false);

        ans_tv.getSettings().setJavaScriptEnabled(true);
        ans_tv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        ans_tv.getSettings().setAppCacheEnabled(true);
        ans_tv.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        ans_tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        ans_tv.setLongClickable(false);

        ques_tv.setOnTouchListener(new View.OnTouchListener() {

            private int mTouchSlop = 150;
            private float x1;
            private float x2;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        float deltaX = x2 - x1;

                        if (Math.abs(deltaX) > mTouchSlop) {
                            // Left to Right swipe action
                            if (x2 > x1) {
                                Log.d("WebView", "left to right Swipe");
                                if (ques_tv.canScrollHorizontally(1)) {
                                    Log.d("Horizontal Scroll", "left to right");
                                    return true;
                                }
                            }

                            // Right to left swipe action
                            else {
                                Log.d("WebView", "Right to left Swipe");
                                if (ques_tv.canScrollHorizontally(-1)) {
                                    Log.d("Horizontal Scroll", "Right to left");
                                    return true;
                                }
                            }

                        } else {
                            // consider as something else - a screen tap for example
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d("ques_tv", "Moved");
                        if (ques_tv.canScrollHorizontally(-1)) {
                            Log.d("Horizontal Scroll", "left to right");
                            return true;
                        }
                }
                Log.d("ques_tv", "Touched!");
                return false;
            }
        });


        ques_tv.loadDataWithBaseURL("file:///android_asset/", getHtmlData(data.get(pos).getQuestion()), "text/html", "UTF-8", null);
        ans_tv.loadDataWithBaseURL("file:///android_asset/", getHtmlData(data.get(pos).getAnswer()), "text/html", "UTF-8", null);

        marks_tv.setText(data.get(pos).getMarks());
//        ques_no_tv.setText(questionAnswer.getQuestionNo() + ".");

    }


    private String getHtmlData(String data) {
        return str.toString() + data + "</body>\n</html>";
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
    protected void onDestroy() {
        adView.removeAllViews();
        adView.destroy();
        super.onDestroy();
    }

}
