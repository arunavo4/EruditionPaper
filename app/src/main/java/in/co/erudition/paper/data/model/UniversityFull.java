package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Arunavo Ray on 01-04-2018.
 */

public class UniversityFull {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("UniversityCode")
    @Expose
    private String universityCode;
    @SerializedName("UniversityName")
    @Expose
    private String universityName;
    @SerializedName("UniversityFullName")
    @Expose
    private String universityFullName;
    @SerializedName("UniversityImageS")
    @Expose
    private String universityImageS;
    @SerializedName("UniversityImageM")
    @Expose
    private String universityImageM;
    @SerializedName("UniversityImageL")
    @Expose
    private String universityImageL;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("Key")
    @Expose
    private String key;
    @SerializedName("UniversityCourse")
    @Expose
    private List<UniversityCourse> universityCourse = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUniversityFullName() {
        return universityFullName;
    }

    public void setUniversityFullName(String universityFullName) {
        this.universityFullName = universityFullName;
    }

    public String getUniversityImageS() {
        return universityImageS;
    }

    public void setUniversityImageS(String universityImageS) {
        this.universityImageS = universityImageS;
    }

    public String getUniversityImageM() {
        return universityImageM;
    }

    public void setUniversityImageM(String universityImageM) {
        this.universityImageM = universityImageM;
    }

    public String getUniversityImageL() {
        return universityImageL;
    }

    public void setUniversityImageL(String universityImageL) {
        this.universityImageL = universityImageL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<UniversityCourse> getUniversityCourse() {
        return universityCourse;
    }

    public void setUniversityCourse(List<UniversityCourse> universityCourse) {
        this.universityCourse = universityCourse;
    }

}
