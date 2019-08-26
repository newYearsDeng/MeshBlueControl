package com.northmeter.meshbluecontrol.activity;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
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
 * 空调控制
 */

public class AirConditioningControlActivity extends BaseActivity implements I_ShowBlueSend {
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.tv_show_temperature)
    TextView tvShowTemperature;
    @BindView(R.id.tv_show_model)
    TextView tvShowModel;
    @BindView(R.id.tv_show_speed)
    TextView tvShowSpeed;
    @BindView(R.id.tv_show_wind_dire)
    TextView tvShowWindDire;
    @BindView(R.id.btn_device_open_close)
    CheckBox btnDeviceOpenClose;
    private String childType, childName, childNum, fatherNum,faterMac;
    private int temputer = 24;
    private SendBlueMessage sendBlueMessage;
    private AirConditioningControlPresenter  controlPresenter;
    private CommonAdapter commonAdapter;
    private ListView listview;

    private String[] modelList = {"制冷","自动","抽湿","送风","制热"};
    private String[] speedList = {"自动","一级","二级","三级"};
    private String[] leftRightList = {"左右","左右关"};
    private String[] upDownList = {"上下","上下关"};
    private int model_id=0,speed_id=0,onoff_id=1,leftright_id=0,updown_id=0;
    private String air_version="0003_1";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_air_conditioning_control;
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
            BlueTooth_ConnectHelper.getInstance().setCommState(70);
            sendBlueMessage.sendBlueMessage(controlPresenter.getReadData(fatherNum,childNum));
        }
    }

    @OnClick({R.id.btn_tb_back, R.id.tv_right_text,R.id.ll_change_model, R.id.ll_change_speed, R.id.ll_change_wind_dire, R.id.iv_temp_down, R.id.iv_temp_up, R.id.btn_device_open_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_tb_back:
                this.finish();
                break;
            case R.id.tv_right_text://刷新数据
                startLoadingDialog();
                BlueTooth_ConnectHelper.getInstance().setCommState(70);
                sendBlueMessage.sendBlueMessage(controlPresenter.getReadData(fatherNum,childNum));
                break;
            case R.id.ll_change_model://模式选择
                control_dialog_show(0);
                break;
            case R.id.ll_change_speed://风速选择
                control_dialog_show(1);
                break;
            case R.id.ll_change_wind_dire://风向选择
                control_dialog_show(2);
                break;
            case R.id.iv_temp_down:
                temputer=Integer.parseInt(tvShowTemperature.getText().toString());
                temputer=temputer-1;
                if(temputer<16){
                    temputer=31;
                }
                tvShowTemperature.setText(String.valueOf(temputer));
                BlueTooth_ConnectHelper.getInstance().setCommState(71);
                sendBlueMessage.sendBlueMessage(controlPresenter.getControlData(fatherNum,childNum,sendAirControl()));
                break;
            case R.id.iv_temp_up:
                temputer=Integer.parseInt(tvShowTemperature.getText().toString());
                temputer=temputer+1;
                if(temputer>31){
                    temputer=16;
                }
                tvShowTemperature.setText(String.valueOf(temputer));
                BlueTooth_ConnectHelper.getInstance().setCommState(71);
                sendBlueMessage.sendBlueMessage(controlPresenter.getControlData(fatherNum,childNum,sendAirControl()));
                break;
            case R.id.btn_device_open_close:
                BlueTooth_ConnectHelper.getInstance().setCommState(71);
                if (btnDeviceOpenClose.isChecked()){
                    onoff_id = 0;
                }else{
                    onoff_id = 1;
                }
                startLoadingDialog();
                sendBlueMessage.sendBlueMessage(controlPresenter.getControlData(fatherNum,childNum,sendAirControl()));
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
                BlueTooth_ConnectHelper.getInstance().setCommState(70);
                sendBlueMessage.sendBlueMessage(controlPresenter.getReadData(fatherNum,childNum));
            }
        } else if (topic.equals(EvenBusEnum.EvenBus_OnRetuenMessage.getEvenName())) {
            int state = BlueTooth_ConnectHelper.getInstance().getCommState();
            switch (state) {
                case 70:
                    if(message.equals("1")){
                        showMsg("刷新失败");
                    }else{
                        message_dialog_show(message);
                    }
                    break;
                case 71://bd00112233445566 84 1800 01b4 010001111820 6801000111182068 8f 04 343342ef 4616a316
                    if(message.equals("1")){
                        showMsg("发送失败");
                    }else{
                        showMsg("发送成功");
                    }
                    break;
            }
        }
    }


    public void control_dialog_show(int showState) {
        final AlertDialog dialogSex = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialog)).create();
        dialogSex.show();
        Window window = dialogSex.getWindow();
        window.setContentView(R.layout.dialog_air_control);

        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(layoutParams);

        dialogSex.setCanceledOnTouchOutside(true);
        dialogSex.setCancelable(true);
        window.setWindowAnimations(R.style.AnimBottom_Dialog);

        TextView tv_toolbar_title = window.findViewById(R.id.tv_toolbar_title);

        window.findViewById(R.id.btn_dialog_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSex.cancel();
            }
        });

        window.findViewById(R.id.dialog_layout).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogSex.cancel();
            }
        });

        window.findViewById(R.id.btn_dialog_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSex.cancel();
            }
        });

        listview = window.findViewById(R.id.listview);

        switch (showState) {
            case 0:
                final List<String> modelDatas = new ArrayList();
                modelDatas.add("制冷");
                modelDatas.add("自动");
                modelDatas.add("抽湿");
                modelDatas.add("送风");
                modelDatas.add("制热");
                commonAdapter = new CommonAdapter<String>(this, modelDatas, R.layout.item_dialog_air_control) {
                    @Override
                    public void convert(ViewHolder helper, String item) {
                        helper.getTextViewSet(R.id.tv_choose_name, item);
                    }
                };
                listview.setAdapter(commonAdapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        tvShowModel.setText(modelDatas.get(position));
                        model_id = position;
                        dialogSex.cancel();
                        startLoadingDialog();
                        BlueTooth_ConnectHelper.getInstance().setCommState(71);
                        sendBlueMessage.sendBlueMessage(controlPresenter.getControlData(fatherNum,childNum,sendAirControl()));
                    }
                });
                break;

            case 1:
                final List<String> speedsDatas = new ArrayList();
                speedsDatas.add("自动");
                speedsDatas.add("低风");
                speedsDatas.add("中风");
                speedsDatas.add("高风");
                commonAdapter = new CommonAdapter<String>(this, speedsDatas, R.layout.item_dialog_air_control) {
                    @Override
                    public void convert(ViewHolder helper, String item) {
                        helper.getTextViewSet(R.id.tv_choose_name, item);
                    }
                };
                listview.setAdapter(commonAdapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        tvShowSpeed.setText(speedsDatas.get(position));
                        speed_id = position;
                        dialogSex.cancel();
                        startLoadingDialog();
                        BlueTooth_ConnectHelper.getInstance().setCommState(71);
                        sendBlueMessage.sendBlueMessage(controlPresenter.getControlData(fatherNum,childNum,sendAirControl()));
                    }
                });
                break;

            case 2:
                final List<String> windDireDatas = new ArrayList();
                windDireDatas.add("上下扫风");
                windDireDatas.add("关闭上下扫风");
                windDireDatas.add("左右扫风");
                windDireDatas.add("关闭左右扫风");
                commonAdapter = new CommonAdapter<String>(this, windDireDatas, R.layout.item_dialog_air_control) {
                    @Override
                    public void convert(ViewHolder helper, String item) {
                        helper.getTextViewSet(R.id.tv_choose_name, item);
                    }
                };
                listview.setAdapter(commonAdapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        tvShowWindDire.setText(windDireDatas.get(position));
                        switch(position){
                            case 0:
                                leftright_id = 0;
                                break;
                            case 1:
                                leftright_id = 1;
                                break;
                            case 2:
                                updown_id = 0;
                                break;
                            case 3:
                                updown_id = 1;
                                break;
                        }
                        dialogSex.cancel();
                        startLoadingDialog();
                        BlueTooth_ConnectHelper.getInstance().setCommState(71);
                        sendBlueMessage.sendBlueMessage(controlPresenter.getControlData(fatherNum,childNum,sendAirControl()));
                    }
                });
                break;
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
                            if(Double.valueOf(result.getGl())<2){
                                btnDeviceOpenClose.setChecked(false);
                                onoff_id = 1;
                            } else{
                                btnDeviceOpenClose.setChecked(true);
                                onoff_id = 0;
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


    public String sendAirControl(){
        String hwmStr= "";
        String kaiguan = "开";
        if(onoff_id==0){
            kaiguan="开";
        }else{
            kaiguan="关";
        }
        String hwm_zl_1=kaiguan+","+modelList[model_id]+","+temputer+","+speedList[speed_id]+","+upDownList[updown_id]+","+leftRightList[leftright_id];
        if(air_version.equals("0003")){
            hwmStr =get_reverse_String(GreeKTHW.getKTHWM("GREE",hwm_zl_1));
            System.out.println("hwmStr: "+hwmStr);
        }else if(air_version.equals("0003_1")){
            if(modelList[model_id].equals("制冷")||modelList[model_id].equals("送风")){
                hwmStr = GreeFrequency.getGreeFrequency(hwm_zl_1);
            }else{
                hwmStr = GreeFrequencyComplement.getGreeFrequencyComplement(hwm_zl_1);
            }
            System.out.println("hwmStr变频: "+hwmStr);
        }else if(air_version.equals("0003_2")){
            hwmStr = HuaLingHW.getHuaLinHw(hwm_zl_1);
            System.out.println("hwmStr: "+hwmStr);
        }

        return hwmStr;
    }

    /**字符串进行字节反向*/
    private static String get_reverse_String(String str){
        StringBuffer strbuf = new StringBuffer();
        for(int i = str.length()/2;i > 0;i--){
            strbuf.append(str.substring(i*2-2,i*2));
        }

        return strbuf.toString();
    }


}
