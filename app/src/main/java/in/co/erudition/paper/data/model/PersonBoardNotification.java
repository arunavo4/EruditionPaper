package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PersonBoardNotification {

    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("BoardCode")
    @Expose
    private String boardCode;
    @SerializedName("CourseCode")
    @Expose
    private String courseCode;
    @SerializedName("SessionCode")
    @Expose
    private String sessionCode;
    @SerializedName("SubjectCode")
    @Expose
    private String subjectCode;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }
}
