package com.kooniao.travel;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import cn.jpush.android.api.JPushInterface;

import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.utils.AppSetting;

/**
 * 首页
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_guide)
public class SplashActivity extends BaseActivity {

	/**
	 * 初始化控件
	 */
	@ViewById(R.id.viewpager)
	ViewPager viewPager;
	@ViewById(R.id.iv_splash)
	ImageView splashImageView;

	/**
	 * 初始化界面
	 */
	@AfterViews
	void init() {
		initData();
		initView();
	}

	/**
	 * 界面数据
	 */
	private List<ImageView> viewList = null; // 引导页
	private boolean isNotFirstStartApp; // 是否不是第一次进入

	/**
	 * 初始化数据
	 */
	private void initData() {
		viewList = new ArrayList<ImageView>();
		isNotFirstStartApp = AppSetting.getInstance().getBooleanPreferencesByKey(Define.IS_NOT_FIRST_START_APP);
	}

	/**
	 * 初始化view
	 */
	private void initView() {
		final int delayTime = 2000; // 启动页延迟时间
		/**
		 * 启动页
		 */
		viewPager.setVisibility(View.GONE);
		splashImageView.setVisibility(View.VISIBLE);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 1f);
		alphaAnimation.setDuration(delayTime);
		splashImageView.setAnimation(alphaAnimation);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (!isNotFirstStartApp) {
					startGuideActivity();
				} else {
					startMainActivity();
				}
			}
		}, delayTime);
	}

	final int viewCount = 4; // 总共view的数目
	int currentViewIndex = 0; // 当前view的位置索引

	/**
	 * 启动引导页
	 */
	private void startGuideActivity() {
		/**
		 * 引导页
		 */
		ImageView itemView;
		splashImageView.setVisibility(View.GONE);
		viewPager.setVisibility(View.VISIBLE);
		for (int i = 0; i < viewCount; i++) {
			itemView = (ImageView) LayoutInflater.from(SplashActivity.this).inflate(R.layout.activity_splash, null);
			if (i == 0) {
				itemView.setImageResource(R.drawable.a0);
			} else if (i == 1) {
				itemView.setImageResource(R.drawable.a1);
			} else if (i == 2) {
				itemView.setImageResource(R.drawable.a2);
			} else if (i == 3) {
				itemView.setImageResource(R.drawable.a3);
				itemView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 更改app，已经不是第一次启动了
						AppSetting.getInstance().saveBooleanPreferencesByKey(Define.IS_NOT_FIRST_START_APP, true);
						startMainActivity();
					}
				});
			}

			viewList.add(itemView);
		}

		viewPager.setAdapter(new ViewPagerAdapter(viewList));
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				currentViewIndex = position;
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (currentViewIndex == viewCount - 1 && state == 1) {
					// 更改app，已经不是第一次启动了
					AppSetting.getInstance().saveBooleanPreferencesByKey(Define.IS_NOT_FIRST_START_APP, true);
					startMainActivity();
				}
			}
		});
	}

	/**
	 * 启动进入首页
	 */
	private void startMainActivity() {
		Intent intent = new Intent(getApplicationContext(), BottomTabBarActivity_.class);
		startActivity(intent);
		finish();
	}

	class ViewPagerAdapter extends PagerAdapter {
		private List<ImageView> viewList;

		public ViewPagerAdapter(List<ImageView> views) {
			this.viewList = views;
		}

		// 销毁position位置的界面
		@Override
		public void destroyItem(View view, int position, Object obj) {
			((ViewPager) view).removeView(viewList.get(position));
		}

		// 获得当前界面数
		@Override
		public int getCount() {
			if (viewList != null) {
				return viewList.size();
			}
			return 0;
		}

		// 初始化position位置的界面
		@Override
		public Object instantiateItem(View view, int position) {
			((ViewPager) view).addView(viewList.get(position), 0);
			return viewList.get(position);
		}

		// 判断是否由对象生成界面
		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return (view == obj);
		}
	}

	@Override
	protected void onResume() {
		JPushInterface.onResume(SplashActivity.this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		JPushInterface.onPause(SplashActivity.this);
		super.onPause();
	}

}
