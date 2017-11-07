package com.lucidleanlabs.dev.lcatalogmod.AR;

import android.annotation.SuppressLint;
import android.app.Application;

public class ARNativeApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Application sInstance;

    // Anywhere in the application where an instance is required, this method
    // can be used to retrieve it.
    public static Application getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        //((ARNativeApplication) sInstance).initializeInstance();
    }




}
