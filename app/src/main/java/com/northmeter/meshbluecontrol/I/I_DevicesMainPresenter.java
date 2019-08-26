package com.northmeter.meshbluecontrol.I;

import com.northmeter.meshbluecontrol.bean.DBBlueToothBean;

import java.util.List;

/**
 * Created by dyd on 2019/4/8.
 */

public interface I_DevicesMainPresenter {
    void addRecord();
    void deleteDevice(String type,List<DBBlueToothBean> datas);
}
