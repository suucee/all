package com.maxivetech.backoffice.dao;

import java.util.List;

import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.Users;

public interface UserBankAccountDao extends _BaseDao {

	public UserBankAccounts getBank(Users user);
	public List<UserBankAccounts> getAllBank(Users user);
}
