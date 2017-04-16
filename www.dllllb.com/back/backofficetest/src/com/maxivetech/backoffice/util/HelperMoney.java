package com.maxivetech.backoffice.util;

import java.text.DecimalFormat;

public class HelperMoney {
	public static String formatMoney(double v) {
		return new DecimalFormat("0.00").format(Math.round(v * 100 - 0.5) / 100.0);
	}
}
