package com.kooniao.travel.receiver;

import java.util.List;

import com.kooniao.travel.BottomTabBarActivity_;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 */
public class JPushReceiver extends BroadcastReceiver {
	private static final String TAG = "JPushReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "接收Registration Id : " + regId);

		} else if (JPushInterface.ACTION_UNREGISTER.equals(intent.getAction())) {
			String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "接收UnRegistration Id : " + regId);

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			Log.d(TAG, "接收到推送下来的通知");
			int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Log.d(TAG, "接收到推送下来的通知的ID: " + notifactionId);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			Log.d(TAG, "用户点击打开了通知");
			Intent newIntent = new Intent(context, BottomTabBarActivity_.class);
			newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(newIntent);
			
		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
			Log.d(TAG, "用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));

		} else {
			Log.d(TAG, "Unhandled intent - " + intent.getAction());

		}

	}

	/**
	 * 判断程序是否在前台运行
	 * 
	 * @param context
	 * @return
	 * @author yuelaiye
	 */
	public static boolean isTopApp(Context context) {
		String packageName = "com.kooniao.travel";
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
				return true;
			}
		}
		return false;
	}

}
