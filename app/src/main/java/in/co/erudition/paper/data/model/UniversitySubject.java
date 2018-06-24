package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arunavo Ray on 01-04-2018.
 */

public class UniversitySubject {

    @SerializedName("SubjectCode")
    @Expose
    private String subjectCode;
    @SerializedName("SubjectName")
    @Expose
    private String subjectName;
    @SerializedName("SubjectFullName")
    @Expose
    private String subjectFullName;
    @SerializedName("SubjectImageS")
    @Expose
    private String subjectImageS;
    @SerializedName("SubjectImageM")
    @Expose
    private String subjectImageM;
    @SerializedName("SubjectImageL")
    @Expose
    private String subjectImageL;
    @SerializedName("SubjectYear")
    @Expose
    private String subjectYear;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Key")
    @Expose
    private String key;
    @SerializedName("_id")
    @Expose
    private String id;

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

    public String getSubjectFullName() {
        return subjectFullName;
    }

    public void setSubjectFullName(String subjectFullName) {
        this.subjectFullName = subjectFullName;
    }

    public String getSubjectImageS() {
        return subjectImageS;
    }

    public void setSubjectImageS(String subjectImageS) {
        this.subjectImageS = subjectImageS;
    }

    public String getSubjectImageM() {
        return subjectImageM;
    }

    public void setSubjectImageM(String subjectImageM) {
        this.subjectImageM = subjectImageM;
    }

    public String getSubjectImageL() {
        return subjectImageL;
    }

    public void setSubjectImageL(String subjectImageL) {
        this.subjectImageL = subjectImageL;
    }

    public String getSubjectYear() {
        return subjectYear;
    }

    public void setSubjectYear(String subjectYear) {
        this.subjectYear = subjectYear;
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

}
