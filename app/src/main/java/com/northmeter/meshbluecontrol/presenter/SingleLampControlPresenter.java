package com.northmeter.meshbluecontrol.presenter;

import com.northmeter.meshbluecontrol.I.I_SingleLampControlPresenter;
import com.northmeter.meshbluecontrol.utils.Udp_Help;

/**
 * Created by dyd on 2019/4/9.
 */

public class SingleLampControlPresenter implements I_SingleLampControlPresenter {
    //透传指令：BD 0A 910215121703 01 1200 01B1 68 910215121703 68 1104 88883235 3016 1916

    /**生成读用电信息数据帧 02ff5555 35328888 反向为88883235*/
    public String getReadData(String fatherNum,String childNum){//BD 00 000000000000 04 0700 01B5 01000000000000  cs 16
        String childData ="68"+ Udp_Help.reverseRst(childNum)+"68110488883235";
        String childCs = Udp_Help.get_sum(childData)+"16";

        String oriData = "00"+Udp_Help.reverseRst(fatherNum)+"01180001B4"+Udp_Help.reverseRst(childNum)+childData+childCs;
        String cs = Udp_Help.get_sum(oriData);
        String resultData = "BD"+oriData+cs+"16";
        System.out.println("resultData:"+resultData);
        return resultData;
    }


    /**生成单灯控制器开关数据帧*/
    public String getOpenOrCloseData(String fatherNum,String childNum ,boolean openOrclose){//BD 00 000000000000 04 0700 01B5 01000000000000  cs 16
//        开灯：
//        68 00 03 15 12 17 03 68 34 05 DD DD DD DD 34 F5 16
//        68 00 03 15 12 17 03 68 B4 00 C8 16
//
//        关灯：
//        68 00 03 15 12 17 03 68 34 05 DD DD DD DD 33 F4 16
//        68 00 03 15 12 17 03 68 B4 00 C8 16
        String oc = "34";
        if(openOrclose){
            oc = "34";
        }else{
            oc = "33";
        }
        String childData = "68"+Udp_Help.reverseRst(childNum)+"683405DDDDDDDD"+oc;
        String childCs = Udp_Help.get_sum(childData)+"16";

        String oriData = "00"+Udp_Help.reverseRst(fatherNum)+"04"+"190001B4"+Udp_Help.reverseRst(childNum)+childData+childCs;
        String cs = Udp_Help.get_sum(oriData);
        String resultData = "BD"+oriData+cs+"16";
        System.out.println("resultData:"+resultData);
        return resultData;
    }
}
