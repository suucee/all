package com.maxivetech.backoffice.dao;

import com.maxivetech.backoffice.entity.UserBalances;
import com.maxivetech.backoffice.entity.Users;


public interface UserBalanceDao extends _BaseDao {
	
	/**
	 * 獲取
	 * @param user
	 * @param currencyType
	 * @return
	 */
	public UserBalances getBalance(Users user, String currencyType);
	
	public boolean increaseAmountAvailable(Users user, String currencyType, double amount);
	public boolean increaseAmountAvailable(UserBalances ub, double amount);

	public boolean increaseAmountFrozen(Users user, String currencyType, double amount);
	public boolean increaseAmountFrozen(UserBalances ub, double amount);
}
