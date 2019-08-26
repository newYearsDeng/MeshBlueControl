package com.northmeter.meshbluecontrol.enumBean;

/**
 * Created by dyd on 2019/4/4.
 * 设备分类管理
 */

public enum DevicesTypeManageEnum {
    Device_GateWay("00/01","网关"),
    Device_ElecMeter("05/09","电表"),
    Device_Socket("06/07","插座"),
    Device_LightControl("0A/0D/0E/0F","灯控"),
    Device_WaterMeter("0B","水表"),
    Device_AirConditioning("08/0C","空调"),
    Device_OtherControl("02/03/04/10","其他设备");

    DevicesTypeManageEnum(String type, String name){
        this.type = type;
        this.name = name;
    }

    private String type;
    private String name;

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


    public static DevicesTypeManageEnum getDevicesTypeEnum(String value) {
        for (DevicesTypeManageEnum deviceType : values()) {
            System.out.println(deviceType.getName());
            if (deviceType.getType().equals(value)) {
                return deviceType;
            }
        }
        return Device_OtherControl;
    }

}
