package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BoardCollege {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("BoardCode")
    @Expose
    private String boardCode;
    @SerializedName("BoardCC")
    @Expose
    private String boardCC;
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("FullName")
    @Expose
    private String fullName;
    @SerializedName("City")
    @Expose
    private String city;
    @SerializedName("Website")
    @Expose
    private String website;
    @SerializedName("Email")
    @Expose
    private String email;
    @SerializedName("Mobile")
    @Expose
    private String mobile;
    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("AdminEId")
    @Expose
    private String adminEId;
    @SerializedName("AdminEmail")
    @Expose
    private String adminEmail;
    @SerializedName("RegDate")
    @Expose
    private String regDate;
    @SerializedName("RegTime")
    @Expose
    private String regTime;
    @SerializedName("Remark")
    @Expose
    private String remark;
    @SerializedName("GovermentId")
    @Expose
    private String govermentId;
    @SerializedName("Avatar")
    @Expose
    private String avatar;
    @SerializedName("Status")
    @Expose
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getBoardCC() {
        return boardCC;
    }

    public void setBoardCC(String boardCC) {
        this.boardCC = boardCC;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAdminEId() {
        return adminEId;
    }

    public void setAdminEId(String adminEId) {
        this.adminEId = adminEId;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getGovermentId() {
        return govermentId;
    }

    public void setGovermentId(String govermentId) {
        this.govermentId = govermentId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
