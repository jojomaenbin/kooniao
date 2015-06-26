package com.kooniao.travel.mine;

import java.util.ArrayList;
import java.util.List;

import com.kooniao.travel.R;
import com.kooniao.travel.manager.ProgressManager;
import com.kooniao.travel.model.OffLine;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 离线
 * 
 * @author ke.wei.quan
 * 
 */
public class OfflineAdapter extends BaseAdapter {
	private Context context;

	public OfflineAdapter(Context context) {
		this.context = context;
	}

	private List<OffLine> offLines = new ArrayList<OffLine>();

	public void setOfflines(List<OffLine> offLines) {
		if (offLines != null) {
			this.offLines = offLines;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return offLines.size();
	}

	@Override
	public Object getItem(int position) {
		return offLines.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_offline, null);
			holder.coverImageView = (ImageView) convertView.findViewById(R.id.iv_offline_cover_img);
			holder.titleTextView = (TextView) convertView.findViewById(R.id.tv_offline_title);
			holder.startPlaceTextView = (TextView) convertView.findViewById(R.id.tv_offline_start_place);
			holder.refferenceTextView = (TextView) convertView.findViewById(R.id.tv_offline_reference_price);
			holder.offlineStatusTextView = (TextView) convertView.findViewById(R.id.tv_offline_state);
			convertView.setTag(holder);
			ProgressManager.addProgressTextView(holder.offlineStatusTextView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final OffLine offLine = offLines.get(position);
		holder.offlineStatusTextView.setTag(offLine.getTravelId()+"");
		String downloadprogress=ProgressManager.getProgressbyDownloadPath(offLine.getTravelId());
		if (downloadprogress!=null) {
			holder.offlineStatusTextView.setText(downloadprogress);
		}
		holder.titleTextView.setText(offLine.getTravelName());
		holder.startPlaceTextView.setText(offLine.getStartPlace());
		holder.refferenceTextView.setText(String.valueOf(offLine.getPrice()));
		ImageLoaderUtil.loadListCoverImg(ImageLoader.getInstance(), offLine.getCoverImg(), holder.coverImageView);

		if (offLine.getDownloadStatus() == 1) {
			// 下载成功
			holder.offlineStatusTextView.setVisibility(View.INVISIBLE);
		} else if (offLine.getDownloadStatus() == -1) {
			// 下载失败
			holder.offlineStatusTextView.setVisibility(View.VISIBLE);
			holder.offlineStatusTextView.setText(R.string.re_download);
		} else if (offLine.getDownloadStatus() == 0) {
			// 下载中
			holder.offlineStatusTextView.setVisibility(View.VISIBLE);
			holder.offlineStatusTextView.setText(offLine.getProgress());
		} else if (offLine.getDownloadStatus() ==3) {
		// 下载中
		holder.offlineStatusTextView.setVisibility(View.VISIBLE);
		holder.offlineStatusTextView.setText("请稍后");
	}else {
			// 需要更新offline
			holder.offlineStatusTextView.setVisibility(View.VISIBLE);
			holder.offlineStatusTextView.setText("更新");
		}
		// 获取下载进度
		// DownloadManagerCallback downloadManagerCallback =
		// offLine.getCallback();
		// if (downloadManagerCallback != null) {
		// downloadManagerCallback.setDownloadCallbackInfo(new
		// DownloadCallback() {
		//
		// @Override
		// public void onSuccess() {
		// notifyDataSetChanged();
		// }
		//
		// @Override
		// public void onLoading(String progress) {
		// notifyDataSetChanged();
		// }
		//
		// @Override
		// public void onFailure() {
		// notifyDataSetChanged();
		// }
		//
		// }, offLine);
		// }

		if (listener != null) {
			holder.offlineStatusTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 重新下载
					if (offLine.getDownloadStatus() == -1) {
						listener.onReDownloadClick(position);
					}
					// 更新
					if (offLine.getDownloadStatus() == 2) {
						listener.onUpdateClick(position);
					}
				}
			});
		}

		return convertView;
	}

	static class ViewHolder {
		ImageView coverImageView; // 封面
		TextView titleTextView; // 标题
		TextView startPlaceTextView; // 出发
		TextView refferenceTextView; // 参考价格
		TextView offlineStatusTextView; // 离线状态
	}

	public interface ListItemRequestListener {
		void onReDownloadClick(int position);

		void onUpdateClick(int position);
	}

	private ListItemRequestListener listener;

	public void setListItemRequestListener(ListItemRequestListener listener) {
		this.listener = listener;
	}

}
