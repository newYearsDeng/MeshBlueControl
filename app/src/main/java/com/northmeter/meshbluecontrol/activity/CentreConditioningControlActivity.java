package com.northmeter.meshbluecontrol.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.northmeter.meshbluecontrol.I.I_ShowBlueSend;
import com.northmeter.meshbluecontrol.R;
import com.northmeter.meshbluecontrol.adapter.CommonAdapter;
import com.northmeter.meshbluecontrol.adapter.ViewHolder;
import com.northmeter.meshbluecontrol.base.BaseActivity;
import com.northmeter.meshbluecontrol.bean.EvenBusBean;
import com.northmeter.meshbluecontrol.bluetooth.BlueTooth_ConnectHelper;
import com.northmeter.meshbluecontrol.bluetooth.SendBlueMessage;
import com.northmeter.meshbluecontrol.control.GreeFrequency;
import com.northmeter.meshbluecontrol.control.GreeFrequencyComplement;
import com.northmeter.meshbluecontrol.control.GreeKTHW;
import com.northmeter.meshbluecontrol.control.HuaLingHW;
import com.northmeter.meshbluecontrol.enumBean.EvenBusEnum;
import com.northmeter.meshbluecontrol.presenter.AirConditioningControlPresenter;
import com.northmeter.meshbluecontrol.utils.MyResult;
import com.northmeter.meshbluecontrol.utils.Udp_Help;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dyd on 2019/4/9.
 * 中央空调控制
 */

