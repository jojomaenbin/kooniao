package com.kooniao.travel.manager;

import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.model.Alarm;
import com.kooniao.travel.model.CurrentTravel;
import com.kooniao.travel.model.DayList;
import com.kooniao.travel.model.MyTravel;
import com.kooniao.travel.model.TravelDetail;
import com.kooniao.travel.model.UserTravel;
import com.kooniao.travel.model.DayList.NodeList;
import com.kooniao.travel.receiver.AlarmReceiver;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.DateUtil;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBase;

/**
 * 行程提醒管理
 * 
 * @author ke.wei.quan
 * 
 */
public class TravelAlarmManager {
	TravelAlarmManager() {
	}

	private static TravelAlarmManager instance;

	public static TravelAlarmManager getInstance() {
		if (instance == null) {
			synchronized (ApiCaller.class) {
				if (instance == null) {
					instance = new TravelAlarmManager();
				}
			}
		}

		return instance;
	}

	/**
	 * 设置行程前提醒
	 * 
	 * @param travel
	 */
	public void setBeforeTravelAlarm(Context context, MyTravel travel) {
		if (travel != null) {
			long firstDayAlarmTimeStamp = travel.getStartTime();
			String firstDayAlarmTime = DateUtil.timeDistanceString(firstDayAlarmTimeStamp, Define.FORMAT_YMD) + " 09:00"; // 每天的09:00提醒
			for (int i = 0; i < 3; i++) {
				Alarm alarm = structureAlarm(travel, i, firstDayAlarmTime);
				setAlarm(context, alarm);
			}
		}
	}

	/**
	 * 构造闹钟model
	 * 
	 * @param travel
	 * @param intervalDate
	 * @param firstDayTimeStamp
	 */
	private Alarm structureAlarm(MyTravel travel, int intervalDate, String firstDayAlarmTime) {
		Alarm beforTravelAlarm = new Alarm();
		beforTravelAlarm.setAlarmCode((int) (travel.getPlanId() * 100 + intervalDate));
		String alarmTitle = "您的"+travel.getName()+"行程将于"+(intervalDate + 1)+"天后开始";
		beforTravelAlarm.setAlarmTitle(alarmTitle);
		String alarmContent = travel.getName();
		beforTravelAlarm.setAlarmContent(alarmContent);
		long alarmTimeStamp = DateUtil.strToTimestamp(firstDayAlarmTime, Define.FORMAT_YMDHM) - intervalDate * 24 * 60 * 60;
		beforTravelAlarm.setAlarmTime(alarmTimeStamp);
		beforTravelAlarm.setBeforeTravel(true);
		beforTravelAlarm.setCoverImgPath(travel.getImage());
		beforTravelAlarm.setEnable(true);
		beforTravelAlarm.setTravelId(travel.getPlanId());
		return beforTravelAlarm;
	}

