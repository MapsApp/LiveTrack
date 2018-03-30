package com.sarthak.trackit.trackit.utils;

import com.sarthak.trackit.trackit.model.ParcelableGeoPoint;

import java.util.HashMap;

public interface LocationSentListener {

    void passLocationToFragment(HashMap<String, ParcelableGeoPoint> map);
}
