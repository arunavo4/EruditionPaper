package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Arunavo Ray on 01-04-2018.
 */

public class UniversityCourse {

    @SerializedName("CourseCode")
    @Expose
    private String courseCode;
    @SerializedName("CourseName")
    @Expose
    private String courseName;
    @SerializedName("CourseFullName")
    @Expose
    private String courseFullName;
    @SerializedName("CourseImageS")
    @Expose
    private String courseImageS;
    @SerializedName("CourseImageM")
    @Expose
    private String courseImageM;
    @SerializedName("CourseImageL")
    @Expose
    private String courseImageL;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Key")
    @Expose
    private String key;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("UniversityStream")
    @Expose
    private List<UniversityStream> universityStream = null;

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseFullName() {
        return courseFullName;
    }

    public void setCourseFullName(String courseFullName) {
        this.courseFullName = courseFullName;
    }

    public String getCourseImageS() {
        return courseImageS;
    }

    public void setCourseImageS(String courseImageS) {
        this.courseImageS = courseImageS;
    }

    public String getCourseImageM() {
        return courseImageM;
    }

    public void setCourseImageM(String courseImageM) {
        this.courseImageM = courseImageM;
    }

    public String getCourseImageL() {
        return courseImageL;
    }

    public void setCourseImageL(String courseImageL) {
        this.courseImageL = courseImageL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<UniversityStream> getUniversityStream() {
        return universityStream;
    }

    public void setUniversityStream(List<UniversityStream> universityStream) {
        this.universityStream = universityStream;
    }

}
