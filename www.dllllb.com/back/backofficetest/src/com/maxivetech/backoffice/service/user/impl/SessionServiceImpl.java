package com.maxivetech.backoffice.service.user.impl;

import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.dao.AdminDao;
import com.maxivetech.backoffice.dao.AttachmentDao;
import com.maxivetech.backoffice.dao.SettingDao;
import com.maxivetech.backoffice.dao.TokenDao;
import com.maxivetech.backoffice.dao.UserBalanceDao;
import com.maxivetech.backoffice.dao.UserBankAccountDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.dao.UserQuestionsDao;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Tokens;
import com.maxivetech.backoffice.entity.UserBalances;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoSession;
import com.maxivetech.backoffice.sendmail.PostmanCode;
import com.maxivetech.backoffice.sendmail.PostmanIndex;
import com.maxivetech.backoffice.service.user.SessionService;
import com.maxivetech.backoffice.sms.HelperSms;
import com.maxivetech.backoffice.util.HelperPassword;
import com.maxivetech.backoffice.util.HelperRandomCode;
import com.maxivetech.backoffice.util.HelperSalty;

@Service
@Transactional
public class SessionServiceImpl implements SessionService {
	@Autowired
	private AdminDao adminDao;
	@Autowired
	private UserQuestionsDao userQuestionsDao;
	@Autowired
	private UserBalanceDao userBalanceDao;
	@Autowired
	private UserBankAccountDao userBankAccountDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private AttachmentDao attachmentDao;
	@Autowired
	private UserProfilesDao userProfilesDao;
	@Autowired
	private TokenDao tokenDao;
	@Autowired
	private SettingDao settingDao;

	public static final String SESSION_KEY = "logined";
	public static final String SESSION_USER_KEY = "loginedUser";
	public static final String SESSION_ADMIN_KEY = "loginedAdmin";

	public static PojoSession getCurrent(HttpSession session) {
		Object object = session.getAttribute(SESSION_KEY);
		if (object != null && object instanceof PojoSession) {
			return (PojoSession) object;
		}
		return null;
	}

	public static Users getCurrentUser(HttpSession session) {
		return (Users) session.getAttribute(SESSION_USER_KEY);
	}

	public static Admins getCurrentAdmin(HttpSession session) {
		return (Admins) session.getAttribute(SESSION_ADMIN_KEY);
	}

	@Override
	public PojoSession checkLogined(HttpSession session) {
		Object object = session.getAttribute(SESSION_KEY);

		if (object != null) {
			if (object instanceof PojoSession) {
				PojoSession pojo = (PojoSession) object;

				return pojo;
			}
		}

		return null;
	}

	@Override
	public PojoSession login(String account, String password, String countryCode, HttpSession session) {
		Users user = null;
		Admins admin = null;

		
		account = account.toLowerCase();
		if (Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", account)) {
			// 根据Email查
			user = userDao.findByEmail(account);
		} else {
			// 根据手机号查
			user = userDao.findByMobile("+" + countryCode + "." + account);
		}
		
		if (user == null) {
			// 根据账号查
			admin = adminDao.findByAccount(account);
		}

		
		
		if (user != null && 
			HelperPassword.verifyPassword(password, user.getPassword(), user.getSalty())) {
			// 验证User账号和密码

			if(user.isDisable()){
				throw new RuntimeException("抱歉，您的账号已被禁用，无法登陆。");
			}
			// 用户登录成功
			user.setLastLogonTime(new Date());
			userDao.saveOrUpdate(user);
			
			PojoSession pojo = new PojoSession(user);
			session.setAttribute(SESSION_KEY, pojo);
			session.setAttribute(SESSION_USER_KEY, user);
			session.setAttribute(SESSION_ADMIN_KEY, null);

			tokenDao.deleteToken(tokenDao.findByUser(user));
			
			String ipAddress = (String) session.getAttribute("RemoteAddr");
			Tokens token = new Tokens();
			token.setAdmin(null);
			token.setCreatTime(new Date());
			token.setExpirationTime(null);
			token.setIpAddress(ipAddress == null ? "" : ipAddress);
			token.setLastAuthorizationTime(null);
			token.setUser(user);
			token.setUuid(UUID.randomUUID().toString());
			tokenDao.save(token);
			tokenDao.commit();

			pojo.setToken(token.getUuid());
			
			return pojo;
		} else if (admin != null
				&& HelperPassword.verifyPassword(password, admin.getPassword(), admin.getSalty())) {
			// 验证Admin账号、密码、是否禁用
			if(admin.isDisabled()){
				throw new RuntimeException("抱歉，您的账号已被禁用，无法登陆。");
			}
			// 管理员登录成功
			admin.setLastLogonTime(new Date());
			adminDao.saveOrUpdate(admin);
			
			PojoSession pojo = new PojoSession(admin);
			session.setAttribute(SESSION_KEY, pojo);
			session.setAttribute(SESSION_USER_KEY, null);
			session.setAttribute(SESSION_ADMIN_KEY, admin);

			tokenDao.deleteToken(tokenDao.findByAdmin(admin));
			
			String ipAddress = (String) session.getAttribute("RemoteAddr");
			Tokens token = new Tokens();
			token.setAdmin(admin);
			token.setCreatTime(new Date());
			token.setExpirationTime(null);
			token.setIpAddress(ipAddress == null ? "" : ipAddress);
			token.setLastAuthorizationTime(null);
			token.setUser(null);
			token.setUuid(UUID.randomUUID().toString());
			tokenDao.save(token);
			tokenDao.commit();

			pojo.setToken(token.getUuid());
			
			return pojo;
		} else {

			// 验证失败
			session.removeAttribute(SESSION_KEY);
			session.removeAttribute(SESSION_USER_KEY);
			session.removeAttribute(SESSION_ADMIN_KEY);
		}

		return null;
	}

	
	
