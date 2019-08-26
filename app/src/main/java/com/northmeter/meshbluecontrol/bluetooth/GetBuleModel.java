package com.northmeter.meshbluecontrol.bluetooth;


import com.northmeter.meshbluecontrol.I.IGetBlueMessage;

/**
 * Created by dyd
 * 2017/5/15
 */
public class GetBuleModel implements IGetBlueMessage {

    protected GetBuleModel(){}

    @Override
    public void getBlueMessage(String str,DownloadMsgCallback downloadMsgCallback) {
        if(str.indexOf("BD",0)>=0){
            int stand_0 = str.indexOf("BD",0);
            String blueMsg = str.substring(stand_0,str.length()).toUpperCase();
            String control = blueMsg.substring(16,18).toUpperCase();//控制字
            switch(control){
                case "81":
                    String msgflag = blueMsg.substring(22,26).toUpperCase();//标示符
                    System.out.println(msgflag);
                    downloadMsgCallback.onMainResult(blueMsg);
                    break;
                case "84"://成功应答
                    downloadMsgCallback.onSuccessResult("0");
                    break;
                default:
                    downloadMsgCallback.onSuccessResult("1");
                    break;
            }


        }
    }

    public interface DownloadMsgCallback {
        void onMainResult(String mainString);

        void onSuccessResult(String successResult);
    }
}
