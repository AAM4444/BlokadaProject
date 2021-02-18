package com.example.blokadaproject;

import android.app.Application;

import timber.log.Timber;

public class BlokadaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }
    }

}
