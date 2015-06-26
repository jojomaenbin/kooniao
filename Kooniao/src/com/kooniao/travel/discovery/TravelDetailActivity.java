package com.kooniao.travel.discovery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.BaiDuMapActivity.From;
import com.kooniao.travel.BaiDuMapActivity_;
import com.kooniao.travel.ImageBrowseActivity_;
import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.customwidget.HeadText;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.customwidget.LinearListLayout;
import com.kooniao.travel.customwidget.LinearListLayout.OnChildItemClickListener;
import com.kooniao.travel.home.CommentAdapter;
import com.kooniao.travel.home.CommentAdapter.ItemRequestListener;
import com.kooniao.travel.home.CommentAdapter.Type;
import com.kooniao.travel.home.LineAdapter;
import com.kooniao.travel.manager.DownloadManager.DownloadCallback;
import com.kooniao.travel.manager.DownloadManager.DownloadManagerCallback;
import com.kooniao.travel.manager.DownloadService;
import com.kooniao.travel.manager.ProductManager;
import com.kooniao.travel.manager.TravelManager;
import com.kooniao.travel.manager.TravelManager.GetOfflineResultCallback;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.manager.ProductManager.StringResultCallback;
import com.kooniao.travel.manager.TravelManager.CommentListResultCallback;
import com.kooniao.travel.manager.TravelManager.StringListCallback;
import com.kooniao.travel.manager.TravelManager.TravelDetailResultCallback;
import com.kooniao.travel.model.Comment;
import com.kooniao.travel.model.DayList;
import com.kooniao.travel.model.OffLine;
import com.kooniao.travel.model.TravelDetail;
import com.kooniao.travel.model.TravelDetail.GuideList;
import com.kooniao.travel.onekeyshare.OnekeyShare;
import com.kooniao.travel.user.LoginActivity_;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.ColorUtil;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.InputMethodUtils;
import com.kooniao.travel.utils.NetUtil;
import com.kooniao.travel.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 行程详情页
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_travel_detail)
public class TravelDetailActivity extends BaseActivity {

	// 头部titlebar
	@ViewById(R.id.iv_travel_plan_team)
	ImageView planTeamImageView; // 团单按钮

	// 底部操作栏
	@ViewById(R.id.tv_add_love_to)
	TextView addCollectTextView; // 想去按钮
	@ViewById(R.id.tv_travel_detail_share)
	TextView shareTextView; // 分享按钮
	@ViewById(R.id.tv_travel_detail_download)
	TextView downloadTextView; // 下载按钮
	@ViewById(R.id.tv_add_my_travel)
	TextView addTravelTextView; // 添加行程按钮
	@ViewById(R.id.tv_travel_feedback)
	TextView travelFeedbackTextView; // 行程反馈按钮
	@ViewById(R.id.tv_travel_comment)
	TextView travelCommentsTextView; // 查看行程评论按钮

	// 中间内容区
	@ViewById(R.id.sv_middle)
	ScrollView scrollView;
	// 封面
	@ViewById(R.id.travel_detail_layout_cover)
	View coverLayout; // 封面布局
	@ViewById(R.id.iv_travel_cover)
	ImageView coverImageView; // 封面
	@ViewById(R.id.tv_travel_cost)
	TextView priceTextView; // 价格
	@ViewById(R.id.tv_travel_start_place)
	TextView startPlaceTextView; // 出发地
	// 标题
	@ViewById(R.id.travel_detail_layout_name)
	View travelNameLayout; // 标题布局
	@ViewById(R.id.tv_travel_detail_name)
	TextView travelNameTextView; // 行程名称
	// 行程描述
	@ViewById(R.id.ht_travel_detail_desc)
	HeadText travelDetailDescTHeadText; // 行程描述
	@ViewById(R.id.view_daylist_start)
	View startDescriptionLayout;
	@ViewById(R.id.tv_travel_detail_start_place)
	TextView startDescriptionTextView; // 开始节点
	@ViewById(R.id.lv_travel_list)
	LinearListLayout traveListLayout; // 行程节点布局
	@ViewById(R.id.view_daylist_end)
	View endDescriptionLayout;
	@ViewById(R.id.tv_travel_detail_end_place)
	TextView endDescriptionTextView; // 结束节点
	@ViewById(R.id.ht_travel_detail_fee)
	HeadText travelDetailFeeHeadText; // 行程费用说明

