package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arunavo Ray on 03-04-2018.
 */

public class PaperQuestion {
    @SerializedName("Question")
    @Expose
    private String question;
    @SerializedName("Answer")
    @Expose
    private String answer;
    @SerializedName("Repeat")
    @Expose
    private String repeat;
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("QuestionCode")
    @Expose
    private String questionCode;
    @SerializedName("QuestionNo")
    @Expose
    private String questionNo;
    @SerializedName("Marks")
    @Expose
    private String marks;
    @SerializedName("Status")
    @Expose
    private String status;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }
}
