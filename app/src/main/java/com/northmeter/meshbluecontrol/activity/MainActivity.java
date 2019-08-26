package com.northmeter.meshbluecontrol.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.northmeter.meshbluecontrol.BuildConfig;
import com.northmeter.meshbluecontrol.R;
import com.northmeter.meshbluecontrol.adapter.CommonAdapter;
import com.northmeter.meshbluecontrol.adapter.ViewHolder;
import com.northmeter.meshbluecontrol.base.BaseActivity;
import com.northmeter.meshbluecontrol.bean.TypeManageBean;
import com.northmeter.meshbluecontrol.enumBean.DevicesTypeManageEnum;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by dyd on 2019/4/19.
 * 显示设备类型主页面
 */

public class MainActivity extends BaseActivity {
    private final static int FIND_BLUETOOTH_CODE = 1;
    private static final int REQUEST_OPENBLUERESULT = 201;
    private static final int REQUEST_LOCATIONARESULT = 2010;

    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    @BindView(R.id.tv_version)
    TextView tvVersion;

    private long exitTime;
    private CommonAdapter commonAdapter;
    private List<TypeManageBean> datas = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                checkLocationAndOpenCamer();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_OPENBLUERESULT);
            }
        }
    }

    @Override
    public void initIntentData() {
        super.initIntentData();
    }

    @Override
    public void setTitle() {
        super.setTitle();
        tvVersion.setText("version:"+BuildConfig.VERSION_NAME);
    }

    @Override
    public void initData() {
        super.initData();
        initListView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showMsg("再按一次退出");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_OPENBLUERESULT:
                boolean isAllGranted = true;
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        isAllGranted = false;
                        break;
                    }
                }
                if (!isAllGranted) {
                    //权限有缺失
                    showMsg("该功能需要您授权打开定位权限");
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, 10);
                } else {
                    checkLocationAndOpenCamer();
                }
                break;
            case REQUEST_LOCATIONARESULT:
                break;
            default:
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 10);
                break;
        }
    }

    /**
     * 检查是否打开了定位服务
     */
    private void checkLocationAndOpenCamer() {
        LocationManager lm = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        boolean locationISOK = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!locationISOK) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, REQUEST_LOCATIONARESULT);
        }
    }


    private void initListView() {
        for (DevicesTypeManageEnum devicesType : DevicesTypeManageEnum.values()) {
            TypeManageBean manageBean = new TypeManageBean(devicesType.getType(), devicesType.getName());
            datas.add(manageBean);
        }

        commonAdapter = new CommonAdapter<TypeManageBean>(this, datas, R.layout.item_main_activity) {
            @Override
            public void convert(ViewHolder helper, final TypeManageBean item) {
                switch (DevicesTypeManageEnum.getDevicesTypeEnum(item.getType())) {
                    case Device_GateWay:
                        helper.getImageViewSet(R.id.iv_device_type, R.drawable.img_device_gateway);
                        break;
                    case Device_ElecMeter://电表
                        helper.getImageViewSet(R.id.iv_device_type, R.drawable.img_device_elemeter);
                        break;
                    case Device_Socket://插座表
                        helper.getImageViewSet(R.id.iv_device_type, R.drawable.img_device_socket);
                        break;
                    case Device_AirConditioning://空调控制器
                        helper.getImageViewSet(R.id.iv_device_type, R.drawable.img_device_airconditioner);
                        break;
                    case Device_WaterMeter://水表
                        helper.getImageViewSet(R.id.iv_device_type, R.drawable.img_device_watermeter);
                        break;
                    case Device_LightControl://灯控
                        helper.getImageViewSet(R.id.iv_device_type, R.drawable.img_device_light);
                        break;
                    default:
                        break;
                }
                helper.getTextViewSet(R.id.tv_device_name, item.getName());
            }
        };
        listview.setAdapter(commonAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("type", datas.get(position).getType());
                intent.putExtra("name", datas.get(position).getName());
                goActivity(DevicesMainActivity.class, intent);
            }
        });
    }
}
