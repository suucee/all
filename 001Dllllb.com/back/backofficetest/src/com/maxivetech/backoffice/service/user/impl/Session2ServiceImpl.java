package com.maxivetech.backoffice.service.user.impl;

import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.dao.AdminDao;
import com.maxivetech.backoffice.dao.SettingDao;
import com.maxivetech.backoffice.dao.TokenDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Tokens;
import com.maxivetech.backoffice.entity.UserBalances;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoSession;
import com.maxivetech.backoffice.sendmail.PostmanCode;
import com.maxivetech.backoffice.sendmail.PostmanUser;
import com.maxivetech.backoffice.service.user.Session2Service;
import com.maxivetech.backoffice.sms.HelperSms;
import com.maxivetech.backoffice.util.HelperPassword;
import com.maxivetech.backoffice.util.HelperRandomCode;
import com.maxivetech.backoffice.util.HelperSalty;

@Service
@Transactional
public class Session2ServiceImpl implements Session2Service {
	public static final String SESSION_KEY = "logined";
	public static final String SESSION_USER_KEY = "loginedUser";
	public static final String SESSION_ADMIN_KEY = "loginedAdmin";
	
	
	
	@Autowired
	private TokenDao tokenDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserProfilesDao userProfilesDao;
	@Autowired
	private SettingDao settingDao;
	@Autowired
	private AdminDao adminDao;
	
	@Override
	public boolean register( String mobile, String smsCode, String password, String countryCode, String referralCode, HttpSession session) {
		
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
			Date _lastSent = (Date)session.getAttribute("lastSent");
			if (_smsCode == null || !smsCode.equals(_smsCode) ||
				_mobile == null || !("+" + countryCode + "." + mobile).equals(_mobile)){
				throw new RuntimeException("验证码不正确.");
			}
			if(_lastSent == null || (new Date()).getTime() - _lastSent.getTime() > 300 * 1000) {
				throw new RuntimeException("验证码不正确（可能已经超时）！");
			}
		}
		//*/
		