	// 点评模块
	@ViewById(R.id.travel_detail_layout_comment)
	View travelCommentLayout; // 点评布局
	@ViewById(R.id.rb_small_travel_rating)
	RatingBar commentRatingBar; // 点评评分条
	@ViewById(R.id.tv_travel_comment_tips)
	TextView ratingTipsTextView; // 评分条提示信息
	@ViewById(R.id.lv_travel_comment_list)
	LinearListLayout commentListLayout; // 评论列表布局
	@ViewById(R.id.et_travel_detail_comment)
	EditText commentInpuEditText; // 评论输入
	@ViewById(R.id.tv_travel_detail_content_count)
	TextView commentContentCounTextView; // 评论输入统计剩余字数

	// 导游信息模块
	@ViewById(R.id.travel_detail_layout_guide_info)
	View guideInfoLayout;
	@ViewById(R.id.iv_guide_avatar)
	ImageView guideAvatarImageView; // 导游头像
	@ViewById(R.id.tv_guide_name)
	TextView guideNameTextView; // 导游姓名
	@ViewById(R.id.tv_guide_tel)
	TextView guideContactPhoneTextView; // 导游联系电话
	@ViewById(R.id.tv_travel_count)
	TextView guideTravelCountTextView; // 导游行程总数
	@ViewById(R.id.tv_guide_city)
	TextView guideTravelCityTextView; // 导游带团区域

	/**
	 * 二维码模块
	 */
	@ViewById(R.id.iv_travel_detail_qrcode)
	ImageView qrCodeImageView;

	/**
	 * 圆形菜单按钮
	 */
	@ViewById(R.id.iv_travel_detail_menu)
	ImageView detailMenuImageView;

	@AfterViews
	void init() {
		initData();
		initView();
		loadTravelDetail();
	}

