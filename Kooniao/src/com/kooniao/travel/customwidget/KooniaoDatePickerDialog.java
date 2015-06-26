package com.kooniao.travel.customwidget;

import android.app.DatePickerDialog;
import android.content.Context;

/**
 * 日期选择对话框
 * 
 * @author ke.wei.quan
 * 
 */
public class KooniaoDatePickerDialog extends DatePickerDialog {

	public KooniaoDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
	}

	public KooniaoDatePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
		super(context, theme, callBack, year, monthOfYear, dayOfMonth);
	}
	
	@Override
	protected void onStop() {
//		super.onStop();
	}

}
