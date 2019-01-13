package com.firebase.ui.auth.viewmodel.email;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import retrofit2.Callback;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.Resource;
import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.auth.data.remote.ProfileMerger;
import com.firebase.ui.auth.ui.email.Login;
import com.firebase.ui.auth.util.data.TaskFailureLogger;
import com.firebase.ui.auth.viewmodel.AuthViewModelBase;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Handles the logic for {@link com.firebase.ui.auth.ui.email.WelcomeBackPasswordPrompt} including
 * signing in with email and password, linking other credentials, and saving credentials to
 * SmartLock.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class WelcomeBackPasswordHandler extends AuthViewModelBase<IdpResponse> {
    private static final String TAG = "WBPasswordHandler";

    private String mPendingPassword;

    public WelcomeBackPasswordHandler(Application application) {
        super(application);
    }

    /**
     * Kick off the sign-in process.
     */
    public void startSignIn(@NonNull final String email,
                            @NonNull final String password,
                            @NonNull final IdpResponse inputResponse,
                            @Nullable final AuthCredential credential) {
        setResult(Resource.<IdpResponse>forLoading());

        // Store the password before signing in so it can be used for later credential building
        mPendingPassword = password;

        // Build appropriate IDP response based on inputs
        final IdpResponse outputResponse;
        if (credential == null) {
            // New credential for the email provider
            outputResponse = new IdpResponse.Builder(
                    new User.Builder(EmailAuthProvider.PROVIDER_ID, email).build())
                    .build();
        } else {
            // New credential for an IDP (Phone or Social)
            outputResponse = new IdpResponse.Builder(inputResponse.getUser())
                    .setToken(inputResponse.getIdpToken())
                    .setSecret(inputResponse.getIdpSecret())
                    .build();
        }

        // Kick off the flow including signing in, linking accounts, and saving with SmartLock
        getAuth().signInWithEmailAndPassword(email, password)
                .continueWithTask(new Continuation<AuthResult, Task<AuthResult>>() {
                    @Override
                    public Task<AuthResult> then(@NonNull Task<AuthResult> task) throws Exception {
                        // Forward task failure by asking for result
                        AuthResult result = task.getResult(Exception.class);

                        // Task succeeded, link user if necessary
                        if (credential == null) {
                            return Tasks.forResult(result);
                        } else {
                            return result.getUser()
                                    .linkWithCredential(credential)
                                    .continueWithTask(new ProfileMerger(outputResponse))
                                    .addOnFailureListener(new TaskFailureLogger(TAG, "linkWithCredential+merge failed."));
                        }
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            setResult(Resource.<IdpResponse>forFailure(task.getException()));
                            return;
                        }

                        setResult(Resource.forSuccess(outputResponse));
                    }
                })
                .addOnFailureListener(
                        new TaskFailureLogger(TAG, "signInWithEmailAndPassword failed."));
    }

    /**
     * Get the most recent pending password.
     */
    public String getPendingPassword() {
        return mPendingPassword;
    }

    public void updatePassword(FirebaseUser user, AuthCredential credential, final String password){
        // Prompt the user to re-provide their sign-in credentials
        Log.d(TAG, "updatePassword()");

        final FirebaseUser firebaseUser = user;
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Password updated");
                                    } else {
                                        Log.d(TAG, "Error password not updated");
                                    }
                                }
                            });
                        } else {
                            Log.d(TAG, "Error auth failed");
                        }
                    }
                });
    }


    public void deleteUser(Context context){
        // Prompt the user to re-provide their sign-in credentials
        Log.d(TAG, "deleteUser()");

        AuthUI.getInstance()
                .delete(context)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG,"Delete Success");
                            // Deletion succeeded
                        } else {
                            Log.d(TAG,"Delete failed");
                            Log.d(TAG,Objects.requireNonNull(task.getException()).getMessage());
                            // Deletion failed
                        }
                    }
                });
    }

    public void loginWithCallback(String email, String password, Callback<Login> callback){
        Log.d("SignupResponseHandler","loginWithCallback");
        String provider = "Email";
        try{
            Class<?> loginUtilClass = Class.forName("in.co.erudition.paper.util.LoginUtils");
            final Object loginUtil = loginUtilClass.newInstance();

            final Method loginViaEmail = loginUtil.getClass().getMethod("login_via_email",String.class,String.class,Callback.class);
            try{
                setResult(Resource.<IdpResponse>forLoading());
                loginViaEmail.invoke(loginUtil,email,password,callback);
            }catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
        Login_via_idp to sign in
     */
    public void loginUser(String email,String password){
        Log.d("SignupResponseHandler","loginUser");
        String provider = "Email";
        try{
            Class<?> loginUtilClass = Class.forName("in.co.erudition.paper.util.LoginUtils");
            final Object loginUtil = loginUtilClass.newInstance();

            final Method confirmEmail = loginUtil.getClass().getMethod("login_via_idp",String.class,String.class,String.class);
            try{
                confirmEmail.invoke(loginUtil,provider,email,password);
            }catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void done(){
        setResult(Resource.<IdpResponse>forDone());
    }

}
