package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arunavo Ray on 09-06-2018.
 */

public class Year {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("Year")
    @Expose
    private String year;
    @SerializedName("SubjectFullName")
    @Expose
    private String subjectFullName;
    @SerializedName("UniversityName")
    @Expose
    private String universityName;
    @SerializedName("TimeAllotted")
    @Expose
    private String timeAllotted;
    @SerializedName("FullMarks")
    @Expose
    private String fullMarks;
    @SerializedName("SubjectName")
    @Expose
    private String subjectName;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("PaperImageM")
    @Expose
    private String paperImageM;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSubjectFullName() {
        return subjectFullName;
    }

    public void setSubjectFullName(String subjectFullName) {
        this.subjectFullName = subjectFullName;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
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

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
