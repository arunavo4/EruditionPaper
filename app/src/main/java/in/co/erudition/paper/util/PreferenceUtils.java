package in.co.erudition.paper.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.Keep;
import in.co.erudition.paper.Erudition;
import in.co.erudition.paper.data.model.Login;
import in.co.erudition.paper.data.model.Person;
import in.co.erudition.paper.data.model.University;

@Keep
public class PreferenceUtils {
    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor mPrefsEdit;
    private static HashMap<String,String> mUniList;

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
        return mPrefs.getString("EId","0");
    }

    public static boolean getJS(){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);
        return mPrefs.getBoolean("JavaScript",false);
    }

    //Getter and setter for university
    public static HashMap<String,String> getUniversitiesList(){
        if (mUniList!=null)
            return mUniList;
        else
            return new LinkedHashMap<String, String>();
    }

    public static void setUniversitiesList(List<University> universities){
        mUniList = new LinkedHashMap<>();
        for (University uni: universities) {
            if (uni.getState()!=null) {
                if (uni.getState().contentEquals("Active")) {
                    mUniList.put(uni.getName(),uni.getCode());
                }
            }
        }
    }

    //===========Getters================

    public static String[] getAcademicDetailsName(String[] academic_details_name){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

        academic_details_name[0] = mPrefs.getString("Fav_BoardName", "University");
        academic_details_name[1] = mPrefs.getString("Fav_CollegeName", "College");
        academic_details_name[2] = mPrefs.getString("Fav_CourseName", "Department");
        academic_details_name[3] = mPrefs.getString("Fav_SessionName", "Semester");

        return academic_details_name;
    }

    public static String[] getAcademicDetails(String[] academic_details){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

        academic_details[0] = mPrefs.getString("Fav_BoardCode", "0");
        academic_details[1] = mPrefs.getString("Fav_CollegeCode", "0");
        academic_details[2] = mPrefs.getString("Fav_CourseCode", "0");
        academic_details[3] = mPrefs.getString("Fav_SessionCode", "0");

        Log.d("academic_params", Arrays.toString(academic_details));

        return academic_details;
    }

    //===========Setters===============

    public static void setBoard(String BoardCode, String BoardName){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);
        mPrefsEdit = mPrefs.edit();
        mPrefsEdit.putString("Fav_BoardCode",BoardCode);
        mPrefsEdit.putString("Fav_BoardName",BoardName);
        mPrefsEdit.apply();
    }

    public static void setCollege(String CollegeCode, String CollegeName){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);
        mPrefsEdit = mPrefs.edit();
        mPrefsEdit.putString("Fav_CollegeCode",CollegeCode);
        mPrefsEdit.putString("Fav_CollegeName",CollegeName);
        mPrefsEdit.apply();
    }

    public static void setCourse(String CourseCode, String CourseName){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);
        mPrefsEdit = mPrefs.edit();
        mPrefsEdit.putString("Fav_CourseCode",CourseCode);
        mPrefsEdit.putString("Fav_CourseName",CourseName);
        mPrefsEdit.apply();
    }

    public static void setSession(String SessionCode, String SessionName){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);
        mPrefsEdit = mPrefs.edit();
        mPrefsEdit.putString("Fav_SessionCode",SessionCode);
        mPrefsEdit.putString("Fav_SessionName",SessionName);
        mPrefsEdit.apply();
    }

}

