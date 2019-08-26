package com.jmesh.blebase.advertiser;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jmesh.blebase.base.BleManager;
import com.jmesh.blebase.utils.HexUtils;

/**
 * Created by BC119 on 2018/6/26.
 */

public class BleAdvertiser {
    /**
     * 1、TYPE = 0x01：标识设备LE物理连接的功能，占一个字节，各bit为1时定义如下：
     * bit 0: LE有限发现模式
     * bit 1: LE普通发现模式
     * bit 2: 不支持BR/EDR
     * bit 3: 对Same Device Capable(Controller)同时支持BLE和BR/EDR
     * bit 4: 对Same Device Capable(Host)同时支持BLE和BR/EDR
     * bit 5..7: 预留
     * 2、TYPE = 0x02：非完整的16 bit UUID列表
     * 3、TYPE = 0x03：完整的16 bit UUID列表
     * 4、TYPE = 0x04：非完整的32 bit UUID列表
     * 5、TYPE = 0x05：完整的32 bit UUID列表
     * 6、TYPE = 0x06：非完整的128 bit UUID列表
     * 7、TYPE = 0x07：完整的128 bit UUID列表
     * 8、TYPE = 0x08：设备简称
     * 9、TYPE = 0x09：设备全名
     * 10、TYPE = 0x0A：表示设备发送广播包的信号强度
     * 11、TYPE = 0x0D：设备类别
     * 12、TYPE = 0x0E：设备配对的Hash值
     * 13、TYPE = 0x0F：设备配对的随机值
     * 14、TYPE = 0x10：TK安全管理（Security Manager TK Value）
     * 15、TYPE = 0x11：带外安全管理（Security Manager Out of Band），各bit定义如下：
     * <p>
     * bit 0: OOB Flag，0-表示没有OOB数据，1-表示有
     * bit 1: 支持LE
     * bit 2: 对Same Device Capable(Host)同时支持BLE和BR/EDR
     * bit 3: 地址类型，0-表示公开地址，1-表示随机地址
     * <p>
     * 16、TYPE = 0x12：外设（Slave）连接间隔范围，数据中定义了Slave最大和最小连接间隔，数据包含4个字节：前两字节定义最小连接间隔，取值范围：0x0006 ~ 0x0C80，而0xFFFF表示未定义；后两字节，定义最大连接间隔，取值范围同上，不过需要保证最大连接间隔大于或者等于最小连接间隔。
     * 17、TYPE = 0x14：服务搜寻16 bit UUID列表
     * 18、TYPE = 0x15：服务搜寻128 bit UUID列表
     * 19、TYPE = 0x16：16 bit UUID Service，前两个字节是UUID，后面是Service的数据
     * 20、TYPE = 0x17：公开目标地址，表示希望这个广播包被指定的目标设备处理，此设备绑定了公开地址
     * 21、TYPE = 0x18：随机目标地址，表示希望这个广播包被指定的目标设备处理，此设备绑定了随机地址
     * 22、TYPE = 0x19：表示设备的外观
     * 23、TYPE = 0x1A：广播区间
     * 24、TYPE = 0x1B：LE设备地址
     * 25、TYPE = 0x1C：LE设备角色
     * 26、TYPE = 0x1D：256位设备配对的Hash值
     * 27、TYPE = 0x1E：256位设备配对的随机值
     * 28、TYPE = 0x20：32 bit UUID Service，前4个字节是UUID，后面是Service的数据
     * 29、TYPE = 0x21：128 bit UUID Service，前16个字节是UUID，后面是Service的数据
     * 30、TYPE = 0x3D：3D信息数据
     * 31、TYPE = 0xFF：厂商自定义数据，厂商自定义的数据中，前两个字节表示厂商ID，剩下的是厂商自己按照需求添加，里面的数据内容自己定义。
     */
    public static final byte BLE_GAP_AD_TYPE_FLAGS = 0x01; //< Flags for discoverability.
    public static final byte BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_MORE_AVAILABLE = 0x02;//< Partial list of 16 bit service UUIDs.
    public static final byte BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_COMPLETE = 0x03; //< Complete list of 16 bit service UUIDs.
    public static final byte BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_MORE_AVAILABLE = 0x04; //< Partial list of 32 bit service UUIDs.
    public static final byte BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_COMPLETE = 0x05; //< Complete list of 32 bit service UUIDs.
    public static final byte BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_MORE_AVAILABLE = 0x06; //< Partial list of 128 bit service UUIDs.
    public static final byte BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_COMPLETE = 0x07; //< Complete list of 128 bit service UUIDs.
    public static final byte BLE_GAP_AD_TYPE_SHORT_LOCAL_NAME = 0x08; //< Short local device name.
    public static final byte BLE_GAP_AD_TYPE_COMPLETE_LOCAL_NAME = 0x09; //< Complete local device name.
    public static final byte BLE_GAP_AD_TYPE_TX_POWER_LEVEL = 0x0A; //< Transmit power level.
    public static final byte BLE_GAP_AD_TYPE_CLASS_OF_DEVICE = 0x0D; //< Class of device.
    public static final byte BLE_GAP_AD_TYPE_SIMPLE_PAIRING_HASH_C = 0x0E; //< Simple Pairing Hash C.
    public static final byte BLE_GAP_AD_TYPE_SIMPLE_PAIRING_RANDOMIZER_R = 0x0F; //< Simple Pairing Randomizer R.
    public static final byte BLE_GAP_AD_TYPE_SECURITY_MANAGER_TK_VALUE = 0x10; //< Security Manager TK Value.
    public static final byte BLE_GAP_AD_TYPE_SECURITY_MANAGER_OOB_FLAGS = 0x11; //< Security Manager Out Of Band Flags.
    public static final byte BLE_GAP_AD_TYPE_SLAVE_CONNECTION_INTERVAL_RANGE = 0x12; //< Slave Connection Interval Range.
    public static final byte BLE_GAP_AD_TYPE_SOLICITED_SERVICE_UUIDS_16BIT = 0x14; //< List of 16-bit Service Solicitation UUIDs.
    public static final byte BLE_GAP_AD_TYPE_SOLICITED_SERVICE_UUIDS_128BIT = 0x15; //< List of 128-bit Service Solicitation UUIDs.
    public static final byte BLE_GAP_AD_TYPE_SERVICE_DATA = 0x16; //< Service Data - 16-bit UUID.
    public static final byte BLE_GAP_AD_TYPE_PUBLIC_TARGET_ADDRESS = 0x17; //< Public Target Address.
    public static final byte BLE_GAP_AD_TYPE_RANDOM_TARGET_ADDRESS = 0x18; //< Random Target Address.
    public static final byte BLE_GAP_AD_TYPE_APPEARANCE = 0x19; //< Appearance.
    public static final byte BLE_GAP_AD_TYPE_ADVERTISING_INTERVAL = 0x1A; //< Advertising Interval.
    public static final byte BLE_GAP_AD_TYPE_LE_BLUETOOTH_DEVICE_ADDRESS = 0x1B; //< LE Bluetooth Device Address.
    public static final byte BLE_GAP_AD_TYPE_LE_ROLE = 0x1C; //< LE Role. #public static final byte BLE_GAP_AD_TYPE_SIMPLE_PAIRING_HASH_C256            0x1D //< Simple Pairing Hash C-256.
    public static final byte BLE_GAP_AD_TYPE_SIMPLE_PAIRING_RANDOMIZER_R256 = 0x1E; //< Simple Pairing Randomizer R-256.
    public static final byte BLE_GAP_AD_TYPE_SERVICE_DATA_32BIT_UUID = 0x20; //< Service Data - 32-bit UUID.
    public static final byte BLE_GAP_AD_TYPE_SERVICE_DATA_128BIT_UUID = 0x21; //< Service Data - 128-bit UUID.
    public static final byte BLE_GAP_AD_TYPE_3D_INFORMATION_DATA = 0x3D; //< 3D Information Data.

