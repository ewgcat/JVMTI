package com.lishuaihua.jvmti.demo;

import android.app.Application;

import com.lishuaihua.jvmti.Monitor;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Monitor.init(this);
    }
}
