package com.northmeter.meshbluecontrol.base;

import android.os.Environment;

import java.io.File;

/**
 * Created by dyd on 2018/11/28.
 */

public class Constants {
    public static final long DEFAULT_MILLISECONDS = 30000;//默认超时时间
    public static final String MQTT_HOST = "tcp://193.112.249.36:1883";

    public static final String TYPE = "eqptType";
    public static final String NAME = "title";
    public static final String METERNUM = "eqptIdCode";

    public static final String ANALYSISNUM = "https://cloud-meters.net/water/hardware/getDigit";
    /**
     * 图片存储路径
     */
    public static final String SAVEPIC = Environment.getExternalStorageDirectory() + File.separator + "com.northmeter.camerameterofscene/picture/";

    /**操作者代码和密码*/
    public static final String HandlerKey = "33333333AB896745";

    /**文件导出地址*/
    public static String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/northmeter/";

}
