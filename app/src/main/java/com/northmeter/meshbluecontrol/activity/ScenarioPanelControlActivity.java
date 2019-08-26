package com.northmeter.meshbluecontrol.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.northmeter.meshbluecontrol.I.I_ShowBlueSend;
import com.northmeter.meshbluecontrol.R;
import com.northmeter.meshbluecontrol.base.BaseActivity;
import com.northmeter.meshbluecontrol.bean.EvenBusBean;
import com.northmeter.meshbluecontrol.bluetooth.BlueTooth_ConnectHelper;
import com.northmeter.meshbluecontrol.bluetooth.SendBlueMessage;
import com.northmeter.meshbluecontrol.enumBean.EvenBusEnum;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dyd on 2019/4/16.
 * 情景面板
 */

public class ScenarioPanelControlActivity extends BaseActivity implements I_ShowBlueSend {
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.checkbox_control_all)
    CheckBox checkboxControlAll;
    @BindView(R.id.checkbox_control_left)
    CheckBox checkboxControlLeft;
    @BindView(R.id.checkbox_control_right)
    CheckBox checkboxControlRight;

    private SendBlueMessage sendBlueMessage;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scenario_panel_control;
    }

    private String childType, childName, childNum, fatherNum,faterMac;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void initIntentData() {
        super.initIntentData();
        childType = getIntent().getStringExtra("childType");
        childName = getIntent().getStringExtra("childName");
        childNum = getIntent().getStringExtra("childNum");
        fatherNum = getIntent().getStringExtra("fatherNum");
        faterMac = getIntent().getStringExtra("fatherMac");//主节点蓝牙mac
    }

    @Override
    public void setTitle() {
        super.setTitle();
        tvToolbarTitle.setText(childName);
    }

    @Override
    public void initData() {
        super.initData();
        sendBlueMessage = new SendBlueMessage(this);
        if(!getIntent().getBooleanExtra("refresh",false)){
            startLoadingDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 事件订阅者处理事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoonEvent(EvenBusBean evenBusBean) {
        stopLoadingDialog();
        String topic = evenBusBean.getTopic();
        String message = evenBusBean.getData();
        if (topic.equals(EvenBusEnum.EvenBus_BlueToothConnect.getEvenName())) {
            showMsg(message.equals("0")?"连接成功":message);
        } else if (topic.equals(EvenBusEnum.EvenBus_OnRetuenMessage.getEvenName())) {
            int state = BlueTooth_ConnectHelper.getInstance().getCommState();
            switch (state) {
                case 80:
                    if (message.equals("1")) {
                        showMsg("操作失败");
                    } else {
                        showMsg("操作完成");
                    }
                    break;
            }
        }
    }


    /**
     * 灯控群控
     mode:0	全灭
     BD 00 33 33 33 33 33 33 01 09 00 03 B4 00 ff 00 00 00 00 00 F2 16

     mode:1	前3个亮，后3个灭
     BD 00 33 33 33 33 33 33 01 09 00 03 B4 01 ff 00 00 00 00 00 F3 16

     mode:2	后3个亮，前3个灭
     BD 00 33 33 33 33 33 33 01 09 00 03 B4 02 ff 00 00 00 00 00 F4 16

     mode:15	全亮
     BD 00 33 33 33 33 33 33 01 09 00 03 B4 0f ff 00 00 00 00 00 01 16
     */

    @OnClick({R.id.btn_tb_back, R.id.checkbox_control_all, R.id.checkbox_control_left, R.id.checkbox_control_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_tb_back:
                this.finish();
                break;
            case R.id.checkbox_control_all:
                startLoadingDialog();
                BlueTooth_ConnectHelper.getInstance().setCommState(80);
                if(checkboxControlAll.isChecked()){
                    sendBlueMessage.sendBlueMessage("BD0033333333333301090003B40fff00000000000116");
                }else{
                    sendBlueMessage.sendBlueMessage("BD0033333333333301090003B400ff0000000000F216");
                }
                break;
            case R.id.checkbox_control_left:
                startLoadingDialog();
                BlueTooth_ConnectHelper.getInstance().setCommState(80);
                if(checkboxControlLeft.isChecked()){
                    sendBlueMessage.sendBlueMessage("BD0033333333333301090003B401ff0000000000F316");
                }else{
                    sendBlueMessage.sendBlueMessage("BD0033333333333301090003B400ff0000000000F216");
                }
                break;
            case R.id.checkbox_control_right:
                startLoadingDialog();
                BlueTooth_ConnectHelper.getInstance().setCommState(80);
                if(checkboxControlRight.isChecked()){
                    sendBlueMessage.sendBlueMessage("BD0033333333333301090003B402ff0000000000F416");
                }else{
                    sendBlueMessage.sendBlueMessage("BD0033333333333301090003B400ff0000000000F216");
                }
                break;
        }
    }

    @Override
    public void showBlueSendMsg(int code) {
        if(code==1){
            showMsg("没有连接蓝牙");
            stopLoadingDialog();
        }
    }
}
