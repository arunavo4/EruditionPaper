package in.co.erudition.paper.activity;

import android.app.Dialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import in.co.erudition.paper.R;
import in.co.erudition.paper.adapter.GroupAdapter;
import in.co.erudition.paper.data.model.Paper;
import in.co.erudition.paper.data.model.PaperGroup;
import in.co.erudition.paper.data.remote.BackendService;
import in.co.erudition.paper.fragment.BottomFragment;
import in.co.erudition.paper.network.NetworkUtils;
import in.co.erudition.paper.util.ApiUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionActivity extends AppCompatActivity {

    private GroupAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private BackendService mService;
    private Call<Paper> call;
    private NetworkUtils mNetworkUtils = new NetworkUtils();
    private FloatingActionButton fab;
    private ProgressBar mProgressBar;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    private String TAG = "QuestionActivity";

    private boolean isChecked_offline = false;
    private boolean isChecked_bookmark = false;
    private Boolean transparent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_new);             //Changed
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_Ques);

        appBarLayout =  (AppBarLayout) findViewById(R.id.my_appbar_container); //Changed
        appBarLayout.bringToFront();

        ViewGroup linearLayout = (ViewGroup) findViewById(R.id.ques_linear_layout);
        int select = getIntent().getIntExtra("PaperActivity.EXTRA_Select",-1);
        if (select==0){
            View header = LayoutInflater.from(this).inflate(R.layout.question_header_chap, linearLayout, false);
            linearLayout.addView(header,0);
        }else if (select==1){
            View header = LayoutInflater.from(this).inflate(R.layout.question_header_new, linearLayout, false);
            linearLayout.addView(header,0);
        }
        // To set the background of the activity go below the StatusBar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorBlack25alpha));
