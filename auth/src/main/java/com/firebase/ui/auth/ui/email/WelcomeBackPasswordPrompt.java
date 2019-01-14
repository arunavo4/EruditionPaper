/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.firebase.ui.auth.ui.email;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StringRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.firebase.ui.auth.data.model.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.R;
import com.firebase.ui.auth.data.model.FlowParameters;
import com.firebase.ui.auth.ui.AppCompatBase;
import com.firebase.ui.auth.util.ExtraConstants;
import com.firebase.ui.auth.util.data.PrivacyDisclosureUtils;
import com.firebase.ui.auth.util.data.ProviderUtils;
import com.firebase.ui.auth.util.ui.ImeHelper;
import com.firebase.ui.auth.viewmodel.ResourceObserver;
import com.firebase.ui.auth.viewmodel.email.WelcomeBackPasswordHandler;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity to link a pre-existing email/password account to a new IDP sign-in by confirming the
 * password before initiating a link.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class WelcomeBackPasswordPrompt extends AppCompatBase
        implements View.OnClickListener, ImeHelper.DonePressedListener {
    private IdpResponse mIdpResponse;
    private WelcomeBackPasswordHandler mHandler;
    private Callback<Login> callback;
    private Callback<Login> firebaseCallback;
    private Button mDoneButton;
    private ProgressBar mProgressBar;
    private TextInputLayout mPasswordLayout;
    private EditText mPasswordField;

    public static Intent createIntent(
            Context context, FlowParameters flowParams, IdpResponse response) {
        return createBaseIntent(context, WelcomeBackPasswordPrompt.class, flowParams)
                .putExtra(ExtraConstants.IDP_RESPONSE, response);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fui_welcome_back_password_prompt_layout);

        // Show keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mIdpResponse = IdpResponse.fromResultIntent(getIntent());
        final String email = mIdpResponse.getEmail();

        mDoneButton = findViewById(R.id.button_done);
        mProgressBar = findViewById(R.id.top_progress_bar);
        mPasswordLayout = findViewById(R.id.password_layout);
        mPasswordField = findViewById(R.id.password);

        ImeHelper.setImeOnDoneListener(mPasswordField, this);

        // Create welcome back text with email bolded.
        String bodyText =
                getString(R.string.fui_welcome_back_password_prompt_body, email);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(bodyText);
        int emailStart = bodyText.indexOf(email);
        spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD),
                emailStart,
                emailStart + email.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        TextView bodyTextView = findViewById(R.id.welcome_back_password_body);
        bodyTextView.setText(spannableStringBuilder);

        // Click listeners
        mDoneButton.setOnClickListener(this);
        findViewById(R.id.trouble_signing_in).setOnClickListener(this);

        // Initialize ViewModel with arguments
        mHandler = ViewModelProviders.of(this).get(WelcomeBackPasswordHandler.class);
        mHandler.init(getFlowParams());

        // Observe the state of the main auth operation
        mHandler.getOperation().observe(this, new ResourceObserver<IdpResponse>(
                this, R.string.fui_progress_dialog_signing_in) {
            @Override
            protected void onSuccess(@NonNull IdpResponse response) {
                startSaveCredentials(
                        mHandler.getCurrentUser(), response, mHandler.getPendingPassword());
//                finish(RESULT_OK,response.toIntent());
            }

            @Override
            protected void onFailure(@NonNull Exception e) {
                //Here there could be a case if Firebase password needs to be updated.
                //And retry
                if (getString(getErrorMessage(e)).equalsIgnoreCase("Incorrect password.")){
                    //MAke the Api call to change the password in firebase
                    mHandler.firebaseUpdate(mIdpResponse.getEmail(),mPasswordField.getText().toString(),firebaseCallback);
                }else {
                    mPasswordLayout.setError(getString(getErrorMessage(e)));
                }
            }
        });

        //First Check Password with Api
        //here call the register email api endpoint
        callback = new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    //Show the info where to get the Secret Code
                    mHandler.done();
                    Login login = response.body();
                    String code = login.getCode();
                    String msg = login.getMsg();
                    if (code.contentEquals("1")){
//                        //Successful login call firebase
                        mHandler.loginUser(mIdpResponse.getEmail(),mPasswordField.getText().toString());
                        validateAndSignIn();
                    }else if (code.contentEquals("7")){
                        //Wrong password
                        mPasswordLayout.setError(msg);
                    }else {
                        Toast.makeText(WelcomeBackPasswordPrompt.this,msg,Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                mHandler.done();
                Toast.makeText(WelcomeBackPasswordPrompt.this,getString(R.string.fui_error_unknown),Toast.LENGTH_LONG).show();
            }
        };

        //This callback is basically trying to update the password
        firebaseCallback = new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    mHandler.done();

                    Login login = response.body();
                    String code = login.getCode();
                    String msg = login.getMsg();

                    if (code.equalsIgnoreCase("11")){
                        //Password updated retry login
                        Toast.makeText(WelcomeBackPasswordPrompt.this,msg,Toast.LENGTH_LONG).show();
                        validateAndSignIn();
                    }else if (code.equalsIgnoreCase("12")){
                        //try again later
                        Toast.makeText(WelcomeBackPasswordPrompt.this,msg,Toast.LENGTH_LONG).show();
                        finish(RESULT_OK,null);
                    }
                    else {
                        Toast.makeText(WelcomeBackPasswordPrompt.this,msg,Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                mHandler.done();
                Toast.makeText(WelcomeBackPasswordPrompt.this,getString(R.string.fui_error_unknown),Toast.LENGTH_LONG).show();
            }
        };

        TextView footerText = findViewById(R.id.email_footer_tos_and_pp_text);
        PrivacyDisclosureUtils.setupTermsOfServiceFooter(this, getFlowParams(), footerText);
    }

    @StringRes
    private int getErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return R.string.fui_error_invalid_password;
        }

        return R.string.fui_error_unknown;
    }

    private void onForgotPasswordClicked() {
        startActivity(RecoverPasswordActivity.createIntent(
                this,
                getFlowParams(),
                mIdpResponse.getEmail()));
    }

    @Override
    public void onDonePressed() {
        loginUserApi();
    }

    private void loginUserApi(){
        String email = mIdpResponse.getEmail();
        String password = mPasswordField.getText().toString();

        if (TextUtils.isEmpty(password)) {
            mPasswordLayout.setError(getString(R.string.fui_required_field));
            return;
        } else {
            mPasswordLayout.setError(null);
        }
        mHandler.loginWithCallback(email,password,callback);
    }

    private void validateAndSignIn() {
        validateAndSignIn(mPasswordField.getText().toString());
    }

    private void validateAndSignIn(String password) {
        // Check for null or empty password
        if (TextUtils.isEmpty(password)) {
            mPasswordLayout.setError(getString(R.string.fui_required_field));
            return;
        } else {
            mPasswordLayout.setError(null);
        }

        AuthCredential authCredential = ProviderUtils.getAuthCredential(mIdpResponse);
        mHandler.startSignIn(mIdpResponse.getEmail(), password, mIdpResponse, authCredential);
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.button_done) {
            loginUserApi();
        } else if (id == R.id.trouble_signing_in) {
            onForgotPasswordClicked();
        }
    }

    @Override
    public void showProgress(int message) {
        mDoneButton.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mDoneButton.setEnabled(true);
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}
