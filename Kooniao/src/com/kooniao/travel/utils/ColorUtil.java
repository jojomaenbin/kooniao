package com.kooniao.travel.utils;

import android.graphics.Color;

public class ColorUtil {

	/**
	 * 随机获取一种颜色
	 * 
	 * @return
	 */
	private static int colorcount = 0;

	public static int getRandomColorRes() {
		colorcount++;
		switch (colorcount % 7) {
		case 0:
			return Color.parseColor("#31A9B6");
		case 1:
			return Color.parseColor("#CC4478");
		case 2:
			return Color.parseColor("#2E9260");
		case 3:
			return Color.parseColor("#F2AE45");
		case 4:
			return Color.parseColor("#83B05F");
		case 5:
			return Color.parseColor("#8F42AE");
		case 6:
			return Color.parseColor("#E96C44");
		default:
			return 0;
		}
		
	}

}
