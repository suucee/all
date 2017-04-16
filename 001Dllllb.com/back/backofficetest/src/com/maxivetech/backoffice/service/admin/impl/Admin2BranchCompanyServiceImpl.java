package com.maxivetech.backoffice.service.admin.impl;

import java.util.Date;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.UserBankAccountDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.entity.UserBalances;
import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.service.admin.Admin2BranchCompanyService;
import com.maxivetech.backoffice.util.HelperPassword;
import com.maxivetech.backoffice.util.HelperRandomCode;
import com.maxivetech.backoffice.util.HelperSalty;

@Service
public class Admin2BranchCompanyServiceImpl implements Admin2BranchCompanyService {

	@Autowired
	private UserDao userDao;
	@Autowired
	private UserProfilesDao userProfilesDao;
	@Autowired
	private UserBankAccountDao userBankAccountDao;
	
	@CheckRole(role = {Role.OperationsManager})
	@Override
	public boolean addStaff(String name,String countryCode,String mobile,String password, HttpSession session) {

		Users user = new Users();

		//检查手机号格式
		if (mobile == null || !Pattern.matches("^[0-9]{8,11}$", mobile)) {
			throw new RuntimeException("非法的手机号(须8-11位)！");
		}
		//检查手机号存在
		{
			if (userDao.findByMobile("+" + countryCode + "." + mobile) != null ) {
				throw new RuntimeException("该手机号已注册了！");
			}
		}
		//检查密码长度
		if ( password != null && password.length() < 6) {
			throw new RuntimeException("密码不能少于6位！");
		}

		Date now = new Date();
				
		
		
		//创建User
		user.setState("AUDITING");
		user.setEmail("");
		user.setMobile("+" + countryCode + "." + mobile);
		String salty = HelperSalty.getCharAndNumr(6);
		Users parentUser = (Users) userDao.getById(1);
		user.setParent(parentUser);// 设置上线
		user.setSerialNumber(0);
		user.setFrozen(false);
		user.setSalty(salty);
		user.setPassword(HelperPassword.beforeSave(password, user.getSalty()));// 密码
		user.setPaymentPassword("");// 操作密码
		user.setRegistrationTime(now);
		user.setLastLogonTime(now);
		if(parentUser == null){
			throw new RuntimeException("请在数据库users表中设置基础数据（ID=1,LEVEL=1）后再继续添加员工。");
		}else{
			user.setLevel(parentUser.getLevel() + 1);
		}
		
		//生成推荐码
		{
			String code = "";
			do {
				code = HelperRandomCode.getARandomCode(5);
			} while (userDao.findByUserCode(code) != null);
					
			//XXX:推荐码用完了会死循环
			user.setReferralCode(code);
		}
		user.setPath("");
		userDao.save(user);
		//设置Path
		user.setPath(parentUser.getPath() + user.getId() + ",");
		user.setPassword(HelperPassword.beforeSave(password, user.getSalty()));// 密码
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
				
		//创建网站资金账户
			UserBalances ub = new UserBalances();
			ub.setAmountAvailable(0);
			ub.setAmountFrozen(0);
			ub.setCurrencyType("USD");
			ub.setUpdatedTime(now);
			ub.setUser(user);
			
			userDao.save(ub);
		
		//创建银行卡
		{
			UserBankAccounts accounts = new UserBankAccounts();
			accounts.setAccountName("");// 
			accounts.setAccountNo("");// 银行卡号
			accounts.setBankAddress("");
			accounts.setBankBranch("");
			accounts.setBankName("");
			accounts.setCountryCode("");
			accounts.setCurrencyType("");
			accounts.setIntermediaryBankAddress("");
			accounts.setIntermediaryBankBicSwiftCode("");
			accounts.setIntermediaryBankBranch("");
			accounts.setIntermediaryBankName("");
			accounts.setSortNum(0);
			accounts.setDefault(true);// 是否是绑定的银行卡。true
			accounts.setSwiftCode("");
			accounts.setBankAddress("");
			accounts.setBankBranch("");
			accounts.setIbanCode("");
			accounts.setUpdateTime(new Date());
			accounts.setUser(user);
			
			userBankAccountDao.save(accounts);
		}
		
		userDao.commit();

		return true;
	}
	
	

}

