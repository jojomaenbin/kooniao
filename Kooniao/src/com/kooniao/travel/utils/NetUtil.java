package com.kooniao.travel.utils;

import com.kooniao.travel.KooniaoApplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络工具
 * 
 * @author Administrator
 * 
 */
public class NetUtil {

	/**
	 * 判断当前网络是否可用
	 * 
	 * @return
	 */
	public static boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) KooniaoApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			// 如果仅仅是用来判断网络连接
			// 则可以使用 cm.getActiveNetworkInfo().isAvailable();
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 判断当前环境是否是wifi
	 * 
	 * @return
	 */
	public static boolean isWifi() {
		ConnectivityManager cm = (ConnectivityManager) KooniaoApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkINfo = cm.getActiveNetworkInfo();
		if (networkINfo != null && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

}
