package com.firebase.ui.auth.viewmodel.email;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.firebase.ui.auth.data.model.Resource;
import com.firebase.ui.auth.viewmodel.AuthViewModelBase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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

    public void forgotPassword(final String email){

//        try{
//            Class<?> loginUtilClass = Class.forName("in.co.erudition.paper.util.LoginUtils");
//            final Object loginUtil = loginUtilClass.newInstance();
//
//            final Method login_idp = loginUtil.getClass().getMethod("login_via_idp",String.class,String.class,String.class);
//            try{
//                String message = (String) login_idp.invoke(loginUtil,response.getUser().getProviderId(),response.getEmail(),response.getIdpToken());
//                Log.d("login_idp_method: ",message);
//            }catch (IllegalAccessException | InvocationTargetException e) {
//                e.printStackTrace();
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }
}
