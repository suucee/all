package com.maxivetech.backoffice.service.user.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.AdminDao;
import com.maxivetech.backoffice.dao.AttachmentDao;
import com.maxivetech.backoffice.dao.EmailValidationDao;
import com.maxivetech.backoffice.dao.SettingDao;
import com.maxivetech.backoffice.dao.UserBankAccountDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Attachments;
import com.maxivetech.backoffice.entity.EmailValidation;
import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoSession;
import com.maxivetech.backoffice.sendmail.HelperSendmail;
import com.maxivetech.backoffice.service.admin.AdminUserService;
import com.maxivetech.backoffice.service.user.SessionService;
import com.maxivetech.backoffice.service.user.UserService;
import com.maxivetech.backoffice.sms.HelperSms;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperPassword;
import com.maxivetech.backoffice.util.HelperSalty;
import com.maxivetech.backoffice.util.Page;
import com.mchange.v2.resourcepool.TimeoutException;


@Service
@Transactional
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDao userDao;
	@Autowired
	private AdminDao adminDao;
	@Autowired
	private AttachmentDao attachmentDao;
	@Autowired
	private UserProfilesDao userProfilesDao;
	@Autowired
	private UserBankAccountDao userBankAccountDao;
	@Autowired
	private EmailValidationDao emailValidationDao;
	@Autowired
	private SettingDao settingDao;

	@Override
	public boolean modifyPassword(String oldPassword, String newPassword,
			HttpSession session) {
		// 检查参数
		if (oldPassword == null || oldPassword.equals("")) {
			return false;
		}
		if (newPassword == null || newPassword.equals("")) {
			return false;
		}

		// 所有人都能修改自己密码，不验证角色了
		if (HelperAuthority.isUser(session)) {
			Users user = (Users) userDao
					.getById(HelperAuthority.getId(session));

			if (user != null
					&& HelperPassword.verifyPassword(oldPassword,
							user.getPassword(), user.getSalty())) {
				//验证新登录密码与支付密码是否一致
				if(HelperPassword.verifyPaymentPassword(newPassword, user)){
					throw new RuntimeCryptoException("登录密码不能与支付密码一致！");
				}
				//验证新登录密码与旧密码是否一致
				if(HelperPassword.verifyPassword(newPassword, user.getPassword(), user.getSalty())){
					throw new RuntimeCryptoException("新登录密码不能与旧密码一致！");
				}
				// 设置新密码
				user.setPassword(HelperPassword.beforeSave(newPassword,
						user.getSalty()));

				userDao.update(user);
				userDao.commit();
				return true;
			} 
		} else {
			Admins admin = (Admins) adminDao.getById(HelperAuthority
					.getId(session));

			if (admin != null
					&& HelperPassword.verifyPassword(oldPassword,
							admin.getPassword(), admin.getSalty())) {
				//验证新登录密码与操作密码是否一致
				if(HelperPassword.verifyOperationPassword(newPassword, admin)){
					throw new RuntimeCryptoException("登录密码不能与操作密码一致！");
				}
				//验证新登录密码与旧密码是否一致
				if(HelperPassword.verifyPassword(newPassword, admin.getPassword(), admin.getSalty())){
					throw new RuntimeCryptoException("新登录密码不能与旧密码一致！");
				}
				// 设置新密码
				admin.setPassword(HelperPassword.beforeSave(newPassword,
						admin.getSalty()));

				adminDao.update(admin);
				adminDao.commit();
				return true;
			}
		}

		return false;
	}

	@CheckRole(role = {Role.User})
	@Override
	public boolean removeImg(int id, HttpSession session) {

		Users user=(Users) userDao.getById(SessionServiceImpl.getCurrent(session).getId());
		if(user==null||user.getState().equals("VERIFIED")){
			return false;
		}
		attachmentDao.deleteById(id);
		
		UserProfiles up = userProfilesDao.findUserProfilesByUser(user);
		if (up != null) {
			List<Attachments> att=attachmentDao.getFileImage(UserProfiles.getOwnertype(), up.getId());
			System.out.println(att.size());
			//如果资料图片少于三张，状态改成资料不全,因为还未commit，所以该<4
			if (att.size() < 1 + settingDao.getInt("UserProfileMinImageNum")){
		    	user.setState("UNVERIFIED");
		    	userDao.saveOrUpdate(user);
		    }
		}
		
		attachmentDao.commit();
		
		return true;
	}

	@CheckRole(role = {Role.User})
	@Override
	public List<Attachments> getproFileImage(
			HttpSession session) {

		int userId=SessionServiceImpl.getCurrent(session).getId();
		Users user=(Users) userDao.getById(userId);
		UserProfiles userProfiles=userProfilesDao.findUserProfilesByUser(user);
		if(userProfiles!=null){
			List<Attachments> attach = attachmentDao.getFileImage(
					UserProfiles.getOwnertype(),userProfiles.getId());
			return attach;
		}
		return null;
	}

	@CheckRole(role = {Role.User})
	@Override
	public UserBankAccounts getBank(HttpSession session) {

		Users users = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		UserBankAccounts BankAccounts = userBankAccountDao
				.getBank(users);
		return BankAccounts;
	}
	
	@CheckRole(role = {Role.User})
	@Override
	public List<Attachments> getBankImageList(HttpSession session){
		Users users = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		UserBankAccounts BankAccounts = userBankAccountDao
				.getBank(users);
        List<Attachments> alist=null;
        if(BankAccounts!=null){
              alist=attachmentDao.getFileImage(UserBankAccounts.getOwnertype(), BankAccounts.getId());
        }
		return alist;
	}
	
	@CheckRole(role = {Role.User})
	@Override
	public UserProfiles getProfiles(HttpSession session) {
		Users users = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		UserProfiles profiles = userProfilesDao.findUserProfilesByUser(users);
		return profiles;
	}



	@CheckRole(role = {Role.User})
	@Override
	public String updateProfile(String name, String ename, String cardType,
			String cardID, String countryCode, String address, String company,
			String userIndustry, String userYearsIncom, String position,
			String bankName, String bankNo, String cardholder_Name,
			String countryAdress,String swiftCode,String ibanCode,String bankBranch,String bankAddress,String attachment_id, HttpSession session) {

		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		if (user.isDisable() || user.isFrozen()){
			return "账户已被冻结或禁用";
		}
		// 修改用户基本信息表
		UserProfiles up=userProfilesDao.findUserProfilesByUser(user);
		
		if (up == null){
			up = new UserProfiles();
			up.setUser(user);
			up.setCreatTime(new Date());
		}
		List<Attachments> ac = null;
		
		if (!user.getState().equals("VERIFIED")) {
			ac = attachmentDao.getFileImage(UserProfiles.getOwnertype(), up.getId());
			// 修改用户信息
			if ((ac == null || ac.size() < settingDao.getInt("UserProfileMinImageNum"))) {
				user.setState("UNVERIFIED");
			}
			if ((ac != null && ac.size() >= settingDao.getInt("UserProfileMinImageNum"))) {
				user.setState("AUDITING");
			}
			userDao.saveOrUpdate(user);
			
			up.setCardType(cardType);
			up.setCompany(company);
			up.setCreatTime(new Date());
			up.setPosition(position);
			up.setUpdatedTime(null);
			up.setUser(user);
			up.setUserComment("");
			up.setUserEName(ename);
			up.setUserEsidentialAddress(address);
			up.setUserIdCard(cardID);
			up.setUserIndustry(userIndustry);
			up.setUserName(name);
			up.setUserNationality(countryCode);
			up.setUserYearsIncom(userYearsIncom);
			
			userProfilesDao.saveOrUpdate(up);
			
			// 绑定银行卡信息
			UserBankAccounts uba = userBankAccountDao.getBank(user);
			if (uba == null){
				UserBankAccounts ub = new UserBankAccounts();
				ub.setAccountName(name);
				ub.setAccountNo(bankNo);
				ub.setBankName(bankName);
				ub.setBankAddress("");
				ub.setCurrencyType("USD");
				ub.setCountryCode(countryCode);
				ub.setDefault(true);
				ub.setIntermediaryBankAddress("");
				ub.setIntermediaryBankBicSwiftCode("");
				ub.setIntermediaryBankBranch("");
				ub.setIntermediaryBankName("");
				ub.setSortNum(1);
				ub.setUser(user);
				ub.setIbanCode(ibanCode);
				ub.setSwiftCode(swiftCode);
				ub.setBankBranch(bankBranch);
				ub.setBankAddress(bankAddress);
				ub.setUpdateTime(new Date());
				userBankAccountDao.save(ub);
				if(!attachment_id.equals("")){
				     String[] str=attachment_id.split(",");
					 int c=0;
				     for (int i = 0; i < str.length; i++) {
					     try {
							c=Integer.parseInt(str[i]);
						} catch (Exception e) {
							break;
						}
					    Attachments a=(Attachments) attachmentDao.getById(c);
					    if(a!=null){
					    	a.setOwnerId(ub.getId());
					    	a.setOwnerType(UserBankAccounts.getOwnertype());
					    	a.setUser(ub.getUser());
					    	attachmentDao.saveOrUpdate(a);
					    }
				     }
				 }
			} else {
				uba.setAccountName(name);
				uba.setAccountNo(bankNo);
				uba.setBankName(bankName);
				uba.setCountryCode(countryCode);
				uba.setIbanCode(ibanCode);
				uba.setSwiftCode(swiftCode);
				uba.setBankBranch(bankBranch);
				uba.setBankAddress(bankAddress);
				
				userBankAccountDao.saveOrUpdate(uba);
			}
			
			userBankAccountDao.commit();
			
			return "提交成功";
		}
		
		return "提交失败";
	}

	@CheckRole(role = {Role.User})
	@Override
	public boolean offEmail(HttpSession session) {
		boolean b = false;
		Users users = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		UserProfiles userProfiles = userProfilesDao
				.findUserProfilesByUser(users);
		EmailValidation validation = new EmailValidation();
		String uuid = UUID.randomUUID().toString();
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id", users.getId());
		map.put("email", users.getEmail());
		map.put("uuid", uuid);
		if (userProfiles.getUserEName() != null) {
			map.put("name", userProfiles.getUserEName());
		}
		if (userProfiles.getUserName() != null) {
			map.put("name", userProfiles.getUserName());
		}
		map.put("uuid", uuid);
		if (users != null) {
			try {
				b = HelperSendmail.send("RIMCemail.html", map, users.getEmail(), "");
			} catch (Exception e) {
				// TODO: handle exception
				e.getStackTrace();
			}
			if (b) {
				// 邮件发送成功，设置邮件码，和发送时间（判断是否过期，24小时）
				validation.setEmailNum(uuid);
				validation.setEmailTime(new Date());
				validation.setPassEmailTime(null);
				validation.setState("UNVERIFIED");
				validation.setUser(users);
				
				emailValidationDao.save(validation);
				emailValidationDao.commit();
			}
		}
		
		return b;
	}


	@CheckRole(role = {Role.User})
	@Override
	public String state(HttpSession session) {
		Users users = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		return users.getState();
	}

	/**
	 * 验证邮箱
	 */
	@SuppressWarnings("unused")
	@CheckRole(role = {Role.User})
	@Override
	public boolean checkEmail(HttpSession session) {
		Users users = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		EmailValidation validation = emailValidationDao.findByEmial(users);
		if (validation == null) {
			return true;
		}
		if (validation != null) {
			validation.setUser(null);
			if (validation.getPassEmailTime() == null
					|| validation.getPassEmailTime().equals("")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean modifyOperationPassword(String oldPassword,
			String newPassword, HttpSession session) {
		// 检查参数
		if (oldPassword == null || oldPassword.equals("")) {// 没有密码的时候设置新的密码
			// 所有人都能修改自己密码，不验证角色了
			if (HelperAuthority.isUser(session)) {
				Users user = (Users) userDao.getById(HelperAuthority
						.getId(session));
				//验证新支付密码与旧密码是否一致
				if(HelperPassword.verifyPaymentPassword(newPassword, user)){
					throw new RuntimeCryptoException("新支付密码不能与旧密码一致！");
				}
				//验证新支付与登录密码是否一致
				if(HelperPassword.verifyPassword(newPassword, user.getPassword(), user.getSalty())){
					throw new RuntimeCryptoException("支付密码不能与登录密码一致！");
				}
				if (user != null && !newPassword.equals("")
						&& newPassword != null) {
					// 设置新密码
					user.setPaymentPassword(HelperPassword.beforeSave(
							newPassword, user.getSalty()));

					userDao.update(user);
					userDao.commit();
					return true;
				}
			} else {
				Admins admin = (Admins) adminDao.getById(HelperAuthority
						.getId(session));
				
				if (admin != null && !newPassword.equals("")
						&& newPassword != null) {
					//验证新操作密码与旧密码是否一致
					if(HelperPassword.verifyOperationPassword(newPassword, admin)){
						throw new RuntimeCryptoException("新操作密码不能与旧密码一致！");
					}
					//验证新操作与登录密码是否一致
					if(HelperPassword.verifyPassword(newPassword, admin.getPassword(), admin.getSalty())){
						throw new RuntimeCryptoException("操作密码不能与登录密码一致！");
					}
					// 设置新密码
					admin.setOperationPassword(HelperPassword.beforeSave(
							newPassword, admin.getSalty()));

					adminDao.update(admin);
					adminDao.commit();
					return true;
				}
			}

		}
		if (newPassword == null || newPassword.equals("")) {
			return false;
		}

		// 所有人都能修改自己密码，不验证角色了
		if (HelperAuthority.isUser(session)) {
			Users user = (Users) userDao
					.getById(HelperAuthority.getId(session));

			if (user != null && 
				HelperPassword.verifyPaymentPassword(oldPassword, user)) {
				//验证新支付密码与登录密码是否一致
				if(HelperPassword.verifyPaymentPassword(newPassword, user)){
					throw new RuntimeCryptoException("新支付密码不能与旧密码一致！");
				}
				//验证新支付密码与登录密码是否一致
				if(HelperPassword.verifyPassword(newPassword, user.getPassword(), user.getSalty())){
					throw new RuntimeCryptoException("支付密码不能与登录密码一致！");
				}
				// 设置新密码
				user.setPaymentPassword(HelperPassword.beforeSave(newPassword, user.getSalty()));

				userDao.update(user);
				userDao.commit();
				
				return true;
			}
		} else {
			Admins admin = (Admins) adminDao.getById(HelperAuthority.getId(session));

			if (admin != null
					&& HelperPassword.verifyPassword(oldPassword,
							admin.getOperationPassword(), admin.getSalty())) {
				//验证新操作密码与登录密码是否一致
				if(HelperPassword.verifyOperationPassword(newPassword, admin)){
					throw new RuntimeCryptoException("新操作密码不能与旧密码一致！");
				}
				//验证新操作与旧操作密码是否一致
				if(HelperPassword.verifyPassword(newPassword, admin.getPassword(), admin.getSalty())){
					throw new RuntimeCryptoException("操作密码不能与登录密码一致！");
				}
				// 设置新密码
				admin.setOperationPassword(HelperPassword.beforeSave(
						newPassword, admin.getSalty()));

				adminDao.update(admin);
				adminDao.commit();
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean checkOperationPwd(HttpSession session) {
		if (HelperAuthority.isUser(session)) {// 是用户的情况下
			Users users = (Users) userDao.getById(SessionServiceImpl
					.getCurrent(session).getId());
			if (users.getPaymentPassword() == null
					|| users.getPaymentPassword().equals("")) {
				return false;
			}
		} else {// 管理员的情况下
			Admins admin = (Admins) adminDao.getById(HelperAuthority
					.getId(session));
			if (admin.getOperationPassword() == null
					|| admin.getOperationPassword().equals("")) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean operationPwd(HttpSession session) {
		if (HelperAuthority.isUser(session)) {// 是用户的情况下
			Users users = (Users) userDao.getById(SessionServiceImpl
					.getCurrent(session).getId());
			if (users.getPaymentPassword() == null
					|| users.getPaymentPassword().equals("")) {
				return false;
			}
		} else {// 管理员的情况下
			Admins admin = (Admins) adminDao.getById(HelperAuthority
					.getId(session));
			if (admin.getOperationPassword() == null
					|| admin.getOperationPassword().equals("")) {
				return false;
			}
		}
		return true;
	}
}
