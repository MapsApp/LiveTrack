package com.sarthak.trackit.trackit.utils;

import com.sarthak.trackit.trackit.model.LatLong;

import java.util.ArrayList;

public interface LocationSentListener {

    void passLocationToFragment(ArrayList<LatLong> list);
}
