package com.kooniao.travel.discovery;

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
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.home.CommentAdapter;
import com.kooniao.travel.home.CommentAdapter.Type;
import com.kooniao.travel.manager.TravelManager;
import com.kooniao.travel.manager.TravelManager.TravelFeedBackResultCallback;
import com.kooniao.travel.model.Comment;
import com.kooniao.travel.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * 行程反馈列表页
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_travel_feedback_list)
public class TravelFeedbackListActivity extends BaseActivity {
	@ViewById(R.id.swipe_refresh_layout)
	PtrFrameLayout refreshLayout; // 下拉刷新
	@ViewById(R.id.lv_product)
	ListView listView;
	@ViewById(R.id.layout_no_data)
	View noDataLayout;

	@AfterViews
	void init() {
		initData();
		initView();
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
	}

	@StringRes(R.string.no_more_data)
	String noreMoreDataTips; // 没有更多数据的提示语

	final int REFRESH_DATA = 1; // 刷新数据

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH_DATA: // 刷新数据
				refreshLayout.autoRefresh(true);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private int travelId; // 行程id
	private ImageLoader imageLoader;

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			travelId = intent.getIntExtra(Define.PID, 0);
		}

		imageLoader = ImageLoader.getInstance();
	}

	private CommentAdapter adapter;
	View listViewHeaderView;
	RatingBar feedBackRouteRatingBar;
	TextView feedBackRouteTipsTextView;
	RatingBar feedBackServiceRatingBar;
	TextView feedBackServiceTipsTextView;
	RatingBar feedBackFoodRatingBar;
	TextView feedBackFoodTipsTextView;

	/**
	 * 初始化界面
	 */
	private void initView() {
		// listview头部
		listViewHeaderView = LayoutInflater.from(TravelFeedbackListActivity.this).inflate(R.layout.sub_travel_feedback_list_header, null);
		// 行程线路评分条
		feedBackRouteRatingBar = (RatingBar) listViewHeaderView.findViewById(R.id.rb_travel_feedback_route);
		// 行程线路评分提示
		feedBackRouteTipsTextView = (TextView) listViewHeaderView.findViewById(R.id.tv_travel_feedback_route_tips);
		// 行程线路评分条
		feedBackServiceRatingBar = (RatingBar) listViewHeaderView.findViewById(R.id.rb_travel_feedback_service);
		// 行程服务评分提示
		feedBackServiceTipsTextView = (TextView) listViewHeaderView.findViewById(R.id.tv_travel_feedback_service_tips);
		// 行程餐饮评分条
		feedBackFoodRatingBar = (RatingBar) listViewHeaderView.findViewById(R.id.rb_travel_feedback_food);
		// 行程餐饮评分提示
		feedBackFoodTipsTextView = (TextView) listViewHeaderView.findViewById(R.id.tv_travel_feedback_food_tips);

		// 配置listview
		adapter = new CommentAdapter(TravelFeedbackListActivity.this, Type.TYPE_REPLY_INVISIBLE);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true, new ListViewScrollListener()));
		// 下拉刷新配置
		MaterialHeader materialHeader = new MaterialHeader(TravelFeedbackListActivity.this);
		materialHeader.setPadding(0, ViewUtils.dpToPx(15, getResources()), 0, ViewUtils.dpToPx(15, getResources()));

		refreshLayout.setHeaderView(materialHeader);
		refreshLayout.addPtrUIHandler(materialHeader);
		refreshLayout.setPtrHandler(new PtrHandler() {
			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
				return PtrDefaultHandler.checkContentCanBePulledDown(frame, listView, header);
			}

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				onRefresh();
			}
		});
	}

	// 滑动监听
	private class ListViewScrollListener implements OnScrollListener {
		@Override
		public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (!isLoadingMore) {
				int lastItem = firstVisibleItem + visibleItemCount;
				if (lastItem == totalItemCount) {
					int childCount = listView.getChildCount();
					if (childCount > 1) {
						View lastItemView = (View) listView.getChildAt(childCount - 1);
						if ((listView.getBottom()) == lastItemView.getBottom()) {
							currentPageNum++;
							loadTravelFeedBackList();
						}
					}
				}
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView listview, int scrollState) {
		}
	}

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	private int currentPageNum = 1; // 当前页码

	// 下拉刷新
	public void onRefresh() {
		currentPageNum = 1;
		loadTravelFeedBackList();
	}

	private boolean isLoadingMore = false; // 是否正在加载更多
	private List<Comment> commentList = new ArrayList<Comment>();

	/**
	 * 加载行程反馈列表
	 */
	private void loadTravelFeedBackList() {
		if (currentPageNum > 1) {
			isLoadingMore = true;
		}

		TravelManager.getInstance().loadTravelFeedback(travelId, currentPageNum, new TravelFeedBackResultCallback() {

			@Override
			public void result(String errMsg, float lineScore, float serviceScore, float repastScore, List<Comment> comments, int totalCount) {
				isLoadingMore = false;
				refreshLayout.refreshComplete();
				if (errMsg == null && comments != null) {
					if (currentPageNum == 1) {
						if (comments.isEmpty()) {
							noDataLayout.setVisibility(View.VISIBLE);
						} else {
							setRating(lineScore, serviceScore, repastScore);
							commentList.clear();
							commentList = comments;
						}
					} else {
						commentList.addAll(comments);
					}
					adapter.setComments(commentList);
				} else {
					Toast.makeText(TravelFeedbackListActivity.this, errMsg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 设置头部评分
	 * 
	 * @param lineScore
	 * @param serviceScore
	 * @param repastScore
	 */
	private void setRating(float lineScore, float serviceScore, float repastScore) {
		// 线路
		feedBackRouteRatingBar.setRating(lineScore);
		if ((int) lineScore == 1) {
			feedBackRouteTipsTextView.setText(R.string.very_yawp); // 十分不满意
		} else if ((int) lineScore == 2) {
			feedBackRouteTipsTextView.setText(R.string.yawp); // 不满意
		} else if ((int) lineScore == 3) {
			feedBackRouteTipsTextView.setText(R.string.gernal_satisfaction); // 一般
		} else if ((int) lineScore == 4) {
			feedBackRouteTipsTextView.setText(R.string.satisfaction); // 满意
		} else if ((int) lineScore == 5) {
			feedBackRouteTipsTextView.setText(R.string.very_satisfaction); // 十分满意
		}
		// 服务
		feedBackServiceRatingBar.setRating(serviceScore);
		if ((int) serviceScore == 1) {
			feedBackServiceTipsTextView.setText(R.string.very_yawp); // 十分不满意
		} else if ((int) serviceScore == 2) {
			feedBackServiceTipsTextView.setText(R.string.yawp); // 不满意
		} else if ((int) serviceScore == 3) {
			feedBackServiceTipsTextView.setText(R.string.gernal_satisfaction); // 一般
		} else if ((int) serviceScore == 4) {
			feedBackServiceTipsTextView.setText(R.string.satisfaction); // 满意
		} else if ((int) serviceScore == 5) {
			feedBackServiceTipsTextView.setText(R.string.very_satisfaction); // 十分满意
		}
		// 餐饮
		feedBackFoodRatingBar.setRating(repastScore);
		if ((int) repastScore == 1) {
			feedBackFoodTipsTextView.setText(R.string.very_yawp); // 十分不满意
		} else if ((int) repastScore == 2) {
			feedBackFoodTipsTextView.setText(R.string.yawp); // 不满意
		} else if ((int) repastScore == 3) {
			feedBackFoodTipsTextView.setText(R.string.gernal_satisfaction); // 一般
		} else if ((int) repastScore == 4) {
			feedBackFoodTipsTextView.setText(R.string.satisfaction); // 满意
		} else if ((int) repastScore == 5) {
			feedBackFoodTipsTextView.setText(R.string.very_satisfaction); // 十分满意
		}
	}

}
