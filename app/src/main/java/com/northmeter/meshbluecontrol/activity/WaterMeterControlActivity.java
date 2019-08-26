package com.northmeter.meshbluecontrol.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.northmeter.meshbluecontrol.I.I_ShowBlueSend;
import com.northmeter.meshbluecontrol.R;
import com.northmeter.meshbluecontrol.base.BaseActivity;
import com.northmeter.meshbluecontrol.bean.EvenBusBean;
import com.northmeter.meshbluecontrol.bluetooth.BlueTooth_ConnectHelper;
import com.northmeter.meshbluecontrol.bluetooth.SendBlueMessage;
import com.northmeter.meshbluecontrol.enumBean.EvenBusEnum;
import com.northmeter.meshbluecontrol.presenter.MeterControlPresenter;
import com.northmeter.meshbluecontrol.utils.Udp_Help;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dyd on 2019/4/9.
 * 水表控制
 */

public class WaterMeterControlActivity extends BaseActivity implements I_ShowBlueSend {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_water_user)
    TextView tvWaterUser;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.tv_show_time)
    TextView tvShowTime;
    private String childType, childName, childNum, fatherNum,faterMac;
    private SendBlueMessage sendBlueMessage;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_watermeter_control;
    }

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
        tvRightText.setText("刷新");
    }

    @Override
    public void initData() {
        super.initData();
        sendBlueMessage = new SendBlueMessage(this);
        startLoadingDialog();
        if(getIntent().getBooleanExtra("refresh",false)){
            BlueTooth_ConnectHelper.getInstance().setCommState(60);
            sendBlueMessage.sendBlueMessage(new MeterControlPresenter().getWaterMeterUserData(fatherNum, childNum));
        }
    }

    @OnClick({R.id.btn_tb_back, R.id.tv_right_text})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_tb_back:
                this.finish();
                break;
            case R.id.tv_right_text:
                startLoadingDialog();
                BlueTooth_ConnectHelper.getInstance().setCommState(60);
                sendBlueMessage.sendBlueMessage(new MeterControlPresenter().getWaterMeterUserData(fatherNum, childNum));
                break;
        }
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
            if(message.equals("0")){
                startLoadingDialog();
                BlueTooth_ConnectHelper.getInstance().setCommState(60);
                sendBlueMessage.sendBlueMessage(new MeterControlPresenter().getWaterMeterUserData(fatherNum, childNum));
            }
        } else if (topic.equals(EvenBusEnum.EvenBus_OnRetuenMessage.getEvenName())) {
            if (message.equals("1")) {
                showMsg("刷新失败");
            } else {
                //bd00112233445566812f0001b496291180010000 fefefe6810962911800100008116901f02001200002c000000002c0000000000000002007d162516
                if (BlueTooth_ConnectHelper.getInstance().getCommState() == 60) {
                    if (message.indexOf("BD") >= 0) {
                        int stand_0 = message.indexOf("BD");
                        String blueMsg = message.substring(stand_0, message.length()).toUpperCase();
                        String control = blueMsg.substring(16, 18).toUpperCase();//控制字
                        if (control.equals("81")) {
                            int stand_1 = message.indexOf("FEFEFE68");
                            String childControl = blueMsg.substring(stand_1 + 24, stand_1 + 26);//控制字
                            if (childControl.equals("81")) {
                                String childData = Udp_Help.reverseRst(blueMsg.substring(stand_1 + 34, stand_1 + 42).toUpperCase());
                                String time = blueMsg.substring(stand_1 + 54, stand_1 + 68).toUpperCase();
                                tvWaterUser.setText(childData.substring(0,6)+"."+childData.substring(6,8)+" m³");
                                String waterTime = time.substring(0,4)+"/"+time.substring(4,6)+"/"+time.substring(6,8)+" "+
                                        time.substring(8,10)+":"+time.substring(10,12)+":"+time.substring(12,14);//00000000000000

                                Date showTiem = new Date();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss"); //HH为24小时，hh为12小时，aa为AM/PM
                                tvShowTime.setText(sdf.format(showTiem));
                            }

                        } else {
                            showMsg("读取失败");
                        }

                    }
                }
            }

        }
    }

    /**
     * 应答:bd00 112233445566 81 2f00 01b4 96291180010000 fefefe 681096291180010000
     * 81 控制字
     * 16 长度
     * 901f 标示符
     * 02 序号ser
     * 00120000 当前累积流量
     * 2c 单位吨
     * 00000000 结算日累积流量
     * 2c 单位吨
     * 00000000000000 实时时间
     * 0200 水表状态
     * 7d16
     * 2516
     */


    @Override
    public void showBlueSendMsg(int code) {
        if(code==1){
            showMsg("没有连接蓝牙");
            stopLoadingDialog();
        }
    }
}
