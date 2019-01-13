package com.firebase.ui.auth.ui.email;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.R;
import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.auth.ui.FragmentBase;
import com.firebase.ui.auth.util.ExtraConstants;
import com.firebase.ui.auth.util.data.PrivacyDisclosureUtils;
import com.firebase.ui.auth.util.data.ProviderUtils;
import com.firebase.ui.auth.util.ui.ImeHelper;
import com.firebase.ui.auth.util.ui.fieldvalidators.BaseValidator;
import com.firebase.ui.auth.util.ui.fieldvalidators.EmailFieldValidator;
import com.firebase.ui.auth.util.ui.fieldvalidators.NoOpValidator;
import com.firebase.ui.auth.util.ui.fieldvalidators.PasswordFieldValidator;
import com.firebase.ui.auth.util.ui.fieldvalidators.RequiredFieldValidator;
import com.firebase.ui.auth.viewmodel.ResourceObserver;
import com.firebase.ui.auth.viewmodel.idp.EmailProviderResponseHandler;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment to display an email/name/password sign up form for new users but old Api.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class RegisterApiEmailFragment extends FragmentBase implements
        View.OnClickListener, View.OnFocusChangeListener, ImeHelper.DonePressedListener{

    public static final String TAG = "RegisterApiEmailFragment";

    private EmailProviderResponseHandler mHandler;
    private FrameLayout layout;
    private Snackbar loading;
    private Callback<Login> callback;
    private Button mNextButton;
    private ProgressBar mProgressBar;

    private EditText mNameEditText;
    private EditText mPasswordEditText;
    private TextInputLayout mPasswordInput;

    private PasswordFieldValidator mPasswordFieldValidator;
    private BaseValidator mNameValidator;

    private User mUser;

    public static RegisterApiEmailFragment newInstance(User user) {
        RegisterApiEmailFragment fragment = new RegisterApiEmailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ExtraConstants.USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mUser = User.getUser(getArguments());
        } else {
            mUser = User.getUser(savedInstanceState);
        }

        //
        mHandler = ViewModelProviders.of(this).get(EmailProviderResponseHandler.class);
        mHandler.init(getFlowParams());
        mHandler.getOperation().observe(this, new ResourceObserver<IdpResponse>(
                this, R.string.fui_progress_dialog_signing_up) {
            @Override
            protected void onSuccess(@NonNull IdpResponse response) {
                Log.d("Hnadler","OnSuccess");
                startSaveCredentials(
                        mHandler.getCurrentUser(),
                        response,
                        mPasswordEditText.getText().toString());
            }

            @Override
            protected void onFailure(@NonNull Exception e) {
                Log.d("Hnadler","OnFailure");
                if (e instanceof FirebaseAuthWeakPasswordException) {
                    mPasswordInput.setError(getResources().getQuantityString(
                            R.plurals.fui_error_weak_password,
                            R.integer.fui_min_password_length));
                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Snackbar.make(layout, getString(R.string.fui_invalid_email_address),Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    // General error message, this branch should not be invoked but
                    // covers future API changes
                    Snackbar.make(layout, getString(R.string.fui_email_account_creation_error),Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });

        layout = (FrameLayout)  getActivity().findViewById(R.id.fragment_register_email);
        loading = Snackbar.make(layout, getString(R.string.fui_progress_dialog_checking),Snackbar.LENGTH_LONG);

        //here call the register email api endpoint
        callback = new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    //Show the info where to get the Secret Code
                    loading.dismiss();
                    Login login = response.body();
                    String code = login.getCode();
                    String msg = login.getMsg();
                    if (code.contentEquals("1")){
//                        //Successful login call firebase
                        mHandler.loginUser(getEmail(),mPasswordEditText.getText().toString());
                        validateAndRegisterUser();
                    }else if (code.contentEquals("7")){
                        //Wrong password
                        mPasswordInput.setError(msg);
                    }else {
                        Snackbar.make(layout, msg,Snackbar.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
//                    hideProgress();
                loading.dismiss();
                Snackbar.make(layout, getString(R.string.fui_error_unknown),Snackbar.LENGTH_LONG)
                        .show();
            }
        };

    }

    private String getEmail(){
        return mUser.getEmail();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fui_register_email_api_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mNextButton = view.findViewById(R.id.button_create);
        mProgressBar = view.findViewById(R.id.top_progress_bar);

        mNameEditText = view.findViewById(R.id.name);
        mPasswordEditText = view.findViewById(R.id.password);
        mPasswordInput = view.findViewById(R.id.password_layout);
        TextInputLayout nameInput = view.findViewById(R.id.name_layout);


        // Create welcome back text with email bolded.
        String bodyText =
                getString(R.string.fui_welcome_back_password_api_prompt_body, getEmail());

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(bodyText);
        int emailStart = bodyText.indexOf(getEmail());
        spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD),
                emailStart,
                emailStart + getEmail().length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        TextView bodyTextView = view.findViewById(R.id.welcome_back_password_body);
        bodyTextView.setText(spannableStringBuilder);

        // Get configuration
        AuthUI.IdpConfig emailConfig = ProviderUtils.getConfigFromIdpsOrThrow(
                getFlowParams().providerInfo, EmailAuthProvider.PROVIDER_ID);
        boolean requireName = emailConfig.getParams()
                .getBoolean(ExtraConstants.REQUIRE_NAME, true);
        mPasswordFieldValidator = new PasswordFieldValidator(
                mPasswordInput,
                getResources().getInteger(R.integer.fui_min_password_length));
        mNameValidator = requireName
                ? new RequiredFieldValidator(nameInput)
                : new NoOpValidator(nameInput);

        ImeHelper.setImeOnDoneListener(mPasswordEditText, this);

        mNameEditText.setOnFocusChangeListener(this);
        mPasswordEditText.setOnFocusChangeListener(this);
        mNextButton.setOnClickListener(this);

        // Only show the name field if required
        nameInput.setVisibility(requireName ? View.VISIBLE : View.GONE);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && getFlowParams().enableCredentials) {
//            mEmailEditText.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
//        }

        TextView footerText = view.findViewById(R.id.email_footer_tos_and_pp_text);
        PrivacyDisclosureUtils.setupTermsOfServiceFooter(getContext(), getFlowParams(), footerText);

        // WARNING: Nothing below this line will be executed on rotation
        if (savedInstanceState != null) {
            return;
        }

        // If email is passed in, fill in the field and move down to the name field.
//        String email = mUser.getEmail();
//        if (!TextUtils.isEmpty(email)) {
//            mEmailEditText.setText(email);
//        }

        // If name is passed in, fill in the field and move down to the password field.
        String name = mUser.getName();
        if (!TextUtils.isEmpty(name)) {
            mNameEditText.setText(name);
        }

        // See http://stackoverflow.com/questions/11082341/android-requestfocus-ineffective#comment51774752_11082523
        if (!requireName || !TextUtils.isEmpty(mNameEditText.getText())) {
            safeRequestFocus(mPasswordEditText);
        } else {//if (!TextUtils.isEmpty(mEmailEditText.getText())) {
            safeRequestFocus(mNameEditText);
        }
//        } else {
//            safeRequestFocus(mEmailEditText);
//        }
    }

    private void safeRequestFocus(final View v) {
        v.post(new Runnable() {
            @Override
            public void run() {
                v.requestFocus();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.fui_title_register_email);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(ExtraConstants.USER,
                new User.Builder(EmailAuthProvider.PROVIDER_ID, getEmail())
                        .setName(mNameEditText.getText().toString())
                        .setPhotoUri(mUser.getPhotoUri())
                        .build());
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) return; // Only consider fields losing focus

        int id = view.getId();
//        if (id == R.id.email) {
//            mEmailFieldValidator.validate(mEmailEditText.getText());
//        } else
            if (id == R.id.name) {
            mNameValidator.validate(mNameEditText.getText());
        } else if (id == R.id.password) {
            mPasswordFieldValidator.validate(mPasswordEditText.getText());
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_create) {
            //Here call Erudition api to register user
            Log.d("OnCliCk","Register user Firebase");
            loginUserApi();
//            validateAndRegisterUser();
        }
    }

    @Override
    public void onDonePressed() {
        loginUserApi();
        Log.d("onDonePressed","Register user Firebase");
//        validateAndRegisterUser();
    }

    private void loginUserApi(){
        String email = getEmail();
        String password = mPasswordEditText.getText().toString();
        String name = mNameEditText.getText().toString();

//        boolean emailValid = mEmailFieldValidator.validate(email);
        boolean passwordValid = mPasswordFieldValidator.validate(password);
        boolean nameValid = mNameValidator.validate(name);
        if (passwordValid && nameValid) {
            loading.show();
            mHandler.loginWithCallback(email,password,callback);
        }

    }

    @Override
    public void showProgress(int message) {
        mNextButton.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mNextButton.setEnabled(true);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void validateAndRegisterUser() {
//        String email = mEmailEditText.getText().toString();
        String email = getEmail();
        String password = mPasswordEditText.getText().toString();
        String name = mNameEditText.getText().toString();

//        boolean emailValid = mEmailFieldValidator.validate(email);
        boolean passwordValid = mPasswordFieldValidator.validate(password);
        boolean nameValid = mNameValidator.validate(name);
        if (passwordValid && nameValid) {
            mHandler.startSignIn(new IdpResponse.Builder(
                            new User.Builder(EmailAuthProvider.PROVIDER_ID, email)
                                    .setName(name)
                                    .setPhotoUri(mUser.getPhotoUri())
                                    .build())
                            .build(),
                    password);
        }
    }
}
