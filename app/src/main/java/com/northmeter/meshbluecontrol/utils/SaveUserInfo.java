package com.northmeter.meshbluecontrol.utils;

import android.content.Context;

import com.northmeter.meshbluecontrol.bean.UserInfo;

import java.io.FileNotFoundException;

import static com.northmeter.meshbluecontrol.utils.SharedPreferencesUtil.getPrefInt;
import static com.northmeter.meshbluecontrol.utils.SharedPreferencesUtil.getPrefString;


public class SaveUserInfo {

    /**
     * 把当前用户保存到本地
     *
     * @param userInfo
     * @throws FileNotFoundException
     */
    public static void saveLoginUser(Context context, UserInfo userInfo) {
        SharedPreferencesUtil.setPrefString(context, "userName", userInfo.getUserName());
        SharedPreferencesUtil.setPrefString(context, "passWord", userInfo.getPassWord());
        SharedPreferencesUtil.setPrefInt(context, "expire", userInfo.getExpire());
        SharedPreferencesUtil.setPrefString(context,"token",userInfo.getToken());
        SharedPreferencesUtil.setPrefBoolean(context,"is_login",true);
    }



    /**
     * 获取当前用户信息
     *
     * @param context
     * @return
     */
    public static UserInfo getLoginUser(Context context) {
        UserInfo userInfo = new UserInfo();
        String userName = getPrefString(context, "userName", userInfo.getUserName());
        String passWd = getPrefString(context, "passWord", userInfo.getPassWord());
        String token = getPrefString(context, "token", userInfo.getToken());
        int expire = getPrefInt(context, "expire", userInfo.getExpire());

        userInfo.setUserName(userName);
        userInfo.setExpire(expire);
        userInfo.setToken(token);
        userInfo.setPassWord(passWd);
        return userInfo;
    }

    /**
     * 删除用户信息
     *
     * @param context
     */
    public static void logoutUser(Context context) {
        SharedPreferencesUtil.setPrefString(context, "userName", "");
        SharedPreferencesUtil.setPrefInt(context, "passWord", 0);
        SharedPreferencesUtil.setPrefString(context, "token", "");
        SharedPreferencesUtil.setPrefBoolean(context,"is_login",false);
    }
}
