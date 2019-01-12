package in.co.erudition.paper.data.remote;

import java.util.List;

import in.co.erudition.paper.data.model.BoardCollege;
import in.co.erudition.paper.data.model.Chapter;
import in.co.erudition.paper.data.model.JwtToken;
import in.co.erudition.paper.data.model.Login;
import in.co.erudition.paper.data.model.Paper;
import in.co.erudition.paper.data.model.Person;
import in.co.erudition.paper.data.model.PresetResponseCode;
import in.co.erudition.paper.data.model.SearchResult;
import in.co.erudition.paper.data.model.University;
import in.co.erudition.paper.data.model.UniversityCourse;
import in.co.erudition.paper.data.model.Year;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Arunavo Ray on 05-06-2018.
 */

public interface BackendService {

    /*
    Login and User data related Calls
    */

    //jwt Token
    @POST("oauth4")
    @FormUrlEncoded
    Call<JwtToken> getToken(@Field("EId") String eid,
                            @Field("Password") String password);

    //Email, Google, Facebook Login
    @POST("login")
    @FormUrlEncoded
    Call<Login> signIn_idp(@Field("Social") String social,
                           @Field("Email") String email,
                           @Field("Password") String password);

    @POST("login")
    @FormUrlEncoded
    Call<com.firebase.ui.auth.ui.email.Login> check_email(@Field("Social") String social,
                                                          @Field("Email") String email);

    @POST("login")
    @FormUrlEncoded
    Call<com.firebase.ui.auth.ui.email.Login> check_email(@Field("Social") String social,
                                                          @Field("Email") String email,
                                                          @Field("Password") String password);

    //SignUp via Email
    @POST("register")
    @FormUrlEncoded
    Call<Login> signUp_email(@Field("FirstName") String first_name,
                             @Field("LastName") String last_name,
                             @Field("Email") String email);

    //Confirm Email/ Password Update
    @POST("password")
    @FormUrlEncoded
    Call<Login> password_update_email(@Field("Email") String email,
                                      @Field("SecretCode") String code,
                                      @Field("Password") String password);

    //Forgot Password
    @POST("forgotpassword")
    @FormUrlEncoded
    Call<com.firebase.ui.auth.ui.email.Login> forgot_password(@Field("Email") String email);


    //Get User Detail By EId or Email
    @POST("person")
    @FormUrlEncoded
    Call<Person> getPersonDetailsEid(@Field("EId") String eid);

    @POST("person")
    @FormUrlEncoded
    Call<Person> getPersonDetailsEmail(@Field("Email") String email);


    //Update User Detail By EId
    @POST("person/{EId}")
    @FormUrlEncoded
    Call<PresetResponseCode> updatePerson(@Path("EId") String eid,
                                          @Field("FirstName") String first_name,
                                          @Field("LastName") String last_name,
                                          @Field("Phone") String phone,
                                          @Field("Gender") String gender,
                                          @Field("DOB")String dob);

    @POST("person/{EId}")
    @FormUrlEncoded
    Call<PresetResponseCode> updatePerson(@Path("EId") String eid,
                                          @Field("FirstName") String first_name,
                                          @Field("LastName") String last_name);

    @POST("person/{EId}")
    @FormUrlEncoded
    Call<PresetResponseCode> updatePerson(@Path("EId") String eid,
                                          @Field("Phone") String phone,
                                          @Field("Gender") String gender,
                                          @Field("DOB") String dob);

    //Upload the Avatar
    @POST("person/{EId}/profileimage")
    @Multipart
    Call<PresetResponseCode> uploadAvatar(@Path("EId") String eid,
                                          @Part MultipartBody.Part image);



    /*
    Backend API Calls
     */

    @POST("board")
    Call<List<University>> getUniversity();

    @POST("board/course")
    @FormUrlEncoded
    Call<UniversityCourse> getCourses(@Field("BoardCode") String boardCode);

    @POST("board/course/session")
    @FormUrlEncoded
    Call<UniversityCourse> getCourses(@Field("BoardCode") String boardCode,
                                      @Field("CourseCode") String courseCode);

