package in.co.erudition.paper.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import in.co.erudition.paper.R;
import in.co.erudition.paper.data.model.BoardCollege;
import in.co.erudition.paper.data.model.BoardCourse;
import in.co.erudition.paper.data.model.BoardSession;
import in.co.erudition.paper.data.model.BoardSubject;
import in.co.erudition.paper.data.model.PresetResponseCode;
import in.co.erudition.paper.data.model.UniversityCourse;
import in.co.erudition.paper.data.remote.BackendService;
import in.co.erudition.paper.network.NetworkUtils;
import in.co.erudition.paper.util.ApiUtils;
import in.co.erudition.paper.util.LimitArrayAdapter;
import in.co.erudition.paper.util.PreferenceUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileEditActivity extends AppCompatActivity {

    private String person_details[] = new String[5];
    private String eid;
    private Context context;
    private int selector = 0;
    private String params[];
    private ArrayList<HashMap<String,String>> listCodePair;
    private ArrayList<ArrayList<String>> list;
    private BackendService mService;
    private Call<UniversityCourse> CourseCall;
    private Call<PresetResponseCode> personCall;
    private Call<PresetResponseCode> favCall;
    private Call<List<BoardCollege>> CollCall;
    private NetworkUtils mNetworkUtils = new NetworkUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        context = this;

        //init
        params = new String[]{"0", "0", "0", "0"};
        listCodePair = new ArrayList<HashMap<String, String>>();
        list = new ArrayList<ArrayList<String>>();
        for (int i=0;i<4;i++){
            list.add(new ArrayList<String>());
            listCodePair.add(new HashMap<String, String>());
        }

//         To set the background of the activity go below the StatusBar
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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (Build.VERSION.SDK_INT >= 20) {
            ViewCompat.setOnApplyWindowInsetsListener(appBarLayout, (View v, WindowInsetsCompat insets) -> {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                params.topMargin = insets.getSystemWindowInsetTop();
                v.invalidate();
                v.requestLayout();

                params = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
                params.bottomMargin = insets.getSystemWindowInsetBottom();
                params.bottomMargin += getResources().getDimensionPixelSize(R.dimen.spacer_10dp);
                fab.invalidate();
                fab.requestLayout();

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
        toolbar.setTitle(getResources().getString(R.string.edit_profile));
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        fab.setOnClickListener(view -> {
            //Check if data has been entered in any input
            //and make the api calls
//                fab.show
            getPersonalData();
            updatePerson();
            //Now update the fav
            params = PreferenceUtils.getAcademicDetails(params);
            updateFavourite();
        });

        eid = PreferenceUtils.getEid();

        //Show already saved data from prefs
        showPersonalDataFrmPrefs();

        //Setup the Spinners
        setupSpinners();

//        DropDownView uni_drop = (DropDownView) findViewById(R.id.uni_drop);
//        uni_drop.setDropDownItemList(Arrays.asList("MAKAUT","WBSTCE","CU"));
//        uni_drop.setOnSelectionListener((dropDownView, position) -> {
//            Snackbar.make((CoordinatorLayout) findViewById(R.id.edit_activity_main_layout), "Position: " + position, Snackbar.LENGTH_LONG)
//                    .show();
//        });

        //Date picker
        final TextInputEditText inputLayout = (TextInputEditText) findViewById(R.id.date_of_birth_tv);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.date_of_birth);

        inputLayout.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileEditActivity.this,
                    (view1, year1, monthOfYear, dayOfMonth) -> inputLayout.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1), year, month, day);
            datePickerDialog.show();
        });

        mService = ApiUtils.getBackendService();

    }

    private void setupSpinners() {
        try {
            NiceSpinner uniSpinner = (NiceSpinner) findViewById(R.id.uni_drop);
            AutoCompleteTextView collSpinner = (AutoCompleteTextView) findViewById(R.id.coll_drop);
            NiceSpinner deptSpinner = (NiceSpinner) findViewById(R.id.dept_drop);
            NiceSpinner semSpinner = (NiceSpinner) findViewById(R.id.sem_drop);

            HashMap<String,String> unisMap = PreferenceUtils.getUniversitiesList();
            Log.d("HashMAp",unisMap.toString());
            List<String> uniList = new ArrayList<String>(unisMap.keySet());
            List<String> dataset = new LinkedList<>(Arrays.asList("University", "College", "Department", "Semester"));
            uniList.add(0,dataset.get(0));
            uniSpinner.attachDataSource(uniList);
            uniSpinner.setSelectedIndex(0);

            String[] colleges = new String[] {"College"};
            LimitArrayAdapter<String> adapter = new LimitArrayAdapter<String>(this,
                    R.layout.drop_down_list_item, colleges,3);
            collSpinner.setDropDownVerticalOffset(getResources().getDimensionPixelOffset(R.dimen.spacer_2dp));
            collSpinner.setThreshold(1);//will start working from first character
            collSpinner.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
//            collSpinner.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white));
//            collSpinner.setTextColor(getResources().getColor(R.color.colorMaterialBlack_no_alpha));

            deptSpinner.attachDataSource(Arrays.asList("Department"));
            deptSpinner.setSelectedIndex(0);
            semSpinner.attachDataSource(Arrays.asList("Semester"));
            semSpinner.setSelectedIndex(0);

            //Setup click listeners on the drop down arrow
            uniSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("UniSpinner",uniList.get(position));
                    if (position!=0){
                        params[0] = unisMap.get(uniList.get(position));
                        //Now make the api call with that boardCode
                        selector = 1;
                        //send the spinner to update the data
                        loadCourses(deptSpinner);
                        //Also make a getCollege() call
                        loadCollege(collSpinner);
                        //Save this selection in pref
                        PreferenceUtils.setBoard(unisMap.get(uniList.get(position)),uniList.get(position));
                    }
                    Log.d("UniSpinner_params",Arrays.toString(params));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.d("UniSpinner","Nothing Selected!");
                }
            });

            collSpinner.setOnItemClickListener((parent, view, position, id) -> {
                Log.d("CollSpinner",list.get(0).get(position));
                //now set the College code
                params[1] = listCodePair.get(0).get(list.get(0).get(position));
                PreferenceUtils.setCollege(params[1],list.get(0).get(position));

                Log.d("CollSpinner_params",Arrays.toString(params));
            });

            deptSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("DeptSpinner",list.get(1).get(position));
                    if (position!=0){
                        //now set the Department code
                        params[2] = listCodePair.get(1).get(list.get(1).get(position));
                        //Now make the api call with that boardCode, CourseCode, DeptCode
                        selector = 2;
                        //send the spinner to update the data
                        loadCourses(semSpinner);
                        //Save to perf
                        PreferenceUtils.setCourse(params[2],list.get(1).get(position));

                        Log.d("DeptSpinner_params",Arrays.toString(params));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.d("DeptSpinner","Nothing Selected!");
                }
            });

            semSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("SemSpinner",list.get(2).get(position));
                    if (position!=0){
                        //now set the Semester code
                        params[3] = listCodePair.get(2).get(list.get(2).get(position));
                        //Save to pref
                        PreferenceUtils.setSession(params[3],list.get(2).get(position));

                        Log.d("SemSpinner_params",Arrays.toString(params));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.d("SemSpinner","Nothing Selected!");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPersonalDataFrmPrefs() {
        try {
            TextInputEditText first_name = (TextInputEditText) findViewById(R.id.first_name_tv);
            TextInputEditText last_name = (TextInputEditText) findViewById(R.id.last_name_tv);
            TextInputEditText phone = (TextInputEditText) findViewById(R.id.phone_tv);
            NiceSpinner gender = (NiceSpinner) findViewById(R.id.gender_tv);
            TextInputEditText dob = (TextInputEditText) findViewById(R.id.date_of_birth_tv);

            List<String> gender_set = new LinkedList<>(Arrays.asList("Male", "Female", "Unspecified"));
            gender.attachDataSource(gender_set);

            person_details = PreferenceUtils.readPersonalDetails(person_details);

            first_name.setText(person_details[0]);
            last_name.setText(person_details[1]);
            phone.setText(person_details[2]);
            int index = gender_set.indexOf(person_details[3]);
            gender.setSelectedIndex((index>=0 && index<3)?index:2);
            dob.setText(person_details[4]);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getPersonalData() {

        try {
            TextInputEditText first_name = (TextInputEditText) findViewById(R.id.first_name_tv);
            TextInputEditText last_name = (TextInputEditText) findViewById(R.id.last_name_tv);
            TextInputEditText phone = (TextInputEditText) findViewById(R.id.phone_tv);
            NiceSpinner gender = (NiceSpinner) findViewById(R.id.gender_tv);
            TextInputEditText dob = (TextInputEditText) findViewById(R.id.date_of_birth_tv);

            person_details[0] = first_name.getText().toString();
            person_details[1] = last_name.getText().toString();
            person_details[2] = phone.getText().toString();
            person_details[3] = gender.getText().toString();
            person_details[4] = dob.getText().toString();

//            for (int i = 0; i < 5; i++) {
//                if (!person_details[i].contentEquals("")) {
//                    selector++;
//                }
//            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void updatePerson() {

        personCall = mService.updatePerson(eid, person_details[0], person_details[1], person_details[2], person_details[3], person_details[4]);

        personCall.enqueue(new Callback<PresetResponseCode>() {
            @Override
            public void onResponse(Call<PresetResponseCode> call, Response<PresetResponseCode> response) {
                //If successful just display that update was successful
                Log.d("Call", call.request().toString());
                if (response.isSuccessful()) {
                    Log.d("updatePerson", "Successful!");
                    if (response.body() != null) {
                        Log.d("Message:", response.body().getMsg());
                    }
                    // showPersonalDataFrmPrefs();

                    Snackbar.make((CoordinatorLayout) findViewById(R.id.edit_activity_main_layout), getString(R.string.successfully_updated), Snackbar.LENGTH_LONG)
                            .show();


                } else {
                    int statusCode = response.code();
                    if (statusCode == 401) {
                        Log.d("StatusCode", "Unauthorized");
                    }
                    if (statusCode == 500){
                        Log.d("StatusCode", "Internal Server Error");
                        Snackbar.make((CoordinatorLayout) findViewById(R.id.edit_activity_main_layout), getString(R.string.error_occurred), Snackbar.LENGTH_LONG)
                                .show();
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
                Snackbar.make((CoordinatorLayout) findViewById(R.id.edit_activity_main_layout), getString(R.string.error_occurred), Snackbar.LENGTH_LONG)
                        .show();
            }
        });

    }

    private void updateFavourite() {

        Log.d("ProfileEditActivity", "updateFavMethod");

        favCall = mService.setFavourite(eid, params[0], params[2], params[3], params[1]);

        Log.d("Params", Arrays.toString(params));
        favCall.enqueue(new Callback<PresetResponseCode>() {
            @Override
            public void onResponse(Call<PresetResponseCode> call, Response<PresetResponseCode> response) {
                Log.d("Call", call.request().toString());
                if (response.isSuccessful()) {
                    Log.d("updateFavourite", "isSuccess");

                    Log.d("Response Body", response.body().toString());


                } else {
                    int statusCode = response.code();
                    if (statusCode == 401) {
                        Log.d("StatusCode", "Unauthorized");
                    }
                    if (statusCode == 500){
                        Log.d("StatusCode", "Internal Server Error");
                        Snackbar.make((CoordinatorLayout) findViewById(R.id.edit_activity_main_layout), getString(R.string.error_occurred), Snackbar.LENGTH_LONG)
                                .show();
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
                Snackbar.make((CoordinatorLayout) findViewById(R.id.edit_activity_main_layout), getString(R.string.error_occurred), Snackbar.LENGTH_LONG)
                        .show();
            }
        });

    }

    private void loadCollege(AutoCompleteTextView spinner){
        Log.d("ProfileEditActivity", "loadCollegeMethod");

        CollCall = mService.getCollege(params[0]);

        CollCall.enqueue(new Callback<List<BoardCollege>>() {
            @Override
            public void onResponse(Call<List<BoardCollege>> call, Response<List<BoardCollege>> response) {
                Log.d("Call", call.request().toString());
                if (response.isSuccessful()) {
                    Log.d("loadCollege", "isSuccess");

                    if (response.body()!=null) {

                        Log.d("Response Body", response.body().toString());
                        for (BoardCollege college: response.body()) {
                            if (college.getStatus()!=null) {
                                if (college.getStatus().contentEquals("Active")) {
                                    listCodePair.get(0).put(college.getFullName(), college.getCode());
                                    list.get(0).add(college.getFullName());
                                }
                            }
                        }
                        //Display the college list with Auto Complete
                        LimitArrayAdapter<String> adapter = new LimitArrayAdapter<String>(ProfileEditActivity.this,
                                R.layout.drop_down_list_item, list.get(0),3);
                        spinner.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<BoardCollege>> call, Throwable t) {
                Snackbar.make((CoordinatorLayout) findViewById(R.id.edit_activity_main_layout), getString(R.string.error_occurred), Snackbar.LENGTH_LONG)
                        .show();
            }
        });

    }

    private void loadCourses(NiceSpinner spinner) {

        Log.d("ProfileEditActivity", "loadCoursesMethod");

        switch (selector) {
            case 1:
                CourseCall = mService.getCourses(params[0]);
                break;
            case 2:
                CourseCall = mService.getCourses(params[0], params[2]);
                break;
        }
        Log.d("Params", Arrays.toString(params));
        CourseCall.enqueue(new Callback<UniversityCourse>() {

            @Override
            public void onResponse(Call<UniversityCourse> call, Response<UniversityCourse> response) {
                Log.d("Call", call.request().toString());
                if (response.isSuccessful()) {
                    Log.d("loadCourses", "isSuccess");

                    Log.d("Response Body", response.body().toString());
                    listCodePair.get(selector).clear();
                    list.get(selector).clear();
                    switch (selector) {
                        case 1:
                            list.get(1).add("Department");
                            if (response.body().getBoardCourse()!=null) {
                                for (BoardCourse obj: response.body().getBoardCourse()) {
                                    if (obj.getState()!=null) {
                                        if (obj.getState().contentEquals("Active")) {
                                            listCodePair.get(1).put(obj.getName(), obj.getCode());
                                            list.get(1).add(obj.getName());
                                        }
                                    }
                                }
                            }
                            break;
                        case 2:
                            list.get(2).add("Semester");
                            if (response.body().getBoardCourse().get(0).getBoardSession()!=null) {
                                for (BoardSession obj: response.body().getBoardCourse().get(0).getBoardSession()) {
                                    if (obj.getState()!=null) {
                                        if (obj.getState().contentEquals("Active")) {
                                            listCodePair.get(2).put(obj.getFullName(),obj.getCode());
                                            list.get(2).add(obj.getFullName());
                                        }
                                    }
                                }
                            }
                            break;
                    }
                    spinner.attachDataSource(list.get(selector));

                    Log.d("ProfileEditActivity", "API success");
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
            public void onFailure(Call<UniversityCourse> call, Throwable t) {
                Snackbar.make((CoordinatorLayout) findViewById(R.id.edit_activity_main_layout), getString(R.string.error_occurred), Snackbar.LENGTH_LONG)
                        .show();
            }
        });

    }

}
