package com.kooniao.travel.customwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.kooniao.travel.R;

/**
 * Created by ZZD on 2015/5/27.
 */
public class ProductTabView extends FrameLayout {

    private FrameLayout mProductTab;
    private TextView textView;
    private ImageView imageView;


    public ProductTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mProductTab= (FrameLayout) LayoutInflater.from(context).inflate(R.layout.product_tab, this);
        textView= (TextView) mProductTab.findViewById(R.id.tab);
        imageView= (ImageView) mProductTab.findViewById(R.id.ib_delete);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FlowLayout)(ProductTabView.this.getParent())).removeView(ProductTabView.this);
            }
        });
    }
    public void setText(String text){
        textView.setText(text);
    }
    public String getText(){
        return textView.getText().toString();
    }

}
