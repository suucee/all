package com.maxivetech.backoffice.service.user.impl;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.UserBankAccountDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.service.user.User2AccountService;
import com.maxivetech.backoffice.util.ForbiddenException;
@Service
@Transactional
public class User2AccountServiceImpl implements User2AccountService {
	@Autowired
	private UserBankAccountDao userBankAccountDao;
	@Autowired
	private UserDao userDao;
	
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
			uba.setState("WAITING");//重新提交之后重新改变为待审核状态
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
}
