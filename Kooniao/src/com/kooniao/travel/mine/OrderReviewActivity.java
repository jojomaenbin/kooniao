package com.kooniao.travel.mine;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.TravelManager;
import com.kooniao.travel.manager.TravelManager.StringResultCallback;

/**
 * 订单点评
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_order_review)
public class OrderReviewActivity extends BaseActivity {
	@ViewById(R.id.rl_order_review_one)
	View orderReviewOne;
	@ViewById(R.id.tv_order_review_name)
	TextView orderReviewOneNameTextView;
	@ViewById(R.id.rb_order_review_one)
	RatingBar orderReviewOneRatingBar;
	@ViewById(R.id.tv_order_review_one_tips)
	TextView orderReviewOneRatingTipsTextView;

	@ViewById(R.id.rl_order_review_two)
	View orderReviewTwo;
	@ViewById(R.id.rb_order_review_two)
	RatingBar orderReviewTwoRatingBar;
	@ViewById(R.id.tv_order_review_two_tips)
	TextView orderReviewTwoRatingTipsTextView;

	@ViewById(R.id.rl_order_review_three)
	View orderReviewThree;
	@ViewById(R.id.rb_order_review_three)
	RatingBar orderReviewThreeRatingBar;
	@ViewById(R.id.tv_order_review_three_tips)
	TextView orderReviewThreeRatingTipsTextView;

	@ViewById(R.id.et_order_review_content)
	EditText inpuEditText;
	@ViewById(R.id.tv_order_review_content_count)
	TextView contentCounTextView; // 字数剩余统计

	@AfterViews
	void init() {
		initData();
		initView();
	}

	private int productType = 4; // 默认类型：线路产品
	private int orderId; // 订单id

	/**
	 * 初始化数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			productType = intent.getIntExtra(Define.PRODUCT_TYPE, 4);
			orderId = intent.getIntExtra(Define.ORDER_ID, 0);
		}
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		if (productType != 4) {
			orderReviewOneNameTextView.setText("评价");
			orderReviewTwo.setVisibility(View.GONE);
			orderReviewThree.setVisibility(View.GONE);
		}

		// 监听行程线路评分变化
		orderReviewOneRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				if (rating == 1) {
					orderReviewOneRatingTipsTextView.setText(R.string.very_yawp); // 十分不满意
				} else if (rating == 2) {
					orderReviewOneRatingTipsTextView.setText(R.string.yawp); // 不满意
				} else if (rating == 3) {
					orderReviewOneRatingTipsTextView.setText(R.string.gernal_satisfaction); // 一般
				} else if (rating == 4) {
					orderReviewOneRatingTipsTextView.setText(R.string.satisfaction); // 满意
				} else if (rating == 5) {
					orderReviewOneRatingTipsTextView.setText(R.string.very_satisfaction); // 十分满意
				}
			}
		});
		
		// 监听行程服务评分变化
		orderReviewTwoRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				if (rating == 1) {
					orderReviewTwoRatingTipsTextView.setText(R.string.very_yawp); // 十分不满意
				} else if (rating == 2) {
					orderReviewTwoRatingTipsTextView.setText(R.string.yawp); // 不满意
				} else if (rating == 3) {
					orderReviewTwoRatingTipsTextView.setText(R.string.gernal_satisfaction); // 一般
				} else if (rating == 4) {
					orderReviewTwoRatingTipsTextView.setText(R.string.satisfaction); // 满意
				} else if (rating == 5) {
					orderReviewTwoRatingTipsTextView.setText(R.string.very_satisfaction); // 十分满意
				}
			}
		});
		
		// 监听行程餐饮评分变化
		orderReviewThreeRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				if (rating == 1) {
					orderReviewThreeRatingTipsTextView.setText(R.string.very_yawp); // 十分不满意
				} else if (rating == 2) {
					orderReviewThreeRatingTipsTextView.setText(R.string.yawp); // 不满意
				} else if (rating == 3) {
					orderReviewThreeRatingTipsTextView.setText(R.string.gernal_satisfaction); // 一般
				} else if (rating == 4) {
					orderReviewThreeRatingTipsTextView.setText(R.string.satisfaction); // 满意
				} else if (rating == 5) {
					orderReviewThreeRatingTipsTextView.setText(R.string.very_satisfaction); // 十分满意
				}
			}
		});
	}
	
	@TextChange(R.id.et_order_review_content)
	void onTextChangesOnStoreNameInput(CharSequence text, TextView textView, int before, int start, int count) {
		contentCounTextView.setText(String.valueOf(400 - text.length()));  
	}

	/**
	 * 点击后退
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 提交点评
	 */
	@Click(R.id.bt_order_review_commit)
	void onCommitClick() {
		commitOrderReview();
	}

	/**
	 * 提交点评
	 */
	private void commitOrderReview() {
		int mark1 = (int) orderReviewOneRatingBar.getRating();
		int mark2 = 0;
		int mark3 = 0;
		if (orderReviewTwo.getVisibility() == View.VISIBLE) {
			mark2 = (int) orderReviewTwoRatingBar.getRating();
		}
		if (orderReviewThree.getVisibility() == View.VISIBLE) {
			mark3 = (int) orderReviewThreeRatingBar.getRating();
		}
		String content = inpuEditText.getText().toString();
		TravelManager.getInstance().commitOrderReview(orderId, mark1, mark2, mark3, content, new StringResultCallback() {

			@Override
			public void result(String errMsg) {
				if (errMsg == null) {
					finish();
				} else {
					Toast.makeText(OrderReviewActivity.this, errMsg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

}
