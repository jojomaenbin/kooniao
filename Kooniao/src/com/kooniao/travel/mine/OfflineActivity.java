package com.kooniao.travel.mine;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.customwidget.SwipeListView;
import com.kooniao.travel.customwidget.SwipeListView.SideSlipOptionCallback;
import com.kooniao.travel.discovery.TravelDetailActivity_;
import com.kooniao.travel.manager.DownloadManager.DownloadCallback;
import com.kooniao.travel.manager.DownloadManager.DownloadManagerCallback;
import com.kooniao.travel.manager.DownloadService;
import com.kooniao.travel.manager.TravelManager;
import com.kooniao.travel.manager.TravelManager.GetOfflineResultCallback;
import com.kooniao.travel.manager.TravelManager.OfflineUpdateInfoListResultCallback;
import com.kooniao.travel.model.OffLine;
import com.kooniao.travel.model.OfflineUpdateInfo;
import com.kooniao.travel.utils.JsonTools;
import com.kooniao.travel.utils.NetUtil;
import com.kooniao.travel.utils.SortListCollections;
import com.kooniao.travel.utils.SortListCollections.Sort;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * 离线页面
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_swipe_listview)
public class OfflineActivity extends BaseActivity implements SideSlipOptionCallback, OfflineAdapter.ListItemRequestListener {
	@ViewById(R.id.listview)
	SwipeListView swipeListView;
	@ViewById(R.id.layout_no_data)
	View noDataLayout; // 无数据布局

	@AfterViews
	void init() {
		initData();
		initView();
	}

