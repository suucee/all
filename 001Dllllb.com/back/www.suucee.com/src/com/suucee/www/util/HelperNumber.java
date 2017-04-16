package com.suucee.www.util;

import java.text.DecimalFormat;

public class HelperNumber {

	public static double toFixed2Num(double num){
		double num1=num-0.004999999;
		DecimalFormat df = new DecimalFormat("#.00");
		return Double.parseDouble(df.format(num1));
	}
	public static double toFixed2NumIn(double num){
		double num1=num+0.004999999;
		DecimalFormat df = new DecimalFormat("#.00");
		return Double.parseDouble(df.format(num1));
	}
}
