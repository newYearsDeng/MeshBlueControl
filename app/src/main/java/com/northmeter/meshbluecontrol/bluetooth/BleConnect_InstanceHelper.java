package com.northmeter.meshbluecontrol.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.os.Handler;
import android.os.Message;

import com.jmesh.blebase.base.BleManager;
import com.jmesh.blebase.bluetooth.GattHandler;
import com.jmesh.blebase.callback.BleGattCallback;
import com.jmesh.blebase.exception.BleException;
import com.jmesh.blebase.state.BleDevice;
import com.jmesh.blebase.utils.JMeshLog;
import com.northmeter.meshbluecontrol.I.IShowSMainMessage;
import com.northmeter.meshbluecontrol.base.HandlerUtil;
import com.northmeter.meshbluecontrol.bean.EvenBusBean;
import com.northmeter.meshbluecontrol.enumBean.EvenBusEnum;
import com.northmeter.meshbluecontrol.utils.Udp_Help;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dyd on 2019/4/29.
 */

public class BleConnect_InstanceHelper implements GattHandler.OnNotifyCallback {
    public static final String SendCharacterist = "00002b0700001000800000805f9b34fb";
    public static final String NotifyCharacterist = "00002b0800001000800000805f9b34fb";

    private int sendInstance;
    private int notifyInstance;
    private String receive_msg="";
    private BluetoothGatt bluetoothGatt;
    public static Set<String> connectingDeviceList = new HashSet<>();
    String macStr;

    public void setMacStr(String macStr) {
        this.macStr = macStr;
    }

    public String getMacStr() {
        return macStr;
    }

    private static BleConnect_InstanceHelper bleConnectInstanceHelper;

    public static BleConnect_InstanceHelper getInstance() {
        if (bleConnectInstanceHelper == null) {
            bleConnectInstanceHelper = new BleConnect_InstanceHelper();
        }
        return bleConnectInstanceHelper;
    }


