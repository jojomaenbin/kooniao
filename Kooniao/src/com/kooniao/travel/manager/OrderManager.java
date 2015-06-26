package com.kooniao.travel.manager;

import java.util.List;

import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.api.ApiCaller.APIMyOrderListResultCallback;
import com.kooniao.travel.api.ApiCaller.APIOrderDetailResultCallback;
import com.kooniao.travel.api.ApiCaller.APIOrderListResultCallback;
import com.kooniao.travel.api.ApiCaller.APIOrderPayResultCallback;
import com.kooniao.travel.api.ApiCaller.APIRemainPayResultCallback;
import com.kooniao.travel.model.MyOrder;
import com.kooniao.travel.model.Order;
import com.kooniao.travel.model.OrderDetail;

/**
 * 订单管理
 * @author ke.wei.quan
 *
 */
public class OrderManager {
	
	OrderManager() {
	}

	private static OrderManager instance;

	public static OrderManager getInstance() {
		if (instance == null) {
			synchronized (ApiCaller.class) {
				if (instance == null) {
					instance = new OrderManager();
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
	
	public interface OrderListResultCallback {
		void result(String errMsg, List<Order> orders, int pageCount);
	}
	
	public interface MyOrderListResultCallback {
		void result(String errMsg, List<MyOrder> myOrders, int pageCount);
	}
	
	public interface OrderDetailResultCallback {
		void result(String errMsg, OrderDetail orderDetail);
	}
	
	public interface OrderPayResultCallback {
		void result(String errMsg, String payInfo);
	}
	
	public interface RemainPayResultCallback {
		void result(String errMsg, int orderId, int invoiceId, float price);
	}

	/**************************************************************************************************************************
	 * 
	 * 请求访问处理
	 * 
	 * *************************************************************************************************************************
	 */
	
	/**
	 * 店铺订单管理
	 * @param type
	 * @param callback
	 */
	public void loadOrderList(String type, int pageNum, final OrderListResultCallback callback) {
		ApiCaller.getInstance().loadOrderList(type, pageNum, new APIOrderListResultCallback() {
			
			@Override
			public void result(String errMsg, List<Order> orders, int pageCount) {
				callback.result(errMsg, orders, pageCount); 
			}
		});
	}
	
	/**
	 * 获取我的订单列表
	 * 
	 * @param callback
	 */
	public void loadMyOrderList(int pageNum, final MyOrderListResultCallback callback) {
		ApiCaller.getInstance().loadMyOrderList(pageNum, new APIMyOrderListResultCallback() {
			
			@Override
			public void result(String errMsg, List<MyOrder> myOrders, int pageCount) {
				callback.result(errMsg, myOrders, pageCount); 
			}
		});
	}
	
	/**
	 * 获取订单详情页
	 * @param orderId
	 * @param callback
	 */
	public void loadOrderDetail(int orderId, final OrderDetailResultCallback callback) {
		ApiCaller.getInstance().loadOrderDetail(orderId, new APIOrderDetailResultCallback() {
			
			@Override
			public void result(String errMsg, OrderDetail orderDetail) {
				callback.result(errMsg, orderDetail); 
			}
		});
	}

	/**
	 * 订单支付
	 * @param orderId
	 * @param invoiceId
	 * @param payType
	 * @param callback
	 */
	public void orderPay(int orderId, int invoiceId, int payType, final OrderPayResultCallback callback) {
		ApiCaller.getInstance().orderPay(orderId, invoiceId, payType, new APIOrderPayResultCallback() {
			
			@Override
			public void result(String errMsg, String payInfo) {
				callback.result(errMsg, payInfo); 
			}
		});
	}
	
	/**
	 * 尾款支付
	 * @param orderId 订单id
	 * @param callback
	 */
	public void remainPay(int orderId, final APIRemainPayResultCallback callback) {
		ApiCaller.getInstance().remainPay(orderId, new APIRemainPayResultCallback() {
			
			@Override
			public void result(String errMsg, int orderId, int invoiceId, float price) {
				callback.result(errMsg, orderId, invoiceId, price); 
			}
		});
	}


}
