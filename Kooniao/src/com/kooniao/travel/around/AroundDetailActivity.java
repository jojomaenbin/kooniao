package com.kooniao.travel.around;

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
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.kooniao.travel.BottomTabBarActivity_;
import com.kooniao.travel.ImageBrowseActivity_;
import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.HeadText;
import com.kooniao.travel.customwidget.InfoDescView;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.customwidget.LinearListLayout;
import com.kooniao.travel.discovery.CommentReplyActivity_;
import com.kooniao.travel.home.CommentAdapter;
import com.kooniao.travel.home.CommentAdapter.ItemRequestListener;
import com.kooniao.travel.home.CommentAdapter.Type;
import com.kooniao.travel.manager.ProductManager;
import com.kooniao.travel.manager.TravelManager;
import com.kooniao.travel.manager.TravelManager.CustomAmusementAndShoppingDetailResultCallback;
import com.kooniao.travel.manager.TravelManager.CustomEventDetailResultCallback;
import com.kooniao.travel.manager.TravelManager.CustomFoodDetailResultCallback;
import com.kooniao.travel.manager.TravelManager.CustomHotelDetailResultCallback;
import com.kooniao.travel.manager.TravelManager.CustomScenicDetailResultCallback;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.manager.ProductManager.StringResultCallback;
import com.kooniao.travel.manager.TravelManager.AmusementDetailResultCallback;
import com.kooniao.travel.manager.TravelManager.CommentListResultCallback;
import com.kooniao.travel.manager.TravelManager.FoodDetailResultCallback;
import com.kooniao.travel.manager.TravelManager.HotelDetailResultCallback;
import com.kooniao.travel.manager.TravelManager.ScenicDetailResultCallback;
import com.kooniao.travel.manager.TravelManager.ShoppingDetailResultCallback;
import com.kooniao.travel.manager.TravelManager.StringListCallback;
import com.kooniao.travel.model.AmusementDetail;
import com.kooniao.travel.model.Around;
import com.kooniao.travel.model.AroundDetail;
import com.kooniao.travel.model.Comment;
import com.kooniao.travel.model.Custom;
import com.kooniao.travel.model.CustomAmusementAndShopping;
import com.kooniao.travel.model.CustomEvent;
import com.kooniao.travel.model.CustomFoodDetail;
import com.kooniao.travel.model.CustomHotelDetail;
import com.kooniao.travel.model.CustomScenicDetail;
import com.kooniao.travel.model.FoodDetail;
import com.kooniao.travel.model.HotelDetail;
import com.kooniao.travel.model.Review;
import com.kooniao.travel.model.ScenicDetail;
import com.kooniao.travel.model.ShoppingDetail;
import com.kooniao.travel.onekeyshare.OnekeyShare;
import com.kooniao.travel.user.LoginActivity_;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.InputMethodUtils;
import com.kooniao.travel.utils.KooniaoTypeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 附近详情页
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_around_detail)
public class AroundDetailActivity extends BaseActivity {

	@ViewById(R.id.title)
	TextView titleTextView; // 标题
	@ViewById(R.id.sv_middle)
	ScrollView scrollView;

	/**
	 * 封面
	 */
	@ViewById(R.id.iv_travel_cover)
	ImageView coverImageView; // 封面图
	@ViewById(R.id.rb_around)
	RatingBar aroundRatingBar; // 评分
	@ViewById(R.id.tv_travel_cost)
	TextView priceTextView; // 价格
	@ViewById(R.id.info_desc)
	InfoDescView infoDescView; // 基本信息布局

	/**
	 * 贴士、介绍....
	 */
	@ViewById(R.id.head_text_one)
	HeadText oneHeadText;
	@ViewById(R.id.head_text_two)
	HeadText twoHeadText;
	@ViewById(R.id.head_text_three)
	HeadText threeHeadText;
	@ViewById(R.id.head_text_four)
	HeadText fourHeadText;
	@ViewById(R.id.head_text_five)
	HeadText fiveHeadText;

	// 点评模块
	@ViewById(R.id.travel_detail_layout_comment)
	View commentLayout; // 评论布局
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

	// 附近列表
	@ViewById(R.id.layout_around)
	View aroundLayout; // 附近布局
	@ViewById(R.id.tv_arround_type)
	TextView aroundHeadTypeTextView; // 附近列表头部类型
	@ViewById(R.id.lv_around_list)
	LinearListLayout aroundListLayout; // 附近列表布局

