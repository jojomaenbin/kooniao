package com.kooniao.travel.manager;

import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.api.ApiCaller.APIURLResolveResultCallback;

/**
 * 二维码扫描请求管理
 * @author ke.wei.quan
 *
 */
public class QRCodeManager {
	QRCodeManager() {
	}

	private static QRCodeManager instance;

	public static QRCodeManager getInstance() {
		if (instance == null) {
			synchronized (ApiCaller.class) {
				if (instance == null) {
					instance = new QRCodeManager();
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
	public interface URLResolveResultCallback {
		void result(String errMsg, int id, String type, int distributorId, String commonType);
	}
	
	/**************************************************************************************************************************
	 * 
	 * 请求访问处理
	 * 
	 * *************************************************************************
	 * ************************************************
	
	
	/**
	 * 二维码链接解码
	 * @param url
	 * @param callback
	 */
	public void urlResolve(String url, final URLResolveResultCallback callback) {
		ApiCaller.getInstance().urlResolve(url, new APIURLResolveResultCallback() {
			
			@Override
			public void result(String errMsg, int id, String type, int distributorId, String commonType) {
				callback.result(errMsg, id, type, distributorId, commonType); 
			}
		});
	}
}
