package sminq.com.geolocation_test;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Pawan on 16/08/17.
 */

public class LatLongPOJO implements Parcelable {

    private double latitude;
    private double longitude;


    protected LatLongPOJO(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public LatLongPOJO(@NotNull double latitiude, @NotNull double longitude) {
        this.latitude = latitiude;
        this.longitude = longitude;
    }//LatLongPOJO constructor closes here....

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<LatLongPOJO> CREATOR = new Parcelable.Creator<LatLongPOJO>() {
        @Override
        public LatLongPOJO createFromParcel(Parcel in) {
            return new LatLongPOJO(in);
        }

        @Override
        public LatLongPOJO[] newArray(int size) {
            return new LatLongPOJO[size];
        }
    };


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
