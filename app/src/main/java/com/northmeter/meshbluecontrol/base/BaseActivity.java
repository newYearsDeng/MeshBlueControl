package com.northmeter.meshbluecontrol.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by dyd on 2018/11/28.
 */

public abstract class BaseActivity extends AppCompatActivity implements IBaseListener {
    protected Context mContext;
    protected Unbinder unbinder;
    private LoadingDialog mLoadingDialog;

    protected abstract int getLayoutId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 锁定竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(getLayoutId());
        if (Build.VERSION.SDK_INT >= 21) {//5.0
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0 - 23   设备手机状态浪字体图标颜色为黑色
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        BaseAppManager.getAppManager().addActivity(this);
        unbinder = ButterKnife.bind(this);
        mContext = this;
        //BlueTooth_ConnectHelper.getInstance();
        start();
    }

    @Override
    public void start() {
        initIntentData();
        setListener();
        initData();
        setTitle();
    }

    @Override
    public void start(View view) {

    }

    @Override
    public void initIntentData() {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void setTitle() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void refreshData() {

    }

    @Override
    public void goActivity() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        BaseAppManager.getAppManager().removeAcitivity(this);
        EventBus.getDefault().unregister(this);
    }

    /**
     * activity简单跳转
     */
    public void goActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    /**
     * activity带数据跳转
     */
    public void goActivity(Class<?> cls, Intent intent) {
        intent.setClass(this, cls);
        startActivity(intent);
    }

    /**
     * activity带数据跳转
     */
    public void goActivityWithTitle(Class<?> cls, String title) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    protected void showMsg(String msg) {
        ToastUtil.showToastShort(mContext, msg);
    }

    protected void showMsgLong(String msg) {
        ToastUtil.showToastLong(mContext, msg);
    }


    protected void startLoadingDialog(){
        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setLoadingText("执行中,请稍后...");
        mLoadingDialog.setInterceptBack(false);
        mLoadingDialog.show();

    }

    protected void stopLoadingDialog(){
        if(mLoadingDialog==null){
            mLoadingDialog = new LoadingDialog(mContext);
        }
        mLoadingDialog.close();
    }

}
