package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Arunavo Ray on 03-04-2018.
 */

public class Paper {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("Logo")
    @Expose
    private String logo;
    @SerializedName("Cover")
    @Expose
    private String cover;
    @SerializedName("Background")
    @Expose
    private String background;
    @SerializedName("Type")
    @Expose
    private String type;
    @SerializedName("BoardCode")
    @Expose
    private String boardCode;
    @SerializedName("BoardName")
    @Expose
    private String boardName;
    @SerializedName("BoardFullName")
    @Expose
    private String boardFullName;
    @SerializedName("CourseCode")
    @Expose
    private String courseCode;
    @SerializedName("CourseName")
    @Expose
    private String courseName;
    @SerializedName("CourseFullName")
    @Expose
    private String courseFullName;
    @SerializedName("SessionCode")
    @Expose
    private String sessionCode;
    @SerializedName("SessionName")
    @Expose
    private String sessionName;
    @SerializedName("SessionFullName")
    @Expose
    private String sessionFullName;
    @SerializedName("SubjectCode")
    @Expose
    private String subjectCode;
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
    @SerializedName("Year")
    @Expose
    private String year;
    @SerializedName("PaperTime")
    @Expose
    private String paperTime;
    @SerializedName("PaperMarks")
    @Expose
    private String paperMarks;
    @SerializedName("Price")
    @Expose
    private String price;
    @SerializedName("CreateDate")
    @Expose
    private String createDate;
    @SerializedName("CreateTime")
    @Expose
    private String createTime;
    @SerializedName("UpdateDate")
    @Expose
    private String updateDate;
    @SerializedName("UpdateTime")
    @Expose
    private String updateTime;
    @SerializedName("Solved")
    @Expose
    private String solved;
    @SerializedName("Tags")
    @Expose
    private String tags;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("View")
    @Expose
    private String view;
    @SerializedName("AuthorEId")
    @Expose
    private String authorEId;
    @SerializedName("AuthorEmail")
    @Expose
    private String authorEmail;
    @SerializedName("AuthorName")
    @Expose
    private String authorName;
    @SerializedName("PaperGroup")
    @Expose
    private List<PaperGroup> paperGroup = null;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getBoardFullName() {
        return boardFullName;
    }

    public void setBoardFullName(String boardFullName) {
        this.boardFullName = boardFullName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseFullName() {
        return courseFullName;
    }

    public void setCourseFullName(String courseFullName) {
        this.courseFullName = courseFullName;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSessionFullName() {
        return sessionFullName;
    }

    public void setSessionFullName(String sessionFullName) {
        this.sessionFullName = sessionFullName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPaperTime() {
        return paperTime;
    }

    public void setPaperTime(String paperTime) {
        this.paperTime = paperTime;
    }

    public String getPaperMarks() {
        return paperMarks;
    }

    public void setPaperMarks(String paperMarks) {
        this.paperMarks = paperMarks;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getSolved() {
        return solved;
    }

    public void setSolved(String solved) {
        this.solved = solved;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
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

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public List<PaperGroup> getPaperGroup() {
        return paperGroup;
    }

    public void setPaperGroup(List<PaperGroup> paperGroup) {
        this.paperGroup = paperGroup;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getChapterCode() {
        return chapterCode;
    }

    public void setChapterCode(String chapterCode) {
        this.chapterCode = chapterCode;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }
}


