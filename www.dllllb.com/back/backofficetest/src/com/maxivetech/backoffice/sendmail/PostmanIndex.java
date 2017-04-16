package com.maxivetech.backoffice.sendmail;

import java.util.HashMap;

import com.maxivetech.backoffice.BackOffice;


public class PostmanIndex extends Postman {
	public PostmanIndex(String email) {
		super("cn", 8, email);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 客户留下联系方式邮件
	 * @param name
	 * @param company
	 * @param email
	 * @param mobile
	 */
	public void leaveContacts(String name, String company, String email, String mobile) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("name", name);
		map.put("company", company);
		map.put("email", email);
		map.put("mobile", mobile);
		
		this.sendAsync("leave_contacts.html", map);
	}
	
	public void leaveContacts1(String name, String email, String mobile,String msgContent) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("name", name);
		map.put("email", email);
		map.put("mobile", mobile);
		map.put("content", msgContent);
		this.sendAsync("leave_contacts1.html", map);
	}
}
