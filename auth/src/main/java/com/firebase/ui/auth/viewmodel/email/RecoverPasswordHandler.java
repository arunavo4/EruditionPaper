package com.firebase.ui.auth.viewmodel.email;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import retrofit2.Callback;

import com.firebase.ui.auth.data.model.Resource;
import com.firebase.ui.auth.ui.email.Login;
import com.firebase.ui.auth.viewmodel.AuthViewModelBase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class RecoverPasswordHandler extends AuthViewModelBase<String> {
    public RecoverPasswordHandler(Application application) {
        super(application);
    }

    public void startReset(final String email) {
        setResult(Resource.<String>forLoading());
        getAuth().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Resource<String> resource = task.isSuccessful()
                                ? Resource.forSuccess(email)
                                : Resource.<String>forFailure(task.getException());
                        setResult(resource);
                    }
                });
    }

    public void forgotPassword(final String email, Callback<Login> callback){

        Log.d("RecoverPass Handler","forgotPassword");
        try{
            Class<?> loginUtilClass = Class.forName("in.co.erudition.paper.util.LoginUtils");
            final Object loginUtil = loginUtilClass.newInstance();

            final Method forgotPassword = loginUtil.getClass().getMethod("forgot_password",String.class,Callback.class);
            try{
                setResult(Resource.<String>forLoading());
                forgotPassword.invoke(loginUtil,email,callback);
            }catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void done(){
        setResult(Resource.<String>forDone());
    }

}
