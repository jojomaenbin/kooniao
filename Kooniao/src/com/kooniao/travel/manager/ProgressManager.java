package com.kooniao.travel.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kooniao.travel.R;
import com.kooniao.travel.model.OffLine;

public class ProgressManager {
	public static List<TextView> mProgressTextViews = new ArrayList<TextView>();
	public static HashMap<String, String> mProgressMap = new HashMap<String, String>();

	public static String getProgressbyDownloadPath(int downloadid) {
		if (mProgressMap.containsKey(downloadid+"")) {
			return mProgressMap.get(downloadid+"");
		} else {
			return null;
		}
	}

	public static void addProgressTextView(TextView progressTextView) {
		mProgressTextViews.add(progressTextView);
		if (mProgressTextViews.size()>7) {
			mProgressTextViews.remove(0);
		}
	}

	public static void putProgress(String downloadid, String progress) {
		mProgressMap.put(downloadid+"", progress);
		for (TextView t : mProgressTextViews) {
			if (t.getTag().toString().equals(downloadid+"")) {
				t.setText(progress);
				t.setVisibility(View.VISIBLE);
			}
		}
	}

	public static void endProgress(OffLine offLine) {
		mProgressMap.remove(offLine.getTravelId()+"");
		for (TextView t : mProgressTextViews) {
			if (t != null)
				if (t.getTag().toString().equals(offLine.getTravelId()+"")) {
					if (offLine.getDownloadStatus() == 1) {
						// 下载成功
						ListView listView =(ListView) getListViewGroup(t);
//						listView.notify();
						if (listView!=null) {
							((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
						}
//						t.setVisibility(View.INVISIBLE);
					} else if (offLine.getDownloadStatus() == -1) {
						// 下载失败
						t.setVisibility(View.VISIBLE);
						t.setText(R.string.re_download);
					} else if (offLine.getDownloadStatus() == 0) {
						// 下载中
						t.setVisibility(View.VISIBLE);
						t.setText(offLine.getProgress());
					} else {
						// 需要更新offline
						t.setVisibility(View.VISIBLE);
						t.setText("更新");
					}
				}
		}

	}
	
	public static ViewGroup getListViewGroup(View view) {
		if (view instanceof ListView) {
			return (ViewGroup) view;
		}
		else {
			if (view.getParent()!=null) {
				return getListViewGroup((View) view.getParent());
			}
			else {
				return null;
			}
		}
	}

}
