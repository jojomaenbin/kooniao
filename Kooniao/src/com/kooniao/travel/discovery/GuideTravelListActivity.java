package com.kooniao.travel.discovery;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.customwidget.LinearListLayout;
import com.kooniao.travel.customwidget.MesureTopScrollView;
import com.kooniao.travel.customwidget.MesureTopScrollView.OnScrollListener;
import com.kooniao.travel.discovery.GuideTravelListAdapter.ItemRequestListener;
import com.kooniao.travel.manager.TravelManager;
import com.kooniao.travel.manager.TravelManager.GuideTravelListResultCallback;
import com.kooniao.travel.model.Guide;
import com.kooniao.travel.model.GuideTravel;
import com.kooniao.travel.utils.BitMapUtil;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 导游行程列表页
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_guide_travel_list)
public class GuideTravelListActivity extends BaseActivity {
	@ViewById(R.id.iv_guide_travel_list_top)
	ImageView topImageView; // 顶部背景图
	@ViewById(R.id.iv_guide_travel_list_suspend)
	ImageView suspendImageView; // 顶部高斯模糊层
	@ViewById(R.id.sv_guide_travel_list)
	MesureTopScrollView scrollView;
	@ViewById(R.id.iv_guide_travel_avatar)
	ImageView avatarImageView; // 头像
	@ViewById(R.id.tv_guide_name)
	TextView nameTextView; // 导游名字
	@ViewById(R.id.tv_guide_sex)
	TextView sexTextView; // 导游性别
	@ViewById(R.id.tv_guide_city)
	TextView cityTextView; // 导游城市
	@ViewById(R.id.ll_guide_travel_list)
	LinearListLayout listLayout; // 行程列表
	@ViewById(R.id.layout_guide_travel_list_back)
	View quickBackLayout;

	@AfterViews
	void init() {
		initView();
		initData();
		loadData();
	}

	/**
	 * 初始化view
	 */
	private void initView() {
		// 滑动监听
		scrollView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(int scrollY) {
				if (scrollY >= top) {
					quickBackLayout.setVisibility(View.VISIBLE);
				} else {
					quickBackLayout.setVisibility(View.GONE);
				}
			}

			@Override
			public void scrollToBottom() {
				if (!isLoading) {
					isLoading = true;
					currentPageNum++;
					loadData();
				}
			}
		});
		// 悬浮层
		suspendImageView = (ImageView) findViewById(R.id.iv_guide_travel_list_suspend);
		topImageView = (ImageView) findViewById(R.id.iv_guide_travel_list_top);
		/**
		 * 设置模糊层
		 */
		topImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				topImageView.getViewTreeObserver().removeOnPreDrawListener(this);
				topImageView.buildDrawingCache();
				Bitmap backgroundBitmap = topImageView.getDrawingCache();
				BitMapUtil.blur(backgroundBitmap, suspendImageView);
				return true;
			}
		});
	}

	private int top; // 顶部高度
	private int guideId; // 导游id
	private ImageLoader imageLoader;
	private GuideTravelListAdapter adapter;
	KooniaoProgressDialog progressDialog;

	/**
	 * 初始化数据
	 */
	private void initData() {
		top = (int) (Define.DENSITY * 250);

		if (progressDialog == null) {
			progressDialog = new KooniaoProgressDialog(GuideTravelListActivity.this);
		}

		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}

		Intent intent = getIntent();
		if (intent != null) {
			guideId = intent.getIntExtra(Define.ID, 0);
		}

		imageLoader = ImageLoader.getInstance();
		adapter = new GuideTravelListAdapter(GuideTravelListActivity.this);
		adapter.setOnItemRequestListener(new ItemRequestListener() {

			@Override
			public void onLoadCoverImgListener(String imgUrl, ImageView coverImageView) {
				ImageLoaderUtil.loadListCoverImg(imageLoader, imgUrl, coverImageView);
			}

			@Override
			public void onItemClickListener(int position) {
				GuideTravel travel = travelList.get(position);
				Intent intent = new Intent(GuideTravelListActivity.this, TravelDetailActivity_.class);
				intent.putExtra(Define.PID, travel.getPlanId());
				startActivity(intent);
			}
		});
	}

	private boolean isLoading = false; // 是否正在加载更多
	private int currentPageNum = 1; // 当前页码

	/**
	 * 加载列表数据
	 */
	private void loadData() {
		TravelManager.getInstance().loadGuideTravelList(guideId, currentPageNum, new GuideTravelListResultCallback() {

			@Override
			public void result(String errMsg, Guide guideInfo, List<GuideTravel> travels, int totalCount) {
				loadGuideTravelListComplete(errMsg, guideInfo, travels, totalCount);
			}
		});
	}

	private List<GuideTravel> travelList = new ArrayList<GuideTravel>();

	/**
	 * 获取导游行程列表请求完成
	 * 
	 * @param errMsg
	 * @param guideInfo
	 * @param travels
	 * @param totalCount
	 */
	private void loadGuideTravelListComplete(String errMsg, Guide guideInfo, List<GuideTravel> travels, int totalCount) {
		isLoading = false;
		progressDialog.dismiss();
		if (errMsg == null && guideInfo != null && travels != null) {
			if (currentPageNum == 1) {
				travelList.clear();
				travelList = travels;
				// 设置导游信息
				setGuideInfo(guideInfo);
			} else {
				travelList.addAll(travels);
			}
			adapter.setTravels(travelList);
			listLayout.setBaseAdapter(adapter);
		} else {
			Toast.makeText(GuideTravelListActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 设置导游信息
	 * 
	 * @param guideInfo
	 */
	private void setGuideInfo(Guide guideInfo) {
		// 加载头像
		ImageLoaderUtil.loadAvatar(imageLoader, guideInfo.getImage(), avatarImageView);
		// 用户名
		nameTextView.setText(guideInfo.getName());
		// 性别
		String sex = guideInfo.getSex() == 0 ? StringUtil.getStringFromR(R.string.sex_woman) : StringUtil.getStringFromR(R.string.sex_man);
		sexTextView.setText(sex);
		// 城市
		cityTextView.setText(guideInfo.getArea());
	}

	/**
	 * 返回按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 快速返回按钮
	 */
	@Click(R.id.iv_quick_go_back)
	void onQuickGoBackClick() {
		finish();
	}

	@Click(R.id.layout_guide_travel_list_back)
	void onQuickGoBackLayoutClick() {
	}

}
