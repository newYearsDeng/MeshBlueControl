package com.jmesh.blebase.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.jmesh.blebase.base.BleManager;
import com.jmesh.blebase.callback.BleMtuChangedCallback;
import com.jmesh.blebase.callback.BleNotifyCallback;
import com.jmesh.blebase.callback.BleWriteCallback;
import com.jmesh.blebase.exception.BleException;
import com.jmesh.blebase.state.BleDevice;
import com.jmesh.blebase.utils.HexUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/31.
 * 由于Gatt数据发送，notify都必须一个一个执行，因此只能在回调函数里面处理
 */

public class GattHandler {
    public static final String LogTag = GattHandler.class.getSimpleName();
    public static GattHandler gattHandler;

    public static GattHandler getInstance() {
        if (gattHandler == null) {
            gattHandler = new GattHandler();
        }
        return gattHandler;
    }

    List<GattCmd> cmds = new ArrayList<>();

    private GattHandler() {

    }

    private void addCmd(GattCmd cmd) {
        cmds.add(cmd);
        if (cmds.size() < 2) {
            nextCmd();
        }
    }

    private void nextCmd() {
        if (cmds.size() < 1) {
            return;
        }
        GattCmd gattCmd = cmds.get(0);
        Log.e("nextCmd", gattCmd.cmdType + "");
        switch (gattCmd.cmdType) {
            case kCmdTypeEnableNotify:
                onEnableNotify(gattCmd.deviceKey, gattCmd.characterInstanceId);
                break;
            case kCmdTypeSetMtu:
                onSetMtu(gattCmd.deviceKey, gattCmd.mtuCount);
                break;
            case kCmdTypeWrite:
                onWrite(gattCmd.deviceKey, gattCmd.characterInstanceId, gattCmd.data);
                break;
            case kCmdTypeEnableNotifyByUUID:
                onEnableNotifyByUUID(gattCmd.deviceKey, gattCmd.uuid);
                break;
            case kCmdTypeWriteByUUID:
                onWrite(gattCmd.deviceKey, gattCmd.uuid, gattCmd.data);
                break;
        }
    }

    private void onEnableNotify(int deviceKey, int characterInstanceId) {
        Log.e(LogTag, "onEnableNotify" + "deviceKey" + deviceKey + "characterInstanceId" + characterInstanceId);
        notifyCallback = new MBleNotifyCallback(deviceKey, characterInstanceId);
        BleDevice bleDevice = BleManager.getInstance().getConnectedDeviceByKey(deviceKey);
        BluetoothGattCharacteristic characteristic = bleDevice.getCharacteristicsByInstanceId(characterInstanceId);
        BleManager.getInstance().notify(bleDevice, characteristic.getService().getUuid().toString(), characteristic.getUuid().toString(), notifyCallback);
    }

    private void onEnableNotifyByUUID(int deviceKey, byte[] uuid) {
        notifyCallback2 = new MBleNotifyCallback2(deviceKey, uuid);
        Log.e(LogTag, " onEnableNotifyByUUID" + " deviceKey" + deviceKey + " uuid" + HexUtils.formatHexString(uuid));
        BleDevice bleDevice = BleManager.getInstance().getConnectedDeviceByKey(deviceKey);
        BluetoothGattCharacteristic characteristic = bleDevice.getCharacteristicsByUUID(uuid);
        BleManager.getInstance().notify(bleDevice, characteristic.getService().getUuid().toString(), characteristic.getUuid().toString(), notifyCallback2);
    }

    private GattCmd getCurrentCmd() {
        if (cmds.size() < 1) {
            return null;
        }
        return cmds.get(0);
    }

    public void write(int deviceKey, int characterInstanceId, byte[] data) {
        GattCmd gattCmd = new GattCmd();
        gattCmd.setDeviceKey(deviceKey);
        gattCmd.setCharacterInstanceId(characterInstanceId);
        gattCmd.setData(data);
        gattCmd.setCmdType(kCmdTypeWrite);
        addCmd(gattCmd);
    }

    public void write(int deviceKey, byte[] uuid, byte[] data) {
        GattCmd gattCmd = new GattCmd();
        gattCmd.setDeviceKey(deviceKey);
        gattCmd.setUUID(uuid);
        gattCmd.setCmdType(kCmdTypeWriteByUUID);
        gattCmd.setData(data);
        addCmd(gattCmd);
    }

