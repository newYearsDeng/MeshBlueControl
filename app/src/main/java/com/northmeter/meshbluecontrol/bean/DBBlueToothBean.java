package com.northmeter.meshbluecontrol.bean;

/**
 * Created by dyd on 2019/4/8.
 * 数据库操作蓝牙的对象
 */

public class DBBlueToothBean {
    private String type;
    private String name;
    private String tableNum;
    private String Mac;
    private String fatherMac;
    private String fatherNum;
    private boolean isCheck;
    private boolean isOnline;

    public DBBlueToothBean(String type,String name,String tableNum,String Mac,String fatherNum,String fatherMac,boolean isCheck,boolean isOnline){
        this.type = type;
        this.name = name;
        this.tableNum = tableNum;
        this.Mac = Mac;
        this.fatherMac = fatherMac;
        this.fatherNum = fatherNum;
        this.isCheck = isCheck;
        this.isOnline = isOnline;

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableNum() {
        return tableNum;
    }

    public void setTableNum(String tableNum) {
        this.tableNum = tableNum;
    }

    public String getMac() {
        return Mac;
    }

    public void setMac(String mac) {
        Mac = mac;
    }

    public String getFatherMac() {
        return fatherMac;
    }

    public void setFatherMac(String fatherMac) {
        this.fatherMac = fatherMac;
    }

    public String getFatherNum() {
        return fatherNum;
    }

    public void setFatherNum(String fatherNum) {
        this.fatherNum = fatherNum;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
