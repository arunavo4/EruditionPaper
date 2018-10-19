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
    @SerializedName("SubjectName")
    @Expose
    private String subjectName;
    @SerializedName("SubjectFullName")
    @Expose
    private String subjectFullName;
    @SerializedName("ChapterCode")
    @Expose
    private String chapterCode;
    @SerializedName("ChapterName")
    @Expose
    private String chapterName;
    @SerializedName("ChapterFullName")
    @Expose
    private String chapterFullName;
    @SerializedName("Solved")
    @Expose
    private String solved;

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

    public String getChapterCode() {
        return chapterCode;
    }

    public void setChapterCode(String chapterCode) {
        this.chapterCode = chapterCode;
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

    public String getSolved() {
        return solved;
    }

    public void setSolved(String solved) {
        this.solved = solved;
    }
}
