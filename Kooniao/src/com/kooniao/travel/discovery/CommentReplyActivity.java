package com.kooniao.travel.discovery;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.manager.TravelManager;

/**
 * 回复评论
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_travel_comment_reply)
public class CommentReplyActivity extends BaseActivity {
	@ViewById(R.id.et_input)
	EditText inputEditText; // 内容输入

	@AfterViews
	void init() {
		initData();
		initView();
	}

	private int id; // 评论目标id
	private int pid; // 评论父id
	private String module; // 类型
	private String userName; // 评论者名字

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			id = intent.getIntExtra(Define.ID, 0);
			pid = intent.getIntExtra(Define.PID, 0);
			module = intent.getStringExtra(Define.MODULE);
			userName = "@" + intent.getStringExtra(Define.USER_NAME);
		}
	}

	KooniaoProgressDialog progressDialog;

	/**
	 * 初始化界面
	 */
	private void initView() {
		progressDialog = new KooniaoProgressDialog(CommentReplyActivity.this);
		// 初始化输入框
		initInputEditText();
	}

	/**
	 * 初始化输入框
	 */
	private void initInputEditText() {
		SpannableString spannableString = new SpannableString(userName);
		spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.v16b8eb)), 0, userName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		inputEditText.setText(spannableString);
		inputEditText.setSelection(userName.length());
	}

	/**
	 * 返回按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 提交回复评论
	 */
	@Click(R.id.tv_reply_submit)
	void onSubmitClick() {
		commitComment(); 
	}

	/**
	 * 提交评论
	 */
	private void commitComment() {
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
		closeInputKeyboard();
		String content = inputEditText.getText().toString().trim();
		float rank = 5.0f;
		TravelManager.getInstance().replyComment(id, content, rank, pid, module, new TravelManager.StringResultCallback() {

			@Override
			public void result(String errMsg) {
				if (errMsg == null) {
					// 清空输入框
					initInputEditText();
					setResult(RESULT_OK);
					finish();
					Toast.makeText(getBaseContext(), R.string.commit_comment_success, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getBaseContext(), errMsg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 关闭输入键盘
	 */
	private void closeInputKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	}

	/**
	 * 清除回复内容
	 */
	@Click(R.id.iv_clear)
	void onClearClick() {
		initInputEditText();
	}

}
