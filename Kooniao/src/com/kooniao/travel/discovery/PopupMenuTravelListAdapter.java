package com.kooniao.travel.discovery;

import java.util.ArrayList;
import java.util.List;

import com.kooniao.travel.R;
import com.kooniao.travel.model.DayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PopupMenuTravelListAdapter extends BaseAdapter {

	private Context context;

	public PopupMenuTravelListAdapter(Context context) {
		this.context = context;
	}

	private List<DayList> dayLists = new ArrayList<DayList>();

	public void setDayList(List<DayList> dayLists) {
		if (dayLists != null) {
			this.dayLists = dayLists;
		}
	}

	@Override
	public int getCount() {
		return dayLists.size();
	}

	@Override
	public Object getItem(int position) {
		return dayLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder viewHolder;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.popupwindow_travel_detail_menu_item, null);
			viewHolder = new ViewHolder();
			viewHolder.scenicNameTextView = (TextView) view.findViewById(R.id.tv_travel_detail_popupmenu_day);
			viewHolder.topDivider = view.findViewById(R.id.v_popupmenu_vertical_top);
			viewHolder.bottomDivider = view.findViewById(R.id.v_popupmenu_vertical_bottom);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		if (position == 0) {
			viewHolder.topDivider.setVisibility(View.INVISIBLE);
		} else if (position == dayLists.size() - 1) {
			viewHolder.bottomDivider.setVisibility(View.INVISIBLE);
		} else {
			viewHolder.topDivider.setVisibility(View.VISIBLE);
			viewHolder.bottomDivider.setVisibility(View.VISIBLE);
		}

		String travelName = dayLists.get(position).getDayTitle();
		viewHolder.scenicNameTextView.setText("DAY " + (position + 1) + " " + travelName);

		return view;
	}

	/**
	 * 菜单行程列表viewholder
	 * 
	 * @author ke.wei.quan
	 * 
	 */
	static class ViewHolder {
		TextView scenicNameTextView; // 景点名称
		View topDivider; // 头部分割线
		View bottomDivider; // 底部分割线
	}

}
