package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arunavo Ray on 31-03-2018.
 */


public class Yin {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("accesstoken")
    @Expose
    private String accesstoken;
    @SerializedName("accesstokenexp")
    @Expose
    private Integer accesstokenexp;
    @SerializedName("refreshtoken")
    @Expose
    private String refreshtoken;
    @SerializedName("refreshtokenexp")
    @Expose
    private Integer refreshtokenexp;
    @SerializedName("status")
    @Expose
    private String status;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }

    public Integer getAccesstokenexp() {
        return accesstokenexp;
    }

    public void setAccesstokenexp(Integer accesstokenexp) {
        this.accesstokenexp = accesstokenexp;
    }

    public String getRefreshtoken() {
        return refreshtoken;
    }

    public void setRefreshtoken(String refreshtoken) {
        this.refreshtoken = refreshtoken;
    }

    public Integer getRefreshtokenexp() {
        return refreshtokenexp;
    }

    public void setRefreshtokenexp(Integer refreshtokenexp) {
        this.refreshtokenexp = refreshtokenexp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
