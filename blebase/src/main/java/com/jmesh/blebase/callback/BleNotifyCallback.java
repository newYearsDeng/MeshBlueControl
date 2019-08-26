package com.jmesh.blebase.callback;


import com.jmesh.blebase.exception.BleException;

public abstract class BleNotifyCallback extends BleBaseCallback {

    public abstract void onNotifySuccess(int deviceKey, int instantceId, String uuid);

    public abstract void onNotifyFailure(BleException exception);

    public abstract void onCharacteristicChanged(byte[] data, int notifyInstanceId, String uuid);

}
