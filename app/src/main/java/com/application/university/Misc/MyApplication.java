package com.application.university.Misc;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by ashish on 27/12/17.
 */

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
