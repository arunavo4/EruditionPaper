package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResult {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("AuthorEId")
    @Expose
    private String authorEId;
    @SerializedName("AuthorEmail")
    @Expose
    private String authorEmail;
    @SerializedName("CreateDate")
    @Expose
    private String createDate;
    @SerializedName("CreateTime")
    @Expose
    private String createTime;
    @SerializedName("Question")
    @Expose
    private String question;
    @SerializedName("Answer")
    @Expose
    private String answer;
    @SerializedName("Marks")
    @Expose
    private String marks;
    @SerializedName("Javascript")
    @Expose
    private String javascript;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("PaperCode")
    @Expose
    private List<PaperCode> paperCode = null;

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

    public String getAuthorEId() {
        return authorEId;
    }

    public void setAuthorEId(String authorEId) {
        this.authorEId = authorEId;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PaperCode> getPaperCode() {
        return paperCode;
    }

    public void setPaperCode(List<PaperCode> paperCode) {
        this.paperCode = paperCode;
    }

    public String getJavascript() {
        return javascript;
    }

    public void setJavascript(String javascript) {
        this.javascript = javascript;
    }
}
