package com.northmeter.meshbluecontrol.bluetooth.bluetooth.blueActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.Toast;

import com.northmeter.meshbluecontrol.R;
import com.northmeter.meshbluecontrol.base.BaseActivity;
import com.northmeter.meshbluecontrol.bean.EvenBusBean;
import com.northmeter.meshbluecontrol.bluetooth.BlueTooth_ConnectHelper;
import com.northmeter.meshbluecontrol.bluetooth.bluetooth.tools.BluetoothConnectionClient;
import com.northmeter.meshbluecontrol.bluetooth.bluetooth.tools.BluetoothScanClient;
import com.northmeter.meshbluecontrol.bluetooth.bluetooth.tools.GattCode;
import com.northmeter.meshbluecontrol.enumBean.EvenBusEnum;
import com.northmeter.meshbluecontrol.utils.Udp_Help;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Blue_MainActivity extends BaseActivity implements BluetoothAdapter.LeScanCallback {

    private final static int FIND_BLUETOOTH_CODE = 1;
    private static final int REQUEST_OPENBLUERESULT = 201;
    private static final int REQUEST_LOCATIONARESULT = 2010;
    @BindView(R.id.button_test)
    Button buttonTest;
    private String TAG = getClass().getSimpleName();
    private BluetoothConnectionClient mConnectionClient;
    private BluetoothScanClient mScanClient;

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
    private long exitTime;
    private String receive_msg = "";
    private String tableMac;//水表编号,mac

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        super.initData();
        init_view();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.button_test)
    public void onViewClicked() {
        if (!mScanClient.isBluetoothOpen()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_OPENBLUERESULT);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    checkLocationAndOpenCamer();
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Toast.makeText(Blue_MainActivity.this, "该功能需要您授权打开定位服务", Toast.LENGTH_LONG).show();
                    }
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATIONARESULT);
                }
            } else {
                startActivityForResult(new Intent(this, DeviceListActivity.class)
                        , DeviceListActivity.REQUEST_DEVICE);
            }
        }
    }

    private void init_view() {
        mScanClient = BluetoothScanClient.getInstance(this, this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(Blue_MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FIND_BLUETOOTH_CODE://低功耗蓝牙
                BluetoothConnectionClient c = new BluetoothConnectionClient(
                        (BluetoothDevice) data.getParcelableExtra(DeviceListActivity.DATA_DEVICE)
                        , this, mGattCallback);
                BlueTooth_ConnectHelper.getInstance().blueToothConnect((BluetoothDevice) data.getParcelableExtra(DeviceListActivity.DATA_DEVICE));
//                Message msg = mHandler.obtainMessage(FOUND_DEVICE);
//                msg.obj = c;
//                mHandler.sendMessage(msg);
                break;
        }
    }

    /**
     * 检查是否打开了定位服务，再打开相机扫描
     */
    private void checkLocationAndOpenCamer() {
        LocationManager lm = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        boolean locationISOK = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (locationISOK) {
            startActivityForResult(new Intent(this, DeviceListActivity.class)
                    , DeviceListActivity.REQUEST_DEVICE);
        } else {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, REQUEST_LOCATIONARESULT);
        }
    }

    //-*-----------------------------------------------------------
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECTED://连接成功
                    Toast.makeText(Blue_MainActivity.this, "连接成功", Toast.LENGTH_LONG).show();

                    break;
                case DISCONNECTED://断开连接
                    Log.e(TAG, "连接断开...");
                    Toast.makeText(Blue_MainActivity.this, "连接断开！", Toast.LENGTH_LONG).show();
                    break;
                case RECONNECT:
                    if (mConnectionClient != null) {
                        mConnectionClient.connect();
                    }/*else{
                        if(mScanClient!=null){
                            mScanClient.startScan();
                        }
                    }*/
                    break;
                case RECEIVE:
                    String data = (String) msg.obj;
                    break;
                case SEND:
                    break;
                case FOUND_DEVICE:
                    mScanClient.stopScan();
//                    mConnectionClient = (BluetoothConnectionClient) msg.obj;
                    if (mConnectionClient != null) {
                        mConnectionClient.disconnect();
                    }
                    System.out.println("找到蓝牙并进行连接 ");
                    mConnectionClient = (BluetoothConnectionClient) msg.obj;
                    mConnectionClient.connect();

                    break;
                case BLUEM_ESSAGE:
                    String blueMsg = (String) msg.obj;
                    if (blueMsg.equals("success")) {
                        Toast.makeText(Blue_MainActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (blueMsg.equals("fail")) {
                        Toast.makeText(Blue_MainActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
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
                    gatt.discoverServices();
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
            Log.e(TAG, receive_msg);
            int state = receive_msg.lastIndexOf("FEFEFEFE68");
            int state_lenth = state + 36;//从FEFEFE68到标识码的长度
            String ditle = receive_msg.substring(receive_msg.length() - 2, receive_msg.length());//检查最后的字节是否为16

            if (state >= 0 && receive_msg.length() >= state_lenth && ditle.equals("16")) {
                String stateControl = receive_msg.substring(state + 24, state + 26);//控制字
                String length = receive_msg.substring(state + 26, state + 28);//长度
                String stateNum = receive_msg.substring(state + 28, state + 36);//标示符
                int len = Integer.valueOf(length, 16);//字符串内长度字节
                receive_msg = "";
            }


        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.d(TAG, "onDescriptorWrite: status=" + status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                mHandler.sendEmptyMessage(CONNECTED);
            }
        }
    };


    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.e(TAG, "附近的蓝牙：" + device.getAddress());
        if (device.getAddress().equals(tableMac)) {
            BluetoothConnectionClient c = new BluetoothConnectionClient(
                    device, this, mGattCallback);
            Message msg = mHandler.obtainMessage(FOUND_DEVICE);
            msg.obj = c;
            mHandler.sendMessage(msg);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            //case BluetoothScanClient.MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION:
            case REQUEST_LOCATIONARESULT:
                boolean isAllGranted = true;
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        isAllGranted = false;
                        break;
                    }
                }
                if (isAllGranted) {
                    //已全部授权
                    checkLocationAndOpenCamer();
                } else {
                    //权限有缺失
                    Toast.makeText(this, R.string.permission_failed,
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, 10);
                }
                break;
            default:
                Toast.makeText(this, R.string.permission_failed, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 10);
                break;
        }

    }

    /**
     * 事件订阅者处理事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoonEvent(EvenBusBean evenBusBean) {
        String topic = evenBusBean.getTopic();
        if (topic.equals(EvenBusEnum.EvenBus_BlueToothConnect.getEvenName())) {
            String message = evenBusBean.getData();
            showMsg(message);
        }
    }

}
