package com.maxivetech.backoffice.service.user.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.AttachmentDao;
import com.maxivetech.backoffice.dao.UserBalanceDao;
import com.maxivetech.backoffice.dao.UserBalanceLogDao;
import com.maxivetech.backoffice.dao.UserBankAccountDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.WithdrawalDao;
import com.maxivetech.backoffice.entity.UserBalanceLogs;
import com.maxivetech.backoffice.entity.UserBalances;
import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.entity.Withdrawals;
import com.maxivetech.backoffice.service.user.UserAccountService;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperDate;
import com.maxivetech.backoffice.util.HelperHttp;
import com.maxivetech.backoffice.util.Page;
import com.maxivetech.backoffice.entity.Attachments;
@Service
@Transactional
public class UserAccountServiceImpl implements UserAccountService {
	@Autowired
	private UserBalanceDao userBalanceDao;
	@Autowired
	private UserBalanceLogDao userBalanceLogDao;
	@Autowired
	private UserBankAccountDao userBankAccountDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private WithdrawalDao withdrawalDao;
	@Autowired
	private AttachmentDao attachmentDao;

	@CheckRole(role = {Role.User})
	@Override
	public List<UserBalances> getBalanceList(HttpSession session) {

		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		if (user == null) {
			return null;
		}

		List<UserBalances> list = userBalanceDao.createCriteria()
				.add(Restrictions.eq("user", user))
				.addOrder(Order.asc("updatedTime")).list();

		// 清理
		for (UserBalances ub : list) {
			ub.setUser(null);
		}

		return list;
	}

	@CheckRole(role = {Role.User})
	@Override
	public Page<UserBalanceLogs> getBalanceLogs(int pageNo, int pageSize,
			String urlFormat, String start, String end, HttpSession session) {

		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		if (user == null) {
			return null;
		}

		ArrayList<Criterion> wheres = new ArrayList<Criterion>();
		wheres.add(Restrictions.eq("user", user));

		Date startDate = HelperDate.parse(start, "yyyy-MM-dd HH:mm:ss");
		Date endDate = HelperDate.parse(end, "yyyy-MM-dd HH:mm:ss");
		if (startDate != null) {
			wheres.add(Restrictions.ge("creatTime", startDate));
		}
		if (endDate != null) {
			wheres.add(Restrictions.lt("creatTime", endDate));
		}

		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("creatTime"));

		Page<UserBalanceLogs> page = (Page<UserBalanceLogs>) userBalanceLogDao
				.getPage(wheres, orders, null, pageSize, pageNo, null);
		page.generateButtons(urlFormat);

		// 清理
		for (UserBalanceLogs ubl : page.getList()) {
			ubl.setUser(null);
		}

