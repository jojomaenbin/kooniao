package com.kooniao.travel.store;

import java.io.File;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.BottomTabBarActivity_;
import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.PhotoPreviewActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.PhotoPreviewActivity.Type;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.StoreManager;
import com.kooniao.travel.manager.StoreManager.StringResultCallback;
import com.kooniao.travel.model.Store;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.BitMapUtil;
import com.kooniao.travel.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * 店铺设置
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_store_setting)
public class StoreSettingActivity extends BaseActivity {
	@ViewById(R.id.iv_store_logo)
	ImageView storeLogoImageView; // 店铺logo
	@ViewById(R.id.iv_store_bg)
	ImageView storeBgImageView; // 店铺背景
	@ViewById(R.id.tv_store_name)
	TextView storeNameTextView; // 店铺名
	@ViewById(R.id.tv_store_contact_phone)
	TextView contactPhoneTextView; // 联系电话

	@AfterViews
	void init() {
		initData();
		initView();
	}

	private Store store;
	private int sid; // 店铺id
	private String storeType; // 店铺类型(a、c)

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			if (intent.hasExtra(Define.DATA)) {
				store = (Store) intent.getSerializableExtra(Define.DATA);
			}

			sid = AppSetting.getInstance().getIntPreferencesByKey(Define.SID);
			storeType = AppSetting.getInstance().getStringPreferencesByKey(Define.TYPE);
		}
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		if (store != null) {
			// 加载店铺logo
			String logoUrl = store.getLogo();
			ImageLoader.getInstance().displayImage(logoUrl, storeLogoImageView, new ImageLoadingListener() {

				@Override
				public void onLoadingStarted(String imageUri, View view) {
					storeLogoImageView.setImageResource(R.drawable.user_default_avatar);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					storeLogoImageView.setImageResource(R.drawable.user_default_avatar);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					if (loadedImage != null) {
						loadedImage = BitMapUtil.toRoundBitmap(loadedImage);
						storeLogoImageView.setImageBitmap(loadedImage);
					} else {
						storeLogoImageView.setImageResource(R.drawable.user_default_avatar);
					}
				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					storeLogoImageView.setImageResource(R.drawable.user_default_avatar);
				}
			});
			// 加载店铺背景图
			String storeBgUrl = store.getBgImg();
			ImageLoader.getInstance().displayImage(storeBgUrl, storeBgImageView, new ImageLoadingListener() {

				@Override
				public void onLoadingStarted(String imageUri, View view) {
					storeBgImageView.setImageResource(R.drawable.default_bg);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					storeBgImageView.setImageResource(R.drawable.default_bg);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					if (loadedImage != null) {
						storeBgImageView.setImageBitmap(loadedImage);
					} else {
						storeBgImageView.setImageResource(R.drawable.default_bg);
					}
				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					storeBgImageView.setImageResource(R.drawable.default_bg);
				}
			});
			// 店铺名称
			String storeName = store.getShopName();
			storeNameTextView.setText(storeName);
			// 联系电话
			String contactPhone = store.getMobile();
			contactPhone = contactPhone.trim().equals("") ? StringUtil.getStringFromR(R.string.has_no_contact_phone) : contactPhone;
			contactPhoneTextView.setText(contactPhone);
		}
	}

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		onActivityFinish();
	}

	@Override
	public void onBackPressed() {
		onActivityFinish();
		super.onBackPressed();
	}

	/**
	 * 店铺icon按钮
	 */
	@Click(R.id.iv_store)
	void onTitleBarStoreClick() {
		onActivityFinish();
		Intent storeIntent = new Intent(StoreSettingActivity.this, BottomTabBarActivity_.class);
		storeIntent.putExtra(Define.TYPE, Define.STORE);
		startActivity(storeIntent);
	}

	/**
	 * 结束当前activity
	 */
	void onActivityFinish() {
		Intent intent = new Intent();
		intent.putExtra(Define.DATA, isDataChange);
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * 店铺logo条目点击
	 */
	@Click(R.id.rl_store_logo)
	void onStoreLogoItemClick() {
		currentImageType = "logo";
		popupImageSelectView("LOGO修改");
	}

	/**
	 * 店铺背景条目点击
	 */
	@Click(R.id.rl_store_bg)
	void onStoreBgItemClick() {
		currentImageType = "bg_image";
		popupImageSelectView("背景图修改");
	}

	private String currentImageType; // 当前选择的图片类型(logo/bg_image)

	/**
	 * 弹出logo修改选择界面
	 */
	private void popupImageSelectView(String title) {
		View selectPhotoView = LayoutInflater.from(StoreSettingActivity.this).inflate(R.layout.popupwindow_avatar_select, null);
		TextView titleTextView=(TextView) selectPhotoView.findViewById(R.id.tv_picture_title);
		titleTextView.setText(title);
		final PopupWindow popupWindow = new PopupWindow(selectPhotoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		popupWindow.setFocusable(false);
		popupWindow.setAnimationStyle(R.style.PopupAnimationFromBottom);
		// 关闭按钮
		selectPhotoView.findViewById(R.id.iv_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});

		// 选择拍照
		selectPhotoView.findViewById(R.id.tv_take_photo).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				Uri uri = Uri.fromFile(new File(KooniaoApplication.getInstance().getPicDir() + Define.PIC_NORMAL));
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				startActivityForResult(cameraIntent, REQUEST_CODE_TAKE_PHOTO);
			}
		});

		// 选择从相册选择
		selectPhotoView.findViewById(R.id.tv_select_photo_from_gallery).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, REQUEST_CODE_GALLERY_PHOTO);
			}
		});

		popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
	}

	private boolean isDataChange = false; // 数据是否发生了更改

	@StringRes(R.string.update_store_logo_success)
	String updateStoreLogoSuccessTips; // 更改店铺logo成功提示
	@StringRes(R.string.update_store_bg_success)
	String updateStoreBgSuccessTips; // 更改店铺背景成功提示

	final int SET_STORE_LOGO = 1; // 设置店铺logo
	final int UPDATE_STORE_LOGO_RESULT = 2; // 更改店铺logo的结果

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SET_STORE_LOGO: // 设置店铺图像
				Bitmap bitmap = (Bitmap) msg.obj;
				if ("logo".equals(currentImageType)) {
					storeLogoImageView.setImageBitmap(bitmap);
				} else {
					storeBgImageView.setImageBitmap(bitmap);
				}
				String logoPath = KooniaoApplication.getInstance().getPicDir() + Define.PIC_NORMAL;
				/**
				 * 上传图像
				 */
				StoreManager.getInstance().uploadStoreImage(sid, storeType, currentImageType, logoPath, new StringResultCallback() {

					@Override
					public void result(String errMsg) {
						Message message = Message.obtain();
						message.what = UPDATE_STORE_LOGO_RESULT;
						message.obj = errMsg;
						handler.sendMessage(message);
					}
				});
				break;

			case UPDATE_STORE_LOGO_RESULT: // 更改店铺logo的结果
				String errMsg = (String) msg.obj;
				if (errMsg == null) {
					isDataChange = true;

					if ("logo".equals(currentImageType)) {
						Toast.makeText(getBaseContext(), updateStoreLogoSuccessTips, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getBaseContext(), updateStoreBgSuccessTips, Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getBaseContext(), errMsg, Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};

	/**
	 * 店铺名称条目点击
	 */
	@Click(R.id.rl_store_name)
	void onStoreNameItemClick() {
		Intent storeNameIntent = new Intent(StoreSettingActivity.this, UpdateStoreNameActivity_.class);
		startActivityForResult(storeNameIntent, REQUEST_CODE_UPDATE_STORE_NAME);
	}

	/**
	 * 店铺联系电话条目点击
	 */
	@Click(R.id.rl_store_contact_phone)
	void onContactPhoneItemClick() {
		Intent storeContactPhoneIntent = new Intent(StoreSettingActivity.this, UpdateStoreContactPhoneActivity_.class);
		startActivityForResult(storeContactPhoneIntent, REQUEST_CODE_UPDATE_STORE_CONTACT_PHONE);
	}

	final int REQUEST_CODE_TAKE_PHOTO = 11; // 照相
	final int REQUEST_CODE_GALLERY_PHOTO = 12; // 图库
	final int REQUEST_CODE_PHOTO_PREVIEW = 13; // 图片预览
	final int REQUEST_CODE_UPDATE_STORE_NAME = 14; // 更改店铺名称
	final int REQUEST_CODE_UPDATE_STORE_CONTACT_PHONE = 15; // 更改店铺联系电话

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_TAKE_PHOTO: // 拍照
				File file = new File(KooniaoApplication.getInstance().getPicDir() + Define.PIC_NORMAL);
				if (file.exists()) {
					Intent intent = new Intent(StoreSettingActivity.this, PhotoPreviewActivity_.class);
					if ("logo".equals(currentImageType)) {
						intent.putExtra(Define.TYPE, Type.ROUND_CLIP.type);
					} else {
						intent.putExtra(Define.TYPE, Type.RECT_CLIP.type);
					}
					startActivityForResult(intent, REQUEST_CODE_PHOTO_PREVIEW);
				}
				break;

			case REQUEST_CODE_GALLERY_PHOTO: // 图库选择
				if (data != null) {
					Uri originalUri = data.getData(); // 获得图片的uri
					String imgPath = BitMapUtil.getRealPathFromURI(StoreSettingActivity.this, originalUri);
					Bitmap bitmap = BitMapUtil.loadBitmap(StoreSettingActivity.this, imgPath);
					boolean result = BitMapUtil.saveBitmap(KooniaoApplication.getInstance().getPicDir() + Define.PIC_NORMAL, bitmap);
					if (result) {
						Intent intent = new Intent(StoreSettingActivity.this, PhotoPreviewActivity_.class);
						if ("logo".equals(currentImageType)) {
							intent.putExtra(Define.TYPE, Type.ROUND_CLIP.type);
						} else {
							intent.putExtra(Define.TYPE, Type.RECT_CLIP.type);
						}
						startActivityForResult(intent, REQUEST_CODE_PHOTO_PREVIEW);
					} else {
						Toast.makeText(StoreSettingActivity.this, "获取图片失败，请重新选择！", Toast.LENGTH_SHORT).show();
					}
				}
				break;

			case REQUEST_CODE_PHOTO_PREVIEW: // 图片预览
				Bitmap avatar = BitMapUtil.loadBitmap(StoreSettingActivity.this, KooniaoApplication.getInstance().getPicDir() + Define.PIC_NORMAL);
				if ("logo".equals(currentImageType)) {
					avatar = BitMapUtil.toRoundBitmap(avatar);
				}
				String avatarPath = KooniaoApplication.getInstance().getPicDir() + Define.PIC_CLIP;
				BitMapUtil.saveBitmap(avatarPath, avatar);
				/**
				 * 通知修改头像
				 */
				Message message = Message.obtain();
				message.obj = avatar;
				message.what = SET_STORE_LOGO;
				handler.sendMessage(message);
				break;

			case REQUEST_CODE_UPDATE_STORE_NAME:// 更改店铺名称
				if (resultCode == RESULT_OK && data != null) {
					isDataChange = data.getBooleanExtra(Define.DATA, false);
					String storeName = data.getStringExtra(Define.SHOP_NAME);
					storeNameTextView.setText(storeName);
				}
				break;

			case REQUEST_CODE_UPDATE_STORE_CONTACT_PHONE:// 更改店铺联系电话
				if (resultCode == RESULT_OK && data != null) {
					isDataChange = data.getBooleanExtra(Define.DATA, false);
					String storeContactPhone = data.getStringExtra(Define.CONTACT_PHONE);
					contactPhoneTextView.setText(storeContactPhone);
				}
				break;
			}

		}
	}
}
