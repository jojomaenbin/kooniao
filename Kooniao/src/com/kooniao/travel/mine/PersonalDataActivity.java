package com.kooniao.travel.mine;

import java.io.File;

import org.androidannotations.annotations.AfterViews;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.PhotoPreviewActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.manager.UserManager.StringResultCallback;
import com.kooniao.travel.manager.UserManager.UserInfoResultCallback;
import com.kooniao.travel.model.UserInfo;
import com.kooniao.travel.user.RoleApplyActivity_;
import com.kooniao.travel.utils.BitMapUtil;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 个人资料界面
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_personal_data)
public class PersonalDataActivity extends BaseActivity {

	@ViewById(R.id.iv_personal_data_avatar)
	ImageView userAvatarImageView; // 用户头像
	@ViewById(R.id.tv_personal_data_nickname)
	TextView nickNameTextView; // 昵称
	@ViewById(R.id.tv_personal_data_sex)
	TextView sexTextView; // 性别
	@ViewById(R.id.tv_personal_data_email)
	TextView emailTextView; // 邮箱
	@ViewById(R.id.tv_personal_data_phone)
	TextView phoneTextView; // 电话
	@ViewById(R.id.ll_personal_data_guide_layout)
	LinearLayout roleLayout; // 用户角色布局
	@ViewById(R.id.tv_role_list)
	TextView roleTextView; // 用户角色
	@ViewById(R.id.tv_tour_apply_status)
	TextView applyStatusTextView; // 用户角色申请状态
	@ViewById(R.id.ll_apply)
	LinearLayout roleApplyLayout; // 用户角色申请布局

	KooniaoProgressDialog progressDialog;
	
	/**
	 * 初始化
	 */
	@AfterViews
	void init() {
		if (progressDialog == null) {
			progressDialog = new KooniaoProgressDialog(PersonalDataActivity.this);
		}
		loadUserDetailInfo();
	}

	/**
	 * 加载用户详细信息
	 */
	private void loadUserDetailInfo() {
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
		
		UserManager.getInstance().loadUserDetailInfo(new UserInfoResultCallback() {

			@Override
			public void result(String errMsg, UserInfo userInfo) {
				initView(errMsg, userInfo);
			}
		});
	}