    public void enableNotify(int deviceKey, int characterInstanceId) {
        GattCmd gattCmd = new GattCmd();
        gattCmd.setDeviceKey(deviceKey);
        gattCmd.setCharacterInstanceId(characterInstanceId);
        gattCmd.setCmdType(kCmdTypeEnableNotify);
        addCmd(gattCmd);
    }

    public void enableNotify(int deviceKey, byte[] uuid) {
        GattCmd gattCmd = new GattCmd();
        gattCmd.setDeviceKey(deviceKey);
        gattCmd.setCmdType(kCmdTypeEnableNotifyByUUID);
        gattCmd.setUUID(uuid);
        addCmd(gattCmd);
    }

    public void setMtu(int devicekey, int mtuCount) {
        GattCmd gattCmd = new GattCmd();
        gattCmd.setDeviceKey(devicekey);
        gattCmd.setMtuCount(mtuCount);
        gattCmd.setCmdType(kCmdTypeSetMtu);
        addCmd(gattCmd);
    }

    byte[] continueByte;

    private void onWrite(int deviceKey, int characterInstanceId, byte[] data) {
        Log.e(LogTag, "onGattWrite" + " deviceKey" + deviceKey + " characterInstenceId" + characterInstanceId + " data" + HexUtils.formatHexString(data));
        BleDevice bleDevice = BleManager.getInstance().getConnectedDeviceByKey(deviceKey);
        BluetoothGattCharacteristic characteristic = bleDevice.getCharacteristicsByInstanceId(characterInstanceId);
        BluetoothGattService service = characteristic.getService();
        BleManager.getInstance().write(bleDevice, service.getUuid().toString(), characteristic.getUuid().toString(), data, bleWriteCallback);
    }

    private void onWrite(int deviceKey, byte[] uuid, byte[] data) {
        Log.e(LogTag, "onGattWriteByUUID" + " deviceKey" + deviceKey + " uuid" + HexUtils.formatHexString(uuid) + " data" + HexUtils.formatHexString(data));
        BleDevice bleDevice = BleManager.getInstance().getConnectedDeviceByKey(deviceKey);
        BluetoothGattCharacteristic characteristic = bleDevice.getCharacteristicsByUUID(uuid);
        BluetoothGattService service = characteristic.getService();
        BleManager.getInstance().write(bleDevice, service.getUuid().toString(), characteristic.getUuid().toString(), data, bleWriteCallback);
    }


    public static final int kTypeSingle = 100;
    public static final int kTypeFirst = 101;
    public static final int kTypeLast = 102;
    public static final int kTypeContinue = 103;

    public static int getType(byte b) {
        Log.e(LogTag, "byte" + HexUtils.formatHexString(new byte[]{b}));
        if ((b & 0xC0) == 0x00) {
            return kTypeSingle;
        } else if ((b & 0xC0) == 0xC0) {
            return kTypeLast;
        } else if ((byte) (b | 0x40) == b) {
            return kTypeFirst;
        } else if ((byte) (b | 0x80) == b) {
            return kTypeContinue;
        }
        return kTypeSingle;
    }


    BleWriteCallback bleWriteCallback = new BleWriteCallback() {
        @Override
        public void onWriteSuccess(int current, int total, byte[] justWrite) {
            Log.e(LogTag, "writeSuccess " + justWrite != null ? HexUtils.formatHexString(justWrite) : HexUtils.formatHexString(new byte[1]));
            if (cmds.size() > 0) {
                boolean isCurrentByte = true;
                for (int i = 0; i < justWrite.length; i++) {
                    if (justWrite[i] != cmds.get(0).data[i]) {
                        isCurrentByte = false;
                        break;
                    }
                }
                if (isCurrentByte) {
                    cmds.remove(0);
                    nextCmd();
                }
            }
        }

        @Override
        public void onWriteFailure(BleException exception) {
            Log.e(LogTag, " onWriteFailure " + exception.toString());
        }
    };


    MBleNotifyCallback notifyCallback;
    MBleNotifyCallback2 notifyCallback2;

    public void setNotifyInstanceId(int notifyInstanceId) {
        if (notifyCallback != null) {
            notifyCallback.setNotifyInstance(notifyInstanceId);
        }
    }

    class MBleNotifyCallback2 extends BleNotifyCallback {

        int deviceKey;
        byte[] notifyUUID;

        public MBleNotifyCallback2(int deviceKey, byte[] notifyUUID) {
            this.deviceKey = deviceKey;
            this.notifyUUID = notifyUUID;
        }