	@Override
	public PojoSession login(String tokenId, HttpSession session) {
		Users user = null;
		Admins admin = null;
		Tokens tokens=tokenDao.findByUUID(tokenId);
		user=tokens.getUser();
		admin=tokens.getAdmin();
		if (user != null&&!user.isDisable()) {
				// 用户登录成功
				user.setLastLogonTime(new Date());
				userDao.saveOrUpdate(user);
			
				PojoSession pojo = new PojoSession(user);
				session.setAttribute(SESSION_KEY, pojo);
				session.setAttribute(SESSION_USER_KEY, user);
				session.setAttribute(SESSION_ADMIN_KEY, null);

				tokenDao.deleteToken(tokenDao.findByUser(user));
				
				String ipAddress = (String) session.getAttribute("RemoteAddr");
				Tokens token = new Tokens();
				token.setAdmin(null);
				token.setCreatTime(new Date());
				token.setExpirationTime(null);
				token.setIpAddress(ipAddress == null ? "" : ipAddress);
				token.setLastAuthorizationTime(null);
				token.setUser(user);
				token.setUuid(UUID.randomUUID().toString());
				tokenDao.save(token);
				tokenDao.commit();

				pojo.setToken(token.getUuid());
				
				return pojo;
			} else if (admin != null&& !admin.isDisabled()) {
				// 验证Admin账号、密码、是否禁用

				// 管理员登录成功
				admin.setLastLogonTime(new Date());
				adminDao.saveOrUpdate(admin);
				
				PojoSession pojo = new PojoSession(admin);
				session.setAttribute(SESSION_KEY, pojo);
				session.setAttribute(SESSION_USER_KEY, null);
				session.setAttribute(SESSION_ADMIN_KEY, admin);

				tokenDao.deleteToken(tokenDao.findByAdmin(admin));
				
				String ipAddress = (String) session.getAttribute("RemoteAddr");
				Tokens token = new Tokens();
				token.setAdmin(admin);
				token.setCreatTime(new Date());
				token.setExpirationTime(null);
				token.setIpAddress(ipAddress == null ? "" : ipAddress);
				token.setLastAuthorizationTime(null);
				token.setUser(null);
				token.setUuid(UUID.randomUUID().toString());
				tokenDao.save(token);
				tokenDao.commit();

				pojo.setToken(token.getUuid());
				
				return pojo;
			} else {

				// 验证失败
				session.removeAttribute(SESSION_KEY);
				session.removeAttribute(SESSION_USER_KEY);
				session.removeAttribute(SESSION_ADMIN_KEY);
			}

			return null;
	}

