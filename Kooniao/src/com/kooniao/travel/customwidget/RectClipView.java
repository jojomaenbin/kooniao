package com.kooniao.travel.customwidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 矩形裁剪区域(16:9)
 * @author ke.wei.quan
 *
 */
public class RectClipView extends View {
	public RectClipView(Context context) {
		super(context);
	}

	public RectClipView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RectClipView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int width = this.getWidth();
		int height = this.getHeight();
		int topY = (height - width * 9 / 16) / 2;
		int bottomY = height -  topY;

		Paint paint = new Paint();
		paint.setColor(0xDD000000);
		canvas.drawRect(0, 0, width, topY, paint);  
		canvas.drawRect(0, bottomY, width, height, paint);
	}

}
