package in.co.erudition.paper.data.remote;

import java.util.List;

import in.co.erudition.paper.data.model.Paper;
import in.co.erudition.paper.data.model.University;
import in.co.erudition.paper.data.model.UniversityFull;
import in.co.erudition.paper.data.model.Yang;
import in.co.erudition.paper.data.model.Year;
import in.co.erudition.paper.data.model.Yin;
import in.co.erudition.paper.data.model.YinYang;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Arunavo Ray on 05-06-2018.
 */

public interface BackendService {

    //yin-yang
    @POST("/yin-yang")
    @FormUrlEncoded
    Call<YinYang> getYinYang(@Field("appid") String appid,
                             @Field("appsecret") String appsecret);

    //yin
    @POST("/yin")
    @FormUrlEncoded
    Call<Yin> getYin(@Field("email") String email,
                     @Field("password") String password,
                     @Field("energy") String energy);

    //yang
    @POST("/yang")
    @FormUrlEncoded
    Call<Yang> getYang(@Field("yin") String yin,
                       @Field("energy") String energy);


    /*
    Backend API Calls
     */

    @GET("/database/university?select=UniversityCode UniversityName UniversityFullName UniversityImageM Status Key") //&Status=Active
    Call<List<University>> getUniversity();

    @GET("/database/university")
    Call<List<UniversityFull>> getCourses(@Query("Key") String key);

    @GET("/database/paper")
    Call<List<Year>> getPapers(@Query("UniversityKey") String UniversityKey,
                               @Query("CourseKey") String CourseKey,
                               @Query("StreamKey") String StreamKey,
                               @Query("SemesterKey") String SemesterKey,
                               @Query("SubjectKey") String SubjectKey,
                               @Query("sort") String ByWhat,
                               @Query("select") String selection);

    @GET("/database/paper")
    Call<List<Paper>> getQuestions(@Query("UniversityKey") String UniversityKey,
                                    @Query("CourseKey") String CourseKey,
                                    @Query("StreamKey") String StreamKey,
                                    @Query("SemesterKey") String SemesterKey,
                                    @Query("SubjectKey") String SubjectKey,
                                    @Query("Year") String Year);

//    @GET("/database/university/{id}")
//    Call<List<UniversityFull>> getCourses(@Path("id") String id);

//    @GET("/dragoprint/ad")
//    Call<List<AdResponse>> getAd(@Query("Category") String category);
//
//    @GET("/{userId}")
//    Call<Product> getUserDetails(@Path("userId") String userId, @Query("albumId") String albumId);    // /USBFUBAF1231?albumId=2

}
