package sminq.com.geolocation_test;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Pawan on 13/08/17.
 */

public class GeofenceTransitionIntentService extends IntentService {

    //Least priority variables goes below....
    private static final String TAG = "GeofenceTransitionIS";

    public GeofenceTransitionIntentService() {
        super(TAG);
    }//GeofenceTransitionIntentService constructor closes here....

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }//onHandleIntent closes here....
}//GeofenceTransitionIntentService closes here....
