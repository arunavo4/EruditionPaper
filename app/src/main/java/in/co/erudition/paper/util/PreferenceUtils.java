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
import in.co.erudition.paper.R;
import in.co.erudition.paper.data.model.Login;
import in.co.erudition.paper.data.model.Person;
import in.co.erudition.paper.data.model.University;

@Keep
public class PreferenceUtils {
    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor mPrefsEdit;
    private static HashMap<String,String> mUniList;
    private static final String BASE_URL = "https://www.erudition.co.in/api/v1/";

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

        String[] params = new String[4];
        params[0] = person.getBoardCode()==null?"0":person.getBoardCode();
        params[1] = person.getCollegeCode()==null?"0":person.getCollegeCode();
        params[2] = person.getCourseCode()==null?"0":person.getCourseCode();
        params[3] = person.getSessionCode()==null?"0":person.getSessionCode();

        //Check if fav is set
        boolean fav_status = true;
        for (String param:params) {
            if (param.contentEquals("0")){
                fav_status = false;
                break;
            }
        }
        mPrefsEdit.putBoolean("FavStatus",fav_status);

        //========Academic details========
        mPrefsEdit.putString("Fav_BoardCode",params[0]);
        mPrefsEdit.putString("Fav_CollegeCode",params[1]);
        mPrefsEdit.putString("Fav_CourseCode",params[2]);
        mPrefsEdit.putString("Fav_SessionCode",params[3]);
        mPrefsEdit.putString("Fav_BoardName",person.getBoardName());
        mPrefsEdit.putString("Fav_CollegeName",person.getCollegeFullName());
        mPrefsEdit.putString("Fav_CourseName",person.getCourseName());
        mPrefsEdit.putString("Fav_SessionName",person.getSessionFullName());

        //========PErsonal Details========
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

    public static String[] getParams(String Code){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

        String[] params = new String[4];

        params[0] = mPrefs.getString("Fav_BoardCode", "0");
        params[1] = mPrefs.getString("Fav_CourseCode", "0");
        params[2] = mPrefs.getString("Fav_SessionCode", "0");
        params[3] = Code;

        return params;
    }

    public static String getSemesterTitle(){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

        String Course = mPrefs.getString("Fav_CourseName", "Dept");
        String Session = mPrefs.getString("Fav_SessionName", "Semester");

        return (Course + ", " + Session);
    }

    public static boolean getFavStatus(){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

        boolean status = mPrefs.getBoolean("FavStatus",false);
        if (status){
            //check if all params are non zero
            String[] params = new String[4];
            params = getAcademicDetails(params);
            for (String param:params) {
                if (param.contentEquals("0")){
                    status = false;
                    break;
                }
            }
        }

        return status;
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

    public static void setFavStatus(boolean status){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);
        mPrefsEdit = mPrefs.edit();
        mPrefsEdit.putBoolean("FavStatus",status);
        mPrefsEdit.apply();
    }


    //========Ad Time===========

    public static void setAdTime(long AdTime){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);
        mPrefsEdit = mPrefs.edit();
        mPrefsEdit.putLong("AdTime",AdTime);
        mPrefsEdit.apply();
    }

    public static Long getAdTime(){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);
//        return Long.valueOf(10000);
        return mPrefs.getLong("AdTime",600000);
    }

    //============ BASE URL =======================
    public static void setBaseUrl(String baseUrl){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);
        mPrefsEdit = mPrefs.edit();
        mPrefsEdit.putString("BASE_URL",baseUrl);
        mPrefsEdit.apply();
    }

    public static String getBaseUrl(){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

        return mPrefs.getString("BASE_URL",BASE_URL);
    }

    //============ Role =====================
    public static String getRole(){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

        return mPrefs.getString("Role","Guest");
    }

    //=========== Announcements ==============
    public static void setAnnouncement(String title, String image_url, String link_url, boolean status){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);
        mPrefsEdit = mPrefs.edit();
        mPrefsEdit.putString("Title",title);
        mPrefsEdit.putString("ImageURL",image_url);
        mPrefsEdit.putString("LinkURL",link_url);
        mPrefsEdit.putBoolean("AnnouncementStatus",status);
        mPrefsEdit.apply();
    }

    public static String getAnnouncementImageUrl(){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

        return mPrefs.getString("ImageURL","https://s3.ap-south-1.amazonaws.com/in.co.erudition/cdn/image/Circle-e-LOGO-small.png");
    }

    public static String getAnnouncementLinkUrl(){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

        return mPrefs.getString("LinkURL","https://paper.erudition.co.in/");
    }

    public static String getAnnouncementTitle(){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

        return mPrefs.getString("Title","Announcements");
    }

    public static boolean getAnnouncementStatus() {
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

        return mPrefs.getBoolean("AnnouncementStatus", false);
    }
    //======== Css and Js Head text ===============
    public static String getDefaultCssHead(){
        return "<head>" + "\n <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, height=device-height\">" +
                "<link rel=\"stylesheet\" href=\"font.css\"><style>body{font-size:14px;font-family:'Source Sans Pro',sans-serif}p{margin-top:0;margin-" +
                "bottom:.4rem}img{height:auto!important;overflow-x:auto!important;overflow-y:hidden!important;border:none!important;max-width:100%;vertical-" +
                "align:middle}table{width:100%!important;height:auto!important;background-color:transparent;border-spacing:0;border-collapse:collapse}</style>\n";
    }

    public static String getDefaultJsHead(){
        return "<link rel=\"stylesheet\" href=\"prism.css\"><script src=\"prism.js\"></script>";
    }

    public static String getCssHead(){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

        return mPrefs.getString("CSS_HEAD",getDefaultCssHead());
    }

    public static String getJsHead(){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);

        return mPrefs.getString("JS_HEAD",getDefaultJsHead());
    }

    public static void setCssHead(String cssHead){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);
        mPrefsEdit = mPrefs.edit();
        mPrefsEdit.putString("CSS_HEAD",cssHead);
        mPrefsEdit.apply();
    }

    public static void setJsHead(String jsHead){
        mPrefs = Erudition.getContextOfApplication().getSharedPreferences("Erudition",
                Context.MODE_PRIVATE);
        mPrefsEdit = mPrefs.edit();
        mPrefsEdit.putString("JS_HEAD",jsHead);
        mPrefsEdit.apply();

    }
}

