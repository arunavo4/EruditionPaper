package com.firebase.ui.auth.viewmodel.idp;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import retrofit2.Callback;

import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.IntentRequiredException;
import com.firebase.ui.auth.data.model.Resource;
import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.auth.data.remote.ProfileMerger;
import com.firebase.ui.auth.ui.email.Login;
import com.firebase.ui.auth.ui.email.WelcomeBackPasswordPrompt;
import com.firebase.ui.auth.ui.idp.WelcomeBackIdpPrompt;
import com.firebase.ui.auth.util.data.ProviderUtils;
import com.firebase.ui.auth.util.data.TaskFailureLogger;
import com.firebase.ui.auth.viewmodel.AuthViewModelBase;
import com.firebase.ui.auth.viewmodel.RequestCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class EmailProviderResponseHandler extends AuthViewModelBase<IdpResponse> {
    private static final String TAG = "EmailProviderResponseHa";

    public EmailProviderResponseHandler(Application application) {
        super(application);
    }

    public void startSignIn(@NonNull final IdpResponse response, @NonNull String password) {
        if (!response.isSuccessful()) {
            setResult(Resource.<IdpResponse>forFailure(response.getError()));
            return;
        }
        if (!response.getProviderType().equals(EmailAuthProvider.PROVIDER_ID)) {
            throw new IllegalStateException(
                    "This handler can only be used with the email provider");
        }
        setResult(Resource.<IdpResponse>forLoading());

        final String email = response.getEmail();
        getAuth().createUserWithEmailAndPassword(email, password)
                .continueWithTask(new ProfileMerger(response))
                .addOnFailureListener(new TaskFailureLogger(TAG, "Error creating user"))
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult result) {
                        setResult(Resource.forSuccess(response));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            // Collision with existing user email, it should be very hard for
                            // the user to even get to this error due to CheckEmailFragment.
                            ProviderUtils.fetchTopProvider(getAuth(), email)
                                    .addOnSuccessListener(new StartWelcomeBackFlow(email))
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            setResult(Resource.<IdpResponse>forFailure(e));
                                        }
                                    });
                        } else {
                            setResult(Resource.<IdpResponse>forFailure(e));
                        }
                    }
                });
    }

    private class StartWelcomeBackFlow implements OnSuccessListener<String> {
        private final String mEmail;

        public StartWelcomeBackFlow(String email) {
            mEmail = email;
        }

        @Override
        public void onSuccess(String provider) {
            if (provider == null) {
                throw new IllegalStateException(
                        "User has no providers even though we got a collision.");
            }

            if (EmailAuthProvider.PROVIDER_ID.equalsIgnoreCase(provider)) {
                setResult(Resource.<IdpResponse>forFailure(new IntentRequiredException(
                        WelcomeBackPasswordPrompt.createIntent(
                                getApplication(),
                                getArguments(),
                                new IdpResponse.Builder(new User.Builder(
                                        EmailAuthProvider.PROVIDER_ID, mEmail).build()
                                ).build()),
                        RequestCodes.WELCOME_BACK_EMAIL_FLOW
                )));
            } else {
                setResult(Resource.<IdpResponse>forFailure(new IntentRequiredException(
                        WelcomeBackIdpPrompt.createIntent(
                                getApplication(),
                                getArguments(),
                                new User.Builder(provider, mEmail).build()),
                        RequestCodes.WELCOME_BACK_IDP_FLOW
                )));
            }
        }
    }


    /*
        Register Email APi call
     */
    public void sendConfirmation(String email, Callback<Login> callback){
        Log.d("SignupResponseHandler","sendConfirmation");
        try{
            Class<?> loginUtilClass = Class.forName("in.co.erudition.paper.util.LoginUtils");
            final Object loginUtil = loginUtilClass.newInstance();

            final Method signUpViaEmail = loginUtil.getClass().getMethod("signUp_via_Email",String.class,Callback.class);
            try{
                signUpViaEmail.invoke(loginUtil,email,callback);
            }catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
        Now confirm Email
     */
    public void registerUser(String email, String fname, String lname, String secretCode, String password, Callback<Login> callback){
        Log.d("SignupResponseHandler","registerUser");
        try{
            Class<?> loginUtilClass = Class.forName("in.co.erudition.paper.util.LoginUtils");
            final Object loginUtil = loginUtilClass.newInstance();

            final Method confirmEmail = loginUtil.getClass().getMethod("confirm_email",String.class,String.class,String.class,String.class,String.class,Callback.class);
            try{
                confirmEmail.invoke(loginUtil,email,fname,lname,secretCode,password,callback);
            }catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loginWithCallback(String email,String password,Callback<Login> callback){
        Log.d("SignupResponseHandler","loginWithCallback");
        String provider = "Email";
        try{
            Class<?> loginUtilClass = Class.forName("in.co.erudition.paper.util.LoginUtils");
            final Object loginUtil = loginUtilClass.newInstance();

            final Method loginViaEmail = loginUtil.getClass().getMethod("login_via_email",String.class,String.class,Callback.class);
            try{
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
}
