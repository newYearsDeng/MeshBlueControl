package com.jmesh.blebase.callback;

import com.jmesh.blebase.state.BleDevice;

/**
 * Created by BC119 on 2018/6/21.
 */

public interface BleScanPresenterImp {
    void onScanStarted(boolean success);

    void onScanning(BleDevice bleDevice);
}