	private int travelId; // 行程id
	private boolean isOffline; // 是否是离线行程
	private ImageLoader imageLoader;
	private LineAdapter lineAdapter;
	private PopupMenuTravelListAdapter menuTravelListAdapter;
	private CommentAdapter commentAdapter;
	private int commentClickIndex = -1; // 回复评论item位置

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			travelId = intent.getIntExtra(Define.PID, 0);
			isOffline = intent.getBooleanExtra(Define.IS_OFFLINE, false);
		}

		imageLoader = ImageLoader.getInstance();
		menuTravelListAdapter = new PopupMenuTravelListAdapter(TravelDetailActivity.this);
		commentAdapter = new CommentAdapter(TravelDetailActivity.this, Type.TYPE_REPLY_VISIBLE);
		commentAdapter.setOnItemRequestListener(new ItemRequestListener() {

			@Override
			public void onReplyCommentClick(int position) {
				commentClickIndex = position;
				// 回复评论
				int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
				if (uid == 0) {
					Intent logIntent = new Intent(TravelDetailActivity.this, LoginActivity_.class);
					startActivityForResult(logIntent, RESULT_CODE_LOGIN_REPLY_COMMENT);
				} else {
					replyComment();
				}
			}
		});
	}

	KooniaoProgressDialog progressDialog;
	PopupWindow menuPopupWindow;
	View menuFeeDescLayout; // 菜单中的费用说明布局
	LinearListLayout menuTravelListLayout;
	View commentFootView; // 评论底部加载更多评论布局
	ScaleAnimation scaleAnimation; // 缩放动画

	/**
	 * 初始化界面
	 */
	private void initView() {
		progressDialog = new KooniaoProgressDialog(TravelDetailActivity.this);
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}

		// 设置按钮的动画
		scaleAnimation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setInterpolator(new OvershootInterpolator());
		scaleAnimation.setDuration(250);

		// 监听评分变化
		commentRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				if (rating == 1) {
					ratingTipsTextView.setText(R.string.very_yawp); // 十分不满意
				} else if (rating == 2) {
					ratingTipsTextView.setText(R.string.yawp); // 不满意
				} else if (rating == 3) {
					ratingTipsTextView.setText(R.string.gernal_satisfaction); // 一般
				} else if (rating == 4) {
					ratingTipsTextView.setText(R.string.satisfaction); // 满意
				} else if (rating == 5) {
					ratingTipsTextView.setText(R.string.very_satisfaction); // 十分满意
				}
			}
		});

		// 菜单布局
		View menuLayout = LayoutInflater.from(TravelDetailActivity.this).inflate(R.layout.popupwindow_travel_detail_menu, null);
		menuPopupWindow = new PopupWindow(menuLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		menuPopupWindow.setFocusable(false);
		menuPopupWindow.setAnimationStyle(R.style.PopupAnimationFromRight);
		menuTravelListLayout = (LinearListLayout) menuLayout.findViewById(R.id.lv_menu_travel_list);
		// 回到顶部
		menuLayout.findViewById(R.id.tv_travel_detail_popupmenu_goto_top).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				scrollToY(coverLayout.getTop(), menuPopupWindow);
			}
		});
		// 费用说明
		menuFeeDescLayout = menuLayout.findViewById(R.id.ll_travel_detail_fee_desc);
		menuLayout.findViewById(R.id.tv_travel_detail_popupmenu_fee).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				scrollToY(travelDetailFeeHeadText.getTop(), menuPopupWindow);
			}
		});
		// 点评
		menuLayout.findViewById(R.id.tv_travel_detail_popupmenu_comment).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				scrollToY(travelCommentLayout.getTop(), menuPopupWindow);
			}
		});
		// TA的线路
		menuLayout.findViewById(R.id.tv_travel_detail_popupmenu_guide_info).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				scrollToY(guideInfoLayout.getTop(), menuPopupWindow);
			}
		});
		// 点击menu其他地方隐藏menu
		menuLayout.findViewById(R.id.fl_travel_detail_popupmenu).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hidePopupWindow(menuPopupWindow);
				return false;
			}
		});

		// 初始化评论加载更多布局
		commentFootView = LayoutInflater.from(getBaseContext()).inflate(R.layout.sub_comment_item_footer, null);
		commentFootView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!progressDialog.isShowing()) {
					progressDialog.show();
				}
				
				if (mIsRequestSuccess) {
					if (TravelDetailActivity.this.comments.size() <= 2) {
						commentPageSize = 5;
					} else {
						commentPageSize = TravelDetailActivity.this.comments.size() + 5;
					}
				}
				handler.sendEmptyMessage(MSG_LOAD_COMMENT_LIST);
			}
		});
	}

	final int MSG_LOAD_COMMENT_LIST = 0; // 加载评论列表

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LOAD_COMMENT_LIST: // 加载评论列表
				loadCommentList();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	/**
	 * 缓慢滚动到指定的y轴的位置
	 * 
	 * @param scrollView
	 * @param y
	 */
	private void scrollToY(final int y, PopupWindow popupWindow) {
		scrollView.smoothScrollTo(0, y);
		hidePopupWindow(popupWindow);
	}

	/**
	 * 隐藏popupwindow
	 * 
	 * @param popupWindow
	 */
	private void hidePopupWindow(final PopupWindow popupWindow) {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}

	/**
	 * 获取行程详情
	 */
	private void loadTravelDetail() {
		TravelManager.getInstance().loadTravelDetail(isOffline, travelId, new TravelDetailResultCallback() {

			@Override
			public void result(String errMsg, TravelDetail travelDetail, int commentCount) {
				loadTravelDetailComplete(errMsg, travelDetail, commentCount);
			}
		});
	}

	private int commentCount; // 评论总数
	private TravelDetail travelDetail;

	/**
	 * 获取行程详情完成
	 * 
	 * @param errMsg
	 * @param travelDetail
	 * @param commentCount
	 */
	private void loadTravelDetailComplete(String errMsg, TravelDetail travelDetail, int commentCount) {
		progressDialog.dismiss();
		if (isNeedToInit) {
			if (errMsg == null && travelDetail != null) {
				this.travelDetail = travelDetail;
				this.commentCount = commentCount;
				setViewInfo();
			} else {
				Toast.makeText(TravelDetailActivity.this, errMsg, Toast.LENGTH_SHORT).show();
			}
		}
	}

	private List<Comment> comments; // 页面评论列表

	/**
	 * 设置界面信息
	 */
	private void setViewInfo() {
		/**
		 * 团单操作按钮、查看行程点评按钮是否显示
		 */
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID); // 本地用户id
		if (travelDetail.getTeamId() == 0) { // 没有团单
			planTeamImageView.setVisibility(View.INVISIBLE); // 团单操作按钮
			travelCommentsTextView.setVisibility(View.GONE); // 查看行程点评
			if (uid == travelDetail.getUid()) {
				added();
			}
			addTravelTextView.setVisibility(View.VISIBLE); // 添加行程
		} else {
			if (travelDetail.getDragoman() == 1) { // 是导游
				planTeamImageView.setVisibility(View.VISIBLE); // 团单操作按钮
				travelCommentsTextView.setVisibility(View.VISIBLE); // 查看行程点评
				addTravelTextView.setVisibility(View.GONE); // 添加行程
			} else if (travelDetail.getStaff() == 1) {
				travelFeedbackTextView.setVisibility(View.VISIBLE); // 行程反馈按钮
				addTravelTextView.setVisibility(View.GONE); // 添加行程
			} else {
				travelFeedbackTextView.setVisibility(View.GONE); // 行程反馈按钮
				if (uid == travelDetail.getUid()) {
					added();
				}
				addTravelTextView.setVisibility(View.VISIBLE); // 添加行程
			}
		}

		/**
		 * 圆形菜单
		 */
		List<DayList> dayLists = travelDetail.getDayList();
		menuTravelListAdapter.setDayList(dayLists);
		menuTravelListLayout.setBaseAdapter(menuTravelListAdapter);
		menuTravelListLayout.setOnChildItemClickListener(new OnChildItemClickListener() {

			@Override
			public void onChildItemClickListener(View groupView, View childView, int groupPosition, int childPosition) {
				/**
				 * 计算滑动距离
				 */
				int topHeight = traveListLayout.getTop();
				if (groupPosition > 0) {
					int index = 0;
					for (int i = groupPosition - 1; i >= 0; i--) {
						index += traveListLayout.getExpandableAdapter().getChildrenCount(i);
					}

					index = index + groupPosition;
					topHeight = topHeight + traveListLayout.getChildAt(index).getTop();
				}

				scrollToY(topHeight, menuPopupWindow);
			}
		}, false);

		/**
		 * 设置头部信息
		 */
		// 加载封面
		coverImageView.setBackgroundColor(ColorUtil.getRandomColorRes());
		String coverImg = travelDetail.getImage();
		if (isOffline) {
			coverImg = "file://" + KooniaoApplication.getInstance().getDownloadDir() + travelId + "/source/picture/image.jpeg";
		}
		ImageLoaderUtil.loadListCoverImg(imageLoader, coverImg, coverImageView);
		// 设置出发地
		String startPlace = travelDetail.getTraffic_departure();
		startPlaceTextView.setText(startPlace);
		// 设置行程价格
		float travelPrice = travelDetail.getRefer_price();
		priceTextView.setText(String.valueOf(travelPrice));

		/**
		 * 设置行程名称
		 */
		String travelName = travelDetail.getTitle();
		travelNameTextView.setText(travelName);

		/**
		 * 行程描述
		 */
		String desc = travelDetail.getDescription();
		travelDetailDescTHeadText.setTitle("行程描述");
		travelDetailDescTHeadText.setSingleContent(desc);
		// 开始节点
		String startDescription = travelDetail.getStartDescription();
		startDescriptionTextView.setText(startDescription);
		startDescriptionLayout.setVisibility(View.VISIBLE);
		// 行程节点列表
		lineAdapter = new LineAdapter(TravelDetailActivity.this, dayLists, isOffline, travelId);
		traveListLayout.setExpandadleAdapter(lineAdapter);
		// 结束节点
		String endDescription = travelDetail.getEndDescription();
		endDescriptionTextView.setText(endDescription);
		endDescriptionLayout.setVisibility(View.VISIBLE);

		/**
		 * 行程费用说明
		 */
		String fee = travelDetail.getDetail_cost();
		travelDetailFeeHeadText.setTitle("费用说明");
		travelDetailFeeHeadText.setSingleContent(fee);
		if (fee.equals("") || fee.equals("0")) {
			menuFeeDescLayout.setVisibility(View.GONE); // 隐藏菜单中的费用说明
		}

		/**
		 * 行程点评
		 */
		comments = travelDetail.getCommentList();
		commentAdapter.setComments(comments);
		commentListLayout.setBaseAdapter(commentAdapter);
		if (commentCount > 1) {
			commentListLayout.addFooterView(commentFootView);
		}

		/**
		 * 导游信息
		 */
		// 导游头像
		GuideList guideInfo = travelDetail.getGuideList();
		String guideAvatar = guideInfo.getGuideUserFace();
		ImageLoaderUtil.loadAvatar(imageLoader, guideAvatar, guideAvatarImageView);
		// 导游名字
		String guideName = guideInfo.getGuideName();
		guideNameTextView.setText(guideName);
		// 导游联系电话
		String contactPhone = guideInfo.getGuideMobile();
		if ("".equals(contactPhone)) {
			guideContactPhoneTextView.setVisibility(View.GONE);
		} else {
			guideContactPhoneTextView.setText(contactPhone);
		}
		// 导游行程数目
		int travelCount = guideInfo.getGuideTravelCount();
		guideTravelCountTextView.setText(String.valueOf(travelCount));
		// 导游带团区域
		String guideTravelCity = guideInfo.getGuideCity();
		guideTravelCityTextView.setText(guideTravelCity);

		/**
		 * 二维码
		 */
		String qrCode = travelDetail.getQRCode();
		if (isOffline) {
			qrCode = "file://" + KooniaoApplication.getInstance().getDownloadDir() + travelId + "/source/picture/QRCode.jpeg";
		}
		ImageLoaderUtil.loadListCoverImg(imageLoader, qrCode, qrCodeImageView);

		/**
		 * 底部操作栏
		 */
		int collect = travelDetail.getCollect();
		if (collect == 1) {
			addCollectTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bottombar_love_hover, 0, 0);
		}

		// 获取下载进度
		OffLine offLine = DownloadService.getDownloadManager(TravelDetailActivity.this).getOffLineByTravelId(travelId);
		if (offLine != null) {
			if (offLine.getDownloadStatus() == 1) {
				downloaded();
			} else {
				DownloadManagerCallback downloadManagerCallback = offLine.getCallback();
				if (downloadManagerCallback != null) {
					downloadManagerCallback.setDownloadCallbackInfo(downloadCallback, offLine);
				}
			}
		}
	}

	/**
	 * 已添加
	 */
	private void added() {
		addTravelTextView.setText("已添加");
		addTravelTextView.setTextColor(Color.parseColor("#118CB3"));
		addTravelTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bottombar_add_travel_press, 0, 0);
		addTravelTextView.setClickable(false);
	}

	private boolean isDownloaded = false; // 是否已经下载

	/**
	 * 已下载
	 */
	private void downloaded() {
		isDownloaded = true;
		isDownloading = false;
		downloadTextView.setText("已下载");
		downloadTextView.setTextColor(Color.parseColor("#118CB3"));
		downloadTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bottombar_download_press, 0, 0);
	}

	@Override
	public void onBackPressed() {
		activityFinish();
		super.onBackPressed();
	}

	/**
	 * 返回按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		activityFinish();
	}

	/**
	 * 团单按钮
	 */
	@Click(R.id.iv_travel_plan_team)
	void onPlanTeamClick() {
		Intent intent = new Intent(TravelDetailActivity.this, MassSingleOperationActivity_.class);
		intent.putExtra(Define.PID, travelId);
		startActivity(intent);
	}

	/**
	 * 导航按钮
	 */
	@Click(R.id.iv_navigation)
	void onNavigationClick() {
		if (travelDetail != null) {
			Intent intent = new Intent(TravelDetailActivity.this, BaiDuMapActivity_.class);
			Bundle extras = new Bundle();
			extras.putInt(Define.FROM, From.FROM_TRAVEL_DETAIL.from);
			extras.putSerializable(Define.DAY_LIST, (Serializable) travelDetail.getDayList());
			intent.putExtras(extras);
			startActivity(intent);
		}
	}

	/**
	 * 点击封面进入大图模式
	 */
	@Click(R.id.travel_detail_layout_cover)
	void onCoverImageClick() {
		TravelManager.getInstance().loadTravelLargeImageList(isOffline, travelId, new StringListCallback() {

			@Override
			public void result(String errMsg, List<String> imgList) {
				loadTravelLargeImageListComplete(errMsg, imgList);
			}
		});
	}

	/**
	 * 获取大图列表完成
	 * 
	 * @param errMsg
	 * @param imgList
	 */
	@UiThread
	void loadTravelLargeImageListComplete(String errMsg, List<String> imgList) {
		if (errMsg == null) {
			Intent intent = new Intent(getBaseContext(), ImageBrowseActivity_.class);
			intent.putStringArrayListExtra(Define.IMG_LIST, (ArrayList<String>) imgList);
			startActivity(intent);
		} else {
			Toast.makeText(getBaseContext(), errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 想去按钮
	 */
	@Click(R.id.tv_add_love_to)
	void onAddCollectClick() {
		// 判断用户登录状态
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		if (uid == 0) {
			Intent logIntent = new Intent(TravelDetailActivity.this, LoginActivity_.class);
			startActivityForResult(logIntent, RESULT_CODE_LOGIN_COLLECT);
		} else {
			addOrCancelToMyCollect();
		}
	}

	/**
	 * 添加或取消到我的收藏列表
	 * 
	 * @param likeId
	 * @param isKeep
	 * @param likeType
	 * 
	 *            行程：plan 景点：location 酒店：hotel 美食：lifestyle_food
	 *            娱乐：lifestyle_funny 购物：lifestyle_shopping 产品酒店：product_hotel
	 *            产品娱乐：product_lifestyle_entertainm
	 *            产品购物：product_lifestyle_shopping
	 *            产品门票：product_location_ticket_type 产品行程：product_plan
	 */
	private void addOrCancelToMyCollect() {
		if (travelDetail != null) {
			final int isKeep = travelDetail.getCollect() == 0 ? 1 : 0;
			travelDetail.setCollect(isKeep);
			String likeType = Define.TRAVEL;
			ProductManager.getInstance().addOrCancelToMyCollect(travelId, isKeep, likeType, null, 0, new StringResultCallback() {

				@Override
				public void result(String errMsg) {
					addOrCancelToMyCollectComplete(isKeep, errMsg);
				}
			});
		}
	}

	private int travelKeep = -1;

	@StringRes(R.string.add_collect_success)
	String addToMyCollectSuccessTips; // 添加到我的收藏列表成功
	@StringRes(R.string.cancel_collect_success)
	String cancelMyCollectSuccessTips; // 取消我的收藏成功

	/**
	 * 添加取消收藏完成
	 * 
	 * @param isKeep
	 * 
	 * @param errMsg
	 */
	@UiThread
	void addOrCancelToMyCollectComplete(int isKeep, String errMsg) {
		if (errMsg == null) {
			travelKeep = isKeep;
			UserManager.getInstance().undateCollectCount(isKeep);
			if (isKeep == 1) {
				addCollectTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bottombar_love_hover, 0, 0);
				Toast.makeText(TravelDetailActivity.this, addToMyCollectSuccessTips, Toast.LENGTH_SHORT).show();
			} else {
				addCollectTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.collect_normal, 0, 0);
				Toast.makeText(TravelDetailActivity.this, cancelMyCollectSuccessTips, Toast.LENGTH_SHORT).show();
			}
		} else {
			if (isKeep == 1) {
				addCollectTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.collect_normal, 0, 0);
			} else {
				addCollectTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bottombar_love_hover, 0, 0);
			}
			Toast.makeText(TravelDetailActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 分享按钮
	 */
	@Click(R.id.tv_travel_detail_share)
	void onShareClick() {
		if (travelDetail != null) {
			OnekeyShare onekeyShare = new OnekeyShare();
			onekeyShare.setTitle(travelDetail.getTitle());
			onekeyShare.setText("我在“酷鸟”看到" + travelDetail.getTitle() + "，很不错！你也来看看~" + travelDetail.getShareUrl());
			onekeyShare.setUrl(travelDetail.getShareUrl());
			onekeyShare.setNotification(R.drawable.app_logo, "酷鸟");
			onekeyShare.setImageUrl(travelDetail.getImage());
			onekeyShare.show(getBaseContext());
		}
	}

	/**
	 * 下载按钮
	 */
	@Click(R.id.tv_travel_detail_download)
	void onDownloadClick() {
		if (travelDetail != null && !isDownloaded && !isDownloading) {
			isDownloading = true;
			downloadTextView.setText("请稍后");
			downloadTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bottombar_download_normal, 0, 0);
			getOfflineZip();
		}
	}

	/**
	 * 获取离线下载包
	 */
	private void getOfflineZip() {
		TravelManager.getInstance().getOfflineZip(travelId, 0, new GetOfflineResultCallback() {

			@Override
			public void result(String errMsg, String zipPath, String zipSize, long mTime) {
				getOfflineZipComplete(errMsg, zipPath, zipSize, mTime);
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
	private void getOfflineZipComplete(String errMsg, String zipPath, String zipSize, long mTime) {
		if (errMsg == null && zipPath != null) {
			final OffLine offLine = new OffLine();
			offLine.setCoverImg(travelDetail.getImage());
			offLine.setDownloadPath(zipPath);
			int start = zipPath.lastIndexOf("/");
			zipPath = zipPath.substring(start + 1, zipPath.length());
			offLine.setFileName(zipPath);
			offLine.setPrice(travelDetail.getRefer_price());
			offLine.setStartPlace(travelDetail.getTraffic_departure());
			offLine.setTravelId(travelId);
			offLine.setTravelName(travelDetail.getTitle());
			offLine.setmTime(mTime);

			if (NetUtil.isWifi()) { // WiFi环境
				downloadTextView.setText(R.string.downloading);
				DownloadService.getDownloadManager(TravelDetailActivity.this).addNewDownload(offLine, downloadCallback);
			} else {
				dialogTitle = "离线下载";
				String dialogMessage = "您处于非WiFi网络环境，确认要下载(" + zipSize + ")行程离线包吗？";
				dialog = new Dialog(TravelDetailActivity.this, dialogTitle, dialogMessage);
				dialog.setCancelable(false);
				dialog.setOnAcceptButtonClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						isDownloading = true;
						downloadTextView.setText(R.string.downloading);
						DownloadService.getDownloadManager(TravelDetailActivity.this).addNewDownload(offLine, downloadCallback);
					}
				});
				dialog.setOnCancelButtonClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						isDownloading = false;
						dialog.dismiss();
					}
				});
				dialog.show();
			}

		} else {
			isDownloading = false;
			downloadTextView.setText(R.string.download_fail);
			Toast.makeText(TravelDetailActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isDownloading = false; // 是否正在下载

	DownloadCallback downloadCallback = new DownloadCallback() {

		@Override
		public void onSuccess() {
			downloaded();
		}

		@Override
		public void onLoading(String progress) {
			isDownloaded = false;
			isDownloading = true;
			downloadTextView.setText(progress);
		}

		@Override
		public void onFailure() {
			isDownloaded = false;
			isDownloading = false;
			downloadTextView.setText("重新下载");
		}

	};

	private boolean addtravelable=true;
	/**
	 * 添加行程按钮
	 */
	@Click(R.id.tv_add_my_travel)
	void onAddTravelClick() {
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		if (uid != 0) {
			if (addtravelable) {
				addToMyTravelList();
				addtravelable=false;
			}
			
		} else {
			Intent logIntent = new Intent(getBaseContext(), LoginActivity_.class);
			startActivityForResult(logIntent, RESULT_CODE_LOGIN_ADD_TRAVEL);
		}
	}

	/**
	 * 添加行程
	 */
	private void addToMyTravelList() {
		TravelManager.getInstance().addToMyTravelList(travelId, new TravelManager.StringResultCallback() {

			@Override
			public void result(String errMsg) {
				addToMyTravelListComplete(errMsg);
			}
		});
	}

	/**
	 * 添加行程请求完成
	 * 
	 * @param errMsg
	 */
	@UiThread
	void addToMyTravelListComplete(String errMsg) {
		addtravelable=true;
		if (errMsg == null) {
			added();
			UserManager.getInstance().undateTravelCount(1);
			Toast.makeText(getBaseContext(), "添加行程成功", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getBaseContext(), errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 行程反馈
	 */
	@Click(R.id.tv_travel_feedback)
	void onTravelFeedbackClick() {
		Intent intent = new Intent(TravelDetailActivity.this, TravelFeedbackActivity_.class);
		intent.putExtra(Define.PID, travelId);
		startActivity(intent);
	}

	/**
	 * 查看行程反馈
	 */
	@Click(R.id.tv_travel_comment)
	void onTravelCommentClick() {
		Intent intent = new Intent(TravelDetailActivity.this, TravelFeedbackListActivity_.class);
		intent.putExtra(Define.PID, travelId);
		startActivity(intent);
	}

	@StringRes(R.string.call)
	String dialogTitle; // 对话框标题
	Dialog dialog; // 拨打电话确认对话框

	/**
	 * 拨打导游联系电话
	 */
	@Click(R.id.tv_guide_tel)
	void onGuideContactPhoneClick() {
		if (travelDetail != null) {
			final String contactPhone = travelDetail.getGuideList().getGuideMobile();
			if (!"".equals(contactPhone)) {
				dialogTitle = StringUtil.getStringFromR(R.string.call);
				dialog = new Dialog(TravelDetailActivity.this, dialogTitle, contactPhone);
				dialog.setCancelable(false);
				dialog.setOnAcceptButtonClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactPhone));
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
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
	 * 点击导游信息布局
	 */
	@Click(R.id.travel_detail_layout_guide_info)
	void onGuideInfoLayoutClick() {
		if (travelDetail != null) {
			Intent intent = new Intent(TravelDetailActivity.this, GuideTravelListActivity_.class);
			// 导游id
			int guideId = travelDetail.getGuideList().getGuideId();
			intent.putExtra(Define.ID, guideId);
			startActivity(intent);
		}
	}

	/**
	 * 点击圆形菜单按钮
	 */
	@Click(R.id.iv_travel_detail_menu)
	void onMenuClick() {
		detailMenuImageView.startAnimation(scaleAnimation);
		scaleAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				menuPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
			}
		});
	}
	
	private boolean canCommitComment = true; // 是否可以点击提交评论

	/**
	 * 点击提交评论
	 */
	@Click(R.id.bt_travel_detail_comment_commit)
	void onCommitCommentClick() {
		// 关闭输入法
		InputMethodUtils.closeInputKeyboard(TravelDetailActivity.this);
		// 判断用户登录状态
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		if (uid == 0) {
			Intent logIntent = new Intent(TravelDetailActivity.this, LoginActivity_.class);
			startActivityForResult(logIntent, RESULT_CODE_LOGIN_COMMIT_COMMENT);
		} else {
			if (canCommitComment) {
				canCommitComment = false;
				commitComment();
			}
		}
	}

	@TextChange(R.id.et_travel_detail_comment)
	void onTextChangesOnStoreNameInput(CharSequence text, TextView textView, int before, int start, int count) {
		commentContentCounTextView.setText(String.valueOf(400 - text.length()));
	}

	/**
	 * 提交评论
	 */
	private void commitComment() {
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
		String content = commentInpuEditText.getText().toString().trim();
		float rank = commentRatingBar.getRating();
		TravelManager.getInstance().commitComment(travelId, content, rank, Define.TRAVEL, new TravelManager.StringResultCallback() {

			@Override
			public void result(String errMsg) {
				progressDialog.dismiss();
				canCommitComment = true;
				if (errMsg == null) {
					// 清空输入框
					commentInpuEditText.setText("");
					commentPageSize = TravelDetailActivity.this.comments.size() + 1;
					handler.sendEmptyMessageDelayed(MSG_LOAD_COMMENT_LIST, 100);
					Toast.makeText(TravelDetailActivity.this, R.string.commit_comment_success, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getBaseContext(), errMsg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 回复评论
	 */
	private void replyComment() {
		Comment comment = comments.get(commentClickIndex);
		Intent intent = new Intent(getBaseContext(), CommentReplyActivity_.class);
		intent.putExtra(Define.ID, travelId);
		intent.putExtra(Define.PID, comment.getCommentId());
		intent.putExtra(Define.MODULE, Define.TRAVEL);
		intent.putExtra(Define.USER_NAME, comment.getCommentUname());
		startActivityForResult(intent, RESULT_CODE_REPLY_COMMENT); 
	}

	private int commentPageSize = 5; // 一页获取多少评论

	/**
	 * 获取评论列表
	 */
	private void loadCommentList() {
		mIsRequestSuccess = false;
		TravelManager.getInstance().loadCommentList(travelId, Define.TRAVEL, 1, commentPageSize, new CommentListResultCallback() {

			@Override
			public void result(String errMsg, List<Comment> comments, int commentCount) {
				loadCommentListComplete(errMsg, comments, commentCount);
			}
		});
	}

	private boolean mIsRequestSuccess = true; // 是否请求成功
	
	/**
	 * 获取评论列表完成
	 * 
	 * @param errMsg
	 * @param comments
	 * @param pageCount
	 */
	@UiThread
	void loadCommentListComplete(String errMsg, List<Comment> comments, int commentCount) {
		progressDialog.dismiss();
		this.commentCount = commentCount;

		if (errMsg == null && comments != null) {
			mIsRequestSuccess = true;
			this.comments = comments;
			commentAdapter.setComments(this.comments);
			commentListLayout.setBaseAdapter(commentAdapter);

			// 没有更多数据就把底部加载更多去掉
			if (this.comments.size() >= commentCount) {
				commentListLayout.removeView(commentFootView);
			} else {
				commentListLayout.addFooterView(commentFootView);
			}
		} else {
			mIsRequestSuccess = false;
			Toast.makeText(getBaseContext(), errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isNeedToInit = true; // 是否需要初始化

	/**
	 * 结束当前activity
	 */
	private void activityFinish() {
		isNeedToInit = false;
		Intent intent = new Intent();
		intent.putExtra(Define.DATA, travelKeep);
		setResult(RESULT_OK, intent);
		finish();
	}

	final int RESULT_CODE_LOGIN_COLLECT = 11; // 收藏请求登录
	final int RESULT_CODE_LOGIN_ADD_TRAVEL = 12; // 添加行程请求登录
	final int RESULT_CODE_LOGIN_COMMIT_COMMENT = 13; // 发表评论请求登录
	final int RESULT_CODE_LOGIN_REPLY_COMMENT = 14; // 回复评论请求登录
	final int RESULT_CODE_REPLY_COMMENT = 15; // 回复评论结果回调

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case RESULT_CODE_LOGIN_COLLECT: // 登录
			if (resultCode == Activity.RESULT_OK) { // 登录成功
				addOrCancelToMyCollect();
			}
			break;

		case RESULT_CODE_LOGIN_ADD_TRAVEL: // 添加行程
			if (resultCode == Activity.RESULT_OK) { // 登录成功
				addToMyTravelList();
			}
			break;

		case RESULT_CODE_LOGIN_COMMIT_COMMENT: // 发表评论
			if (resultCode == Activity.RESULT_OK) { // 登录成功
				commitComment();
			}
			break;

		case RESULT_CODE_LOGIN_REPLY_COMMENT: // 回复评论
			if (resultCode == Activity.RESULT_OK) { // 登录成功
				replyComment();
			}
			break;
			
		case RESULT_CODE_REPLY_COMMENT: // 回复评论结果回调
			if (resultCode == Activity.RESULT_OK) { 
				if (!progressDialog.isShowing()) {
					progressDialog.show();
				}
				commentPageSize = TravelDetailActivity.this.comments.size() + 1;
				handler.sendEmptyMessageDelayed(MSG_LOAD_COMMENT_LIST, 100);
				Toast.makeText(TravelDetailActivity.this, R.string.commit_comment_success, Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
