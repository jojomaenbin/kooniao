package com.kooniao.travel.customwidget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;

import com.kooniao.travel.R;
import com.kooniao.travel.model.RollCallDetail;
import com.kooniao.travel.model.RollCallDetail.Tourist;

public class TagListView extends FlowLayout {

	private OnTagCheckedChangedListener mOnTagCheckedChangedListener;
	private final List<Tourist> planUsers = new ArrayList<RollCallDetail.Tourist>();

	/**
	 * @param context
	 */
	public TagListView(Context context) {
		this(context, null);
	}

	/**
	 * @param context
	 * @param attributeSet
	 */
	public TagListView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	private void inflateTagView(final Tourist tourist) {
		TagView tagView = (TagView) View.inflate(getContext(), R.layout.item_tagview, null);
		tagView.setText(tourist.getName());
		tagView.setTag(tourist);
		boolean isChecked = tourist.getState() == 0 ? false : true;
		tagView.setChecked(isChecked);
		if (isChecked) {
			tagView.changeSelectedBackground();
		} else {
			tagView.changeNormalBackground();
		}

		tagView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean) {
				int state = paramAnonymousBoolean ? 1 : 0;
				tourist.setState(state);
				if (TagListView.this.mOnTagCheckedChangedListener != null) {
					TagListView.this.mOnTagCheckedChangedListener.onTagCheckedChanged((TagView) paramAnonymousCompoundButton, tourist, paramAnonymousBoolean);
				}
			}
		});

		tagView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				TagListView.this.mOnTagCheckedChangedListener.onLongClickListener((TagView) v, tourist);
				return true;
			}
		});

		addView(tagView);
	}

	public void addTag(Tourist tourist) {
		planUsers.add(tourist);
		inflateTagView(tourist);
	}

	public void setOnTagCheckedChangedListener(OnTagCheckedChangedListener onTagCheckedChangedListener) {
		mOnTagCheckedChangedListener = onTagCheckedChangedListener;
	}

	public void setTags(List<? extends Tourist> lists) {
		removeAllViews();
		planUsers.clear();
		for (int i = 0; i < lists.size(); i++) {
			addTag((Tourist) lists.get(i));
		}
	}

	public static abstract interface OnTagCheckedChangedListener {
		public abstract void onTagCheckedChanged(TagView tagView, Tourist tourist, boolean isChecked);

		public abstract void onLongClickListener(TagView tagView, Tourist tourist);
	}

}
