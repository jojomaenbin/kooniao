package com.kooniao.travel.store;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.utils.ViewUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZZD on 2015/6/1.
 */
@EActivity(R.layout.activity_category)
public class ProductStarLevelActivity extends BaseActivity {


    @ViewById(R.id.title)
    TextView titleTextView; //
    @ViewById(R.id.ll_participant_info)
    LinearLayout noDataLayout; //

    @AfterViews
    void init() {
        initData();
        Intent intent=getIntent();
        if (intent.getStringExtra(Define.DATA).contains("酒店"))
        loadCategoryDetail("hotel");
        if (intent.getStringExtra(Define.DATA).contains("景点"))
            loadCategoryDetail("location");
    }

    public void initData() {
        titleTextView.setText("星级");
    }

    public void initView() {
        int padding= ViewUtils.dip2px(ProductStarLevelActivity.this, 10);
        for (starlevel sl:mStarlevels) {
            TextView textView=new TextView(ProductStarLevelActivity.this);
            textView.setText(sl.name);
            textView.setTag(sl.id);
            textView.setPadding(padding, padding, padding, padding);
            textView.setTextSize(20);
            textView.setBackgroundResource(R.drawable.list_item_selector);
            noDataLayout.addView(textView);
            View line=new View(ProductStarLevelActivity.this);
            line.setBackgroundResource(R.color.divider_line_bg);
            noDataLayout.addView(line, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent data = new Intent(ProductStarLevelActivity.this, ProductInfoActivity_.class);
                    int id=(int) v.getTag();
                    data.putExtra(Define.ID,id);
                    data.putExtra(Define.DATA,((TextView) v).getText().toString());
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
        }
    }

    private List<starlevel> mStarlevels;

    /**
     * 加载星级详情
     */
    private void loadCategoryDetail(String type) {
        ApiCaller.getInstance().loadStarLevelDetail(type, new ApiCaller.APIStarLevelResultCallback() {

            @Override
            public void result(String errMsg, starlist starlevels) {
                if (errMsg == null && starlevels != null) {
                    mStarlevels = starlevels.gradeList;
                   initView();
                } else {
                    Toast.makeText(ProductStarLevelActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * 后退按钮
     */
    @Click(R.id.iv_go_back)
    void onGoBackClick() {
        finish();
    }

    public class starlist {
    	public List<starlevel> gradeList;
		
	}
    public class starlevel implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4760138625140760159L;
		public int id;
        public String name;
    }
}

