package com.kooniao.travel.home;

import java.util.ArrayList;
import java.util.List;

import com.kooniao.travel.R;
import com.kooniao.travel.WebBrowserActivity_;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.model.Ad;
import com.kooniao.travel.utils.ColorUtil;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class AdListPagerAdapter extends PagerAdapter {

	private List<Ad> ads = new ArrayList<Ad>();

	public void setAdList(List<Ad> ads) {
		if (ads != null) {
			this.ads = ads;
		}
	}

	private Context context;
	private Animation alphaShowAnimation;

	public AdListPagerAdapter(Context context) {
		this.context = context;
		alphaShowAnimation = AnimationUtils.loadAnimation(context, R.anim.alpha_show);
		alphaShowAnimation.setDuration(1000);
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		currentPosition = position = position % ads.size();
		View view = LayoutInflater.from(context).inflate(R.layout.item_img_list, null);
		ImageView bgimageView = (ImageView) view.findViewById(R.id.iv_item_img_list);
		bgimageView.setBackgroundColor(ColorUtil.getRandomColorRes());
		bgimageView.setAnimation(alphaShowAnimation);
		Ad ad = ads.get(position);
		// 图片下载路径
		String uri = ad.getImg();
		// 加载图片
		ImageLoaderUtil.loadListCoverImg(ImageLoader.getInstance(), uri, bgimageView); 
		bgimageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Ad ad = ads.get(currentPosition);
				String url = ad.getUrl();
				String title = ad.getTitle();
				Intent intent = new Intent(context, WebBrowserActivity_.class);
				intent.putExtra(Define.URL, url);
				intent.putExtra(Define.TITLE, title);
				if (intent != null) {
					((Activity) context).startActivity(intent);
				}
			}
		});
		((ViewPager) container).addView(view);
		return view;
	}

	private int currentPosition;

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
		currentPosition = position = position % ads.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}

}