    public static String LogTag = BleAdvertiser.class.getSimpleName();

    private static BleAdvertiser sBleAdvertiser;

    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    private BleAdvertiser() {

    }

    public static BleAdvertiser getInstance() {
        if (sBleAdvertiser == null) {
            sBleAdvertiser = new BleAdvertiser();
        }
        return sBleAdvertiser;
    }

    public AdvertiseSettings setAdvertiser(int adMode, boolean connectable, int timeOut, int txPowerLevel) {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(adMode);
        builder.setConnectable(connectable);
        builder.setTimeout(timeOut);
        builder.setTxPowerLevel(txPowerLevel);
        return builder.build();
    }

    public AdvertiseSettings getDefaultAdSetting(boolean connectable) {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        builder.setConnectable(connectable);
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        return builder.build();
    }

    public AdvertiseSettings getServerSetting() {
        Log.e(LogTag,"getServerSetting");
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        builder.setConnectable(true);
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        return builder.build();
    }


    public AdvertiseSettings getDefaultAdSetting() {
        return getDefaultAdSetting(false);
    }


    public boolean isAllowAdvertise() {
        BluetoothLeAdvertiser advertiser = BleManager.getInstance().getBluetoothAdapter().getBluetoothLeAdvertiser();
        return advertiser != null;
    }

