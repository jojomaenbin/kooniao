package com.kooniao.travel.view.materialdesign;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * Created by ZZD on 2015/5/24.
 */
public class ProductRadioButton extends RadioButton {
    public ProductRadioButton(Context context) {
        super(context);
    }

    public ProductRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProductRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
