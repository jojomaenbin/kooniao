package com.kooniao.travel.customwidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

/**
 * 圆形裁剪
 * @author ke.wei.quan
 *
 */
public class RoundClipView extends View {
	public RoundClipView(Context context) {
		super(context);
	}

	public RoundClipView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundClipView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	private Paint paint;
	private Bitmap mBitmap;

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = getWidth();
		int height = getHeight();
		if (paint == null) {
			paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		}

		mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
		Canvas canvasOverlay = new Canvas(mBitmap);
		canvasOverlay.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		canvasOverlay.drawColor(Color.parseColor("#BB000000"));

		paint.setColor(Color.BLACK);
		paint.setXfermode(new PorterDuffXfermode(Mode.XOR));
		canvasOverlay.drawCircle(width / 2, height / 2, height / 4, paint);
		canvas.drawBitmap(mBitmap, 0, 0, paint);
	}

}