	// 底部操作栏
	@ViewById(R.id.scenic_detail_layout_bottombar)
	View bottomBarView;
	@ViewById(R.id.tv_around_detail_love)
	TextView addCollectTextView; // 添加收藏

	@AfterViews
	void init() {
		initData();
		initView();
		loadDetail();
	}

	private int id;
	private int travelId;
	private String type; // 类型
	private String lunchType; // 后退启动模式
	private boolean isOffline; // 是否是离线行程
	private CommentAdapter commentAdapter;
	private AroundListAdapter aroundListAdapter;
	private int commentClickIndex = -1; // 回复评论item位置
	private ImageLoader imageLoader;

	/**
	 * 初始化数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			id = intent.getIntExtra(Define.ID, 0);
			travelId = intent.getIntExtra(Define.PID, 0);
			type = intent.getStringExtra(Define.TYPE);
			lunchType = intent.getStringExtra(Define.LUNCH_TYPE);
			isOffline = intent.getBooleanExtra(Define.IS_OFFLINE, false);
		}

		// 评论适配器
		commentAdapter = new CommentAdapter(AroundDetailActivity.this, Type.TYPE_REPLY_VISIBLE);
		commentAdapter.setOnItemRequestListener(new ItemRequestListener() {

			@Override
			public void onReplyCommentClick(int position) {
				commentClickIndex = position;
				// 回复评论
				int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
				if (uid == 0) {
					Intent logIntent = new Intent(AroundDetailActivity.this, LoginActivity_.class);
					startActivityForResult(logIntent, RESULT_CODE_LOGIN_REPLY_COMMENT);
				} else {
					replyComment();
				}
			}
		});

		aroundListAdapter = new AroundListAdapter(AroundDetailActivity.this);
		aroundListAdapter.setOnItemRequestListener(itemRequestListener);
		// 初始化图片加载器
		imageLoader = ImageLoader.getInstance();
	}

	AroundListAdapter.ItemRequestListener itemRequestListener = new AroundListAdapter.ItemRequestListener() {

		@Override
		public void onLoadCoverImgListener(String imgUrl, ImageView coverImageView) {
			ImageLoaderUtil.loadListCoverImg(imageLoader, imgUrl, coverImageView);
		}

		@Override
		public void onItemClickListener(int position) {
			Around around = arounds.get(position);
			Intent intent = new Intent(AroundDetailActivity.this, AroundDetailActivity_.class);
			intent.putExtra(Define.ID, around.getId());
			intent.putExtra(Define.TYPE, around.getType());
			startActivity(intent);
		}
	};

	KooniaoProgressDialog progressDialog;
	View commentFootView; // 评论底部加载更多评论布局

	/**
	 * 初始化界面
	 */
	private void initView() {
		progressDialog = new KooniaoProgressDialog(AroundDetailActivity.this);
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}

		if (type != null) {
			// 设置标题
			String title = KooniaoTypeUtil.getAroundDetailTitleByType(type);
			titleTextView.setText(title);
			// 设置附近列表的标题
			String aroundTypeTitle = KooniaoTypeUtil.getAroundTitleByType(type);
			aroundHeadTypeTextView.setText(aroundTypeTitle);
		}

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

