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

import com.northmeter.meshbluecontrol.I.I_ShowDevicesMain;
import com.northmeter.meshbluecontrol.R;
import com.northmeter.meshbluecontrol.adapter.CommonAdapter;
import com.northmeter.meshbluecontrol.adapter.ViewHolder;
import com.northmeter.meshbluecontrol.base.BaseActivity;
import com.northmeter.meshbluecontrol.bean.BleBlueToothBean;
import com.northmeter.meshbluecontrol.bean.DBBlueToothBean;
import com.northmeter.meshbluecontrol.bean.EvenBusBean;
import com.northmeter.meshbluecontrol.bluetooth.BleConnect_InstanceHelper;
import com.northmeter.meshbluecontrol.bluetooth.BlueTooth_ConnectHelper;
import com.northmeter.meshbluecontrol.bluetooth.bluetooth.blueActivity.DeviceListActivity;
import com.northmeter.meshbluecontrol.bluetooth.bluetooth.tools.BluetoothScanClient;
import com.northmeter.meshbluecontrol.enumBean.DevicesTypeEnum;
import com.northmeter.meshbluecontrol.enumBean.DevicesTypeManageEnum;
import com.northmeter.meshbluecontrol.enumBean.EvenBusEnum;
import com.northmeter.meshbluecontrol.presenter.DevicesMainPresenter;
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
 * 网关设备列表页面，添加和删除
 */

