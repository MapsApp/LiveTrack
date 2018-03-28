package com.sarthak.trackit.trackit.model;

<<<<<<< HEAD
public class LatLong {
=======
import android.os.Parcel;
import android.os.Parcelable;

public class LatLong implements Parcelable {
>>>>>>> c714b061e3347db877e77988722773d1698f4edf

    private String latitude;
    private String longitude;

    public LatLong() {
    }

    public LatLong(String latitude, String longitude) {

        this.latitude = latitude;
        this.longitude = longitude;
    }

<<<<<<< HEAD
=======
    protected LatLong(Parcel in) {

        this.latitude = in.readString();
        this.longitude = in.readString();
    }

    public static final Creator<LatLong> CREATOR = new Creator<LatLong>() {
        @Override
        public LatLong createFromParcel(Parcel in) {
            return new LatLong(in);
        }

        @Override
        public LatLong[] newArray(int size) {
            return new LatLong[size];
        }
    };

>>>>>>> c714b061e3347db877e77988722773d1698f4edf
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
<<<<<<< HEAD
=======

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(latitude);
        dest.writeString(longitude);
    }
>>>>>>> c714b061e3347db877e77988722773d1698f4edf
}
