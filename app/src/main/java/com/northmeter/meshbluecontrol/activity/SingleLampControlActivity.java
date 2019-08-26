package com.northmeter.meshbluecontrol.activity;

import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jmesh.blebase.state.BleDevice;
import com.northmeter.meshbluecontrol.I.I_ShowBlueSend;
import com.northmeter.meshbluecontrol.R;
import com.northmeter.meshbluecontrol.base.BaseActivity;
import com.northmeter.meshbluecontrol.bean.EvenBusBean;
import com.northmeter.meshbluecontrol.bluetooth.BlueTooth_ConnectHelper;
import com.northmeter.meshbluecontrol.bluetooth.SendBlueMessage;
import com.northmeter.meshbluecontrol.enumBean.EvenBusEnum;
import com.northmeter.meshbluecontrol.presenter.SingleLampControlPresenter;
import com.northmeter.meshbluecontrol.utils.MyResult;
import com.northmeter.meshbluecontrol.utils.Udp_Help;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dyd on 2019/4/9.
 * 单灯控制器
 */

public class SingleLampControlActivity extends BaseActivity implements I_ShowBlueSend{

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.btn_device_open_close)
    CheckBox btnDeviceOpenClose;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
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
    @BindView(R.id.iv_back_show)
    ImageView ivBackShow;

    private String childType, childName, childNum, fatherNum,faterMac;
    private SingleLampControlPresenter singleLampControlPresenter;
    private SendBlueMessage sendBlueMessage;

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
        btnDeviceOpenClose.setChecked(false);
        tvToolbarTitle.setText(childName);
        tvRightText.setText("刷新");
        ivBackShow.setImageDrawable(getResources().getDrawable(R.drawable.img_back_singlelamp));
    }

    @Override
    public void initData() {
        super.initData();
        singleLampControlPresenter = new SingleLampControlPresenter();
        sendBlueMessage = new SendBlueMessage(this);
        startLoadingDialog();
        if(getIntent().getBooleanExtra("refresh",false)){
            BlueTooth_ConnectHelper.getInstance().setCommState(40);
            sendBlueMessage.sendBlueMessage(singleLampControlPresenter.getReadData(fatherNum, childNum));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.btn_tb_back, R.id.btn_device_open_close, R.id.tv_right_text})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_tb_back:
                this.finish();
                break;
            case R.id.tv_right_text:
                startLoadingDialog();
                BlueTooth_ConnectHelper.getInstance().setCommState(40);
                sendBlueMessage.sendBlueMessage(singleLampControlPresenter.getReadData(fatherNum, childNum));
                break;
            case R.id.btn_device_open_close:
                startLoadingDialog();
                boolean ocFlag = false;
                if (btnDeviceOpenClose.isChecked()) {
                    BlueTooth_ConnectHelper.getInstance().setCommState(41);
                    ocFlag = true;
                } else {
                    BlueTooth_ConnectHelper.getInstance().setCommState(42);
                    ocFlag = false;
                }
                sendBlueMessage.sendBlueMessage(singleLampControlPresenter.getOpenOrCloseData(fatherNum, childNum, ocFlag));
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
                BlueTooth_ConnectHelper.getInstance().setCommState(40);
                sendBlueMessage.sendBlueMessage(singleLampControlPresenter.getReadData(fatherNum, childNum));
            }
        } else if (topic.equals(EvenBusEnum.EvenBus_OnRetuenMessage.getEvenName())) {
            int state = BlueTooth_ConnectHelper.getInstance().getCommState();
            switch (state) {
                case 40:
                    if(message.equals("1")){
                        showMsg("刷新失败");
                    }else{
                        showTVData(message);
                    }
                    break;
                case 41:
                    if (message.equals("0")) {
                        showMsg("合闸成功");
                    } else {
                        showMsg("操作失败");
                        btnDeviceOpenClose.setChecked(false);
                    }
                    break;
                case 42:
                    if (message.equals("0")) {
                        showMsg("跳闸成功");
                    } else {
                        showMsg("操作失败");
                        btnDeviceOpenClose.setChecked(true);
                    }
                    break;
            }
        }
    }

    //bd00112233445566 81 2c00 01b4 673900180918 68 673900180918 68 91 18 88883235 333333334655333333333333323232c97c334334 e716 8416
    //bd00112233445566 81 2c00 01b4 910215121703 68 910215121703 68 91 18 88883235 493333335555333333333333323232cc7c334333 0916 c316
    private void showTVData(String str) {
        if (str.indexOf("BD") >= 0 &&str.length()>=114) {
            int stand_0 = str.indexOf("BD");
            String blueMsg = str.substring(stand_0, str.length()).toUpperCase();
            String control = blueMsg.substring(16, 18).toUpperCase();//控制字
            if (control.equals("81")) {
                String msgflag = blueMsg.substring(22, 26).toUpperCase();//标示符
                String childControl = blueMsg.substring(54, 56).toUpperCase();//645协议控制字
                switch (childControl) {
                    case "91":
                        String childFlag = blueMsg.substring(58, 66).toUpperCase();//645协议标示符
                        if(childFlag.equals("88883235")){
                            String data = blueMsg.substring(66, blueMsg.length()-8).toUpperCase();//645协议数据域
                            String firstData = Udp_Help.get_645ToHex(data);
                            MyResult result = new MyResult(firstData);
                            tvShowYdl.setText(result.getZdn());
                            tvShowDy.setText(result.getDy());
                            tvShowDl.setText(result.getDl());
                            tvShowGl.setText(result.getGl());
                            tvShowDwpl.setText(result.getPl());
                            tvShowGlys.setText(result.getGlys());
                            if (result.getThzzt().equals("00")) {
                                btnDeviceOpenClose.setChecked(false);
                            } else {
                                btnDeviceOpenClose.setChecked(true);
                            }
                            break;
                        }else{
                            showMsg("数据异常");
                        }
                    default:
                        showMsg("读取失败");
                        break;
                }
            }else{
                showMsg("读取失败");
            }
        }
    }

}
