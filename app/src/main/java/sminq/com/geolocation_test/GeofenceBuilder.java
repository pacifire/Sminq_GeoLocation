package sminq.com.geolocation_test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.widget.TextView;

import com.google.android.gms.location.Geofence;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Pawan on 16/08/17.
 */

public class GeofenceBuilder {


    public static ArrayList<Geofence> geoFencesAl;
    private static HashMap<String, LatLongPOJO> circularRegionMap = new HashMap<>();
    private static final int radius = 100;//This is the radius of GeoFencing.
    private @NonNull static TextView textBuilder;
    private static Context mContext;

    /**
     * Developer should call this method to get the List of Geofences to be added in the App.
     **/
    public static ArrayList<Geofence> getGeoFences() {

        if (geoFencesAl == null) {
            //Lets create the Al for Geolocation & return...
            initialize();


            //Iniializing the Event expiry time....
            Calendar geoFenceExpiryCalendar = Calendar.getInstance();
            geoFenceExpiryCalendar.add(Calendar.HOUR, 24);//We will add 24 Hrs, in the Calendar, i,e, GeFencing will expire after 2 Hrs from current Time.


            Iterator it = circularRegionMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();

                //        mMainTxtV.setText(getString(R.string.introTextPrefix)
//                +" ("+latitude+","+longitude+") "
//                +getString(R.string.introTextMiddle)
//                +" "+radius+" "
//                +getString(R.string.introTextSuffix));




                Geofence geoFence = new Geofence.Builder().setRequestId(pair.getKey().toString().trim())
                        .setCircularRegion(((LatLongPOJO) pair.getValue()).getLatitude(), ((LatLongPOJO) pair.getValue()).getLongitude(), radius)
                        .setExpirationDuration(geoFenceExpiryCalendar.getTimeInMillis())
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build();


                geoFencesAl.add(geoFence);





                ///////////////..............ADDING TEXT ON TEXTVIEW..........JUST FOR UI PURPOSES, NOTHING TO-DO WITH THE LOGIC..............\\\\\\\\\\\\\\\\\
                if(GeofenceBuilder.textBuilder != null && mContext != null){

                    if(GeofenceBuilder.textBuilder.getText().toString().trim().equals(mContext.getString(R.string.permission_rationale)))
                        GeofenceBuilder.textBuilder.setText("");


                    if(GeofenceBuilder.textBuilder.getText().toString().trim().isEmpty()){
                        //if Txt is empty then control comes here....
                        GeofenceBuilder.textBuilder.setText(mContext.getString(R.string.introTextPrefix)
                                +" "+pair.getKey().toString().trim()+" "
                                +" "+mContext.getString(R.string.introGeoFenceNameSuffix)
                                +" ("+((LatLongPOJO) pair.getValue()).getLatitude()+","+((LatLongPOJO) pair.getValue()).getLongitude()+") "
                                +mContext.getString(R.string.introTextMiddle)
                                +" "+radius+" "
                                +mContext.getString(R.string.introTextSuffix));
                    }//if Text is empty closes here....
                    else{
                        //else textV is not empty so leta append the data.....
                        GeofenceBuilder.textBuilder.append("\n\n\n"
                                +mContext.getString(R.string.introTextPrefix)
                                +" "+pair.getKey().toString().trim()
                                +" "+mContext.getString(R.string.introGeoFenceNameSuffix)
                                +" ("+((LatLongPOJO) pair.getValue()).getLatitude()+","+((LatLongPOJO) pair.getValue()).getLongitude()+") "
                                +mContext.getString(R.string.introTextMiddle)
                                +" "+radius+" "
                                +mContext.getString(R.string.introTextSuffix));
                    }//else closes here...
                }//if(GeofenceBuilder.textBuilder != null) closes here...
                //////////////////////..............DECORTAING UI CLOSES ABOVE.............\\\\\\\\\\\\\\\\\\\\\\\\\

                it.remove(); // avoids a ConcurrentModificationException
            }//while (it.hasNext()) closes here....
        }//if(geoFencesAl == null) closes here....

        return geoFencesAl;
    }//getGeoFences closes here.....


    /**
     * Lets initialze the Map such that it can be added in the Geofence.
     **/
    private static void initialize() {
        geoFencesAl = new ArrayList<Geofence>();

        circularRegionMap.put("DAGDUSHETH TEMPLE", new LatLongPOJO(18.516422, 73.856103));
        circularRegionMap.put("ISCKON TEMPLE", new LatLongPOJO(18.447876, 73.880686));
        circularRegionMap.put("SMINQ_OFFICE_LOCATION", new LatLongPOJO(18.544763, 73.911425));
        circularRegionMap.put("HGS_OFFICE_LOCATION", new LatLongPOJO(19.062964, 72.998081));
        circularRegionMap.put("PUNE JN", new LatLongPOJO(18.528896, 73.874391));

    }//initialize closes here.....


    public static HashMap<String, LatLongPOJO> getCircularRegionMap() {
        return circularRegionMap;
    }

    public static void setTextBuilder(@NonNull TextView textBuilder, @NotNull Context mContexr) {
        GeofenceBuilder.textBuilder = textBuilder;
        GeofenceBuilder.mContext = mContexr;
    }
}//GeofenceBuilder closes here....
