package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Arunavo Ray on 03-04-2018.
 */

public class PaperGroup {
    @SerializedName("GroupCode")
    @Expose
    private String groupCode;
    @SerializedName("GroupName")
    @Expose
    private String groupName;
    @SerializedName("GroupDesc1")
    @Expose
    private String groupDesc1;
    @SerializedName("GroupDesc2")
    @Expose
    private String groupDesc2;
    @SerializedName("GroupMarks")
    @Expose
    private String groupMarks;
    @SerializedName("GroupMarksDesc")
    @Expose
    private String groupMarksDesc;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("PaperQuestion")
    @Expose
    private List<PaperQuestion> paperQuestion = null;

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

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

    public String getGroupMarks() {
        return groupMarks;
    }

    public void setGroupMarks(String groupMarks) {
        this.groupMarks = groupMarks;
    }

    public String getGroupMarksDesc() {
        return groupMarksDesc;
    }

    public void setGroupMarksDesc(String groupMarksDesc) {
        this.groupMarksDesc = groupMarksDesc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PaperQuestion> getPaperQuestion() {
        return paperQuestion;
    }

    public void setPaperQuestion(List<PaperQuestion> paperQuestion) {
        this.paperQuestion = paperQuestion;
    }
}