		// 初始化评论加载更多布局
		commentFootView = LayoutInflater.from(getBaseContext()).inflate(R.layout.sub_comment_item_footer, null);
		commentFootView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!progressDialog.isShowing()) {
					progressDialog.show();
				}
				
				if (mIsRequestSuccess) {
					if (AroundDetailActivity.this.comments.size() <= 2) {
						commentPageSize = 5;
					} else {
						commentPageSize = AroundDetailActivity.this.comments.size() + 5;
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
	 * 加载详情
	 */
	private void loadDetail() {
		if (Define.LOCATION.equals(type)) {
			TravelManager.getInstance().loadScenicDetail(isOffline, travelId, id, new ScenicDetailResultCallback() {

				@Override
				public void result(String errMsg, ScenicDetail scenicDetail) {
					setBaseViewInfo(errMsg, scenicDetail, Define.LOCATION);
					setScenicViewInfo(scenicDetail);
				}
			});
		} else if (Define.CUSTOM_LOCATION.equals(type)) {
			TravelManager.getInstance().loadCustomScenicDetail(isOffline, travelId, id, new CustomScenicDetailResultCallback() {

				@Override
				public void result(String errMsg, CustomScenicDetail customScenicDetail) {
					setBaseCustomViewInfo(errMsg, customScenicDetail);
					setCustomScenicViewInfo(customScenicDetail);
				}
			});
		} else if (Define.HOTEL.equals(type)) {
			TravelManager.getInstance().loadHotelDetail(isOffline, travelId, id, new HotelDetailResultCallback() {

				@Override
				public void result(String errMsg, HotelDetail hotelDetail) {
					setBaseViewInfo(errMsg, hotelDetail, Define.HOTEL);
					setHotelViewInfo(hotelDetail);
				}
			});
		} else if (Define.CUSTOM_HOTEL.equals(type)) {
			TravelManager.getInstance().loadCustomHotelDetail(isOffline, travelId, id, new CustomHotelDetailResultCallback() {

				@Override
				public void result(String errMsg, CustomHotelDetail customHotelDetail) {
					setBaseCustomViewInfo(errMsg, customHotelDetail);
					setCustomHotelViewInfo(customHotelDetail);
				}
			});
		} else if (Define.FOOD.equals(type)) {
			TravelManager.getInstance().loadFoodDetail(isOffline, travelId, id, new FoodDetailResultCallback() {

				@Override
				public void result(String errMsg, FoodDetail foodDetail) {
					setBaseViewInfo(errMsg, foodDetail, Define.FOOD);
					setFoodViewInfo(foodDetail);
				}
			});
		} else if (Define.CUSTOM_FOOD.equals(type)) {
			TravelManager.getInstance().loadCustomFoodDetail(isOffline, travelId, id, new CustomFoodDetailResultCallback() {

				@Override
				public void result(String errMsg, CustomFoodDetail customFoodDetail) {
					setBaseCustomViewInfo(errMsg, customFoodDetail);
					setCustomFoodViewInfo(customFoodDetail);
				}
			});
		} else if (Define.SHOPPING.equals(type)) {
			TravelManager.getInstance().loadShoppingDetail(isOffline, travelId, id, new ShoppingDetailResultCallback() {

				@Override
				public void result(String errMsg, ShoppingDetail shoppingDetail) {
					setBaseViewInfo(errMsg, shoppingDetail, Define.SHOPPING);
					setShoppingViewInfo(shoppingDetail);
				}
			});
		} else if (Define.CUSTOM_SHOPPING.equals(type)) {
			TravelManager.getInstance().loadCustomShoppingDetail(isOffline, travelId, id, new CustomAmusementAndShoppingDetailResultCallback() {

				@Override
				public void result(String errMsg, CustomAmusementAndShopping customAmusementAndShopping) {
					setBaseCustomViewInfo(errMsg, customAmusementAndShopping);
					setCustomAmusementAndShoppingViewInfo(customAmusementAndShopping);
				}
			});
		} else if (Define.AMUSEMENT.equals(type)) {
			TravelManager.getInstance().loadAmusementDetail(isOffline, travelId, id, new AmusementDetailResultCallback() {

				@Override
				public void result(String errMsg, AmusementDetail amusementDetail) {
					setBaseViewInfo(errMsg, amusementDetail, Define.AMUSEMENT);
					setAmusementViewInfo(amusementDetail);
				}
			});
		} else if (Define.CUSTOM_ENTERTAINMENT.equals(type)) {
			TravelManager.getInstance().loadCustomAmusementDetail(isOffline, travelId, id, new CustomAmusementAndShoppingDetailResultCallback() {

				@Override
				public void result(String errMsg, CustomAmusementAndShopping customAmusementAndShopping) {
					setBaseCustomViewInfo(errMsg, customAmusementAndShopping);
					setCustomAmusementAndShoppingViewInfo(customAmusementAndShopping);
				}
			});
		} else {
			TravelManager.getInstance().loadOtherCustomDetail(isOffline, type, travelId, id, new CustomEventDetailResultCallback() {

				@Override
				public void result(String errMsg, CustomEvent customEvent) {
					setBaseCustomViewInfo(errMsg, customEvent);
					setCustomEventViewInfo(customEvent);
				}
			});
		}
	}

	/**
	 * 设置景点特有信息
	 * 
	 * @param scenicDetail
	 */
	private void setScenicViewInfo(ScenicDetail scenicDetail) {
		if (scenicDetail != null) {
			// 景点门票
			String ticket = scenicDetail.getTicket();
			priceTextView.setText(ticket);
			// 景点贴士
			oneHeadText.setVisibility(View.VISIBLE);
			oneHeadText.setTitle("景点贴士");
			String openTime = scenicDetail.getOpenTime();// 开放时间
			oneHeadText.setMutilContent(0, "开放时间", openTime);
			String suggested = scenicDetail.getSuggested(); // 建议时长
			oneHeadText.setMutilContent(1, "建议时长", suggested);
			String scenicType = scenicDetail.getLabelList();
			oneHeadText.setMutilContent(2, "景点类型", scenicType);
			String bestTime = scenicDetail.getBestTime();// 最佳游玩时间
			oneHeadText.setMutilContent(3, "建议游玩时间", bestTime);
			// 简介
			twoHeadText.setVisibility(View.VISIBLE);
			twoHeadText.setTitle("简介");
			String desc = scenicDetail.getDescription();
			twoHeadText.setSingleContent(desc);
		}
	}

	/**
	 * 设置自定义景点页面信息
	 * 
	 * @param errMsg
	 * @param customScenicDetail
	 */
	private void setCustomScenicViewInfo(CustomScenicDetail customScenicDetail) {
		if (customScenicDetail != null) {
			// 景点门票
			String ticket = customScenicDetail.getTc_ticket();
			priceTextView.setText(ticket);
			// 景点贴士
			oneHeadText.setVisibility(View.VISIBLE);
			oneHeadText.setTitle("景点贴士");
			String openTime = customScenicDetail.getOpen_time();// 开放时间
			oneHeadText.setMutilContent(0, "开放时间", openTime);
			String suggested = customScenicDetail.getPlaytime(); // 建议时长
			oneHeadText.setMutilContent(1, "建议时长", suggested);
			String scenicType = customScenicDetail.getLocation_type(); // 景点类型
			oneHeadText.setMutilContent(2, "景点类型", scenicType);
			String bestTime = customScenicDetail.getBest_time();// 最佳游玩时间
			oneHeadText.setMutilContent(3, "建议游玩时间", bestTime);
			// 简介
			twoHeadText.setVisibility(View.VISIBLE);
			twoHeadText.setTitle("简介");
			String desc = customScenicDetail.getDescription();
			twoHeadText.setSingleContent(desc);
		}
	}

	/**
	 * 设置酒店特有信息
	 * 
	 * @param hotelDetail
	 */
	@UiThread
	void setHotelViewInfo(HotelDetail hotelDetail) {
		if (hotelDetail != null) {
			// 酒店价格
			float price = hotelDetail.getPrice();
			priceTextView.setText(String.valueOf(price));
			// 酒店星级
			float rank = hotelDetail.getStars();
			aroundRatingBar.setRating(rank);
			// 酒店服务
			oneHeadText.setVisibility(View.VISIBLE);
			oneHeadText.setTitle("酒店服务");
			String service = hotelDetail.getService();
			oneHeadText.setSingleContent(service);
			// 会议设施
			twoHeadText.setVisibility(View.VISIBLE);
			twoHeadText.setTitle("会议设施");
			String meetingFacilities = hotelDetail.getMeetingFacilities();
			twoHeadText.setSingleContent(meetingFacilities);
			// 支付方式
			threeHeadText.setVisibility(View.VISIBLE);
			threeHeadText.setTitle("支付方式");
			String payment = hotelDetail.getPayment();
			threeHeadText.setSingleContent(payment);
		}
	}

	/**
	 * 设置自定义酒店信息
	 * 
	 * @param errMsg
	 * @param customHotelDetail
	 */
	private void setCustomHotelViewInfo(CustomHotelDetail customHotelDetail) {
		if (customHotelDetail != null) {
			// 酒店价格
			String price = customHotelDetail.getTicket_price();
			priceTextView.setText(price);
			// 酒店星级
			float rank = customHotelDetail.getGrade_id();
			aroundRatingBar.setRating(rank);
			// 酒店服务
			List<String> special = customHotelDetail.getSpecial();
			if (!special.isEmpty()) {
				oneHeadText.setVisibility(View.VISIBLE);
				oneHeadText.setTitle("酒店服务");
				String service = special.toString();
				oneHeadText.setSingleContent(service);
			}
			// 会议设施
			List<String> mettingList = customHotelDetail.getMetting();
			if (!mettingList.isEmpty()) {
				twoHeadText.setVisibility(View.VISIBLE);
				twoHeadText.setTitle("会议设施");
				String meetingFacilities = mettingList.toString();
				twoHeadText.setSingleContent(meetingFacilities);
			}
			// 支付方式
			threeHeadText.setVisibility(View.VISIBLE);
			threeHeadText.setTitle("房间类型");
			String roomType = customHotelDetail.getRoom_type();
			threeHeadText.setSingleContent(roomType);
		}
	}

	/**
	 * 设置美食特有信息
	 * 
	 * @param foodDetail
	 */
	@UiThread
	void setFoodViewInfo(FoodDetail foodDetail) {
		if (foodDetail != null) {
			// 人均消费
			float price = foodDetail.getPrice();
			priceTextView.setText(String.valueOf(price));
			// 商家贴士
			oneHeadText.setVisibility(View.VISIBLE);
			oneHeadText.setTitle("商家贴士");
			String label = foodDetail.getLabelList();
			oneHeadText.setMutilContent(0, "分类标签", label);
			if ("".equals(label)) {
				oneHeadText.setVisibility(View.GONE);
			} else {
				oneHeadText.setVisibility(View.VISIBLE);
			}
			// 商家推荐
			twoHeadText.setVisibility(View.VISIBLE);
			twoHeadText.setTitle("商家推荐");
			String recommend = foodDetail.getRecommend();
			twoHeadText.setSingleContent(recommend);
		}
	}

	/**
	 * 自定义美食节点信息
	 * 
	 * @param errMsg
	 * @param customFoodDetail
	 */
	private void setCustomFoodViewInfo(CustomFoodDetail customFoodDetail) {
		if (customFoodDetail != null) {
			// 人均消费
			float price = customFoodDetail.getAve_cost();
			priceTextView.setText(String.valueOf(price));
			// 商家贴士
			oneHeadText.setTitle("商家贴士");
			String label = customFoodDetail.getSm_type();
			oneHeadText.setMutilContent(0, "分类标签", label);
			if ("".equals(label)) {
				oneHeadText.setVisibility(View.GONE);
			} else {
				oneHeadText.setVisibility(View.VISIBLE);
			}
			// 商家推荐
			twoHeadText.setVisibility(View.VISIBLE);
			twoHeadText.setTitle("商家推荐");
			String recommend = customFoodDetail.getOverview();
			twoHeadText.setSingleContent(recommend);
		}
	}

	/**
	 * 设置购物特有信息
	 * 
	 * @param shoppingDetail
	 */
	@UiThread
	void setShoppingViewInfo(ShoppingDetail shoppingDetail) {
		if (shoppingDetail != null) {
			// 人均消费
			float price = shoppingDetail.getPrice();
			priceTextView.setText(String.valueOf(price));
			// 商家贴士
			oneHeadText.setVisibility(View.VISIBLE);
			oneHeadText.setTitle("商家贴士");
			String tag = shoppingDetail.getTags();
			oneHeadText.setMutilContent(0, "分类标签", tag);
			if ("".equals(tag)) {
				oneHeadText.setVisibility(View.GONE);
			} else {
				oneHeadText.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 设置自定义娱乐、购物节点信息
	 * 
	 * @param errMsg
	 * @param customAmusementAndShopping
	 */
	private void setCustomAmusementAndShoppingViewInfo(CustomAmusementAndShopping customAmusementAndShopping) {
		if (customAmusementAndShopping != null) {
			// 人均消费
			float price = customAmusementAndShopping.getAve_cost();
			priceTextView.setText(String.valueOf(price));
			// 商家贴士
			oneHeadText.setTitle("商家贴士");
			String trafficInfo = customAmusementAndShopping.getTraffic_info();
			oneHeadText.setMutilContent(0, "交通信息", trafficInfo);
			if ("".equals(trafficInfo)) {
				oneHeadText.setVisibility(View.GONE);
			} else {
				oneHeadText.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 设置娱乐特有信息
	 * 
	 * @param amusementDetail
	 */
	@UiThread
	void setAmusementViewInfo(AmusementDetail amusementDetail) {
		if (amusementDetail != null) {
			// 人均消费
			float price = amusementDetail.getPrice();
			priceTextView.setText(String.valueOf(price));
		}
	}

	/**
	 * 设置自定义事件信息
	 * 
	 * @param errMsg
	 * @param customEvent
	 */
	private void setCustomEventViewInfo(CustomEvent customEvent) {
		if (customEvent != null) {
			oneHeadText.setVisibility(View.VISIBLE);
			oneHeadText.setTitle("建议时长");
			oneHeadText.setSingleContent(customEvent.getPlaytime());
		}
	}

	private AroundDetail aroundDetail;
	private int commentCount; // 评论总条数
	private List<Around> arounds;
	private boolean isCustom = false; // 是否是自定义节点

	/**
	 * 设置页面基本信息
	 * 
	 * @param errMsg
	 * @param aroundDetail
	 * @param type
	 */
	@UiThread
	void setBaseViewInfo(String errMsg, AroundDetail aroundDetail, String type) {
		progressDialog.dismiss();
		if (errMsg != null) {
			Toast.makeText(AroundDetailActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}

		isCustom = false;
		if (aroundDetail != null) {
			this.aroundDetail = aroundDetail;
			// 加载封面
			String imgUrl = aroundDetail.getImg();
			if (isOffline) {
				int start = imgUrl.lastIndexOf("/") + 1;
				imgUrl = imgUrl.substring(start);
				imgUrl = "file://" + KooniaoApplication.getInstance().getDownloadDir() + travelId + "/source/picture/" + imgUrl;
			}
			ImageLoaderUtil.loadListCoverImg(ImageLoader.getInstance(), imgUrl, coverImageView);
			// 评分
			float rank = aroundDetail.getRank();
			aroundRatingBar.setRating(rank);
			// 名称
			String name = aroundDetail.getName();
			infoDescView.setName(name, type);
			// 地址
			String address = aroundDetail.getAddr();
			float lat = aroundDetail.getLat();
			float lon = aroundDetail.getLon();
			infoDescView.setAddress(address, lat, lon);
			// 联系方式
			String contactways = aroundDetail.getContactways();
			infoDescView.setPhone(contactways);
			// 附近列表
			arounds = aroundDetail.getRecommendList();
			aroundListAdapter.setArounds(arounds);
			aroundListLayout.setBaseAdapter(aroundListAdapter);
			// 评论列表
			commentCount = aroundDetail.getReviewCount(); // 评论数目
			if (commentCount > 0) {
				// 填充数据
				List<Review> reviewList = aroundDetail.getReviewList();
				for (Review review : reviewList) {
					Comment comment = new Comment();
					comment.setCommentContent(review.getReviewCont());
					comment.setCommentId(review.getReviewID());
					comment.setCommentRank(review.getReviewRank());
					comment.setCommentTime(review.getReviewTime());
					comment.setCommentUid(review.getReviewUID());
					comment.setCommentUname(review.getReviewUName());
					comment.setCommentUserFace(review.getReviewUserImg());
					comments.add(comment);
				}
				commentAdapter.setComments(comments);
				commentListLayout.setBaseAdapter(commentAdapter);
				// 是否显示尾部加载更多
				if (commentCount > 2) {
					commentListLayout.addFooterView(commentFootView);
				}
			}

			// 是否收藏
			int collect = aroundDetail.getCollect();
			if (collect == 1) {
				addCollectTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bottombar_love_hover, 0, 0);
			}
		} else {
			bottomBarView.setVisibility(View.GONE);
		}
	}

	private Custom custom; // 自定义节点详情

	/**
	 * 设置自定义节点基本信息
	 * 
	 * @param errMsg
	 * @param custom
	 */
	private void setBaseCustomViewInfo(String errMsg, Custom custom) {
		progressDialog.dismiss();
		if (errMsg != null) {
			Toast.makeText(AroundDetailActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
		isCustom = true;
		this.custom = custom;
		commentLayout.setVisibility(View.GONE);
		aroundLayout.setVisibility(View.GONE);
		bottomBarView.setVisibility(View.GONE);
		if (custom != null) {
			// 加载封面
			String imgUrl = custom.getLogo();
			if (isOffline) {
				imgUrl = "file://" + KooniaoApplication.getInstance().getDownloadDir() + travelId + "/source/picture/" + imgUrl;
			}
			ImageLoaderUtil.loadListCoverImg(ImageLoader.getInstance(), imgUrl, coverImageView);
			// 评分
			aroundRatingBar.setVisibility(View.INVISIBLE);
			// 名称
			String name = custom.getName();
			infoDescView.setName(name, type);
			// 地址
			String address = custom.getAddress();
			infoDescView.setAddress(address, 0, 0);
			// 联系方式
			String contactways = custom.getTel();
			infoDescView.setPhone(contactways);
		}
	}

	/**
	 * 后退按钮点击
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
		if (Define.HOME_PAGE.equals(lunchType)) {
			Intent intent = new Intent(AroundDetailActivity.this, BottomTabBarActivity_.class);
			intent.putExtra(Define.TYPE, lunchType);
			startActivity(intent);
		}
		finish();
	}

	private boolean canCommitComment = true; // 是否可以点击提交评论
	
	/**
	 * 点击提交评论
	 */
	@Click(R.id.bt_travel_detail_comment_commit)
	void onCommitCommentClick() {
		// 关闭输入法
		InputMethodUtils.closeInputKeyboard(AroundDetailActivity.this);
		// 判断用户登录状态
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		if (uid == 0) {
			Intent logIntent = new Intent(AroundDetailActivity.this, LoginActivity_.class);
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
		String nodeType = type;
		if (Define.FOOD.equals(type) || Define.SHOPPING.equals(type) || Define.AMUSEMENT.equals(type)) {
			nodeType = Define.LIFESTYLE;
		}
		TravelManager.getInstance().commitComment(id, content, rank, nodeType, new TravelManager.StringResultCallback() {

			@Override
			public void result(String errMsg) {
				progressDialog.dismiss();
				canCommitComment = true;
				if (errMsg == null) {
					// 清空输入框
					commentInpuEditText.setText("");
					commentPageSize = AroundDetailActivity.this.comments.size() + 1;
					handler.sendEmptyMessageDelayed(MSG_LOAD_COMMENT_LIST, 100);
					Toast.makeText(AroundDetailActivity.this, R.string.commit_comment_success, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(AroundDetailActivity.this, errMsg, Toast.LENGTH_SHORT).show();
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
		intent.putExtra(Define.ID, id);
		intent.putExtra(Define.PID, comment.getCommentId());
		intent.putExtra(Define.MODULE, Define.LOCATION);
		intent.putExtra(Define.USER_NAME, comment.getCommentUname());
		startActivityForResult(intent, RESULT_CODE_REPLY_COMMENT);
	}

	private List<Comment> comments = new ArrayList<Comment>(); // 页面评论列表
	private int commentPageSize = 5; // 一页获取多少评论

	/**
	 * 获取评论列表
	 */
	private void loadCommentList() {
		mIsRequestSuccess = false;
		String nodeType = type;
		if (Define.FOOD.equals(type) || Define.SHOPPING.equals(type) || Define.AMUSEMENT.equals(type)) {
			nodeType = Define.LIFESTYLE;
		}
		TravelManager.getInstance().loadCommentList(id, nodeType, 1, commentPageSize, new CommentListResultCallback() {

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
	 * @param commentCount
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

	/**
	 * 添加到想去列表（收藏）
	 */
	@Click(R.id.tv_around_detail_love)
	void onAddCollectClick() {
		// 判断用户登录状态
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		if (uid == 0) {
			Intent logIntent = new Intent(AroundDetailActivity.this, LoginActivity_.class);
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
		if (aroundDetail != null) {
			final int isKeep = aroundDetail.getCollect() == 0 ? 1 : 0;
			aroundDetail.setCollect(isKeep);
			ProductManager.getInstance().addOrCancelToMyCollect(id, isKeep, type, null, 0, new StringResultCallback() {

				@Override
				public void result(String errMsg) {
					addOrCancelToMyCollectComplete(isKeep, errMsg);
				}
			});
		}
	}

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
			UserManager.getInstance().undateCollectCount(isKeep);
			if (isKeep == 1) {
				addCollectTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bottombar_love_hover, 0, 0);
				Toast.makeText(AroundDetailActivity.this, addToMyCollectSuccessTips, Toast.LENGTH_SHORT).show();
			} else {
				addCollectTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.collect_normal, 0, 0);
				Toast.makeText(AroundDetailActivity.this, cancelMyCollectSuccessTips, Toast.LENGTH_SHORT).show();
			}
		} else {
			if (isKeep == 1) {
				addCollectTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.collect_normal, 0, 0);
			} else {
				addCollectTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bottombar_love_hover, 0, 0);
			}
			Toast.makeText(AroundDetailActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 分享按钮
	 */
	@Click(R.id.tv_around_detail_share)
	void onShareClick() {
		if (aroundDetail != null) {
			OnekeyShare onekeyShare = new OnekeyShare();
			onekeyShare.setTitle(aroundDetail.getName() + "-" + aroundDetail.getAddr());
			onekeyShare.setText("我在“酷鸟”看到" + aroundDetail.getName() + "，很不错！你也来看看~" + aroundDetail.getShareUrl());
			onekeyShare.setUrl(aroundDetail.getShareUrl());
			onekeyShare.setNotification(R.drawable.app_logo, "酷鸟");
			onekeyShare.setImageUrl(aroundDetail.getImg());
			onekeyShare.show(getBaseContext());
		}
	}

	/**
	 * 查看评论列表
	 */
	@Click(R.id.tv_around_detail_comment)
	void onCheckCommentListClick() {
		scrollView.smoothScrollTo(0, commentLayout.getTop());
	}

	/**
	 * 点击进入大图模式
	 */
	@Click(R.id.travel_detail_layout_cover)
	void onCoverImageClick() {
		int imgCount = 0;
		List<String> imgList = new ArrayList<String>();
		if (!isCustom) {
			imgCount = aroundDetail.getImgCount();
		} else {
			if (custom != null) {
				imgList = custom.getAll_attach_list() == null ? imgList : custom.getAll_attach_list();
				imgCount = imgList.size();
			}
		}
		if (imgCount == 0) {
			Toast.makeText(getBaseContext(), "没有大图列表数据", Toast.LENGTH_SHORT).show();
		} else {
			if (isCustom) {
				Intent intent = new Intent(getBaseContext(), ImageBrowseActivity_.class);
				intent.putStringArrayListExtra(Define.IMG_LIST, (ArrayList<String>) imgList);
				startActivity(intent);
			} else {
				TravelManager.getInstance().loadAroundLargeImageList(isOffline, type, travelId, id, new StringListCallback() {

					@Override
					public void result(String errMsg, List<String> imgList) {
						loadAroundLargeImageListComplete(errMsg, imgList);
					}
				});
			}
		}
	}

	/**
	 * 获取大图列表完成
	 * 
	 * @param errMsg
	 * @param imgList
	 */
	@UiThread
	void loadAroundLargeImageListComplete(String errMsg, List<String> imgList) {
		if (errMsg == null) {
			Intent intent = new Intent(getBaseContext(), ImageBrowseActivity_.class);
			intent.putStringArrayListExtra(Define.IMG_LIST, (ArrayList<String>) imgList);
			startActivity(intent);
		} else {
			Toast.makeText(getBaseContext(), errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 附近列表头部点击
	 */
	@Click(R.id.ll_around_header)
	void onAroundHeaderClick() {
		Intent intent = new Intent(AroundDetailActivity.this, AroundListActivity_.class);
		intent.putExtra(Define.CITY_NAME_AROUND_LON, aroundDetail.getLon());
		intent.putExtra(Define.CITY_NAME_AROUND_LAT, aroundDetail.getLat());
		intent.putExtra(Define.TYPE, type);
		startActivity(intent);
	}

	final int RESULT_CODE_LOGIN_COLLECT = 11; // 收藏请求登录
	final int RESULT_CODE_LOGIN_COMMIT_COMMENT = 13; // 发表评论请求登录
	final int RESULT_CODE_LOGIN_REPLY_COMMENT = 14; // 回复评论请求登录
	final int RESULT_CODE_REPLY_COMMENT = 15; // 回复评论结果回调

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case RESULT_CODE_LOGIN_COLLECT: // 收藏
			if (resultCode == Activity.RESULT_OK) { // 登录成功
				addOrCancelToMyCollect();
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
				commentPageSize = AroundDetailActivity.this.comments.size() + 1;
				handler.sendEmptyMessageDelayed(MSG_LOAD_COMMENT_LIST, 100);
				Toast.makeText(AroundDetailActivity.this, R.string.commit_comment_success, Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
