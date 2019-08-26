package com.northmeter.meshbluecontrol.enumBean;

/**
 * Created by dyd on 2019/3/27.
 */

public enum EvenBusEnum {
    EvenBus_BlueToothConnect("EvenBus_BlueToothConnect"),
    EvenBus_GateWayNotife("EvenBus_GateWayNotife"),
    EvenBus_SingleLampControl("SingleLampControl"),
    EvenBus_SocketControl("SocketControl"),
    EvenBus_GuidMeterControl("GuidMeterControl"),
    EvenBus_WaterMeterControl("WaterMeterControl"),
    EvenBus_AirConditioningControl("AirConditioningControl"),
    EvenBus_OnRetuenMessage("OnRetuenMessage");

    private String evenName;

    EvenBusEnum(String evenName){
        this.evenName = evenName;
    }

    public String getEvenName() {
        return evenName;
    }

    public void setEvenName(String evenName) {
        this.evenName = evenName;
    }
}
