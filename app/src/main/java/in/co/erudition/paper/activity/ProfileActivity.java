package in.co.erudition.paper.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import in.co.erudition.avatar.AvatarPlaceholder;
import in.co.erudition.paper.Erudition;
import in.co.erudition.paper.LoginActivity;
import in.co.erudition.paper.R;
import in.co.erudition.paper.data.model.Person;
import in.co.erudition.paper.data.remote.BackendService;
import in.co.erudition.paper.util.ApiUtils;
import in.co.erudition.paper.util.GlideApp;
import in.co.erudition.paper.util.PreferenceUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ProfileActivity extends AppCompatActivity {
    private String person_details[] = new String[5];
    private BackendService mService;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private static SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_container);
//        TextView profile_name = (TextView) findViewById(R.id.profile_name_tv);        //old version
        ImageView img_profile_pic = (ImageView) findViewById(R.id.img_profile_pic);

        //Shared Preferences
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

        //Read existing data from prefs
        showPersonalDataFrmPrefs();

        mService = ApiUtils.getBackendService();

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
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.my_appbar_container);

        if (Build.VERSION.SDK_INT >= 20) {
            ViewCompat.setOnApplyWindowInsetsListener(appBarLayout, (View v, WindowInsetsCompat insets) -> {
                v.getLayoutParams().height -= getResources().getDimensionPixelSize(R.dimen.status_bar_height);
                v.getLayoutParams().height += insets.getSystemWindowInsetTop();

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
        collapsingToolbarLayout.setTitle(PreferenceUtils.readName());
        setSupportActionBar(toolbar);
        //profile_name.setText(name);       //No more required (old version_)

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fab on click
                Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
                startActivity(intent);
            }
        });


        /*
            Set the Display Picture
            If there is an avatar URL then display it
            else make a new layer using the initial letters
         */
        setProfilePicture(img_profile_pic);


        loadPersonDetails(mPrefs.getString("EId",""),mPrefs.getString("Email",""));
    }

    private void loadPersonDetails(String Eid, String Email) {
        String id = "";
        Call<Person> personCall;

        if (Eid.contentEquals("")) {
            id = Eid;
            personCall = mService.getPersonDetailsEid(id);
        } else{
            id = Email;
            personCall = mService.getPersonDetailsEmail(id);
        }

        if (!id.contentEquals("")) {
            personCall.enqueue(new Callback<Person>() {
                @Override
                public void onResponse(Call<Person> call, Response<Person> response) {
                    Log.d("Call", call.request().toString());
                    if (response.isSuccessful()) {
                        //Write to Shared Preferences
                        Person person = response.body();

                        PreferenceUtils.writePersonDetails(person);

                        Log.d("Person Call:", "Successfully Written Details");
                        //read from prefs
                        showPersonalDataFrmPrefs();
                        collapsingToolbarLayout.setTitle(PreferenceUtils.readName());
                        //Also change the name

//                    setProfilePicture(img_profile_pic);

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
                public void onFailure(Call<Person> call, Throwable t) {
                    Log.d("Person Call:", "Failed to get Person Details");
                }
            });
        }
    }

    private void setProfilePicture(ImageView imageView){
        String image_uri = getProfileAvatar();
        AvatarPlaceholder placeholder = new AvatarPlaceholder(this, getInitials(PreferenceUtils.readName()));

//        AvatarGlideLoader imageLoader = new AvatarGlideLoader(getInitials(name));
//        imageLoader.loadImage(img_profile_pic, placeholder, image_uri);
        GlideApp
                .with(this)
                .load(image_uri)
                .apply(RequestOptions.placeholderOf(placeholder)
                        .fitCenter())
                .transition(withCrossFade())
                .error(placeholder)
                .circleCrop()
                .into(imageView);
    }

    private void setPersonalDetails(){
        TextView first_name = (TextView) findViewById(R.id.first_name_tv_p);
        TextView last_name = (TextView) findViewById(R.id.last_name_tv_p);
        TextView phone = (TextView) findViewById(R.id.phone_tv_p);
        TextView gender = (TextView) findViewById(R.id.gender_tv_p);
        TextView dob = (TextView) findViewById(R.id.date_of_birth_tv_p);

    }

    private void showPersonalDataFrmPrefs(){
        TextView first_name = (TextView) findViewById(R.id.first_name_tv_p);
        TextView last_name = (TextView) findViewById(R.id.last_name_tv_p);
        TextView phone = (TextView) findViewById(R.id.phone_tv_p);
        TextView gender = (TextView) findViewById(R.id.gender_tv_p);
        TextView dob = (TextView) findViewById(R.id.date_of_birth_tv_p);
        TextView email = (TextView) findViewById(R.id.email_tv_p);

        person_details = PreferenceUtils.readPersonalDetails(person_details);

        first_name.setText(person_details[0]);
        last_name.setText(person_details[1]);
        phone.setText(person_details[2]);
        gender.setText(person_details[3]);
        dob.setText(person_details[4]);
        email.setText(PreferenceUtils.readEmail());

    }

    private String getProfileAvatar() {

        String uri_profile_img = mPrefs.getString("ProfileImage", "");
        if(!uri_profile_img.contentEquals("")){
            return uri_profile_img;
        }else{
            uri_profile_img = mPrefs.getString("Avatar", "");
        }
        Log.d("Avatar: ", uri_profile_img);

        return uri_profile_img;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            Log.d("ProfileActivity", "Signed Out!");
                            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /*
     * Return user short name
     */
    private static String getInitials(String name) {

        String[] strings = name.split(" ");//no i18n
        String shortName;
        if (strings.length == 1) {
            shortName = strings[0].substring(0, 2);
        } else {
            shortName = strings[0].substring(0, 1) + strings[1].substring(0, 1);
        }
        return shortName.toUpperCase();
    }

}
