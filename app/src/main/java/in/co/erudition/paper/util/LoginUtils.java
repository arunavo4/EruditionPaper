package in.co.erudition.paper.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;

import in.co.erudition.paper.Erudition;
import in.co.erudition.paper.data.model.Login;
import in.co.erudition.paper.data.model.Person;
import in.co.erudition.paper.data.remote.BackendService;
import in.co.erudition.paper.network.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginUtils {
    private BackendService mService;
    private NetworkUtils mNetworkUtils = new NetworkUtils();
    public static String eid = "0";
    private String message = "";

    public LoginUtils() {
        //Empty constructor
        mService = ApiUtils.getBackendService();
    }

    public String login_via_idp(String provider_name, final String userEmail, String jwt_token) {
        String provider = "";

        if (provider_name.contentEquals("google.com")) {
            provider = "Google";
        } else if (provider_name.contentEquals("facebook.com")) {
            provider = "Facebook";
        }
        Log.d("Login Params: ", provider + "," + userEmail + "," + jwt_token);
        Call<Login> loginCall = mService.signIn_idp(provider, userEmail, jwt_token);
        loginCall.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                Login login = response.body();
                eid = login.geteId();
                message = login.getMsg();
                Log.d("Login_via_idp:", "Eid: " + eid);
                Log.d("Login_via_idp:", "message: " + message);
                PreferenceUtils.writeLoginDetails(login,userEmail);

                person_details(eid, null);
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.d("Login_via_idp:", "Failed to login");
            }
        });

        return message;
    }

    //SignUp via Email
    public int signUp_via_Email(String first_name, String last_name, String userEmail) {
        int message = 0;

        try {
            Call<Login> loginCallEmail = mService.signUp_email(first_name, last_name, userEmail);
            Response<Login> loginResponseEmail = loginCallEmail.execute();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    //Confirm Email/ Password Update
    public int confirm_email_pass_update(String userEmail, String code, String password) {

        try {
            Call<Login> call = mService.password_update_email(userEmail, code, password);
            Response<Login> response = call.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    //Forgot Password
    public int forgot_password(String userEmail) {

        try {
            Call<Login> call = mService.forgot_password(userEmail);
            Response<Login> response = call.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int person_details(String Eid, String Email) {
        String id = "";
        Call<Person> personCall;

        if (Eid != null) {
            id = Eid;
            personCall = mService.getPersonDetailsEid(id);
        } else{
            id = Email;
            personCall = mService.getPersonDetailsEmail(id);
        }

        if (!id.contentEquals("")) {
            personCall.enqueue(new Callback<Person>() {
                @Override
                public void onResponse(Call<Person> call, Response<Person> response) {
                    //Write to Shared Preferences
                    Person person = response.body();

                    PreferenceUtils.writePersonDetails(person);
                }

                @Override
                public void onFailure(Call<Person> call, Throwable t) {
                    Log.d("Person Call:", "Failed to get Person Details");
                }
            });
        }

        return 0;
    }

}
