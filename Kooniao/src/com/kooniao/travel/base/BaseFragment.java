package com.kooniao.travel.base;

import android.app.Activity;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

	public NotificationCallback notificationCallback;

	public interface NotificationCallback {
		void onHideBottomBarListener(boolean isNeedToHide);
	}

	public boolean isAttach = false;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		isAttach = true;
		try {
			notificationCallback = (NotificationCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement NotificationCallback...");
		}
	}
}
