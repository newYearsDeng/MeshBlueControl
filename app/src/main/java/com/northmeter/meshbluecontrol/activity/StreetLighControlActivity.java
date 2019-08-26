package com.northmeter.meshbluecontrol.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.northmeter.meshbluecontrol.I.I_ShowBlueSend;
import com.northmeter.meshbluecontrol.R;
import com.northmeter.meshbluecontrol.base.BaseActivity;
import com.northmeter.meshbluecontrol.bean.EvenBusBean;
import com.northmeter.meshbluecontrol.bluetooth.BlueTooth_ConnectHelper;
import com.northmeter.meshbluecontrol.bluetooth.SendBlueMessage;
import com.northmeter.meshbluecontrol.enumBean.DevicesTypeEnum;
import com.northmeter.meshbluecontrol.enumBean.EvenBusEnum;
import com.northmeter.meshbluecontrol.presenter.StreetLighControlPresenter;
import com.northmeter.meshbluecontrol.utils.MyResult;
import com.northmeter.meshbluecontrol.utils.Udp_Help;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dyd on 2019/4/16.
 * 四路或者三路控制面板
 */

public class StreetLighControlActivity extends BaseActivity implements I_ShowBlueSend {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.checkbox_control_one)
    CheckBox checkboxControlOne;
    @BindView(R.id.checkbox_control_two)
    CheckBox checkboxControlTwo;
    @BindView(R.id.checkbox_control_three)
    CheckBox checkboxControlThree;
    @BindView(R.id.checkbox_control_four)
    CheckBox checkboxControlFour;
    @BindView(R.id.relativelayout_show)
    RelativeLayout relativelayoutShow;
    private StreetLighControlPresenter streetLighControlPresenter;
    private String childType, childName, childNum, fatherNum,faterMac;
    private SendBlueMessage sendBlueMessage;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_street_light_control;
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
        if (childType.equals(DevicesTypeEnum.Device_ThreeStreetLighControl.getType())) {
            relativelayoutShow.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void initData() {
        super.initData();
        sendBlueMessage = new SendBlueMessage(this);
        streetLighControlPresenter = new StreetLighControlPresenter(this);
        if(!getIntent().getBooleanExtra("refresh",false)){
            startLoadingDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.btn_tb_back, R.id.tv_right_text, R.id.checkbox_control_one, R.id.checkbox_control_two, R.id.checkbox_control_three, R.id.checkbox_control_four})
    public void onViewClicked(View view) {
        boolean openOrclose = false;
        String openOrcloseItem = "34";
        switch (view.getId()) {
            case R.id.btn_tb_back:
                this.finish();
                break;
            case R.id.tv_right_text:
                startLoadingDialog();
                BlueTooth_ConnectHelper.getInstance().setCommState(90);
                sendBlueMessage.sendBlueMessage(streetLighControlPresenter.getReadData(fatherNum,childNum));
                break;
            case R.id.checkbox_control_one:
                openOrclose = checkboxControlOne.isChecked();
                openOrcloseItem = "34";
                sendControl(openOrclose,openOrcloseItem);
                break;
            case R.id.checkbox_control_two:
                openOrclose = checkboxControlTwo.isChecked();
                openOrcloseItem = "35";
                sendControl(openOrclose,openOrcloseItem);
                break;
            case R.id.checkbox_control_three:
                openOrclose = checkboxControlThree.isChecked();
                openOrcloseItem = "37";
                sendControl(openOrclose,openOrcloseItem);
                break;
            case R.id.checkbox_control_four:
                openOrclose = checkboxControlFour.isChecked();
                openOrcloseItem = "3B";
                sendControl(openOrclose,openOrcloseItem);
                break;
        }
    }

    private void sendControl(boolean openOrclose,String openOrcloseItem){
        startLoadingDialog();
        BlueTooth_ConnectHelper.getInstance().setCommState(91);
        sendBlueMessage.sendBlueMessage(streetLighControlPresenter.getOpenOrCloseData(fatherNum,childNum,openOrclose,openOrcloseItem));
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
        } else if (topic.equals(EvenBusEnum.EvenBus_OnRetuenMessage.getEvenName())) {
            int state = BlueTooth_ConnectHelper.getInstance().getCommState();
            switch (state) {
                case 90:
                    if (message.equals("1")) {
                        showMsg("刷新失败");
                    } else {
                        message_dialog_show(message);
                    }
                    break;
                case 91:
                    showMsg(message.equals("1") ? "操作失败" : "操作成功");
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

}