	/**
	 * 设置行程中提醒
	 * 
	 * @param travel
	 */
	public void setOnTravelAlarm(Context context, int travelId, TravelDetail travelDetail) {
		if (travelDetail != null) {
			List<DayList> parentNodes = travelDetail.getDayList();
			String particularTime = ""; // 特定时间段的时间
			long dayParentNodeTime; // 每天父节点的日期
			String dayChildNodeTime = ""; // 每天子节点的时间
			long dayTimeStamp; // 每天节点的时间戳
			long currentStamp = System.currentTimeMillis() / 1000; // 当前时间戳
			for (int i = 0; i < parentNodes.size(); i++) {
				DayList travelParentNode = parentNodes.get(i);
				dayParentNodeTime = travelParentNode.getDayDate();
				// /////////////////////==1==//////////////////////////////
				particularTime = DateUtil.timestampToStr(dayParentNodeTime, Define.FORMAT_YMD) + " 11:00";
				dayTimeStamp = DateUtil.strToTimestamp(particularTime, Define.FORMAT_YMDHM);
				Alarm alarm = new Alarm();
				if (dayTimeStamp > currentStamp) {
					alarm.setTravelId(travelId);
					alarm.setAlarmCode((int) (travelId * 1000 + 11+i*100));
					alarm.setAlarmTime(dayTimeStamp);
					alarm.setAlarmTitle("行程提醒");
					alarm.setAlarmContent("填饱肚子的时间到啦，查看附近美食~");
					alarm.setNodeAlarmType(Define.FOOD);
					alarm.setCoverImgPath(travelDetail.getImage());
					alarm.setBeforeTravel(false);
					alarm.setNodeAlarm(false);
					alarm.setEnable(true);
					setAlarm(context, alarm);
				}
				// //////////////////////==2==/////////////////////////////
				particularTime = DateUtil.timestampToStr(dayParentNodeTime, Define.FORMAT_YMD) + " 17:00";
				dayTimeStamp = DateUtil.strToTimestamp(particularTime, Define.FORMAT_YMDHM);
				if (dayTimeStamp > currentStamp) {
					alarm = new Alarm();
					alarm.setTravelId(travelId);
					alarm.setAlarmCode((int) (travelId * 1000 + 17+i*100));
					alarm.setAlarmTime(dayTimeStamp);
					alarm.setAlarmTitle("行程提醒");
					alarm.setAlarmContent("填饱肚子的时间到啦，查看附近美食~");
					alarm.setNodeAlarmType(Define.FOOD);
					alarm.setCoverImgPath(travelDetail.getImage());
					alarm.setBeforeTravel(false);
					alarm.setEnable(true);
					setAlarm(context, alarm);
				}
				// ///////////////////////==3==////////////////////////////
				particularTime = DateUtil.timestampToStr(dayParentNodeTime, Define.FORMAT_YMD) + " 19:00";
				dayTimeStamp = DateUtil.strToTimestamp(particularTime, Define.FORMAT_YMDHM);
				if (dayTimeStamp > currentStamp) {
					alarm = new Alarm();
					alarm.setTravelId(travelId);
					alarm.setAlarmCode((int) (travelId * 1000 + 19+i*100));
					alarm.setAlarmTime(dayTimeStamp);
					alarm.setAlarmTitle("行程提醒");
					alarm.setAlarmContent("不知去哪了？查看附近有什么推荐~");
					alarm.setNodeAlarmType(Define.AMUSEMENT);
					alarm.setCoverImgPath(travelDetail.getImage());
					alarm.setBeforeTravel(false);
					alarm.setEnable(true);
					setAlarm(context, alarm);
				}
				// ///////////////////////==4==////////////////////////////
				List<NodeList> travelSubNodes = travelParentNode.getNodeList();
				for (NodeList travelSubNode : travelSubNodes) {
					if (travelSubNode.getNodeTimeStatus() == 1) {
						dayChildNodeTime = travelSubNode.getNodeTime();
						particularTime = DateUtil.timestampToStr(dayParentNodeTime, Define.FORMAT_YMD) + " " + dayChildNodeTime;
						dayTimeStamp = DateUtil.strToTimestamp(particularTime, Define.FORMAT_YMDHM);
						if (dayTimeStamp > currentStamp) {
							alarm = new Alarm();
							alarm.setTravelId(travelId);
							alarm.setNodeId(travelSubNode.getNodeId());
							alarm.setAlarmCode((int) (travelId * 1000 + travelSubNode.getNodeId()+i*100));
							alarm.setAlarmTime(dayTimeStamp);
							alarm.setAlarmTitle("行程提醒");
							alarm.setAlarmContent(travelSubNode.getNodeName());
							alarm.setNodeAlarmType(travelSubNode.getNodeType());
							alarm.setCoverImgPath(travelDetail.getImage());
							alarm.setBeforeTravel(false);
							alarm.setNodeAlarm(true);
							alarm.setEnable(true);
							setAlarm(context, alarm);
						}
					}

				}

			}
		}
	}
	
	
	/**
	 * 设置行程中提醒
	 * 
	 * @param travel
	 */
	public void setOnTravelAlarm(Context context, CurrentTravel travelDetail) {
		if (travelDetail != null) {
			List<DayList> parentNodes = travelDetail.getDayList();
			String particularTime = ""; // 特定时间段的时间
			long dayParentNodeTime; // 每天父节点的日期
			String dayChildNodeTime = ""; // 每天子节点的时间
			long dayTimeStamp; // 每天节点的时间戳
			long currentStamp = System.currentTimeMillis() / 1000; // 当前时间戳
			for (int i = 0; i < parentNodes.size(); i++) {
				DayList travelParentNode = parentNodes.get(i);
				dayParentNodeTime = travelParentNode.getDayDate();
				// /////////////////////==1==//////////////////////////////
				particularTime = DateUtil.timestampToStr(dayParentNodeTime, Define.FORMAT_YMD) + " 11:00";
				dayTimeStamp = DateUtil.strToTimestamp(particularTime, Define.FORMAT_YMDHM);
				Alarm alarm = new Alarm();
				if (dayTimeStamp > currentStamp) {
					alarm.setTravelId(travelDetail.getId());
					alarm.setAlarmCode((int) (travelDetail.getId() * 1000 + 11+i*100));
					alarm.setAlarmTime(dayTimeStamp);
					alarm.setAlarmTitle("行程提醒");
					alarm.setAlarmContent("填饱肚子的时间到啦，查看附近美食~");
					alarm.setNodeAlarmType(Define.FOOD);
					alarm.setCoverImgPath(travelDetail.getImage());
					alarm.setBeforeTravel(false);
					alarm.setNodeAlarm(false);
					alarm.setEnable(true);
					setAlarm(context, alarm);
				}
				// //////////////////////==2==/////////////////////////////
				particularTime = DateUtil.timestampToStr(dayParentNodeTime, Define.FORMAT_YMD) + " 17:00";
				dayTimeStamp = DateUtil.strToTimestamp(particularTime, Define.FORMAT_YMDHM);
				if (dayTimeStamp > currentStamp) {
					alarm = new Alarm();
					alarm.setTravelId(travelDetail.getId());
					alarm.setAlarmCode((int) (travelDetail.getId() * 1000 + 17+i*100));
					alarm.setAlarmTime(dayTimeStamp);
					alarm.setAlarmTitle("行程提醒");
					alarm.setAlarmContent("填饱肚子的时间到啦，查看附近美食~");
					alarm.setNodeAlarmType(Define.FOOD);
					alarm.setCoverImgPath(travelDetail.getImage());
					alarm.setBeforeTravel(false);
					alarm.setEnable(true);
					setAlarm(context, alarm);
				}
				// ///////////////////////==3==////////////////////////////
				particularTime = DateUtil.timestampToStr(dayParentNodeTime, Define.FORMAT_YMD) + " 19:00";
				dayTimeStamp = DateUtil.strToTimestamp(particularTime, Define.FORMAT_YMDHM);
				if (dayTimeStamp > currentStamp) {
					alarm = new Alarm();
					alarm.setTravelId(travelDetail.getId());
					alarm.setAlarmCode((int) (travelDetail.getId() * 1000 + 19+i*100));
					alarm.setAlarmTime(dayTimeStamp);
					alarm.setAlarmTitle("行程提醒");
					alarm.setAlarmContent("不知去哪了？查看附近有什么推荐~");
					alarm.setNodeAlarmType(Define.AMUSEMENT);
					alarm.setCoverImgPath(travelDetail.getImage());
					alarm.setBeforeTravel(false);
					alarm.setEnable(true);
					setAlarm(context, alarm);
				}
				// ///////////////////////==4==////////////////////////////
				List<NodeList> travelSubNodes = travelParentNode.getNodeList();
				for (NodeList travelSubNode : travelSubNodes) {
					if (travelSubNode.getNodeTimeStatus() == 1) {
						dayChildNodeTime = travelSubNode.getNodeTime();
						particularTime = DateUtil.timestampToStr(dayParentNodeTime, Define.FORMAT_YMD) + " " + dayChildNodeTime;
						dayTimeStamp = DateUtil.strToTimestamp(particularTime, Define.FORMAT_YMDHM);
						if (dayTimeStamp > currentStamp) {
							alarm = new Alarm();
							alarm.setTravelId(travelDetail.getId());
							alarm.setNodeId(travelSubNode.getNodeId());
							alarm.setAlarmCode((travelDetail.getId() * 1000 + travelSubNode.getNodeId()+i*100));
							alarm.setAlarmTime(dayTimeStamp);
							alarm.setAlarmTitle("行程提醒");
							alarm.setAlarmContent(travelSubNode.getNodeName());
							alarm.setNodeAlarmType(travelSubNode.getNodeType());
							alarm.setCoverImgPath(travelDetail.getImage());
							alarm.setBeforeTravel(false);
							alarm.setNodeAlarm(true);
							alarm.setEnable(true);
							setAlarm(context, alarm);
						}
					}

				}

			}
		}
	}

