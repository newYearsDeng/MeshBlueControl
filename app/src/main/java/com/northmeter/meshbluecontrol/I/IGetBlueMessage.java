package com.northmeter.meshbluecontrol.I;


import com.northmeter.meshbluecontrol.bluetooth.GetBuleModel;

/**
 * Created by dyd
 * 2017/5/15
 */
public interface IGetBlueMessage {
    void getBlueMessage(String str, GetBuleModel.DownloadMsgCallback downloadMsgCallback);
}
