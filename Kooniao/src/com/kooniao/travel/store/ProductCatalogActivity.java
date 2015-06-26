package com.kooniao.travel.store;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.kooniao.travel.R;
import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.FlowLayout;
import com.kooniao.travel.model.ProductCatalog;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZZD on 2015/5/28.
 */
@SuppressLint("InflateParams")
@EActivity(R.layout.activity_product_catalog)
public class ProductCatalogActivity extends BaseActivity {

    @ViewById(R.id.bqzs)
    FlowLayout bqzs_layout; //

    private List<Catalog> mCatalogs = new ArrayList<>();
    private List<ProductCatalog> catalogs;
    private int maxsort=0;

    @SuppressWarnings("unchecked")
	@AfterViews
    public void init() {
        Intent intent = getIntent();
           catalogs = (List<ProductCatalog>) intent.getSerializableExtra(Define.DATA);
        showProgressDialog();
        ApiCaller.getInstance().loadProductCatalog(new ApiCaller.APILoadProductCatalogResultCallback() {
            @Override
            public void result(String errMsg, List<Catalog> catalogs) {
                dissmissProgressDialog();
                if (errMsg == null) {
                    mCatalogs = catalogs;
                    initView();
                } else {
                    Toast.makeText(ProductCatalogActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
    

    public void initView() {
        for (Catalog cata : mCatalogs) {
        	if (maxsort<cata.sort) {
        		maxsort=cata.sort;
			}
        	LinearLayout addLayout=(LinearLayout) getLayoutInflater().inflate(R.layout.item_product_catalog, null);
        	CheckBox checkedTextView = (CheckBox) addLayout.getChildAt(0); 
            checkedTextView.setText(cata.title);
            checkedTextView.setTag(cata.cid);
            bqzs_layout.addView(addLayout);
            if (catalogs!=null) {
				for (ProductCatalog pCatalog:catalogs) {
				if (cata.cid==pCatalog.getId()) {
					checkedTextView.setChecked(true);
				}
			}
			}
            
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK){
        	maxsort+=1;
        	LinearLayout addLayout=(LinearLayout) getLayoutInflater().inflate(R.layout.item_product_catalog, null);
        	CheckBox addCheckBox = (CheckBox) addLayout.getChildAt(0); 
            addCheckBox.setText(data.getStringExtra(Define.TITLE));
            addCheckBox.setTag(data.getIntExtra(Define.CID,0));
            addCheckBox.setChecked(true);
            bqzs_layout.addView(addLayout);
        }
    }

    /**
     * 添加标签
     */
    @Click(R.id.tjml)
    void onTjmlClick() {
        Intent intent = new Intent(ProductCatalogActivity.this, ProductAddCatalogActivity_.class);
        intent.putExtra(Define.TAB_SORT, maxsort);
        startActivityForResult(intent, 0);

    }

    /**
     * 后退按钮
     */
    @Click(R.id.iv_go_back)
    void onGoBackClick() {
            Intent intent = new Intent();
            catalogs=new ArrayList<ProductCatalog>();
            for (int i = 0; i < bqzs_layout.getChildCount(); i++) {
            	CheckBox box=(CheckBox) ((LinearLayout) bqzs_layout.getChildAt(i)).getChildAt(0);
            	if (box.isChecked()) {
					ProductCatalog catalog=new ProductCatalog();
            	catalog.setId(Integer.valueOf(box.getTag().toString()));
            	catalog.setTitle(box.getText().toString());
            	catalogs.add(catalog);
				}
			}
            intent.putExtra(Define.DATA, (Serializable)catalogs);
            setResult(RESULT_OK, intent);
        finish();
    }

    public class Catalog {
        int cid;
        String title;
        int sort;
    }
}