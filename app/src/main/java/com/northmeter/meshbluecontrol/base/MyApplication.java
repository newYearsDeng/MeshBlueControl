package com.northmeter.meshbluecontrol.base;

import android.app.Application;
import android.content.Context;

import com.jmesh.blebase.base.BleManager;
import com.northmeter.meshbluecontrol.bluetooth.BlueTooth_ConnectHelper;

/**
 * Created by dyd on 2018/11/28.
 */

public class MyApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        BlueTooth_ConnectHelper.getInstance();
        BleManager.getInstance().init(this);
    }
    public static Context getContext() {
        return context;
    }
}
