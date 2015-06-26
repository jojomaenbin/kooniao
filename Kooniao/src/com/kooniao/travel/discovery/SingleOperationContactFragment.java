package com.kooniao.travel.discovery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.api.ApiCaller.APITeamCustomerResultCallback;
import com.kooniao.travel.base.BaseFragment;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.customwidget.SideBar;
import com.kooniao.travel.customwidget.SwipeListView;
import com.kooniao.travel.customwidget.SideBar.OnTouchingLetterChangedListener;
import com.kooniao.travel.customwidget.SwipeListView.SideSlipOptionCallback;
import com.kooniao.travel.manager.TravelManager;
import com.kooniao.travel.manager.TravelManager.StringResultCallback;
import com.kooniao.travel.model.TeamCustomer;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.CharacterParser;
import com.kooniao.travel.utils.SortListCollections;
import com.kooniao.travel.utils.SortListCollections.Sort;

/**
 * 团单操作联系人界面
 * 
 * @author ke.wei.quan
 * 
 */
@EFragment(R.layout.fragment_mass_single_operation_contact)
public class SingleOperationContactFragment extends BaseFragment implements SideSlipOptionCallback, TeamCustomerAdapter.ListItemRequestListener {

	private int travelId;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		travelId = getArguments().getInt(Define.PID, 0);
	}

	@ViewById(R.id.slv_contact_list)
	SwipeListView swipeListView;
	@ViewById(R.id.sb_right)
	SideBar sideBar; // 右侧条形bar
	@ViewById(R.id.tv_contact_selected_tips)
	TextView tipTextView; // 中心提示
	@ViewById(R.id.ll_contact_mass_texting_tips)
	LinearLayout massTextingTipsLayout;
	@ViewById(R.id.tv_contact_count)
	TextView contactCount; // 已选人数比例
	@ViewById(R.id.tv_contact_select_all)
	TextView selectAllTextView; // 全选
	@ViewById(R.id.tv_contact_confirm)
	TextView confirmTextView; // 确认
	@ViewById(R.id.tv_contact_mass_texting)
	TextView massTextView; // 群发短信

	private boolean isSelectedAll = false;

	/**
	 * 全选
	 */
	@Click(R.id.tv_contact_select_all)
	void onSelectAllClick() {
		if (!isSelectedAll) {
			isSelectedAll = true;
			for (TeamCustomer teamCustomer : teamCustomers) {
				teamCustomer.setSelected(true);
			}
		} else {
			isSelectedAll = false;
			for (TeamCustomer teamCustomer : teamCustomers) {
				teamCustomer.setSelected(false);
			}
		}
		setSelectedContact();
		adapter.setTeamCustomerList(teamCustomers);
	}

	/**
	 * 群发短信布局点击
	 */
	@Click(R.id.ll_contact_mass_texting_tips)
	void onMassTextingLayoutClick() {
		isMass = false;
		massTextView.setVisibility(View.VISIBLE);
		massTextingTipsLayout.setVisibility(View.GONE);
		for (TeamCustomer teamCustomer : teamCustomers) {
			teamCustomer.setCanSelect(false);
			teamCustomer.setSelected(false); 
		}
		adapter.setTeamCustomerList(teamCustomers);
	}

	/**
	 * 确认
	 */
	@Click(R.id.tv_contact_confirm)
	void onConfirmClick() {
		List<TeamCustomer> teamCustomerList = new ArrayList<TeamCustomer>();
		for (TeamCustomer teamCustomer : teamCustomers) {
			if (teamCustomer.isSelected()) {
				teamCustomerList.add(teamCustomer);
			}
		}
		if (teamCustomerList.isEmpty()) {
			Toast.makeText(getActivity(), "请选择联系人!", Toast.LENGTH_LONG).show();
		} else {
			Intent intent = new Intent(getActivity(), MassTextingActivity_.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(Define.DATA, (Serializable) teamCustomerList);
			intent.putExtras(bundle);
			startActivity(intent); 
		}
	}

	private boolean isMass = false; // 是否处于群发短信布局

	/**
	 * 群发短信
	 */
	@Click(R.id.tv_contact_mass_texting)
	void onMassTextingClick() {
		if (teamCustomers != null && !teamCustomers.isEmpty()) {
			if (!isMass) {
				isMass = true;
				massTextView.setVisibility(View.GONE);
				massTextingTipsLayout.setVisibility(View.VISIBLE);
				for (TeamCustomer teamCustomer : teamCustomers) {
					teamCustomer.setCanSelect(true);
				}
			}
			setSelectedContact();
			adapter.setTeamCustomerList(teamCustomers);
		}
	}

	private TeamCustomerAdapter adapter;
	KooniaoProgressDialog progressDialog;

	@AfterViews
	void initView() {
		if (progressDialog == null) {
			progressDialog = new KooniaoProgressDialog(getActivity());
		}
		
		sideBar.setTextView(tipTextView);
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					swipeListView.setSelection(position);
				}

			}
		});
		adapter = new TeamCustomerAdapter(getActivity());
		adapter.setOnListItemRequestListener(this);
		swipeListView.setAdapter(adapter);
		swipeListView.setBackViewOffSet((int) (95 * Define.DENSITY));
		swipeListView.setSideSlipOptionCallback(this);
		swipeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (teamCustomers != null && !teamCustomers.isEmpty()) {
					View dialogContentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_team_user_info_contentview, null);
					TextView nameTextView = (TextView) dialogContentView.findViewById(R.id.tv_user_name);
					TextView sexTextView = (TextView) dialogContentView.findViewById(R.id.tv_sex);
					TextView phoneTextView = (TextView) dialogContentView.findViewById(R.id.tv_mobile);
					TextView idNumTextView = (TextView) dialogContentView.findViewById(R.id.tv_num);
					TeamCustomer teamCustomer = teamCustomers.get(position);
					// 姓名
					nameTextView.setText(teamCustomer.getName());
					// 性别
					sexTextView.setText(teamCustomer.getGender());
					// 电话
					phoneTextView.setText(teamCustomer.getTel());
					// ID号
					idNumTextView.setText(teamCustomer.getCertificate());
					dialog = new Dialog(getActivity(), View.VISIBLE, View.GONE, dialogContentView);
					dialog.setCancelable(false);
					dialog.setOnAcceptButtonClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.show();
				}
			}
		});
		// 加载列表数据
		loadTeamCustomerList();
	}

	private int teamId; // 团单id

	/**
	 * 获取联系人列表
	 */
	private void loadTeamCustomerList() {
		TravelManager.getInstance().loadTeamCustomerList(travelId, new APITeamCustomerResultCallback() {

			@Override
			public void result(String errMsg, int teamId, List<TeamCustomer> teamCustomers) {
				loadTeamCustomerListComplete(errMsg, teamId, teamCustomers);
			}
		});
	}

	private List<TeamCustomer> teamCustomers;

	/**
	 * 获取联系人列别完成
	 * 
	 * @param errMsg
	 * @param teamId
	 * @param teamCustomers
	 */
	private void loadTeamCustomerListComplete(String errMsg, int teamId, List<TeamCustomer> teamCustomers) {
		if (errMsg == null && teamCustomers != null) {
			if (teamCustomers.isEmpty()) {
				dialogTitle = "暂无团单信息";
				String dialogMessage = "请到电脑端添加参团名单";
				dialog = new Dialog(getActivity(), View.VISIBLE, View.GONE, dialogTitle, dialogMessage);
				dialog.setCancelable(false);
				dialog.setOnAcceptButtonClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						getActivity().finish();
						dialog.dismiss();
					}
				});
				dialog.show();
			} else {
				this.teamId = teamId;
				this.teamCustomers = teamCustomers;
				setLetter(teamCustomers);
				SortListCollections<TeamCustomer> sortListCollections = new SortListCollections<TeamCustomer>();
				sortListCollections.sort(teamCustomers, "sortLetters", Sort.ASC);
				adapter.setTeamCustomerList(teamCustomers);
				// 保存到本地
				AppSetting.getInstance().saveIntPreferencesByKey(Define.TEAM_ID, teamId); 
			}
		} else {
			Toast.makeText(getActivity(), errMsg, Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * 获取团单id
	 * @return
	 */
	protected int getTeamId() {
		return teamId;
	}

	/**
	 * 设置首字母
	 * 
	 * @param teamCustomers
	 */
	private void setLetter(List<TeamCustomer> teamCustomers) {
		for (int i = 0; i < teamCustomers.size(); i++) {
			TeamCustomer teamCustomer = teamCustomers.get(i);
			// 汉字转换成拼音
			String pinyin = CharacterParser.getInstance().getSelling(teamCustomer.getName());
			String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINA);
			teamCustomer.setSortLetters(sortString.toUpperCase(Locale.CHINA));
		}
	}

	@Override
	public void onSideSlipOptionSelected(int menuType, int position) {
		final TeamCustomer teamCustomer = teamCustomers.get(position);
		if (menuType == 3) {
			dialogTitle = "删除联系人";
			String dialogMessage = "确定删除" + teamCustomer.getName() + "吗？";
			dialog = new Dialog(getActivity(), dialogTitle, dialogMessage);
			dialog.setCancelable(false);
			dialog.setOnAcceptButtonClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					deleteUser(teamCustomer);
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
	}

	/**
	 * 删除联系人
	 * 
	 * @param teamCustomer
	 */
	private void deleteUser(final TeamCustomer teamCustomer) {
		if (teamCustomer != null) {
			if (!progressDialog.isShowing()) {
				progressDialog.show();
			}
			TravelManager.getInstance().deleteTeamCustomer(teamId, teamCustomer.getName(), teamCustomer.getTel(), new StringResultCallback() {
				
				@Override
				public void result(String errMsg) {
					progressDialog.dismiss();
					if (errMsg == null) {
						teamCustomers.remove(teamCustomer);
						adapter.setTeamCustomerList(teamCustomers); 
					} else {
						Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}

	@StringRes(R.string.dial)
	String dialogTitle; // 拨打电话
	Dialog dialog; // 拨打电话确认对话框

	@Override
	public void onPhoneCallClickListener(int position) {
		TeamCustomer teamCustomer = teamCustomers.get(position);
		final String phoneNum = teamCustomer.getTel();
		dialog = new Dialog(getActivity(), dialogTitle, phoneNum);
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

	@Override
	public void onCircleImageClickListener(int position) {
		TeamCustomer teamCustomer = teamCustomers.get(position);
		if (teamCustomer.isSelected()) {
			teamCustomer.setSelected(false);
		} else {
			teamCustomer.setSelected(true);
		}
		setSelectedContact();
		adapter.setTeamCustomerList(teamCustomers);
	}

	/**
	 * 设置已选择的联系人人数
	 */
	private void setSelectedContact() {
		int selectedCount = 0;
		boolean isAllSelected = true;
		for (TeamCustomer teamCustomer : teamCustomers) {
			if (teamCustomer.isSelected()) {
				selectedCount++;
			}

			isAllSelected = isAllSelected && teamCustomer.isSelected();
		}

		if (isAllSelected) {
			selectAllTextView.setText("全不选");
		} else {
			selectAllTextView.setText("全选");
		}
		contactCount.setText(selectedCount + "/" + teamCustomers.size());
	}

}
