package com.maxivetech.backoffice.service.admin.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.UserBankAccountDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.entity.Attachments;
import com.maxivetech.backoffice.entity.UserBalances;
import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.service.admin.AdminBranchCompanyService;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperPassword;
import com.maxivetech.backoffice.util.HelperRandomCode;
import com.maxivetech.backoffice.util.HelperSalty;
import com.maxivetech.backoffice.util.Page;

@Service
public class AdminBranchCompanyServiceImpl implements AdminBranchCompanyService {

	@Autowired
	private UserDao userDao;
	@Autowired
	private UserProfilesDao userProfilesDao;
	@Autowired
	private UserBankAccountDao userBankAccountDao;
	

	@CheckRole(role = {Role.OperationsManager})
	@Override
	public Page<HashMap<String,Object>> getAllBranchCompany(int pageNo, int pageSize, String urlFormat,
			HttpSession session) {
		
		StringBuilder sqlBuilder=new StringBuilder("select");
		sqlBuilder.append(" u.id as u_id,");
		sqlBuilder.append(" u.state as u_state,");
		sqlBuilder.append(" u.vip_grade as u_vip_grade,");
		sqlBuilder.append(" u.email as u_email,");
		sqlBuilder.append(" u.mobile as u_mobile,");
		sqlBuilder.append(" up.name as u_name,");
		sqlBuilder.append(" ub.account_name as u_bank_id_name,");
		sqlBuilder.append(" ub.account_no as u_bank_id,");
		sqlBuilder.append(" ub.bank_name as u_bank_name");
		sqlBuilder.append(" from users u");
		sqlBuilder.append(" left join (select  * from  (select  MAX(id) as mid from user__profiles ut group by ut.user_id) um left join user__profiles usp on usp.id=um.mid)  up on up.user_id=u.id");
		sqlBuilder.append(" left join (select  * from  (select  MAX(id) as mid from user__bank_accounts ut group by ut.user_id) um left join user__bank_accounts usp on usp.id=um.mid)  ub on ub.user_id=u.id");
		sqlBuilder.append(" where level in (2,3)");
		String sqlstr=sqlBuilder.toString();
		Page<HashMap<String,Object>> userarr=userDao.getHashPageBySQL(sqlstr, pageSize, pageNo, urlFormat);
		userarr.generateButtons(urlFormat);
		return userarr;
	}
	


