package com.kooniao.travel.user;

import java.io.File;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
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

import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.PhotoPreviewActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.PhotoPreviewActivity.Type;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.manager.UserManager.StringResultCallback;
import com.kooniao.travel.utils.BitMapUtil;
import com.kooniao.travel.utils.StringUtil;

/**
 * 完善个人资料页
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_improve_user_info)
public class ImprovePersonDataActivity extends BaseActivity {

	@ViewById(R.id.iv_user_avatar)
	ImageView userAvatarImageView; // 用户头像
	@ViewById(R.id.tv_sex)
	TextView sexTextView; // 用户性别
	@ViewById(R.id.tv_apply_status)
	TextView applyStatusTextView; // 用户角色申请状态
	@ViewById(R.id.tv_skip)
	TextView skipTextView; // 右上角文字

	@StringRes(R.string.update_user_avatar_success)
	String updateUserAvatarSuccessTips; // 更改用户头像成功提示

	final int SET_USER_AVATAR = 1; // 设置用户头像
	final int UPDATE_USER_AVATAR_RESULT = 2; // 更改用户头像的结果
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SET_USER_AVATAR:
				Bitmap avatar = (Bitmap) msg.obj;
				userAvatarImageView.setImageBitmap(avatar);
				String avatarPath = KooniaoApplication.getInstance().getPicDir() + Define.PIC_NORMAL;
				/**
				 * 上传用户头像
				 */
				UserManager.getInstance().uploadUserAvatar(avatarPath, new StringResultCallback() {

					@Override
					public void result(String errMsg) {
						Message message = Message.obtain();
						message.what = UPDATE_USER_AVATAR_RESULT;
						message.obj = errMsg;
						handler.sendMessage(message);
					}
				});
				break;

			case UPDATE_USER_AVATAR_RESULT:
				String errMsg = (String) msg.obj;
				if (errMsg == null) {
					updateRightText();
					Toast.makeText(getBaseContext(), updateUserAvatarSuccessTips, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getBaseContext(), errMsg, Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};

	/**
	 * 点击跳过
	 */
	@Click(R.id.tv_skip)
	void onSkipClick() {
		finish();
	}
	
	/**
	 * 更新设置右上角文字提示
	 */
	private void updateRightText() {
		skipTextView.setText(R.string.finish);
	}

	/**
	 * 头像item点击
	 */
	@Click(R.id.rl_select_avatar)
	void onUserAvatarItemClick() {
		popupAvatarSelectView();
	}

	/**
	 * 弹出头像修改选择界面
	 */
	private void popupAvatarSelectView() {
		View selectPhotoView = LayoutInflater.from(ImprovePersonDataActivity.this).inflate(R.layout.popupwindow_avatar_select, null);
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

	/**
	 * 性别item点击
	 */
	@Click(R.id.rl_improve_select_sex)
	void onSexItemClick() {
		popupSexSelectView();
	}

	boolean isDataChange = false; // 页面数据是否更改

	/**
	 * 弹出性别选择界面
	 */
	private void popupSexSelectView() {
		View selectPhotoView = LayoutInflater.from(ImprovePersonDataActivity.this).inflate(R.layout.popupwindow_sex_select, null);
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

		// 选择性别男
		selectPhotoView.findViewById(R.id.tv_sex_man).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sexTextView.setText(StringUtil.getStringFromR(R.string.sex_man));
				popupWindow.dismiss();
				isDataChange = true;
				updateUserSex(1);
			}
		});

		// 选择性别女
		selectPhotoView.findViewById(R.id.tv_sex_woman).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sexTextView.setText(StringUtil.getStringFromR(R.string.sex_woman));
				popupWindow.dismiss();
				isDataChange = true;
				updateUserSex(0);
			}
		});

		popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
	}

	@StringRes(R.string.update_user_info_success)
	String updateUserInfoSuccessTips; // 更新用户信息成功提示

	/**
	 * 更新用户性别
	 * 
	 * @param sex
	 */
	@UiThread
	void updateUserSex(int sex) {
		UserManager.getInstance().updateSex(sex, new StringResultCallback() {

			@Override
			public void result(String errMsg) {
				if (errMsg == null) {
					updateRightText();
					Toast.makeText(getBaseContext(), updateUserInfoSuccessTips, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getBaseContext(), errMsg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 点击申请角色
	 */
	@Click(R.id.rl_improve_apply)
	void onRoleApplyclick() {
		Intent intent = new Intent(ImprovePersonDataActivity.this, RoleApplyActivity_.class);
		startActivityForResult(intent, REQUEST_CODE_ROLE_APPLY);
	}

	final int REQUEST_CODE_TAKE_PHOTO = 11; // 照相
	final int REQUEST_CODE_GALLERY_PHOTO = 12; // 图库
	final int REQUEST_CODE_PHOTO_PREVIEW = 13; // 图片预览
	final int REQUEST_CODE_ROLE_APPLY = 14; // 角色申请

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_TAKE_PHOTO: // 拍照
				File file = new File(KooniaoApplication.getInstance().getPicDir() + Define.PIC_NORMAL);
				if (file.exists()) {
					Intent intent = new Intent(ImprovePersonDataActivity.this, PhotoPreviewActivity_.class);
					startActivityForResult(intent, REQUEST_CODE_PHOTO_PREVIEW);
				}
				break;

			case REQUEST_CODE_GALLERY_PHOTO: // 图库选择
				if (data != null) {
					Uri originalUri = data.getData(); // 获得图片的uri
					String imgPath = BitMapUtil.getRealPathFromURI(ImprovePersonDataActivity.this, originalUri);
					Bitmap bitmap = BitMapUtil.loadBitmap(ImprovePersonDataActivity.this, imgPath);
					BitMapUtil.saveBitmap(KooniaoApplication.getInstance().getPicDir() + Define.PIC_NORMAL, bitmap);
					Intent intent = new Intent(ImprovePersonDataActivity.this, PhotoPreviewActivity_.class);
					intent.putExtra(Define.TYPE, Type.ROUND_CLIP.type);
					startActivityForResult(intent, REQUEST_CODE_PHOTO_PREVIEW);
				}
				break;

			case REQUEST_CODE_PHOTO_PREVIEW: // 图片预览
				Bitmap avatar = BitMapUtil.loadBitmap(ImprovePersonDataActivity.this, KooniaoApplication.getInstance().getPicDir() + Define.PIC_NORMAL);
				avatar = BitMapUtil.toRoundBitmap(avatar);
				String avatarPath = KooniaoApplication.getInstance().getPicDir() + Define.PIC_CLIP;
				BitMapUtil.saveBitmap(avatarPath, avatar);
				/**
				 * 通知修改头像
				 */
				Message message = Message.obtain();
				message.obj = avatar;
				message.what = SET_USER_AVATAR;
				handler.sendMessage(message);
				break;

			case REQUEST_CODE_ROLE_APPLY:// 角色申请
				if (resultCode == RESULT_OK) {
					updateRightText();
					applyStatusTextView.setText(R.string.wait_for_review);
				}
				break;
			}
		}
	}

}
