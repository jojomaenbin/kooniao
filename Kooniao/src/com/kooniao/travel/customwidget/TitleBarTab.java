package com.kooniao.travel.customwidget;

import com.kooniao.travel.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TitleBarTab extends LinearLayout implements View.OnClickListener {
	
	public TitleBarTab(Context context) {
		this(context, null);
	}

	public TitleBarTab(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 初始化
	 */
	private void init(Context context) {
		View titleBarTabView = LayoutInflater.from(context).inflate(R.layout.title_tab, null);
		addView(titleBarTabView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));   
		initWidget();
	}
	
	private LinearLayout tabOne; // 第一个选项
	private LinearLayout tabTwo; // 第二个选项
	private LinearLayout tabThree; // 第三个选项
	
	private TextView tabTextOne; // 第一个选项内容
	private TextView tabTextTwo; // 第二个选项内容
	private TextView tabTextThree; // 第三个选项内容
	
	private View bottomLineOne; // 第一个底部蓝条
	private View bottomLineTwo; // 第二个底部蓝条
	private View bottomLineThree; // 第三个底部蓝条
	
	/**
	 * 初始化界面控件
	 */
	private void initWidget() {
		tabOne = (LinearLayout) findViewById(R.id.ll_title_bar_tab_one);
		tabOne.setOnClickListener(this);
		tabTwo = (LinearLayout) findViewById(R.id.ll_title_bar_tab_two);
		tabTwo.setOnClickListener(this);
		tabThree = (LinearLayout) findViewById(R.id.ll_title_bar_tab_three);
		tabThree.setOnClickListener(this);
		
		tabTextOne = (TextView) findViewById(R.id.tv_title_bar_tab_one);
		tabTextTwo = (TextView) findViewById(R.id.tv_title_bar_tab_two);
		tabTextThree = (TextView) findViewById(R.id.tv_title_bar_tab_three);
		textColorControl(R.color.v16b8eb, R.color.v020202, R.color.v020202);
		
		bottomLineOne = findViewById(R.id.divider_line_one);
		bottomLineTwo = findViewById(R.id.divider_line_two);
		bottomLineThree = findViewById(R.id.divider_line_three);
	}
	
	/**
	 * 设置第一个选项是否显示
	 * @param visibility
	 */
	public void setTabOneVisibility(int visibility) {
		tabOne.setVisibility(visibility);
	}
	
	/**
	 * 设置第二个选项是否显示
	 * @param visibility
	 */
	public void setTabTwoVisibility(int visibility) {
		tabTwo.setVisibility(visibility);
	}
	
	/**
	 * 设置第三个选项是否显示
	 * @param visibility
	 */
	public void setTabThreeVisibility(int visibility) {
		tabThree.setVisibility(visibility);
	}
	
	/**
	 * 第一个tab显示内容
	 * @param oneTabText
	 */
	public void setTabOneText(int oneTabText) {
		tabTextOne.setText(oneTabText); 
	}
	
	/**
	 * 第二个tab显示内容
	 * @param oneTabText
	 */
	public void setTabTwoText(int twoTabText) {
		tabTextTwo.setText(twoTabText); 
	}
	
	/**
	 * 第三个tab显示内容
	 * @param oneTabText
	 */
	public void setTabThreeText(int threeTabText) {
		tabTextThree.setText(threeTabText); 
	}
	
	public interface onTabClickListener {
		void onClick(int tabIndex);
	}
	
	private onTabClickListener tabClickListener;
	
	public void setOnTabClickListener(onTabClickListener tabClickListener) {
		this.tabClickListener = tabClickListener;
	}

	@Override
	public void onClick(View v) {
		if (tabClickListener != null) {
			switch (v.getId()) {
			case R.id.ll_title_bar_tab_one:
				tabClickListener.onClick(0);
				bottomLineControl(VISIBLE, INVISIBLE, INVISIBLE);
				textColorControl(R.color.v16b8eb, R.color.v020202, R.color.v020202);
				break;
				
			case R.id.ll_title_bar_tab_two:
				tabClickListener.onClick(1);
				bottomLineControl(INVISIBLE, VISIBLE, INVISIBLE);
				textColorControl(R.color.v020202, R.color.v16b8eb, R.color.v020202);
				break;
				
			case R.id.ll_title_bar_tab_three:
				tabClickListener.onClick(2);
				bottomLineControl(INVISIBLE, INVISIBLE, VISIBLE);
				textColorControl(R.color.v020202, R.color.v020202, R.color.v16b8eb);
				break;
				
			default:
				break;
			}
		}
	}
	
	/**
	 * 设置底部线条显示与否
	 * @param tabOneVisibility
	 * @param tabTwoVisibility
	 * @param tabThreeVisibility
	 */
	private void bottomLineControl(int tabOneVisibility, int tabTwoVisibility, int tabThreeVisibility) {
		bottomLineOne.setVisibility(tabOneVisibility);
		bottomLineTwo.setVisibility(tabTwoVisibility);
		bottomLineThree.setVisibility(tabThreeVisibility);
	}
	
	/**
	 * 按钮颜色改变
	 * @param tabOneTextColor
	 * @param tabTwoTextColor
	 * @param tabThreeTextColor
	 */
	private void textColorControl(int tabOneTextColor, int tabTwoTextColor, int tabThreeTextColor) {
		tabTextOne.setTextColor(getResources().getColor(tabOneTextColor));
		tabTextTwo.setTextColor(getResources().getColor(tabTwoTextColor));
		tabTextThree.setTextColor(getResources().getColor(tabThreeTextColor));
	}

	public void checkTab(int i) {
		if (i==2) {
			tabTwo.performClick();
		}
		if (i==3) {
			tabThree.performClick();
		}
	}
}