	/**
	 * 设闹钟
	 * 
	 * @param alarmContent
	 * @param intervalDate
	 * @param alarm
	 */
	private void setAlarm(Context context, Alarm alarm) {
		long intervalTime = alarm.getAlarmTime() - System.currentTimeMillis() / 1000;
		if (intervalTime > 0) {
			alarm.setUserid(AppSetting.getInstance().getIntPreferencesByKey(Define.UID));
			Intent intent = new Intent(KooniaoApplication.getInstance(), AlarmReceiver.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("alarm", alarm);
			intent.putExtras(bundle);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(KooniaoApplication.getInstance(), alarm.getAlarmCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmManager = (AlarmManager) KooniaoApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.getAlarmTime() * 1000, pendingIntent);

		}
	}

	/**
	 * 取消行程中闹钟
	 * 
	 * @param alarm
	 */
	public void cancelOnTravelAlarm(Context context) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		UserTravel usertravel  = db.queryById( AppSetting.getInstance().getIntPreferencesByKey(Define.UID), UserTravel.class);
		if (usertravel != null) {
			db.delete(usertravel);
		}
	}
	/**
	 * 取消行程前提醒
	 * 
	 * @param travel
	 */
	public void cancelTravelAlarm(Context context, MyTravel travel) {
		if (travel != null) {
			long firstDayAlarmTimeStamp = travel.getStartTime();
			String firstDayAlarmTime = DateUtil.timeDistanceString(firstDayAlarmTimeStamp, Define.FORMAT_YMD) + " 09:00"; // 每天的09:00提醒
			long firstDayTimeStamp = DateUtil.strToTimestamp(firstDayAlarmTime, Define.FORMAT_YMDHM);
			long intervalTime = firstDayTimeStamp - System.currentTimeMillis() / 1000;
			int intervalDate = (int) (intervalTime / 24 / 60 / 60);
			for (int i = 0; i < 2; i++) {
				Alarm alarm = structureAlarm(travel, intervalDate, firstDayAlarmTime);
				cancelAlarm(context, alarm);
			}
		}
	}


	/**
	 * 取消闹钟
	 * 
	 * @param myTravel
	 */
	public void cancelAlarm(Context context, Alarm alarm) {
		alarm.setUserid(AppSetting.getInstance().getIntPreferencesByKey(Define.UID));
		Intent intent = new Intent(KooniaoApplication.getInstance(), AlarmReceiver.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("alarm", alarm);
		intent.putExtras(bundle);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(KooniaoApplication.getInstance(), alarm.getAlarmCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) KooniaoApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);

	}

}