    private void startAdvertiseServer(byte[] data) {
        BluetoothLeAdvertiser advertiser = getAdvertiser();
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        int mfId = convertId(data);
        byte[] restData = handleData(data);
        Log.e(LogTag, " mfid "+ Integer.toString(mfId)+" mfid_hex "+ " restdata "+ HexUtils.formatHexString(restData));
        builder.addManufacturerData(mfId, restData);
        builder.setIncludeDeviceName(true);
        if (advertiser != null) {
            advertiser.startAdvertising(getServerSetting(), builder.build(), callback);
        }
    }


    private void startAdvertise(boolean connectable, byte[] data) {
        BluetoothLeAdvertiser advertiser = getAdvertiser();
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        int mfId = convertId(data);
        byte[] restData = handleData(data);
        Log.e(LogTag, "mfid "+ Integer.toString(mfId)+ " mfid_hex "+ " restdata "+HexUtils.formatHexString(restData));
        builder.addManufacturerData(mfId, restData);
        if (advertiser != null) {
            countAgain();
            advertiser.startAdvertising(getDefaultAdSetting(connectable), builder.build(), callback);
        }
    }

    private int convertId(byte[] data) {
        Log.e(LogTag,HexUtils.formatHexString(data));
        int first = data[1] & 0xFF;
        Log.e(LogTag, "first "+Integer.toString(first));
        int second = data[0] & 0xFF;
        Log.e(LogTag, "second "+Integer.toString(second));
        int newFist = first << 4 << 4;
        Log.e(LogTag, "newFist "+ Integer.toString(newFist));
        return second + newFist;
    }

    private byte[] handleData(byte[] data) {
//        return HexUtils.hexStringToBytes("12345");
        byte[] newData = new byte[data.length - 2];
        for (int i = 0; i < newData.length; i++) {
            newData[i] = data[i + 2];
        }
        return newData;
    }

    static final AdvertiseCallback callback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.e(LogTag, "onStartSuccess");
            super.onStartSuccess(settingsInEffect);
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.e(LogTag, "onStartFailure"+errorCode + "");
            super.onStartFailure(errorCode);
        }
    };

    public void advertise(boolean connectable, byte[] data) {
        if (isAllowAdvertise()) {
            Log.e(LogTag, "allow advertise");
            startAdvertise(connectable, data);
        } else {
            Log.e(LogTag, "is not allow advertising!");
        }
    }

    public void advertiseServer(byte[] data) {
        if (isAllowAdvertise()) {
            Log.e(LogTag, "allow advertise");
            startAdvertiseServer(data);
        } else {
            Log.e(LogTag, "is not allow advertising!");
        }
    }

    public void stopAdvertise() {
        if (callback == null) {
            return;
        }
        try {
            getAdvertiser().stopAdvertising(callback);
            Log.e(LogTag, "stop");
        } catch (Throwable t) {

        }
    }

    private BluetoothLeAdvertiser getAdvertiser() {
        if (mBluetoothLeAdvertiser == null) {
            mBluetoothLeAdvertiser = BleManager.getInstance().getBluetoothAdapter().getBluetoothLeAdvertiser();
        }
        return mBluetoothLeAdvertiser;
    }

    private void countAgain() {
        if (thread != null) {
            thread.interrupt();
        }
        thread = new Thread(new MyTask());
        thread.start();
    }

    Thread thread;

    class MyTask implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(60*60*1000);
                handler.sendEmptyMessage(kStopAdvertise);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public static final int kStopAdvertise = 100;

    public static class AdvertiseHandle extends Handler {
        @Override
        public void handleMessage(Message message) {
            if (message.what == kStopAdvertise) {
                BleAdvertiser.getInstance().getAdvertiser().stopAdvertising(callback);
                Log.e(LogTag,"advertiser auto stop");
            }
        }
    }

    public static AdvertiseHandle handler = new AdvertiseHandle();
}
