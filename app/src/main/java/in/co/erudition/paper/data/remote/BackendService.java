package in.co.erudition.paper.data.remote;

import java.util.List;

import in.co.erudition.paper.data.model.Chapter;
import in.co.erudition.paper.data.model.JwtToken;
import in.co.erudition.paper.data.model.Paper;
import in.co.erudition.paper.data.model.University;
import in.co.erudition.paper.data.model.UniversityCourse;
import in.co.erudition.paper.data.model.Year;
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

    //jwt Token
    @POST("oauth4")
    @FormUrlEncoded
    Call<JwtToken> getToken(@Field("EId") String eid,
                              @Field("Password") String password);


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



//    @GET("/database/university/{id}")
//    Call<List<UniversityFull>> getCourses(@Path("id") String id);

//    @GET("/dragoprint/ad")
//    Call<List<AdResponse>> getAd(@Query("Category") String category);
//
//    @GET("/{userId}")
//    Call<Product> getUserDetails(@Path("userId") String userId, @Query("albumId") String albumId);    // /USBFUBAF1231?albumId=2

}
