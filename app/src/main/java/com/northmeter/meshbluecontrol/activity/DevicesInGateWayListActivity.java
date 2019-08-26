package com.northmeter.meshbluecontrol.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jmesh.blebase.base.BleManager;
import com.northmeter.meshbluecontrol.I.I_ShowBlueSend;
import com.northmeter.meshbluecontrol.I.I_ShowDevicesInGateWay;
import com.northmeter.meshbluecontrol.R;
import com.northmeter.meshbluecontrol.adapter.CommonAdapter;
import com.northmeter.meshbluecontrol.adapter.ViewHolder;
import com.northmeter.meshbluecontrol.base.BaseActivity;
import com.northmeter.meshbluecontrol.bean.BleBlueToothBean;
import com.northmeter.meshbluecontrol.bean.DBBlueToothBean;
import com.northmeter.meshbluecontrol.bean.EvenBusBean;
import com.northmeter.meshbluecontrol.bluetooth.BleConnect_InstanceHelper;
import com.northmeter.meshbluecontrol.bluetooth.BlueTooth_ConnectHelper;
import com.northmeter.meshbluecontrol.bluetooth.SendBlueMessage;
import com.northmeter.meshbluecontrol.bluetooth.bluetooth.blueActivity.DeviceListActivity;
import com.northmeter.meshbluecontrol.bluetooth.bluetooth.tools.BluetoothScanClient;
import com.northmeter.meshbluecontrol.enumBean.DevicesTypeEnum;
import com.northmeter.meshbluecontrol.enumBean.EvenBusEnum;
import com.northmeter.meshbluecontrol.presenter.DevicesInGateWayListPresenter;
import com.northmeter.meshbluecontrol.sqlite.BlueDeviceHelper;
import com.northmeter.meshbluecontrol.utils.Udp_Help;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dyd on 2019/4/4.
 * 网关中设备列表页面，添加和删除
 */

