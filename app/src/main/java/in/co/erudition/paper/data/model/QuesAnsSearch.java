package in.co.erudition.paper.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class QuesAnsSearch implements Parcelable {

    private String questionCode;
    private String question;
    private String answer;
    private String marks;
    private String status;
    private String javascript;

    public QuesAnsSearch(SearchResult searchResult){
        questionCode = searchResult.getCode();
        question = searchResult.getQuestion();
        answer = searchResult.getAnswer();
        marks = searchResult.getMarks();
        status = searchResult.getStatus();
        javascript = searchResult.getJavascript();
    }

    protected QuesAnsSearch(Parcel in) {
        questionCode = in.readString();
        question = in.readString();
        answer = in.readString();
        marks = in.readString();
        status = in.readString();
        javascript = in.readString();
    }

    public static final Creator<QuesAnsSearch> CREATOR = new Creator<QuesAnsSearch>() {
        @Override
        public QuesAnsSearch createFromParcel(Parcel in) {
            return new QuesAnsSearch(in);
        }

        @Override
        public QuesAnsSearch[] newArray(int size) {
            return new QuesAnsSearch[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(questionCode);
        dest.writeString(question);
        dest.writeString(answer);
        dest.writeString(marks);
        dest.writeString(status);
        dest.writeString(javascript);
    }

    public String getQuestionCode() {
        return questionCode;
    }

    public void setQuestionCode(String questionCode) {
        this.questionCode = questionCode;
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

    public String getJavascript() {
        return javascript;
    }

    public void setJavascript(String javascript) {
        this.javascript = javascript;
    }
}