public class DevicesMainActivity extends BaseActivity implements BluetoothAdapter.LeScanCallback, I_ShowDevicesMain {
    private final static int FIND_BLUETOOTH_CODE = 1;
    private static final int REQUEST_OPENBLUERESULT = 201;
    private static final int REQUEST_LOCATIONARESULT = 2010;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.btn_device_del_sure)
    Button btnDeviceDelSure;
    @BindView(R.id.iv_device_delete)
    ImageView ivDeviceDel;
    @BindView(R.id.tv_device_cancle)
    TextView tvDeviceCancle;
    @BindView(R.id.tv_device_title)
    TextView tvDeviceTitle;
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.iv_device_add)
    ImageView ivDeviceAdd;

    private CommonAdapter commonAdapter;
    private List<DBBlueToothBean> datas = new ArrayList<>();
    private BlueDeviceHelper blueDeviceHelper;
    private boolean showOrHide = false;//显示或隐藏设备的
    private DevicesMainPresenter devicesMainPresenter;
    private BluetoothScanClient mScanClient;
    private String type, name;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_devices_mainactivity;
    }

    @Override
    public void initIntentData() {
        super.initIntentData();
        type = getIntent().getStringExtra("type");
        name = getIntent().getStringExtra("name");
        if (type.equals(DevicesTypeManageEnum.Device_GateWay.getType())) {
            ivDeviceAdd.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setTitle() {
        super.setTitle();
        tvToolbarTitle.setText(name);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void initData() {
        super.initData();
        blueDeviceHelper = new BlueDeviceHelper(this);
        devicesMainPresenter = new DevicesMainPresenter(this);
        String[] typeList = type.split("/");
        String selectClause = "";
        for(String item : typeList){
            selectClause = selectClause+"type=? or ";
        }
        datas.addAll(blueDeviceHelper.queryByConditMore(selectClause.substring(0,selectClause.length()-4), typeList));
        initListView();

        mScanClient = BluetoothScanClient.getInstance(this, this);
        if (!mScanClient.isBluetoothOpen()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_OPENBLUERESULT);
        }
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    @OnClick({R.id.btn_tb_back,R.id.iv_device_add, R.id.iv_device_delete, R.id.tv_device_cancle,
            R.id.btn_device_del_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_tb_back:
                this.finish();
                break;
            case R.id.iv_device_add://添加设备
                Intent intent = new Intent(this, BleDeviceListActivity.class);
                intent.putExtra("scanType", DevicesTypeEnum.Device_GateWay.getType());//表示搜索网关
                startActivityForResult(intent, BleDeviceListActivity.REQUEST_DEVICE);
                break;
            case R.id.iv_device_delete://进入删除设备功能
                ivDeviceDel.setVisibility(View.GONE);
                tvDeviceCancle.setVisibility(View.VISIBLE);
                btnDeviceDelSure.setVisibility(View.VISIBLE);
                showOrHide = true;
                commonAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_device_cancle://退出删除功能
                ivDeviceDel.setVisibility(View.VISIBLE);
                tvDeviceCancle.setVisibility(View.GONE);
                btnDeviceDelSure.setVisibility(View.GONE);
                showOrHide = false;
                commonAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_device_del_sure:
                devicesMainPresenter.deleteDevice(type,datas);
                break;
        }
    }

    private void initListView() {
        commonAdapter = new CommonAdapter<DBBlueToothBean>(this, datas, R.layout.item_main_device_list) {
            @Override
            public void convert(ViewHolder helper, final DBBlueToothBean item) {
                switch (DevicesTypeEnum.getDevicesTypeEnum(item.getType())) {
                    case Device_GateWay:
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
                helper.getTextViewSet(R.id.tv_device_name,  item.getName());
                helper.getTextViewSet(R.id.tv_device_num,  item.getTableNum());
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
                DBBlueToothBean dataItem = datas.get(position);
                Intent goIntent = new Intent();
                goIntent.putExtra("refresh",false);//跳转后是否发送抄表命令，如果从网关进入则需要
                goIntent.putExtra("childType", dataItem.getType());
                goIntent.putExtra("childName", dataItem.getName());
                goIntent.putExtra("childNum", dataItem.getTableNum());
                goIntent.putExtra("fatherNum", dataItem.getFatherNum());
                goIntent.putExtra("fatherMac", dataItem.getFatherMac());
                switch (DevicesTypeEnum.getDevicesTypeEnum(dataItem.getType())) {
                    case Device_GateWay://网关
                        goActivity(DevicesInGateWayListActivity.class, goIntent);
                        break;
                    case Device_WallMountedSocket://墙挂式插座表
                    case Device_MobileSocket://移动式插座表
                        connectBlue(dataItem.getFatherMac());
                        goActivity(Socket_ControlActivity.class, goIntent);
                        break;
                    case Device_AirConditioning://空调控制器
                        connectBlue(dataItem.getFatherMac());
                        goActivity(AirConditioningControlActivity.class, goIntent);
                        break;
                    case Device_CentralAirConditioner://空调控制器
                        connectBlue(dataItem.getFatherMac());
                        goActivity(CentreConditioningControlActivity.class, goIntent);
                        break;
                    case Device_GuideMeter://导轨表电表
                        connectBlue(dataItem.getFatherMac());
                        goActivity(GuidMeterControlActivity.class, goIntent);
                        break;
                    case Device_SingleLampControl://单灯控制器
                        connectBlue(dataItem.getFatherMac());
                        goActivity(SingleLampControlActivity.class, goIntent);
                        break;
                    case Device_WaterMeter://蓝牙水表
                        connectBlue(dataItem.getFatherMac());
                        goActivity(WaterMeterControlActivity.class, goIntent);
                        break;
                    case Device_ThreeStreetLighControl://三路灯控
                    case Device_FourStreetLightControl://四路灯控
                        connectBlue(dataItem.getFatherMac());
                        goActivity(StreetLighControlActivity.class, goIntent);
                        break;
                    case Device_ScenarioPanel://情景面板
                        connectBlue(dataItem.getFatherMac());
                        goActivity(ScenarioPanelControlActivity.class, goIntent);
                        break;

                }
            }
        });
    }

    private void connectBlue(String mac){
//        if(BlueTooth_ConnectHelper.getInstance().isBooleanConnected()){
//            BlueTooth_ConnectHelper.getInstance().blueToothConnect(mScanClient.getDevice(mac));
//        }else{
//            showMsg("蓝牙未连接");
//        }
        BleConnect_InstanceHelper bleConnect = BleConnect_InstanceHelper.getInstance();
        bleConnect.setMacStr(mac);
        bleConnect.connecedDevice();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_OPENBLUERESULT:
                break;
            case FIND_BLUETOOTH_CODE://低功耗蓝牙
                if (resultCode == RESULT_OK) {
                    List<BleBlueToothBean> deviceList = (List<BleBlueToothBean>) data.getSerializableExtra(DeviceListActivity.DATA_DEVICE);
                    List<DBBlueToothBean> blueList = new ArrayList<>();
                    for(BleBlueToothBean device : deviceList){
                        if(device.getType().equals(DevicesTypeEnum.Device_GateWay.getType())){
                            blueList.add(new DBBlueToothBean(device.getType(), DevicesTypeEnum.Device_GateWay.getName(),
                                    device.getTableNum(), device.getAddress(),device.getTableNum(),device.getAddress(), false, true));
                        }
                    }
                    blueDeviceHelper.insert(blueList);
                    datas.clear();
                    datas.addAll(blueDeviceHelper.queryByCondit("type", DevicesTypeEnum.Device_GateWay.getType(), ""));
                    commonAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    /**
     * 事件订阅者处理事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoonEvent(EvenBusBean evenBusBean) {
        String topic = evenBusBean.getTopic();
        if (topic.equals(EvenBusEnum.EvenBus_BlueToothConnect.getEvenName())) {
            String message = evenBusBean.getData();
            showMsg(message);
        }
    }

    @Override
    public void showData(int code) {
        switch (code) {
            case 0:
                String[] typeList = type.split("/");
                String selectClause = "";
                for(String item : typeList){
                    selectClause = selectClause+"type=? or ";
                }
                datas.clear();
                datas.addAll(blueDeviceHelper.queryByConditMore(selectClause.substring(0,selectClause.length()-4), typeList));
                commonAdapter.notifyDataSetChanged();
                break;
        }
    }

}
