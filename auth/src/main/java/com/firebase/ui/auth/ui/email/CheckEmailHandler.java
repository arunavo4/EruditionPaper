package com.firebase.ui.auth.ui.email;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import retrofit2.Callback;

import com.firebase.ui.auth.data.model.PendingIntentRequiredException;
import com.firebase.ui.auth.data.model.Resource;
import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.auth.util.data.ProviderUtils;
import com.firebase.ui.auth.viewmodel.AuthViewModelBase;
import com.firebase.ui.auth.viewmodel.RequestCodes;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

@SuppressWarnings("WrongConstant")
public class CheckEmailHandler extends AuthViewModelBase<User> {
    public CheckEmailHandler(Application application) {
        super(application);
    }

    public void fetchCredential() {
        Log.d("Inside Email HAndler","fetchCredential");
        setResult(Resource.<User>forFailure(new PendingIntentRequiredException(
                Credentials.getClient(getApplication()).getHintPickerIntent(
                        new HintRequest.Builder().setEmailAddressIdentifierSupported(true).build()),
                RequestCodes.CRED_HINT
        )));
    }

    public void fetchProvider(final String email) {
        Log.d("Inside Email HAndler","fetchProvider");
        setResult(Resource.<User>forLoading());
        ProviderUtils.fetchTopProvider(getAuth(), email)
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            setResult(Resource.forSuccess(
                                    new User.Builder(task.getResult(), email).build()));
                        } else {
                            setResult(Resource.<User>forFailure(task.getException()));
                        }
                    }
                });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != RequestCodes.CRED_HINT || resultCode != Activity.RESULT_OK) { return; }

        Log.d("Inside Email HAndler","onActivityResult");
        setResult(Resource.<User>forLoading());
        final Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
        final String email = credential.getId();
        ProviderUtils.fetchTopProvider(getAuth(), email)
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            setResult(Resource.forSuccess(new User.Builder(task.getResult(), email)
                                    .setName(credential.getName())
                                    .setPhotoUri(credential.getProfilePictureUri())
                                    .build()));
                        } else {
                            setResult(Resource.<User>forFailure(task.getException()));
                        }
                    }
                });
    }

    public void checkUserEmail(final String email,Callback<Login> callback){
        Log.d("Inside Email HAndler","checkUserEmail");
        try{
            Class<?> loginUtilClass = Class.forName("in.co.erudition.paper.util.LoginUtils");
            final Object loginUtil = loginUtilClass.newInstance();

            final Method login_via_email = loginUtil.getClass().getMethod("login_via_email",String.class,String.class,Callback.class);
            try{
                login_via_email.invoke(loginUtil,email,null,callback);
            }catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