	@Override
	public PojoSession logout(HttpSession session) {
		PojoSession pojoOld = this.getCurrent(session);
		if (pojoOld != null && pojoOld.getHoldAdminId() > 0) {
			//代管返登
			
			// 管理员登录成功
			Admins admin = (Admins)adminDao.getById(pojoOld.getHoldAdminId());
			
			PojoSession pojo = new PojoSession(admin);
			pojo.setRedirectUrl("user_edit_profile.html?userId=" + pojoOld.getId());
			session.setAttribute(SESSION_KEY, pojo);
			session.setAttribute(SESSION_USER_KEY, null);
			session.setAttribute(SESSION_ADMIN_KEY, admin);

			tokenDao.deleteToken(tokenDao.findByAdmin(admin));

			String ipAddress = (String) session.getAttribute("RemoteAddr");
			Tokens token = new Tokens();
			token.setAdmin(admin);
			token.setCreatTime(new Date());
			token.setExpirationTime(null);
			token.setIpAddress(ipAddress == null ? "" : ipAddress);
			token.setLastAuthorizationTime(null);
			token.setUser(null);
			token.setUuid(UUID.randomUUID().toString());
			tokenDao.save(token);
			tokenDao.commit();
			
			pojo.setToken(token.getUuid());
			
			return pojo;
		}
		
		session.removeAttribute(SESSION_KEY);
		return null;
	}

	@Override
	public String getVersion(HttpSession session) {
		// TODO Auto-generated method stub
		return BackOffice.getInst().VERSION;
	}
	
	@Override
	public boolean register(String email, String mobile, String smsCode, 
			String password, String paymentPassword, String name, String countryCode, 
			String referralCode, HttpSession session) {
		//小写化
		email = email.toLowerCase();
		
		//检查Email格式
		if (!Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", email)) {
			throw new RuntimeException("非法的Email地址！");
		}
		//检查Email存在
		if (userDao.findByEmail(email) != null) {
			throw new RuntimeException("该Email已注册，如果你忘记了密码，可以通过“忘记密码”功能重设密码！");
		}
		
		//检查手机号格式
		if (mobile == null || !Pattern.matches("^[0-9]{8,11}$", mobile)) {
			throw new RuntimeException("非法的手机号(须8-11位)！");
		}
		//检查手机号存在
		if (userDao.findByMobile("+" + countryCode + "." + mobile) != null) {
			throw new RuntimeException("该手机号已注册，如果你忘记了密码，可以通过“忘记密码”功能重设密码！");
		}

		//检查短信验证码
		if (smsCode == null || !Pattern.matches("^[0-9]{6}$", smsCode)) {
			throw new RuntimeException("非法的手机验证码！");
		}
		//*检查短信验证码和手机号
		{
			String _smsCode = (String)session.getAttribute("smsCode");
			String _mobile = (String)session.getAttribute("mobile");
			String _email = (String)session.getAttribute("email");
			Date _lastSent = (Date)session.getAttribute("lastSent");
			if (_smsCode == null || !smsCode.equals(_smsCode) ||
				_mobile == null || !("+" + countryCode + "." + mobile).equals(_mobile) ||
				_email == null ||!email.equals(_email) ||
				_lastSent == null || (new Date()).getTime() - _lastSent.getTime() > 300 * 1000) {
				
				throw new RuntimeException("非法的验证码（可能已经超时）！");
			}
		}
		//*/
		
		Users parentUser = null;
		if (referralCode != null && !referralCode.equals("")) {
			//检查推荐码格式
			if (!Pattern.matches("^[0-9a-zA-Z]{5}$", referralCode)) {
				throw new RuntimeException("非法的推荐码格式");
			}
			//检查推荐码存在
			parentUser = userDao.findByUserCode(referralCode);
			if (parentUser == null || 
				!BackOffice.getInst().companyHook.isReferralCodeAvailable(parentUser.getVipGrade(), parentUser.getLevel())) {
				throw new RuntimeException("非法的推荐码！");
			}
		} else {
			parentUser = userDao.findByUserCode(settingDao.getString("DefaultAgent"));
			if (parentUser == null) {
				throw new RuntimeException("系统错误：默认经纪人丢失！");
			}
		}

		//检查密码长度
		if (password.length() < 6) {
			throw new RuntimeException("密码不能少于6位！");
		}
		
		Date now = new Date();
		String salty = HelperSalty.getCharAndNumr(6);
		
		//创建User
		Users user = new Users();
		user.setParent(parentUser);// 设置上线
		user.setSerialNumber(0);
		user.setState("UNVERIFIED");
		user.setFrozen(false);
		user.setEmail(email);
		user.setMobile("+" + countryCode + "." + mobile);
		user.setPassword(HelperPassword.beforeSave(password, salty));// 密码
		user.setPaymentPassword(HelperPassword.beforeSave(paymentPassword, salty));// 支付密码
		user.setSalty(salty);
		user.setRegistrationTime(now);
		user.setLastLogonTime(now);
		user.setLevel(Math.max(parentUser.getLevel() + 1, 4));
		//生成推荐码
		if (settingDao.getInt("AutoAgent") == 1)
		{
			String code = "";
			do {
				code = HelperRandomCode.getARandomCode(5);
			} while (userDao.findByUserCode(code) != null);
			
			//XXX:推荐码用完了会死循环
			user.setReferralCode(code);
		} else {
			user.setReferralCode(null);
		}
		
		user.setPath("");
		userDao.save(user);
		//设置Path
		{
			user.setPath(parentUser.getPath() + user.getId() + ",");// 设置当前用的path
																	// //
		}															// 方便查询
		userDao.update(user);
		
		//创建UserProfile
		{
			UserProfiles profile = new UserProfiles();
			profile.setUser(user);
			profile.setCardType("");
			profile.setCompany("");
			profile.setCreatTime(now);
			profile.setPosition("");
			profile.setUpdatedTime(now);
			profile.setUserComment("");
			profile.setUserEName("");
			profile.setUserEsidentialAddress("");
			profile.setUserIdCard("");
			profile.setUserIndustry("");
			profile.setUserName(name);
			profile.setUserNationality("");
			profile.setUserYearsIncom("");
			
			userProfilesDao.save(profile);
		}
		
		//创建网站账户
		{
			UserBalances ub = new UserBalances();
			ub.setAmountAvailable(0);
			ub.setAmountFrozen(0);
			ub.setCurrencyType("USD");
			ub.setUpdatedTime(now);
			ub.setUser(user);
			
			userDao.save(ub);
		}
		userDao.commit();

		session.removeAttribute("smsCode");
		session.removeAttribute("mobile");
		session.removeAttribute("lastSent");
		
		return true;
	}

