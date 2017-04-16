package com.maxivetech.backoffice.sendmail;

import java.util.Date;
import java.util.HashMap;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.entity.Users;


public class PostmanCode extends Postman {
	public PostmanCode(String email) {
		super("cn", 8, email);
		// TODO Auto-generated constructor stub
	}

	public void sendCode(String code) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		//String url = BackOfficeConsts.getInst().URL_ROOT+"user/email_address_verification.html?"+emailVerificationCode;
		String url = "";
		map.put("code", code);
		map.put("send_time", this.formatDate(new Date()));
		
		this.sendAsync("verification_code.html", map);
	}
}