public class DevicesInGateWayListActivity extends BaseActivity implements BluetoothAdapter.LeScanCallback, I_ShowBlueSend, I_ShowDevicesInGateWay {
    private final static int FIND_BLUETOOTH_CODE = 1;
    private static final int REQUEST_OPENBLUERESULT = 201;
    private static final int REQUEST_LOCATIONARESULT = 2010;
    String TAG = getClass().getSimpleName();

    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.tv_device_title)
    TextView tvDeviceTitle;
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.btn_device_del_sure)
    Button btnDeviceDelSure;
    @BindView(R.id.iv_device_del)
    ImageView ivDeviceDel;
    @BindView(R.id.tv_empty)
    ImageView tvEmpty;

    private CommonAdapter commonAdapter;
    private List<BleBlueToothBean> addDeviceList;
    private List<DBBlueToothBean> datas = new ArrayList<>();
    private String fatherMac, fatherNum;
    private BlueDeviceHelper blueDeviceHelper;
    private BluetoothScanClient mScanClient;
    private boolean showOrHide = false;//显示或隐藏设备的

    private DevicesInGateWayListPresenter devicesInGateWayListPresenter;
    private SendBlueMessage sendBlueMessage;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_devices_in_gateway;
    }

    @Override
    public void initIntentData() {
        super.initIntentData();
        fatherMac = getIntent().getStringExtra("fatherMac");//主节点蓝牙mac
        fatherNum = getIntent().getStringExtra("fatherNum");//主节点的表地址
        System.out.println("fatherNum" + fatherNum);
    }

    @Override
    public void setTitle() {
        super.setTitle();
        tvDeviceTitle.setText(fatherMac);
        tvToolbarTitle.setText(fatherNum);
        tvRightText.setText("取消");
        tvRightText.setVisibility(View.GONE);
        ivDeviceDel.setVisibility(View.VISIBLE);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void initData() {
        super.initData();
        blueDeviceHelper = new BlueDeviceHelper(this);
        devicesInGateWayListPresenter = new DevicesInGateWayListPresenter(this);
        sendBlueMessage = new SendBlueMessage(this);
        datas.addAll(blueDeviceHelper.queryByCondit("fatherMac", fatherMac, "and fatherMac<>Mac"));
        tvEmpty.setVisibility(datas.isEmpty()?View.VISIBLE:View.GONE);
        initListView();

        startLoadingDialog();
        BleConnect_InstanceHelper bleConnect = BleConnect_InstanceHelper.getInstance();
        bleConnect.setMacStr(fatherMac);
        bleConnect.connecedDevice();

        mScanClient = BluetoothScanClient.getInstance(this, this);
//        if (!mScanClient.isBluetoothOpen()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_OPENBLUERESULT);
//        } else {
//            startLoadingDialog();
//            mScanClient.startScan();
//        }
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.w(TAG, "onScanResult" + Udp_Help.bytesToHexString(scanRecord));
        Log.w(TAG, "onLeScan: device == " + device.getAddress() + " == " + device.getName());
        String recordStr = Udp_Help.bytesToHexString(scanRecord).toUpperCase();
        String recordState = recordStr.substring(4, 6);
        if (device.getAddress().equals(fatherMac)) {
            mScanClient.stopScan();
            BlueTooth_ConnectHelper.getInstance().blueToothConnect(device);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mScanClient != null)
        mScanClient.stopScan();
        mScanClient.destroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        listview.setAdapter(commonAdapter);
        commonAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_device_add, R.id.btn_tb_back, R.id.iv_device_del, R.id.tv_right_text, R.id.btn_device_del_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_device_add://添加设备
                Intent intent = new Intent(this, DeviceListActivity.class);
                intent.putExtra("scanType", "01");//表示自搜索其他子节点
                startActivityForResult(intent, DeviceListActivity.REQUEST_DEVICE);
                break;
            case R.id.btn_tb_back:
                this.finish();
                break;
            case R.id.iv_device_del://进入删除设备功能
                ivDeviceDel.setVisibility(View.GONE);
                tvRightText.setVisibility(View.VISIBLE);
                btnDeviceDelSure.setVisibility(View.VISIBLE);
                showOrHide = true;
                commonAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_right_text://退出删除功能
                ivDeviceDel.setVisibility(View.VISIBLE);
                tvRightText.setVisibility(View.GONE);
                btnDeviceDelSure.setVisibility(View.GONE);
                showOrHide = false;
                commonAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_device_del_sure:
                //devicesInGateWayListPresenter.deleteDevice(datas);
                startLoadingDialog();
                BlueTooth_ConnectHelper.getInstance().setCommState(22);
                sendBlueMessage.sendBlueMessage(devicesInGateWayListPresenter.delleteRecord(fatherNum,datas));
                break;
        }
    }

    private void initListView() {
        commonAdapter = new CommonAdapter<DBBlueToothBean>(this, datas, R.layout.item_device_in_gateway_list) {
            @Override
            public void convert(ViewHolder helper, final DBBlueToothBean item) {
                switch (DevicesTypeEnum.getDevicesTypeEnum(item.getType())) {
                    case Device_GateWay://网关模块
                        helper.getImageViewSet(R.id.iv_device_type, R.drawable.img_device_gateway);
                        break;
                    case Device_Termial://终端模块
//                    case Device_GasAlarm://燃气报警器
//                    case Device_WindowOpener://开窗器
//                    case Device_Manipulator://机械手
                    case Device_CentralAirConditioner://中央空调器
                        helper.getImageViewSet(R.id.iv_device_type, R.drawable.img_device_airconditioner);
                        break;
                    case Device_MobileSocket://移动式插座表
                    case Device_WallMountedSocket://墙挂式插座表
                        helper.getImageViewSet(R.id.iv_device_type, R.drawable.img_device_socket);
                        break;
                    case Device_AirConditioning://空调控制器
                        helper.getImageViewSet(R.id.iv_device_type, R.drawable.img_device_airconditioner);
                        break;
                    case Device_ElecMeter://电表
                    case Device_GuideMeter://导轨表
                        helper.getImageViewSet(R.id.iv_device_type, R.drawable.img_device_elemeter);
                        break;
                    case Device_WaterMeter://水表
                        helper.getImageViewSet(R.id.iv_device_type, R.drawable.img_device_watermeter);
                        break;
                    case Device_SingleLampControl://单灯控制器
                        helper.getImageViewSet(R.id.iv_device_type, R.drawable.img_device_light);
                        break;
                    case Device_FourStreetLightControl://四路灯控
                    case Device_ThreeStreetLighControl://三路灯控
                        helper.getImageViewSet(R.id.iv_device_type, R.drawable.img_device_light);
                        break;
                    case Device_ScenarioPanel://情景面板
                        helper.getImageViewSet(R.id.iv_device_type, R.drawable.img_device_light);
                        break;
                    default:
                        break;

                }
                if (showOrHide) {
                    helper.getView(R.id.btn_device_check).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.btn_device_check).setVisibility(View.GONE);
                }
                helper.getCheckViewSet(R.id.btn_device_check,item.isCheck());
                helper.getTextViewSet(R.id.tv_device_name, item.getName());
                helper.getTextViewSet(R.id.tv_device_num, item.getTableNum());
                if (item.isOnline()) {
                    helper.getTextViewSet(R.id.tv_online_info, "已入网");
                } else {
                    helper.getTextViewSet(R.id.tv_online_info, "未入网");
                }
                helper.getView(R.id.btn_device_check).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.isCheck()) {
                            item.setCheck(false);
                        } else {
                            item.setCheck(true);
                        }
                        commonAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        listview.setAdapter(commonAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //if (BlueTooth_ConnectHelper.getInstance().isBooleanConnected()) {
                if (BleManager.getInstance().getConnectedDeviceByMac(fatherMac).getDevice()!=null) {
                    DBBlueToothBean dataItem = datas.get(position);
                    Intent goIntent = new Intent();
                    goIntent.putExtra("refresh",true);//跳转后是否发送抄表命令，如果从网关进入则需要
                    goIntent.putExtra("childType", dataItem.getType());
                    goIntent.putExtra("childName", dataItem.getName());
                    goIntent.putExtra("childNum", dataItem.getTableNum());
                    goIntent.putExtra("fatherNum", fatherNum);
                    goIntent.putExtra("fatherMac", fatherMac);
                    switch (DevicesTypeEnum.getDevicesTypeEnum(dataItem.getType())) {
                        case Device_WallMountedSocket://墙挂式插座表
                        case Device_MobileSocket://移动式插座表
                            goActivity(Socket_ControlActivity.class, goIntent);
                            break;
                        case Device_AirConditioning://空调控制器
                            goActivity(AirConditioningControlActivity.class, goIntent);
                            break;
                        case Device_CentralAirConditioner://中央空调器
                            goActivity(CentreConditioningControlActivity.class, goIntent);
                            break;
                        case Device_GuideMeter://导轨表电表
                            goActivity(GuidMeterControlActivity.class, goIntent);
                            break;
                        case Device_SingleLampControl://单灯控制器
                            goActivity(SingleLampControlActivity.class, goIntent);
                            break;
                        case Device_WaterMeter://蓝牙水表
                            goActivity(WaterMeterControlActivity.class, goIntent);
                            break;
                        case Device_ThreeStreetLighControl://三路灯控
                            goActivity(StreetLighControlActivity.class, goIntent);
                            break;
                        case Device_FourStreetLightControl://四路灯控
                            goActivity(StreetLighControlActivity.class, goIntent);
                            break;
                        case Device_ScenarioPanel://情景面板
                            goActivity(ScenarioPanelControlActivity.class, goIntent);
                            break;

                    }
                } else {
                    showMsg("未连接蓝牙");
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FIND_BLUETOOTH_CODE://低功耗蓝牙
                if (resultCode == RESULT_OK) {
                    addDeviceList = (List<BleBlueToothBean>) data.getSerializableExtra(DeviceListActivity.DATA_DEVICE);
                    //搜索返回的子节点在这里添加档案到主节点
                    startLoadingDialog();
                    BlueTooth_ConnectHelper.getInstance().setCommState(20);
                    sendBlueMessage.sendBlueMessage(devicesInGateWayListPresenter.getAddRecordData(fatherNum, addDeviceList));
                }
                break;
            case REQUEST_OPENBLUERESULT:
                if (resultCode == RESULT_OK) {
                    startLoadingDialog();
                    mScanClient.startScan();
                    return;
                }
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
            if (message.equals("0")) {
                startLoadingDialog();
                BlueTooth_ConnectHelper.getInstance().setCommState(21);
                sendBlueMessage.sendBlueMessage(devicesInGateWayListPresenter.readOnlineRecord(fatherNum));//读取路由信息
            }
        } else if (topic.equals(EvenBusEnum.EvenBus_OnRetuenMessage.getEvenName())) {
            int state = BlueTooth_ConnectHelper.getInstance().getCommState();
            switch (state) {
                case 20://添加档案成功
                    if (message.equals("0")) {
                        List<DBBlueToothBean> blueList = new ArrayList<>();
                        for(BleBlueToothBean device : addDeviceList){
                            blueList.add(new DBBlueToothBean(device.getType(), device.getName(),
                                    device.getTableNum(), device.getAddress(),fatherNum,fatherMac, false, true));
                        }

                        blueDeviceHelper.insert(blueList);
                        datas.clear();
                        datas.addAll(blueDeviceHelper.queryByCondit("fatherMac", fatherMac, "and fatherMac<>Mac"));
                        commonAdapter.notifyDataSetChanged();

                        tvEmpty.setVisibility(datas.isEmpty()?View.VISIBLE:View.GONE);

                    }else{
                        showMsg("添加失败");
                    }
                    break;
                case 21://bd00112233445566 81 0e00 02b3 0200 02 673900180918(长地址) 1900(短地址)01 673900180918 1900 01 9e16
                    showMsg(message);
                    if (message.indexOf("BD") >= 0) {
                        int stand_0 = message.indexOf("BD");
                        String blueMsg = message.substring(stand_0, message.length()).toUpperCase();
                        String control = blueMsg.substring(16, 18).toUpperCase();//控制字
                        if (control.equals("81")) {
                            int total = Integer.valueOf(blueMsg.substring(30, 32).toUpperCase(), 16);//回复已入网节点个数
                            String childInfo = message.substring(32, message.length() - 4);
                            for (int i = 0; i < total; i++) {
                                String childNum = Udp_Help.reverseRst(childInfo.substring(i * 18, i * 18 + 12));
                                for (DBBlueToothBean blueitem : datas) {
                                    if (blueitem.getTableNum().equals(childNum)) {
                                        blueitem.setOnline(true);
                                        commonAdapter.notifyDataSetChanged();
                                        System.out.println("已入网");
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 22:
                    if (message.equals("0")) {
                        devicesInGateWayListPresenter.deleteDevice(datas);
                    }else{
                        showMsg("删除失败");
                    }
                    break;
            }

        }
    }

    @Override
    public void showBlueSendMsg(int code) {
        if(code==1){
            showMsg("没有连接蓝牙");
        }
    }

    @Override
    public void showData() {

    }

    @Override
    public void returnMessage(String message) {
        if (message.equals("01")) {
            commonAdapter.notifyDataSetChanged();
        }
    }
}
