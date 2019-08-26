package com.northmeter.meshbluecontrol.base;

import android.view.View;

/**
 * Created by dyd on 2018/11/28.
 */

interface IBaseListener {
    /**
     * 开始方法用于Activity
     */
    void start();

    /**
     * 开始方法用于Fragment
     */
    void start(View view);

    /**
     * 接受上个页面传递过来的值
     */
    void initIntentData();


    /**
     * 设置监听器
     */
    void setListener();

    /**
     * 设置标题栏
     */
    void setTitle();

    /**
     * 初始化数据
     */
    void initData();

    /**
     * 刷新数据，需要在执行该方法
     */
    void refreshData();

    /**
     * 跳转activity
     */
    void goActivity();

}
