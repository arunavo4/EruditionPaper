package in.co.erudition.paper;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import in.co.erudition.paper.activitiy.MainActivity;
import in.co.erudition.paper.network.NetworkUtils;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    //Network helper class
    private NetworkUtils mNetworkUtils = new NetworkUtils();

    private FirebaseAuth mAuth;

    //An Arbitrary Request code value for FireBase AuthUI
    public static final int RC_SIGN_IN = 240;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Instantiate the variable
        mAuth = FirebaseAuth.getInstance();

    }

    /**
     * Update the UI accordingly
     *
     * @param user
     */
    public void updateUI(FirebaseUser user) {
        if (user != null) {
            // User is signed in
            Intent home_activity = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(home_activity);
            LoginActivity.this.finish();
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.FacebookBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                            .setTheme(R.style.AuthUI_Theme)
                            .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                            .build(),
                    RC_SIGN_IN);
            // Can also add the Privacy Policy and Terms of Service
//                    .setTosAndPrivacyPolicyUrls("https://superapp.example.com/terms-of-service.html",
//                                    "https://superapp.example.com/privacy-policy.html")
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            Log.d(TAG, "response");
            // Successfully signed in
            if (resultCode == RESULT_OK) {
                //Take to main activity
                Intent home_activity = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(home_activity);
                finish();
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "sign_in_cancelled", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!mNetworkUtils.isOnline(this)) {
                    LinearLayoutCompat mlinearLayoutCompat = (LinearLayoutCompat) findViewById(R.id.login_splash);
                    Snackbar.make(mlinearLayoutCompat, "No Network Connection", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    updateUI(currentUser);
                                }
                            }).show();
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "Unknown Error", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
}
