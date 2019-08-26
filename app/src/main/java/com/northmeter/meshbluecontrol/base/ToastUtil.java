package com.northmeter.meshbluecontrol.base;

import android.content.Context;
import android.widget.Toast;

/**
 * 自定义Toast
 * 
 * @author Administrator
 * 
 */
public class ToastUtil {
	/**
	 * 显示时间短
	 * 
	 * @param context 上下文
	 * @param str 提示的字符串
	 */
	public static void showToastShort(Context context, String str) {
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
		//MyToast.makeText(context,str,MyToast.LENGTH_SHORT).show();
	}

	/**
	 * 显示时间长
	 * 
	 * @param context 上下文
	 * @param str 提示的字符串
	 */
	public static void showToastLong(Context context, String str) {
		Toast.makeText(context, str, Toast.LENGTH_LONG).show();
		//MyToast.makeText(context,str,MyToast.LENGTH_LONG).show();
	}
	
	/**
	 * 判断网络失败提示语
	 * 
	 * @param context 上下文
	 */
	public static void showNoNet(Context context) {
		Toast.makeText(context, "当前网络不可用\n请检查你的网络设置!", Toast.LENGTH_SHORT).show();
	}
	/**
	 * 请求超时
	 * 
	 * @param context 上下文
	 */
	public static void showLongTime(Context context) {
		Toast.makeText(context, "请求超时!", Toast.LENGTH_SHORT).show();
	}
	/**
	 * 请求失败
	 * 
	 * @param context 上下文
	 */
	public static void showRequestFail(Context context) {
		Toast.makeText(context, "请求失败!", Toast.LENGTH_SHORT).show();
	}
	/**
	 * 没有更多数据了
	 * 
	 * @param context 上下文
	 */
	public static void notMoreData(Context context) {
		Toast.makeText(context, "暂无更多数据!", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 没有数据
	 * 
	 * @param context 上下文
	 */
	public static void notData(Context context) {
		Toast.makeText(context, "暂无数据!", Toast.LENGTH_SHORT).show();
	}

}
