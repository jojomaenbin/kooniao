package com.kooniao.travel.mine;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.kooniao.travel.BottomTabBarActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.api.ApiCaller.APICurrentResultCallback;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.customwidget.KooniaoDatePickerDialog;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.customwidget.SwipeListView;
import com.kooniao.travel.customwidget.SwipeListView.SideSlipOptionCallback;
import com.kooniao.travel.discovery.TravelDetailActivity_;
import com.kooniao.travel.manager.DownloadManager;
import com.kooniao.travel.manager.DownloadManager.DownloadCallback;
import com.kooniao.travel.manager.DownloadService;
import com.kooniao.travel.manager.TravelAlarmManager;
import com.kooniao.travel.manager.TravelManager;
import com.kooniao.travel.manager.TravelManager.GetOfflineResultCallback;
import com.kooniao.travel.manager.TravelManager.MyTravelResultCallback;
import com.kooniao.travel.manager.TravelManager.StringResultCallback;
import com.kooniao.travel.manager.TravelManager.TravelDetailResultCallback;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.model.Alarm;
import com.kooniao.travel.model.DayList;
import com.kooniao.travel.model.MyTravel;
import com.kooniao.travel.model.OffLine;
import com.kooniao.travel.model.TravelDetail;
import com.kooniao.travel.model.UserTravel;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.DateUtil;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.NetUtil;
import com.kooniao.travel.utils.ViewUtils;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * 我的行程
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_my_travel)
public class MyTravelActivity extends BaseActivity implements SideSlipOptionCallback, MyTravelAdapter.ListItemRequestListener {
	@ViewById(R.id.swipe_refresh_layout)
	PtrFrameLayout refreshLayout; // 下拉刷新
	@ViewById(R.id.listview)
	SwipeListView swipeListView;
	@ViewById(R.id.iv_travel_help)
	ImageView helpTipsImageView; // 帮助页
	@ViewById(R.id.layout_no_data)
	View noDataLayout; // 无数据布局

	@AfterViews
	void init() {
		initData();
		initView();
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
	}

