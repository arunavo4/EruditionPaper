package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arunavo Ray on 31-03-2018.
 */

public class Yang {
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("accesstoken")
    @Expose
    private String accesstoken;
    @SerializedName("accesstokenexp")
    @Expose
    private Integer accesstokenexp;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

}
