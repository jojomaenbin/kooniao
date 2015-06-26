package com.kooniao.travel.discovery;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseFragment;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.customwidget.LinearListLayout;
import com.kooniao.travel.manager.TravelManager;
import com.kooniao.travel.manager.TravelManager.GroupInfoResultCallback;
import com.kooniao.travel.model.GroupInfo;
import com.kooniao.travel.model.PlanGuide;

/**
 * 团信息界面
 * 
 * @author ke.wei.quan
 * 
 */
@EFragment(R.layout.fragment_mass_single_operation_group_info)
public class SingleOperationGroupInfoFragment extends BaseFragment {
	@ViewById(R.id.tv_message_travel_unit)
	TextView travelUnitTextView; // 出行单位
	@ViewById(R.id.tv_message_travel_date)
	TextView travelDateTextView; // 出行时间

	@ViewById(R.id.ll_guide_list)
	LinearListLayout guideListLayout; // 全陪导游列表
	PlanGuideAdapter guideAdapter;

	@ViewById(R.id.ll_localguide_list)
	LinearListLayout localGuideListLayout; // 地陪导游列表
	PlanGuideAdapter localGuideAdapter;

	@ViewById(R.id.ll_drivers_list)
	LinearListLayout driversListLayout; // 随行司机导游列表
	PlanGuideAdapter driversAdapter;

	@ViewById(R.id.tv_message_travel_car_number)
	TextView carNumberTextView; // 车牌号码
	@ViewById(R.id.tv_message_travel_adult_count)
	TextView adultTextView; // 成人
	@ViewById(R.id.tv_message_travel_oldman_count)
	TextView oldmanTextView; // 老人
	@ViewById(R.id.tv_message_travel_children_count)
	TextView childrenTextView; // 儿童
	@ViewById(R.id.tv_message_travel_handicapped_count)
	TextView handicappedTextView; // 残疾人
	@ViewById(R.id.tv_message_travel_soldier_count)
	TextView soldierTextView; // 军人
	@ViewById(R.id.tv_message_travel_number)
	TextView travelCountTextView; // 出行人数
	
	private int travelId;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		travelId = getArguments().getInt(Define.PID, 0);
		initData();
	}
	
	KooniaoProgressDialog progressDialog;
	
	@AfterViews
	void initData() {
		if (progressDialog == null) {
			progressDialog = new KooniaoProgressDialog(getActivity());
		}

		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
		
		TravelManager.getInstance().loadGroupInfo(travelId, new GroupInfoResultCallback() {
			
			@Override
			public void result(String errMsg, GroupInfo groupInfo) {
				progressDialog.dismiss();
				setViewInfo(groupInfo);
			}
		});
	}


	/**
	 * 初始化view
	 * 
	 * @param view
	 */
	private void setViewInfo(GroupInfo groupInfo) {
		travelUnitTextView.setText(groupInfo.getTeamOrganization());
		travelDateTextView.setText(groupInfo.getStime());
		carNumberTextView.setText(groupInfo.getCarLicense());
		adultTextView.setText(groupInfo.getAdult()+"");
		oldmanTextView.setText(groupInfo.getOldMan()+"");
		childrenTextView.setText(groupInfo.getChildren()+"");
		handicappedTextView.setText(groupInfo.getHandicapped()+"");
		soldierTextView.setText(groupInfo.getSoldier()+"");
		travelCountTextView.setText(groupInfo.getTeamCount()+"");
		
		guideAdapter = new PlanGuideAdapter(groupInfo.getGuide(), "全陪导游");
		guideListLayout.setBaseAdapter(guideAdapter);
		
		localGuideAdapter = new PlanGuideAdapter(groupInfo.getLocalGuide(), "地陪导游");
		localGuideListLayout.setBaseAdapter(localGuideAdapter);
		
		driversAdapter = new PlanGuideAdapter(groupInfo.getDriver(), "随行司机");
		driversListLayout.setBaseAdapter(driversAdapter);
	}

	Dialog dialog;
	public class PlanGuideAdapter extends BaseAdapter {
		private List<PlanGuide> guides = new ArrayList<PlanGuide>();
		private String guideType;

		public PlanGuideAdapter(List<PlanGuide> guides, String guideType) {
			this.guides = guides;
			this.guideType = guideType;
		}

		@Override
		public int getCount() {
			return guides.size();
		}

		@Override
		public Object getItem(int position) {
			return guides.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder viewHolder;
			if (view == null) {
				view = LayoutInflater.from(getActivity()).inflate(R.layout.item_planguide, null);
				viewHolder = new ViewHolder();
				viewHolder.typeTextView = (TextView) view.findViewById(R.id.tv_planteam_info_planguide_type);
				viewHolder.planGuideNameTextView = (TextView) view.findViewById(R.id.tv_message_travel_tour_guide_name);
				viewHolder.planGuideTelTextView = (TextView) view.findViewById(R.id.tv_message_travel_tour_guide_phone);
				viewHolder.phoneCallImageView = (ImageView) view.findViewById(R.id.iv_message_phone);

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			final PlanGuide planGuide = guides.get(position);
			viewHolder.typeTextView.setText(guideType);
			viewHolder.planGuideNameTextView.setText(planGuide.getName());
			viewHolder.planGuideTelTextView.setText(planGuide.getTel());
			viewHolder.phoneCallImageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					makeADial(planGuide.getTel()); 
				}
			});

			return view;
		}

	}

	static class ViewHolder {
		TextView typeTextView;
		TextView planGuideNameTextView;
		TextView planGuideTelTextView;
		ImageView phoneCallImageView;
	}

	/**
	 * 拨打电话
	 * 
	 * @param telNum
	 */
	private void makeADial(final String telNum) {
	    dialog = new Dialog(getActivity(), "拨打电话", telNum);
		dialog.setCancelable(false);
		dialog.setOnAcceptButtonClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telNum));
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
}
