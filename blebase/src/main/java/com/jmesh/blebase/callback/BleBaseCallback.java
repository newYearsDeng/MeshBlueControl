package com.jmesh.blebase.callback;


import android.os.Handler;

public abstract class BleBaseCallback {

    private String key;
    private int notifyInstanceId;
    private Handler handler;
    private String notifyUUID;

    public String getNotifyUUID() {
        return notifyUUID;
    }

    public void setNotifyUUID(String notifyUUID) {
        this.notifyUUID = notifyUUID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public int getNotifyInstanceId() {
        return notifyInstanceId;
    }

    public void setNotifyInstanceId(int notifyInstanceId) {
        this.notifyInstanceId = notifyInstanceId;
    }
}
