package com.northmeter.meshbluecontrol.presenter;

import com.northmeter.meshbluecontrol.base.Constants;
import com.northmeter.meshbluecontrol.utils.Udp_Help;

/**
 * Created by dyd on 2019/4/10.
 */

public class MeterControlPresenter {

    public MeterControlPresenter(){}


    /**查询水表用电数据块
     * 68 99 69 00 26 11 18 68 1104 88 88 32 35 AD 16
     */

    /**生成抄电表表用电量数据帧
     * BD00 112233445566 01 1800 01B4 910215121703 6891021512170368 11(控制字) 04(长度) 88883235 3016 7d16
     * 组合有功总电能 00000000 (33333333)
     * 电压 02010100 （35343433）
     * 电流 02020100  （35353433）
     * 功率 02030000  （35363333）
     * 电网频率 02800002    （35B33335）
     * 功率因数 02060000    （35393333）
     *
     * */
    public String getElecMeterUserData(String fatherNum,String childNum,String bsf){//BD 00 000000000000 04 0700 01B5 01000000000000  cs 16
        String childData = "68"+ Udp_Help.reverseRst(childNum)+"681104"+bsf;
        String childCs = childData+Udp_Help.get_sum(childData)+"16";

        String oriData = "00"+Udp_Help.reverseRst(fatherNum)+"01180001B4"+Udp_Help.reverseRst(childNum)+childCs;
        String cs = Udp_Help.get_sum(oriData);
        String resultData = "BD"+oriData+cs+"16";
        System.out.println("resultData:"+resultData);
        return resultData;
    }

    /**生成插电表跳合闸数据帧
     * */
    public String getOpenOrCloseData(String fatherNum,String childNum ,boolean openOrclose){//BD 00 000000000000 04 0700 01B5 01000000000000  cs 16
        String ocData = "1B";
        if(openOrclose){
            ocData = "4E";//合闸
        }else{
            ocData = "4D";//跳闸
        }
        String childData = "68"+ Udp_Help.reverseRst(childNum)+"681C10"+ "35333333AB896745"+ocData+"32CCCCCCCCCCCC";
        String childCs = Udp_Help.get_sum(childData)+"16";

        String oriData = "00"+Udp_Help.reverseRst(fatherNum)+"04240001B4"+Udp_Help.reverseRst(childNum)+childData+childCs;
        String cs = Udp_Help.get_sum(oriData);
        String resultData = "BD"+oriData+cs+"16";
        System.out.println("resultData:"+resultData);
        return resultData;
    }




    /**生成抄水表用水量数据帧
     * BD00 333333333333 01 1B00 01B4 962911800100 FEFEFE6810 962911800100 000103901F02 7E16 6016
     * 应答：bd 00 112233445566 81 3200 01b4 96291180010000 fefefe6810 964b2e61c001000081b31cd21f0200120318c02c000000002c0000000000000001007c16dc16
     * */
    public String getWaterMeterUserData(String fatherNum,String childNum ){//BD 00 000000000000 04 0700 01B5 01000000000000  cs 16
        String childData = "6810"+ Udp_Help.reverseRst(childNum)+"000103901F02";
        String childCs = "FEFEFE"+childData+Udp_Help.get_sum(childData)+"16";

        String oriData = "00"+Udp_Help.reverseRst(fatherNum)+"011B0001B4"+Udp_Help.reverseRst(childNum)+childCs;
        String cs = Udp_Help.get_sum(oriData);
        String resultData = "BD"+oriData+cs+"16";
        System.out.println("resultData:"+resultData);
        return resultData;
    }





    /***
     通用方法
     */
    public String controlData(String fatherNum,String childNum,String kzz645,String bsf645,String data){//BD 00 000000000000 04 0700 01B5 01000000000000  cs 16
        String childDataFileds = bsf645+Udp_Help.create_645ToHex(data);//标准645数据域（标示符+数据）
        String childLen = Udp_Help.getLength_1(childDataFileds);//标准645长度
        String childData = "68"+ Udp_Help.reverseRst(childNum)+"68"+kzz645+childLen+childDataFileds;//完整645帧
        String childCs = Udp_Help.get_sum(childData)+"16";//645校验核 + 16结尾

        String fatherDataFileds = "01B4"+Udp_Help.reverseRst(childNum)+childData+childCs;
        String fatherLen = Udp_Help.getLength_2(fatherDataFileds);
        String oriData = "00"+Udp_Help.reverseRst(fatherNum)+"04"+fatherLen+fatherDataFileds;
        String cs = Udp_Help.get_sum(oriData);
        String resultData = "BD"+oriData+cs+"16";
        System.out.println("resultData:"+resultData);
        return resultData;
    }
}
