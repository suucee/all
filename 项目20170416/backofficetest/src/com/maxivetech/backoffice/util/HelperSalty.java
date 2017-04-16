package com.maxivetech.backoffice.util;

import java.util.Random;

public class HelperSalty {
	public static String getCharAndNumr(int length) {
		String val = "";
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			// 输出字母还是数字
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			// 字符串　　
			if ("char".equalsIgnoreCase(charOrNum)) {
				// 取得大写字母还是小写字母　　
				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
				val += (char) (choice + random.nextInt(26));
			} else if ("num".equalsIgnoreCase(charOrNum)) {
				// 数字　　
				val += String.valueOf(random.nextInt(6));
			}
		}
		return val;
	}
	

	public static String getRandomNum() {
		return String.valueOf((int)(Math.random() * 899999) + 100000);
	}
}
