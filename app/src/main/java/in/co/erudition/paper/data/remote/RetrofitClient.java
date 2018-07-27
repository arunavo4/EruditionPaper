package in.co.erudition.paper.data.remote;

import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;

import in.co.erudition.paper.data.model.JwtToken;
import in.co.erudition.paper.util.ApiUtils;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Arunavo Ray on 05-06-2018.
 */

public class RetrofitClient {
    //RetroFit Instance Class
    private static Retrofit retrofit = null;
    private static Retrofit retrofitAuth = null;
    private static String AccessToken = null;
    private static BackendService mService = ApiUtils.getAuthBackendService();
    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor mPrefsEdit;

    //TODO: delete these after making the login
    /*
    DOnt encrypt just temporary
     */
    private static final String eid = "Android";
    private static final String password = "Password";

    private RetrofitClient(){
        // this default constructor is private and you can't call it like :
        // RetrofitClient client = new RetrofitClient();
        // only way calling it : Retrofit client = RetrofitClient.getClient();
    }

    public static Retrofit getClient(String baseUrl) {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

            OkHttpClient httpClient = new OkHttpClient
                    .Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            //get the current request
                            Request request = chain.request();
                            Request.Builder newRequest = request.newBuilder();
                            if (hasToken()) {
                                newRequest.header("Authorization", getToken());
                                Log.d("Interceptor->", "hasToken");
                            }
                            else{
                                //probably the first time
                                Log.d("Interceptor->", "NoToken");
                                newRequest.header("Authorization", generateToken());
                            }
                            okhttp3.Response response = chain.proceed(newRequest.build());

                            boolean unauthorized = response.code() == 401;
                            if (unauthorized) {
                                Log.d("Interceptor->", "Unauthorized");
                                clearToken();
                                //String newToken = mTokenManager.refreshToken();
                                newRequest.header("Authorization", refreshToken());
                                return chain.proceed(newRequest.build());
                            }
                            return response;
                        }
                    })
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getAuthClient(String baseUrl) {
        if (retrofitAuth == null) {

            Dispatcher dispatcher = new Dispatcher();
            dispatcher.setMaxRequests(1);

            OkHttpClient httpClient = new OkHttpClient
                    .Builder()
                    .dispatcher(dispatcher)
                    .build();

            retrofitAuth = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitAuth;
    }


    private static String getToken() {
        return AccessToken;
    }


    private static String generateToken() {
        Log.d("Interceptor","generating Token");

        //make synchronous calls;
        try{
            Call<JwtToken> jwtTokenCall = mService.getToken(eid,password);
            Response<JwtToken> jwtTokenResponse = jwtTokenCall.execute();
            JwtToken jwtToken = jwtTokenResponse.body();
            Log.d("jwtCall", jwtTokenResponse.raw().toString());

            Log.d("Jwt Token ",String.valueOf(jwtTokenResponse.body()));

            AccessToken = jwtToken.getAccessToken();
            Log.d("Access Token->",String.valueOf(AccessToken));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return AccessToken;
    }


    private static boolean hasToken() {
        return AccessToken!=null;
    }


    private static void clearToken() {
        if(AccessToken!=null)
            AccessToken = null;
    }


    private static String refreshToken() {


        return null;
    }


}