public class CentreConditioningControlActivity extends BaseActivity implements I_ShowBlueSend {
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.tv_show_temperature)
    TextView tvShowTemperature;
    @BindView(R.id.tv_show_speed)
    TextView tvShowSpeed;
    @BindView(R.id.btn_device_open_close)
    CheckBox btnDeviceOpenClose;
    private String childType, childName, childNum, fatherNum,faterMac;

    private SendBlueMessage sendBlueMessage;
    private AirConditioningControlPresenter  controlPresenter;
    private CommonAdapter commonAdapter;
    private ListView listview;
    private int temputer = 24;
    private String speedModel = "B4";//01  02  03  81
    private String openModel = "DD";//远程开关（开0xaa ；关 0x55） dd  88

    @Override
    protected int getLayoutId() {
        return R.layout.activity_centre_conditioning_control;
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
        tvRightText.setText("用电信息");
    }

    @Override
    public void initData() {
        super.initData();
        sendBlueMessage = new SendBlueMessage(this);
        controlPresenter = new AirConditioningControlPresenter(this);
        startLoadingDialog();
        if(getIntent().getBooleanExtra("refresh",false)){
            BlueTooth_ConnectHelper.getInstance().setCommState(100);
            sendBlueMessage.sendBlueMessage(controlPresenter.getReadData(fatherNum,childNum));
        }
    }

    @OnClick({R.id.btn_tb_back, R.id.tv_right_text,R.id.ll_speed_low, R.id.ll_speed_centre, R.id.ll_speed_high,R.id.ll_speed_auto, R.id.iv_temp_down, R.id.iv_temp_up, R.id.btn_device_open_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_tb_back:
                this.finish();
                break;
            case R.id.tv_right_text://刷新数据
                startLoadingDialog();
                BlueTooth_ConnectHelper.getInstance().setCommState(100);
                sendBlueMessage.sendBlueMessage(controlPresenter.getReadData(fatherNum,childNum));
                break;
            case R.id.ll_speed_low:
                speedModel = "34";
                tvShowSpeed.setText("低风速");
                send();
                break;
            case R.id.ll_speed_centre:
                speedModel = "35";
                tvShowSpeed.setText("中风速");
                send();
                break;
            case R.id.ll_speed_high:
                tvShowSpeed.setText("高风速");
                speedModel = "36";
                send();
                break;
            case R.id.ll_speed_auto:
                tvShowSpeed.setText("自动风");
                speedModel = "B4";
                send();
                break;
            case R.id.iv_temp_down:
                temputer=Integer.parseInt(tvShowTemperature.getText().toString());
                temputer=temputer-1;
                if(temputer<16){
                    temputer=31;
                }
                tvShowTemperature.setText(String.valueOf(temputer));
                send();
                break;
            case R.id.iv_temp_up:
                temputer=Integer.parseInt(tvShowTemperature.getText().toString());
                temputer=temputer+1;
                if(temputer>31){
                    temputer=16;
                }
                tvShowTemperature.setText(String.valueOf(temputer));
                send();
                break;
            case R.id.btn_device_open_close:
                if (btnDeviceOpenClose.isChecked()){
                    openModel = "DD";
                }else{
                    openModel = "88";
                }
                send();
                break;
        }
    }

    private void send(){
        startLoadingDialog();
        BlueTooth_ConnectHelper.getInstance().setCommState(101);
        sendBlueMessage.sendBlueMessage(controlPresenter.getCentreControlData(fatherNum,childNum,openModel,speedModel,temputer));
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
                BlueTooth_ConnectHelper.getInstance().setCommState(100);
                sendBlueMessage.sendBlueMessage(controlPresenter.getReadData(fatherNum,childNum));
            }
        } else if (topic.equals(EvenBusEnum.EvenBus_OnRetuenMessage.getEvenName())) {
            int state = BlueTooth_ConnectHelper.getInstance().getCommState();
            switch (state) {
                case 100:
                    if(message.equals("1")){
                        showMsg("刷新失败");
                    }else{
                        message_dialog_show(message);
                    }
                    break;
                case 101://bd00112233445566 84 1800 01b4 010001111820 6801000111182068 8f 04 343342ef 4616a316
                    if(message.equals("1")){
                        showMsg("发送失败");
                    }else{
                        showMsg("发送成功");
                    }
                    break;
            }
        }
    }




    public void message_dialog_show(String str){
        final AlertDialog dialogSex = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialog)).create();
        dialogSex.show();
        Window window = dialogSex.getWindow();
        window.setContentView(R.layout.dialog_layout);
        dialogSex.setCanceledOnTouchOutside(true);
        dialogSex.setCancelable(true);
        // 在此设置显示动画
        window.setWindowAnimations(R.style.AnimBottom_Dialog);
        TextView tvShowYdl = (TextView) window.findViewById(R.id.tv_show_ydl);
        TextView tvShowDy = (TextView) window.findViewById(R.id.tv_show_dy);
        TextView tvShowDl = (TextView) window.findViewById(R.id.tv_show_dl);
        TextView tvShowGl = (TextView) window.findViewById(R.id.tv_show_gl);
        TextView tvShowDwpl = (TextView) window.findViewById(R.id.tv_show_dwpl);
        TextView tvShowGlys = (TextView) window.findViewById(R.id.tv_show_glys);
        dialogSex.show();
        window.findViewById(R.id.relativeLayout2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialogSex.cancel();
            }
        });

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
                        if (childFlag.equals("88883235")) {
                            String data = blueMsg.substring(66, blueMsg.length() - 8).toUpperCase();//645协议数据域
                            String firstData = Udp_Help.get_645ToHex(data);
                            MyResult result = new MyResult(firstData);
                            tvShowYdl.setText(result.getZdn()+"kWh");
                            tvShowDy.setText(result.getDy()+"V");
                            tvShowDl.setText(result.getDl()+"A");
                            tvShowGl.setText(result.getGl()+"kW");
                            tvShowDwpl.setText(result.getPl()+"Hz");
                            tvShowGlys.setText(result.getGlys());
                            if(result.getThzzt().equals("00")){
                                btnDeviceOpenClose.setChecked(true);
                                openModel = "DD";
                            } else{
                                btnDeviceOpenClose.setChecked(false);
                                openModel = "88";
                            }
                        } else {
                            showMsg("数据异常");
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

    @Override
    public void showBlueSendMsg(int code) {
        if(code==1){
            showMsg("没有连接蓝牙");
            stopLoadingDialog();
        }
    }

}
