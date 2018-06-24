package in.co.erudition.paper.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Arunavo Ray on 13-06-2018.
 */

public class QuestionAnswer implements Parcelable{

    private String groupName;
    private String groupDesc1;
    private String groupDesc2;
    private String questionCode;
    private String questionNo;
    private String question;
    private String answer;
    private String marks;
    private String repeat;

    public QuestionAnswer(PaperGroup paperGroup,PaperQuestion paperQuestion){
        groupName = paperGroup.getGroupName();
        groupDesc1 = paperGroup.getGroupDesc1();
        groupDesc2 = paperGroup.getGroupDesc2();
        questionCode = paperQuestion.getQuestionCode();
        questionNo = paperQuestion.getQuestionNo();
        question = paperQuestion.getQuestion();
        answer = paperQuestion.getAnswer();
        marks = paperQuestion.getMarks();
        repeat = paperQuestion.getRepeat();
    }

    protected QuestionAnswer(Parcel in) {
        groupName = in.readString();
        groupDesc1 = in.readString();
        groupDesc2 = in.readString();
        questionCode = in.readString();
        questionNo = in.readString();
        question = in.readString();
        answer = in.readString();
        marks = in.readString();
        repeat = in.readString();
    }

    public static final Creator<QuestionAnswer> CREATOR = new Creator<QuestionAnswer>() {
        @Override
        public QuestionAnswer createFromParcel(Parcel in) {
            return new QuestionAnswer(in);
        }

        @Override
        public QuestionAnswer[] newArray(int size) {
            return new QuestionAnswer[size];
        }
    };

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDesc1() {
        return groupDesc1;
    }

    public void setGroupDesc1(String groupDesc1) {
        this.groupDesc1 = groupDesc1;
    }

    public String getGroupDesc2() {
        return groupDesc2;
    }

    public void setGroupDesc2(String groupDesc2) {
        this.groupDesc2 = groupDesc2;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(groupName);
        dest.writeString(groupDesc1);
        dest.writeString(groupDesc2);
        dest.writeString(questionCode);
        dest.writeString(questionNo);
        dest.writeString(question);
        dest.writeString(answer);
        dest.writeString(marks);
        dest.writeString(repeat);
    }
}
