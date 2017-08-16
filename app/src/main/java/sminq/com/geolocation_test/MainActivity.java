package sminq.com.geolocation_test;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Pawan on 16/08/17.
 */

public class MainActivity extends AppCompatActivity {

    //High priority UI variables goes below...
    private TextView mMainTxtV;



    //Medium priority variables goes below....
    private final int radius = 40;//This is the radius of GeoFencing.
    private final double latitude = 40;//THis is the Latitude of the GeoFencing.
    private final double longitude = 40;//This is the Longitude of the GeoFencing.
    private GeofencingClient mGeoFencingClient;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private final String GEO_FENCE_KEY = "SMINQ_OFFICE_LOCATION";


    private GeofencingRequest geoFencingRequst;
    private PendingIntent geoFencingIntent;



    //Least priority variables goes below....
    private final String TAG = "MainActivity";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializations();


        /////////////////////..............LETS ASK FOR THE LOCATION PERMISSIONS.................\\\\\\\\\\\\\\\\\
        chkLocationPermissions();



        mMainTxtV.setText(getString(R.string.introTextPrefix)
                +" ("+latitude+","+longitude+") "
                +getString(R.string.introTextMiddle)
                +" "+radius+" "
                +getString(R.string.introTextSuffix));




        //Accessing the GeoFrncing Client....
        mGeoFencingClient = LocationServices.getGeofencingClient(this);

    }//onCreatecloses here....


    /**
     * This method asks for Location permissions & when permission is permitted, we will add the GeoFence.
     * **/
    private void chkLocationPermissions() {


        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)
        {
            //If ACCESS FINE LOCAITON is permission denied then control comes here.
            //So lets ask for permissions...

            boolean shouldProvideRationale =
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION);

            // Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn't check the "Don't ask again" checkbox.
            if (shouldProvideRationale) {
                showSnackbar(R.string.permission_rationale, android.R.string.ok,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Request permission
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_PERMISSIONS_REQUEST_CODE);
                            }//onClick closes here....
                        });//View.OnClickListener closes here.....
            }//if (shouldProvideRationale) closes here....
            else {
                // Request permission. It's possible this can be auto answered if device policy
                // sets the permission in a given state or the user denied the permission
                // previously and checked "Never ask again".
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSIONS_REQUEST_CODE);
            }//else Requesting Permissions closes here....

        }//if ACCESS_FINE_LOCATION is PErmission Denied closes here....

    }//chkLocationPermissions closes here.....



    /**
     * When we ask for Locaion permissions, the rsultant comes in this method.
     * **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case REQUEST_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length <= 0) {
                    // If user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                    Log.i(TAG, "User interaction was cancelled.");
                }//if(grantResults.length <= 0) closes here....
                else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission granted.");
                    addGeofences();
                }//else if PERMISSIONS_GRANTED closes here....
                else {
                    // Permission denied.

                    // Notify the user via a SnackBar that they have rejected a core permission for the
                    // app, which makes the Activity useless. In a real app, core permissions would
                    // typically be best requested during a welcome-screen flow.

                    // Additionally, it is important to remember that a permission might have been
                    // rejected without asking the user for permission (device policy or "Never ask
                    // again" prompts). Therefore, a user interface affordance is typically implemented
                    // when permissions are denied. Otherwise, your app could appear unresponsive to
                    // touches or interactions which have required permissions.
                    showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Build intent that displays the App settings screen.
                                    Intent intent = new Intent();
                                    intent.setAction(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package",
                                            BuildConfig.APPLICATION_ID, null);
                                    intent.setData(uri);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });
                }//Permission denied closes here....
                break;

            default:
                break;
        }//switch (requestCode) closes here....
    }//onRequestPermissionsResult closes here....



    /**
     * This method is called when user wants to Add Geofences.
     * This method is called, when permissions are permitted.
     * **/
    private void addGeofences() {

        if(mGeoFencingClient != null) {

            //Creating GeoFencing Request.....
            GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);/** The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
                                                                                    GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
                                                                                    is already inside that geofence.**/


            Calendar geoFenceExpiryCalendar = Calendar.getInstance();
            geoFenceExpiryCalendar.add(Calendar.HOUR, 24);//We will add 24 Hrs, in the Calendar, i,e, GeFencing will expire after 2 Hrs from current Time.

            Geofence geoFence = new Geofence.Builder().setRequestId(GEO_FENCE_KEY)
                                    .setCircularRegion(latitude, longitude, radius)
                                    .setExpirationDuration(geoFenceExpiryCalendar.getTimeInMillis())
                                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                                    .build();


            // Add the geofences to be monitored by geofencing service.
            ArrayList<Geofence> geofencesAl = new ArrayList<>();
            geofencesAl.add(geoFence);
            builder.addGeofences(geofencesAl);
            geoFencingRequst = builder.build();


            //Creating Pending Intent for GeoFencing....
            Intent intent = new Intent(this, GeofenceTransitionIntentService.class);
            // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
            // addGeofences() and removeGeofences().
            geoFencingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);



            if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                mGeoFencingClient.addGeofences(geoFencingRequst, geoFencingIntent);
            else
                Log.w(TAG, "Geo Fence not added, bcoz of permission issues !");
        }//if(mGeoFencingClient != null) closes here....
    }//addGeofences closes here....


    private void initializations() {
        mMainTxtV = findViewById(R.id.main_activity_txtV);
    }//initializations closes here....


    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }//showSnackbar closes here....

}//MainActivity closes here....
