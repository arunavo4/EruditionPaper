package com.firebase.ui.auth.ui.idp;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.R;
import com.firebase.ui.auth.data.model.FlowParameters;
import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.auth.data.remote.FacebookSignInHandler;
import com.firebase.ui.auth.data.remote.GoogleSignInHandler;
import com.firebase.ui.auth.data.remote.TwitterSignInHandler;
import com.firebase.ui.auth.ui.InvisibleActivityBase;
import com.firebase.ui.auth.util.ExtraConstants;
import com.firebase.ui.auth.util.data.ProviderUtils;
import com.firebase.ui.auth.viewmodel.ResourceObserver;
import com.firebase.ui.auth.viewmodel.idp.ProviderSignInBase;
import com.firebase.ui.auth.viewmodel.idp.SocialProviderResponseHandler;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class SingleSignInActivity extends InvisibleActivityBase {
    private SocialProviderResponseHandler mHandler;
    private ProviderSignInBase<?> mProvider;

    public static Intent createIntent(Context context, FlowParameters flowParams, User user) {
        return createBaseIntent(context, SingleSignInActivity.class, flowParams)
                .putExtra(ExtraConstants.USER, user);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User user = User.getUser(getIntent());
        final String provider = user.getProviderId();

        Log.d("auth:SingleSignIn",provider);

        AuthUI.IdpConfig providerConfig =
                ProviderUtils.getConfigFromIdps(getFlowParams().providerInfo, provider);
        if (providerConfig == null) {
            finish(RESULT_CANCELED, IdpResponse.getErrorIntent(new FirebaseUiException(
                    ErrorCodes.DEVELOPER_ERROR,
                    "Provider not enabled: " + provider)));
            return;
        }

        ViewModelProvider supplier = ViewModelProviders.of(this);

        mHandler = supplier.get(SocialProviderResponseHandler.class);
        mHandler.init(getFlowParams());

        switch (provider) {
            case GoogleAuthProvider.PROVIDER_ID:
                GoogleSignInHandler google = supplier.get(GoogleSignInHandler.class);
                google.init(new GoogleSignInHandler.Params(providerConfig, user.getEmail()));
                mProvider = google;
                break;
            case FacebookAuthProvider.PROVIDER_ID:
                FacebookSignInHandler facebook = supplier.get(FacebookSignInHandler.class);
                facebook.init(providerConfig);
                mProvider = facebook;
                break;
            case TwitterAuthProvider.PROVIDER_ID:
                TwitterSignInHandler twitter = supplier.get(TwitterSignInHandler.class);
                twitter.init(null);
                mProvider = twitter;
                break;
            default:
                throw new IllegalStateException("Invalid provider id: " + provider);
        }

        mProvider.getOperation().observe(this, new ResourceObserver<IdpResponse>(this) {
            @Override
            protected void onSuccess(@NonNull IdpResponse response) {
                Log.d("auth:SingleSignIn","provider onSuccess");
                mHandler.startSignIn(response);
            }

            @Override
            protected void onFailure(@NonNull Exception e) {
                Log.d("auth:SingleSignIn","provider onFailure");
                mHandler.startSignIn(IdpResponse.from(e));
            }
        });

        mHandler.getOperation().observe(this, new ResourceObserver<IdpResponse>(
                this, R.string.fui_progress_dialog_loading) {
            @Override
            protected void onSuccess(@NonNull IdpResponse response) {
                Log.d("auth:SingleSignIn","handler onSuccess");
                Log.d("auth:SingleSignIn","handler onSuccess");
                Log.d("Provider Idp Token",response.getUser().getProviderId() + " : " + response.getIdpToken());
                Log.d("User Email:",response.getEmail());

                try{
                    Class<?> loginUtilClass = Class.forName("in.co.erudition.paper.util.LoginUtils");
                    final Object loginUtil = loginUtilClass.newInstance();

                    final Method login_idp = loginUtil.getClass().getMethod("login_via_idp",String.class,String.class,String.class);
                    try{
                        String message = (String) login_idp.invoke(loginUtil,response.getUser().getProviderId(),response.getEmail(),response.getIdpToken());
                        Log.d("login_idp_method: ",message);
                    }catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                startSaveCredentials(mHandler.getCurrentUser(), response, null);
            }

            @Override
            protected void onFailure(@NonNull Exception e) {
                finish(RESULT_CANCELED, IdpResponse.getErrorIntent(e));
            }
        });

        if (mHandler.getOperation().getValue() == null) {
            mProvider.startSignIn(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mHandler.onActivityResult(requestCode, resultCode, data);
        mProvider.onActivityResult(requestCode, resultCode, data);
    }
}
