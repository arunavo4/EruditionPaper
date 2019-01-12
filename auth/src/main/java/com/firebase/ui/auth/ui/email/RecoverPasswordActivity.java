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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.firebase.ui.auth.data.model.Resource;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.auth.R;
import com.firebase.ui.auth.data.model.FlowParameters;
import com.firebase.ui.auth.ui.AppCompatBase;
import com.firebase.ui.auth.util.ExtraConstants;
import com.firebase.ui.auth.util.data.PrivacyDisclosureUtils;
import com.firebase.ui.auth.util.ui.ImeHelper;
import com.firebase.ui.auth.util.ui.fieldvalidators.EmailFieldValidator;
import com.firebase.ui.auth.viewmodel.ResourceObserver;
import com.firebase.ui.auth.viewmodel.email.RecoverPasswordHandler;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

/**
 * Activity to initiate the "forgot password" flow by asking for the user's email.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class RecoverPasswordActivity extends AppCompatBase implements View.OnClickListener,
        ImeHelper.DonePressedListener {
    private RecoverPasswordHandler mHandler;

    private TextInputLayout mEmailInputLayout;
    private EditText mEmailEditText;
    private EmailFieldValidator mEmailFieldValidator;
    private Callback<Login> callback;

    public static Intent createIntent(Context context, FlowParameters params, String email) {
        return createBaseIntent(context, RecoverPasswordActivity.class, params)
                .putExtra(ExtraConstants.EMAIL, email);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fui_forgot_password_layout);

        mHandler = ViewModelProviders.of(this).get(RecoverPasswordHandler.class);
        mHandler.init(getFlowParams());
        mHandler.getOperation().observe(this, new ResourceObserver<String>(
                this, R.string.fui_progress_dialog_sending) {
            @Override
            protected void onSuccess(@NonNull String email) {
//                mEmailInputLayout.setError(null);
//                showEmailSentDialog(email);
            }

            @Override
            protected void onFailure(@NonNull Exception e) {
//                if (e instanceof FirebaseAuthInvalidUserException
//                        || e instanceof FirebaseAuthInvalidCredentialsException) {
//                    // No FirebaseUser exists with this email address, show error.
//                    mEmailInputLayout.setError(getString(R.string.fui_error_email_does_not_exist));
//                } else {
//                    // Unknown error
//                    mEmailInputLayout.setError(getString(R.string.fui_error_unknown));
//                }
            }
        });

        mEmailInputLayout = findViewById(R.id.email_layout);
        mEmailEditText = findViewById(R.id.email);
        mEmailFieldValidator = new EmailFieldValidator(mEmailInputLayout);

        String email = getIntent().getStringExtra(ExtraConstants.EMAIL);
        if (email != null) {
            mEmailEditText.setText(email);
        }

        callback = new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    mHandler.done();
                    Login login = response.body();
                    String code = login.getCode();

                    if (code.contentEquals("0")){
                        mEmailInputLayout.setError(getString(R.string.fui_error_email_does_not_exist));
                    }else if (code.contentEquals("1")){
                        mEmailInputLayout.setError(null);
                        showEmailSentDialog();
                    }else {
                        mEmailInputLayout.setError(getString(R.string.fui_error_unknown));
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                mEmailInputLayout.setError(getString(R.string.fui_error_unknown));
            }
        };

        ImeHelper.setImeOnDoneListener(mEmailEditText, this);
        findViewById(R.id.button_done).setOnClickListener(this);

        TextView footerText = findViewById(R.id.email_footer_tos_and_pp_text);
        PrivacyDisclosureUtils.setupTermsOfServiceFooter(this, getFlowParams(), footerText);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_done
                && mEmailFieldValidator.validate(mEmailEditText.getText())) {
            onDonePressed();
        }
    }

    @Override
    public void onDonePressed() {
        mHandler.forgotPassword(mEmailEditText.getText().toString(),callback);
    }

    private void showEmailSentDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.fui_title_confirm_recover_password)
                .setMessage(getString(R.string.fui_confirm_recovery_body, mEmailEditText.getText().toString()))
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish(RESULT_OK, new Intent());
                    }
                })
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
