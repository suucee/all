package com.maxivetech.backoffice.sendmail;

import java.util.Date;
import java.util.HashMap;

public class PostmanUserProfile extends Postman {

	public PostmanUserProfile(String email) {
		super("cn", 8, email);
		// TODO Auto-generated constructor stub
	}

	public void verified() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("audited_time", this.formatDate(new Date()));
		map.put("state", "已认证");
		
		this.sendAsync("user_profile.html", map);
	}
	
	public void rejected(String auditedMemo) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("audited_time", this.formatDate(new Date()));
		map.put("audited_memo", auditedMemo);
		map.put("state", "资料驳回");
		
		this.sendAsync("user_profile_refused.html", map);
	}
}
