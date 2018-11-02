package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaperCode {
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("PaperCode")
    @Expose
    private String paperCode;
    @SerializedName("QuestionCode")
    @Expose
    private String questionCode;
    @SerializedName("QuestionNo")
    @Expose
    private String questionNo;
    @SerializedName("ChapterName")
    @Expose
    private String chapterName;
    @SerializedName("Year")
    @Expose
    private String year;
    @SerializedName("Status")
    @Expose
    private String status;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPaperCode() {
        return paperCode;
    }

    public void setPaperCode(String paperCode) {
        this.paperCode = paperCode;
    }

    public String getQuestionCode() {
        return questionCode;
    }

    public void setQuestionCode(String questionCode) {
        this.questionCode = questionCode;
    }

    public String getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(String questionNo) {
        this.questionNo = questionNo;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
