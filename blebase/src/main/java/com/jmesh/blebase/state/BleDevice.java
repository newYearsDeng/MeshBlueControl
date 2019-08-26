package com.jmesh.blebase.state;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.jmesh.blebase.base.BleManager;
import com.jmesh.blebase.utils.HexUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/**
 * Created by BC119 on 2018/6/21.
 */

public class BleDevice implements Parcelable {
    public static final String LogTag = BleDevice.class.getSimpleName();
    private BluetoothDevice mDevice;
    private byte[] mScanRecord;
    private int mRssi;
    private long mTimestampNanos;
    private Map<String, Integer> instanceIdMap;
    private Map<Integer, BluetoothGattCharacteristic> characterInstanceIdMap;
    private Map<String, BluetoothGattCharacteristic> characteristicUUIDMap;

    private boolean hasInvite;

    public boolean isHasInvite() {
        return hasInvite;
    }

    public void setHasInvite(boolean hasInvite) {
        this.hasInvite = hasInvite;
    }

    public BleDevice(BluetoothDevice device) {
        mDevice = device;
    }

    public BleDevice(BluetoothDevice device, int rssi, byte[] scanRecord, long timestampNanos) {
        mDevice = device;
        mScanRecord = scanRecord;
        mRssi = rssi;
        mTimestampNanos = timestampNanos;
        setKey(connectKey++);
    }

    public static final int SERVICE_DEFAULT_COUNT = 5;
    public static final int CHARACTERISTIC_DEFAULT_COUNT = SERVICE_DEFAULT_COUNT * 10;

