package com.northmeter.meshbluecontrol.enumBean;

import com.northmeter.meshbluecontrol.R;

/**
 * Created by dyd on 2019/4/4.
 * 设备类型
 */

public enum DevicesTypeEnum {
    Device_GateWay("00","网关"),
    Device_Termial("01","终端模块"),
    Device_GasAlarm("02","燃气报警器"),
    Device_WindowOpener("03","开窗器"),
    Device_Manipulator("04","机械手"),
    Device_ElecMeter("05","电表"),
    Device_WallMountedSocket("06","墙挂式插座表"),
    Device_MobileSocket("07","移动式插座表"),
    Device_AirConditioning("08","空调控制器"),
    Device_GuideMeter("09","导轨表"),
    Device_SingleLampControl("0A","单灯控制器"),
    Device_WaterMeter("0B","水表"),
    Device_CentralAirConditioner("0C","中央空调器"),
    Device_FourStreetLightControl("0D","四路灯控"),
    Device_ThreeStreetLighControl("0E","三路灯控"),
    Device_ScenarioPanel("0F","情景面板"),
    Device_OtherControl("10","其他设备");

    DevicesTypeEnum(String type,String name){
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


    public static DevicesTypeEnum getDevicesTypeEnum(String value) {
        for (DevicesTypeEnum deviceType : values()) {
            System.out.println(deviceType.getName());
            if (deviceType.getType().equals(value)) {
                return deviceType;
            }
        }
        return Device_OtherControl;
    }

}
