package com.kooniao.travel.utils;

import com.kooniao.travel.R;

/**
 * 订单工具类
 * 
 * @author ke.wei.quan
 * 
 */
public class OrderUtil {

	/**
	 * 根据订单状态code(int)获取文字颜色
	 * 
	 * @param orderStatus
	 * @return
	 */
	public static int getStatusTextColor(int orderStatus) {
		int orderStatusColorRes = 0;

		switch (orderStatus) {
		case -2: // 已关闭
			orderStatusColorRes = R.color.v909090;
			break;

		case -1: // 已取消
			orderStatusColorRes = R.color.v909090;
			break;

		case 0: // 未处理
			orderStatusColorRes = R.color.ve73222;
			break;

		case 1: // 已确认
			orderStatusColorRes = android.R.color.holo_green_dark;
			break;

		case 2: // 已收款
			orderStatusColorRes = android.R.color.holo_green_dark;
			break;

		case 3: // 已退订
			orderStatusColorRes = R.color.v909090;
			break;

		case 4: // 已出团
			orderStatusColorRes = android.R.color.holo_green_dark;
			break;

		case 5: // 部分收款
			orderStatusColorRes = R.color.v909090;
			break;

		default:
			break;
		}

		return orderStatusColorRes;
	}
	
	/**
	 * 根据订单状态code(int)获取订单状态(String)
	 * 
	 * @param orderStatus
	 * @return
	 */
	public static String getStatusText(int orderStatus) {
		String orderStatusText = "";
		
		switch (orderStatus) {
		case -2: // 已关闭
			orderStatusText = StringUtil.getStringFromR(R.string.closed);
			break;

		case -1: // 已取消
			orderStatusText = StringUtil.getStringFromR(R.string.canceled);
			break;

		case 0: // 未处理
			orderStatusText = StringUtil.getStringFromR(R.string.processing);
			break;

		case 1: // 已确认
			orderStatusText = StringUtil.getStringFromR(R.string.confirmed);
			break;

		case 2: // 已收款
			orderStatusText = StringUtil.getStringFromR(R.string.money_receipt);
			break;

		case 3: // 已退订
			orderStatusText = StringUtil.getStringFromR(R.string.unsubcribed);
			break;

		case 4: // 已出团
			orderStatusText = StringUtil.getStringFromR(R.string.have_a_ball);
			break;

		case 5: // 部分收款
			orderStatusText = StringUtil.getStringFromR(R.string.part_of_collection);
			break;

		default:
			break;
		}
		
		return orderStatusText;
	}
	
	/**
	 * 根据订单状态code(int)获取订单状态处理信息
	 * 
	 * @param orderStatus
	 * @return
	 */
	public static String getStatusTipsText(int orderStatus) {
		String orderStatusTipsText = "";
		
		switch (orderStatus) {
		case -2: // 已关闭
			orderStatusTipsText = StringUtil.getStringFromR(R.string.order_status_tips_consult_customer);
			break;
			
		case -1: // 已取消
			orderStatusTipsText = "订单已取消";
			break;
			
		case 0: // 默认
			orderStatusTipsText = "默认状态";
			break;
			
		case 1: // 未付款
			orderStatusTipsText = "订单未付款";
			break;
			
		case 2: // 订单关闭
			orderStatusTipsText = "订单已关闭";
			break;
			
		case 3: // 支付尾款
			orderStatusTipsText = "订单未付款";
			break;
			
		case 4: // 支付完成
			orderStatusTipsText = "订单已付款";
			break;
			
		case 5: // 部分收款
			orderStatusTipsText = StringUtil.getStringFromR(R.string.order_status_tips_part_of_collection);
			break;
			
		default:
			break;
		}
		
		return orderStatusTipsText;
	}
	
	/**
	 * 根据类型获取证件字符串
	 * @param type
	 * @return
	 */
	public static String getCertificateByType(int type) {
		String certificateType = "";
		switch (type) {
		case 0: // 身份证
		case 1:
			certificateType = StringUtil.getStringFromR(R.string.certificate_type_id_card);
			break;
			
		case 2: // 护照
			certificateType = StringUtil.getStringFromR(R.string.certificate_type_passport);
			break;
		case 3: // 其他
			certificateType = StringUtil.getStringFromR(R.string.certificate_type_other);
			break;

		default:
			break;
		}
		
		return certificateType;
	}
	
}
