package com.fansion.proxydelegatedemo;

import android.app.Application;
import android.util.Log;

/**
 * 原程序真实入口;在Proxy/Delegate框架中相当于Delegate
 */
public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("fan","MyApplication onCreate");
    }
}
