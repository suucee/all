package com.maxivetech.backoffice.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HelperDate {

	private HelperDate() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * * 获取指定日期是星期几 参数为null时表示获取当前日期是星期几
	 * 
	 * @param date
	 * @return
	 */

	public static int getWeekOfDate(Date date) {
		Calendar calendar = Calendar.getInstance();

		if (date != null) {
			calendar.setTime(date);
		}

		int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;

		return w;
	}
	

	/**
	 * 方便點，String to Date
	 * @param s
	 * @param format
	 * @return
	 */
	public static Date parse(String s, String format) {
		if (s == null || s.equals("")) {
			return null;
		}
		try {
			return (new SimpleDateFormat(format)).parse(s);
		} catch (ParseException e) {
			return null;
		}
	}
	
	
	
	
	public static String format(Date date, String format) {
		if (date == null || date.equals("")) {
			return null;
		}
		if (format == null || format.equals("")) {
			return null;
		}
		try {
			return (new SimpleDateFormat(format)).format(date);
		} catch (Exception e) {
			return null;
		}
	}
}