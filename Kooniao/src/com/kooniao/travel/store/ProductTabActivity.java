package com.kooniao.travel.store;

import android.content.Intent;
import android.widget.EditText;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.FlowLayout;
import com.kooniao.travel.customwidget.ProductTabView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZZD on 2015/5/27.
 */
@EActivity(R.layout.activity_product_tab)
public class ProductTabActivity extends BaseActivity {

    @ViewById(R.id.bqzs)
    FlowLayout bqzs_layout; //
    @ViewById(R.id.srbq)
    EditText srbqEidt; //

    private List<String> tabs=new ArrayList<>();

    @SuppressWarnings("unchecked")
	@AfterViews
    public void init(){
        Intent intent= getIntent();
        if (intent.getSerializableExtra(Define.DATA)!=null){
            tabs= (List<String>) intent.getSerializableExtra(Define.DATA);
            for (String s:tabs){
                ProductTabView productTabView=new ProductTabView(ProductTabActivity.this,null);
                productTabView.setText(s);
                bqzs_layout.addView(productTabView);
            }
        }
    }

    /**
     * 添加标签
     */
    @Click(R.id.tjbq)
    void onTjbqClick() {
        String tab=srbqEidt.getText().toString();
        String tabcheck=tab.replace(" ","");
        if (tab==null||tabcheck.equals(""))
            return;
        for (String s:tabs){
            if (tab.equals(s)){
                return;
            }
        }
        tabs.add(tab);
        ProductTabView productTabView=new ProductTabView(ProductTabActivity.this,null);
        productTabView.setText(tab);
        bqzs_layout.addView(productTabView);
        srbqEidt.setText("");
    }

    /**
     * 后退按钮
     */
    @Click(R.id.iv_go_back)
    void onGoBackClick() {
        tabs=new ArrayList<>();
        if (bqzs_layout.getChildCount()>0){
            for (int i=0;i<bqzs_layout.getChildCount();i++){
                tabs.add(((ProductTabView) bqzs_layout.getChildAt(i)).getText());
            }
        }
        Intent intent=new Intent();
        intent.putExtra(Define.DATA, (Serializable) tabs);
        setResult(RESULT_OK,intent);
        finish();
    }
}
