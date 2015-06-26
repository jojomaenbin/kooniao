package com.kooniao.travel.customwidget;

import com.kooniao.travel.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChangeNumView extends LinearLayout {

	public ChangeNumView(Context context) {
		this(context, null);
	}

	public ChangeNumView(Context context, AttributeSet attrs) {
		super(context, attrs);
		View contentView = LayoutInflater.from(context).inflate(R.layout.change_num, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(contentView, params);
		initView();
	}

	private LinearLayout addLayout; // 加数
	private LinearLayout subtractLayout; // 减数
	private TextView numTextView; // 显示的数目

	/**
	 * 初始化
	 */
	private void initView() {
		addLayout = (LinearLayout) findViewById(R.id.ll_add);
		subtractLayout = (LinearLayout) findViewById(R.id.ll_subtract);
		numTextView = (TextView) findViewById(R.id.tv_num);
		numTextView.setText(String.valueOf(minimum));  

		subtractLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int num = Integer.parseInt(numTextView.getText().toString());
				if (num > minimum) {
					num--;
					numTextView.setText(String.valueOf(num));
				}
				
				if (listener != null) {
					listener.onNumChange(num);
				}
			}
		});

		addLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int num = Integer.parseInt(numTextView.getText().toString());
				num++;
				numTextView.setText(String.valueOf(num));
				
				if (listener != null) {
					listener.onNumChange(num);
				}
			}
		});
	}

	/**
	 * 获取数目
	 * 
	 * @return
	 */
	public int getTextNum() {
		int textNum = Integer.parseInt(numTextView.getText().toString());
		return textNum;
	}
	
	/**
	 * 设置数目
	 * @param numText
	 */
	public void setTextNum(String numText) {
		numTextView.setText(numText); 
	}
	
	private int minimum = 1;
	
	/**
	 * 设置最小数目
	 * @param num
	 */
	public void setMinimumNum(int minimum) {
		this.minimum = minimum;
	}
	
	public interface OnNumChangeListener {
		void onNumChange(int num);
	}
	
	private OnNumChangeListener listener;
	
	public void setOnNumChangeListener(OnNumChangeListener listener) {
		this.listener = listener;
	}

}