        @Override
        public void onNotifySuccess(int deviceKey, int instantceId, String uuid) {
            Log.e(LogTag, " onNotifySuccess");
            if (cmds.size() > 0) {
                cmds.remove(0);
            }
            nextCmd();
            BleDevice bleDevice = BleManager.getInstance().getConnectedDeviceByKey(deviceKey);
            bleDevice.addNotifiedUUID(uuid);
        }

        @Override
        public void onNotifyFailure(BleException exception) {

        }

        @Override
        public void onCharacteristicChanged(byte[] data, int notifyInstanceId, String uuid) {
            Log.e(LogTag, "onCharacteristicChanged " + " uuid " + uuid);
            if (onNotifyCallback != null) {
                onNotifyCallback.onNotifyCallback(deviceKey, 0, uuid, data);
            }
        }
    }


    class MBleNotifyCallback extends BleNotifyCallback {

        int notifyInstance;
        int deviceKey;

        public void setNotifyInstance(int notifyInstance) {
            this.notifyInstance = notifyInstance;
        }

        public MBleNotifyCallback(int deviceKey, int notifyInstance) {
            this.notifyInstance = notifyInstance;
            this.deviceKey = deviceKey;
        }


        @Override
        public void onNotifySuccess(int deviceKey, int instanceId, String uuid) {
            Log.e(LogTag, "onNotifySuccess");
            if (cmds.size() > 0) {
                cmds.remove(0);
            }
            nextCmd();
            BleDevice bleDevice = BleManager.getInstance().getConnectedDeviceByKey(deviceKey);
            bleDevice.setNotifyAttInstance(getKey());
        }

        @Override
        public void onNotifyFailure(BleException exception) {

        }

        @Override
        public void onCharacteristicChanged(byte[] data, int instance, String uuid) {
            if (onNotifyCallback != null) {
                onNotifyCallback.onNotifyCallback(deviceKey, instance, null, data);
            }
        }
    }


    private void onSetMtu(int devicekey, int mtuCount) {
        Log.e(LogTag, "mtuCount: " + mtuCount);
        BleDevice bleDevice = BleManager.getInstance().getConnectedDeviceByKey(devicekey);
        BleManager.getInstance().setMtu(bleDevice, mtuCount, bleMtuChangedCallback);
    }

    BleMtuChangedCallback bleMtuChangedCallback = new BleMtuChangedCallback() {
        @Override
        public void onSetMTUFailure(BleException exception) {
            Log.e(LogTag, "onSetMTUFailure " + exception.toString());
        }

        @Override
        public void onMtuChanged(int mtu) {
            Log.e(LogTag, "onSetMtuSuccess " + " mtu " + mtu);
            if (cmds.size() > 0) {
                cmds.remove(0);
            }
            nextCmd();
        }
    };


    public static final int kCmdTypeEnableNotify = 100;
    public static final int kCmdTypeWrite = 101;
    public static final int kCmdTypeSetMtu = 102;
    public static final int kCmdTypeEnableNotifyByUUID = 103;
    public static final int kCmdTypeWriteByUUID = 104;


    class GattCmd {
        int deviceKey;
        int characterInstanceId;
        byte[] data;
        int cmdType;
        int mtuCount;
        byte[] uuid;

        public GattCmd() {

        }

        public GattCmd(int cmdType, int deviceKey, int characterInstanceId, byte[] data, int mtuCount) {
            this.cmdType = cmdType;
            this.deviceKey = deviceKey;
            this.characterInstanceId = characterInstanceId;
            this.data = data;
            this.mtuCount = mtuCount;
        }

        public void setDeviceKey(int deviceKey) {
            this.deviceKey = deviceKey;
        }

        public void setCharacterInstanceId(int characterInstanceId) {
            this.characterInstanceId = characterInstanceId;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        public void setCmdType(int cmdType) {
            this.cmdType = cmdType;
        }

        public void setMtuCount(int mtuCount) {
            this.mtuCount = mtuCount;
        }

        public void setUUID(byte[] uuid) {
            this.uuid = uuid;
        }

    }

    public interface OnNotifyCallback {
        void onNotifyCallback(int devicekey, int notifyAttInstance, String uuid, byte[] data);
    }

    private OnNotifyCallback onNotifyCallback;

    public void setOnNotifyCallback(OnNotifyCallback onNotifyCallback) {
        this.onNotifyCallback = onNotifyCallback;
    }


}