package com.kooniao.travel.customwidget;

import java.util.List;

import com.kooniao.travel.BaiDuMapActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.BaiDuMapActivity.From;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.utils.StringUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * title
 * @author ke.wei.quan
 *
 */
public class InfoDescView extends LinearLayout {

	public InfoDescView(Context context) {
		this(context, null);
	}

	private Context context;

	public InfoDescView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		addView(LayoutInflater.from(context).inflate(R.layout.widget_around_detail_info, null), new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		initWiget();
	}

	private LinearLayout nameLayout; // 名字布局
	private TextView nameTextView; // 名字
	private LinearLayout addressLayout; // 地址布局
	private TextView addressTextView; // 地址
	private LinearLayout phoneLayout; // 电话布局
	private TextView phoneTextView; // 电话

	/**
	 * 初始化view控件
	 */
	private void initWiget() {
		/**
		 * 名称布局
		 */
		nameLayout = (LinearLayout) findViewById(R.id.ll_name);
		nameTextView = (TextView) findViewById(R.id.tv_small_travel_name);
		/**
		 * 地址布局
		 */
		addressLayout = (LinearLayout) findViewById(R.id.ll_address);
		addressTextView = (TextView) findViewById(R.id.tv_arround_address);
		/**
		 * 地址布局
		 */
		phoneLayout = (LinearLayout) findViewById(R.id.ll_phone);
		phoneTextView = (TextView) findViewById(R.id.tv_arround_phonenum);
	}

	/**
	 * 设置名字
	 * 
	 * @param name
	 * @param nodeType
	 */
	public void setName(String name, String nodeType) {
		if (name.equals("") || name.equals("0")) {
			nameLayout.setVisibility(GONE);
		} else {
			nameTextView.setText(name);
			if (Define.LOCATION.equals(nodeType)) {// 景点
				nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.detail_scenic, 0, 0, 0);
			} else if (Define.HOTEL.equals(nodeType)) {// 酒店
				nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.detail_hotel, 0, 0, 0);
			} else if (Define.FOOD.equals(nodeType)) {// 美食
				nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.detail_food, 0, 0, 0);
			} else if (Define.SHOPPING.equals(nodeType)) {// 购物
				nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.detail_shopping, 0, 0, 0);
			} else if (Define.AMUSEMENT.equals(nodeType)) {// 娱乐
				nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.detail_amusement, 0, 0, 0);
			}
		}
	}

	/**
	 * 设置地址
	 * 
	 * @param address
	 * @param lat
	 * @param lon
	 */
	public void setAddress(final String address, final float lat, final float lon) {
		if (address.equals("") || address.equals("0")) {
			addressLayout.setVisibility(GONE);
		} else {
			addressTextView.setText(address);
			if (lat != 0 && lon != 0) {
				addressLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Uri mUri = Uri.parse("geo:" + lat + "," + lon + "?q=" + address);
						Intent navigationIntent = new Intent(Intent.ACTION_VIEW, mUri);
						PackageManager pm = context.getPackageManager();
						List<ResolveInfo> mapAppInfos = pm.queryIntentActivities(navigationIntent, 0);
						if (mapAppInfos.size() > 0) {
							((Activity) context).startActivity(navigationIntent);
						} else {
							Intent intent = new Intent(context, BaiDuMapActivity_.class);
							intent.putExtra(Define.FROM, From.FROM_OTHER.from);
							Bundle extras = new Bundle();
							extras.putInt(Define.FROM, From.FROM_OTHER.from);
							extras.putDouble(Define.LAT, lat);
							extras.putDouble(Define.LON, lon);
							intent.putExtras(extras);
							context.startActivity(intent);
						}
					}
				});
			}
		}
	}

	Dialog dialog;

	/**
	 * 设置地址
	 * 
	 * @param address
	 */
	public void setPhone(final String phone) {
		if (phone.equals("") || phone.equals("0")) {
			phoneLayout.setVisibility(GONE);
		} else {
			phoneTextView.setText(phone);
			final String title = StringUtil.getStringFromR(R.string.call);
			phoneLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog = new Dialog(context, title, phone);
					dialog.setCancelable(false);
					dialog.setOnAcceptButtonClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							((Activity) context).startActivity(intent);
						}
					});
					dialog.setOnCancelButtonClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.show();
				}
			});
		}
	}

}