	private int myTravelCount; // 我的行程数目
	private int myOfflineCount; // 我的离线行程数目
	private ImageLoader imageLoader;
	private MyTravelAdapter adapter;
	private Alarm alarm; // 闹钟

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			myTravelCount = intent.getIntExtra(Define.TRAVEL_COUNT, 0);
			alarm = (Alarm) intent.getSerializableExtra("alarm");
		}
		// 初始化图片加载器
		imageLoader = ImageLoader.getInstance();
		adapter = new MyTravelAdapter(MyTravelActivity.this);
	}

	KooniaoProgressDialog progressDialog;

	/**
	 * 初始化界面
	 */
	private void initView() {
		if (progressDialog == null) {
			progressDialog = new KooniaoProgressDialog(MyTravelActivity.this);
		}
		// 设置侧滑
		swipeListView.setBackViewOffSet(Define.widthPx * 1 / 2);
		swipeListView.setSideSlipOptionCallback(this);
		// 配置listview
		adapter.setOnListItemRequestListener(this);
		swipeListView.setAdapter(adapter);
		// 设置滑动监听
		swipeListView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true, new ListViewScrollListener()));
		swipeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position = position - swipeListView.getHeaderViewsCount();
				itemClick(position);
			}
		});
		// 下拉刷新配置
		MaterialHeader materialHeader = new MaterialHeader(this);
		materialHeader.setPadding(0, ViewUtils.dpToPx(15, getResources()), 0, ViewUtils.dpToPx(15, getResources()));

		refreshLayout.setHeaderView(materialHeader);
		refreshLayout.addPtrUIHandler(materialHeader);
		refreshLayout.setPtrHandler(new PtrHandler() {
			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
				return PtrDefaultHandler.checkContentCanBePulledDown(frame, swipeListView, header);
			}

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				onRefresh();
			}
		});

		// 三天前提醒点击状态栏进来
		if (alarm != null) {
			String dialogMessage = alarm.getAlarmTitle() + ",您是否准备好了";
			dialog = new Dialog(MyTravelActivity.this, dialogTitle, dialogMessage);
			dialog.setCancelable(false);
			dialog.setOnAcceptButtonClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					if (!progressDialog.isShowing()) {
						progressDialog.show();
					}
					TravelManager.getInstance().setCurrentTravel(alarm.getTravelId(), new APICurrentResultCallback() {

						@Override
						public void result(String errMsg, int oldMtime, int newMtime) {
							progressDialog.dismiss();
							if (errMsg != null) {
								Toast.makeText(MyTravelActivity.this, errMsg, Toast.LENGTH_SHORT).show();
							} else {

								isCurrentTravelChange = true;
								handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
								DownloadManager downloadManager = DownloadService.getDownloadManager(MyTravelActivity.this);
								OffLine localOffLine = downloadManager.getOffLineByTravelId(alarm.getTravelId());
								int position = -1;
								if (localOffLine != null) {

									for (int i = 0; i < allTravels.size(); i++) {
										if (alarm.getTravelId() == allTravels.get(i).getTeamId()) {
											position = i;
										}
									}
									if (localOffLine.getmTime() < oldMtime) {
										getOfflineZip(position);
										localOffLine.setmTime(newMtime);
										localOffLine.setDownloadedTimeStamp(0);
										downloadManager.saveOrUpdateOffline(localOffLine);
										setOnTravelAlarmById(alarm.getTravelId(), false, 0);
									} else {
										localOffLine.setmTime(newMtime);
										localOffLine.setDownloadedTimeStamp(0);
										downloadManager.saveOrUpdateOffline(localOffLine);
										setOnTravelAlarmById(alarm.getTravelId(), true, newMtime);
									}
								}
							}
						}
					});

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

	/**
	 * 点击item
	 * 
	 * @param position
	 */
	private void itemClick(int position) {
		MyTravel travel = allTravels.get(position);
		Intent intent = new Intent(MyTravelActivity.this, TravelDetailActivity_.class);
		intent.putExtra(Define.PID, travel.getPlanId());
		startActivity(intent);
	}

	// 滑动监听
	private class ListViewScrollListener implements OnScrollListener {
		@Override
		public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		}

		@Override
		public void onScrollStateChanged(AbsListView listview, int scrollState) {
			switch (scrollState) {
			// 当不滚动时
			case OnScrollListener.SCROLL_STATE_IDLE:
				// 判断滚动到底部
				if (!isLoadingMore) {
					if (listview.getLastVisiblePosition() == (listview.getCount() - 1)) {
						if (currentPageNum < pageCount) {
							currentPageNum++;
							loadMyTravelList();
						} else {
							handler.sendEmptyMessageAtTime(NORE_MORE_DATA, 100);
						}
					}
				}
				break;
			}
		}
	}

	private boolean isLoadingMore = false; // 是否正在加载更多

	/**
	 * 下拉刷新
	 */
	private void onRefresh() {
		currentPageNum = 1;
		isNeedToShowNoMoreTips = true;
		loadMyTravelList();
	}

	/**
	 * 加载我的行程列表
	 */
	private void loadMyTravelList() {
		if (currentPageNum > 1) {
			isLoadingMore = true;
		}
		TravelManager.getInstance().loadMyTravelList(currentPageNum, new MyTravelResultCallback() {

			@Override
			public void result(String errMsg, List<MyTravel> allTravels, List<MyTravel> teamTravels, int pageCount) {
				loadMyTravelListComplete(errMsg, allTravels, teamTravels, pageCount);
			}
		});
	}

	private List<MyTravel> allTravels = new ArrayList<MyTravel>(); // 全部行程

	/**
	 * 获取我的行程列表请求完成
	 * 
	 * @param errMsg
	 * @param allTravels
	 * @param teamTravels
	 * @param pageCount
	 */
	private void loadMyTravelListComplete(String errMsg, List<MyTravel> allTravels, List<MyTravel> teamTravels, int pageCount) {
		if (currentPageNum > 1) {
			isLoadingMore = false;
		}
		refreshLayout.refreshComplete();
		this.pageCount = pageCount;
		if (errMsg == null && allTravels != null && teamTravels != null) {
			if (currentPageNum == 1) {
				if (allTravels.isEmpty()) {
					noDataLayout.setVisibility(View.VISIBLE);
				} else {
					this.allTravels.clear();
					this.allTravels = allTravels;
					noDataLayout.setVisibility(View.GONE);
				}
			} else {
				this.allTravels.addAll(allTravels);
			}
			if (!allTravels.isEmpty() && this.allTravels.get(0).getCurrent() == 1) {
				this.allTravels.get(0).setOfflineDownloadState(3);
				DataBase db = LiteOrm.newInstance(getApplicationContext(), Define.DB_NAME);
				UserTravel usertravel = db.queryById(AppSetting.getInstance().getIntPreferencesByKey(Define.UID), UserTravel.class);
				if (usertravel != null && usertravel.getTravelid() == this.allTravels.get(0).getPlanId()) {
					this.allTravels.get(0).setReceiveTravelRemind(usertravel.isReceiveTravelRemind());
				}
			}

			adapter.setTravels(this.allTravels);
			// 设置团队行程前提醒闹钟
			for (MyTravel myTravel : teamTravels) {
				TravelAlarmManager.getInstance().setBeforeTravelAlarm(MyTravelActivity.this, myTravel);
			}
		} else {
			Toast.makeText(MyTravelActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	private int currentPageNum = 1; // 当前页码
	private int pageCount; // 总共页码

	private boolean isNeedToShowNoMoreTips = true; // 是否需要提示没有更多数据了
	@StringRes(R.string.no_more_data)
	String noreMoreDataTips; // 没有更多数据的提示语

	final int REFRESH_DATA = 1; // 刷新数据
	final int NORE_MORE_DATA = 2; // 没有更多数据

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH_DATA: // 刷新数据
				swipeListView.setSelection(0);
				refreshLayout.autoRefresh(true);
				break;

			case NORE_MORE_DATA: // 没有更多数据
				if (isNeedToShowNoMoreTips) {
					isNeedToShowNoMoreTips = false;
					Toast.makeText(MyTravelActivity.this, noreMoreDataTips, Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		activityFinish();
	}

	private boolean isDataChange = false; // 数据是否发生改变

	/**
	 * 结束当前界面
	 */
	private void activityFinish() {
		Intent intent = null;
		if (alarm != null) {
			intent = new Intent(MyTravelActivity.this, BottomTabBarActivity_.class);
			startActivity(intent);
		} else {
			intent = new Intent();
			intent.putExtra(Define.CURRENT_TRAVEL_CHANGE, isCurrentTravelChange);
			intent.putExtra(Define.DATA, isDataChange);
			List<OffLine> allOffLines = DownloadService.getDownloadManager(MyTravelActivity.this).getAllOffLines();
			if (allOffLines != null) {
				myOfflineCount = allOffLines.size();
				intent.putExtra(Define.OFFLINE_COUNT, myOfflineCount);
			}
			intent.putExtra(Define.TRAVEL_COUNT, myTravelCount);
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	@Override
	public void onLoadCoverImgListener(String imgUrl, ImageView coverImageView) {
		ImageLoaderUtil.loadListCoverImg(imageLoader, imgUrl, coverImageView);
	}

	@StringRes(R.string.mine_travel)
	String dialogTitle; // 对话框标题
	@StringRes(R.string.sure_delete_travel)
	String dialogMessage; // 对话框内容
	Dialog dialog; // 删除收藏确认对话框

	/**
	 * menuType(1:remind,2:calendar,3:delete)
	 */
	@Override
	public void onSideSlipOptionSelected(int menuType, final int position) {
		final MyTravel travel = allTravels.get(position);
		if (menuType == 1) {
			popupAlarmSelectView(travel);
		} else if (menuType == 2) {
			final Time time = new Time();
			time.setToNow();
			KooniaoDatePickerDialog datePickerDialog = new KooniaoDatePickerDialog(MyTravelActivity.this, new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					String timeString = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " " + time.hour + ":" + time.minute + ":" + time.second;
					long timeStamp = DateUtil.strToTimestamp(timeString);
					updateMyTravelDate(timeStamp, position); // 修改行程出发时间
				}
			}, time.year, time.month, time.monthDay);
			datePickerDialog.show();
		} else if (menuType == 3) {
			if (travel.getCurrent() == 1) {
				// 取消当前行程
				TravelManager.getInstance().cancelCurrentTravel(travel.getPlanId(), new StringResultCallback() {

					@Override
					public void result(String errMsg) {
						if (errMsg == null) {
							// 取消提醒闹钟
							DataBase db = LiteOrm.newInstance(getApplicationContext(), Define.DB_NAME);
							UserTravel usertravel = db.queryById(AppSetting.getInstance().getIntPreferencesByKey(Define.UID), UserTravel.class);
							if (usertravel != null) {
								usertravel.setTravelable(false);
								db.update(usertravel);
							}
							isCurrentTravelChange = true;
							TravelAlarmManager.getInstance().cancelOnTravelAlarm(MyTravelActivity.this);
							handler.sendEmptyMessageDelayed(REFRESH_DATA, 200);
						} else {
							Toast.makeText(MyTravelActivity.this, errMsg, Toast.LENGTH_SHORT).show();
						}
					}
				});
			} else {
				// 删除行程
				dialog = new Dialog(MyTravelActivity.this, dialogTitle, dialogMessage);
				dialog.setCancelable(false);
				dialog.setOnAcceptButtonClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						deleteTravel(travel);
						TravelAlarmManager.getInstance().cancelTravelAlarm(MyTravelActivity.this, travel);
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
	}

	/**
	 * 更改我的行程时间
	 * 
	 * @param timeStamp
	 * @param position
	 */
	private void updateMyTravelDate(final long timeStamp, final int position) {
		final MyTravel travel = allTravels.get(position);
		final int travelId = travel.getPlanId();
		/**
		 * 判断行程日期是否是设置为今天
		 */
		long currentTime = System.currentTimeMillis() / 1000; // 当前时间
		String currentDate = DateUtil.timeDistanceString(currentTime, Define.FORMAT_YMD);
		final String modifyDate = DateUtil.timeDistanceString(timeStamp, Define.FORMAT_YMD); // 修改的时间
		if (currentDate.equals(modifyDate)) { // 设置日期为当天（设为当前行程）
			boolean hasCurrentTravel = allTravels.get(0).getCurrent() == 1 ? true : false; // 是否有当前行程
			setCurrentTravel(hasCurrentTravel, position);
		} else {
			long timeSettingStamp = DateUtil.strToTimestamp(DateUtil.timeDistanceString(timeStamp, Define.FORMAT_YMD), Define.FORMAT_YMD);
			long currentTimeSettingStamp = DateUtil.strToTimestamp(DateUtil.timeDistanceString(currentTime, Define.FORMAT_YMD), Define.FORMAT_YMD);
			if (timeSettingStamp < currentTimeSettingStamp) {
				Toast.makeText(MyTravelActivity.this, R.string.please_input_correct_date, Toast.LENGTH_SHORT).show();
			} else {
				if (!progressDialog.isShowing()) {
					progressDialog.show();
				}
				/**
				 * 请求服务器修改时间
				 */
				TravelManager.getInstance().updateMyTravelDate(timeStamp, travelId, new StringResultCallback() {

					@Override
					public void result(String errMsg) {
						progressDialog.dismiss();
						if (errMsg == null) {
							// 刷新数据
							handler.sendEmptyMessageDelayed(REFRESH_DATA, 300);
							// 设置行程前提醒
							travel.setStartTime(timeStamp);
							TravelAlarmManager.getInstance().setBeforeTravelAlarm(MyTravelActivity.this, travel);
						} else {
							Toast.makeText(MyTravelActivity.this, errMsg, Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		}
	}

	private boolean isCurrentTravelChange = false; // 是否更改了当前行程
	private MyTravel myTravel;

	/**
	 * 设置为当前行程
	 * 
	 * @param position
	 */
	private void setCurrentTravel(boolean hasCurrentTravel, final int position) {
		if (hasCurrentTravel) {
			dialogMessage = "是否用该行程替换当前行程？";
		} else {
			dialogMessage = "是否设置为当前行程？";
		}

		final int travelId = allTravels.get(position).getPlanId();
		myTravel = allTravels.get(position);
		dialog = new Dialog(MyTravelActivity.this, dialogTitle, dialogMessage);
		dialog.setCancelable(false);
		dialog.setOnAcceptButtonClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (!progressDialog.isShowing()) {
					progressDialog.show();
				}

				TravelManager.getInstance().setCurrentTravel(travelId, new APICurrentResultCallback() {

					@Override
					public void result(String errMsg, int oldMtime, int newMtime) {
						progressDialog.dismiss();
						if (errMsg != null) {
							Toast.makeText(MyTravelActivity.this, errMsg, Toast.LENGTH_SHORT).show();
						} else {
							DataBase db = LiteOrm.newInstance(getApplicationContext(), Define.DB_NAME);
							UserTravel usertravel = db.queryById(AppSetting.getInstance().getIntPreferencesByKey(Define.UID), UserTravel.class);
							if (usertravel != null) {
								usertravel.setTravelable(true);
								db.update(usertravel);
							} else {
								usertravel = new UserTravel();
								usertravel.setReceiveTravelRemind(true);
								usertravel.setUserid(AppSetting.getInstance().getIntPreferencesByKey(Define.UID));
								usertravel.setTravelid(myTravel.getPlanId());
								usertravel.setTravelbengin(myTravel.getStartTime());
								usertravel.setTravelend(myTravel.getStartTime() + 1 * 60 * 60 * 24);
								usertravel.setTravelable(true);
								db.save(usertravel);
							}
							isCurrentTravelChange = true;
							handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
							DownloadManager downloadManager = DownloadService.getDownloadManager(MyTravelActivity.this);
							OffLine localOffLine = downloadManager.getOffLineByTravelId(travelId);
							if (localOffLine != null) {
								if (localOffLine.getmTime() < oldMtime) {
									getOfflineZip(position);
									localOffLine.setmTime(newMtime);
									localOffLine.setDownloadedTimeStamp(0);
									downloadManager.saveOrUpdateOffline(localOffLine);
									setOnTravelAlarmById(travelId, false, 0);
								} else {
									localOffLine.setmTime(newMtime);
									localOffLine.setDownloadedTimeStamp(0);
									downloadManager.saveOrUpdateOffline(localOffLine);
									setOnTravelAlarmById(travelId, true, newMtime);
								}

							} else {
								getOfflineZip(position);
								setOnTravelAlarmById(travelId, false, 0);
							}
						}
					}
				});
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

	/**
	 * 获取离线行程下载包
	 * 
	 * @param travelId
	 */
	private void getOfflineZip(final int position) {
		if (position != -1) {
			final MyTravel travel = allTravels.get(position);
			int travelId = travel.getPlanId();
			final DownloadManager downloadManager = DownloadService.getDownloadManager(MyTravelActivity.this);
			OffLine localOffLine = downloadManager.getOffLineByTravelId(travelId);
			if (localOffLine != null) {
				downloadManager.deleteOffline(localOffLine);
			}

			TravelAlarmManager.getInstance().setBeforeTravelAlarm(MyTravelActivity.this, travel);
			TravelManager.getInstance().getOfflineZip(travelId, 1, new GetOfflineResultCallback() {

				@Override
				public void result(String errMsg, String zipPath, String zipSize, long mTime) {
					progressDialog.dismiss();
					if (errMsg == null) {
						// 构造离线下载model
						final OffLine offLine = new OffLine();
						offLine.setCoverImg(travel.getImage());
						offLine.setDownloadPath(zipPath);
						int start = zipPath.lastIndexOf("/");
						zipPath = zipPath.substring(start + 1, zipPath.length());
						offLine.setFileName(zipPath);
						offLine.setPrice(travel.getPrice());
						offLine.setStartPlace(travel.getStartCity());
						offLine.setTravelId(travel.getPlanId());
						offLine.setTravelName(travel.getName());
						offLine.setmTime(mTime);

						if (NetUtil.isWifi()) { // WiFi环境
							downloadOfflineZip(0, downloadManager, offLine);
						} else {
							dialogMessage = "您处于非WiFi网络环境，确认要下载(" + zipSize + ")行程离线包吗？";
							dialog = new Dialog(MyTravelActivity.this, dialogTitle, dialogMessage);
							dialog.setCancelable(false);
							dialog.setOnAcceptButtonClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									dialog.dismiss();
									downloadOfflineZip(0, downloadManager, offLine);
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
						Toast.makeText(MyTravelActivity.this, errMsg, Toast.LENGTH_SHORT).show();
					}
				}
			});

		}
	}

	/**
	 * 下载离线行程
	 * 
	 * @param position
	 * 
	 * @param downloadManager
	 * @param offLine
	 */
	private void downloadOfflineZip(final int position, final DownloadManager downloadManager, final OffLine offLine) {
		OffLine localOffLine = downloadManager.getOffLineByTravelId(offLine.getTravelId());
		DownloadCallback downloadCallback = new DownloadCallback() {

			@Override
			public void onSuccess() {
				allTravels.get(position).setOfflineDownloadState(2);
				adapter.setTravels(allTravels);
				// 设置行程中闹钟
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						setOnTravelAlarmById(offLine.getTravelId(), false, 0);
					}
				}, 500);
			}

			boolean firstNotify = true;

			@Override
			public void onLoading(String progress) {
				if (firstNotify) {
					firstNotify = false;
				}
			}

			@Override
			public void onFailure() {
			}
		};

		if (localOffLine != null) {
			downloadManager.resumeDownload(offLine, downloadCallback);
		} else {
			downloadManager.addNewDownload(offLine, downloadCallback);
		}
	}

	/**
	 * 设置行程中闹钟
	 * 
	 * @param travelId
	 */
	private void setOnTravelAlarmById(final int travelId, final boolean resavetravel, final int newMtime) {
		TravelManager.getInstance().loadTravelDetail(true, travelId, new TravelDetailResultCallback() {

			@Override
			public void result(String errMsg, TravelDetail travelDetail, int commentCount) {
				if (travelDetail != null) {
					if (resavetravel) {
						long travelDetailDate = travelDetail.getDayList().get(0).getDayDate();
						if (travelDetailDate < newMtime) {
							int time = (int) (newMtime - travelDetail.getDayList().get(0).getDayDate());
							for (DayList day : travelDetail.getDayList()) {
								day.setDayDate(day.getDayDate() + time);
							}
							ApiCaller.getInstance().saveTravelDetail(travelId, travelDetail);
						}
					}
					TravelAlarmManager.getInstance().setOnTravelAlarm(MyTravelActivity.this, travelId, travelDetail);
					DataBase db = LiteOrm.newInstance(getApplicationContext(), Define.DB_NAME);
					UserTravel usertravel = db.queryById(AppSetting.getInstance().getIntPreferencesByKey(Define.UID), UserTravel.class);
					if (usertravel != null) {
						if (usertravel.getTravelid() != myTravel.getPlanId()) {
							usertravel.setReceiveTravelRemind(true);
						}
						usertravel.setTravelid(myTravel.getPlanId());
						usertravel.setTravelbengin(myTravel.getStartTime());
						usertravel.setTravelend(myTravel.getStartTime() + travelDetail.getDayList().size() * 60 * 60 * 24);
						db.update(usertravel);
					} else {
						usertravel = new UserTravel();
						usertravel.setReceiveTravelRemind(true);
						usertravel.setUserid(AppSetting.getInstance().getIntPreferencesByKey(Define.UID));
						usertravel.setTravelid(myTravel.getPlanId());
						usertravel.setTravelbengin(myTravel.getStartTime());
						usertravel.setTravelend(myTravel.getStartTime() + travelDetail.getDayList().size() * 60 * 60 * 24);
						db.save(usertravel);
					}
				}
			}
		});
	}

	/**
	 * 删除我的行程
	 * 
	 * @param travel
	 */
	private void deleteTravel(MyTravel travel) {
		allTravels.remove(travel);
		adapter.setTravels(allTravels);
		TravelManager.getInstance().deleteMyTravel(travel.getPlanId(), new StringResultCallback() {

			@Override
			public void result(String errMsg) {
				if (errMsg != null) {
					Toast.makeText(MyTravelActivity.this, errMsg, Toast.LENGTH_SHORT).show();
				} else {
					isDataChange = true;
					isCurrentTravelChange = true;
					myTravelCount--;
					// 更新本地行程数目
					UserManager.getInstance().updateTravelCount(myTravelCount);
				}
			}
		});
	}

	/**
	 * 点击今日出发
	 */
	@Override
	public void onStartingTodayClickListener(int position) {
		MyTravel travel = allTravels.get(position);
		if (travel.getOfflineDownloadState() == -1) {
			getOfflineZip(position);
		} else {
			boolean hasCurrentTravel = allTravels.get(0).getCurrent() == 1 ? true : false; // 是否有当前行程
			setCurrentTravel(hasCurrentTravel, position);
		}
	}

	private PopupWindow popupWindow;

	/**
	 * 弹出闹钟选择界面
	 */
	private void popupAlarmSelectView(final MyTravel myTravel) {
		View selectPhotoView = LayoutInflater.from(MyTravelActivity.this).inflate(R.layout.popupwindow_alarmset, null);
		popupWindow = new PopupWindow(selectPhotoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		popupWindow.setFocusable(false);
		popupWindow.setAnimationStyle(R.style.PopupAnimationFromBottom);
		// 关闭按钮
		selectPhotoView.findViewById(R.id.iv_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});

		TextView alrmaccept = (TextView) selectPhotoView.findViewById(R.id.iv_alarm_accept);
		TextView alrmnotaccept = (TextView) selectPhotoView.findViewById(R.id.iv_alarm_not_accept);
		if (myTravel.isReceiveTravelRemind()) {
			alrmaccept.setClickable(false);
			alrmaccept.setTextColor(getResources().getColor(R.color.v909090));
			// 选择不接收
			selectPhotoView.findViewById(R.id.iv_alarm_not_accept).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
					myTravel.setReceiveTravelRemind(false);
					DataBase db = LiteOrm.newInstance(getApplicationContext(), Define.DB_NAME);
					UserTravel usertravel = db.queryById(AppSetting.getInstance().getIntPreferencesByKey(Define.UID), UserTravel.class);
					if (usertravel != null) {
						usertravel.setReceiveTravelRemind(false);
						db.update(usertravel);
					} else {
						Toast.makeText(MyTravelActivity.this, "设置失败，没有闹钟信息！", Toast.LENGTH_SHORT).show();
					}
					adapter.notifyDataSetChanged();
				}
			});
		} else {
			alrmnotaccept.setClickable(false);
			alrmnotaccept.setTextColor(getResources().getColor(R.color.v909090));
			// 选择接收
			selectPhotoView.findViewById(R.id.iv_alarm_accept).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
					myTravel.setReceiveTravelRemind(true);
					DataBase db = LiteOrm.newInstance(getApplicationContext(), Define.DB_NAME);
					UserTravel usertravel = db.queryById(AppSetting.getInstance().getIntPreferencesByKey(Define.UID), UserTravel.class);
					if (usertravel != null) {
						usertravel.setReceiveTravelRemind(true);
						db.update(usertravel);
					} else {
						Toast.makeText(MyTravelActivity.this, "设置失败，没有闹钟信息！", Toast.LENGTH_SHORT).show();
					}
					adapter.notifyDataSetChanged();
				}
			});
		}
		// // 选择接收
		// selectPhotoView.findViewById(R.id.iv_alarm_accept).setOnClickListener(new
		// OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// popupWindow.dismiss();
		// Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Uri uri = Uri.fromFile(new
		// File(KooniaoApplication.getInstance().getPicDir() +
		// Define.PIC_NORMAL));
		// cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		// }
		// });
		//
		// // 选择不接收
		// selectPhotoView.findViewById(R.id.iv_alarm_not_accept).setOnClickListener(new
		// OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// popupWindow.dismiss();
		// Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		// intent.setType("image/*");
		// }
		// });

		popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
	}

	@Override
	public void onBackPressed() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			super.onBackPressed();
		}

	}
}