	@Override
	public boolean forgotPassword(String email, String mobile, String smsCode,
			String password, String countryCode, String paymentPassword, HttpSession session) {
		//小写化
		email = email.toLowerCase();
				
		//检查Email格式
		if (!Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", email)) {
			throw new RuntimeException("非法的Email地址！");
		}
		//检查Email存在
		Users userByEmail = userDao.findByEmail(email);
		if (userByEmail == null) {
			throw new RuntimeException("该Email未注册！");
		}

		//检查手机号格式
		if (mobile == null || !Pattern.matches("^[0-9]{8,11}$", mobile)) {
			throw new RuntimeException("非法的手机号(须8-11位)！");
		}
		//检查手机号存在
		Users userByMobile = userDao.findByMobile("+" + countryCode + "." + mobile);
		if (userByMobile == null) {
			throw new RuntimeException("该手机号未注册！");
		}
		
		//检查匹配
		if (userByEmail.getId() != userByMobile.getId()) {
			throw new RuntimeException("Email与手机号不匹配！");
		}
		
		//检查短信验证码
		if (smsCode == null || !Pattern.matches("^[0-9]{6}$", smsCode)) {
			throw new RuntimeException("非法的手机验证码！");
		}
		//*检查短信验证码和手机号
		{
			String _smsCode = (String)session.getAttribute("smsCode");
			String _mobile = (String)session.getAttribute("mobile");
			String _email = (String)session.getAttribute("email");
			Date _lastSent = (Date)session.getAttribute("lastSent");
			if (_smsCode == null || !smsCode.equals(_smsCode) ||
				_mobile == null || !("+" + countryCode + "." + mobile).equals(_mobile) ||
				_email == null ||!email.equals(_email) ||
				_lastSent == null || (new Date()).getTime() - _lastSent.getTime() > 300 * 1000) {
						
				throw new RuntimeException("非法的验证码（可能已经超时）！");
			}
		}
		//*/

		//检查登录密码
		if (password == null || password.length() < 6) {
			throw new RuntimeException("登录密码不能少于6位！");
		}
		//检查支付密码
		if (paymentPassword == null || paymentPassword.length() < 6) {
			throw new RuntimeException("登录密码不能少于6位！");
		}
		//检查
		if (paymentPassword.equals(password)) {
			throw new RuntimeException("支付密码不能与登录密码相同！");
		}
		
		//通过
		userByEmail.setPassword(HelperPassword.beforeSave(password, userByEmail.getSalty()));
		userByEmail.setPaymentPassword(HelperPassword.beforeSave(paymentPassword, userByEmail.getSalty()));
		userDao.update(userByEmail);
		userDao.commit();

		session.removeAttribute("mobile");
		session.removeAttribute("smsCode");
		session.removeAttribute("email");
		session.removeAttribute("lastSent");

		return true;
	}


