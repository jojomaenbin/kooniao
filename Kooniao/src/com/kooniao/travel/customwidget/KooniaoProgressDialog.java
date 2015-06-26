package com.kooniao.travel.customwidget;

import com.kooniao.travel.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class KooniaoProgressDialog extends Dialog {

	private boolean cancelable = true;
	private TextView messageTextView;
	private ImageView tipIcon;
	RotateAnimation rotateAnimation;

	public KooniaoProgressDialog(Context context) {
		this(context, R.string.loading);
	}

	public KooniaoProgressDialog(Context context, int msgKey) {
		this(context, msgKey, R.style.ProgressDialogTheme);
	}

	public KooniaoProgressDialog(Context context, int msgKey, int theme) {
		super(context, theme);
		this.setContentView(R.layout.progress_dialog_view);
		rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setInterpolator(new LinearInterpolator());
		rotateAnimation.setDuration(1000);
		rotateAnimation.setRepeatCount(-1);  
		rotateAnimation.startNow(); 
		messageTextView = ((TextView) findViewById(R.id.id_tv_loadingmsg));
		tipIcon = (ImageView) findViewById(R.id.iv_tip_icon);
		tipIcon.setAnimation(rotateAnimation); 
		messageTextView.setText(context.getResources().getString(msgKey));
		this.getWindow().getAttributes().gravity = Gravity.CENTER;
		this.getWindow().getAttributes().dimAmount = 0.5f;
		this.setCancelable(cancelable);
	}

	/**
	 * 设置加载提示文字
	 * @param resId
	 */
	public void setMessage(int resId) {
		messageTextView.setText(resId);
	}

	/**
	 * 设置加载提示文字
	 * @param messageString
	 */
	public void setMessage(String messageString) {
		messageTextView.setText(messageString);
	}

	public void setTipIcon(int iconId) {
		tipIcon.setImageResource(iconId);
	}
	
}
