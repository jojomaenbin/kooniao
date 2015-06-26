package com.kooniao.travel.mine;

import java.util.ArrayList;
import java.util.List;

import com.kooniao.travel.R;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.ProgressManager;
import com.kooniao.travel.model.MyTravel;
import com.kooniao.travel.utils.ColorUtil;
import com.kooniao.travel.utils.DateUtil;
import com.kooniao.travel.utils.StringUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * 我的行程adapter
 * 
 * @author ke.wei.quan
 * 
 */
public class MyTravelAdapter extends BaseAdapter {
	private Context context;

	public MyTravelAdapter(Context context) {
		this.context = context;
	}

	private List<MyTravel> travels = new ArrayList<MyTravel>();

	public void setTravels(List<MyTravel> travels) {
		if (travels != null) {
			this.travels = travels;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return travels.size();
	}

	@Override
	public Object getItem(int position) {
		return travels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_my_travel, null);
			holder.frontView = convertView.findViewById(R.id.front);
			holder.remindImageButton = (ImageButton) convertView.findViewById(R.id.ib_remind);
			holder.remindImageButton.setLayoutParams(new LinearLayout.LayoutParams(Define.widthPx / 4, LayoutParams.MATCH_PARENT));
			holder.calendarImageButton = (ImageButton) convertView.findViewById(R.id.ib_calendar);
			holder.calendarImageButton.setLayoutParams(new LinearLayout.LayoutParams(Define.widthPx / 4, LayoutParams.MATCH_PARENT));
			holder.deleteImageButton = (ImageButton) convertView.findViewById(R.id.ib_delete);
			holder.deleteImageButton.setLayoutParams(new LinearLayout.LayoutParams(Define.widthPx / 4, LayoutParams.MATCH_PARENT));
			holder.travelCoverImageView = (ImageView) convertView.findViewById(R.id.iv_travel_cover);
			holder.travelMaskView = convertView.findViewById(R.id.ll_travel_cover_mask);
			holder.travelStateTextView = (TextView) convertView.findViewById(R.id.tv_travel_state);
			holder.travelDateTextView = (TextView) convertView.findViewById(R.id.tv_travel_date);
			holder.travelNameTextView = (TextView) convertView.findViewById(R.id.tv_travel_name);
			holder.travelRatingBar = (RatingBar) convertView.findViewById(R.id.rb_travel_rating);
			holder.travelPriceTextView = (TextView) convertView.findViewById(R.id.tv_travel_refference_price);
			holder.startingTodayTextView = (TextView) convertView.findViewById(R.id.tv_starting_today);
			ProgressManager.addProgressTextView(holder.startingTodayTextView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.frontView.setPressed(false); 
		MyTravel travel = travels.get(position);
		// 行程名
		String name = travel.getName();
		holder.travelNameTextView.setText(name);
		// 行程评分
		float rank = travel.getRank();
		holder.travelRatingBar.setRating(rank);
		// 行程参考价格
		float price = travel.getPrice();
		holder.travelPriceTextView.setText(String.valueOf(price));
		String travelState = travel.getPlanState(); // 出行状态
		long startTimeStamp = travel.getStartTime();
		String startDate = DateUtil.timeDistanceString(startTimeStamp, Define.FORMAT_YMD);
		holder.travelDateTextView.setText(startDate);

		final boolean isCurrentTravel = travel.getCurrent() == 1 ? true : false; // 是否是当前行程
		final int permission = travel.getPermission(); // 权限
		if (isCurrentTravel) {
			holder.travelStateTextView.setText(StringUtil.getStringFromR(R.string.starting_today));
			holder.travelMaskView.setVisibility(View.VISIBLE);
			holder.travelCoverImageView.setBackgroundColor(context.getResources().getColor(R.color.translucence_black_light));
			holder.travelMaskView.setBackgroundColor(context.getResources().getColor(R.color.vaa16b8eb));
			holder.startingTodayTextView.setVisibility(View.INVISIBLE);
			/**
			 * 侧滑按钮
			 */
			holder.calendarImageButton.setVisibility(View.GONE);
			holder.deleteImageButton.setVisibility(View.VISIBLE);
			holder.remindImageButton.setVisibility(View.VISIBLE);
			holder.deleteImageButton.setImageResource(R.drawable.close_white);
			 if (!travel.isReceiveTravelRemind()) { // 如果是接收推送
			 holder.remindImageButton.setImageResource(R.drawable.remind_rings_on);
			 } else {
			 holder.remindImageButton.setImageResource(R.drawable.remind_rings_off);
			 }
		} else {
			holder.startingTodayTextView.setVisibility(View.VISIBLE);
			// 侧滑图标
			holder.calendarImageButton.setVisibility(View.VISIBLE);
			holder.deleteImageButton.setVisibility(View.VISIBLE);
			holder.remindImageButton.setVisibility(View.GONE);
			holder.deleteImageButton.setImageResource(R.drawable.delete);
			if (travelState.equals(StringUtil.getStringFromR(R.string.travel_state_ing))) {// 进行中
				holder.travelMaskView.setVisibility(View.VISIBLE);
				holder.travelDateTextView.setVisibility(View.VISIBLE);
				holder.travelStateTextView.setText(travelState);
				holder.travelCoverImageView.setBackgroundColor(context.getResources().getColor(R.color.translucence_black_light));
				holder.travelMaskView.setBackgroundColor(context.getResources().getColor(R.color.vaa16b8eb));
			} else if (travelState.equals(StringUtil.getStringFromR(R.string.travel_state_finish))) {// 已结束
				holder.travelMaskView.setVisibility(View.VISIBLE);
				holder.travelDateTextView.setVisibility(View.GONE);
				holder.travelStateTextView.setText(travelState);
				holder.travelCoverImageView.setBackgroundColor(context.getResources().getColor(R.color.translucence_black_light));
				holder.travelMaskView.setBackgroundColor(context.getResources().getColor(R.color.vaa909090));
			} else if (travelState.equals(StringUtil.getStringFromR(R.string.travel_state_dns))) {// 未开始
				holder.travelMaskView.setVisibility(View.VISIBLE);
				holder.travelDateTextView.setVisibility(View.VISIBLE);
				travelState = StringUtil.getStringFromR(R.string.travel_state_ns);
				holder.travelStateTextView.setText(travelState);
				holder.travelCoverImageView.setBackgroundColor(context.getResources().getColor(R.color.translucence_black_light));
				holder.travelMaskView.setBackgroundColor(context.getResources().getColor(R.color.vaa7ac142));
			} else {
				holder.travelMaskView.setVisibility(View.GONE);
				holder.travelDateTextView.setVisibility(View.VISIBLE);
				holder.travelCoverImageView.setBackgroundColor(ColorUtil.getRandomColorRes());
			}
		}
		
		/**
		 * 判断是否有权限操作行程
		 */
		if (permission == 1 || permission == 2) {
			/**
			 * 侧滑按钮
			 */
			convertView.setTag(R.id.item_slideable, true); // 可以侧滑
		} else {
			/**
			 * 侧滑按钮
			 */
			convertView.setTag(R.id.item_slideable, false); // 不可以侧滑
		}

		/**
		 * 下载状态
		 */
		if (travel.getOfflineDownloadState() != 0) {
			if (travel.getOfflineDownloadState() == 1) {
				holder.startingTodayTextView.setText(R.string.downloading);
			} else if (travel.getOfflineDownloadState() == 2) {
				holder.startingTodayTextView.setText(R.string.starting_today);
			} else if (travel.getOfflineDownloadState() == -1) {
				holder.startingTodayTextView.setText(R.string.download_fail);
			} else if (travel.getOfflineDownloadState() == 3) {
//				holder.startingTodayTextView.setVisibility(View.VISIBLE);
				holder.startingTodayTextView.setText("请稍后");
			}
		} else {
			if (permission == 1 || permission == 2) {
				if (travel.getOfflineDownloadState() == 0) {
					holder.startingTodayTextView.setText(R.string.starting_today);
				}
			} else {
				holder.startingTodayTextView.setVisibility(View.INVISIBLE);
			}
		}

		holder.startingTodayTextView.setTag(travel.getPlanId()+"");
		String downloadprogress=ProgressManager.getProgressbyDownloadPath(travel.getPlanId());
		if (downloadprogress!=null) {
			holder.startingTodayTextView.setVisibility(View.VISIBLE);
			holder.startingTodayTextView.setText(downloadprogress);
		}
		// 请求加载封面 
		if (listener != null) {
			String imgUrl = travel.getImage();
			listener.onLoadCoverImgListener(imgUrl, holder.travelCoverImageView);
			// 点击今日出发
			holder.startingTodayTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					listener.onStartingTodayClickListener(position); 
				}
			});
		}

		return convertView;
	}

	static class ViewHolder {
		View frontView;
		ImageButton remindImageButton; // 提醒按钮
		ImageButton calendarImageButton; // 日历按钮
		ImageButton deleteImageButton; // 删除按钮
		ImageView travelCoverImageView; // 封面
		View travelMaskView; // 覆盖层
		TextView travelStateTextView; // 行程状态
		TextView travelDateTextView; // 行程日期
		TextView travelNameTextView; // 行程名称
		RatingBar travelRatingBar; // 行程评分
		TextView travelPriceTextView; // 行程价格
		TextView startingTodayTextView; // 今日出发按钮
	}

	public interface ListItemRequestListener {
		void onLoadCoverImgListener(String imgUrl, ImageView coverImageView);

		void onStartingTodayClickListener(int position);
	}

	private ListItemRequestListener listener;

	public void setOnListItemRequestListener(ListItemRequestListener listener) {
		this.listener = listener;
	}

}
