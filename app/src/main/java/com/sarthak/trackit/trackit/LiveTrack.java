package com.sarthak.trackit.trackit;

import android.annotation.SuppressLint;
import android.app.Application;

/**
 * Created by karan on 3/11/2018.
 */

public class LiveTrack extends Application {

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate() {
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/montserrat_light.otf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
    }
}
