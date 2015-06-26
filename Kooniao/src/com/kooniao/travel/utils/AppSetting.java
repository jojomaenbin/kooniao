package com.kooniao.travel.utils;

import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.constant.Define;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 一些app的偏好设置，保存到SharedPreferences，单例模式
 * 
 * @author ke.wei.quan
 * 
 */
public class AppSetting {

	private final static String SHAREPREFERENCE_NAME = "KooniaoAppSettings";
	private static AppSetting appSetting;
	private static SharedPreferences sharedPreferences;
	private static Editor editor;

	static {
		sharedPreferences = KooniaoApplication.getInstance().getSharedPreferences(SHAREPREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	AppSetting() {

	}

	public static AppSetting getInstance() {
		initPreferences();

		if (appSetting == null) {
			synchronized (AppSetting.class) {
				if (appSetting == null) {
					appSetting = new AppSetting();
				}
			}
		}
		return appSetting;
	}

	/**
	 * 初始化preference环境
	 */
	private static void initPreferences() {
		if (sharedPreferences == null) {
			sharedPreferences = KooniaoApplication.getInstance().getSharedPreferences(SHAREPREFERENCE_NAME, Context.MODE_PRIVATE);
		}

		if (editor == null) {
			editor = sharedPreferences.edit();
		}
	}

	/**
	 * 保存String类型的数据
	 * 
	 * @param key
	 * @param value
	 */
	public void saveStringPreferencesByKey(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 保存int类型的数据
	 * 
	 * @param key
	 * @param value
	 */
	public void saveIntPreferencesByKey(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * 保存boolean类型的数据
	 * 
	 * @param key
	 * @param value
	 */
	public void saveBooleanPreferencesByKey(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * 读取String类型的数据
	 * 
	 * @param key
	 * @return
	 */
	public String getStringPreferencesByKey(String key) {
		return sharedPreferences.getString(key, null);
	}

	/**
	 * 读取int类型的数据
	 * 
	 * @param key
	 * @return
	 */
	public int getIntPreferencesByKey(String key) {
		return sharedPreferences.getInt(key, 0);
	}

	/**
	 * 读取boolean类型的数据
	 * 
	 * @param key
	 * @return
	 */
	public boolean getBooleanPreferencesByKey(String key) {
		return sharedPreferences.getBoolean(key, false);
	}
	
	/**
	 * 读取boolean类型的数据
	 * 
	 * @param key
	 * @return
	 */
	public boolean getBooleanPreferencesByKey(String key, boolean defaultValue) {
		return sharedPreferences.getBoolean(key, defaultValue);
	}

	/**
	 * 保存float类型的数据
	 * 
	 * @param key
	 * @return
	 */
	public void saveFloatPreferencesByKey(String key, float value) {
		editor.putFloat(key, value);
		editor.commit();
	}

	/**
	 * 读取float类型的数据
	 * 
	 * @param key
	 * @return
	 */
	public float getFloatPreferencesByKey(String key) {
		return sharedPreferences.getFloat(key, 0.0f);
	}
	
	/**
	 * 保存long类型的数据
	 * 
	 * @param key
	 * @param value
	 */
	public void saveLongPreferencesByKey(String key, long value) {
		editor.putLong(key, value);
		editor.commit();
	}

	/**
	 * 读取long类型的数据
	 * 
	 * @param key
	 * @param value
	 */
	public long getLongPreferencesByKey(String key) {
		return sharedPreferences.getLong(key, 0);
	}

	/**
	 * 根据key删除某一项内容
	 * 
	 * @param key
	 */
	public void removePreferenceByKey(String key) {
		editor.remove(key);
		editor.commit();
	}

	/**
	 * 清空sharepreference
	 */
	public void clearAllPreference() {
		initPreferences();
		editor.clear();
		editor.commit();
	}

	/**
	 * 清空用户相关sharepreference
	 */
	public void clearUserPreference() {
		initPreferences();
		removePreferenceByKey(Define.ApiKey);
		removePreferenceByKey(Define.ApiKeySecret);
		removePreferenceByKey(Define.UID);
		removePreferenceByKey(Define.CURRENT_USER_NAME);
		removePreferenceByKey(Define.CID_DISCOVERY);
		removePreferenceByKey(Define.CID_HOME_PAGE);
		removePreferenceByKey(Define.CITY_NAME_AROUND);
		removePreferenceByKey(Define.CITY_NAME_AROUND_LAT);
		removePreferenceByKey(Define.CITY_NAME_AROUND_LON);
		removePreferenceByKey(Define.CURRENT_TRAVEL_LAST_DATE);
		removePreferenceByKey(Define.IS_NOT_FIRST_TIME_GET_MESSAGE_TEMPLATE);
	}

}
