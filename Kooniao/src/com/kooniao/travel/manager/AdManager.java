package com.kooniao.travel.manager;

import java.util.List;

import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.api.ApiCaller.APIAdListResultCallback;
import com.kooniao.travel.model.Ad;

/**
 * 广告管理
 * 
 * @author ke.wei.quan
 * 
 */
public class AdManager {

	AdManager() {
	}

	private static AdManager instance;

	public static AdManager getInstance() {
		if (instance == null) {
			synchronized (ApiCaller.class) {
				if (instance == null) {
					instance = new AdManager();
				}
			}
		}

		return instance;
	}

	/**************************************************************************************************************************
	 * 
	 * 回调接口
	 * 
	 * *************************************************************************
	 * ************************************************
	 */
	public interface AdListResultCallback {
		void result(String errMsg, List<Ad> ads);
	}

	/**************************************************************************************************************************
	 * 
	 * 请求访问处理
	 * 
	 * *************************************************************************
	 * ************************************************
	 */

	/**
	 * 获取首页广告列表
	 * 
	 * @param cid
	 * @param callback
	 */
	public void loadAdList(int cid, final AdListResultCallback callback) {
		ApiCaller.getInstance().loadAdList(cid, new APIAdListResultCallback() {

			@Override
			public void result(String errMsg, List<Ad> ads) {
				callback.result(errMsg, ads);
			}
		});
	}

}
