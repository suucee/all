package com.maxivetech.backoffice.service.user;

import javax.servlet.http.HttpSession;

import com.maxivetech.backoffice.pojo.PojoSession;

public interface Session2Service {
	
	/**
	 * 注册
	 * @param mobile
	 * @param smsCode
	 * @param password
	 * @param countryCode
	 * @param referralCode
	 * @param session
	 * @return boolean
	 */
	public boolean register( String mobile, String smsCode, String password, String countryCode, String referralCode, HttpSession session);
	
	
	/**
	 * 发短信（注册用）
	 * @param email
	 * @param mobile
	 * @param session
	 * @return
	 */
	public boolean sendSms4Register(String mobile, String countryCode, HttpSession session);
	
	
	
	boolean sendEmailRegister(String email, HttpSession session);
	
	
	boolean bindEmail(int userId , String email,String emailCode,  HttpSession session);
	
	/**
	 * 发送邮件验证码到邮箱
	 * @param email
	 * @param session
	 * @return
	 */
	boolean sendEmail4ForgotPassword(String email,HttpSession session);
	
	/**
	 * 发送短信验证码到手机
	 * @param mobile 手机号
	 * @param countryCode 区号
	 * @param session
	 * @return
	 */
	boolean sendSms4ForgotPassword(String mobile, String countryCode, HttpSession session);
	
	String checkCode(String email, String mobile,String countryCode, String code, HttpSession session);
	
	/**
	 * 重置密码（用户：登录+支付，管理员：登录+操作）
	 * @param account
	 * @param countryCode
	 * @param password
	 * @param password2 用户：支付，管理员：操作
	 * @param session
	 * @return
	 */
	boolean resetPassword(String account,String countryCode, String password,String password2, HttpSession session);
	
	

    
	//申请成为经纪人发送短信验证手机
	public boolean sendSmsApply(String hasNo,String email, String mobile, String countryCode, HttpSession session);


	public boolean registerApply(String hasNo,String email, String mobile, String smsCode, String password, 
			String name, String countryCode, HttpSession session);


	PojoSession logout(HttpSession session);


	PojoSession loginPwdError(String account, String password, String countryCode, HttpSession session);


	/**
	 * 解除账户锁定
	 * @param email
	 * @param mobile
	 * @param countryCode
	 * @param code
	 * @param session
	 * @return
	 */
	String checkCode4Unlock(String email, String mobile, String countryCode, String code, HttpSession session);
}
