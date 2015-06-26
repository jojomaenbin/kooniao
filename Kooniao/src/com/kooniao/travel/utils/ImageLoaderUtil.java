package com.kooniao.travel.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.kooniao.travel.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * 图片加载工具类
 * 
 * @author ke.wei.quan
 * 
 */
public class ImageLoaderUtil {

	/**
	 * 加载列表图片
	 * 
	 * @param imageLoader
	 * @param imgUrl
	 * @param coverImageView
	 */
	public static void loadListCoverImg(ImageLoader imageLoader, String imgUrl, final ImageView coverImageView) {
		imageLoader.displayImage(imgUrl, coverImageView, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {
				coverImageView.setImageResource(R.drawable.list_default_cover);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				coverImageView.setImageResource(R.drawable.list_default_cover);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				if (loadedImage != null) {
					coverImageView.setImageBitmap(loadedImage);
				} else {
					coverImageView.setImageResource(R.drawable.list_default_cover);
				}
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				coverImageView.setImageResource(R.drawable.list_default_cover);
			}
		});
	}

	/**
	 * 加载列表图片
	 * 
	 * @param imageLoader
	 * @param imgUrl
	 * @param coverImageView
	 * @param defaultImgResId
	 */
	public static void loadListCoverImg(ImageLoader imageLoader, String imgUrl, final ImageView coverImageView, final int defaultImgResId) {
		imageLoader.displayImage(imgUrl, coverImageView, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {
				coverImageView.setImageResource(defaultImgResId);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				coverImageView.setImageResource(defaultImgResId);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				if (loadedImage != null) {
					coverImageView.setImageBitmap(loadedImage);
				} else {
					coverImageView.setImageResource(defaultImgResId);
				}
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				coverImageView.setImageResource(defaultImgResId);
			}
		});
	}

	/**
	 * 加载头像
	 * 
	 * @param imageLoader
	 * @param imgUrl
	 * @param avatarImageView
	 */
	public static void loadAvatar(ImageLoader imageLoader, String imgUrl, final ImageView avatarImageView) {
		imageLoader.displayImage(imgUrl, avatarImageView, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {
				avatarImageView.setImageResource(R.drawable.user_default_avatar);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				avatarImageView.setImageResource(R.drawable.user_default_avatar);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				if (loadedImage != null) {
					loadedImage = BitMapUtil.toRoundBitmap(loadedImage);
					avatarImageView.setImageBitmap(loadedImage);
					avatarImageView.setBackgroundResource(R.drawable.avatar_round_white_bg); 
				} else {
					avatarImageView.setImageResource(R.drawable.user_default_avatar);
				}
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				avatarImageView.setImageResource(R.drawable.user_default_avatar);
			}
		});
	}
}
