package in.co.erudition.paper.util;

import androidx.annotation.Keep;
import in.co.erudition.paper.data.remote.BackendService;
import in.co.erudition.paper.data.remote.RetrofitClient;

/**
 * Created by Arunavo Ray on 05-06-2018.
 */
@Keep
public class ApiUtils {
    //TODO: encrypt this address
    private static final String BASE_URL = "https://www.erudition.co.in/";

    public static BackendService getBackendService() {
        return RetrofitClient.getClient(BASE_URL).create(BackendService.class);
    }

    public static BackendService getAuthBackendService(){
        return RetrofitClient.getAuthClient(BASE_URL).create(BackendService.class);
    }
}
