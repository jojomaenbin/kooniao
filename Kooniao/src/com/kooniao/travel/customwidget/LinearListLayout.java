package com.kooniao.travel.customwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;

public class LinearListLayout extends LinearLayout {

	public LinearListLayout(Context context) {
		this(context, null);
	}

	public LinearListLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.VERTICAL); // 设置垂直布局
	}

	/**
	 * 添加footerview
	 * 
	 * @param footerView
	 */
	public void addFooterView(View footerView) {
		addView(footerView);
	}

	/**
	 * 更新布局
	 */
	public void notifyLayoutChange(int startIndex) {
		bindBaseListLayout(startIndex);
	}

	private BaseAdapter baseAdapter;

	/**
	 * 绑定baseAdapter
	 * 
	 * @param baseAdapter
	 */
	public void setBaseAdapter(BaseAdapter baseAdapter) {
		this.baseAdapter = baseAdapter;
		bindBaseListLayout(0);
	}

	/**
	 * 绑定baseAdapter布局
	 */
	private void bindBaseListLayout(int startIndex) {
		if (startIndex == 0) {
			removeAllViews();
		}
		int count = baseAdapter.getCount();
		for (int i = startIndex; i < count; i++) {
			View itemView = baseAdapter.getView(i, null, null);
			addView(itemView, i);
		}
	}

	/**
	 * 获取baseAdapter
	 * 
	 * @return
	 */
	public BaseAdapter getBaseAdapter() {
		return baseAdapter;
	}

	private BaseExpandableListAdapter expandableAdapter;

	/**
	 * 绑定层级展开的adapter
	 * 
	 * @param expandableAdapter
	 */
	public void setExpandadleAdapter(BaseExpandableListAdapter expandableAdapter) {
		this.expandableAdapter = expandableAdapter;
		bindExpandableListLayout();
	}

	/**
	 * 获取层级展开的adapter
	 * 
	 * @return
	 */
	public BaseExpandableListAdapter getExpandableAdapter() {
		return expandableAdapter;
	}

	/**
	 * 绑定层级展开的布局
	 */
	private void bindExpandableListLayout() {
		int index = 0;
		removeAllViews();
		int groupCount = expandableAdapter.getGroupCount();
		for (int i = 0; i < groupCount; i++) {
			View groupView = expandableAdapter.getGroupView(i, true, null, null);
			addView(groupView, index);
			index++;
			int childCount = expandableAdapter.getChildrenCount(i);
			for (int j = 0; j < childCount; j++) {
				View childView = expandableAdapter.getChildView(i, j, false, null, null);
				addView(childView, index);
				index++;
			}
		}
	}

	public interface OnChildItemClickListener {
		void onChildItemClickListener(View groupView, View childView, int groupPosition, int childPosition);
	}

	private OnChildItemClickListener onChildItemClickListener;

	/**
	 * 设置条目监听对象
	 * 
	 * @param onChildItemClickListener
	 */
	public void setOnChildItemClickListener(OnChildItemClickListener onChildItemClickListener, boolean isExpandable) {
		this.onChildItemClickListener = onChildItemClickListener;
		setChildItemClickListener(isExpandable);
	}

	/**
	 * 获取条目的监听对象
	 * 
	 * @return
	 */
	public OnChildItemClickListener getChildItemClickListener() {
		return onChildItemClickListener;
	}

	/**
	 * 设置条目的点击事件
	 */
	private void setChildItemClickListener(boolean isExpandable) {
		if (isExpandable) {
			int index = 0;
			int groupCount = expandableAdapter.getGroupCount();
			for (int i = 0; i < groupCount; i++) {
				View groupView = getChildAt(index);
				index++;
				int childCount = expandableAdapter.getChildrenCount(i);
				for (int j = 0; j < childCount; j++) {
					View childView = getChildAt(index);
					if (onChildItemClickListener != null) {
						childView.setOnClickListener(new ChildItemClickListener(groupView, childView, i, j));
					}
					index++;
				}
			}
		} else {
			int count = baseAdapter.getCount();
			for (int i = 0; i < count; i++) {
				View itemView = getChildAt(i);
				if (onChildItemClickListener != null) {
					itemView.setOnClickListener(new ChildItemClickListener(itemView, i));
				}
			}
		}
	}

	class ChildItemClickListener implements OnClickListener {
		int groupPosition;
		int childPosition;
		View groupView;
		View childView;

		public ChildItemClickListener(View groupView, int groupPosition) {
			this.groupPosition = groupPosition;
			this.groupView = groupView;
		}

		public ChildItemClickListener(View groupView, View childView, int groupPosition, int childPosition) {
			this.groupPosition = groupPosition;
			this.childPosition = childPosition;
			this.groupView = groupView;
			this.childView = childView;
		}

		@Override
		public void onClick(View v) {
			if (onChildItemClickListener != null) {
				onChildItemClickListener.onChildItemClickListener(groupView, childView, groupPosition, childPosition);
			}
		}

	}

}
