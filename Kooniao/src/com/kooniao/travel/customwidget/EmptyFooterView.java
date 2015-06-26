package com.kooniao.travel.customwidget;

import com.kooniao.travel.R;
import com.kooniao.travel.constant.Define;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class EmptyFooterView extends LinearLayout {

	public EmptyFooterView(Context context) {
		super(context);
		addView(LayoutInflater.from(context).inflate(R.layout.footer_empty_view, null), new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)(60 * Define.DENSITY)));
	}

}