    public void cancelConnect() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
        }
    }

    public void connecedDevice(){
        final BleDevice bleDevice = BleManager.getInstance().getConnectedDeviceByMac(macStr);
        if (bleDevice != null) {
            if (bleGattCallback != null) {
                HandlerUtil.runOnUiThreadDelay(new Runnable() {
                    @Override
                    public void run() {
                        bleGattCallback.onConnectSuccess(bleDevice, null, 0);
                    }
                }, 500);
                return;
            }
        }
        connecting = true;
        if (connectingDeviceList.contains(macStr)) {
            return;
        } else {
            bluetoothGatt = BleManager.getInstance().connect(macStr, bleGattCallback, 5000/*ms*/);
            connectingDeviceList.add(macStr);
        }
        //BleManager.getInstance().connect("03:17:12:15:02:91",bleGattCallback);
    }

    public void send(String data) {
        BleDevice bleDevice = BleManager.getInstance().getConnectedDeviceByMac(macStr);
        if (bleDevice == null) {
            return;
        }
        GattHandler.getInstance().setOnNotifyCallback(this);
        if (!bleDevice.hasNotified(NotifyCharacterist)) {
            GattHandler.getInstance().setMtu(bleDevice.getKey(), 200);
            GattHandler.getInstance().enableNotifyByUUID(bleDevice.getKey(), NotifyCharacterist);
            bleDevice.setNotifyAttInstance(NotifyCharacterist);
        }
        GattHandler.getInstance().writeByUuid(bleDevice.getKey(), SendCharacterist, Udp_Help.strtoByteArray(data));

//        GattHandler.getInstance().setMtu(bleDevice.getKey(), 200);
//        notifyInstance = bleDevice.getInstanceIdByUuid(NotifyCharacterist);
//        sendInstance = bleDevice.getInstanceIdByUuid(SendCharacterist);
//        GattHandler.getInstance().setOnNotifyCallback(this);
//        GattHandler.getInstance().enableNotify(bleDevice.getKey(), notifyInstance);
//        bleDevice.getCharacteristicsByInstanceId(sendInstance);
//        GattHandler.getInstance().write(bleDevice.getKey(), sendInstance, Udp_Help.strtoByteArray(data));
    }


    @Override
    public void onNotifyCallback(int i, int i1, byte[] bytes, String uuid) {
        receive_msg = receive_msg+Udp_Help.bytesToHexString(bytes);
        System.out.println("======="+receive_msg.toUpperCase());
        handleBtBlueMessage(receive_msg.toUpperCase());
    }

    /**
     * 处理蓝牙接收到的数据 BD 00 000000000000 04 0000 01B5 01000000000000  cs 16
     * bd 00 11223344556681020001b59e16
     */
    private String handleBtBlueMessage(String data) {//0080FEFEFE6821000016200168940000BC16C000FCF8
        int state = data.indexOf("BD",0);
        int state_lenth = state + 26;//从BD到标识码的长度
        String ditle = data.substring(data.length() - 2, data.length());//检查最后的字节是否为16

        if (state >= 0 && data.length() > state_lenth && ditle.equals("16")) {
            //String stateNum = data.substring(state+28,state+36);//标识码
            String length_1 = data.substring(state + 18, state + 20);//长度 c3
            String length_2 = data.substring(state + 20, state + 22);//长度 00
            System.out.println(length_1 + "/" + length_2);
            int len = Integer.valueOf(length_2 + length_1, 16);//字符串内长度字节
            if (data.substring(state + 22, data.length() - 4).length() / 2 == len) {
                new GetBlueEntity(new IShowSMainMessage() {
                    @Override
                    public void showMainMsg(String message) {
                        sendEventBus(EvenBusEnum.EvenBus_OnRetuenMessage.getEvenName(),message);
                    }
                    @Override
                    public void showReturnCode(String code) {
                        sendEventBus(EvenBusEnum.EvenBus_OnRetuenMessage.getEvenName(),code);
                    }

                }).transmitBlueMsg(data.substring(state, data.length()));
                receive_msg = "";
            }
        }
        return null;
    }

    public void sendEventBus(String topic, String data) {
        EvenBusBean bean = new EvenBusBean();
        bean.setTopic(topic);
        bean.setData(data);
        EventBus.getDefault().post(bean);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EventConnectState.connectFailed:
                    sendEventBus(EvenBusEnum.EvenBus_BlueToothConnect.getEvenName(),"连接失败");
                    onConnectedFailed();
                    break;
                case EventConnectState.connecting:
                    break;
                case EventConnectState.connectSuccess:
                    sendEventBus(EvenBusEnum.EvenBus_BlueToothConnect.getEvenName(),"0");
                    JMeshLog.e("onEvent", "connect_success");
                    onConnectedSuccess();
                    break;
                case EventConnectState.disconnected:
                    sendEventBus(EvenBusEnum.EvenBus_BlueToothConnect.getEvenName(),"连接断开");
                    onDisConnected();
                    break;
            }
        }
    };

    public static class EventConnectState {
        public int state;
        public static final int connecting = 10;
        public static final int connectFailed = 11;
        public static final int connectSuccess = 12;
        public static final int disconnected = 14;

        public EventConnectState(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }
    }

    public BleGattCallback bleGattCallback = new BleGattCallback() {
        @Override
        public void onStartConnect() {
            mHandler.sendEmptyMessage(EventConnectState.connecting);
        }

        @Override
        public void onConnectFail(BleDevice bleDevice, BleException e) {
            mHandler.sendEmptyMessage(EventConnectState.connectFailed);
            connectingDeviceList.remove(bleDevice.getMac());
        }

        @Override
        public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
            JMeshLog.e("callback", "onconnectSuccess");
            mHandler.sendEmptyMessage(EventConnectState.connectSuccess);
            connectingDeviceList.remove(bleDevice.getMac());
        }

        @Override
        public void onDisConnected(boolean b, BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
            mHandler.sendEmptyMessage(EventConnectState.disconnected);
        }
    };


    private boolean connecting;

    public boolean isConnecting() {
        return connecting;
    }

    public void setConnecting(boolean connecting) {
        this.connecting = connecting;
    }

    private void onConnectedFailed() {
        setConnecting(true);
        if (isConnecting()) {
            connecedDevice();
        }
    }

    private void onConnectedSuccess() {
        setConnecting(false);
    }


    private void onDisConnected() {
        setConnecting(true);
        if (isConnecting()) {
            connecedDevice();
        }
    }

}
