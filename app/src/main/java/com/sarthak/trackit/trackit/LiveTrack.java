package com.sarthak.trackit.trackit;

import android.annotation.SuppressLint;
import android.app.Application;

import com.sarthak.trackit.trackit.utils.TypefaceUtil;

public class LiveTrack extends Application {

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate() {
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/montserrat_light.otf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
    }
}