		return page;
	}

	@CheckRole(role = {Role.User})
	@Override
	public List<UserBankAccounts> getBankAccountList(HttpSession session) {

		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		if (user == null) {
			return null;
		}

		List<UserBankAccounts> list = userBankAccountDao.getAllBank(user);

		for (UserBankAccounts item : list) {
			item.setUser(null);
		}

		return list;
	}

	@CheckRole(role = {Role.User})
	@Override
	public UserBankAccounts getBankAccount(int bankAccountId,
			HttpSession session) {

		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		if (user == null) {
			return null;
		}

		UserBankAccounts userBankAccount = (UserBankAccounts) userBankAccountDao
				.getById(bankAccountId);
		if (userBankAccount != null
				&& userBankAccount.getUser().getId() == user.getId()) {
			// 通过所有者验证
			userBankAccount.setUser(null);

			return userBankAccount;
		}

		return null;
	}

	@CheckRole(role = {Role.User})
	@Override
	public boolean deleteBankAccount(int bankAccountId, HttpSession session)
			throws ForbiddenException {
		boolean result=false;
		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		if (user == null) {
			result= false;
		}
		if (user.isFrozen()) {
			throw new ForbiddenException();
		} // 凍結拒絕

		UserBankAccounts userBankAccount = (UserBankAccounts) userBankAccountDao
				.getById(bankAccountId);
		if (userBankAccount != null&& userBankAccount.getUser().getId() == user.getId()&&!userBankAccount.isDefault()) {
			// 通过所有者验证

			// 删除
			userBankAccount.setState("DELETED");			
			userBankAccountDao.saveOrUpdate(userBankAccount);
			userBankAccountDao.commit();
			result= true;
		}

		return result;
	}

	@CheckRole(role = {Role.User})
	@Override
	public double getWithdrawalsSum(String currencyType, HttpSession session) {
		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		if (user == null) {
			return 0;
		}
		Object result = withdrawalDao
				.createCriteria()
				.setProjection(
						Projections.projectionList().add(
								Projections.sum("amount")))
				.add(Restrictions.eq("user", user))
				.add(Restrictions.eq("currency", currencyType))
				.add(Restrictions.ne("state", "REMITTED"))
				.add(Restrictions.ne("state", "CANCELED")).uniqueResult();
		if (result != null) {

			return (double) result;
		}
		return 0;
	}

	@CheckRole(role = {Role.User})
	@Override
	public boolean addBankAccount(String bankName, String accountName,
			String accountNo, String countryCode,String swiftCode,String ibanCode,String bankBranch,String bankAddress,String attachment_id,HttpSession session) {
		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		if (user == null) {
			return false;
		}
		UserBankAccounts uba = new UserBankAccounts();

		uba.setAccountName(accountName);
		uba.setAccountNo(accountNo);
		uba.setBankName(bankName);
		uba.setCurrencyType("USD");
		uba.setCountryCode(countryCode);
		uba.setDefault(false);
		uba.setIntermediaryBankAddress("");
		uba.setIntermediaryBankBicSwiftCode("");
		uba.setIntermediaryBankBranch("");
		uba.setIntermediaryBankName("");
		uba.setIbanCode(ibanCode);
		uba.setSwiftCode(swiftCode);
		uba.setBankAddress(bankAddress);
		uba.setBankBranch(bankBranch);
		uba.setSortNum(0);
		uba.setUser(user);
		uba.setState("WAITING");
		uba.setUpdateTime(new Date());
		userBankAccountDao.save(uba);
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
			    	a.setOwnerId(uba.getId());
			    	a.setOwnerType(UserBankAccounts.getOwnertype());
			    	a.setUser(uba.getUser());
			    	attachmentDao.saveOrUpdate(a);
			    }
		     }
		 }
		userBankAccountDao.commit();
		return true;
	}

	@CheckRole(role = {Role.User})
	@Override
	public boolean editBankAccount(int bankAccountId, String bankName,
			String accountName, String accountNo,
			String countryCode,String swiftCode,String ibanCode,String bankBranch,String bankAddress, HttpSession session)
			throws ForbiddenException {
		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		if (user == null) {
			return false;
		}
		if (user.isFrozen()) {
			throw new ForbiddenException();
		} // 凍結拒絕

		UserBankAccounts uba = (UserBankAccounts) userBankAccountDao.getById(bankAccountId);
		if (uba != null && uba.getUser().getId() == user.getId()) {
			// 所有者验证
			uba.setAccountName(accountName);
			uba.setAccountNo(accountNo);
			uba.setBankName(bankName);
			uba.setCountryCode(countryCode);
			uba.setIbanCode(ibanCode);
			uba.setSwiftCode(swiftCode);
			uba.setBankAddress(bankAddress);
			uba.setBankBranch(bankBranch);
            uba.setUpdateTime(new Date());
            System.out.println(new Date().getTime());
			userBankAccountDao.update(uba);
			userBankAccountDao.commit();

			return true;
		}

		return false;
	}

	@CheckRole(role = {Role.User})
	@Override
	public List<Attachments> getBankImageList(int id,HttpSession session){
        List<Attachments> alist=attachmentDao.getFileImage(UserBankAccounts.getOwnertype(), id);
		return alist;
	}
	
	@CheckRole(role = {Role.User})
	@Override
	public boolean deleteBankImage(int id,HttpSession session){
		return  attachmentDao.deleteAttachment(id);
	}
	
	
	@CheckRole(role = {Role.User})
	@Override
	public void refreshMT4Users(HttpSession session) {
		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(session).getId());
		if (user == null) {
			return;
		}
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userId", user.getId());
		HelperHttp.doGet(BackOffice.getInst().LOCAL_CONTROLLER_ROOT+"/syncOneUser.do", map);
	}

	@CheckRole(role = {Role.User})
	@Override
	public double getMt4Balance(int login,HttpSession session) {
		
		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(session).getId());
		if (user == null) {
			return 0;
		}
		if(login == 0){
			//网站余额
			UserBalances ub = userBalanceDao.getBalance(user, "USD");
		 	if(ub.getUser() == user){
		 		return ub.getAmountAvailable();
		 	}
		}else {
		}
		return 0;
	}
}