    @POST("board/course/session/subject")
    @FormUrlEncoded
    Call<UniversityCourse> getCourses(@Field("BoardCode") String boardCode,
                                      @Field("CourseCode") String courseCode,
                                      @Field("SessionCode") String sessionCode);

    @POST("board/course/session/subject/year")
    @FormUrlEncoded
    Call<List<Year>> getYear(@Field("BoardCode") String boardCode,
                             @Field("CourseCode") String courseCode,
                             @Field("SessionCode") String sessionCode,
                             @Field("SubjectCode") String subjectCode);

    @POST("board/course/session/subject/chapter")
    @FormUrlEncoded
    Call<List<Chapter>> getChapter(@Field("BoardCode") String boardCode,
                                   @Field("CourseCode") String courseCode,
                                   @Field("SessionCode") String sessionCode,
                                   @Field("SubjectCode") String subjectCode);


    @POST("paper")
    @FormUrlEncoded
    Call<Paper> getPaper(@Field("PaperCode") String paperCode);


    /*
        Notify me Call
     */
    @POST("person/{EId}/notifyme")
    @FormUrlEncoded
    Call<PresetResponseCode> notifyMe(@Path("EId") String eid,
                                      @Field("BoardCode") String boardCode);

    @POST("person/{EId}/notifyme")
    @FormUrlEncoded
    Call<PresetResponseCode> notifyMe(@Path("EId") String eid,
                                      @Field("BoardCode") String boardCode,
                                      @Field("CourseCode") String courseCode);

    @POST("person/{EId}/notifyme")
    @FormUrlEncoded
    Call<PresetResponseCode> notifyMe(@Path("EId") String eid,
                                      @Field("BoardCode") String boardCode,
                                      @Field("CourseCode") String courseCode,
                                      @Field("SessionCode") String sessionCode);

    @POST("person/{EId}/notifyme")
    @FormUrlEncoded
    Call<PresetResponseCode> notifyMe(@Path("EId") String eid,
                                      @Field("BoardCode") String boardCode,
                                      @Field("CourseCode") String courseCode,
                                      @Field("SessionCode") String sessionCode,
                                      @Field("SubjectCode") String subjectCode);


    /*
        Set Favourite or Semester Carousel
     */
    @POST("person/{EId}/favorite")
    @FormUrlEncoded
    Call<PresetResponseCode> setFavourite(@Path("EId") String eid,
                                          @Field("BoardCode") String boardCode);

    @POST("person/{EId}/favorite")
    @FormUrlEncoded
    Call<PresetResponseCode> setFavourite(@Path("EId") String eid,
                                          @Field("BoardCode") String boardCode,
                                          @Field("CourseCode") String courseCode);

    @POST("person/{EId}/favorite")
    @FormUrlEncoded
    Call<PresetResponseCode> setFavourite(@Path("EId") String eid,
                                          @Field("BoardCode") String boardCode,
                                          @Field("CourseCode") String courseCode,
                                          @Field("SessionCode") String sessionCode);

    @POST("person/{EId}/favorite")
    @FormUrlEncoded
    Call<PresetResponseCode> setFavourite(@Path("EId") String eid,
                                          @Field("BoardCode") String boardCode,
                                          @Field("CourseCode") String courseCode,
                                          @Field("SessionCode") String sessionCode,
                                          @Field("CollegeCode") String CollegeCode);

    //College Call
    @POST("institute")
    @FormUrlEncoded
    Call<List<BoardCollege>> getCollege(@Field("BoardCode") String boardCode);


    @POST("question/search")
    @FormUrlEncoded
    Call<List<SearchResult>> search(@Field("Search") String query);

    @POST("feedback")
    @FormUrlEncoded
    Call<PresetResponseCode> submitFeedback(@Field("Subject") String subject,
                                            @Field("MsgBody") String msgBody);



//    @GET("/database/university/{id}")
//    Call<List<UniversityFull>> getCourses(@Path("id") String id);

//    @GET("/dragoprint/ad")
//    Call<List<AdResponse>> getAd(@Query("Category") String category);
//
//    @GET("/{userId}")
//    Call<Product> getUserDetails(@Path("userId") String userId, @Query("albumId") String albumId);    // /USBFUBAF1231?albumId=2

}
