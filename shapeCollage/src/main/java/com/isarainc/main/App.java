package com.isarainc.main;

import android.app.Application;
import android.content.Context;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.isarainc.shapecollage.R;

import java.util.HashMap;

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
