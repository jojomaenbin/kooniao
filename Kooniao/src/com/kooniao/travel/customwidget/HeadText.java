package com.kooniao.travel.customwidget;

import com.kooniao.travel.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HeadText extends LinearLayout implements View.OnClickListener {

	public HeadText(Context paramContext) {
		this(paramContext, null);
	}

	public HeadText(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		addView(LayoutInflater.from(paramContext).inflate(R.layout.widget_head_text, null), new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		initWiget();
	}

	private TextView headTextView; // 标题
	private TextView singleContentTextView; // 单文本内容
	private LinearLayout mutilContentLayout; // 多文本内容
	private LinearLayout arrowLayout; // 箭头布局
	private ImageView arrowImageView; // 箭头

	/**
	 * 初始化控件
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void initWiget() {
		this.headTextView = (TextView) findViewById(R.id.tv_head_name);
		this.singleContentTextView = (TextView) findViewById(R.id.tv_detail_single_content);
		this.mutilContentLayout = (LinearLayout) findViewById(R.id.ll_detail_mutil_content);
		this.arrowLayout = (LinearLayout) findViewById(R.id.ll_arrow);
		this.arrowImageView = (ImageView) findViewById(R.id.iv_arrow);
	}

	/**
	 * 设置内容标题
	 * 
	 * @param paramString
	 */
	public void setTitle(String paramString) {
		this.headTextView.setText(paramString);
	}

	/**
	 * 设置单文本内容
	 * 
	 * @param paramString
	 */
	public void setSingleContent(String paramString) {
		if (paramString == null) {
			setVisibility(View.GONE);
			return;
		}
		if ((paramString.trim().equals("")) || (paramString.trim().equals("0"))) {
			setVisibility(View.GONE);
			return;
		}

		paramString = paramString.replaceAll("<p>", "");
		paramString = paramString.replaceAll("</p>", "<br><br>");
		final String addedPatternChar = "#_#"; // 作为识别字符串
		paramString = paramString + addedPatternChar;
		final String patternChar = "<br><br>" + addedPatternChar;
		int lastIndex = paramString.lastIndexOf(patternChar);
		while (lastIndex != -1) {
			paramString = paramString.substring(0, lastIndex) + addedPatternChar;
			lastIndex = paramString.lastIndexOf(patternChar);
		}
		paramString = paramString.substring(0, paramString.length() - addedPatternChar.length());
		singleContentTextView.setText(Html.fromHtml(paramString));
		mutilContentLayout.setVisibility(View.GONE);
		hideMoreLayout();
	}

	final int defaultMaxLineCount = 5; // 默认最大行数
	final int maxLineCount = Integer.MAX_VALUE; // 最大行数
	int lineCount;
	boolean hasMessure = false; // 测量singleContentTextview，只测量一次

	/**
	 * 隐藏5行以后的文本布局
	 */
	@SuppressLint("NewApi")
	private void hideMoreLayout() {
		singleContentTextView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

			@Override
			public boolean onPreDraw() {
				if (!hasMessure) {
					hasMessure = true;
					lineCount = singleContentTextView.getLineCount();
					if (lineCount > defaultMaxLineCount) {
						singleContentTextView.setMaxLines(defaultMaxLineCount);
						singleContentTextView.setEllipsize(TruncateAt.END);
						arrowLayout.setVisibility(VISIBLE);
						arrowLayout.setOnClickListener(HeadText.this);
						singleContentTextView.setOnClickListener(HeadText.this);
					} else {
						arrowLayout.setVisibility(GONE);
					}
				}
				return true;
			}
		});
	}

	/**
	 * 设置多文本内容
	 * 
	 * @param index
	 * @param subTitle
	 * @param subContent
	 */
	public void setMutilContent(int index, String subTitle, String subContent) {
		TextView localTextView = new TextView(getContext());
		localTextView.setTextColor(getResources().getColor(R.color.v909090));
		localTextView.setTextSize(19.0F);
		localTextView.setText("【" + subTitle + "】" + " " + subContent);
		LinearLayout.LayoutParams layoutParams;
		if ((subContent.trim().equals("")) || (subContent.trim().equals("0"))) {
			layoutParams = new LinearLayout.LayoutParams(0, 0);
		} else {
			layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			layoutParams.bottomMargin = 15;
		}

		mutilContentLayout.addView(localTextView, index, layoutParams);
		mutilContentLayout.setVisibility(View.VISIBLE);
		singleContentTextView.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_arrow: // 箭头布局
			hideOrShowMoreLayout();
			break;

		case R.id.tv_detail_single_content: // 单文本内容布局
			hideOrShowMoreLayout();
			break;

		default:
			break;
		}
	}

	boolean showMoreLayoutFlag = true; // 展开剩下的布局标志

	/**
	 * 展开或隐藏多余的布局
	 */
	private void hideOrShowMoreLayout() {
		if (showMoreLayoutFlag) {
			showMoreLayoutFlag = false;
			singleContentTextView.setEllipsize(null); // 展开
			singleContentTextView.setMaxLines(maxLineCount);
			arrowImageView.setImageResource(R.drawable.arrow_up_detail);

		} else {
			showMoreLayoutFlag = true;
			singleContentTextView.setEllipsize(TextUtils.TruncateAt.END); // 收缩
			singleContentTextView.setMaxLines(defaultMaxLineCount);
			arrowImageView.setImageResource(R.drawable.arrow_down_detail);

		}
	}

}
