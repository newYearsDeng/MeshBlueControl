package com.northmeter.meshbluecontrol.presenter;

import android.content.Context;
import android.util.Log;

import com.northmeter.meshbluecontrol.I.I_DevicesInGateWayList;
import com.northmeter.meshbluecontrol.I.I_ShowDevicesInGateWay;
import com.northmeter.meshbluecontrol.bean.BleBlueToothBean;
import com.northmeter.meshbluecontrol.bean.DBBlueToothBean;
import com.northmeter.meshbluecontrol.sqlite.BlueDeviceHelper;
import com.northmeter.meshbluecontrol.utils.Udp_Help;

import java.util.List;

/**
 * Created by dyd on 2019/4/9.
 */

public class DevicesInGateWayListPresenter implements I_DevicesInGateWayList {
    private Context context;
    private I_ShowDevicesInGateWay showDevicesInGateWay;


    public DevicesInGateWayListPresenter(Context context){
        this.context = context;
        this.showDevicesInGateWay = (I_ShowDevicesInGateWay) context;
    }

    @Override
    public void deleteDevice(List<DBBlueToothBean> datas) {
        for(DBBlueToothBean item:datas){
            if(item.isCheck()){
                boolean result = new BlueDeviceHelper(context).delete("Mac",item.getMac());
            }
        }
        showDevicesInGateWay.returnMessage("01");

    }



    /**获取添加档案的数据帧*/
    public String getAddRecordData(String fatherNum,List<BleBlueToothBean> childList){//BD 00 000000000000 04 0700 01B5 01000000000000  cs 16
        String len = Udp_Help.intToHex(childList.size());
        StringBuffer addChildStr = new StringBuffer();
        for(BleBlueToothBean item:childList){
            addChildStr.append(Udp_Help.reverseRst(item.getTableNum()));
        }
        String firstData = "01B5"+len+addChildStr;
        String Length = Udp_Help.getLength_2(firstData);
        String oriData = "00"+Udp_Help.reverseRst(fatherNum)+"04"+Length+firstData;
        String cs = Udp_Help.get_sum(oriData);
        String resultData = "BD"+oriData+cs+"16";
        System.out.println("resultData:"+resultData);
        return resultData;
    }

    /**查询档案*/
    public String readRecord(String fatherNum){//BD 00 33 33 33 33 33 33 01 02 00 03 B5 ED 16
        String oriData = "00"+Udp_Help.reverseRst(fatherNum)+"01020003B5";
        String cs = Udp_Help.get_sum(oriData);
        String resultData = "BD"+oriData+cs+"16";
        System.out.println("resultData:"+resultData);
        return resultData;
    }

    /**删除档案*/
    public String delleteRecord(String fatherNum ,List<DBBlueToothBean> datas){
        //BD 00 333333333333 04 0900 02B5 01 030101010101 FF16
        //BD 00 112233445566 04 0900 02B5 01 962911800100 7b16

        String delTableNum="";
        int index = 0;
        for(int i = 0;i<datas.size();i++){
            DBBlueToothBean item = datas.get(i);
            if(item.isCheck()){
                 index = index+1;
                 delTableNum = delTableNum+Udp_Help.reverseRst(item.getTableNum());
            }
        }
        String total = Udp_Help.intToHex(index);
        String dataAddre = "02B5"+total+delTableNum;
        String dataLen = Udp_Help.getLength_2(dataAddre);

        String oriData = "00"+Udp_Help.reverseRst(fatherNum)+"04"+dataLen+dataAddre;
        String cs = Udp_Help.get_sum(oriData);
        String resultData = "BD"+oriData+cs+"16";
        System.out.println("resultData:"+resultData);
        return resultData;
    }


    /**查询入网节点
     BD 00 33 33 33 33 33 33 01 03 00 02 B3 00 EB 16 //未入网节点
     BD 00 33 33 33 33 33 33 01 03 00 02 B3 01 EC 16 //入网节点
     BD 00 33 33 33 33 33 33 01 03 00 02 B3 02 ED 16 //代维护节点
     BD 00 33 33 33 33 33 33 01 03 00 02 B3 03 EE 16 //故障节点*/
    public String readOnlineRecord(String fatherNum){//BD 00 33 33 33 33 33 33 01 03 00 02 B3 01 EC 16
        String oriData = "00"+Udp_Help.reverseRst(fatherNum)+"01030002B301";
        String cs = Udp_Help.get_sum(oriData);
        String resultData = "BD"+oriData+cs+"16";
        System.out.println("resultData:"+resultData);
        return resultData;
    }

}
