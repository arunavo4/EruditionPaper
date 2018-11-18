package in.co.erudition.paper.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.Calendar;
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
import in.co.erudition.paper.data.model.PresetResponseCode;
import in.co.erudition.paper.data.remote.BackendService;
import in.co.erudition.paper.network.NetworkUtils;
import in.co.erudition.paper.util.ApiUtils;
import in.co.erudition.paper.util.PreferenceUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileEditActivity extends AppCompatActivity {

    private String person_details[] = new String[5];
    private String eid;
    private Context context;
    private int selector = 0;
    private BackendService mService;
    private Call<PresetResponseCode> personCall;
    private NetworkUtils mNetworkUtils = new NetworkUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        context = this;

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
        });

        eid = PreferenceUtils.getEid();

        //Show already saved data from prefs
        showPersonalDataFrmPrefs();

        //Setup the Spinners
        NiceSpinner uniSpinner = (NiceSpinner) findViewById(R.id.uni_drop);
        NiceSpinner collSpinner = (NiceSpinner) findViewById(R.id.college_drop);
        NiceSpinner deptSpinner = (NiceSpinner) findViewById(R.id.dept_drop);
        NiceSpinner semSpinner = (NiceSpinner) findViewById(R.id.sem_drop);
        List<String> dataset = new LinkedList<>(Arrays.asList("University", "College", "Department", "Semester"));
        uniSpinner.attachDataSource(dataset);
        uniSpinner.setSelectedIndex(0);
        collSpinner.attachDataSource(dataset);
        collSpinner.setSelectedIndex(1);
        deptSpinner.attachDataSource(dataset);
        deptSpinner.setSelectedIndex(2);
        semSpinner.attachDataSource(dataset);
        semSpinner.setSelectedIndex(3);
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

    private void showPersonalDataFrmPrefs() {
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
        gender.setSelectedIndex(gender_set.indexOf(person_details[3]));
        dob.setText(person_details[4]);

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

            for (int i = 0; i < 5; i++) {
                if (!person_details[i].contentEquals("")) {
                    selector++;
                }
            }

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
                    Log.d("EditDetails:", "Successful!");
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

}
