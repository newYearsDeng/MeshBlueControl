package com.northmeter.meshbluecontrol.bean;

/**
 * Created by dyd on 2019/4/19.
 */

public class TypeManageBean {
    private String type;
    private String name;


    public TypeManageBean(String type,String name){
        this.type = type;
        this.name = name;
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
}
