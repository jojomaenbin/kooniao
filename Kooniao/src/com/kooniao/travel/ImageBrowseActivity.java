package com.kooniao.travel;

import java.util.ArrayList;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.scaleimage.TouchImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * 大图浏览
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_img_browse)
public class ImageBrowseActivity extends BaseActivity {

	/**
	 * 初始化
	 */
	@AfterViews
	void initialize() {
		initData();
		initViews();
	}

	private ArrayList<String> imgUrlList;

	/**
	 * 初始化数据
	 */
	private void initData() {
		imgUrlList = new ArrayList<String>();
		if (getIntent() != null) {
			imgUrlList = getIntent().getStringArrayListExtra(Define.IMG_LIST);
		}
	}

	@ViewById(R.id.tv_schedule_imgbrowser)
	TextView rateTextView;// 进度显示
	@ViewById(R.id.vp_recommand)
	ViewPager viewPager;
	private ImagePagerAdapter adapter;

	/**
	 * 初始化界面
	 */
	private void initViews() {
		// 初始化浏览进度
		rateTextView.setText("1/" + imgUrlList.size());
		// viewpager 界面
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// 改变浏览进度
				rateTextView.setText((position + 1) + "/" + imgUrlList.size());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		adapter = new ImagePagerAdapter(getBaseContext());
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(5); 
	}

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	int currentViewIndex = -1; // 当前view的索引

	class ImagePagerAdapter extends PagerAdapter {

		ImagePagerAdapter(Context context) {
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
			super.setPrimaryItem(container, position, object);
			if (currentViewIndex != position) {
				currentViewIndex = position;
				View currentView = (View) object;
				// 默认图片
				ImageView defaultImageView = (ImageView) currentView.findViewById(R.id.iv_list_default);
				// 缩放图片
				TouchImageView touchImageView = (TouchImageView) currentView.findViewById(R.id.full_image);
				// 进度条
				ProgressBar progressBar = (ProgressBar) currentView.findViewById(R.id.progress);
				// 图片下载路径
				String uri = imgUrlList.get(position);
				// 加载图片
				loadImageImage(defaultImageView, touchImageView, progressBar, uri);
			}
		}

		@Override
		public int getCount() {
			return imgUrlList == null ? 0 : imgUrlList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_img_browse, null);
			((ViewPager) container).addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}
	}

	/**
	 * 加载图片
	 * 
	 * @param defaultImageView
	 * @param touchImageView
	 * @param progressBar
	 * @param uri
	 */
	private void loadImageImage(final ImageView defaultImageView, final TouchImageView touchImageView, final ProgressBar progressBar, String uri) {
		ImageLoader.getInstance().displayImage(uri, touchImageView, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {
				defaultImageView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				Toast.makeText(getBaseContext(), "加载失败", Toast.LENGTH_SHORT).show();
				defaultImageView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				defaultImageView.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				touchImageView.setImageBitmap(loadedImage);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
			}
		});
	}

}
