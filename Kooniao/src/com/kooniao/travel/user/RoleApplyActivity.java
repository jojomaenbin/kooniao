package com.kooniao.travel.user;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.manager.UserManager.RoleListResultCallback;
import com.kooniao.travel.manager.UserManager.StringResultCallback;
import com.kooniao.travel.model.Role;

/**
 * 角色申请页
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_role_apply)
public class RoleApplyActivity extends BaseActivity { 
	@ViewById(R.id.tv_apply_type)
	TextView applyTypeTextView; // 申请类型
	@ViewById(R.id.et_real_name)
	EditText realNameEditText; // 真实姓名输入框
	@ViewById(R.id.et_certification_num)
	EditText certificationNumEditText; // 真实姓名输入框
	@ViewById(R.id.et_phone_num)
	EditText phoneNumEditText; // 电话号码输入框

	/**
	 * 点击取消
	 */
	@Click(R.id.tv_tour_apply_cancel)
	void onCancelClick() {
		finish();
	}

	KooniaoProgressDialog progressDialog;

	/**
	 * 点击选择申请类型
	 */
	@Click(R.id.rl_apply_select_type)
	void onApplicationTypeClick() {
		if (progressDialog == null) {
			progressDialog = new KooniaoProgressDialog(RoleApplyActivity.this);
		}
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
		UserManager.getInstance().getRoleList(new RoleListResultCallback() {

			@Override
			public void result(String errMsg, List<Role> roles) {
				getRoleListComplete(errMsg, roles);
			}
		});
	}

	/**
	 * 获取角色列表请求完成
	 * 
	 * @param errMsg
	 * @param roles2
	 */
	private void getRoleListComplete(String errMsg, List<Role> roles) {
		progressDialog.dismiss();
		if (errMsg == null && roles != null) {
			this.roles = roles;
			popupRoleListSelectView();
		} else {
			Toast.makeText(RoleApplyActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	private List<Role> roles = new ArrayList<Role>(); // 角色列表
	private int roleId; // 角色id
	PopupWindow popupWindow;

	/**
	 * 弹出角色选择列表
	 * 
	 * @param roles
	 */
	private void popupRoleListSelectView() {
		View selectRoleView = LayoutInflater.from(RoleApplyActivity.this).inflate(R.layout.popupwindow_role_apply_type, null);
		popupWindow = new PopupWindow(selectRoleView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		popupWindow.setFocusable(false);
		popupWindow.setAnimationStyle(R.style.PopupAnimationFromBottom); 
		final ListView listView = (ListView) selectRoleView.findViewById(R.id.listview);
		listView.setAdapter(new RoleListAdapter());
		selectRoleView.findViewById(R.id.iv_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				popupWindow.dismiss();
			}
		});

		popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
	}

	class RoleListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return roles.size();
		}

		@Override
		public Object getItem(int position) {
			return roles.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder viewHolder = null;
			if (view == null) {
				view = LayoutInflater.from(RoleApplyActivity.this).inflate(R.layout.item_role_list, null);
				viewHolder = new ViewHolder();
				viewHolder.roleNameTextView = (TextView) view.findViewById(R.id.tv_role_name);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			final Role role = roles.get(position);
			viewHolder.roleNameTextView.setText(role.getTitle());
			viewHolder.roleNameTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Role role = roles.get(position);
					roleId = role.getId();
					applyTypeTextView.setText(role.getTitle()); 
					popupWindow.dismiss();
				}
			});
			return view;
		}
	}

	static class ViewHolder {
		TextView roleNameTextView;
	}

	/**
	 * 点击提交申请
	 */
	@Click(R.id.bt_submit_application)
	void onSubmitClick() {
		// 真实姓名
		String realName = realNameEditText.getText().toString().trim();
		// 资格证号
		String certificationNum = certificationNumEditText.getText().toString().trim();
		// 电话号码
		String phoneNum = phoneNumEditText.getText().toString().trim();
		if (roleId == 0) {
			Toast.makeText(RoleApplyActivity.this, R.string.select_type_wrong_tips, Toast.LENGTH_SHORT).show();
		} else if ("".equals(realName)) {
			Toast.makeText(RoleApplyActivity.this, R.string.hint_input_real_name, Toast.LENGTH_SHORT).show();
		} else if ("".equals(certificationNum)) {
			Toast.makeText(RoleApplyActivity.this, R.string.hint_input_certification_num, Toast.LENGTH_SHORT).show();
		} else if ("".equals(phoneNum)) {
			Toast.makeText(RoleApplyActivity.this, R.string.hint_input_phone_num, Toast.LENGTH_SHORT).show();
		} else if (phoneNum.length() != 11) {
			Toast.makeText(RoleApplyActivity.this, R.string.phone_num_form_wrong, Toast.LENGTH_SHORT).show();
		} else {
			UserManager.getInstance().roleApply(realName, certificationNum, phoneNum, roleId, new StringResultCallback() {

				@Override
				public void result(String errMsg) {
					if (errMsg == null) {
						setResult(RESULT_OK);
						finish();
						Toast.makeText(RoleApplyActivity.this, R.string.submit_success, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(RoleApplyActivity.this, errMsg, Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}
}
