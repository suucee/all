package com.maxivetech.backoffice.sendmail;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Postman {
	protected String lang;
	protected double timezone;
	protected String email;
	
	public Postman(String lang, double timezone, String email) {
		this.lang = lang;
		this.timezone = timezone;
		this.email = email;
	}
	
	protected void sendAsync(String filename, HashMap<String, Object> map) {
		HelperSendmail.sendAsync("email_"+this.lang+"/"+filename, map, this.email, "");
	}
	
	protected boolean send(String filename, HashMap<String, Object> map) {
		return HelperSendmail.send("email_"+this.lang+"/"+filename, map, this.email, "");
	}
	
	protected String formatPrice(double v) {
		return (new BigDecimal(v)).setScale(5, RoundingMode.HALF_UP).toString();
	}
	protected String formatMoney(double v) {
		//return new DecimalFormat("#.00").format(Math.round(v * 100 - 0.5) / 100.0);
		return String.valueOf(Math.round(v * 100 - 0.5) / 100.0);
	}
	protected String formatDate(Date date) {
		if (date == null) {return "-";}
		
		Date newDate = new Date((long) (date.getTime() + 1000 * 60 * 60 * (this.timezone - 8)));
		
		return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(newDate)
				+ " GMT"
				+ (this.timezone >= 0 ? "+" : "")
				+ (int)this.timezone
				+ ":00";
	}
}
