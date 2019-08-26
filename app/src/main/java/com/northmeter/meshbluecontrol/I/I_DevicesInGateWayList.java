package com.northmeter.meshbluecontrol.I;

import com.northmeter.meshbluecontrol.bean.DBBlueToothBean;

import java.util.List;

/**
 * Created by dyd on 2019/4/9.
 */

public interface I_DevicesInGateWayList {

    void deleteDevice(List<DBBlueToothBean> datas);
}
