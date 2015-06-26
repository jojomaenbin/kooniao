package com.kooniao.travel.utils;

import java.util.Comparator;

import com.kooniao.travel.model.CustomerInfo;

public class ClientPinyinComparator implements Comparator<CustomerInfo> {

	public int compare(CustomerInfo o1, CustomerInfo o2) {
		return o1.getSortLetters().compareTo(o2.getSortLetters());
	}
}
