package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arunavo Ray on 03-04-2018.
 */

public class PaperQuestion {
    @SerializedName("QuestionCode")
    @Expose
    private String questionCode;
    @SerializedName("QuestionNo")
    @Expose
    private String questionNo;
    @SerializedName("Question")
    @Expose
    private String question;
    @SerializedName("Answer")
    @Expose
    private String answer;
    @SerializedName("Marks")
    @Expose
    private String marks;
    @SerializedName("Repeat")
    @Expose
    private String repeat;
    @SerializedName("_id")
    @Expose
    private String id;

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

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
