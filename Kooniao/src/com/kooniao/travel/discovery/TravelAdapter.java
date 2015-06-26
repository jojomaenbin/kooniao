package com.kooniao.travel.discovery;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.kooniao.travel.R;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.SelectedImgeView;
import com.kooniao.travel.model.Travel;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.ColorUtil;

public class TravelAdapter extends BaseAdapter implements SectionIndexer {
	private Context context;
	private int cid;

	public TravelAdapter(Context context) {
		this.context = context;
		cid = AppSetting.getInstance().getIntPreferencesByKey(Define.CID_DISCOVERY);
	}

	private List<Travel> travels = new ArrayList<Travel>();

	public void setTravelList(List<Travel> travels) {
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
		View view = convertView;
		ViewHolder viewHolder = null;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.item_discovery_travel, null);
			viewHolder = new ViewHolder();
			viewHolder.tipsLayout = (LinearLayout) view.findViewById(R.id.ll_recommend_tips);
			viewHolder.colletImageView = (SelectedImgeView) view.findViewById(R.id.iv_collect_travel);
			viewHolder.costOrientationTextView = (TextView) view.findViewById(R.id.tv_scenic_cost_orientation);
			viewHolder.costTextView = (TextView) view.findViewById(R.id.tv_travel_cost);
			viewHolder.coverImageView = (ImageView) view.findViewById(R.id.iv_cover_travel);
			viewHolder.titleTextView = (TextView) view.findViewById(R.id.tv_travel_start_place);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		final Travel travel = travels.get(position);
		if (travel.getCollect() == 1) {
			viewHolder.colletImageView.setImageResource(R.drawable.collect_press);
		} else {
			viewHolder.colletImageView.setImageResource(R.drawable.collect_normal);
		}

		if (position == getPositionForSection(1)) {
			viewHolder.tipsLayout.setVisibility(View.VISIBLE);
		} else {
			viewHolder.tipsLayout.setVisibility(View.GONE);
		}

		String price = travel.getPrice();
		if ("0".equals(price)) {
			viewHolder.costTextView.setVisibility(View.INVISIBLE);
			viewHolder.costOrientationTextView.setVisibility(View.INVISIBLE);
		} else {
			viewHolder.costTextView.setVisibility(View.VISIBLE);
			viewHolder.costOrientationTextView.setVisibility(View.VISIBLE);
			viewHolder.costTextView.setText(price);
		}
		viewHolder.titleTextView.setText(travel.getTitle());
		// 加载图片
		final ImageView imageView = viewHolder.coverImageView;
		imageView.setBackgroundColor(ColorUtil.getRandomColorRes());
		String imagePath = travel.getImage();
		if (listener != null) {
			listener.onLoadCoverImgListener(imagePath, imageView);
			/**
			 * 点击添加收藏
			 */
			viewHolder.colletImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onCollectClickListener(position, (ImageView) v);
				}
			});
			
			// item点击
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					listener.onItemClickListener(position); 
				}
			});
		}

		return view;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			int isRecommend = travels.get(i).getCityId() == cid ? 1 : 0;
			if (isRecommend == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 获取是否是推荐的行程(0:不是推荐，1：是推荐的行程)
	 */
	@Override
	public int getSectionForPosition(int position) {
		return travels.get(position).getCityId() == cid ? 1 : 0;
	}

	public interface ListItemRequestListener {
		void onLoadCoverImgListener(String imgUrl, ImageView coverImageView);

		void onCollectClickListener(int position, ImageView imageView);
		
		void onItemClickListener(int position);
	}

	private ListItemRequestListener listener;

	public void setOnListItemRequestListener(ListItemRequestListener listener) {
		this.listener = listener;
	}

	class ViewHolder {
		LinearLayout tipsLayout; // 顶部提示布局
		TextView titleTextView; // 标题
		TextView costOrientationTextView; // 价格单位
		TextView costTextView; // 价格
		ImageView coverImageView; // 封面底图
		SelectedImgeView colletImageView; // 收藏，想去
	}
}
