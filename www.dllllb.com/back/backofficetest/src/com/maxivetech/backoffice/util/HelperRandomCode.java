package com.maxivetech.backoffice.util;

import java.util.Random;

public class HelperRandomCode {
	public static String getARandomCode(int length) {
		String codearr[] = { "2", "3", "4", "5", "6", "7", "8", "9",
				"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
				"M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
				"Y", "Z" };
		Random random = new Random();
		int c = 0;
		boolean isuse = true;
		String code = "";
			while (c < 5) {
				int s = random.nextInt(codearr.length - 1)
						% (codearr.length);
				code += codearr[s];
				c++;
			}
		return code;
	}
}
