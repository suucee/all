package com.maxivetech.backoffice.sendmail;

import java.util.Date;
import java.util.HashMap;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.entity.Users;


public class PostmanAgent extends Postman {
	public PostmanAgent(String email) {
		super("cn", 8, email);
		// TODO Auto-generated constructor stub
	}

	public void sendAgentAgreement(int  user_id,String downloadUrl) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		//String url = BackOfficeConsts.getInst().URL_ROOT+"user/email_address_verification.html?"+emailVerificationCode;
		String url = "";
		map.put("user_id", user_id);
		map.put("download_url", downloadUrl);
		
		this.sendAsync("agent_agreement.html", map);
	}
}
