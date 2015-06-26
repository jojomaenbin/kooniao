package com.kooniao.travel.manager;

import java.util.List;

import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.api.ApiCaller.APICollectListResultCallback;
import com.kooniao.travel.model.Collect;

/**
 * 收藏管理
 * @author ke.wei.quan
 *
 */
public class CollectManage {
	CollectManage() {
	}

	private static CollectManage instance;

	public static CollectManage getInstance() {
		if (instance == null) {
			synchronized (ApiCaller.class) {
				if (instance == null) {
					instance = new CollectManage();
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
	
	public interface CollectListResultCallback {
		void result(String errMsg, List<Collect> collects, int pageCount);
	}

	/**************************************************************************************************************************
	 * 
	 * 请求访问处理
	 * 
	 * *************************************************************************
	 * ************************************************
	 */

	/**
	 * 获取我的收藏列表
	 * @param pageNum
	 * @param callback
	 */
	public void loadMyCollects(int pageNum, final CollectListResultCallback callback) {
		ApiCaller.getInstance().loadMyCollects(pageNum, new APICollectListResultCallback() {
			
			@Override
			public void result(String errMsg, List<Collect> collects, int pageCount) {
				callback.result(errMsg, collects, pageCount);
			}
		});
	}

	/**************************************************************************************************************************
	 * 
	 * 数据库操作
	 * 
	 * *************************************************************************
	 * ************************************************
	 */
}
