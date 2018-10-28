package in.co.erudition.paper;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;

public class Erudition extends Application {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    public static Context contextOfApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        FirebaseApp.initializeApp(this);
        //AdMob init
        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));
        //Application context
        contextOfApplication = getApplicationContext();
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static Context getContextOfApplication(){
        return contextOfApplication;
    }
    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
