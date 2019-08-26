package com.northmeter.meshbluecontrol.activity;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jmesh.blebase.base.BleManager;
import com.jmesh.blebase.callback.BleScanCallback;
import com.jmesh.blebase.state.BleDevice;
import com.northmeter.meshbluecontrol.R;
import com.northmeter.meshbluecontrol.bean.BleBlueToothBean;
import com.northmeter.meshbluecontrol.bluetooth.bluetooth.tools.BluetoothScanClient;
import com.northmeter.meshbluecontrol.bluetooth.bluetooth.view.DeviceListItemView;
import com.northmeter.meshbluecontrol.enumBean.DevicesTypeEnum;
import com.northmeter.meshbluecontrol.utils.Udp_Help;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class BleDeviceListActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String DATA_DEVICE = "DEVICE";
    String TAG = getClass().getSimpleName();

    public static final int REQUEST_DEVICE = 0X01;
    private ListView lv;
    private TextView confirm, cancel;

    private DeviceAdapter adapter;
    private ArrayList<BleBlueToothBean> devices;
    private List<BleBlueToothBean> checkedDevice = new ArrayList<>();
    private String scanType,type,tableNum;
    int prevCheckedPosition = -1;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            adapter.add((BleBlueToothBean) msg.obj);
            adapter.notifyDataSetChanged();
        }
    };


    private static final int ENABLE_BT_REQUEST_ID = 1;
    public static int REQUEST_CODE_DEVICE_NAME = 11;
    public static String DEVICE_NAME = "DeviceListActivity.DEVICE_NAME";
    public static final String TOAST = "toast";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_bluetooth_device_list);
            if (Build.VERSION.SDK_INT >= 21) {//5.0
                View decorView = getWindow().getDecorView();
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                getWindow().setStatusBarColor(Color.TRANSPARENT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0 - 23   设备手机状态浪字体图标颜色为黑色
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!bluetoothAdapter.isEnabled()) {
                BleManager.getInstance().enableBluetooth();
            }

            scanType = getIntent().getStringExtra("scanType");
            refreshDevice();
            initView();
            initData();
            initListener();
        }catch(Exception e){
            e.printStackTrace();;
        }
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // user didn't want to turn on BT
    	System.out.println("requestCode="+requestCode+" RESULT_OK "+RESULT_OK);
        if (requestCode == ENABLE_BT_REQUEST_ID) {
        	if(resultCode == RESULT_OK) {
        		//mScanClient.startScan();
		        return;
		    }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initData() {
//		ArrayList<DeviceInfo> dd = new ArrayList<DeviceInfo>();
//		for(int i=0;i<5;i++){
//			dd.add(new DeviceInfo("iFever"+i,"address"+i));
//		}
//		devices = dd;
        devices = new ArrayList<BleBlueToothBean>();
        adapter = new DeviceAdapter(this, devices);
        lv.setAdapter(adapter);
    }

    private void initView() {
        lv = (ListView) findViewById(R.id.device_list);
        confirm = (TextView) findViewById(R.id.confirm);
        cancel = (TextView) findViewById(R.id.cancel);
        lv.setEmptyView(findViewById(R.id.empty_view));
    }

    private void initListener() {
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d(TAG, "on ItemClick : position =" + position + ", id=" + id);
                if(adapter.getItem(position).isCheck()){
                    adapter.getItem(position).setCheck(false);
                }else{
                    adapter.getItem(position).setCheck(true);
                }
                adapter.notifyDataSetChanged();
            }
        });

        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                if (checkedDevice == null) return;
                for(BleBlueToothBean device: devices){
                    if(device.isCheck()){
                        checkedDevice.add(device);
                    }
                }


                Intent i = new Intent();
                i.putExtra(DATA_DEVICE, (Serializable) checkedDevice);
                setResult(RESULT_OK,i);
                finish();
                break;
            case R.id.cancel:
                finish();
                break;
            default:
                break;
        }
    }

    private void refreshDevice() {
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().clearBleDevice();
        BleManager.getInstance().scan(callback, 30000);
    }

    BleScanCallback callback = new BleScanCallback() {
        @Override
        public void onScanFinished(List<BleDevice> list) {

        }

        @Override
        public void onScanStarted(boolean b) {

        }

        @Override
        public void onScanning(BleDevice bleDevice) {
            BluetoothDevice device = bleDevice.getDevice();
            byte[] scanRecord = bleDevice.getScanRecord();
            String recordStr = Udp_Help.bytesToHexString(scanRecord).toUpperCase();
            if(recordStr.length()>20){
                String recordState = recordStr.substring(4,6);//广播是否以BD开头
                String scanTy = recordStr.substring(6,8);//设备类型
                String tableNum = Udp_Help.reverseRst(recordStr.substring(8,20));//表号
                if(scanType.equals(DevicesTypeEnum.Device_GateWay.getType())){//如果scanType为00.则表示只搜索网关，否则为子节点
                    if (checkDeviceExist(device)&&recordState.equals("BD")&&scanTy.equals(scanType)) {
                        Message msg = mHandler.obtainMessage(1);
                        msg.obj = new BleBlueToothBean(device.getAddress(),scanTy,tableNum,DevicesTypeEnum.Device_GateWay.getName(),false);
                        mHandler.sendMessage(msg);
                    }
                }else{
                    if (checkDeviceExist(device)&&recordState.equals("BD")&&!scanTy.equals(DevicesTypeEnum.Device_GateWay.getType())) {
                        Message msg = mHandler.obtainMessage(1);
                        msg.obj = new BleBlueToothBean(device.getAddress(),scanTy,tableNum,DevicesTypeEnum.getDevicesTypeEnum(scanTy).getName(),false);
                        mHandler.sendMessage(msg);
                    }
                }
            }
        }
    };


    @SuppressLint("NewApi")
	@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case BluetoothScanClient.REQUEST_LOCATIONARESULT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The requested permission is granted.
//                    if (mScanClient != null) {
//                        mScanClient.startScan();
//                    }
                } else {
                    // The user disallowed the requested permission.
                    Toast.makeText(this, R.string.permission_failed, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private class DeviceAdapter extends ArrayAdapter<BleBlueToothBean> {
        Context context;

        public DeviceAdapter(Context context, ArrayList<BleBlueToothBean> objects) {
            super(context, 0, objects);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BleBlueToothBean device = getItem(position);
            DeviceListItemView v;
            if (convertView == null) {
                v = new DeviceListItemView(context, device);

            } else {
                v = (DeviceListItemView) convertView;
                v.setDevice(device);
                v.initData();
            }
            v.setCheckState(device.isCheck());

            return v;
        }

    }

    private boolean checkDeviceExist(BluetoothDevice device) {
        if (devices == null)
            return false;

        for (BleBlueToothBean d : devices) {
            if (d.getAddress().equals(device.getAddress()))
                return false;
        }
        return true;
    }

}
