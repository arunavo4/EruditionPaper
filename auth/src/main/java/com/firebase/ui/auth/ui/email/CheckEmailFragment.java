package com.firebase.ui.auth.ui.email;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.material.textfield.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.auth.R;
import com.firebase.ui.auth.data.model.FlowParameters;
import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.auth.ui.FragmentBase;
import com.firebase.ui.auth.util.ExtraConstants;
import com.firebase.ui.auth.util.data.PrivacyDisclosureUtils;
import com.firebase.ui.auth.util.ui.ImeHelper;
import com.firebase.ui.auth.util.ui.fieldvalidators.EmailFieldValidator;
import com.firebase.ui.auth.viewmodel.ResourceObserver;
import com.google.firebase.auth.EmailAuthProvider;

/**
 * Fragment that shows a form with an email field and checks for existing accounts with that email.
 * <p>
 * Host Activities should implement {@link CheckEmailListener}.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class CheckEmailFragment extends FragmentBase implements
        View.OnClickListener,
        ImeHelper.DonePressedListener {

    /**
     * Interface to be implemented by Activities hosting this Fragment.
     */
    interface CheckEmailListener {

        /**
         * Email entered belongs to an existing email user.
         */
        void onExistingEmailUser(User user);

        /**
         * Email entered belongs to an existing IDP user.
         */
        void onExistingIdpUser(User user);

        /**
         * Email entered does not belong to an existing user.
         */
        void onNewUser(User user);

        /**
         * Email entered does not belong to an existing user.
         * But Exists on the Api
         */
        void onNewUserApi(User user);

    }

    public static final String TAG = "CheckEmailFragment";

    private CheckEmailHandler mHandler;

    private Button mNextButton;
    private ProgressBar mProgressBar;

    private EditText mEmailEditText;
    private TextInputLayout mEmailLayout;

    private EmailFieldValidator mEmailFieldValidator;
    private CheckEmailListener mListener;

    public static CheckEmailFragment newInstance(@Nullable String email) {
        CheckEmailFragment fragment = new CheckEmailFragment();
        Bundle args = new Bundle();
        args.putString(ExtraConstants.EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fui_check_email_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mNextButton = view.findViewById(R.id.button_next);
        mProgressBar = view.findViewById(R.id.top_progress_bar);

        // Email field and validator
        mEmailLayout = view.findViewById(R.id.email_layout);
        mEmailEditText = view.findViewById(R.id.email);
        mEmailFieldValidator = new EmailFieldValidator(mEmailLayout);
        mEmailLayout.setOnClickListener(this);
        mEmailEditText.setOnClickListener(this);

        ImeHelper.setImeOnDoneListener(mEmailEditText, this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && getFlowParams().enableHints) {
            mEmailEditText.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
        }

        mNextButton.setOnClickListener(this);

        TextView termsText = view.findViewById(R.id.email_tos_and_pp_text);
        TextView footerText = view.findViewById(R.id.email_footer_tos_and_pp_text);
        FlowParameters flowParameters = getFlowParams();

        if (flowParameters.isSingleProviderFlow()) {
            PrivacyDisclosureUtils.setupTermsOfServiceAndPrivacyPolicyText(getContext(),
                    flowParameters,
                    termsText);
        } else {
            termsText.setVisibility(View.GONE);
            PrivacyDisclosureUtils.setupTermsOfServiceFooter(getContext(),
                    flowParameters,
                    footerText);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler = ViewModelProviders.of(this).get(CheckEmailHandler.class);
        mHandler.init(getFlowParams());

        if (!(getActivity() instanceof CheckEmailListener)) {
            throw new IllegalStateException("Activity must implement CheckEmailListener");
        }
        mListener = (CheckEmailListener) getActivity();

        mHandler.getOperation().observe(this, new ResourceObserver<User>(
                this, R.string.fui_progress_dialog_checking_accounts) {
            @Override
            protected void onSuccess(@NonNull User user) {
                final String email = user.getEmail();
                final User reuser = user;
                String provider = user.getProviderId();

                Log.d("Inside Fragment","On Success");
                mEmailEditText.setText(email);
                //noinspection ConstantConditions new user
                if (provider == null) {
                    //Here we wanna check with our own api before calling on new user
                    Callback<Login> callback = new Callback<Login>() {
                        @Override
                        public void onResponse(Call<Login> call, Response<Login> response) {
                            Login login = response.body();
                            String code = login.getCode();

                            if (code.contentEquals("8")){
                                //User exists on Api
                                Log.d("provider null","USer exits on Api");
                                //Ask for password
                                mListener.onNewUserApi(new User.Builder(EmailAuthProvider.PROVIDER_ID, email)
                                        .setName(reuser.getName())
                                        .setPhotoUri(reuser.getPhotoUri())
                                        .build());
                            }else {
                                Log.d("provider null","Calling On new user");
                                mListener.onNewUser(new User.Builder(EmailAuthProvider.PROVIDER_ID, email)
                                        .setName(reuser.getName())
                                        .setPhotoUri(reuser.getPhotoUri())
                                        .build());
                            }
                        }

                        @Override
                        public void onFailure(Call<Login> call, Throwable t) {
                            Log.d("Inside Fragment","check email callback failed");
                        }
                    };

                    mHandler.checkUserEmail(email,callback);
                    Log.d("Inside Fragment","On Success New User");
                } else if (provider.equals(EmailAuthProvider.PROVIDER_ID)) {
                    mListener.onExistingEmailUser(user);
                    Log.d("Inside Fragment","On Success onExistingEmailUser");
                } else {
                    mListener.onExistingIdpUser(user);
                    Log.d("Inside Fragment","On Success onExistingIdpUser Forced Email");
                }
            }

            @Override
            protected void onFailure(@NonNull Exception e) {
                // Just let the user enter their data
            }
        });

        if (savedInstanceState != null) { return; }

        // Check for email
        /*
         *  Applying my own Api
         */
        String email = getArguments().getString(ExtraConstants.EMAIL);
        if (!TextUtils.isEmpty(email)) {
            mEmailEditText.setText(email);
            validateAndProceed();
        } else if (getFlowParams().enableHints) {
            mHandler.fetchCredential();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mHandler.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.button_next) {
            validateAndProceed();
        } else if (id == R.id.email_layout || id == R.id.email) {
            mEmailLayout.setError(null);
        }
    }

    @Override
    public void onDonePressed() {
        validateAndProceed();
    }

    private void validateAndProceed() {
        String email = mEmailEditText.getText().toString();
        if (mEmailFieldValidator.validate(email)) {
            mHandler.fetchProvider(email);
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
}
