package com.kooniao.travel.utils;

import java.util.Comparator;

import com.kooniao.travel.model.City;

public class CityPinyinComparator implements Comparator<City> {

	public int compare(City o1, City o2) {
		
		if (o1.getSortLetters().equals("定")) {
			return -1;
			
		} else if (o2.getSortLetters().equals("定")) {
			return 1;
			
		} else if (o1.getSortLetters().equals("热")) {
			return -1;
			
		} else if (o2.getSortLetters().equals("热")) {
			return 1;
			
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
			
		}
		
	}

}
