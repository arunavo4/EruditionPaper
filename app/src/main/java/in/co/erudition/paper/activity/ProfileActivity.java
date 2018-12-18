package in.co.erudition.paper.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import in.co.erudition.avatar.AvatarPlaceholder;
import in.co.erudition.avatar.AvatarView;
import in.co.erudition.paper.Erudition;
import in.co.erudition.paper.LoginActivity;
import in.co.erudition.paper.R;
import in.co.erudition.paper.data.model.Person;
import in.co.erudition.paper.data.model.PresetResponseCode;
import in.co.erudition.paper.data.remote.BackendService;
import in.co.erudition.paper.util.ApiUtils;
import in.co.erudition.paper.util.AvatarGlideLoader;
import in.co.erudition.paper.util.GlideApp;
import in.co.erudition.paper.util.PreferenceUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ProfileActivity extends AppCompatActivity {
    private String person_details[] = new String[5];
    private BackendService mService;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor mPrefsEdit;
    private Uri mCropImageUri;
    private AvatarView img_profile_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_container);
//        TextView profile_name = (TextView) findViewById(R.id.profile_name_tv);        //old version
        img_profile_pic = (AvatarView) findViewById(R.id.profile_avatar);

        img_profile_pic.setOnClickListener(v -> {
            //Call the picker activity
            CropImage.startPickImageActivity(this);
        });

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

        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        fab.setOnClickListener(view -> {
            //fab on click
            Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
            startActivity(intent);
        });

        /*
            Set the Display Picture
            If there is an avatar URL then display it
            else make a new layer using the initial letters
         */
        setProfilePicture(img_profile_pic);

        loadPersonDetails(mPrefs.getString("EId", ""), mPrefs.getString("Email", ""));
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already granted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                if (result != null) {
                    img_profile_pic.setImageURI(result.getUri());
                    Log.d("Cropping success Sample", String.valueOf(result.getSampleSize()));
                    //Upload the file
                    uploadAvatar(result.getUri().getPath());
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.d("Cropping failed", String.valueOf(result.getError()));
                Toast.makeText(getApplicationContext(), "Cropping failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadAvatar(String  filePath) {
        File file = new File(filePath);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("Avatar", file.getName(), reqFile);

        Call<PresetResponseCode> uploadAvatarCall = mService.uploadAvatar(mPrefs.getString("EId", ""),body);

        uploadAvatarCall.enqueue(new Callback<PresetResponseCode>() {
            @Override
            public void onResponse(Call<PresetResponseCode> call, Response<PresetResponseCode> response) {
                //make the person call again and then Toast
                loadPersonDetails(mPrefs.getString("EId", ""), mPrefs.getString("Email", ""));
                mPrefsEdit = mPrefs.edit();
                mPrefsEdit.putBoolean("Avatar Updated",true);
                mPrefsEdit.apply();
                //Toast
                Toast.makeText(getApplicationContext(),getString(R.string.avatar_updated_msg),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<PresetResponseCode> call, Throwable t) {
                Toast.makeText(getApplicationContext(),getString(R.string.avatar_update_fail_msg),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
            } else {
                Toast.makeText(getApplicationContext(), "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .setRequestedSize(500, 500)
                .start(this);

//                    .setActivityTitle("My Crop")
//                    .setCropShape(CropImageView.CropShape.OVAL)
//                    .setCropMenuCropButtonTitle("Done")
//                    .setCropMenuCropButtonIcon(R.drawable.ic_launcher)
    }

    private void loadPersonDetails(String Eid, String Email) {
        String id = "";
        Call<Person> personCall;

        if (Eid.contentEquals("")) {
            id = Eid;
            personCall = mService.getPersonDetailsEid(id);
        } else {
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

    private void setProfilePicture(ImageView imageView) {
        String image_uri = getProfileAvatar();
        AvatarPlaceholder placeholder = new AvatarPlaceholder(this, getInitials(PreferenceUtils.readName()));

        AvatarGlideLoader imageLoader = new AvatarGlideLoader(getInitials(PreferenceUtils.readName()));
        imageLoader.loadImage(img_profile_pic, placeholder, image_uri);
//        GlideApp
//                .with(this)
//                .load(image_uri)
//                .apply(RequestOptions.placeholderOf(placeholder)
//                        .fitCenter())
//                .transition(withCrossFade())
//                .error(placeholder)
//                .circleCrop()
//                .into(imageView);
    }

    private void setPersonalDetails() {
        TextView first_name = (TextView) findViewById(R.id.first_name_tv_p);
        TextView last_name = (TextView) findViewById(R.id.last_name_tv_p);
        TextView phone = (TextView) findViewById(R.id.phone_tv_p);
        TextView gender = (TextView) findViewById(R.id.gender_tv_p);
        TextView dob = (TextView) findViewById(R.id.date_of_birth_tv_p);

    }

    private void showPersonalDataFrmPrefs() {
        TextView first_name = (TextView) findViewById(R.id.first_name_tv_p);
        TextView last_name = (TextView) findViewById(R.id.last_name_tv_p);
        TextView phone = (TextView) findViewById(R.id.phone_tv_p);
        TextView gender = (TextView) findViewById(R.id.gender_tv_p);
        TextView dob = (TextView) findViewById(R.id.date_of_birth_tv_p);
        TextView email = (TextView) findViewById(R.id.email_tv_p);

        person_details = PreferenceUtils.readPersonalDetails(person_details);

        if (!person_details[0].contentEquals("First Name")) {
            first_name.setText(person_details[0]);
            first_name.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        if (!person_details[1].contentEquals("Last Name")) {
            last_name.setText(person_details[1]);
            last_name.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        if (!person_details[2].contentEquals("Phone")) {
            phone.setText(person_details[2]);
            phone.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        if (!person_details[3].contentEquals("Unspecified")) {
            gender.setText(person_details[3]);
            gender.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        if (!person_details[4].contentEquals("Date of Birth")) {
            dob.setText(person_details[4]);
            dob.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        email.setText(PreferenceUtils.readEmail());

    }

    private String getProfileAvatar() {

        String uri_profile_img = mPrefs.getString("ProfileImage", "");
        if (!uri_profile_img.contentEquals("")) {
            return uri_profile_img;
        } else {
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
