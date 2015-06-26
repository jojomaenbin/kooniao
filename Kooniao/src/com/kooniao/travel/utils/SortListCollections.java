package com.kooniao.travel.utils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.kooniao.travel.constant.Define;

import android.util.Log;

/**
 * List排序
 * @author ke.wei.quan
 *
 * @param <T>
 */
public class SortListCollections<T> {
	
	public static enum Sort {
		DESC(0), // 降序
		ASC(1); // 升序
		
		int sort;
		private Sort(int sort) {
			this.sort = sort;
		}
	}
	
	private String sortKey;
	private Sort sort;
	
	/**
	 * 排序
	 * @param list 排序数据
	 * @param sortKey model排序字段
	 * @param sort 排序方式
	 */
	public void sort(List<T> list, final String sortKey, final Sort sort) {
		this.sortKey = sortKey;
		this.sort = sort;
		Collections.sort(list, new SortComparetor()); 
	}
	
	class SortComparetor implements Comparator<T> {

		@Override
		public int compare(T lhs, T rhs) {
			int result = 1;
			try {
				Field lhsField = ((T)lhs).getClass().getDeclaredField(sortKey);
				lhsField.setAccessible(true);
				Field rhsField = ((T)rhs).getClass().getDeclaredField(sortKey);
				rhsField.setAccessible(true);
				if (sort.sort == Sort.DESC.sort) {
					result = rhsField.get((T)rhs).toString().compareTo(lhsField.get((T)lhs).toString());
				} else {
					result = lhsField.get((T)lhs).toString().compareTo(rhsField.get((T)rhs).toString());
				}
			} catch (Exception e) {
				Log.e(Define.LOG_TAG, e.toString()); 
			}
			return result;
		}
		
	}

}
