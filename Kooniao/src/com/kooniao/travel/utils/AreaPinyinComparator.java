package com.kooniao.travel.utils;

import java.util.Comparator;

import com.kooniao.travel.model.SubArea;


public class AreaPinyinComparator implements Comparator<SubArea> {

	public int compare(SubArea o1, SubArea o2) {
		if (o1.getSort_letter().equals("#")) {
			return -1;
		} else if (o2.getSort_letter().equals("#")) {
			return 1;
		} else {
			return o1.getSort_letter().compareTo(o2.getSort_letter());
		}
	}

}
