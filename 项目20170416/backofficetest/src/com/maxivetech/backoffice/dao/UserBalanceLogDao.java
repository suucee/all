package com.maxivetech.backoffice.dao;

import com.maxivetech.backoffice.entity.UserBalances;


public interface UserBalanceLogDao extends _BaseDao {
	
	public void addLog(UserBalances userBalance, double amount,int depositId,int withdrawalId,int transfersId ,String description);
}
