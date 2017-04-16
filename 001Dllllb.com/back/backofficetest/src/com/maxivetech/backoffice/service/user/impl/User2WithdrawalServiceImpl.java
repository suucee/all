package com.maxivetech.backoffice.service.user.impl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.management.RuntimeErrorException;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpSession;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.support.HttpAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.annotation.CheckPaymentPassword;
import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.SettingDao;
import com.maxivetech.backoffice.dao.UserBalanceDao;
import com.maxivetech.backoffice.dao.UserBalanceLogDao;
import com.maxivetech.backoffice.dao.UserBankAccountDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.dao.WithdrawalDao;
import com.maxivetech.backoffice.entity.UserBalances;
import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.entity.Withdrawals;
import com.maxivetech.backoffice.sendmail.HelperSendmail;
import com.maxivetech.backoffice.sendmail.Postman;
import com.maxivetech.backoffice.sendmail.PostmanWithdrawal;
import com.maxivetech.backoffice.service.user.User2WithdrawalService;
import com.maxivetech.backoffice.service.user.UserWithdrawalService;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperPassword;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class User2WithdrawalServiceImpl implements User2WithdrawalService {
	@Autowired
	private WithdrawalDao withdrawalDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserBankAccountDao userBankAccountDao;
	@Autowired
	private UserBalanceDao userBalanceDao;
	@Autowired
	private UserBalanceLogDao userBalanceLogDao;
	@Autowired
	private UserProfilesDao userProfileDao;
	@Autowired
	private SettingDao settingDao;
	
	
	@CheckRole(role = {Role.User})
	@CheckPaymentPassword(parameterIndex = 4)
	@Override
	public synchronized boolean addWithMt4(String currencyType, double amount, String userMemo,
			int bankAccountId, String paymentPassword, double rate,int fromLogin, HttpSession session) throws ForbiddenException {
		
		if(fromLogin > 0){
			
			//2、网站转银行卡_出金
			return add(currencyType, amount, userMemo, bankAccountId, paymentPassword, rate, session);
			
		}else{
			
			//1、网站转银行卡_出金
			return add(currencyType, amount, userMemo, bankAccountId, paymentPassword, rate, session);
		}
	}
	
	
	
	
	
	
	
	
	@CheckRole(role = {Role.User})
	@CheckPaymentPassword(parameterIndex = 4)
	@Override
	public synchronized boolean add(String currencyType, double amount, String userMemo,
			int bankAccountId, String paymentPassword, double rate, HttpSession session) throws ForbiddenException {

		// 登录检查
		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(session).getId());
		if (user.isFrozen()) {
			return false;
		}

		UserBankAccounts uba = (UserBankAccounts) userBankAccountDao
				.getById(bankAccountId);
		// 银行账号验证
		if (uba == null) {
			return false;
		}
		// 所有者验证
		if (uba.getUser().getId() != user.getId()) {
			return false;
		}

		// 可用余额验证
		UserBalances ub = userBalanceDao.getBalance(user, currencyType);
		if (ub == null) {
			return false;
		}
		// 出金金额检查
		if (amount <= 0) {
			return false;
		}
		if (amount > ub.getAmountAvailable()) {
			return false;
		}

		double minWithdrawalAmountUSD =  settingDao.getDouble("MinWithdrawalAmountUSD") ;
		if(amount < minWithdrawalAmountUSD){
			throw new RuntimeException("对不起，出金失败：出金金额小于规定最小出金金额（"+minWithdrawalAmountUSD+"）。");
		}
		
		{ // 扣余额
			boolean flag = userBalanceDao.increaseAmountAvailable(ub, (-amount));
			if (!flag) {
				return false;
			}

			// 生成单据
			Withdrawals withdrawal = new Withdrawals();
			
			// 汇款信息,这部分的汇率是根据客户提交的汇率来的
			if (rate <= 0) {
				throw new RuntimeException("");
			}
			
			//double amountCNY = (long) (amount * rate * 100) / 100.0; // 去尾法
			//withdrawal.setAmount(amountCNY);
			//withdrawal.setCurrency("CNY");
			withdrawal.setAmount(amount);
			withdrawal.setCurrency("USD");
			withdrawal.setRemittance("");
			withdrawal.setType("");

			// 银行信息
			withdrawal.setAccountName(uba.getAccountName());
			withdrawal.setAccountNumber(uba.getAccountNo());
			withdrawal.setBankName(uba.getBankName());
			withdrawal.setBankReference("");
			withdrawal.setSwiftCode(uba.getSwiftCode());
			withdrawal.setBranch("");
			withdrawal.setDateTime(null);
            withdrawal.setBankBranch(uba.getBankBranch());
            withdrawal.setBankAddress(uba.getBankAddress());
            withdrawal.setSwiftCode(uba.getSwiftCode());
            withdrawal.setIbanCode(uba.getIbanCode());
            withdrawal.setExchangeRate(rate);
            
            
			// 地址信息
			withdrawal.setCountry(uba.getCountryCode());
			withdrawal.setAddress1("");
			withdrawal.setAddress2("");
			withdrawal.setAddress3("");
			withdrawal.setExpiryDate(null);
			withdrawal.setPostcode("");

			withdrawal.setCreatTime(new Date());
			withdrawal.setAuditedTime(null);
			withdrawal.setAuditedMemo("");
			withdrawal.setIntermediaryBankBicSwiftCode("");
			withdrawal.setIntermediaryBankBranch("");
			withdrawal.setIntermediaryBankName("");

			withdrawal.setSenderReference("");
			withdrawal.setInternalReference("");
			withdrawal.setRemittance("");
			withdrawal.setState("WAITING");
			withdrawal.setUser(user);
			withdrawal.setUserMemo(userMemo);

			withdrawalDao.save(withdrawal);

			// 扣款日志
			userBalanceLogDao.addLog(ub, -amount, 0, withdrawal.getId(), 0,
					"用户出金（汇率：" + rate + "）→银行卡："+uba.getAccountNo());

			withdrawalDao.commit();
			
			//发邮件——待出金成功在发送
			{
				//PostmanWithdrawal postman = new PostmanWithdrawal(user.getEmail());
				//postman.waiting(withdrawal, userBalanceDao.getBalance(user,  "USD"));
			}
		}

		return true;
	}

	
	@CheckRole(role = {Role.User})
	@Override
	public Page<Withdrawals> getPageByTimeAndState(int pageNo, int pageSize,
			String urlFormat, String startDate, String endDate, String state,HttpSession session) {

		// 登录检查
		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		if (user == null) {
			return null;
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //格式化时间
	    Date sDate = null,eDate = null;
		ArrayList<Criterion> wheres = new ArrayList<Criterion>();
		wheres.add(Restrictions.eq("user", user));
		 //格式化startDate 开始时间为 Date类型
		 //格式化startDate 开始时间为 Date类型
		java.sql.Timestamp staDate = null; //new Timestamp(sDate.getTime());
		java.sql.Timestamp enDate = null;//new Timestamp(eDate.getTime());
		
		if(startDate.length()>0){
			try {
				sDate = sdf.parse(startDate);
				staDate = new Timestamp(sDate.getTime());
			} catch (ParseException e) {
				staDate = null;
			}
		}
		
		if(endDate.length()>0){
			try {
				eDate=sdf.parse(endDate);   //格式化endDate 结束时间为 Date类型
				enDate = new Timestamp(eDate.getTime());
			} catch (ParseException e) {
				enDate = null;
			}
		}
		
		if(state.length()>0){
			wheres.add(Restrictions.eq("state", state));
		}
		
		if(staDate == null && enDate == null){//不限
		}
		else if(staDate != null && enDate == null){//有开始限制
			wheres.add(Restrictions.or(
					Restrictions.ge("creatTime", staDate),
					Restrictions.ge("dateTime", staDate),
					Restrictions.ge("auditedTime", staDate),
					Restrictions.ge("canceledTime", staDate)));
		}
		else if(staDate == null && enDate != null){//有结束限制
			wheres.add(Restrictions.or(
					Restrictions.le("creatTime", enDate),
					Restrictions.le("dateTime", staDate),
					Restrictions.le("auditedTime", enDate),
					Restrictions.le("canceledTime", enDate)));
		}
		else if(staDate != null && enDate != null){//两端都有限制
			wheres.add(Restrictions.or(
					Restrictions.between("creatTime", staDate, enDate),
					Restrictions.between("dateTime", staDate,endDate),
					Restrictions.between("auditedTime", staDate, enDate),
					Restrictions.between("canceledTime", staDate, enDate)));
		}

		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("creatTime"));

		Projection projs = null;

		Page<Withdrawals> page = (Page<Withdrawals>) withdrawalDao.getPage(
				wheres, orders, projs, pageSize, pageNo, null);
		page.generateButtons(urlFormat);
		// 清除
		for (Withdrawals item : page.getList()) {
			item.setUser(null);
		}

		return page;
	}
	
	
	
	@CheckRole(role = {Role.User})
	@Override
	public Page<Withdrawals> getPage(int pageNo, int pageSize,
			String urlFormat, HttpSession session) {

		// 登录检查
		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		if (user == null) {
			return null;
		}

		ArrayList<Criterion> wheres = new ArrayList<Criterion>();
		wheres.add(Restrictions.eq("user", user));

		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("creatTime"));

		Projection projs = null;

		Page<Withdrawals> page = (Page<Withdrawals>) withdrawalDao.getPage(
				wheres, orders, projs, pageSize, pageNo, null);
		page.generateButtons(urlFormat);

		// 清除
		for (Withdrawals item : page.getList()) {
			item.setUser(null);
		}

		return page;
	}

	@CheckRole(role = {Role.User})
	@Override
	public Page<Withdrawals> getPageByUser(int userId, int pageNo,
			int pageSize, String scheme, String urlFormat, HttpSession session) {

		// 登录检查
		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		if (user == null) {
			return null;
		}
		if (userId != 0) {
			// 如果userId不为空，及查询下线记录
			user = (Users) userDao.getById(userId);
		}

		ArrayList<Criterion> wheres = new ArrayList<Criterion>();
		wheres.add(Restrictions.eq("user", user));
		switch (scheme) {
		case "remitted":
			wheres.add(Restrictions.eq("state", "REMITTED"));
			break;

		default:
			break;
		}

		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("creatTime"));

		Projection projs = null;

		Page<Withdrawals> page = (Page<Withdrawals>) withdrawalDao.getPage(
				wheres, orders, projs, pageSize, pageNo, null);
		page.generateButtons(urlFormat);

		// 清除
		for (Withdrawals item : page.getList()) {
			item.set_userId(item.getUser().getId());
			item.set_userEmail(item.getUser().getEmail());
			item.set_userName(userProfileDao.getUserName(item.getUser()));
			item.setUser(null);
		}

		return page;
	}

	@CheckRole(role = {Role.User})
	@Override
	public boolean cancel(int id, HttpSession session)
			throws ForbiddenException {

		// 登录检查
		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		if (user == null) {
			return false;
		}
		if (user.isFrozen()) {
			return false;
		}

		Withdrawals withdrawal = (Withdrawals) withdrawalDao.getById(id);
		if (withdrawal != null && withdrawal.getUser().getId() == user.getId()) {
			// 所有权验证通过
			switch (withdrawal.getState()) {
			case "WAITING": // 待審核
			case "REJECTED": // 駁回
			case "BACK": // 銀行退回
				// 更新資金
				try {
					//汇率
					double rate = settingDao.getDouble("WithdrawalRateCNY");
					if (rate <= 0) {
						throw new RuntimeException("汇率错误！");
					}
					//加回钱
					//double amountUSD = (long)(withdrawal.getAmount() / rate * 100) / 100.0;
					double amountUSD = withdrawal.getAmount();
					boolean flag = userBalanceDao.increaseAmountAvailable(user, "USD", amountUSD);
					if (!flag) {
						return false;
					}
					//出金单状态设为取消
					withdrawal.setState("CANCELED");
					withdrawal.setCanceledTime(new Date());
					withdrawalDao.update(withdrawal);

					//日志
					UserBalances ub = userBalanceDao.getBalance(user, "USD");
					userBalanceLogDao.addLog(ub, amountUSD, 0, withdrawal.getId(), 0, "取消出金");

					withdrawalDao.commit();

					// 发邮件
					{
						PostmanWithdrawal postman = new PostmanWithdrawal(user.getEmail());
						UserBalances ub2 = userBalanceDao.getBalance(
								withdrawal.getUser(), withdrawal.getCurrency());

						postman.canceled(withdrawal, ub2);
					}

					return true;

				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			case "AUDITED": // 已審核
			case "PENDING_SUPERVISOR": // 待财务主管审核
			case "REMITTED": // 已匯出
			case "CANCELED": // 已撤消
			default:
				return false;
			}
		}
		return false;
	}

	@CheckRole(role = {Role.User})
	@Override
	public Withdrawals getById(int id, HttpSession session) {


		// 登录检查
		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		if (user == null) {
			return null;
		}
		Withdrawals ret = (Withdrawals) withdrawalDao.getById(id);
		if (ret != null && ret.getUser().getId() == user.getId()) {
			// 通过所有者验证

			// 清除
			ret.setUser(null);

			return ret;
		}
		return null;
	}

	@CheckRole(role = {Role.User})
	@Override
	public double getRate(HttpSession session) {
		return settingDao.getDouble("WithdrawalRateCNY");
	}

}
