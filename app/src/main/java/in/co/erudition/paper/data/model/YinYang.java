package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arunavo Ray on 31-03-2018.
 */


public class YinYang {
    @SerializedName("yin")
    @Expose
    private String yin;
    @SerializedName("yang")
    @Expose
    private String yang;

    public String getYin() {
        return yin;
    }

    public void setYin(String yin) {
        this.yin = yin;
    }

    public String getYang() {
        return yang;
    }

    public void setYang(String yang) {
        this.yang = yang;
    }
}