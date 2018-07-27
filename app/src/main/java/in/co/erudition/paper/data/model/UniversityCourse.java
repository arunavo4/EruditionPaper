package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Arunavo Ray on 01-04-2018.
 */

public class UniversityCourse {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("FullName")
    @Expose
    private String fullName;
    @SerializedName("Logo")
    @Expose
    private String logo;
    @SerializedName("BoardCourse")
    @Expose
    private List<BoardCourse> boardCourse = null;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<BoardCourse> getBoardCourse() {
        return boardCourse;
    }

    public void setBoardCourse(List<BoardCourse> boardCourse) {
        this.boardCourse = boardCourse;
    }

}
