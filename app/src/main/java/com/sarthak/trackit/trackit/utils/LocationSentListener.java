package com.sarthak.trackit.trackit.utils;

import com.sarthak.trackit.trackit.model.ParcelableGeoPoint;

import java.util.ArrayList;

public interface LocationSentListener {

    void passLocationToFragment(ArrayList<ParcelableGeoPoint> list);
}
