package com.kooniao.travel.discovery;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
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
 * 行程反馈页
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_travel_feedback)
public class TravelFeedbackActivity extends BaseActivity {
	@ViewById(R.id.rb_travel_feedback_route)
	RatingBar feedBackRouteRatingBar; // 行程线路评分条
	@ViewById(R.id.tv_travel_feedback_route_tips)
	TextView feedBackRouteTipsTextView; // 行程线路评分提示

	@ViewById(R.id.rb_travel_feedback_service)
	RatingBar feedBackServiceRatingBar; // 行程线路评分条
	@ViewById(R.id.tv_travel_feedback_service_tips)
	TextView feedBackServiceTipsTextView; // 行程服务评分提示

	@ViewById(R.id.rb_travel_feedback_food)
	RatingBar feedBackFoodRatingBar; // 行程餐饮评分条
	@ViewById(R.id.tv_travel_feedback_food_tips)
	TextView feedBackFoodTipsTextView; // 行程餐饮评分提示

	@ViewById(R.id.et_travel_feedback_content)
	EditText inputEditText; // 输入框
	@ViewById(R.id.tv_travel_feedback_content_count)
	TextView contentCounTextView; // 输入内容字数统计

	@AfterViews
	void init() {
		initData();
		initView();
	}
	
	private int travelId; // 行程id
	
	/**
	 * 初始化界面数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			travelId = intent.getIntExtra(Define.PID, 0);
		}
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 监听行程线路评分变化
		feedBackRouteRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				if (rating == 1) {
					feedBackRouteTipsTextView.setText(R.string.very_yawp); // 十分不满意
				} else if (rating == 2) {
					feedBackRouteTipsTextView.setText(R.string.yawp); // 不满意
				} else if (rating == 3) {
					feedBackRouteTipsTextView.setText(R.string.gernal_satisfaction); // 一般
				} else if (rating == 4) {
					feedBackRouteTipsTextView.setText(R.string.satisfaction); // 满意
				} else if (rating == 5) {
					feedBackRouteTipsTextView.setText(R.string.very_satisfaction); // 十分满意
				}
			}
		});
		// 监听行程服务评分变化
		feedBackServiceRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				if (rating == 1) {
					feedBackServiceTipsTextView.setText(R.string.very_yawp); // 十分不满意
				} else if (rating == 2) {
					feedBackServiceTipsTextView.setText(R.string.yawp); // 不满意
				} else if (rating == 3) {
					feedBackServiceTipsTextView.setText(R.string.gernal_satisfaction); // 一般
				} else if (rating == 4) {
					feedBackServiceTipsTextView.setText(R.string.satisfaction); // 满意
				} else if (rating == 5) {
					feedBackServiceTipsTextView.setText(R.string.very_satisfaction); // 十分满意
				}
			}
		});
		// 监听行程餐饮评分变化
		feedBackFoodRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				if (rating == 1) {
					feedBackFoodTipsTextView.setText(R.string.very_yawp); // 十分不满意
				} else if (rating == 2) {
					feedBackFoodTipsTextView.setText(R.string.yawp); // 不满意
				} else if (rating == 3) {
					feedBackFoodTipsTextView.setText(R.string.gernal_satisfaction); // 一般
				} else if (rating == 4) {
					feedBackFoodTipsTextView.setText(R.string.satisfaction); // 满意
				} else if (rating == 5) {
					feedBackFoodTipsTextView.setText(R.string.very_satisfaction); // 十分满意
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

	/**
	 * 发表按钮
	 */
	@Click(R.id.bt_travel_feedback_commit)
	void onCommitClick() {
		String feedBackContent = inputEditText.getText().toString();
		if (feedBackContent.trim().equals("")) {
			Toast.makeText(TravelFeedbackActivity.this, R.string.feed_back_empty, Toast.LENGTH_SHORT).show();
		} else {
			float lineScore = feedBackRouteRatingBar.getRating();
			float serviceScore = feedBackServiceRatingBar.getRating();
			float repastScore = feedBackFoodRatingBar.getRating();
			String content = inputEditText.getText().toString();
			TravelManager.getInstance().commitTravelFeedback(travelId, lineScore, serviceScore, repastScore, content, new StringResultCallback() {
				
				@Override
				public void result(String errMsg) {
					if (errMsg == null) {
						finish();
					} else {
						Toast.makeText(TravelFeedbackActivity.this, errMsg, Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}

}
