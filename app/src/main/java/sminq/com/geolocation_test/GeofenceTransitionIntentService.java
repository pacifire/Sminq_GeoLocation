package sminq.com.geolocation_test;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pawan on 13/08/17.
 */

public class GeofenceTransitionIntentService extends IntentService {

    //Least priority variables goes below....
    private static final String TAG = "GeofenceTransitionIS";


    //Medium priority variables goes below....
    private final String ENTRY_CHANNEL_ID = "ENTRY_CAHNNEL";
    private final String EXIT_CHANNEL_ID = "EXIT_CHANNEL";
    /**
     * Channels are needed, bcoz as per Android "O", the notifications can be grouped.
     * **/


    public GeofenceTransitionIntentService() {
        super(TAG);
    }//GeofenceTransitionIntentService constructor closes here....

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: ");
        
        GeofencingEvent geoFencingEvent = GeofencingEvent.fromIntent(intent);//Extractig the GeoFencingEvent from the Intent....

        if(geoFencingEvent.hasError()){
            //if GeoFencing Event gives some Error (like License issue, payment isuees or anyother issue)
            //The Error will be handled & displayed here.

            String errorMsg = GeofenceErrorMessages.getErrorString(this, geoFencingEvent.getErrorCode());
            Log.w(TAG, errorMsg);
            return;
        }//if(geoFencingEvent.hasError()) closes here....


        //If the GeoFencing Event is proper so lets chk the Type of Event, i.e. it is Entry or Exit...
        int geoFencingTransition = geoFencingEvent.getGeofenceTransition();
        switch (geoFencingTransition) {

            case Geofence.GEOFENCE_TRANSITION_ENTER:
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                // Get the geofences that were triggered. A single event can trigger multiple geofences.
                List<Geofence> triggeringGeofences = geoFencingEvent.getTriggeringGeofences();
                Log.d(TAG, "triggeringGeofences: "+triggeringGeofences.size());

                // Get the transition details as a String.
                String geofenceTransitionDetails = getGeofenceTransitionDetails(geoFencingTransition,
                        triggeringGeofences);

                // Send notification and log the transition details.
                sendNotification(geofenceTransitionDetails, geoFencingTransition);

                Log.i(TAG, geofenceTransitionDetails);
                break;

            default:
                Log.e(TAG, "User is Dwelling & we have not handled the Dwelling");
                break;
        }//switch (geoFencingEvent.getGeofenceTransition()) closes here....


    }//onHandleIntent closes here....



    /**
     * This method is responsible for displaying the Notificaton when user Enters or Exits the GeoFence.
     * **/
    private void sendNotification(@NotNull String geofenceTransitionDetails, int geoFencingTransition) {
        Intent notifictionIntent = new Intent(getApplicationContext(), MainActivity.class);
        NotificationCompat.Builder builder = null;

        switch (geoFencingTransition) {

            case Geofence.GEOFENCE_TRANSITION_ENTER:
                builder = new NotificationCompat.Builder(this, ENTRY_CHANNEL_ID);
                break;

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                builder = new NotificationCompat.Builder(this, EXIT_CHANNEL_ID);
                break;

            default:
                Log.w(TAG, "Unhandled geoFencing");
                break;
        }//switch (geoFencingTransition) closes here...


        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notifictionIntent);


        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);



        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(geofenceTransitionDetails)
                .setContentText(getString(R.string.app_name))
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());

    }//sendNotification closes here....


    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }





    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }
}//GeofenceTransitionIntentService closes here....