//            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorBlack75alpha));
        }

        Drawable bg;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            bg = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
            bg.setColorFilter(ContextCompat.getColor(this, R.color.colorBlack75alpha), PorterDuff.Mode.MULTIPLY);
        }
        else {
            bg = VectorDrawableCompat.create(getResources(), R.drawable.ic_arrow_back_black_24dp, null);
            bg = DrawableCompat.wrap(bg);
            DrawableCompat.setTint(bg, ContextCompat.getColor(this, R.color.colorBlack75alpha));
        }

        toolbar.setNavigationIcon(bg);
                                                            //CourseActivity.EXTRA_University_Key
        toolbar.setTitle("");                                                        //changed
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        try {
            if (select==0){
                TextView chap_name_tv = (TextView) findViewById(R.id.chap_name_tv);
                TextView chap_num_tv = (TextView) findViewById(R.id.chap_num_tv);

                chap_name_tv.setText(getIntent().getStringExtra("PaperActivity.EXTRA_Full_Name"));
                String str = "Chapter " + getIntent().getStringExtra("PaperActivity.EXTRA_Name");
                chap_num_tv.setText(str);
            }else if (select==1){
                TextView year_tv = (TextView) findViewById(R.id.year_tv);
                TextView sub_name_tv = (TextView) findViewById(R.id.subject_name_tv);

                year_tv.setText(getIntent().getStringExtra("PaperActivity.EXTRA_Year"));
                sub_name_tv.setText(getIntent().getStringExtra("PaperActivity.EXTRA_Full_Name"));
            }
        }
        catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e)
        {   Log.e("Exception",e.toString()); }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomFragment dialogFrag = new BottomFragment();
                dialogFrag.setParentFab(fab);
                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
            }
        });

        //Loading Starts
        mProgressBar.setVisibility(View.VISIBLE);

        mService = ApiUtils.getBackendService();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_questions_group);
        mAdapter = new GroupAdapter(this, new ArrayList<PaperGroup>(), getIntent(), new GroupAdapter.GroupItemListener() {
            @Override
            public void onGroupClick(String id) {
                Toast.makeText(QuestionActivity.this, "Post id is" + id, Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.d(TAG,"done adapter");

        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nested_parent_scroll);
        if (nestedScrollView != null){
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY && fab.getVisibility() == View.VISIBLE) {
                        //DOWN SCROLL
                        fab.hide();
                        if (transparent){
                            appBarLayout.setBackground(getResources().getDrawable(R.drawable.bg_white));
                            transparent = false;
                            //getIntent().getStringExtra("PaperActivity.EXTRA_Subject_Name") + " " +
                            String str = getIntent().getStringExtra("PaperActivity.EXTRA_Year");
                            toolbar.setTitle(str);
                        }
                    }
                    if (scrollY < oldScrollY && fab.getVisibility() != View.VISIBLE) {
                        fab.show();
                    }
                    if (scrollY == 0) {
                        //TOP SCROLL
                        if (!transparent){
                            appBarLayout.setBackground(getResources().getDrawable(R.drawable.bg_trasparent));
                            transparent = true;
                            toolbar.setTitle("");
                        }
                    }
                }
            });
        }

        Log.d(TAG,"loading paper groups");

        loadPaperGroups();
    }

    private void loadPaperGroups() {

        Log.d(TAG, "loadPaperGroupsMethod");

        call = mService.getPaper(getIntent().getStringExtra("PaperActivity.EXTRA_Paper_Code"));
//        call = mService.getPaper("214");
        call.enqueue(new Callback<Paper>() {

            @Override
            public void onResponse(Call<Paper> call, Response<Paper> response) {
                Log.d("Call",call.request().toString());
                if(response.isSuccessful()) {
                    Log.d(TAG,"issuccess");

                    int select = getIntent().getIntExtra("PaperActivity.EXTRA_Select",-1);
                    mProgressBar.setVisibility(View.GONE);
                    Log.d("Response Body",response.body().toString());
                    mAdapter.updateGroups(response.body());
                    if (select==0){
                        updatePaperCardChap(response.body());
                    }else if (select==1){
                        updatePaperCard(response.body());
                    }
                    Log.d(TAG, "API success");
                }else {
                    int statusCode  = response.code();
                    if(statusCode==401){
                        Log.d("StatusCode","Unauthorized");
                    }
                    if (statusCode==200){
                        Log.d("StatusCode","OK");
                    }
                    else {
                        Log.d("StatusCode", String.valueOf(statusCode));
                    }
                    // handle request errors depending on status code
                }
            }

            @Override
            public void onFailure(Call<Paper> call, Throwable t) {
                String str = "Failed";
                if(call.isCanceled()){
                    Log.d(TAG, "call is cancelled");

                }
                else if(mNetworkUtils.isOnline(QuestionActivity.this)){
                    Log.d("MainActivity", "error loading from API");
                    str = "error loading from API";
                    showDialogError();
                }else{
                    Log.d("MainActivity", "Check your network connection");
                    str = "Check your network connection";
                    showDialogNoNet();
                }

                mProgressBar.setVisibility(View.GONE);
                Snackbar.make((CoordinatorLayout)findViewById(R.id.app_bar_main4_layout),str, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onRetryLoadPaperGroups();
                            }
                        }).show();
            }
        });
    }

    private void updatePaperCard(Paper body) {
        //Set the Question header;
        ImageView img = (ImageView) findViewById(R.id.university_img);
        TextView uni_name = (TextView) findViewById(R.id.university_tv);
        TextView year_tv = (TextView) findViewById(R.id.year_tv);
        TextView sub_name_tv = (TextView) findViewById(R.id.subject_name_tv);
        TextView time_tv = (TextView) findViewById(R.id.time_tv);
        TextView marks_tv = (TextView) findViewById(R.id.marks_tv);

        // Set item views based on your views and data model
        try {
            if(!body.getLogo().contentEquals("#")) {
                Glide
                        .with(this)
                        .load(body.getLogo())
                        .apply(RequestOptions.placeholderOf(R.drawable.bg_white))
                        .thumbnail(0.1f)
                        .into(img);
            }
            uni_name.setText(body.getBoardFullName());
            year_tv.setText(body.getYear());
            sub_name_tv.setText(body.getSubjectFullName());
            String time = body.getPaperTime() + " Min";
            time_tv.setText(time);
            marks_tv.setText(body.getPaperMarks());
        }
        catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e)
        {   Log.e("Exception",e.toString()); }
    }

    private void updatePaperCardChap(Paper body){
        TextView sub_name_tv = (TextView) findViewById(R.id.subject_name_tv);
        TextView chap_name_tv = (TextView) findViewById(R.id.chap_name_tv);
        TextView chap_num_tv = (TextView) findViewById(R.id.chap_num_tv);
        TextView counter_tv = (TextView) findViewById(R.id.counter_tv);

        // Set item views based on your views and data model
        try {
            chap_name_tv.setText(body.getChapterFullName());
            sub_name_tv.setText(body.getSubjectFullName());
            String str = "Chapter " + body.getChapterName();
            chap_num_tv.setText(str);
            counter_tv.setText("999");
        }
        catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e)
        {   Log.e("Exception",e.toString()); }
    }

    private void onRetryLoadPaperGroups() {
        //call load Papers
        mProgressBar.setVisibility(View.VISIBLE);
        Log.d(TAG,"retrying loading Paper groups");
        loadPaperGroups();
    }

    @Override
    public void onBackPressed() {
        if (!call.isExecuted()){
            call.cancel();
        }
        super.onBackPressed();
    }

    /**
     * Method to inflate the dialog and show it.
     */
    private void showDialogNoNet() {
        View view = getLayoutInflater().inflate(R.layout.dialog_no_internet,null);

        Button btn_retry = (Button) view.findViewById(R.id.btn_retry);

        final Dialog dialog = new Dialog(this,android.R.style.Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        dialog.show();

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retry and close dialogue
                if (dialog.isShowing()){
                    dialog.cancel();
                    onRetryLoadPaperGroups();
                }
            }
        });
    }

    private void showDialogError() {
        View view = getLayoutInflater().inflate(R.layout.dialog_page_not_found,null);

        Button btn_go_back = (Button) view.findViewById(R.id.btn_go_back);

        final Dialog dialog = new Dialog(this,android.R.style.Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        dialog.show();

        btn_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retry and close dialogue
                if (dialog.isShowing()){
                    dialog.cancel();
                    onBackPressed();
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem offline = menu.findItem(R.id.action_offline);
        offline.setChecked(isChecked_offline);

        MenuItem bookmark = menu.findItem(R.id.action_bookmark);
        bookmark.setChecked(isChecked_bookmark);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            return true;
        }
        else if (id == R.id.action_offline){
            isChecked_offline = !item.isChecked();
            item.setChecked(isChecked_offline);
            if (isChecked_offline){
                item.setTitle(R.string.action_offline_2);
            }
            else {
                item.setTitle(R.string.action_offline_1);
            }
            return true;
        }
        else if (id == R.id.action_bookmark){
            isChecked_bookmark = !item.isChecked();
            item.setChecked(isChecked_bookmark);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