    private void createInstanceIdMap() {
        characterInstanceIdMap = new HashMap<Integer, BluetoothGattCharacteristic>(CHARACTERISTIC_DEFAULT_COUNT);
        instanceIdMap = new HashMap<>(CHARACTERISTIC_DEFAULT_COUNT);
        BluetoothGatt bluetoothGatt = BleManager.getInstance().getBluetoothGatt(this);
        if (bluetoothGatt != null) {
            for (BluetoothGattService service : bluetoothGatt.getServices()) {
                if (service == null) {
                    continue;
                }
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    Log.e(LogTag, "character_uuid "+characteristic.getUuid().toString()+ " character_instanceID "+characteristic.getInstanceId() + "");
                    if (characteristic.getUuid().toString().equals("00002afe-0000-1000-8000-00805f9b34fb") && characteristic.getInstanceId() == 0) {
                        characterInstanceIdMap.put(14, characteristic);
                    } else if (characteristic.getUuid().toString().equals("00002aff-0000-1000-8000-00805f9b34fb") && characteristic.getInstanceId() == 0) {
                        characterInstanceIdMap.put(16, characteristic);
                    } else if (characteristic.getUuid().toString().equals("00002b00-0000-1000-8000-00805f9b34fb") && characteristic.getInstanceId() == 0) {
                        characterInstanceIdMap.put(19, characteristic);
                    } else if (characteristic.getUuid().toString().equals("00002b01-0000-1000-8000-00805f9b34fb") && characteristic.getInstanceId() == 0) {
                        characterInstanceIdMap.put(22, characteristic);
                    } else if (characteristic.getUuid().toString().equals("00002b02-0000-1000-8000-00805f9b34fb") && characteristic.getInstanceId() == 0) {
                        characterInstanceIdMap.put(25, characteristic);
                    } else {
                        characterInstanceIdMap.put(characteristic.getInstanceId(), characteristic);
                        instanceIdMap.put(characteristic.getUuid().toString(), characteristic.getInstanceId());
                    }

                }
            }
        }
    }


    public int getInstanceIdByUuid(String uuid) {
        if (instanceIdMap == null) {
            createInstanceIdMap();
        }
        if (instanceIdMap.get(uuid) == null) {
            return 0;
        }
        return instanceIdMap.get(uuid);
    }

    public int getInstanceIdByServiceUUID(String uuid) {
        BluetoothGatt bluetoothGatt = BleManager.getInstance().getBluetoothGatt(this);
        for (BluetoothGattService service : bluetoothGatt.getServices()) {
            String uuidStr = HexUtils.deleteCharFromString(service.getUuid().toString(), "-");
            String srcUUID = HexUtils.deleteCharFromString(uuid, "-");
            if (srcUUID.equals(uuidStr)) {
                return service.getInstanceId();
            }
        }
        return 0;
    }

    public BluetoothGattCharacteristic getCharacteristicsByInstanceId(int instanceId) {
        if (characterInstanceIdMap == null) {
            createInstanceIdMap();
        }
        return characterInstanceIdMap.get(instanceId);
    }

    public BluetoothGattCharacteristic getCharacteristicsByUUID(byte[] uuid) {
        if (characteristicUUIDMap == null) {
            createUUIDMap();
        }
        String uuidStr = HexUtils.formatHexString(uuid);
        return characteristicUUIDMap.get(uuidStr);
    }

    private void createUUIDMap() {
        if (characteristicUUIDMap == null) {
            characteristicUUIDMap = new HashMap<>();
        }
        BluetoothGatt bluetoothGatt = BleManager.getInstance().getBluetoothGatt(this);
        if (bluetoothGatt != null) {
            for (BluetoothGattService service : bluetoothGatt.getServices()) {
                if (service == null) {
                    continue;
                }
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    Log.e(LogTag," character_uuid "+characteristic.getUuid().toString()+" instanceId "+characteristic.getInstanceId());
                    characteristicUUIDMap.put(characteristic.getUuid().toString().replace("-", ""), characteristic);
                }
            }
        }
    }

    protected BleDevice(Parcel in) {
        mDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
        mScanRecord = in.createByteArray();
        mRssi = in.readInt();
        mTimestampNanos = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mDevice, flags);
        dest.writeByteArray(mScanRecord);
        dest.writeInt(mRssi);
        dest.writeLong(mTimestampNanos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BleDevice> CREATOR = new Creator<BleDevice>() {
        @Override
        public BleDevice createFromParcel(Parcel in) {
            return new BleDevice(in);
        }

        @Override
        public BleDevice[] newArray(int size) {
            return new BleDevice[size];
        }
    };

    public String getName() {
        if (mDevice != null)
            return mDevice.getName();
        return null;
    }

    public String getMac() {
        if (mDevice != null)
            return mDevice.getAddress();
        return null;
    }

    public byte[] getHexMac() {
        if (mDevice != null) {
            String address = mDevice.getAddress();
            String newAddress = deleteCharString1(address, ':');
            byte[] bytes = HexUtils.hexStringToBytes(newAddress);
            return bytes;
        }
        return null;
    }

    public String deleteCharString1(String sourceString, char chElemData) {
        String deleteString = "";
        int iIndex = 0;
        for (int i = 0; i < sourceString.length(); i++) {
            if (sourceString.charAt(i) == chElemData) {
                if (i > 0) {
                    deleteString += sourceString.substring(iIndex, i);
                }
                iIndex = i + 1;
            }
        }
        if (iIndex <= sourceString.length()) {
            deleteString += sourceString.substring(iIndex, sourceString.length());
        }
        return deleteString;
    }

    public static int connectKey = 10;

    public int key;

    public void setKey(int key) {
        this.key = key % 128;
    }

    public Integer getKey() {
        return key;
    }

    public String getUUid() {
        if (mDevice != null) {
            return mDevice.getUuids().toString();
        }
        return new String();
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public void setDevice(BluetoothDevice device) {
        this.mDevice = device;
    }

    public byte[] getScanRecord() {
        return mScanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.mScanRecord = scanRecord;
    }

    public int getRssi() {
        return mRssi;
    }

    public void setRssi(int rssi) {
        this.mRssi = rssi;
    }

    public long getTimestampNanos() {
        return mTimestampNanos;
    }

    public void setTimestampNanos(long timestampNanos) {
        this.mTimestampNanos = timestampNanos;
    }

    private String notifyAttUUid = "";

    public void setNotifyAttInstance(String notifyAttUUid) {
        this.notifyAttUUid = notifyAttUUid;
    }

    public String getNotifyAttInstance() {
        return notifyAttUUid;
    }

    private Set<String> enabledNotifyCharacterists;

    public void addNotifiedUUID(String uuid) {
        if (enabledNotifyCharacterists == null) {
            enabledNotifyCharacterists = new HashSet<>();
        }
        enabledNotifyCharacterists.add(uuid);
    }

    public boolean isCharacterNotified(String notifyAttUUid) {
        if (enabledNotifyCharacterists == null) {
            enabledNotifyCharacterists = new HashSet<>();
        }
        if (enabledNotifyCharacterists.contains(notifyAttUUid)) {
            return true;
        } else return false;
    }


}
