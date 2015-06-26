package com.kooniao.travel.discovery;

import java.util.ArrayList;
import java.util.List;

import com.kooniao.travel.R;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.model.TeamCustomer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SectionIndexer;
import android.widget.TextView;

@SuppressLint("DefaultLocale")
public class TeamCustomerAdapter extends BaseAdapter implements SectionIndexer {
	private Context context;

	public TeamCustomerAdapter(Context context) {
		this.context = context;
	}

	private List<TeamCustomer> teamCustomerList = new ArrayList<TeamCustomer>();

	public void setTeamCustomerList(List<TeamCustomer> teamCustomerList) {
		if (teamCustomerList != null) {
			this.teamCustomerList = teamCustomerList;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return teamCustomerList.size();
	}

	@Override
	public Object getItem(int position) {
		return teamCustomerList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_contact, null);
			holder.circleImageView = (ImageView) convertView.findViewById(R.id.iv_contact_circle);
			holder.letterTextView = (TextView) convertView.findViewById(R.id.tv_contact_letter);
			holder.nameTextView = (TextView) convertView.findViewById(R.id.tv_contact_name);
			holder.phoneCallImageView = (ImageView) convertView.findViewById(R.id.iv_contact_call);
			holder.deleteButton = (ImageButton) convertView.findViewById(R.id.ib_delete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		TeamCustomer teamCustomer = teamCustomerList.get(position);
		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			holder.letterTextView.setVisibility(View.VISIBLE);
			String sortLetter = teamCustomer.getSortLetters();
			holder.letterTextView.setText(sortLetter);
			LayoutParams layoutParams = new LayoutParams(Define.widthPx / 5, LayoutParams.MATCH_PARENT);
			layoutParams.setMargins(0, (int) (29 * Define.DENSITY), 0, 0);
			holder.deleteButton.setLayoutParams(layoutParams);
		} else {
			holder.letterTextView.setVisibility(View.GONE);
			LayoutParams layoutParams = new LayoutParams(Define.widthPx / 5, LayoutParams.MATCH_PARENT);
			holder.deleteButton.setLayoutParams(layoutParams);
		}
		// 是否处于可选择状态
		if (teamCustomer.isCanSelect()) {
			holder.circleImageView.setVisibility(View.VISIBLE);
		} else {
			holder.circleImageView.setVisibility(View.GONE);
		}
		// 选择状态
		if (teamCustomer.isSelected()) {
			holder.circleImageView.setImageResource(R.drawable.circle_selected);
		} else {
			holder.circleImageView.setImageResource(R.drawable.circle_unselected);
		}

		if (listener != null) {
			holder.circleImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onCircleImageClickListener(position);
				}
			});

			holder.phoneCallImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onPhoneCallClickListener(position);
				}
			});
		}
		// 名字
		String name = teamCustomer.getName();
		holder.nameTextView.setText(name);

		return convertView;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return teamCustomerList.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = teamCustomerList.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	static class ViewHolder {
		TextView letterTextView;
		TextView nameTextView;
		ImageView phoneCallImageView;
		ImageView circleImageView;
		ImageButton deleteButton;
	}

	public interface ListItemRequestListener {
		void onPhoneCallClickListener(int position);

		void onCircleImageClickListener(int position);
	}

	private ListItemRequestListener listener;

	public void setOnListItemRequestListener(ListItemRequestListener listener) {
		this.listener = listener;
	}

}
