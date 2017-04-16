package com.maxivetech.backoffice.sendmail;

import java.util.HashMap;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.entity.Users;


public class PostmanUser extends Postman {
	public PostmanUser(String email) {
		super("cn", 8, email);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Email地址验证
	 * @param email
	 * @param userId
	 * @param url
	 */
	public void emailAddressVerification(int id, String emailVerificationCode) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		//String url = BackOfficeConsts.getInst().URL_ROOT+"user/email_address_verification.html?"+emailVerificationCode;
		String url = "";
		map.put("id", id);
		map.put("email", this.email);
		map.put("url", url);
		
		this.sendAsync("email_address_verification.html", map);
	}
	/**
	 * 重置网站密码
	 * @param email
	 * @param userId
	 * @param url
	 */
	public void resetUserPassword(String email, String password, String paymentPassword) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("email", email);
		map.put("password", password);
		map.put("paymentPassword", paymentPassword);
		
		this.sendAsync("admin_reset_userPassword.html", map);
	}
	
	
	/**
	 * 通知用户重置密码成功，注意，这里不包含明文密码
	 * @param email
	 */
	public void userResetPassword(String email){
		this.sendAsync("user_reset_password.html", new HashMap<String, Object>());
	}
}
