package com.maxivetech.backoffice.service.user;

import javax.servlet.http.HttpSession;

import com.maxivetech.backoffice.entity.UserQuestions;
import com.maxivetech.backoffice.pojo.PojoSession;

public interface SessionService {
	public PojoSession checkLogined(HttpSession session);
	
	/**
	 * 
	 * @param account
	 * @param password
	 * @param session
	 * @return 返回Token值或null
	 */
	public PojoSession login(String tokenId,HttpSession session);
	public PojoSession login(String account, String password, String countryCode, HttpSession session);
	public PojoSession logout(HttpSession session);
	public String getVersion(HttpSession session);
	public void leaveContacts(String name, String company, String email, String mobile, HttpSession session);
	
	/**
	 * 注册
	 * @param email
	 * @param mobile
	 * @param smsCode
	 * @param password
	 * @param referralCode
	 * @param session
	 * @return
	 */
	public boolean register(String email, String mobile, String smsCode, String password, String paymentPassword, String name, String countryCode, String referralCode, HttpSession session);
	/**
	 * 发短信（注册用）
	 * @param email
	 * @param mobile
	 * @param session
	 * @return
	 */
	public boolean sendSms4Register(String email, String mobile, String countryCode, HttpSession session);
	/**
	 * 发短信（找回密码用）
	 * @param email
	 * @param mobile
	 * @param session
	 * @return
	 */
	public boolean sendSms4ForgotPassword(String email, String mobile, String countryCode, HttpSession session);
	/**
	 * 忘记密码重设
	 * @param email
	 * @param mobile
	 * @param smsCode
	 * @param password
	 * @param session
	 * @return
	 */
	public boolean forgotPassword(String email, String mobile, String smsCode, String password, String countryCode, String paymentPassword, HttpSession session);
    /**
     *  发送邮件验证码注册
     * @param email
     * @param mobile
     * @param countryCode
     * @param session
     * @return
     */
	public boolean sendEmailRegister(String email, String mobile, String countryCode, HttpSession session);
    /**
     * 发送邮件验证码更改密码
     * @param email
     * @param mobile
     * @param countryCode
     * @param session
     * @return
     */
	public boolean sendEmailForgotPassword(String email, String mobile, String countryCode, HttpSession session);

	public boolean leaveContacts1(String userName, String userPhone, String userEmail, String msgContent, HttpSession session);
}