	@Override
	public boolean sendSms4Register(String email, String mobile, String countryCode, HttpSession session) {
		String smsCode = HelperSalty.getRandomNum();
		
		Date now = new Date();
		
		//检查上次发送短信时间
		Date lastSent = (Date) session.getAttribute("lastSent");
		if (lastSent != null && now.getTime() - lastSent.getTime() < 120 * 1000) {
			//120秒内只能发送1次手机验证码
			throw new RuntimeException("120秒内只能发送1次手机验证码");
		}
		
		//检查Email格式
		if (!Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", email)) {
			throw new RuntimeException("非法的Email地址！");
		}
		//检查Email存在
		if (userDao.findByEmail(email) != null) {
			throw new RuntimeException("该Email已注册，如果你忘记了密码，可以通过“忘记密码”功能重设密码！");
		}
		//检查手机号格式
		if (mobile == null || !Pattern.matches("^[0-9]{8,11}$", mobile)) {
			throw new RuntimeException("非法的手机号(须8-11位)！");
		}
		//检查手机号存在
		if (userDao.findByMobile(mobile) != null) {
			throw new RuntimeException("该手机号已注册，如果你忘记了密码，可以通过“忘记密码”功能重设密码！");
		}
			
		session.setAttribute("smsCode", smsCode);
		session.setAttribute("mobile", "+" + countryCode + "." + mobile);
		session.setAttribute("email", email);
		session.setAttribute("lastSent", new Date());
			
		HelperSms.send(countryCode + mobile, "欢迎您注册"+BackOffice.getInst().COMPANY_NAME+"，您的手机验证码是："+smsCode+"，5分钟内有效【"+BackOffice.getInst().COMPANY_NAME+"】");		
		return true;
	}
	
	@Override
	public boolean sendSms4ForgotPassword(String email, String mobile, String countryCode, HttpSession session) {
		String smsCode = HelperSalty.getRandomNum();
		
		Date now = new Date();
		
		//检查上次发送短信时间
		Date lastSent = (Date) session.getAttribute("lastSent");
		if (lastSent != null && now.getTime() - lastSent.getTime() < 120 * 1000) {
			//120秒内只能发送1次手机验证码
			throw new RuntimeException("120秒内只能发送1次手机验证码");
		}
		
		//检查Email格式
		if (!Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", email)) {
			throw new RuntimeException("非法的Email地址！");
		}
		//检查Email存在
		Users userByEmail = userDao.findByEmail(email);
		if (userByEmail == null) {
			throw new RuntimeException("该Email未注册！");
		}
		//检查手机号格式
		if (mobile == null || !Pattern.matches("^[0-9]{8,11}$", mobile)) {
			throw new RuntimeException("非法的手机号(须8-11位)！");
		}
		//检查手机号存在
		Users userByMobile = userDao.findByMobile("+" + countryCode + "." + mobile);
		if (userByMobile == null) {
			throw new RuntimeException("该手机号未注册！");
		}
		//检查匹配
		if (userByEmail.getId() != userByMobile.getId()) {
			throw new RuntimeException("Email与手机号不匹配！");
		}
			
		session.setAttribute("smsCode", smsCode);
		session.setAttribute("mobile", "+" + countryCode + "." + mobile);
		session.setAttribute("email", email);
		session.setAttribute("lastSent", new Date());
			
		HelperSms.send(countryCode + mobile, "您正在重设网站账号密码，您的手机验证码是："+smsCode+"，5分钟内有效【"+BackOffice.getInst().COMPANY_NAME+"】");
			
		return true;
	}

