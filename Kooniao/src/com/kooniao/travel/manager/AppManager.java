package com.kooniao.travel.manager;

import java.text.DecimalFormat;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.api.ApiCaller.APIStringResultCallback;
import com.kooniao.travel.api.ApiCaller.ApiVersionResultCallback;
import com.kooniao.travel.model.Version;
import com.kooniao.travel.utils.FileUtil;

/**
 * app管理者
 * @author ke.wei.quan
 *
 */
public class AppManager {
	
	AppManager() {
	}

	private static AppManager instance;

	public static AppManager getInstance() {
		if (instance == null) {
			synchronized (ApiCaller.class) {
				if (instance == null) {
					instance = new AppManager();
				}
			}
		}

		return instance;
	}
	
	/**************************************************************************************************************************
	 * 
	 * 回调接口
	 * 
	 * *************************************************************************************************************************
	 */
	public interface CheckVersionResultCallback {
		void result(String errMsg, boolean isNeedForceUpdate, String appDownloadUrl);
	}
	
	public interface StringResultCallback {
		void result(String errMsg);
	}

	/**************************************************************************************************************************
	 * 
	 * 请求访问处理
	 * 
	 * *************************************************************************************************************************
	 */
	
	
	/**
	 * 获取当前版本号
	 * 
	 * @return
	 */
	public String getCurrentAppVersion() {
		PackageManager pm = KooniaoApplication.getInstance().getPackageManager();
		PackageInfo pi;
		String version = null;
		try {
			pi = pm.getPackageInfo(KooniaoApplication.getInstance().getPackageName(), 0);
			version = "V" + pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}
	
	/***
	 * 检查新版本
	 */
	public void checkLastVersion(final CheckVersionResultCallback callback) {
		ApiCaller.getInstance().checkLastVersion(new ApiVersionResultCallback() {
			
			@Override
			public void result(String errMsg, Version version) {
				if (errMsg == null) {
					String currentAppVersion = getCurrentAppVersion();
					String serverAppVersion = version.getVersion();
					boolean isNeedForceUpdate = version.getUpdates() == 1 ? true : false; 
					String appDownloadUrl = version.getAppUrl();
					if (!currentAppVersion.equals(serverAppVersion)) { 
						callback.result(null, isNeedForceUpdate, appDownloadUrl);
					} else {
						callback.result(null, false, null);
					}
				} else {
					callback.result(errMsg, false, null); 
				}
			}
		});
	}
	
	/**
	 * 获取app缓存大小
	 * @return
	 */
	public String getCacheSize() {
		double size = FileUtil.getSize(KooniaoApplication.getInstance().getPicDir());
		DecimalFormat df = new DecimalFormat("0.0");
		String cacheSize = df.format(size).equals("0.0") ? "0MB" : df.format(size) + "MB";
		return cacheSize;
	}
	
	/**
	 * 清理app缓存
	 */
	public void clearCache() {
		FileUtil.delAllFile(KooniaoApplication.getInstance().getPicDir());
	}
	
	/**
	 * 上传日志文件
	 * @param filePath
	 * @param callback
	 */
	public void uploadLogFile(String filePath, final StringResultCallback callback) {
		ApiCaller.getInstance().uploadLogFile(filePath, new APIStringResultCallback() {
			
			@Override
			public void result(String errMsg) {
				callback.result(errMsg);
			}
		});
	}
	
}
