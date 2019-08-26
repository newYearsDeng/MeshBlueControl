package com.northmeter.meshbluecontrol.bluetooth;


import com.northmeter.meshbluecontrol.I.IBlueEntity;
import com.northmeter.meshbluecontrol.I.IShowSMainMessage;

/**
 * Created by dyd
 * 2017/5/15
 */
public class GetBlueEntity implements IBlueEntity {
    private GetBuleModel buleModel;
    private IShowSMainMessage showSMainMessage;
    public GetBlueEntity(IShowSMainMessage showSMainMessage){
        buleModel = new GetBuleModel();
        this.showSMainMessage = showSMainMessage;
    }

    @Override
    public void transmitBlueMsg(String prar) {
        buleModel.getBlueMessage(prar,new GetBuleModel.DownloadMsgCallback() {
            @Override
            public void onMainResult(String mainString) {
                showSMainMessage.showMainMsg(mainString);
            }

            @Override
            public void onSuccessResult(String code) {
                showSMainMessage.showReturnCode(code);
            }
        });
    }
}
