package com.kooniao.travel.discovery;

import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.customwidget.TagListView;
import com.kooniao.travel.customwidget.TagView;
import com.kooniao.travel.customwidget.TagListView.OnTagCheckedChangedListener;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.manager.UserManager.RollCallDetailResultCallback;
import com.kooniao.travel.manager.UserManager.StringResultCallback;
import com.kooniao.travel.model.RollCallDetail;
import com.kooniao.travel.model.RollCallDetail.Tourist;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.DateUtil;
import com.kooniao.travel.utils.JsonTools;

/**
 * 点名详情页
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_rollcall_detail)
public class RollCallDetailActivity extends BaseActivity {
	@ViewById(R.id.tv_call_name_time)
	TextView timeTextView; // 标题时间
	@ViewById(R.id.tv_call_name_count)
	TextView countTextView; // 人数统计
	@ViewById(R.id.tagview)
	TagListView tagListView;
	@ViewById(R.id.iv_call_name_help)
	ImageView helpImageView; // 帮助页

	@AfterViews
	void init() {
		initData();
		initView();
	}

	KooniaoProgressDialog progressDialog;
	private List<Tourist> touristList;
	private int rollCallId; // 点名名单id
	private int teamId; // 团单id

	/**
	 * 初始化界面数据
	 */
	@SuppressWarnings("unchecked")
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			rollCallId = intent.getIntExtra(Define.ROLLCALL_ID, 0);
			teamId = intent.getIntExtra(Define.TEAM_ID, 0);
			touristList = (List<Tourist>) intent.getSerializableExtra(Define.DATA);
		}

		if (touristList != null && !touristList.isEmpty()) {
			isRollCallChange = true;
			tagListView.setTags(touristList);
			long currentTimeStamp = System.currentTimeMillis() / 1000;
			String time = DateUtil.timeDistanceString(currentTimeStamp, Define.FORMAT_YMDHM);
			timeTextView.setText(time);
			countSelected();
		} else {
			if (progressDialog == null) {
				progressDialog = new KooniaoProgressDialog(RollCallDetailActivity.this);
			}
			if (!progressDialog.isShowing()) {
				progressDialog.show();
			}
			loadRolllCallDetail();
		}
	}

	private boolean isRollCallChange = false; // 点名名单是否更改
	@StringRes(R.string.call)
	String dialogTitle;
	Dialog dialog;

	/**
	 * 初始化界面
	 */
	private void initView() {
		boolean isNotFirstTime = AppSetting.getInstance().getBooleanPreferencesByKey(Define.IS_NOT_FIRST_TIME_IN_ROLL_CALL);
		if (!isNotFirstTime) {
			helpImageView.setVisibility(View.VISIBLE);
		} else {
			helpImageView.setVisibility(View.GONE);
		}

		tagListView.setOnTagCheckedChangedListener(new OnTagCheckedChangedListener() {

			@Override
			public void onTagCheckedChanged(TagView tagView, Tourist tourist, boolean isChecked) {
				isRollCallChange = true;
				if (isChecked) {
					tourist.setState(1);
					tagView.changeSelectedBackground();
				} else {
					tourist.setState(0);
					tagView.changeNormalBackground();
				}
				countSelected();
			}

			@Override
			public void onLongClickListener(TagView tagView, Tourist tourist) {
				final String phoneNum = tourist.getTel();
				dialog = new Dialog(RollCallDetailActivity.this, dialogTitle, phoneNum);
				dialog.setCancelable(false);
				dialog.setOnAcceptButtonClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_DIAL);
						intent.setData(Uri.parse("tel:" + phoneNum));
						startActivity(intent);
					}
				});
				dialog.setOnCancelButtonClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});

	}

	/**
	 * 获取点名名单详情
	 */
	private void loadRolllCallDetail() {
		UserManager.getInstance().loadRollCallDetail(teamId, rollCallId, new RollCallDetailResultCallback() {

			@Override
			public void result(String errMsg, RollCallDetail rollCallDetail) {
				loadRolllCallDetailComplete(errMsg, rollCallDetail);
			}
		});
	}

	/**
	 * 获取点名名单详情请求完成
	 * 
	 * @param errMsg
	 * @param rollCallDetail
	 */
	private void loadRolllCallDetailComplete(String errMsg, RollCallDetail rollCallDetail) {
		progressDialog.dismiss();
		if (errMsg == null && rollCallDetail != null) {
			// 设置时间
			long rollCallTimeStamp = rollCallDetail.getRollCallTime();
			String rollCallTime = DateUtil.timeDistanceString(rollCallTimeStamp, Define.FORMAT_YMDHM);
			timeTextView.setText(rollCallTime);
			// 设置已选择的点名人数
			this.touristList = rollCallDetail.getTouristList();
			countSelected();
			List<Tourist> tourists = rollCallDetail.getTouristList();
			if (tourists != null) {
				tagListView.setTags(tourists);
			}
		} else {
			Toast.makeText(RollCallDetailActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 统计已选择的名单
	 */
	private void countSelected() {
		if (touristList != null) {
			int count = 0;
			for (Tourist tourist : touristList) {
				if (tourist.getState() == 1) {
					count++;
				}
			}
			countTextView.setText(count + "/" + touristList.size());
		}
	}

	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		activityFinish();
	}

	@Override
	public void onBackPressed() {
		activityFinish();
		super.onBackPressed();
	}

	/**
	 * 结束当前界面
	 */
	private void activityFinish() {
		if (isRollCallChange) {
			setResult(RESULT_OK);
		}
		finish();
	}

	/**
	 * 全选
	 */
	@Click(R.id.tv_call_name_all_select)
	void onNameAllSelectClick() {
		if (touristList != null) {
			for (Tourist tourist : touristList) {
				tourist.setState(1);
			}
			countSelected();
			isRollCallChange = true;
			tagListView.setTags(touristList);
		}
	}

	/**
	 * 保存点名名单
	 */
	@Click(R.id.tv_call_name_save)
	void onRollCallSaveClick() {
		String touristListJson = JsonTools.listToJson(touristList);
		UserManager.getInstance().editOrAddRollCall(touristListJson, teamId, rollCallId, new StringResultCallback() {

			@Override
			public void result(String errMsg) {
				if (errMsg == null) {
					activityFinish();
				} else {
					Toast.makeText(RollCallDetailActivity.this, errMsg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 点击帮助页
	 */
	@Click(R.id.iv_call_name_help)
	void onHelpClick() {
		AppSetting.getInstance().saveBooleanPreferencesByKey(Define.IS_NOT_FIRST_TIME_IN_ROLL_CALL, true);
		helpImageView.setVisibility(View.GONE);
	}

}
