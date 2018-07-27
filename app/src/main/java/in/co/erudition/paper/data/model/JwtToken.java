package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JwtToken {
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Msg")
    @Expose
    private String msg;
    @SerializedName("EId")
    @Expose
    private String eId;
    @SerializedName("AccessToken")
    @Expose
    private String accessToken;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getEId() {
        return eId;
    }

    public void setEId(String eId) {
        this.eId = eId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
