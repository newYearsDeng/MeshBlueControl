package com.northmeter.meshbluecontrol.bluetooth.bluetooth.view;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.northmeter.meshbluecontrol.R;
import com.northmeter.meshbluecontrol.bean.BleBlueToothBean;


public class DeviceListItemView extends RelativeLayout {
	private Context context;
	RelativeLayout v;
	RadioButton checkState;
	TextView deviceName;

	BleBlueToothBean device;
	
	public DeviceListItemView(Context context, BleBlueToothBean device) {
		super(context);
		this.context = context;
		this.device = device;
		init();
		initData();
	}
	
	public DeviceListItemView(Context context, AttributeSet attrs){
		super(context,attrs);
		this.context = context;
		init();
		initData();
	}
	
	public void setCheckState(boolean checked){
		checkState.setChecked(checked);
	}

	private void init(){
		v = (RelativeLayout)LayoutInflater.from(context)
				.inflate(R.layout.device_list_item, this,true);
		checkState = (RadioButton)v.findViewById(R.id.radio_bt_check_state);
		deviceName = (TextView)v.findViewById(R.id.text_device_name);
	}
	
	public void initData(){
		if(device==null) return;
		setName(device.getName()+"\n"+device.getAddress());
	}
	
	
	public void setName(String text){
		deviceName.setText(text);
	}

	public BleBlueToothBean getDevice() {
		return device;
	}

	public void setDevice(BleBlueToothBean device) {
		this.device = device;
	}
}
