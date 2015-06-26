package com.kooniao.travel.home;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kooniao.travel.R;
import com.kooniao.travel.around.AroundDetailActivity_;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.model.DayList;
import com.kooniao.travel.utils.DateUtil;

public class LineAdapter extends BaseExpandableListAdapter {
	private Context context;
	private List<DayList> dayLists = new ArrayList<DayList>();
	private String currentDate; // 当前日期
	private boolean isOffline; // 
	private int travelId; // 行程id
	
	public LineAdapter(Context context, List<DayList> travelList, boolean isOffline) {
		this(context, travelList, isOffline, 0);
	}

	private boolean mIsLineProduct;

	public LineAdapter(Context context, List<DayList> travelList, boolean isOffline, boolean isLineProduct) {
		this(context, travelList, isOffline, 0);
		mIsLineProduct = isLineProduct;
	}
	
	@SuppressLint("SimpleDateFormat")
	public LineAdapter(Context context, List<DayList> travelList, boolean isOffline, int travelId) {
		this.context = context;
		this.isOffline = isOffline;
		this.travelId = travelId;
		if (travelList != null) {
			this.dayLists = travelList;
		}

		// 当前日期
		SimpleDateFormat dateFormat = new SimpleDateFormat(Define.FORMAT_YMD);
		currentDate = dateFormat.format(new java.util.Date());
	}

