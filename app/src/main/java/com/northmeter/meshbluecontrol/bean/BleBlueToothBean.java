package com.northmeter.meshbluecontrol.bean;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dyd on 2019/4/4.
 */

public class BleBlueToothBean implements Serializable{
    private String address;
    private String type;
    private String tableNum;
    private String name;
    private boolean isCheck;

    public BleBlueToothBean(String address,String type,String tableNum,String name,boolean isCheck){
        this.address = address;
        this.type=type;
        this.tableNum = tableNum;
        this.name = name;
        this.isCheck = isCheck;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTableNum() {
        return tableNum;
    }

    public void setTableNum(String tableNum) {
        this.tableNum = tableNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
