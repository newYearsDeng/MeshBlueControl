package com.northmeter.meshbluecontrol.bluetooth.bluetooth.tools;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by benjamin on 16/4/21.
 */
public class GattCode {

    /** 与service相关的UUID */
    // 打开通知profil的固定UUID
    /**
     * 与service相关的UUID
     */
    // 打开通知profil的固定UUID

    public static final String CLIENT_CHARACTERISTIC_CONFIG
            = "00002902-0000-1000-8000-00805f9b34fb";
    // main service uuid
    public static final UUID FFF_SERVICE_OTHER = UUID
            .fromString("0000ffe5-0000-1000-8000-00805f9b34fb");
//    public static final UUID FFF_SERVICE = UUID
//            .fromString("0000fff0-0000-1000-8000-00805f9b34fb");
      public static final UUID FFF_SERVICE = UUID
              .fromString("0000ff03-0000-1000-8000-00805f9b34fb");


    // characteristic
    public static final UUID FFF_1 = UUID
            .fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    public static final UUID FFF_2 = UUID
            .fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    public static final UUID FFF_3 = UUID
            .fromString("00002b08-0000-1000-8000-00805f9b34fb");
    public static final UUID FFF_4 = UUID
            .fromString("00002b07-0000-1000-8000-00805f9b34fb");
    public static final UUID FFF_4_OHTER = UUID
            .fromString("0000ffe9-0000-1000-8000-00805f9b34fb");
    public static final UUID FFF_5 = UUID
            .fromString("0000fff5-0000-1000-8000-00805f9b34fb");

    public static final UUID DESCRIPTOR = UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG);
    public static final int CHARACTERISTIC1 = 1;
    public static final int CHARACTERISTIC2 = 2;
    public static final int CHARACTERISTIC3 = 3;
    public static final int CHARACTERISTIC4 = 4;
    public static final int CHARACTERISTIC5 = 5;


    public static byte[] AUTHENTICATE = {
            0x21, 0x07, 0x06, 0x05,
            0x04, 0x03, 0x02, 0x01,
            (byte) 0xB8, 0x22, 0x00, 0x00,
            0x00, 0x00, 0x00
    };

    public static byte[] ACTIVE_DATA = {
            0x0B, 1, 0, 0, 0
    };
    public static byte[] CLOSE_DATA = {
            0x0B, 0, 0, 0, 0
    };
    public static byte[] ADJUST_37 = {
            (byte) 0xfe, 3, 0, 0, 0
    };
    public static byte[] ADJUST_SAVE = {
            (byte) 0xfe, (byte) 0xff, 0, 0, 0
    };


    public static byte[] getTimeByte() {
        byte[] buf;
        Calendar c = Calendar.getInstance();
        c.set(2000, 0, 1, 0, 0, 0);
        TimeZone tz = TimeZone.getTimeZone("GMT");
        c.setTimeZone(tz);
        long t1 = c.getTimeInMillis();
        long t2 = System.currentTimeMillis();
        int t3 = (int) ((t2 - t1) / 1000);
        buf = intToByteInverse(t3);
        byte[] value = new byte[5];
        value[0] = 0x01;
        value[1] = buf[0];
        value[2] = buf[1];
        value[3] = buf[2];
        value[4] = buf[3];

        return value;
    }

    public static byte[] intToByteInverse(int key) {
        return intToByteInverse(key, 4, 0, 0);
    }

    public static byte[] intToByteInverse(int key, int count, int offset, int type) {
        byte[] result = new byte[count + offset];
        if (offset == 1) {
            result[0] = (byte) type;
        }
        for (int i = 0; i < count; i++) {
            result[i + offset] = (byte) (key >> (i * 8) & 0xff);
        }
        return result;
    }

}
