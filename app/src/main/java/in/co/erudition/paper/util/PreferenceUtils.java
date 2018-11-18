package in.co.erudition.paper.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Keep;
import in.co.erudition.paper.Erudition;
import in.co.erudition.paper.data.model.Login;
import in.co.erudition.paper.data.model.Person;

@Keep
public class PreferenceUtils {
    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor mPrefsEdit;

    public PreferenceUtils(){
        //Empty constructor
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);
    }

    public static void writePersonDetails(Person person){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);
        mPrefsEdit = mPrefs.edit();
        mPrefsEdit.putString("Avatar", person.getAvatar());
        mPrefsEdit.putString("ProfileImage", person.getProfileImage());
        mPrefsEdit.putString("FirstName", person.getFirstName());
        mPrefsEdit.putString("LastName", person.getLastName());
        mPrefsEdit.putString("EId", person.getEId());
        mPrefsEdit.putString("Phone", person.getPhone());
        mPrefsEdit.putString("Gender", person.getGender());
        mPrefsEdit.putString("DOB", person.getdOB());

        mPrefsEdit.putString("Email", person.getEmail());
        mPrefsEdit.putString("Role", person.getRole());
        mPrefsEdit.putString("Status", person.getStatus());
        mPrefsEdit.apply();

    }

    public static String[] readPersonalDetails(String[] person_details){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

        person_details[0] = mPrefs.getString("FirstName", "First Name");
        person_details[1] = mPrefs.getString("LastName", "Last Name");
        person_details[2] = mPrefs.getString("Phone", "Phone");
        person_details[3] = mPrefs.getString("Gender", "Unspecified");
        person_details[4] = mPrefs.getString("DOB", "Date of Birth");

        return person_details;
    }

    public static String readName(){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

       return mPrefs.getString("FirstName", "Android") + " " + mPrefs.getString("LastName", "Studio");
    }


    public static String readEmail(){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

        return mPrefs.getString("Email","Email");
    }

    static void writeLoginDetails(Login login, String userEmail){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);
        mPrefsEdit = mPrefs.edit();
        mPrefsEdit.putString("EId", login.geteId());
        mPrefsEdit.putString("Email", userEmail);
        mPrefsEdit.putString("Msg", login.getMsg());
        mPrefsEdit.apply();
    }

    public static String getEid(){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);
        return mPrefs.getString("EId",null);
    }

    public static boolean getJS(){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);
        return mPrefs.getBoolean("JavaScript",false);
    }

}

