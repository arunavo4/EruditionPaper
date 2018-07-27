package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Year {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("SubjectName")
    @Expose
    private String subjectName;
    @SerializedName("SubjectFullName")
    @Expose
    private String subjectFullName;
    @SerializedName("Year")
    @Expose
    private String year;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
