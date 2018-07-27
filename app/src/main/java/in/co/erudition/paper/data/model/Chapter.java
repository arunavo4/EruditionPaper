package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Chapter {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("ChapterName")
    @Expose
    private String chapterName;
    @SerializedName("ChapterFullName")
    @Expose
    private String chapterFullName;
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

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterFullName() {
        return chapterFullName;
    }

    public void setChapterFullName(String chapterFullName) {
        this.chapterFullName = chapterFullName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
