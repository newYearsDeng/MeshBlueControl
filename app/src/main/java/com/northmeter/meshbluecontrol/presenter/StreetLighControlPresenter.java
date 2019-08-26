package com.northmeter.meshbluecontrol.presenter;

import android.content.Context;

import com.northmeter.meshbluecontrol.I.I_StreetLighControlPresenter;
import com.northmeter.meshbluecontrol.base.Constants;
import com.northmeter.meshbluecontrol.utils.Udp_Help;

/**
 * Created by dyd on 2019/4/16.
 */

public class StreetLighControlPresenter implements I_StreetLighControlPresenter {

    private Context context;
    public StreetLighControlPresenter(Context context){
        this.context = context;
    }


    /**
     * 读数据
     * 控制制码：读数据 C=11 正常应答 C=91  异常应答 　C=D1
     *
     * 写数据
     * 控制制码：读数据 C=14 正常应答 C=94  异常应答 　C=D4
     * */

    /**生成读插座信息数据帧 02ff5555 35328888 反向为88883235*/
    public String getReadData(String fatherNum,String childNum){//BD 00 000000000000 04 0700 01B5 01000000000000  cs 16
        String childData ="68"+ Udp_Help.reverseRst(childNum)+"68110488883235";
        String childCs = Udp_Help.get_sum(childData)+"16";

        String oriData = "00"+Udp_Help.reverseRst(fatherNum)+"01180001B4"+Udp_Help.reverseRst(childNum)+childData+childCs;
        String cs = Udp_Help.get_sum(oriData);
        String resultData = "BD"+oriData+cs+"16";
        System.out.println("resultData:"+resultData);
        return resultData;
    }


    /**生成插座控制器开关数据帧
     * 写数据
     * 控制制码：读数据 C=14 正常应答 C=94  异常应答 　C=D4
     * 97	20	01	01(CA533434 反向为343453CA)	xx	1			*	跳闸（数据域需为8421）
     *97	20	01	02(CA533435 反向为353453CA)	xx	1			*	合闸（数据域需为8421）
     * */
    public String getOpenOrCloseData(String fatherNum,String childNum ,boolean openOrclose,String openOrcloseItem){//BD 00 000000000000 04 0700 01B5 01000000000000  cs 16
        String ocFlag = "353453CA";
        if(openOrclose){
            ocFlag = "353453CA";//合闸
        }else{
            ocFlag = "343453CA";//跳闸
        }
        String childData = "68"+ Udp_Help.reverseRst(childNum)+"68140D"+ocFlag+ Constants.HandlerKey+openOrcloseItem;
        String childCs = Udp_Help.get_sum(childData)+"16";

        String oriData = "00"+Udp_Help.reverseRst(fatherNum)+"04210001B4"+Udp_Help.reverseRst(childNum)+childData+childCs;
        String cs = Udp_Help.get_sum(oriData);
        String resultData = "BD"+oriData+cs+"16";
        System.out.println("resultData:"+resultData);
        return resultData;
    }
}
