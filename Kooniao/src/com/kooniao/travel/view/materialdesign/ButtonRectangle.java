package com.kooniao.travel.view.materialdesign;

import com.kooniao.travel.R;
import com.kooniao.travel.utils.ViewUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ButtonRectangle extends Button {

	TextView textButton;

	int paddingTop, paddingBottom, paddingLeft, paddingRight;

	public ButtonRectangle(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDefaultProperties();
	}

	@Override
	protected void setDefaultProperties() {
		super.minWidth = 80;
		super.minHeight = 36;
		super.background = R.drawable.background_button_rectangle;
		super.setDefaultProperties();
		rippleSpeed = ViewUtils.dpToPx(6, getResources());
	}

	// Set atributtes of XML to View
	protected void setAttributes(AttributeSet attrs) {
		// Set background Color
		// Color by resource
		int bacgroundColor = attrs.getAttributeResourceValue(ANDROIDXML, "background", -1);
		if (bacgroundColor != -1) {
			setBackgroundColor(getResources().getColor(bacgroundColor));
		} else {
			// Color by hexadecimal
			String background = attrs.getAttributeValue(ANDROIDXML, "background");
			if (background != null)
				setBackgroundColor(Color.parseColor(background));
		}

		// Set text button
		String text = null;
		int textResource = attrs.getAttributeResourceValue(ANDROIDXML, "text", -1);
		if (textResource != -1) {
			text = getResources().getString(textResource);
		} else {
			text = attrs.getAttributeValue(ANDROIDXML, "text");
		}
		if (text != null) {
			textButton = new TextView(getContext());
			textButton.setText(text);
			textButton.setTextColor(Color.WHITE);
			textButton.setTypeface(null, Typeface.BOLD);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			params.setMargins(ViewUtils.dpToPx(5, getResources()), ViewUtils.dpToPx(5, getResources()), ViewUtils.dpToPx(5, getResources()), ViewUtils.dpToPx(5, getResources()));
			textButton.setLayoutParams(params);
			addView(textButton);
		}
	}

	Integer height;
	Integer width;

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (x != -1) {
			Rect src = new Rect(0, 0, getWidth() - ViewUtils.dpToPx(6, getResources()), getHeight() - ViewUtils.dpToPx(7, getResources()));
			Rect dst = new Rect(ViewUtils.dpToPx(6, getResources()), ViewUtils.dpToPx(6, getResources()), getWidth() - ViewUtils.dpToPx(6, getResources()), getHeight() - ViewUtils.dpToPx(7, getResources()));
			canvas.drawBitmap(makeCircle(), src, dst, null);
		}
		invalidate();
	}

	public void setText(String text) {
		textButton.setText(text);
	}

	public void setTextColor(int color) {
		textButton.setTextColor(color);
	}

	@Override
	public TextView getTextView() {
		return textButton;
	}

}
