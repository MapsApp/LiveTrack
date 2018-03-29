package com.sarthak.trackit.trackit.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

public class ParcelableGeoPoint implements Parcelable {

    private GeoPoint geoPoint;

    public ParcelableGeoPoint() {
    }

    public ParcelableGeoPoint(GeoPoint geoPoint) {

        this.geoPoint = geoPoint;
    }

    private ParcelableGeoPoint(Parcel in) {
        double lat = in.readDouble();
        double lon = in.readDouble();
        geoPoint = new GeoPoint(lat, lon);
    }

    public static final Creator<ParcelableGeoPoint> CREATOR = new Creator<ParcelableGeoPoint>() {
        @Override
        public ParcelableGeoPoint createFromParcel(Parcel in) {
            return new ParcelableGeoPoint(in);
        }

        @Override
        public ParcelableGeoPoint[] newArray(int size) {
            return new ParcelableGeoPoint[size];
        }
    };

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(geoPoint.getLatitude());
        out.writeDouble(geoPoint.getLongitude());
    }
}
