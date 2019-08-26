package com.northmeter.meshbluecontrol.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;

/**
 * Created by dyd on 2019/4/28.
 */

public class CheckPermissions extends BaseActivity{
    private final static int FIND_BLUETOOTH_CODE = 1;
    private static final int REQUEST_OPENBLUERESULT = 201;
    private static final int REQUEST_LOCATIONARESULT = 2010;
    private String[] permissionsList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION };

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                checkLocationAndOpenCamer();
            } else {
                requestPermissions(permissionsList, REQUEST_OPENBLUERESULT);
            }
        }
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

}
