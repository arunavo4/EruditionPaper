package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Arunavo Ray on 01-04-2018.
 */

public class UniversitySemester {

    @SerializedName("SemesterCode")
    @Expose
    private String semesterCode;
    @SerializedName("SemesterName")
    @Expose
    private String semesterName;
    @SerializedName("SemesterFullName")
    @Expose
    private String semesterFullName;
    @SerializedName("SemesterImageS")
    @Expose
    private String semesterImageS;
    @SerializedName("SemesterImageM")
    @Expose
    private String semesterImageM;
    @SerializedName("SemesterImageL")
    @Expose
    private String semesterImageL;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Key")
    @Expose
    private String key;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("UniversitySubject")
    @Expose
    private List<UniversitySubject> universitySubject = null;

    public String getSemesterCode() {
        return semesterCode;
    }

    public void setSemesterCode(String semesterCode) {
        this.semesterCode = semesterCode;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getSemesterFullName() {
        return semesterFullName;
    }

    public void setSemesterFullName(String semesterFullName) {
        this.semesterFullName = semesterFullName;
    }

    public String getSemesterImageS() {
        return semesterImageS;
    }

    public void setSemesterImageS(String semesterImageS) {
        this.semesterImageS = semesterImageS;
    }

    public String getSemesterImageM() {
        return semesterImageM;
    }

    public void setSemesterImageM(String semesterImageM) {
        this.semesterImageM = semesterImageM;
    }

    public String getSemesterImageL() {
        return semesterImageL;
    }

    public void setSemesterImageL(String semesterImageL) {
        this.semesterImageL = semesterImageL;
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

    public List<UniversitySubject> getUniversitySubject() {
        return universitySubject;
    }

    public void setUniversitySubject(List<UniversitySubject> universitySubject) {
        this.universitySubject = universitySubject;
    }
}
