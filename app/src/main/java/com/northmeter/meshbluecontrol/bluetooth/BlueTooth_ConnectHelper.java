package com.northmeter.meshbluecontrol.bluetooth;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.northmeter.meshbluecontrol.I.IShowSMainMessage;
import com.northmeter.meshbluecontrol.base.MyApplication;
import com.northmeter.meshbluecontrol.bean.EvenBusBean;
import com.northmeter.meshbluecontrol.bluetooth.bluetooth.blueActivity.Blue_MainActivity;
import com.northmeter.meshbluecontrol.bluetooth.bluetooth.blueActivity.DeviceListActivity;
import com.northmeter.meshbluecontrol.bluetooth.bluetooth.tools.BluetoothConnectionClient;
import com.northmeter.meshbluecontrol.bluetooth.bluetooth.tools.GattCode;
import com.northmeter.meshbluecontrol.enumBean.EvenBusEnum;
import com.northmeter.meshbluecontrol.utils.Udp_Help;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by dyd on 2019/4/2.
 */

public class BlueTooth_ConnectHelper {
    private final int FOUND_DEVICE = 0X01;
    private final int DISCONNECTED = 0X02;
    private final int FOUND_SERVICE = 0X03;
    private final int WRITE_SUCCESS = 0X04;
    private final int WRITE_FAILED = 0X05;
    private final int RECONNECT = 0x06;
    private final int RECEIVE = 0x07;
    private final int SEND = 0x08;
    private final int CONNECTED = 0X09;
    private final int BLUEM_ESSAGE = 0x0a;

    private String TAG = getClass().getSimpleName();
    private String receive_msg = "";

    /**蓝牙对象*/
    private BluetoothConnectionClient mConnectionClient;

    private static BlueTooth_ConnectHelper uniqueInstance=null;

    private static String mConnectedDeviceName;

    private static int commState;

    /**bt蓝牙是否连接成功*/
    private static boolean booleanConnected = false;

    public BlueTooth_ConnectHelper() {
        mConnectionClient = null;
        booleanConnected = false;
        mConnectedDeviceName = "No Connected";
    }

    public static BlueTooth_ConnectHelper getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new BlueTooth_ConnectHelper();
        }
        return uniqueInstance;
    }


    public void blueToothConnect(BluetoothDevice blueDevice){
        if (mConnectionClient != null) {
            mConnectionClient.disconnect();
        }
        mConnectionClient = new BluetoothConnectionClient(blueDevice, MyApplication.getContext(), mGattCallback);
        mConnectionClient.connect();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECTED://连接成功
                    Log.w(TAG, "连接成功");
                    BlueTooth_ConnectHelper.getInstance().setBooleanConnected(true);
                    sendEventBus(EvenBusEnum.EvenBus_BlueToothConnect.getEvenName(),"0");
                    break;
                case DISCONNECTED://断开连接
                    Log.w(TAG,"连接断开！");
                    BlueTooth_ConnectHelper.getInstance().setBooleanConnected(false);
                    sendEventBus(EvenBusEnum.EvenBus_BlueToothConnect.getEvenName(),"连接断开");
                    break;
                case RECONNECT:
                    if (mConnectionClient != null) {
                        mConnectionClient.connect();
                        sendEventBus(EvenBusEnum.EvenBus_BlueToothConnect.getEvenName(),"正在重连...");
                    }
                    break;
                case RECEIVE:
                    String data = (String) msg.obj;
                    break;
                case SEND:
                    break;
                case FOUND_DEVICE:
                    if (mConnectionClient != null) {
                        mConnectionClient.disconnect();
                    }
                    System.out.println("找到蓝牙并进行连接");
                    sendEventBus(EvenBusEnum.EvenBus_BlueToothConnect.getEvenName(),"正在连接...");
                    mConnectionClient = (BluetoothConnectionClient) msg.obj;
                    mConnectionClient.connect();
                    break;
                case BLUEM_ESSAGE:
                    String blueMsg = (String) msg.obj;
                    if (blueMsg.equals("success")) {
                        Log.w(TAG,  "设置成功");
                        return;
                    } else if (blueMsg.equals("fail")) {
                        Log.w(TAG, "操作失败");
                        return;
                    }
                    break;

            }
        }
    };

    @SuppressLint("NewApi")
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    try {
                        Thread.sleep(500);
                        gatt.discoverServices();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (newState == BluetoothGatt.STATE_DISCONNECTED || status == 133) {
                Log.w(TAG, "onConnectionStateChange: disconnected");
                mHandler.sendEmptyMessage(DISCONNECTED);
                mHandler.sendEmptyMessage(RECONNECT);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            BluetoothGattService service = gatt.getService(GattCode.FFF_SERVICE);
            System.out.println("Uuid: " + service.getUuid());

            mConnectionClient.addCharacteristic(3, service.getCharacteristic(GattCode.FFF_3));
            mConnectionClient.addCharacteristic(4, service.getCharacteristic(GattCode.FFF_4));

            Log.w(TAG, "onServicesDiscovered: ");
            mConnectionClient.setCharacteristicNotification(GattCode.DESCRIPTOR,
                    3, true);

            mHandler.sendEmptyMessage(FOUND_SERVICE);

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.w(TAG, "onCharacteristicWrite: status=" + status);
            if (status == 0) {
                Message msg = mHandler.obtainMessage(SEND);
                msg.obj = characteristic.getValue();
                mHandler.sendMessage(msg);
            } else {
                mHandler.sendEmptyMessage(WRITE_FAILED);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] value = characteristic.getValue();
            receive_msg = receive_msg + Udp_Help.bytesToHexString(value);
            Log.w(TAG, receive_msg);
            System.out.println("resultData:"+receive_msg.toUpperCase());
            handleBtBlueMessage(receive_msg.toUpperCase());
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.w(TAG, "onDescriptorWrite: status=" + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                setMTU(gatt,253);//修改mtu最大传输字节
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Log.w(TAG,"onMtuChanged="+mtu+",status="+status);
            if(status == BluetoothGatt.GATT_SUCCESS){
                mHandler.sendEmptyMessage(CONNECTED);
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean setMTU(BluetoothGatt gatt,int mtu){
        Log.w(TAG,"setMTU "+mtu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if(mtu>20){
                boolean ret = gatt.requestMtu(mtu);
                Log.w(TAG,"requestMTU "+mtu+" ret="+ret);
                return ret;
            }
        }
        return false;
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


    public BluetoothConnectionClient getConnectionClient() {
        return mConnectionClient;
    }

    public void setConnectionClient(BluetoothConnectionClient mConnectionClient) {
        this.mConnectionClient = mConnectionClient;
    }

    public static boolean isBooleanConnected() {
        return booleanConnected;
    }

    public static void setBooleanConnected(boolean booleanConnected) {
        BlueTooth_ConnectHelper.booleanConnected = booleanConnected;
    }

    public static String getmConnectedDeviceName() {
        return mConnectedDeviceName;
    }

    public static void setmConnectedDeviceName(String mConnectedDeviceName) {
        BlueTooth_ConnectHelper.mConnectedDeviceName = mConnectedDeviceName;
    }

    public static int getCommState() {
        return commState;
    }

    public static void setCommState(int commState) {
        BlueTooth_ConnectHelper.commState = commState;
    }
}