	@Override
	public int getGroupCount() {
		return dayLists.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		DayList dayList = dayLists.get(groupPosition);
		List<DayList.NodeList> subNodeList = dayList.getNodeList();
		return subNodeList == null ? 0 : subNodeList.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return dayLists.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return dayLists.get(groupPosition).getNodeList().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View view = convertView;
		final DayTravelViewHolder dayTravelViewHolder;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.item_daytravel, null);
			dayTravelViewHolder = new DayTravelViewHolder();
			dayTravelViewHolder.dayTextView = (TextView) view.findViewById(R.id.tv_travel_day);
			dayTravelViewHolder.dateTextView = (TextView) view.findViewById(R.id.tv_travel_scenic_day_date);
			dayTravelViewHolder.dayTravelNameTextView = (TextView) view.findViewById(R.id.tv_travel_scenic_day_name);
			dayTravelViewHolder.remarksImageView = (ImageView) view.findViewById(R.id.iv_travel_scenic_day_remarks);
			dayTravelViewHolder.remarkContentTextView = (TextView) view.findViewById(R.id.tv_travel_scenic_day_remarks);
			dayTravelViewHolder.topVerticalDivider = view.findViewById(R.id.v_vertical_top);
			view.setTag(dayTravelViewHolder);
		} else {
			dayTravelViewHolder = (DayTravelViewHolder) view.getTag();
		}

		final DayList dayList = dayLists.get(groupPosition);

		// 是否显示备注按钮
		final String remarks = dayList.getDayTips();
		if (remarks.equals("")) {
			dayTravelViewHolder.remarksImageView.setVisibility(View.INVISIBLE);
		} else {
			dayTravelViewHolder.remarksImageView.setVisibility(View.VISIBLE);
			dayTravelViewHolder.remarkContentTextView.setText(remarks);
		}

		// 第几天
		String day = String.valueOf(groupPosition + 1);
		dayTravelViewHolder.dayTextView.setText(day);
		dayTravelViewHolder.dayTextView.setVisibility(View.VISIBLE);
		dayTravelViewHolder.dayTextView.setBackgroundResource(R.drawable.travel_detail_day);
		if (!mIsLineProduct) {
			// 日期
			long dateStamp = dayList.getDayDate();
			String date = DateUtil.timestampToStr(dateStamp, Define.FORMAT_YMD);
			if (!date.contains("1970")) { 
				dayTravelViewHolder.dateTextView.setText(date);
			} else {
				dayTravelViewHolder.dateTextView.setText("时间待定"); 
			}
		} else { 
			dayTravelViewHolder.dateTextView.setVisibility(View.GONE);
		}
		// 行程名
		String travelName = dayList.getDayTitle();
		dayTravelViewHolder.dayTravelNameTextView.setVisibility(View.VISIBLE);
		dayTravelViewHolder.dayTravelNameTextView.setText(travelName);

		// 点击事件
		dayTravelViewHolder.remarksImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dayTravelViewHolder.remarkContentTextView.getVisibility() == View.VISIBLE) {
					// 隐藏
					dayTravelViewHolder.remarkContentTextView.setVisibility(View.GONE);
				} else if (!remarks.equals("")) {
					// 显示
					dayTravelViewHolder.remarkContentTextView.setVisibility(View.VISIBLE);
				}
			}
		});
		// 天节点备注当天自动展开
		if (currentDate.equals(dayList.getDayDate())) {
			dayTravelViewHolder.remarksImageView.performClick();
		}

		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View view = convertView;
		final DayScenicViewHolder dayScenicViewHolder;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.item_travel_dayscenic, null);
			dayScenicViewHolder = new DayScenicViewHolder();
			dayScenicViewHolder.scenicTypeImageView = (ImageView) view.findViewById(R.id.iv_travel_scenic_type);
			dayScenicViewHolder.dayTravelNameTextView = (TextView) view.findViewById(R.id.tv_travel_scenic_name);
			dayScenicViewHolder.remarksImageView = (ImageView) view.findViewById(R.id.iv_travel_scenic_remarks);
			dayScenicViewHolder.timeTextView = (TextView) view.findViewById(R.id.tv_travel_scenic_time);
			dayScenicViewHolder.remarkContentTextView = (TextView) view.findViewById(R.id.tv_travel_scenic_remarks);
			view.setTag(dayScenicViewHolder);
		} else {
			dayScenicViewHolder = (DayScenicViewHolder) view.getTag();
		}

		final DayList dayList = dayLists.get(groupPosition);
		final DayList.NodeList subNode = dayList.getNodeList().get(childPosition);

		// 图标
		String scenicType = subNode.getNodeType().trim();
		if (scenicType.equals(Define.LOCATION)) {
			/**
			 * 景点
			 */
			dayScenicViewHolder.scenicTypeImageView.setImageResource(R.drawable.travel_detail_scenic);
		} else if (scenicType.equals(Define.HOTEL)) {
			/**
			 * 酒店
			 */
			dayScenicViewHolder.scenicTypeImageView.setImageResource(R.drawable.travel_detail_hotel);
		} else if (scenicType.equals(Define.FOOD)) {
			/**
			 * 美食
			 */
			dayScenicViewHolder.scenicTypeImageView.setImageResource(R.drawable.travel_detail_food);
		} else if (scenicType.equals(Define.AMUSEMENT)) {
			/**
			 * 娱乐
			 */
			dayScenicViewHolder.scenicTypeImageView.setImageResource(R.drawable.travel_detail_amusement);
		} else if (scenicType.equals(Define.SHOPPING)) {
			/**
			 * 购物
			 */
			dayScenicViewHolder.scenicTypeImageView.setImageResource(R.drawable.travel_detail_shopping);
		} else if (scenicType.equals(Define.CUSTOM_EVENT) || scenicType.equals(Define.CUSTOM_TRAFFIC)) {
			/**
			 * 自定义事件、交通
			 */
			dayScenicViewHolder.scenicTypeImageView.setImageResource(R.drawable.travel_detail_custom);
		} else if (scenicType.equals(Define.CUSTOM_LOCATION)) {
			/**
			 * 自定义景点
			 */
			dayScenicViewHolder.scenicTypeImageView.setImageResource(R.drawable.travel_detail_scenic);
		} else if (scenicType.equals(Define.CUSTOM_HOTEL)) {
			/**
			 * 自定义酒店
			 */
			dayScenicViewHolder.scenicTypeImageView.setImageResource(R.drawable.travel_detail_hotel);
		} else if (scenicType.equals(Define.CUSTOM_FOOD)) {
			/**
			 * 自定义美食
			 */
			dayScenicViewHolder.scenicTypeImageView.setImageResource(R.drawable.travel_detail_food);
		} else if (scenicType.equals(Define.CUSTOM_SHOPPING)) {
			/**
			 * 自定义购物
			 */
			dayScenicViewHolder.scenicTypeImageView.setImageResource(R.drawable.travel_detail_shopping);
		} else if (scenicType.equals(Define.CUSTOM_ENTERTAINMENT)) {
			/**
			 * 自定义娱乐
			 */
			dayScenicViewHolder.scenicTypeImageView.setImageResource(R.drawable.travel_detail_amusement);
		}

		// 景点名
		dayScenicViewHolder.dayTravelNameTextView.setText(subNode.getNodeName());

		// 备注按钮
		final String remarks = subNode.getNodeRemark();
		if (remarks.equals("")) {
			dayScenicViewHolder.remarksImageView.setVisibility(View.INVISIBLE);
		} else {
			dayScenicViewHolder.remarksImageView.setVisibility(View.VISIBLE);
			dayScenicViewHolder.remarkContentTextView.setText(remarks);
		}
		dayScenicViewHolder.remarksImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dayScenicViewHolder.remarkContentTextView.getVisibility() == View.GONE && !remarks.equals("")) {
					dayScenicViewHolder.remarkContentTextView.setVisibility(View.VISIBLE);
				} else {
					dayScenicViewHolder.remarkContentTextView.setVisibility(View.GONE);
				}
			}
		});
		// 如果是当天的节点，则自动展开
		if (currentDate.equals(dayList.getDayDate()) && !remarks.equals("")) {
			dayScenicViewHolder.remarksImageView.performClick();
		}
		
		// 景点开始时间
//		String time = subNode.getNodeTime();
//		dayScenicViewHolder.timeTextView.setText(time);

		// item点击事件
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, AroundDetailActivity_.class);
				intent.putExtra(Define.ID, subNode.getNodeId());
				intent.putExtra(Define.PID, travelId);
				intent.putExtra(Define.TYPE, subNode.getNodeType());
				intent.putExtra(Define.IS_OFFLINE, isOffline);
				context.startActivity(intent);
			}
		});

		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	/**
	 * 天
	 * 
	 * @author ke.wei.quan
	 * 
	 */
	final static class DayTravelViewHolder {
		TextView dayTextView; // 第几天
		TextView dateTextView; // 当天行程日期
		TextView dayTravelNameTextView; // 当天行程名
		ImageView remarksImageView; // 备注按钮
		TextView remarkContentTextView; // 备注的信息
		View topVerticalDivider; // 头部分割线
	}

	/**
	 * 景点
	 * 
	 * @author ke.wei.quan
	 * 
	 */
	final static class DayScenicViewHolder {
		ImageView scenicTypeImageView; // 景点类型
		TextView dayTravelNameTextView; // 景点名
		ImageView remarksImageView; // 备注按钮
		TextView timeTextView; // 景点时间
		TextView remarkContentTextView; // 备注的信息
	}

}
