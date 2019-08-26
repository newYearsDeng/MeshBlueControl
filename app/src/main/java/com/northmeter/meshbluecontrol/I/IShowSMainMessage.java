package com.northmeter.meshbluecontrol.I;

/**
 * Created by dyd
 * 2017/5/23
 */
public interface IShowSMainMessage {
    /**蓝牙消息返回*/
    void showMainMsg(String message);
    /**蓝牙消息返回*/
    void showReturnCode(String code);//0 成功 1 失败
}