	private ImageLoader imageLoader;
	private OfflineAdapter adapter;
	private List<OffLine> offLines = new ArrayList<OffLine>();

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		// 初始化图片加载器
		imageLoader = ImageLoader.getInstance();
		// 获取本地离线数据
		offLines = DownloadService.getDownloadManager(OfflineActivity.this).getAllOffLines();
		// 按下载时间排序
		SortListCollections<OffLine> sortListCollections = new SortListCollections<OffLine>();
		sortListCollections.sort(offLines, "downloadedTimeStamp", Sort.DESC);
		adapter = new OfflineAdapter(OfflineActivity.this);
		adapter.setListItemRequestListener(this);
		adapter.setOfflines(offLines);
		if (offLines.isEmpty()) {
			noDataLayout.setVisibility(View.VISIBLE);
		} else {
			noDataLayout.setVisibility(View.GONE);
		}
		// 检查离线更新
		checkOfflineUpdate();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 设置侧滑
		swipeListView.setBackViewOffSet(Define.widthPx * 1 / 4);
		swipeListView.setSideSlipOptionCallback(this);
		swipeListView.setAdapter(adapter);
		// 设置滑动监听
		swipeListView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true));
		swipeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position = position - swipeListView.getHeaderViewsCount();
				itemClick(position);
			}
		});
	}

	/**
	 * 检查离线更新
	 */
	private void checkOfflineUpdate() {
		List<String> offlineIdList = new ArrayList<String>();
		if (offLines != null) {
			for (OffLine offLine : offLines) {
				offlineIdList.add(String.valueOf(offLine.getTravelId()));
			}
		}

		String offlineIdJsonArray = JsonTools.listToJson(offlineIdList);
		TravelManager.getInstance().checkOfflineUpdate(offlineIdJsonArray, new OfflineUpdateInfoListResultCallback() {

			@Override
			public void result(String errMsg, List<OfflineUpdateInfo> offlineUpdateInfos) {
				if (errMsg == null) {
					for (OffLine offLine : offLines) {
						for (OfflineUpdateInfo offlineUpdateInfo : offlineUpdateInfos) {
							if (offLine.getTravelId() == offlineUpdateInfo.getPlanId() && offLine.getmTime() < offlineUpdateInfo.getMtime()) {
								offLine.setDownloadStatus(2);
							}
						}
					}

					adapter.setOfflines(offLines);
				} else {
					Toast.makeText(OfflineActivity.this, errMsg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 点击item
	 * 
	 * @param position
	 */
	private void itemClick(int position) {
		OffLine offLine = offLines.get(position);
		if (offLine != null && (offLine.getDownloadStatus() == 1 || offLine.getDownloadStatus() == 2)) {
			Intent intent = new Intent(OfflineActivity.this, TravelDetailActivity_.class);
			intent.putExtra(Define.PID, offLine.getTravelId());
			intent.putExtra(Define.IS_OFFLINE, true);
			startActivity(intent);
		}
	}

	/**
	 * 点击后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		activityFinish();
	}

	@Override
	public void onBackPressed() {
		activityFinish();
	}

	/**
	 * 结束当前界面
	 */
	private void activityFinish() {
		Intent intent = new Intent();
		intent.putExtra(Define.DATA, isDataChange);
		setResult(RESULT_OK, intent);
		finish();
	}

	@StringRes(R.string.offline_travel)
	String dialogTitle; // 对话框标题
	@StringRes(R.string.sure_delete_offline)
	String dialogMessage; // 对话框内容
	Dialog dialog; // 删除收藏确认对话框

	@Override
	public void onSideSlipOptionSelected(int menuType, final int position) {
		if (menuType == 3) {
			dialog = new Dialog(OfflineActivity.this, dialogTitle, dialogMessage);
			dialog.setCancelable(false);
			dialog.setOnAcceptButtonClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					deleteOffline(position);
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
	}

	private boolean isDataChange = false;

	/**
	 * 删除离线
	 * 
	 * @param position
	 */
	private void deleteOffline(final int position) {
		isDataChange = true;
		OffLine offLine = offLines.get(position);
		offLines.remove(position);
		adapter.setOfflines(offLines);
		DownloadService.getDownloadManager(OfflineActivity.this).deleteOffline(offLine);
		if (offLines.isEmpty()) {
			noDataLayout.setVisibility(View.VISIBLE);
		} else {
			noDataLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onReDownloadClick(int position) {
		final OffLine offLine = offLines.get(position);
		downloadOffline(position, offLine);
	}

	/**
	 * 下载
	 * 
	 * @param offLine
	 */
	private void downloadOffline(int position, final OffLine offLine) {
		DownloadManagerCallback downloadManagerCallback = offLine.getCallback();
		if (downloadManagerCallback != null) {
			DownloadService.getDownloadManager(OfflineActivity.this).resumeDownload(offLine, downloadManagerCallback.getDownloadCallback());
		} else {
			offLines.remove(offLine);
			offLines.add(position, offLine);
			DownloadService.getDownloadManager(OfflineActivity.this).addNewDownload(offLine, new DownloadCallback() {

				@Override
				public void onSuccess() {
				}

				@Override
				public void onLoading(String progress) {
				}

				@Override
				public void onFailure() {
				}
			});

			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onUpdateClick(int position) {
		OffLine offLine = offLines.get(position);
		offLine.setDownloadStatus(3);
		offLine.setProgress("请稍后");
		adapter.setOfflines(offLines);
		getOfflineZip(position, offLine);
	}

	/**
	 * 获取离线下载包
	 */
	private void getOfflineZip(final int position, final OffLine offLine) {
		TravelManager.getInstance().getOfflineZip(offLine.getTravelId(), 1, new GetOfflineResultCallback() {

			@Override
			public void result(String errMsg, String zipPath, String zipSize, long mTime) {
				getOfflineZipComplete(position, offLine, errMsg, zipPath, zipSize, mTime);
			}
		});
	}

	/**
	 * 获取离线下载包请求完成
	 * 
	 * @param errMsg
	 * @param zipPath
	 * @param zipSize
	 */
	private void getOfflineZipComplete(final int position, final OffLine offLine, String errMsg, String zipPath, String zipSize, long mTime) {
		if (errMsg == null && zipPath != null) {
			offLine.setDownloadPath(zipPath);
			int start = zipPath.lastIndexOf("/");
			zipPath = zipPath.substring(start + 1, zipPath.length());
			offLine.setFileName(zipPath);
			offLine.setmTime(mTime);
			if (NetUtil.isWifi()) { // WiFi环境
				downloadOffline(position, offLine);
			} else {
				dialogMessage = "您处于非WiFi网络环境，确认要下载(" + zipSize + ")行程离线包吗？";
				dialog = new Dialog(OfflineActivity.this, dialogTitle, dialogMessage);
				dialog.setCancelable(false);
				dialog.setOnAcceptButtonClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						downloadOffline(position, offLine);
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
		} else {
			Toast.makeText(OfflineActivity.this, errMsg, Toast.LENGTH_SHORT).show();
			offLine.setDownloadStatus(2);
			adapter.setOfflines(offLines);
		}
	}

}
