package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Arunavo Ray on 01-04-2018.
 */

public class UniversityStream {
    @SerializedName("StreamCode")
    @Expose
    private String streamCode;
    @SerializedName("StreamName")
    @Expose
    private String streamName;
    @SerializedName("StreamFullName")
    @Expose
    private String streamFullName;
    @SerializedName("StreamImageS")
    @Expose
    private String streamImageS;
    @SerializedName("StreamImageM")
    @Expose
    private String streamImageM;
    @SerializedName("StreamImageL")
    @Expose
    private String streamImageL;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Key")
    @Expose
    private String key;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("UniversitySemester")
    @Expose
    private List<UniversitySemester> universitySemester = null;

    public String getStreamCode() {
        return streamCode;
    }

    public void setStreamCode(String streamCode) {
        this.streamCode = streamCode;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public String getStreamFullName() {
        return streamFullName;
    }

    public void setStreamFullName(String streamFullName) {
        this.streamFullName = streamFullName;
    }

    public String getStreamImageS() {
        return streamImageS;
    }

    public void setStreamImageS(String streamImageS) {
        this.streamImageS = streamImageS;
    }

    public String getStreamImageM() {
        return streamImageM;
    }

    public void setStreamImageM(String streamImageM) {
        this.streamImageM = streamImageM;
    }

    public String getStreamImageL() {
        return streamImageL;
    }

    public void setStreamImageL(String streamImageL) {
        this.streamImageL = streamImageL;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<UniversitySemester> getUniversitySemester() {
        return universitySemester;
    }

    public void setUniversitySemester(List<UniversitySemester> universitySemester) {
        this.universitySemester = universitySemester;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
