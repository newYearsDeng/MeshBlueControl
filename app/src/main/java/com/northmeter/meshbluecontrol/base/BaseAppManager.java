package com.northmeter.meshbluecontrol.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Stack;

/**
 * Created by dyd on 2018/8/29.
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 */

public class BaseAppManager {
    private static Stack<Activity> activityStack;
    private static BaseAppManager instance;

    private BaseAppManager() {
    }

    public static BaseAppManager getAppManager(){
        if(instance == null){
            instance = new BaseAppManager();
        }
        return instance;
    }
    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if (activityStack!=null){
            Activity activity = activityStack.lastElement();
            return activity;
        }else {
            return null;
        }
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 移除Activity
     */
    public void removeAcitivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }


    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    @SuppressWarnings("deprecation")
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            //友盟统计---->如果开发者调用Process.kill或者System.exit之类的方法杀死进程，
            //请务必在此之前调用MobclickAgent.onKillProcess(Context context)方法，用来保存统计数据。
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
//			MobclickAgent.onKillProcess(context);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {

        }
    }

    // 记录第一次点击的时间
    private long clickTime = 0;

    /**
     * 再次点击退出程序
     *
     * @param context
     */
    public void exit(Context context) {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            ToastUtil.showToastShort(context, "再按一次后退键退出程序");
            clickTime = System.currentTimeMillis();
        } else {
            AppExit(context);
            // ((Activity) context).finish();
        }
    }

}