	@Override
	public boolean sendEmailRegister(String email, String mobile, String countryCode, HttpSession session) {
		String code = HelperSalty.getRandomNum();
		
		Date now = new Date();
		
		//检查上次发送短信时间
//		Date lastSent = (Date) session.getAttribute("lastSent");
//		if (lastSent != null && now.getTime() - lastSent.getTime() < 120 * 1000) {
//			//120秒内只能发送1次手机验证码
//			throw new RuntimeException("120秒内只能发送1次手机验证码");
//		}
		
		//检查Email格式
		if (!Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", email)) {
			throw new RuntimeException("非法的Email地址！");
		}
		//检查Email存在
		if (userDao.findByEmail(email) != null) {
			throw new RuntimeException("该Email已注册，如果你忘记了密码，可以通过“忘记密码”功能重设密码！");
		}
		//检查手机号格式
		if (mobile == null || !Pattern.matches("^[0-9]{8,11}$", mobile)) {
			throw new RuntimeException("非法的手机号(须8-11位)！");
		}
		//检查手机号存在
		if (userDao.findByMobile(mobile) != null) {
			throw new RuntimeException("该手机号已注册，如果你忘记了密码，可以通过“忘记密码”功能重设密码！");
		}
			
		session.setAttribute("smsCode", code);
		session.setAttribute("mobile", "+" + countryCode + "." + mobile);
		session.setAttribute("email", email);
		session.setAttribute("lastSent", new Date());

		
		PostmanCode postmanCode=new PostmanCode(email);	
		postmanCode.sendCode(code);
		return true;
	}
	
	@Override
	public boolean sendEmailForgotPassword(String email, String mobile, String countryCode, HttpSession session) {
		String code = HelperSalty.getRandomNum();
		
		Date now = new Date();
		
		//检查上次发送短信时间
//		Date lastSent = (Date) session.getAttribute("lastSent");
//		if (lastSent != null && now.getTime() - lastSent.getTime() < 120 * 1000) {
//			//120秒内只能发送1次手机验证码
//			throw new RuntimeException("120秒内只能发送1次手机验证码");
//		}
		
		//检查Email格式
		if (!Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", email)) {
			throw new RuntimeException("非法的Email地址！");
		}
		//检查Email存在
		Users userByEmail = userDao.findByEmail(email);
		if (userByEmail == null) {
			throw new RuntimeException("该Email未注册！");
		}
		//检查手机号格式
		if (mobile == null || !Pattern.matches("^[0-9]{8,11}$", mobile)) {
			throw new RuntimeException("非法的手机号(须8-11位)！");
		}
		//检查手机号存在
		Users userByMobile = userDao.findByMobile("+" + countryCode + "." + mobile);
		if (userByMobile == null) {
			throw new RuntimeException("该手机号未注册！");
		}
		//检查匹配
		if (userByEmail.getId() != userByMobile.getId()) {
			throw new RuntimeException("Email与手机号不匹配！");
		}
			
		session.setAttribute("smsCode", code);
		session.setAttribute("mobile", "+" + countryCode + "." + mobile);
		session.setAttribute("email", email);
		session.setAttribute("lastSent", new Date());
		
		PostmanCode postmanCode=new PostmanCode(email);	
		postmanCode.sendCode(code);
			
		return true;
	}

	@Override
	public void leaveContacts(String name, String company, String email, String mobile, HttpSession session) {
		if (!Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", email)) {
			throw new RuntimeException("非法的Email地址！");
		}
		
		//发邮件
		{
			PostmanIndex postman = new PostmanIndex(settingDao.getString("CSEmail"));
			postman.leaveContacts(name, company, email, mobile);
		}
	}



	@Override
	public boolean leaveContacts1(String userName, String userPhone,String userEmail,String msgContent, HttpSession session){
		if (!Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", userEmail)) {
			throw new RuntimeException("非法的Email地址！");
		}
		PostmanIndex post=new PostmanIndex(settingDao.getString("CSEmail"));
		post.leaveContacts1(userName, userEmail, userPhone, msgContent);
		return true;
	}
}
