package com.jmesh.blebase.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;

import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.jmesh.blebase.base.BleManager;
import com.jmesh.blebase.callback.BleGattCallback;
import com.jmesh.blebase.callback.BleIndicateCallback;
import com.jmesh.blebase.callback.BleMtuChangedCallback;
import com.jmesh.blebase.callback.BleNotifyCallback;
import com.jmesh.blebase.callback.BleReadCallback;
import com.jmesh.blebase.callback.BleRssiCallback;
import com.jmesh.blebase.callback.BleWriteCallback;
import com.jmesh.blebase.exception.ConnectException;
import com.jmesh.blebase.exception.OtherException;
import com.jmesh.blebase.exception.TimeoutException;
import com.jmesh.blebase.state.BleConnectStateParameter;
import com.jmesh.blebase.state.BleDevice;
import com.jmesh.blebase.state.BleMsg;
import com.jmesh.blebase.utils.HexUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleBluetooth {
    public static final String LogTag = BleBluetooth.class.getSimpleName();
    private BleGattCallback bleGattCallback;
    private BleRssiCallback bleRssiCallback;
    private BleMtuChangedCallback bleMtuChangedCallback;
    private HashMap<String, BleNotifyCallback> bleNotifyCallbackHashMap = new HashMap<>();
    private HashMap<String, BleIndicateCallback> bleIndicateCallbackHashMap = new HashMap<>();
    private HashMap<String, BleWriteCallback> bleWriteCallbackHashMap = new HashMap<>();
    private HashMap<String, BleReadCallback> bleReadCallbackHashMap = new HashMap<>();

    private LastState lastState;
    private boolean isActiveDisconnect = false;
    private BleDevice bleDevice;
    private BluetoothGatt bluetoothGatt;
    private MainHandler mainHandler = new MainHandler(Looper.getMainLooper());
    private int connectRetryCount = 0;

    public BleBluetooth(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    public BleConnector newBleConnector() {
        return new BleConnector(this);
    }

    public synchronized void addConnectGattCallback(BleGattCallback callback) {
        bleGattCallback = callback;
    }

    public synchronized void removeConnectGattCallback() {
        bleGattCallback = null;
    }

    public synchronized void addNotifyCallback(String uuid, BleNotifyCallback bleNotifyCallback) {
        bleNotifyCallbackHashMap.put(uuid, bleNotifyCallback);
    }

    public synchronized void addIndicateCallback(String uuid, BleIndicateCallback bleIndicateCallback) {
        bleIndicateCallbackHashMap.put(uuid, bleIndicateCallback);
    }

    public synchronized void addWriteCallback(String uuid, BleWriteCallback bleWriteCallback) {
        bleWriteCallbackHashMap.put(uuid, bleWriteCallback);
    }

    public synchronized void addReadCallback(String uuid, BleReadCallback bleReadCallback) {
        bleReadCallbackHashMap.put(uuid, bleReadCallback);
    }

    public synchronized void removeNotifyCallback(String uuid) {
        if (bleNotifyCallbackHashMap.containsKey(uuid))
            bleNotifyCallbackHashMap.remove(uuid);
    }

    public synchronized void removeIndicateCallback(String uuid) {
        if (bleIndicateCallbackHashMap.containsKey(uuid))
            bleIndicateCallbackHashMap.remove(uuid);
    }

    public synchronized void removeWriteCallback(String uuid) {
        if (bleWriteCallbackHashMap.containsKey(uuid))
            bleWriteCallbackHashMap.remove(uuid);
    }

    public synchronized void removeReadCallback(String uuid) {
        if (bleReadCallbackHashMap.containsKey(uuid))
            bleReadCallbackHashMap.remove(uuid);
    }

    public synchronized void clearCharacterCallback() {
        if (bleNotifyCallbackHashMap != null)
            bleNotifyCallbackHashMap.clear();
        if (bleIndicateCallbackHashMap != null)
            bleIndicateCallbackHashMap.clear();
        if (bleWriteCallbackHashMap != null)
            bleWriteCallbackHashMap.clear();
        if (bleReadCallbackHashMap != null)
            bleReadCallbackHashMap.clear();
    }

    public synchronized void addRssiCallback(BleRssiCallback callback) {
        bleRssiCallback = callback;
    }

    public synchronized void removeRssiCallback() {
        bleRssiCallback = null;
    }

    public synchronized void addMtuChangedCallback(BleMtuChangedCallback callback) {
        bleMtuChangedCallback = callback;
    }

    public synchronized void removeMtuChangedCallback() {
        bleMtuChangedCallback = null;
    }


    public Integer getDeviceKey() {
        return bleDevice.getKey();
    }

    public BleDevice getDevice() {
        return bleDevice;
    }

    public BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    public synchronized BluetoothGatt connect(BleDevice bleDevice,
                                              boolean autoConnect,
                                              BleGattCallback callback) {
        return this.connect(bleDevice, autoConnect, callback, BleManager.getInstance().getConnectOverTime());
    }

    public synchronized BluetoothGatt connect(BleDevice bleDevice,
                                              boolean autoConnect,
                                              BleGattCallback callback, long timeout) {
        Log.i(LogTag, "connect device: " + bleDevice.getName()
                + "\nmac: " + bleDevice.getMac()
                + "\nautoConnect: " + autoConnect
                + "\ncurrentThread: " + Thread.currentThread().getId());

        addConnectGattCallback(callback);

        lastState = LastState.CONNECT_CONNECTING;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothGatt = bleDevice.getDevice().connectGatt(BleManager.getInstance().getContext(),
                    autoConnect, coreGattCallback, TRANSPORT_LE);
        } else {
//            bluetoothGatt = bleDevice.getDevice().connectGatt(BleManager.getInstance().getContext(),
//                    true, coreGattCallback);
            try {
                Class localClass = bleDevice.getDevice().getClass();
                Class[] arrayOfClass = new Class[4];
                arrayOfClass[0] = Context.class;
                arrayOfClass[1] = Boolean.TYPE;
                arrayOfClass[2] = BluetoothGattCallback.class;
                arrayOfClass[3] = Integer.TYPE;
                Method localMethod = localClass.getMethod("connectGatt", arrayOfClass);
                if (localMethod != null) {
                    Object[] arrayOfObject = new Object[4];
                    arrayOfObject[0] = BleManager.getInstance().getContext();
                    boolean bool2 = false;
                    arrayOfObject[1] = Boolean.valueOf(bool2);
                    arrayOfObject[2] = coreGattCallback;
                    arrayOfObject[3] = Integer.valueOf(2);
                    bluetoothGatt = (BluetoothGatt) localMethod.invoke(bleDevice.getDevice(), arrayOfObject);
                }
            } catch (Throwable throwable) {
                Log.e(LogTag, throwable.toString());
            }
        }
        if (bluetoothGatt != null) {
            if (bleGattCallback != null) {
                bleGattCallback.onStartConnect();
            }
            Message message = mainHandler.obtainMessage();
            message.what = BleMsg.MSG_CONNECT_OVER_TIME;
            mainHandler.sendMessageDelayed(message, timeout);
        } else {
            disconnectGatt();
            refreshDeviceCache();
            closeBluetoothGatt();
            lastState = LastState.CONNECT_FAILURE;
            BleManager.getInstance().getMultipleBluetoothController().removeConnectingBle(BleBluetooth.this);
            if (bleGattCallback != null)
                bleGattCallback.onConnectFail(bleDevice, new OtherException("GATT connect exception occurred!"));

        }
        return bluetoothGatt;
    }

    public synchronized void disconnect() {
        isActiveDisconnect = true;
        disconnectGatt();
    }

    public synchronized void destroy() {
        lastState = LastState.CONNECT_IDLE;
        disconnectGatt();
        refreshDeviceCache();
        closeBluetoothGatt();
        removeConnectGattCallback();
        removeRssiCallback();
        removeMtuChangedCallback();
        clearCharacterCallback();
        mainHandler.removeCallbacksAndMessages(null);
    }

    private synchronized void disconnectGatt() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
        }
    }

    private synchronized void refreshDeviceCache() {
        try {
            final Method refresh = BluetoothGatt.class.getMethod("refresh");
            if (refresh != null && bluetoothGatt != null) {
                boolean success = (Boolean) refresh.invoke(bluetoothGatt);
                Log.i(LogTag, "refreshDeviceCache, is success:  " + success);
            }
        } catch (Exception e) {
            Log.i(LogTag, "exception occur while refreshing device: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private synchronized void closeBluetoothGatt() {
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
        }
    }

    private final class MainHandler extends Handler {

        MainHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BleMsg.MSG_CONNECT_FAIL: {
                    disconnectGatt();
                    refreshDeviceCache();
                    closeBluetoothGatt();

                    if (connectRetryCount < BleManager.getInstance().getReConnectCount()) {
                        Log.e(LogTag, "Connect fail, try reconnect " + BleManager.getInstance().getReConnectInterval() + " Millisecond later");

                        connectRetryCount++;

                        Message message = mainHandler.obtainMessage();
                        message.what = BleMsg.MSG_RECONNECT;
                        mainHandler.sendMessageDelayed(message, BleManager.getInstance().getReConnectInterval());
                    } else {
                        connectRetryCount = 0;

                        lastState = LastState.CONNECT_FAILURE;
                        BleManager.getInstance().getMultipleBluetoothController().removeConnectingBle(BleBluetooth.this);

                        BleConnectStateParameter para = (BleConnectStateParameter) msg.obj;
                        int status = para.getStatus();
                        if (bleGattCallback != null) {
                            bleGattCallback.onConnectFail(bleDevice, new ConnectException(bluetoothGatt, status));
                        }

                    }
                }
                break;

                case BleMsg.MSG_DISCONNECTED: {
                    lastState = LastState.CONNECT_DISCONNECT;
                    BleManager.getInstance().getMultipleBluetoothController().removeBleBluetooth(BleBluetooth.this);

                    refreshDeviceCache();
                    closeBluetoothGatt();
                    removeRssiCallback();
                    removeMtuChangedCallback();
                    clearCharacterCallback();
                    mainHandler.removeCallbacksAndMessages(null);

                    BleConnectStateParameter para = (BleConnectStateParameter) msg.obj;
                    boolean isActive = para.isActive();
                    int status = para.getStatus();
                    if (bleGattCallback != null)
                        bleGattCallback.onDisConnected(isActive, bleDevice, bluetoothGatt, status);
                }
                break;

                case BleMsg.MSG_RECONNECT: {
                    connect(bleDevice, false, bleGattCallback);
                }
                break;

                case BleMsg.MSG_CONNECT_OVER_TIME: {
                    disconnectGatt();
                    refreshDeviceCache();
                    closeBluetoothGatt();

                    lastState = LastState.CONNECT_FAILURE;
                    BleManager.getInstance().getMultipleBluetoothController().removeConnectingBle(BleBluetooth.this);

                    if (bleGattCallback != null)
                        bleGattCallback.onConnectFail(bleDevice, new TimeoutException());
                }
                break;

                case BleMsg.MSG_DISCOVER_SERVICES: {
                    connectRetryCount = 0;
                    if (bluetoothGatt != null) {
                        boolean discoverServiceResult = bluetoothGatt.discoverServices();
                        if (!discoverServiceResult) {
                            Message message = mainHandler.obtainMessage();
                            message.what = BleMsg.MSG_DISCOVER_FAIL;
                            mainHandler.sendMessage(message);
                        }
                    } else {
                        Message message = mainHandler.obtainMessage();
                        message.what = BleMsg.MSG_DISCOVER_FAIL;
                        mainHandler.sendMessage(message);
                    }
                }
                break;

                case BleMsg.MSG_DISCOVER_FAIL: {
                    disconnectGatt();
                    refreshDeviceCache();
                    closeBluetoothGatt();

                    lastState = LastState.CONNECT_FAILURE;
                    BleManager.getInstance().getMultipleBluetoothController().removeConnectingBle(BleBluetooth.this);

                    if (bleGattCallback != null)
                        bleGattCallback.onConnectFail(bleDevice,
                                new OtherException("GATT discover services exception occurred!"));
                }
                break;

                case BleMsg.MSG_DISCOVER_SUCCESS: {
                    lastState = LastState.CONNECT_CONNECTED;
                    isActiveDisconnect = false;
                    BleManager.getInstance().getMultipleBluetoothController().removeConnectingBle(BleBluetooth.this);
                    BleManager.getInstance().getMultipleBluetoothController().addBleBluetooth(BleBluetooth.this);

                    BleConnectStateParameter para = (BleConnectStateParameter) msg.obj;
                    int status = para.getStatus();
                    if (bleGattCallback != null)
                        bleGattCallback.onConnectSuccess(bleDevice, bluetoothGatt, status);
                }
                break;

                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private BluetoothGattCallback coreGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.i(LogTag, "BluetoothGattCallback：onConnectionStateChange "
                    + '\n' + "status: " + status
                    + '\n' + "newState: " + newState
                    + '\n' + "currentThread: " + Thread.currentThread().getId());

            bluetoothGatt = gatt;

            mainHandler.removeMessages(BleMsg.MSG_CONNECT_OVER_TIME);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Message message = mainHandler.obtainMessage();
                message.what = BleMsg.MSG_DISCOVER_SERVICES;
                mainHandler.sendMessageDelayed(message, 500);

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (lastState == LastState.CONNECT_CONNECTING) {
                    Message message = mainHandler.obtainMessage();
                    message.what = BleMsg.MSG_CONNECT_FAIL;
                    message.obj = new BleConnectStateParameter(status);
                    mainHandler.sendMessage(message);

                } else if (lastState == LastState.CONNECT_CONNECTED) {
                    Message message = mainHandler.obtainMessage();
                    message.what = BleMsg.MSG_DISCONNECTED;
                    BleConnectStateParameter para = new BleConnectStateParameter(status);
                    para.setActive(isActiveDisconnect);
                    message.obj = para;
                    mainHandler.sendMessage(message);
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.i(LogTag, "BluetoothGattCallback：onServicesDiscovered "
                    + '\n' + "status: " + status
                    + '\n' + "currentThread: " + Thread.currentThread().getId());

            bluetoothGatt = gatt;

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Message message = mainHandler.obtainMessage();
                message.what = BleMsg.MSG_DISCOVER_SUCCESS;
                message.obj = new BleConnectStateParameter(status);
                mainHandler.sendMessage(message);

            } else {
                Message message = mainHandler.obtainMessage();
                message.what = BleMsg.MSG_DISCOVER_FAIL;
                mainHandler.sendMessage(message);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.e(BleBluetooth.LogTag, "ble_notify " + HexUtils.formatHexString(characteristic.getValue()));
            Iterator iterator = bleNotifyCallbackHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object callback = entry.getValue();
                if (callback instanceof BleNotifyCallback) {
                    BleNotifyCallback bleNotifyCallback = (BleNotifyCallback) callback;
                    if (characteristic.getUuid().toString().equalsIgnoreCase(bleNotifyCallback.getKey())) {
                        Handler handler = bleNotifyCallback.getHandler();
                        if (handler != null) {
                            Message message = handler.obtainMessage();
                            if (characteristic.getInstanceId() == 0) {
                                bleNotifyCallback.setNotifyInstanceId(getJmeshInstanceId(characteristic.getUuid().toString()));
                            } else {
                                bleNotifyCallback.setNotifyInstanceId(characteristic.getInstanceId());
                            }
                            bleNotifyCallback.setNotifyUUID(characteristic.getUuid().toString());
                            message.what = BleMsg.MSG_CHA_NOTIFY_DATA_CHANGE;
                            message.obj = bleNotifyCallback;
                            Bundle bundle = new Bundle();
                            bundle.putByteArray(BleMsg.KEY_NOTIFY_BUNDLE_VALUE, characteristic.getValue());
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }
                }
            }

            iterator = bleIndicateCallbackHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object callback = entry.getValue();
                if (callback instanceof BleIndicateCallback) {
                    BleIndicateCallback bleIndicateCallback = (BleIndicateCallback) callback;
                    if (characteristic.getUuid().toString().equalsIgnoreCase(bleIndicateCallback.getKey())) {
                        Handler handler = bleIndicateCallback.getHandler();
                        if (handler != null) {
                            Message message = handler.obtainMessage();
                            message.what = BleMsg.MSG_CHA_INDICATE_DATA_CHANGE;
                            message.obj = bleIndicateCallback;
                            Bundle bundle = new Bundle();
                            bundle.putByteArray(BleMsg.KEY_INDICATE_BUNDLE_VALUE, characteristic.getValue());
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }
                }
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);

            Iterator iterator = bleNotifyCallbackHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object callback = entry.getValue();
                if (callback instanceof BleNotifyCallback) {
                    BleNotifyCallback bleNotifyCallback = (BleNotifyCallback) callback;
                    if (descriptor.getCharacteristic().getUuid().toString().equalsIgnoreCase(bleNotifyCallback.getKey())) {
                        Handler handler = bleNotifyCallback.getHandler();
                        if (handler != null) {
                            Message message = handler.obtainMessage();
                            message.what = BleMsg.MSG_CHA_NOTIFY_RESULT;
                            bleNotifyCallback.setNotifyInstanceId(descriptor.getCharacteristic().getInstanceId());
                            bleNotifyCallback.setNotifyUUID(descriptor.getCharacteristic().getUuid().toString());
                            message.obj = bleNotifyCallback;
                            Bundle bundle = new Bundle();
                            bundle.putInt(BleMsg.KEY_NOTIFY_BUNDLE_STATUS, status);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }
                }
            }

            iterator = bleIndicateCallbackHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object callback = entry.getValue();
                if (callback instanceof BleIndicateCallback) {
                    BleIndicateCallback bleIndicateCallback = (BleIndicateCallback) callback;
                    if (descriptor.getCharacteristic().getUuid().toString().equalsIgnoreCase(bleIndicateCallback.getKey())) {
                        Handler handler = bleIndicateCallback.getHandler();
                        if (handler != null) {
                            Message message = handler.obtainMessage();
                            message.what = BleMsg.MSG_CHA_INDICATE_RESULT;
                            message.obj = bleIndicateCallback;
                            Bundle bundle = new Bundle();
                            bundle.putInt(BleMsg.KEY_INDICATE_BUNDLE_STATUS, status);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Iterator iterator = bleWriteCallbackHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object callback = entry.getValue();
                if (callback instanceof BleWriteCallback) {
                    BleWriteCallback bleWriteCallback = (BleWriteCallback) callback;
                    if (characteristic.getUuid().toString().equalsIgnoreCase(bleWriteCallback.getKey())) {
                        Handler handler = bleWriteCallback.getHandler();
                        if (handler != null) {
                            Message message = handler.obtainMessage();
                            message.what = BleMsg.MSG_CHA_WRITE_RESULT;
                            message.obj = bleWriteCallback;
                            Bundle bundle = new Bundle();
                            bundle.putInt(BleMsg.KEY_WRITE_BUNDLE_STATUS, status);
                            bundle.putByteArray(BleMsg.KEY_WRITE_BUNDLE_VALUE, characteristic.getValue());
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

            Iterator iterator = bleReadCallbackHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object callback = entry.getValue();
                if (callback instanceof BleReadCallback) {
                    BleReadCallback bleReadCallback = (BleReadCallback) callback;
                    if (characteristic.getUuid().toString().equalsIgnoreCase(bleReadCallback.getKey())) {
                        Handler handler = bleReadCallback.getHandler();
                        if (handler != null) {
                            Message message = handler.obtainMessage();
                            message.what = BleMsg.MSG_CHA_READ_RESULT;
                            message.obj = bleReadCallback;
                            Bundle bundle = new Bundle();
                            bundle.putInt(BleMsg.KEY_READ_BUNDLE_STATUS, status);
                            bundle.putByteArray(BleMsg.KEY_READ_BUNDLE_VALUE, characteristic.getValue());
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }
                }
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);

            if (bleRssiCallback != null) {
                Handler handler = bleRssiCallback.getHandler();
                if (handler != null) {
                    Message message = handler.obtainMessage();
                    message.what = BleMsg.MSG_READ_RSSI_RESULT;
                    message.obj = bleRssiCallback;
                    Bundle bundle = new Bundle();
                    bundle.putInt(BleMsg.KEY_READ_RSSI_BUNDLE_STATUS, status);
                    bundle.putInt(BleMsg.KEY_READ_RSSI_BUNDLE_VALUE, rssi);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);

            if (bleMtuChangedCallback != null) {
                Handler handler = bleMtuChangedCallback.getHandler();
                if (handler != null) {
                    Message message = handler.obtainMessage();
                    message.what = BleMsg.MSG_SET_MTU_RESULT;
                    message.obj = bleMtuChangedCallback;
                    Bundle bundle = new Bundle();
                    bundle.putInt(BleMsg.KEY_SET_MTU_BUNDLE_STATUS, status);
                    bundle.putInt(BleMsg.KEY_SET_MTU_BUNDLE_VALUE, mtu);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        }
    };

    enum LastState {
        CONNECT_IDLE,
        CONNECT_CONNECTING,
        CONNECT_CONNECTED,
        CONNECT_FAILURE,
        CONNECT_DISCONNECT
    }

    public int getJmeshInstanceId(String uuid) {
        if (uuid.equals("00002afe-0000-1000-8000-00805f9b34fb")) {
            return 14;
        } else if (uuid.equals("00002aff-0000-1000-8000-00805f9b34fb")) {
            return 16;
        } else if (uuid.equals("00002b00-0000-1000-8000-00805f9b34fb")) {
            return 19;
        } else if (uuid.equals("00002b01-0000-1000-8000-00805f9b34fb")) {
            return 22;
        } else if (uuid.equals("00002b02-0000-1000-8000-00805f9b34fb")) {
            return 25;
        }
        return 0;
    }

}
