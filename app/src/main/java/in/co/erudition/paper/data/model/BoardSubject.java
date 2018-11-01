package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BoardSubject {

    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("FullName")
    @Expose
    private String fullName;
    @SerializedName("Logo")
    @Expose
    private String logo;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("State")
    @Expose
    private String state;
    @SerializedName("Syllabus")
    @Expose
    private String syllabus;
    @SerializedName("YearView")
    @Expose
    private String yearView;
    @SerializedName("ChapterView")
    @Expose
    private String chapterView;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSyllabus() {
        return syllabus;
    }

    public void setSyllabus(String syllabus) {
        this.syllabus = syllabus;
    }

    public String getYearView() {
        return yearView;
    }

    public void setYearView(String yearView) {
        this.yearView = yearView;
    }

    public String getChapterView() {
        return chapterView;
    }

    public void setChapterView(String chapterView) {
        this.chapterView = chapterView;
    }
}