	@CheckRole(role = {Role.OperationsManager})
	@Override
	public boolean saveOrUpdate(int id, String companyName, String mobile,
			String email, String password, String bankName,
			String bankAccountName, String accountNo, HttpSession session) {


		Users user = null;
		if (id > 0) {
			user = (Users) userDao.getById(id);
			if (user.getLevel() != 2 && user.getLevel() != 3) {
				throw new RuntimeException("操作的用户有误！");
			}
			if (user == null) {
				throw new RuntimeException("未找到用户！");
			}
		} else {
			user = new Users();
		}
		
		//小写化
		email = email.toLowerCase();

		//检查Email格式
		if (!Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", email)) {
			throw new RuntimeException("非法的Email地址！");
		}
		//检查Email存在
		{
			Users userByEmail = userDao.findByEmail(email);
			if (userByEmail != null && 
					userByEmail.getId() != user.getId()) {
				throw new RuntimeException("该Email已注册！");
			}
		}
				
		//检查手机号格式
		if (mobile == null || !Pattern.matches("^[0-9]{8,11}$", mobile)) {
			throw new RuntimeException("非法的手机号(须8-11位)！");
		}
		//检查手机号存在
		{
			Users userByMobile = userDao.findByMobile(mobile);
			if (userByMobile != null && 
				userByMobile.getId() != user.getId()) {
				throw new RuntimeException("该手机号已注册！");
			}
		}

		//检查密码长度
		if (id > 0 
			? password.length() != 0 && password.length() < 6 
			: password.length() < 6) {
			throw new RuntimeException("密码不能少于6位！");
		}

		Date now = new Date();
				
		//创建User
		user.setState("AUDITING");
		user.setEmail(email);
		user.setMobile(mobile);
		
		if (id == 0) {
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
			user.setLevel(parentUser.getLevel() + 1);
			
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
			userDao.saveOrUpdate(user);
			//设置Path
			user.setPath(parentUser.getPath() + user.getId() + ",");
		} else {
			
		}
		
		if (password.length() > 6) {
			user.setPassword(HelperPassword.beforeSave(password, user.getSalty()));// 密码
		}
		userDao.update(user);
		
				
		//创建UserProfile
		{
			UserProfiles profile = new UserProfiles();
			if (id > 0) {
				profile = userProfilesDao.findUserProfilesByUser(user);
				if (profile == null) {
					profile = new UserProfiles();
				}
			} else {
				profile = new UserProfiles();
			}
			
			profile.setUser(user);
			profile.setCardType("");
			profile.setCompany(companyName);
			profile.setCreatTime(now);
			profile.setPosition("");
			profile.setUpdatedTime(now);
			profile.setUserComment("");
			profile.setUserEName("");
			profile.setUserEsidentialAddress("");
			profile.setUserIdCard("");
			profile.setUserIndustry("");
			profile.setUserName(companyName);
			profile.setUserNationality("");
			profile.setUserYearsIncom("");
					
			userProfilesDao.saveOrUpdate(profile);
		}
				
		//创建网站资金账户
		if (id == 0)
		{
			
			UserBalances ub = new UserBalances();
			ub.setAmountAvailable(0);
			ub.setAmountFrozen(0);
			ub.setCurrencyType("USD");
			ub.setUpdatedTime(now);
			ub.setUser(user);
			
			userDao.saveOrUpdate(ub);
		}
		
		//创建银行卡
		{
			UserBankAccounts accounts = null;
			if (id > 0) {
				accounts = userBankAccountDao.getBank(user);
				if (accounts == null) {
					accounts = new UserBankAccounts();
				}
			} else {
				accounts = new UserBankAccounts();
			}
			
			accounts.setAccountName(bankAccountName);// 
			accounts.setAccountNo(accountNo);// 银行卡号
			accounts.setBankAddress("");
			accounts.setBankBranch("");
			accounts.setBankName(bankName);
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
			
			userBankAccountDao.saveOrUpdate(accounts);
		}
		
		userDao.commit();

		return true;
	}
	
	


	@CheckRole(role = {Role.OperationsManager})
	@Override
	public HashMap<String, Object> getOneBranchCompany(int id, HttpSession session) {

		StringBuilder sqlBuilder=new StringBuilder("select");
		sqlBuilder.append(" u.id as u_id,");
		sqlBuilder.append(" u.state as u_state,");
		sqlBuilder.append(" u.vip_grade as u_vip_grade,");
		sqlBuilder.append(" u.level as u_level,");
		sqlBuilder.append(" u.email as u_email,");
		sqlBuilder.append(" u.mobile as u_mobile,");
		sqlBuilder.append(" up.name as u_name,");
		sqlBuilder.append(" ub.account_name as u_bank_id_name,");
		sqlBuilder.append(" ub.account_no as u_bank_id,");
		sqlBuilder.append(" ub.bank_name as u_bank_name");
		sqlBuilder.append(" from users u");
		sqlBuilder.append(" left join (select  * from  (select  MAX(id) as mid from user__profiles ut group by ut.user_id) um left join user__profiles usp on usp.id=um.mid)  up on up.user_id=u.id");
		sqlBuilder.append(" left join (select  * from  (select  MAX(id) as mid from user__bank_accounts ut group by ut.user_id) um left join user__bank_accounts usp on usp.id=um.mid)  ub on ub.user_id=u.id");
		sqlBuilder.append(" where level IN(2, 3) and u.id="+id);
		String sqlstr=sqlBuilder.toString();
		HashMap<String,Object> result=userDao.getOneUserBySQL(sqlstr);
		
		if (result != null) {
			result.put("_vipGradeName", BackOffice.getInst().companyHook.getVipGradeShowName((int)result.get("u_vip_grade"), (int)result.get("u_level")));
		}
		
		return result;
	}
}

