package com.kooniao.travel.customwidget;

import com.kooniao.travel.R;
import com.kooniao.travel.constant.Define;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class SwipeListView extends ListView {

	public SwipeListView(Context context) {
		this(context, null);
	}
	
	private int mTouchSlop;
	
	public SwipeListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}
	
	private int downY;
	private int downX;
	private View itemView;
	private View preItemView;
	private int slidePosition;
	private static final int SNAP_VELOCITY = 600;
	private VelocityTracker velocityTracker;
	public boolean isSlide = false;
	public boolean isObstruct = true;

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		try {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {

				addVelocityTracker(event);

				downX = (int) event.getX();
				downY = (int) event.getY();

				slidePosition = pointToPosition(downX, downY);
				if (slidePosition == AdapterView.INVALID_POSITION || slidePosition < getHeaderViewsCount()) {
					return super.dispatchTouchEvent(event);
				}

				itemView = getChildAt(slidePosition - getFirstVisiblePosition());
				itemView.findViewById(R.id.front).setPressed(false);
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				if (slidePosition != AdapterView.INVALID_POSITION && Math.abs(getScrollVelocity()) > SNAP_VELOCITY || (Math.abs(event.getX() - downX) > mTouchSlop && Math.abs(event.getY() - downY) < mTouchSlop)) {
					isSlide = true;
				} else {
					isSlide = false;
				}
				break;
			}
			case MotionEvent.ACTION_UP:
				recycleVelocityTracker();
				isObstruct = true;
				break;
			}
			return super.dispatchTouchEvent(event);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (itemView != null) {
			itemView.findViewById(R.id.front).setPressed(false);
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE:
			isSlide = true;
			break;

		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	private int preSlidePosition = AdapterView.INVALID_POSITION;
	private boolean isResponse = true;
	
	public boolean onTouchEvent(MotionEvent ev) {
		if (!isSlide && ev.getAction() == MotionEvent.ACTION_MOVE) {
			if (preSlidePosition != AdapterView.INVALID_POSITION) {
				preItemView = getChildAt(preSlidePosition - getFirstVisiblePosition());
				preSlidePosition = AdapterView.INVALID_POSITION;
				closePreMenu(); 
			}
		}
		if (isSlide && slidePosition != AdapterView.INVALID_POSITION) {
			if (itemView != null) {
				itemView.findViewById(R.id.front).setPressed(false);
			}
			addVelocityTracker(ev);
			final int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_MOVE:
				if (isObstruct) {
					if (isResponse == true && ev.getX() - downX < -5) {
						if (preSlidePosition != AdapterView.INVALID_POSITION && preSlidePosition != slidePosition) {
							preItemView = getChildAt(preSlidePosition - getFirstVisiblePosition());
							closePreMenu();
						}

						if (itemView != null) {
							Object tag = itemView.getTag(R.id.item_slideable);
							boolean canSlideable = true;
							if (tag != null) {
								canSlideable = (Boolean) tag;
							}
							if (canSlideable) {
								openMenu();
							}
						}

					} else if (isResponse == true && ev.getX() - downX > 10) {
						if (itemView != null) {
							closeMenu();
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				isObstruct = true;
				recycleVelocityTracker();
				isSlide = true;
				break;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 重置menu
	 */
	public void resetMenu() {
		if (preSlidePosition != AdapterView.INVALID_POSITION) {
			preItemView = getChildAt(preSlidePosition - getFirstVisiblePosition());
			preSlidePosition = AdapterView.INVALID_POSITION;
			closePreMenu(); 
		}
	}
	
	/**
	 * listview item 侧滑打开动画
	 */
	private void openMenu() {
		if (itemView != null) {
			ObjectAnimator animator = ObjectAnimator.ofFloat(itemView.findViewById(R.id.front), "X", 0 - offSet);
			animator.addListener(new AnimatorListenerAdapter() {
				
				@Override
				public void onAnimationStart(Animator arg0) {
					isResponse = false;
					isObstruct = false;
					itemView.findViewById(R.id.back).setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onAnimationEnd(Animator arg0) {
					preSlidePosition = slidePosition;
					itemView.findViewById(R.id.front).setPressed(false);
					isResponse = true;
					View backView = itemView.findViewById(R.id.back);
					
					int vChildCount = ((ViewGroup) backView).getChildCount();
					for (int i = 0; i < vChildCount; i++) {
						View subView = ((ViewGroup) backView).getChildAt(i);
						subView.setClickable(true);
						subView.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								if (sideSlipOptionCallback != null) {
									slidePosition = slidePosition - getHeaderViewsCount();
									sideSlipOptionCallback.onSideSlipOptionSelected(Integer.valueOf(v.getTag().toString()), slidePosition);
									closeMenu();
								}
							}
						});
					}
				}
			});
			animator.setDuration(250);
			animator.start();
		}
	}

	/**
	 * listview item 侧滑关闭前一个view的关闭动画
	 */
	public void closePreMenu() {
		if (preItemView != null) {
			ObjectAnimator animator = ObjectAnimator.ofFloat(preItemView.findViewById(R.id.front), "X", 0);
			animator.addListener(new AnimatorListenerAdapter() {
				
				@Override
				public void onAnimationStart(Animator arg0) {
					preItemView.findViewById(R.id.front).setPressed(false);
					isResponse = false;
					isObstruct = false;
				}
				
				@Override
				public void onAnimationEnd(Animator arg0) {
					isSlide = false;
					isResponse = true;
					isObstruct = true;
					preItemView.findViewById(R.id.back).setVisibility(View.GONE);
				}
			});
			animator.setDuration(250);
			animator.start();
		}
	}

	/**
	 * listview item 侧滑关闭动画
	 */
	public void closeMenu() {
		if (itemView != null) {
			ObjectAnimator animator = ObjectAnimator.ofFloat(itemView.findViewById(R.id.front), "X", 0);
			animator.addListener(new AnimatorListenerAdapter() {
				
				@Override
				public void onAnimationStart(Animator arg0) {
					isResponse = false;
					isObstruct = false;
				}
				
				@Override
				public void onAnimationEnd(Animator arg0) {
					itemView.findViewById(R.id.front).setPressed(false);
					isSlide = false;
					isResponse = true;
					isObstruct = true;
					itemView.findViewById(R.id.back).setVisibility(View.GONE);
					itemView = null;
					preSlidePosition = AdapterView.INVALID_POSITION;
				}
			});
			animator.setDuration(250);
			animator.start();
		}
	}
	
	private int offSet = Define.widthPx * 1 / 5; 

	/**
	 * 设置backview的滑动距离
	 * 
	 * @param offSet
	 */
	public void setBackViewOffSet(int offSet) {
		this.offSet = offSet;
	}
	
	private void addVelocityTracker(MotionEvent event) {
		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}
		velocityTracker.addMovement(event);
	}

	private void recycleVelocityTracker() {
		if (velocityTracker != null) {
			velocityTracker.recycle();
			velocityTracker = null;
		}
	}

	private int getScrollVelocity() {
		velocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) velocityTracker.getXVelocity();
		return velocity;
	}
	
	private SideSlipOptionCallback sideSlipOptionCallback;
	
	public interface SideSlipOptionCallback {
		/**
		 * @param menuType
		 *            侧滑选项
		 * @param position
		 *            listview位置
		 */
		public void onSideSlipOptionSelected(int menuType, int position);
	}
	
	public void setSideSlipOptionCallback(SideSlipOptionCallback sideSlipOptionCallback) {
		this.sideSlipOptionCallback = sideSlipOptionCallback;
	}
}