		Users parentUser = null;
		if (referralCode != null && !referralCode.equals("")) {
			//检查推荐码格式
			if (!Pattern.matches("^[0-9a-zA-Z]{5}$", referralCode)) {
				throw new RuntimeException("推荐码格式不正确！");
			}
			//检查推荐码存在
			parentUser = userDao.findByUserCode(referralCode);
			if (parentUser == null ||
				!BackOffice.getInst().companyHook.isReferralCodeAvailable(parentUser.getVipGrade(), parentUser.getLevel())) {
				throw new RuntimeException("无效的推荐码。");
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
		
		user.setEmail("");
		user.setPaymentPassword("");// 支付密码
		
		user.setMobile("+" + countryCode + "." + mobile);
		user.setPassword(HelperPassword.beforeSave(password, salty));// 密码
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
			profile.setUserName("");
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
	public boolean sendSms4Register(String mobile, String countryCode, HttpSession session) {
		String smsCode = HelperSalty.getRandomNum();
		
		Date now = new Date();
		
		//检查上次发送短信时间
		Date lastSent = (Date) session.getAttribute("lastSent");
		if (lastSent != null && now.getTime() - lastSent.getTime() < 120 * 1000) {
//			120秒内只能发送1次手机验证码
			throw new RuntimeException("120秒内只能发送1次手机验证码");
		}
		//检查手机号格式
		if (mobile == null || !Pattern.matches("^[0-9]{8,11}$", mobile)) {
			throw new RuntimeException("非法的手机号(须8-11位)！");
		}
		//检查手机号存在
		if (userDao.findByMobile("+" + countryCode + "." + mobile) != null) {
			throw new RuntimeException("该手机号已注册，如果你忘记了密码，可以通过“忘记密码”功能重设密码！");
		}
			
		session.setAttribute("smsCode", smsCode);
		session.setAttribute("mobile", "+" + countryCode + "." + mobile);
		session.setAttribute("lastSent", new Date());
			
		HelperSms.send(countryCode + mobile, "欢迎您注册"+BackOffice.getInst().COMPANY_NAME+"，您的手机验证码是："+smsCode+"，5分钟内有效【"+BackOffice.getInst().COMPANY_NAME+"】");		
		return true;
	}



	@Override
	public boolean sendEmailRegister(String email, HttpSession session) {
		
		Date _lastSent = (Date)session.getAttribute("lastSent");
		if (_lastSent != null && new Date().getTime() - _lastSent.getTime() < 120 * 1000) {
			//120秒内只能发送1次邮箱验证码
			throw new RuntimeException("120秒内只能发送1次邮箱验证码");
		}
		
		//检查Email格式
		if (!Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", email)) {
			throw new RuntimeException("电子邮箱地址格式不正确！");
		}
		//检查Email存在
		if (userDao.findByEmail(email) != null) {
			throw new RuntimeException("该电子邮箱已注册，如果你忘记了密码，可以通过“忘记密码”功能重设密码！");
		}
			
		String code = HelperSalty.getRandomNum();
		session.setAttribute("emailCode", code);
		session.setAttribute("email", email);
		session.setAttribute("lastSent", new Date());

		
		PostmanCode postmanCode=new PostmanCode(email);	
		postmanCode.sendCode(code);
		return true;
	}



	@Override
	public boolean bindEmail(int userId , String email,String emailCode,  HttpSession session) {
		email = email.toLowerCase();
		Users user = null;
		if(userId > 0){
			try{
				user = (Users) userDao.getById(userId);
				if(user == null){
					throw new RuntimeException("对不起，没有找到对应的用户！");
				}
			}catch (Exception e)
			{
				
			}
		}else{
			user=SessionServiceImpl.getCurrentUser(session);
		}
		
		//检查Email格式
		if (!Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", email)) {
			throw new RuntimeException("电子邮箱地址格式不正确！");
		}
		
		if(adminDao.findByAccount(email) != null || userDao.findByEmail(email) != null){
			throw new RuntimeException("已经有用户绑定了该邮箱了。");
		}
		
		
		
		if(user.getEmail() == null || user.getEmail().equals("")){
			String _emailCode = (String)session.getAttribute("emailCode");
			Date _lastSent = (Date)session.getAttribute("lastSent");
			if (_emailCode == null || !_emailCode.equals(emailCode)){
				throw new RuntimeException("不正确的验证码！");
			}
			if(_lastSent == null || (new Date()).getTime() - _lastSent.getTime() > 300*1000) {
				throw new RuntimeException("不正确的验证码（可能已经超时）！");
			}else{
				user.setEmail(email);
				userDao.saveOrUpdate(user);
				userDao.commit();
				return true;
			}
		}else{
			throw new RuntimeException("已经绑定了邮箱："+user.getEmail());
		}
	}



	/**
	 * 通过此方式找回密码的可以是管理员，也可以是用户
	 */
	@Override
	public boolean sendEmail4ForgotPassword(String email, HttpSession session) {
		Date _lastSent = (Date)session.getAttribute("lastSent");
		if (_lastSent != null && new Date().getTime() - _lastSent.getTime() < 120 * 1000) {
			//120秒内只能发送1次邮箱验证码
			throw new RuntimeException("120秒内只能发送1次邮箱验证码");
		}
		
		//检查Email格式
		if (!Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", email)) {
			throw new RuntimeException("电子邮箱地址格式不正确！");
		}
		//检查Email存在
		if (userDao.findByEmail(email) == null && adminDao.findByAccount(email) == null) {
			throw new RuntimeException("对不起，该电子邮箱还未绑定任何账户！");
		}
			
		String code = HelperSalty.getRandomNum();
		session.setAttribute("emailCode", code);
		session.setAttribute("email", email);
		session.setAttribute("lastSent", new Date());
		
		PostmanCode postmanCode=new PostmanCode(email);	
		postmanCode.sendCode(code);
		return true;
	}



	@Override
	public boolean sendSms4ForgotPassword(String mobile, String countryCode, HttpSession session) {
		String smsCode = HelperSalty.getRandomNum();
		
		Date now = new Date();
		
		//检查上次发送短信时间
		Date lastSent = (Date) session.getAttribute("lastSent");
		if (lastSent != null && now.getTime() - lastSent.getTime() < 120 * 1000) {
//			120秒内只能发送1次手机验证码
			throw new RuntimeException("120秒内只能发送1次手机验证码");
		}
		//检查手机号格式
		if (mobile == null || !Pattern.matches("^[0-9]{8,11}$", mobile)) {
			throw new RuntimeException("手机号格式不正确(须8-11位)！");
		}
		//检查手机号存在
		if (userDao.findByMobile("+" + countryCode + "." + mobile) == null) {
			throw new RuntimeException("对不起，该手机号还未注册。");
		}
			
		session.setAttribute("smsCode", smsCode);
		session.setAttribute("mobile", "+" + countryCode + "." + mobile);
		session.setAttribute("lastSent", new Date());
			
		HelperSms.send(countryCode + mobile, "您正在重设网站账号密码，您的手机验证码是："+smsCode+"，5分钟内有效【"+BackOffice.getInst().COMPANY_NAME+"】");
		return true;
	}


	@Override
	public String checkCode(String email,String mobile,String countryCode, String code, HttpSession session) {
		String userOrAdmin = null;//"USER / ADMIN"
		//检查上次发送时间
		Date _lastSent = (Date) session.getAttribute("lastSent");
		
		String _smsCode = (String)session.getAttribute("smsCode");
		String _mobile = (String)session.getAttribute("mobile");
		String _emailCode = (String)session.getAttribute("emailCode");
		String _email = (String)session.getAttribute("email");
		
		//通过手机验证码方式
		if(email == null && mobile.length() >0 && countryCode.length() > 0 && code.length()>0){//手机号码
			boolean matchesMobile = Pattern.matches("^[0-9]{8,11}$", mobile);
			if(!matchesMobile){
				throw new RuntimeException("手机号格式不正确(须8-11位)！");
			}else{
				userOrAdmin = "USER";
				//检查手机号存在
				if (userDao.findByMobile("+" + countryCode + "." + mobile) == null) {
					throw new RuntimeException("对不起，该手机号还未注册。");
				}else{
					if (_smsCode == null || !code.equals(_smsCode) || !("+" + countryCode + "." + mobile).equals(_mobile)){
						throw new RuntimeException("验证码不正确.");
					}
					if(_lastSent == null || (new Date()).getTime() - _lastSent.getTime() > 300 * 1000) {
						throw new RuntimeException("验证码不正确（可能已经超时）！");
					}
					return userOrAdmin;
				}
			}
			
		}
		
		//通过邮箱验证码方式
		if(mobile == null && countryCode == null && email.length() > 0 && code.length()>0){//邮箱
			email = email.toLowerCase();
			boolean matchesEmail = Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", email);
			if(!matchesEmail){
				throw new RuntimeException("电子邮箱地址格式不正确！");
			}else{
				//检查Email存在
				if (userDao.findByEmail(email) == null) {
					
					if(adminDao.findByAccount(email) == null){
						throw new RuntimeException("对不起，该电子邮箱还未绑定任何账户！");
					}else{
						userOrAdmin = "ADMIN";
					}
					
				}else{
					userOrAdmin = "USER";
				}
				
				if (_email == null || !code.equals(_emailCode) || !_email.equals(email)){
					throw new RuntimeException("验证码不正确.");
				}
				if(_lastSent == null || (new Date()).getTime() - _lastSent.getTime() > 300 * 1000) {
					throw new RuntimeException("验证码不正确（可能已经超时）！");
				}
				return userOrAdmin;
			
			}
		}
		return userOrAdmin;
		
	}

	
	@Override
	public String checkCode4Unlock(String email,String mobile,String countryCode, String code, HttpSession session) {
		String userOrAdmin = null;//"USER / ADMIN"
		//检查上次发送时间
		Date _lastSent = (Date) session.getAttribute("lastSent");
		
		String _smsCode = (String)session.getAttribute("smsCode");
		String _mobile = (String)session.getAttribute("mobile");
		String _emailCode = (String)session.getAttribute("emailCode");
		String _email = (String)session.getAttribute("email");
		
		//通过手机验证码方式
		if(email == null && mobile.length() >0 && countryCode.length() > 0 && code.length()>0){//手机号码
			boolean matchesMobile = Pattern.matches("^[0-9]{8,11}$", mobile);
			if(!matchesMobile){
				throw new RuntimeException("手机号格式不正确(须8-11位)！");
			}else{
				userOrAdmin = "USER";
				//检查手机号存在
				Users user = userDao.findByMobile("+" + countryCode + "." + mobile);
				if (user == null) {
					throw new RuntimeException("对不起，该手机号还未注册。");
				}else{
					if (_smsCode == null || !code.equals(_smsCode) || !("+" + countryCode + "." + mobile).equals(_mobile)){
						throw new RuntimeException("验证码不正确.");
					}
					if(_lastSent == null || (new Date()).getTime() - _lastSent.getTime() > 300 * 1000) {
						throw new RuntimeException("验证码不正确（可能已经超时）！");
					}
					
					user.setPwdLocked(false);
					user.setPwdErrorCount(0);
					userDao.saveOrUpdate(user);
					userDao.commit();
					
					return userOrAdmin;
				}
			}
			
		}
		
		//通过邮箱验证码方式
		if(mobile == null && countryCode == null && email.length() > 0 && code.length()>0){//邮箱
			email = email.toLowerCase();
			boolean matchesEmail = Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", email);
			if(!matchesEmail){
				throw new RuntimeException("电子邮箱地址格式不正确！");
			}else{
				Users user = userDao.findByEmail(email);
				//检查Email存在
				if (user == null) {
					if(adminDao.findByAccount(email) == null){
						throw new RuntimeException("对不起，该电子邮箱还未绑定任何账户！");
					}else{
						userOrAdmin = "ADMIN";
					}
					
				}else{
					userOrAdmin = "USER";
				}
				
				if (_email == null || !code.equals(_emailCode) || !_email.equals(email)){
					throw new RuntimeException("验证码不正确.");
				}
				if(_lastSent == null || (new Date()).getTime() - _lastSent.getTime() > 300 * 1000) {
					throw new RuntimeException("验证码不正确（可能已经超时）！");
				}
				
				
				user.setPwdLocked(false);
				user.setPwdErrorCount(0);
				userDao.saveOrUpdate(user);
				userDao.commit();
				return userOrAdmin;
			
			}
		}
		return userOrAdmin;
		
	}
	
	


	@Override
	public boolean resetPassword(String account, String countryCode, String password, String password2, HttpSession session) {
//		这部分放在前台验证（用js）
//		if (password == null || password.length() < 6) {
//			throw new RuntimeException("登录密码不能少于6位！");
//		}
//		if (password2 == null || password2.length() < 6) {
//			throw new RuntimeException("支付密码不能少于6位！");
//		}
		
		Users user = null;
		Admins admin = null;
		
		account = account.toLowerCase();
		
		//查用户
		if (Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", account)) {
			// 根据Email查
			user = userDao.findByEmail(account);
			
		} else if(countryCode != null){
			// 根据手机号查
			user = userDao.findByMobile("+" + countryCode + "." + account);
		}
		
		
		//不是用户，查admin
		if (user == null) {
			// 根据账号查
			admin = adminDao.findByAccount(account);
		}
		
		if (user != null ){
			//用户
			user.setPassword(HelperPassword.beforeSave(password, user.getSalty()));//登录密码
			user.setPaymentPassword(HelperPassword.beforeSave(password2, user.getSalty()));//支付密码
			
			userDao.update(user);
			userDao.commit();
	
			session.removeAttribute("mobile");
			session.removeAttribute("smsCode");
			session.removeAttribute("email");
			session.removeAttribute("lastSent");
			PostmanUser postman = new PostmanUser(user.getEmail());
			postman.userResetPassword(user.getEmail());
			return true;
		}else if(admin != null){
			//管理员
			admin.setPassword(HelperPassword.beforeSave(password, admin.getSalty()));//登录密码
			admin.setOperationPassword(HelperPassword.beforeSave(password2, admin.getSalty()));//操作密码
			
			adminDao.update(admin);
			adminDao.commit();
	
			session.removeAttribute("mobile");
			session.removeAttribute("smsCode");
			session.removeAttribute("email");
			session.removeAttribute("lastSent");
			PostmanUser postman = new PostmanUser(admin.getAccount());
			postman.userResetPassword(admin.getAccount());
			return true;
			
		}else{
			throw new RuntimeException("无法根据指定内容获取用户.");
		}
	}

	@Override
	public boolean sendSmsApply(String hasNo,String email, String mobile, String countryCode, HttpSession session) {
		String smsCode = HelperSalty.getRandomNum();
		//smsCode="666666";
		Date now = new Date();
		
		//检查上次发送短信时间
		Date lastSent = (Date) session.getAttribute("lastSent");
		if (lastSent != null && now.getTime() - lastSent.getTime() < 120 * 1000) {
			//120秒内只能发送1次手机验证码
			throw new RuntimeException("120秒内只能发送1次手机验证码");
		}
		if(hasNo.equals("no")){
			//检查Email格式
			if (!Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", email)) {
				throw new RuntimeException("非法的Email地址！");
			}
			//检查Email存在
			Users userByEmail = userDao.findByEmail(email);
			if (userByEmail != null) {
				throw new RuntimeException("该Email已注册！");
			}
			//检查手机号格式
			if (mobile == null || !Pattern.matches("^[0-9]{8,11}$", mobile)) {
				throw new RuntimeException("非法的手机号(须8-11位)！");
			}
			//检查手机号存在
			Users userByMobile = userDao.findByMobile("+" + countryCode + "." + mobile);
			if (userByMobile != null) {
				throw new RuntimeException("该手机号已注册！");
			}
			session.setAttribute("smsCode", smsCode);
			session.setAttribute("mobile", "+" + countryCode + "." + mobile);
			session.setAttribute("email", email);
			session.setAttribute("lastSent", new Date());	
			
		}else{
			//检查手机号格式
			if (mobile == null || !Pattern.matches("^[0-9]{8,11}$", mobile)) {
				throw new RuntimeException("非法的手机号(须8-11位)！");
			}

			session.setAttribute("smsCode", smsCode);
			session.setAttribute("mobile", "+" + countryCode + "." + mobile);
			session.setAttribute("lastSent", new Date());	
		}	
		HelperSms.send(countryCode + mobile, "您正在申请成为经纪人，您的手机验证码是："+smsCode+"，5分钟内有效【"+BackOffice.getInst().COMPANY_NAME+"】");
		return true;
	}

	@Override
	public boolean registerApply(String hasNo,String email, String mobile, String smsCode, 
			String password, String name, String countryCode,  HttpSession session) {
		if(hasNo.equals("no")){
		String referralCode="";
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
		user.setPaymentPassword("");// 支付密码
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
		}else{
			//检查短信验证码
			if (smsCode == null || !Pattern.matches("^[0-9]{6}$", smsCode)) {
				throw new RuntimeException("非法的手机验证码！");
			}
			//*检查短信验证码和手机号
			{
				String _smsCode = (String)session.getAttribute("smsCode");
				String _mobile = (String)session.getAttribute("mobile");
				Date _lastSent = (Date)session.getAttribute("lastSent");
				if (_smsCode == null || !smsCode.equals(_smsCode) ||
					_mobile == null || !("+" + countryCode + "." + mobile).equals(_mobile) ||
					_lastSent == null || (new Date()).getTime() - _lastSent.getTime() > 300 * 1000) {
					throw new RuntimeException("非法的验证码（可能已经超时）！");
				}
			}
			//检查手机号格式
			if (mobile == null || !Pattern.matches("^[0-9]{8,11}$", mobile)) {
				throw new RuntimeException("非法的手机号(须8-11位)！");
			}
			Users user=userDao.findByMobile("+" + countryCode + "." + mobile);
			//检查手机号存在
			if (user == null) {
				throw new RuntimeException("该用户不存在，请检查你填写的手机号码是否正确！");
			}
			userDao.saveOrUpdate(user);
			userDao.commit();
		}
		session.removeAttribute("smsCode");
		session.removeAttribute("mobile");
		session.removeAttribute("lastSent");
		return true;
	}

	@Override
	public PojoSession logout(HttpSession session) {
		PojoSession pojoOld = SessionServiceImpl.getCurrent(session);
		if (pojoOld != null && pojoOld.getHoldAdminId() > 0) {
			//代管返登
			
			// 管理员登录成功
			Admins admin = (Admins)adminDao.getById(pojoOld.getHoldAdminId());
			
			PojoSession pojo = new PojoSession(admin);
//			pojo.setRedirectUrl(pojoOld.getRedirectUrl());
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
	public PojoSession loginPwdError(String account, String password, String countryCode, HttpSession session) {
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

		int PWD_ERROR_DURATION_MS = BackOffice.getInst().PWD_ERROR_DURATION_MS;
		int PWD_ERROR_COUNT = BackOffice.getInst().PWD_ERROR_COUNT;
		int PWD_ERROR_TRIGGER = BackOffice.getInst().PWD_ERROR_TRIGGER;
		
		if (user != null){
			int hours = PWD_ERROR_DURATION_MS / 1000 / 60 / 60;
			if(user.isPwdLocked()){
				throw new RuntimeException("抱歉，您在规定的时间("+hours+"小时)内累计输错"+PWD_ERROR_COUNT+"次密码，账户已经被锁定，请前往自行解除锁定。");
			}
			if(user.getPwdFirstErrorTime() == null){
				user.setPwdFirstErrorTime(new Date());
			}
			//当前 - 第一次 > 一天  ======>过了一天，次数计0
			if(new Date().getTime() - user.getPwdFirstErrorTime().getTime() > PWD_ERROR_DURATION_MS){
				user.setPwdErrorCount(0);
			}
			
			
			// 验证User账号和密码
			//密码错误
			if(! HelperPassword.verifyPassword(password, user.getPassword(), user.getSalty())) {
				user.setPwdErrorCount(user.getPwdErrorCount() + 1);
				
				if(user.getPwdErrorCount() == 1){//第一次输错
					user.setPwdFirstErrorTime(new Date());
				}else if(user.getPwdErrorCount() == PWD_ERROR_COUNT){//到规定次数
					user.setPwdLocked(true);
					userDao.saveOrUpdate(user);
					userDao.commit();
					throw new RuntimeException("抱歉，您在规定的时间("+hours+"小时)内累计输错"+PWD_ERROR_COUNT+"次密码，账户已经被锁定无法登陆，请自行前往解除锁定。");
				}
				if(user.getPwdErrorCount() > PWD_ERROR_TRIGGER)//不能一直提醒，大于PWD_ERROR_TRIGGER次就提醒
				{
					userDao.saveOrUpdate(user);
					userDao.commit();
					throw new RuntimeException("提示：在规定的时间("+hours+"小时)内您已累计输错密码"+user.getPwdErrorCount()+"次。 如果累计输错"+PWD_ERROR_COUNT+"次密码，账户将被锁定。");
				}
				
				userDao.saveOrUpdate(user);
				userDao.commit();
			}
			else{
	
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
			}
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
	
	
	
}
