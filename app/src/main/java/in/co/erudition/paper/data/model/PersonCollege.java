package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PersonCollege {
    @SerializedName("InstituteType")
    @Expose
    private String instituteType;
    @SerializedName("InstituteCode")
    @Expose
    private String instituteCode;
    @SerializedName("InstituteTitle")
    @Expose
    private String instituteTitle;
    @SerializedName("ApplicationFee")
    @Expose
    private String applicationFee;
    @SerializedName("RegDate")
    @Expose
    private String regDate;
    @SerializedName("RegTime")
    @Expose
    private String regTime;
    @SerializedName("Status")
    @Expose
    private String status;

    public String getInstituteType() {
        return instituteType;
    }

    public void setInstituteType(String instituteType) {
        this.instituteType = instituteType;
    }

    public String getInstituteCode() {
        return instituteCode;
    }

    public void setInstituteCode(String instituteCode) {
        this.instituteCode = instituteCode;
    }

    public String getInstituteTitle() {
        return instituteTitle;
    }

    public void setInstituteTitle(String instituteTitle) {
        this.instituteTitle = instituteTitle;
    }

    public String getApplicationFee() {
        return applicationFee;
    }

    public void setApplicationFee(String applicationFee) {
        this.applicationFee = applicationFee;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
