package com.northmeter.meshbluecontrol.presenter;

import android.content.Context;

import com.northmeter.meshbluecontrol.I.I_AirConditioningControlPresenter;
import com.northmeter.meshbluecontrol.base.Constants;
import com.northmeter.meshbluecontrol.utils.Udp_Help;

/**
 * Created by dyd on 2019/4/12.
 */

public class AirConditioningControlPresenter implements I_AirConditioningControlPresenter {
    private Context context;
    public AirConditioningControlPresenter(Context context){
        this.context = context;
    }



    /**
     * 读数据
     * 控制制码：读数据 C=11 正常应答 C=91  异常应答 　C=D1
     *
     * 写数据
     * 控制制码：读数据 C=14 正常应答 C=94  异常应答 　C=D4
     * */
    /**生成读电量信息数据帧 02ff5555 35328888 反向为88883235*/
    public String getReadData(String fatherNum,String childNum){//BD 00 000000000000 04 0700 01B5 01000000000000  cs 16
        String childData ="68"+ Udp_Help.reverseRst(childNum)+"68110488883235";
        String childCs = Udp_Help.get_sum(childData)+"16";

        String oriData = "00"+Udp_Help.reverseRst(fatherNum)+"01180001B4"+Udp_Help.reverseRst(childNum)+childData+childCs;
        String cs = Udp_Help.get_sum(oriData);
        String resultData = "BD"+oriData+cs+"16";
        System.out.println("resultData:"+resultData);
        return resultData;
    }


    /***
     * 应答8F，说明格式成功
     * bc 0f 00 01  (EF 42 33 34 ) 343342EF
     * 68 11 11 11 11 11 11 68 0F 36 34 33 42 EF 34 72 35 B8 35 72 35 12 39 37 34 86 56 45 45 56 6C 37 53 83 35 8C 82 33 53 43 54 33 D3 A4 D1 34 6B 56 44 45 56 6C 37 53 A3 35 85 82 33 53 33 33 63 D3 13 16
     */
    public String getControlData(String fatherNum,String childNum,String controlHwm){//BD 00 000000000000 04 0700 01B5 01000000000000  cs 16
        String childDataFileds ="343342EF"+Udp_Help.create_645ToHex(controlHwm);
        System.out.println("resultData:=="+controlHwm);
        System.out.println("resultData:=="+Udp_Help.create_645ToHex(controlHwm));
        String childLen = Udp_Help.getLength_1(childDataFileds);
        String childData = "68"+ Udp_Help.reverseRst(childNum)+"680F"+childLen+childDataFileds;
        String childCs = Udp_Help.get_sum(childData)+"16";

        String fatherDataFileds = "01B4"+Udp_Help.reverseRst(childNum)+childData+childCs;
        String fatherLen = Udp_Help.getLength_2(fatherDataFileds);
        String oriData = "00"+Udp_Help.reverseRst(fatherNum)+"04"+fatherLen+fatherDataFileds;
        String cs = Udp_Help.get_sum(oriData);
        String resultData = "BD"+oriData+cs+"16";
        System.out.println("resultData:"+resultData);
        return resultData;
    }



    /***
     * 中央空调开关控制
     * BC AA 01 09  (3C 34 DD EF )
     * 68 01 00 29 08 15 20 68 14 10 3C 34 DD EF 35 DF F1 FF AB 89 67 45 DD 33 57 35 17 16
     * */
    public String getCentreControlData(String fatherNum,String childNum,String openModel,String speedModel,int temputer){//BD 00 000000000000 04 0700 01B5 01000000000000  cs 16
        String childDataFileds ="3C34DDEF35DFF1FFAB896745"+openModel+ Udp_Help.create_645ToHex("00"+temputer)+ speedModel;
        String childLen = Udp_Help.getLength_1(childDataFileds);
        String childData = "68"+ Udp_Help.reverseRst(childNum)+"6814"+childLen+childDataFileds;
        String childCs = Udp_Help.get_sum(childData)+"16";

        String fatherDataFileds = "01B4"+Udp_Help.reverseRst(childNum)+childData+childCs;
        String fatherLen = Udp_Help.getLength_2(fatherDataFileds);
        String oriData = "00"+Udp_Help.reverseRst(fatherNum)+"04"+fatherLen+fatherDataFileds;
        String cs = Udp_Help.get_sum(oriData);
        String resultData = "BD"+oriData+cs+"16";
        System.out.println("resultData:"+resultData);
        return resultData;
    }
}
