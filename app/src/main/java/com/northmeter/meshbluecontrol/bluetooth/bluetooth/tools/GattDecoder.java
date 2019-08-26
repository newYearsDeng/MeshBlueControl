package com.northmeter.meshbluecontrol.bluetooth.bluetooth.tools;

/**
 * Created by benjamin on 16/4/22.
 */
public class GattDecoder {

    public float getTemperature(byte[] value){
        float temp = 0f;
        temp =getShort(value,4)/10f;
        return temp;
    }

    // 将byte数组转化成short类型
    private short getShort(byte[] b, int index) {
        return (short) (((b[index + 1] << 8) | b[index + 0] & 0xff));
    }


}
