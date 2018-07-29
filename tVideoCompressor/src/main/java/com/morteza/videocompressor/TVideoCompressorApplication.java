package com.morteza.videocompressor;

/*
*By Morteza bayat (@MathematicsAndroid (https://t.me/MathematicsAndroid)) 2018
* */

import android.app.Application;

import com.morteza.videocompressor.file.FileUtils;

abstract public class TVideoCompressorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FileUtils.createApplicationFolder();
    }

}