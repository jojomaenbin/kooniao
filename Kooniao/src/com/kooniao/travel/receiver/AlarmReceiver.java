package com.kooniao.travel.receiver;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.kooniao.travel.BottomTabBarActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.around.AroundDetailActivity_;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.mine.MyTravelActivity_;
import com.kooniao.travel.model.Alarm;
import com.kooniao.travel.model.UserTravel;
import com.kooniao.travel.utils.AppSetting;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBase;

/**
 * 本地设置提醒接收器
 * 
 * @author ke.wei.quan
 * 
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 获取传递的信息
		Bundle bundle = intent.getExtras();
		Alarm alarm = (Alarm) bundle.getSerializable("alarm");

		if (alarm != null) {
			if (alarm.getUserid() == AppSetting.getInstance().getIntPreferencesByKey(Define.UID)) {
				DataBase db = LiteOrm.newInstance(context, Define.DB_NAME);
				UserTravel userTravel = db.queryById(alarm.getUserid(), UserTravel.class);
				if (userTravel != null&&userTravel.isReceiveTravelRemind()&&userTravel.isTravelable()) {
					long nowtime=System.currentTimeMillis()/1000;
					if (alarm.getTravelId() == userTravel.getTravelid()) {
						
						if (nowtime > userTravel.getTravelbengin() && nowtime < userTravel.getTravelend()) {
							notifiAlarm(context,alarm);
						}
					} else {
						if (nowtime < userTravel.getTravelbengin() || nowtime > userTravel.getTravelend()) {
							notifiAlarm(context,alarm);
						}
					}
				}
				else {
					if (alarm.getNodeAlarmType()==null) {
						notifiAlarm(context,alarm);
					}
				}
			}
		}
	}

	public void notifiAlarm(Context context,Alarm alarm) {
		int alarmCode = alarm.getAlarmCode();
		String title = alarm.getAlarmTitle();
		String content = alarm.getAlarmContent();
		String alarmType = alarm.getNodeAlarmType();
		boolean isBeforeTravel = alarm.isBeforeTravel();
		Intent intentTarget = null;
		if (isBeforeTravel) { // 行程前的提醒
			intentTarget = new Intent(context, MyTravelActivity_.class);
		} else if (alarmType != null) { // 行程中的提醒
			boolean isNodeAlarm = alarm.isNodeAlarm(); // 是否是节点提醒
			if (!isNodeAlarm) { // 不是节点提醒则跳转附近页
				intentTarget = new Intent(context, BottomTabBarActivity_.class);
				intentTarget.putExtra(Define.TYPE, Define.AROUND);
				if (alarmType.equals(Define.AMUSEMENT)) {
					intentTarget.putExtra(Define.TAB_POSITION, 4);
				} else {
					intentTarget.putExtra(Define.TAB_POSITION, 2);
				}
			} else { // 是节点提醒则跳转附近页
				intentTarget = new Intent(context, AroundDetailActivity_.class);
				intentTarget.putExtra(Define.ID, alarm.getNodeId());
				intentTarget.putExtra(Define.PID, alarm.getTravelId());
				intentTarget.putExtra(Define.TYPE, alarmType);
				intentTarget.putExtra(Define.LUNCH_TYPE, Define.HOME_PAGE);
			}
		}

		if (intentTarget != null) {
			intentTarget.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			Bundle data = new Bundle();
			data.putSerializable("alarm", alarm);
			intentTarget.putExtras(data);
			PendingIntent contentIntent = PendingIntent.getActivity(context, alarmCode, intentTarget, PendingIntent.FLAG_UPDATE_CURRENT);
			NotificationManager notiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification.Builder(context) //
					.setContentTitle(title) // 标题
					.setContentText(content) // 内容
					.setAutoCancel(true) //
					.setWhen(alarm.getAlarmTime()*1000) //
					.setDefaults(Notification.DEFAULT_ALL) //
					.setSmallIcon(R.drawable.app_logo) //
					.setContentIntent(contentIntent) //
					.build(); //
			notiManager.notify(alarmCode, notification);
		}
	}
}
