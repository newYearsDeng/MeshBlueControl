package com.northmeter.meshbluecontrol.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
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
import com.northmeter.meshbluecontrol.utils.MyResult;
import com.northmeter.meshbluecontrol.utils.Udp_Help;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dyd on 2019/4/9.
 * 电表控制（导轨表）控制
 */

public class GuidMeterControlActivity extends BaseActivity implements I_ShowBlueSend {


    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.btn_device_open_close)
    CheckBox btnDeviceOpenClose;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.iv_back_show)
    ImageView ivBackShow;
    @BindView(R.id.tv_show_ydl)
    TextView tvShowYdl;
    @BindView(R.id.tv_show_dy)
    TextView tvShowDy;
    @BindView(R.id.tv_show_dl)
    TextView tvShowDl;
    @BindView(R.id.tv_show_gl)
    TextView tvShowGl;
    @BindView(R.id.tv_show_dwpl)
    TextView tvShowDwpl;
    @BindView(R.id.tv_show_glys)
    TextView tvShowGlys;
    private String childType, childName, childNum, fatherNum,faterMac;
    private SendBlueMessage sendBlueMessage;
    private MeterControlPresenter meterControlPresenter;
    private static final String bsf_use ="88883235";//用电数据块
    private static final String bsf_ydl="33333333";//读用电量标示符
    private static final String bsf_dy="33343435";//读电压标示符
    private static final String bsf_dl="33343535";//读电流标示符
    private static final String bsf_gl="35363333";//读功率标示符
    private static final String bsf_dwpl="3533B335";//读电网频率标示符
    private static final String bsf_glys="33333935";//读功率因数标示符

    @Override
    protected int getLayoutId() {
        return R.layout.activity_socket_control;
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
        ivBackShow.setImageDrawable(getResources().getDrawable(R.drawable.img_back_elcmeter));
    }

    @Override
    public void initData() {
        super.initData();
        sendBlueMessage = new SendBlueMessage(this);
        meterControlPresenter = new MeterControlPresenter();
        startLoadingDialog();
        if(getIntent().getBooleanExtra("refresh",false)){
            BlueTooth_ConnectHelper.getInstance().setCommState(50);
            sendBlueMessage.sendBlueMessage(meterControlPresenter.getElecMeterUserData(fatherNum, childNum, bsf_use));
        }

    }


    @OnClick({R.id.btn_tb_back, R.id.tv_right_text,R.id.btn_device_open_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_tb_back:
                this.finish();
                break;
            case R.id.tv_right_text:
                startLoadingDialog();
                BlueTooth_ConnectHelper.getInstance().setCommState(50);
                sendBlueMessage.sendBlueMessage(meterControlPresenter.getElecMeterUserData(fatherNum, childNum, bsf_use));
                break;
            case R.id.btn_device_open_close:
                startLoadingDialog();
                boolean ocFlag = false;
                if (btnDeviceOpenClose.isChecked()) {
                    BlueTooth_ConnectHelper.getInstance().setCommState(56);
                    ocFlag = true;
                } else {
                    BlueTooth_ConnectHelper.getInstance().setCommState(57);
                    ocFlag = false;
                }
                sendBlueMessage.sendBlueMessage(meterControlPresenter.getOpenOrCloseData(fatherNum, childNum, ocFlag));
                break;
        }
    }

    @Override
    public void showBlueSendMsg(int code) {
        if(code == 1){
            showMsg("没有连接蓝牙");
            stopLoadingDialog();
        }
    }


    /**
     * 事件订阅者处理事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoonEvent(EvenBusBean evenBusBean) {
        String topic = evenBusBean.getTopic();
        String message = evenBusBean.getData();
        if (topic.equals(EvenBusEnum.EvenBus_BlueToothConnect.getEvenName())) {
            showMsg(message.equals("0")?"连接成功":message);
            stopLoadingDialog();
            if(message.equals("0")){
                startLoadingDialog();
                BlueTooth_ConnectHelper.getInstance().setCommState(50);
                sendBlueMessage.sendBlueMessage(meterControlPresenter.getElecMeterUserData(fatherNum, childNum, bsf_use));
            }
        } else if (topic.equals(EvenBusEnum.EvenBus_OnRetuenMessage.getEvenName())) {
            int state = BlueTooth_ConnectHelper.getInstance().getCommState();
            switch (state) {
                case 50:
                    stopLoadingDialog();
                    if (message.equals("1")) {
                        showMsg("刷新失败");
                    } else {
                        showTVData(message, bsf_use,tvShowYdl);
                        //BlueTooth_ConnectHelper.getInstance().setCommState(51);
                        //sendBlueMessage.sendBlueMessage(meterControlPresenter.getElecMeterUserData(fatherNum, childNum, bsf_dy));
                    }
                    break;
                case 51:
                    if (message.equals("1")) {
                        stopLoadingDialog();
                        showMsg("刷新失败");
                    } else {
                        showTVData(message, bsf_dy,tvShowDy);
                        BlueTooth_ConnectHelper.getInstance().setCommState(52);
                        sendBlueMessage.sendBlueMessage(meterControlPresenter.getElecMeterUserData(fatherNum, childNum, bsf_dl));
                    }
                    break;
                case 52:
                    if (message.equals("1")) {
                        stopLoadingDialog();
                        showMsg("刷新失败");
                    } else {
                        showTVData(message,bsf_dl, tvShowDl);
                        BlueTooth_ConnectHelper.getInstance().setCommState(53);
                        sendBlueMessage.sendBlueMessage(meterControlPresenter.getElecMeterUserData(fatherNum, childNum, bsf_gl));
                    }
                    break;
                case 53:
                    if (message.equals("1")) {
                        stopLoadingDialog();
                        showMsg("刷新失败");
                    } else {
                        showTVData(message,bsf_gl, tvShowGl);
                        BlueTooth_ConnectHelper.getInstance().setCommState(54);
                        sendBlueMessage.sendBlueMessage(meterControlPresenter.getElecMeterUserData(fatherNum, childNum, bsf_dwpl));
                    }
                    break;
                case 54:
                    if (message.equals("1")) {
                        stopLoadingDialog();
                        showMsg("刷新失败");
                    } else {
                        showTVData(message,bsf_dwpl, tvShowDwpl);
                        BlueTooth_ConnectHelper.getInstance().setCommState(55);
                        sendBlueMessage.sendBlueMessage(meterControlPresenter.getElecMeterUserData(fatherNum, childNum, bsf_glys));
                    }
                    break;
                case 55:
                    stopLoadingDialog();
                    if (message.equals("1")) {
                        showMsg("刷新失败");
                    } else {
                        showTVData(message,bsf_glys, tvShowGlys);
                    }
                    break;
                case 56:
                    stopLoadingDialog();
                    if (message.equals("0")) {
                        showMsg("合闸成功");
                    } else {
                        showMsg("合闸失败");
                        btnDeviceOpenClose.setChecked(false);
                    }
                    break;
                case 57:
                    stopLoadingDialog();
                    if (message.equals("0")) {
                        showMsg("跳闸成功");
                    } else {
                        showMsg("跳闸失败");
                        btnDeviceOpenClose.setChecked(true);
                    }
                    break;
            }
        }
    }

    //bd00112233445566 81 1c00 01b4 996900261118 68 996900261118 68 91 08 33333333 33333333 5216 c216
    //BD00232222121111 81 2C00 01B4 996900261118 68 996900261118 68 91 18 88883235 3333333348563333333333333232323683334333D5160E16
    private void showTVData(String str,String childBSF, TextView showView) {
        if (str.indexOf("BD") >= 0) {
            int stand_0 = str.indexOf("BD");
            String blueMsg = str.substring(stand_0, str.length()).toUpperCase();
            String control = blueMsg.substring(16, 18).toUpperCase();//控制字
            if (control.equals("81")) {
                String msgflag = blueMsg.substring(22, 26).toUpperCase();//标示符
                String childControl = blueMsg.substring(54, 56).toUpperCase();//645协议控制字
                switch (childControl) {
                    case "91":
                        String childFlag = blueMsg.substring(58, 66).toUpperCase();//645协议标示符
                        String data = blueMsg.substring(66, blueMsg.length() - 8).toUpperCase();//645协议数据域
                        String firstData = Udp_Help.get_645ToHex(data);
                        switch (childFlag){
                            case bsf_use://用电数据块
                                MyResult result = new MyResult(firstData);
                                tvShowYdl.setText(result.getZdn());
                                tvShowDy.setText(result.getDy());
                                tvShowDl.setText(result.getDl());
                                tvShowGl.setText(result.getGl());
                                tvShowDwpl.setText(result.getPl());
                                tvShowGlys.setText(result.getGlys());
                                break;
                            case bsf_ydl://用电量
                                showView.setText(firstData.substring(0, 6) + "." + firstData.substring(6, 8));
                                break;
                            case bsf_dy://电压
                                showView.setText(firstData.substring(0, 3) + "." + firstData.substring(3, 4));
                                break;
                            case bsf_dl://电流
                                showView.setText(firstData.substring(0, 3) + "." + firstData.substring(3, 6));
                                break;
                            case bsf_gl://功率
                                showView.setText(firstData.substring(0, 2) + "." + firstData.substring(2, 6));
                                break;
                            case bsf_dwpl://电网频率
                                showView.setText(firstData.substring(0, 2) + "." + firstData.substring(2, 4));
                                break;
                            case bsf_glys://功率因数
                                showView.setText(firstData.substring(0, 1) + "." + firstData.substring(1, 4));
                                break;
                        }
                        break;
                    default:
                        showMsg("读取失败");
                        break;
                }
            } else {
                showMsg("读取失败");
            }
        }
    }
}
