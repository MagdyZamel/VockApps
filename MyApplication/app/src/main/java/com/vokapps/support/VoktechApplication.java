package com.vokapps.support;

import android.app.Application;

import io.intercom.android.sdk.Intercom;

/**
 * Created by magdyzamel on 7/12/17.
 */

public class VoktechApplication extends Application {

    static final String YOUR_API_KEY = "android_sdk-50527976245ff341377aaa16ea82c12e58817cd2";
    static final String YOUR_APP_ID = "ov82v36k";

    @Override public void onCreate() {
        super.onCreate();
        Intercom.initialize(this, YOUR_API_KEY, YOUR_APP_ID);


    }


}
