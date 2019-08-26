package com.northmeter.meshbluecontrol.presenter;

import android.content.Context;

import com.northmeter.meshbluecontrol.I.I_DevicesMainPresenter;
import com.northmeter.meshbluecontrol.I.I_ShowDevicesMain;
import com.northmeter.meshbluecontrol.bean.DBBlueToothBean;
import com.northmeter.meshbluecontrol.enumBean.DevicesTypeManageEnum;
import com.northmeter.meshbluecontrol.sqlite.BlueDeviceHelper;

import java.util.List;

/**
 * Created by dyd on 2019/4/8.
 */

public class DevicesMainPresenter implements I_DevicesMainPresenter {
    private Context context;
    private I_ShowDevicesMain showDevicesMain;

    public DevicesMainPresenter(Context context){
        this.context = context;
        this.showDevicesMain = (I_ShowDevicesMain) context;
    }

    @Override
    public void addRecord() {

    }

    @Override
    public void deleteDevice(String type,List<DBBlueToothBean> datas) {
        for(DBBlueToothBean item:datas){
            if(item.isCheck()){
                if(type.equals(DevicesTypeManageEnum.Device_GateWay.getType())){
                    boolean result = new BlueDeviceHelper(context).delete("fatherMac",item.getFatherMac());
                }else{
                    boolean result = new BlueDeviceHelper(context).delete("Mac",item.getMac());
                }

            }
        }
        showDevicesMain.showData(0);
    }
}
