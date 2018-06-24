package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arunavo Ray on 31-03-2018.
 */

public class University {

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
    @SerializedName("UniversityImageM")
    @Expose
    private String universityImageM;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Key")
    @Expose
    private String key;

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

    public String getUniversityImageM() {
        return universityImageM;
    }

    public void setUniversityImageM(String universityImageM) {
        this.universityImageM = universityImageM;
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
}
