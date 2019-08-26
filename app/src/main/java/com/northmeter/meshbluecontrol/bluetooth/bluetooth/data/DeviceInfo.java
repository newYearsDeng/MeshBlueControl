package com.northmeter.meshbluecontrol.bluetooth.bluetooth.data;

import java.io.Serializable;

public class DeviceInfo implements Serializable{
	private String name;
	private String address;
	private boolean checked=false;
	public DeviceInfo(){
		
	}
	
	public DeviceInfo(String name, String address) {
		super();
		this.name = name;
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	@Override
	public String toString() {
		return "DeviceInfo [name=" + name + ", address=" + address
				+ ", checked=" + checked + "]";
	}
}
