package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Arunavo Ray on 03-04-2018.
 */

public class Paper {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("PaperCode")
    @Expose
    private String paperCode;
    @SerializedName("PaperName")
    @Expose
    private String paperName;
    @SerializedName("UniversityCode")
    @Expose
    private String universityCode;
    @SerializedName("UniversityName")
    @Expose
    private String universityName;
    @SerializedName("CourseCode")
    @Expose
    private String courseCode;
    @SerializedName("CourseName")
    @Expose
    private String courseName;
    @SerializedName("StreamCode")
    @Expose
    private String streamCode;
    @SerializedName("StreamName")
    @Expose
    private String streamName;
    @SerializedName("SemesterNo")
    @Expose
    private String semesterNo;
    @SerializedName("SemesterName")
    @Expose
    private String semesterName;
    @SerializedName("SubjectCode")
    @Expose
    private String subjectCode;
    @SerializedName("SubjectName")
    @Expose
    private String subjectName;
    @SerializedName("Year")
    @Expose
    private String year;
    @SerializedName("TimeAllotted")
    @Expose
    private String timeAllotted;
    @SerializedName("FullMarks")
    @Expose
    private String fullMarks;
    @SerializedName("Author")
    @Expose
    private String author;
    @SerializedName("CreatedOn")
    @Expose
    private String createdOn;
    @SerializedName("Solved")
    @Expose
    private String solved;
    @SerializedName("Tags")
    @Expose
    private String tags;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("PaperImageM")
    @Expose
    private String paperImageM;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("View")
    @Expose
    private String view;
    @SerializedName("SemesterCode")
    @Expose
    private String semesterCode;
    @SerializedName("SubjectKey")
    @Expose
    private String subjectKey;
    @SerializedName("SemesterKey")
    @Expose
    private String semesterKey;
    @SerializedName("StreamKey")
    @Expose
    private String streamKey;
    @SerializedName("CourseKey")
    @Expose
    private String courseKey;
    @SerializedName("UniversityKey")
    @Expose
    private String universityKey;
    @SerializedName("SubjectFullName")
    @Expose
    private String subjectFullName;
    @SerializedName("Comment")
    @Expose
    private List<Object> comment = null;
    @SerializedName("PaperImage")
    @Expose
    private List<Object> paperImage = null;
    @SerializedName("PaperGroup")
    @Expose
    private List<PaperGroup> paperGroup = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaperCode() {
        return paperCode;
    }

    public void setPaperCode(String paperCode) {
        this.paperCode = paperCode;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public String getUniversityCode() {
        return universityCode;
    }

    public void setUniversityCode(String universityCode) {
        this.universityCode = universityCode;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

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

    public String getStreamCode() {
        return streamCode;
    }

    public void setStreamCode(String streamCode) {
        this.streamCode = streamCode;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public String getSemesterNo() {
        return semesterNo;
    }

    public void setSemesterNo(String semesterNo) {
        this.semesterNo = semesterNo;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTimeAllotted() {
        return timeAllotted;
    }

    public void setTimeAllotted(String timeAllotted) {
        this.timeAllotted = timeAllotted;
    }

    public String getFullMarks() {
        return fullMarks;
    }

    public void setFullMarks(String fullMarks) {
        this.fullMarks = fullMarks;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getSolved() {
        return solved;
    }

    public void setSolved(String solved) {
        this.solved = solved;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaperImageM() {
        return paperImageM;
    }

    public void setPaperImageM(String paperImageM) {
        this.paperImageM = paperImageM;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getSemesterCode() {
        return semesterCode;
    }

    public void setSemesterCode(String semesterCode) {
        this.semesterCode = semesterCode;
    }

    public String getSubjectKey() {
        return subjectKey;
    }

    public void setSubjectKey(String subjectKey) {
        this.subjectKey = subjectKey;
    }

    public String getSemesterKey() {
        return semesterKey;
    }

    public void setSemesterKey(String semesterKey) {
        this.semesterKey = semesterKey;
    }

    public String getStreamKey() {
        return streamKey;
    }

    public void setStreamKey(String streamKey) {
        this.streamKey = streamKey;
    }

    public String getCourseKey() {
        return courseKey;
    }

    public void setCourseKey(String courseKey) {
        this.courseKey = courseKey;
    }

    public String getUniversityKey() {
        return universityKey;
    }

    public void setUniversityKey(String universityKey) {
        this.universityKey = universityKey;
    }

    public String getSubjectFullName() {
        return subjectFullName;
    }

    public void setSubjectFullName(String subjectFullName) {
        this.subjectFullName = subjectFullName;
    }

    public List<Object> getComment() {
        return comment;
    }

    public void setComment(List<Object> comment) {
        this.comment = comment;
    }

    public List<Object> getPaperImage() {
        return paperImage;
    }

    public void setPaperImage(List<Object> paperImage) {
        this.paperImage = paperImage;
    }

    public List<PaperGroup> getPaperGroup() {
        return paperGroup;
    }

    public void setPaperGroup(List<PaperGroup> paperGroup) {
        this.paperGroup = paperGroup;
    }
}