	/**
	 * 初始化界面
	 */
	@UiThread
	void initView(String errMsg, UserInfo userInfo) {
		progressDialog.dismiss();
		
		if (errMsg == null && userInfo != null) {
			// 用户头像
			String userAvatarUrl = userInfo.getFace();
			ImageLoaderUtil.loadAvatar(ImageLoader.getInstance(), userAvatarUrl, userAvatarImageView);
			// 昵称
			String nickName = userInfo.getUname();
			nickNameTextView.setText(nickName);
			// 性别
			int sex = userInfo.getSex();
			String sexText = sex == 0 ? StringUtil.getStringFromR(R.string.sex_woman) : StringUtil.getStringFromR(R.string.sex_man);
			sexTextView.setText(sexText);
			// 邮箱
			String emailAddress = userInfo.getEmail(); 
			emailAddress = emailAddress == null || "".equals(emailAddress) ? StringUtil.getStringFromR(R.string.unbound) : emailAddress;
			emailTextView.setText(emailAddress);
			// 电话
			String phone = userInfo.getMobile();
			phone = phone == null  || "".equals(phone) ? StringUtil.getStringFromR(R.string.unbound) : phone;
			phoneTextView.setText(phone);
			// 用户权限
			int userType = userInfo.getUserType();
			// 角色布局是否显示
			int roleApplyLayoutVisibility = userType == 0 ? View.VISIBLE : View.GONE;
			roleApplyLayout.setVisibility(roleApplyLayoutVisibility);
			// 角色布局是否显示
			int roleLayoutVisibility = userType == 0 ? View.GONE : View.VISIBLE;
			roleLayout.setVisibility(roleLayoutVisibility);
			if (roleLayoutVisibility == View.VISIBLE) {
				String roleText = userInfo.getUserProfession();
				roleTextView.setText(roleText);
			}

		} else {
			Toast.makeText(getBaseContext(), errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		activityFinish();
	}

	/**
	 * 头像item点击
	 */
	@Click(R.id.lr_personal_data_avatar)
	void onUserAvatarItemClick() {
		popupAvatarSelectView();
	}

	/**
	 * 弹出头像修改选择界面
	 */
	private void popupAvatarSelectView() {
		View selectPhotoView = LayoutInflater.from(PersonalDataActivity.this).inflate(R.layout.popupwindow_avatar_select, null);
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
	 * 昵称item点击
	 */
	@Click(R.id.lr_personal_data_nickname)
	void onNickNameItemClick() {
		Intent intent = new Intent(PersonalDataActivity.this, UpdateUserNameActivity_.class);
		intent.putExtra(Define.SEX, sexTextView.getText().toString()); 
		startActivityForResult(intent, REQUEST_CODE_NICKNAME); 
	}

	/**
	 * 性别item点击
	 */
	@Click(R.id.lr_personal_data_sex)
	void onSexItemClick() {
		popupSexSelectView();
	}

	boolean isDataChange = false; // 页面数据是否更改

	/**
	 * 弹出性别选择界面
	 */
	private void popupSexSelectView() {
		View selectPhotoView = LayoutInflater.from(PersonalDataActivity.this).inflate(R.layout.popupwindow_sex_select, null);
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
					Toast.makeText(getBaseContext(), updateUserInfoSuccessTips, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getBaseContext(), errMsg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

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
					isDataChange = true;
					Toast.makeText(getBaseContext(), updateUserAvatarSuccessTips, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getBaseContext(), errMsg, Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};

	/**
	 * 绑定邮箱item点击
	 */
	@Click(R.id.lr_personal_data_email)
	void onEmailItemClick() {
		String email = emailTextView.getText().toString();
		String unBoundString = StringUtil.getStringFromR(R.string.unbound);
		if (email.equals(unBoundString)) {
			Intent intent = new Intent(PersonalDataActivity.this, BindingEmailActivity_.class);
			startActivityForResult(intent, REQUEST_CODE_BIND_EMAIL); 
		}
	}

	/**
	 * 绑定电话item点击
	 */
	@Click(R.id.lr_personal_data_phone)
	void onPhoneItemClick() {
		String email = phoneTextView.getText().toString();
		String unBoundString = StringUtil.getStringFromR(R.string.unbound);
		if (email.equals(unBoundString)) {
			Intent intent = new Intent(PersonalDataActivity.this, BindingPhoneActivity_.class);
			startActivityForResult(intent, REQUEST_CODE_BIND_PHONE); 
		}
	}

	/**
	 * 角色申请item点击
	 */
	@Click(R.id.lr_tour_apply)
	void onRoleApplyItemClick() {
		Intent intent = new Intent(PersonalDataActivity.this, RoleApplyActivity_.class);
		startActivityForResult(intent, REQUEST_CODE_ROLE_APPLY);
	}

	/**
	 * 结束当前activity
	 */
	private void activityFinish() {
		Intent intent = new Intent();
		intent.putExtra(Define.DATA, isDataChange);
		setResult(RESULT_OK, intent); 
		finish();
	}

	@Override
	public void onBackPressed() {
		activityFinish();
	}

	final int REQUEST_CODE_TAKE_PHOTO = 11; // 照相
	final int REQUEST_CODE_GALLERY_PHOTO = 12; // 图库
	final int REQUEST_CODE_PHOTO_PREVIEW = 13; // 图片预览
	final int REQUEST_CODE_NICKNAME = 14; // 昵称
	final int REQUEST_CODE_ROLE_APPLY = 15; // 角色申请
	final int REQUEST_CODE_BIND_EMAIL = 16; // 绑定邮箱
	final int REQUEST_CODE_BIND_PHONE = 17; // 绑定手机

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_TAKE_PHOTO: // 拍照
				File file = new File(KooniaoApplication.getInstance().getPicDir() + Define.PIC_NORMAL);
				if (file.exists()) {
					Intent intent = new Intent(PersonalDataActivity.this, PhotoPreviewActivity_.class);
					startActivityForResult(intent, REQUEST_CODE_PHOTO_PREVIEW);
				}
				break;

			case REQUEST_CODE_GALLERY_PHOTO: // 图库选择
				if (data != null) {
					Uri originalUri = data.getData(); // 获得图片的uri
					String imgPath = BitMapUtil.getRealPathFromURI(PersonalDataActivity.this, originalUri);
					Bitmap bitmap = BitMapUtil.loadBitmap(PersonalDataActivity.this, imgPath);
					boolean result = BitMapUtil.saveBitmap(KooniaoApplication.getInstance().getPicDir() + Define.PIC_NORMAL, bitmap);
					if (result) {
						Intent intent = new Intent(PersonalDataActivity.this, PhotoPreviewActivity_.class);
						startActivityForResult(intent, REQUEST_CODE_PHOTO_PREVIEW);
					} else {
						Toast.makeText(PersonalDataActivity.this, "获取图片失败，请重新选择！", Toast.LENGTH_SHORT).show();
					}
				}
				break;

			case REQUEST_CODE_PHOTO_PREVIEW: // 图片预览
				Bitmap avatar = BitMapUtil.loadBitmap(PersonalDataActivity.this, KooniaoApplication.getInstance().getPicDir() + Define.PIC_NORMAL);
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

			case REQUEST_CODE_NICKNAME: // 昵称
				if (resultCode == RESULT_OK && data != null) {
					String name = data.getStringExtra(Define.DATA);
					if (name != null) {
						isDataChange = true;
						nickNameTextView.setText(name);
					}
				}
				break;

			case REQUEST_CODE_ROLE_APPLY: // 角色申请回来
				if (resultCode == RESULT_OK) {
					loadUserDetailInfo(); 
				}
				break;

			case REQUEST_CODE_BIND_EMAIL: // 绑定邮箱
			case REQUEST_CODE_BIND_PHONE: // 绑定手机
				if (resultCode == RESULT_OK) {
					isDataChange = true;
					loadUserDetailInfo(); 
				}
				break;
			}

		}
	}

}
