package com.maxivetech.backoffice.dao.impl;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.UserBalanceLogDao;
import com.maxivetech.backoffice.entity.UserBalanceLogs;
import com.maxivetech.backoffice.entity.UserBalances;



@Repository
public class UserBalanceLogDaoImpl extends _BaseDaoImpl implements UserBalanceLogDao {
	@Override
	public Class<?> classModel() {return UserBalanceLogs.class;}

	@Override
	public void addLog(UserBalances userBalance, double amount
			,int depositId,int withdrawalId,int transfersId ,String description) {
		UserBalanceLogs log = new UserBalanceLogs();

		//log.setId(UUID.randomUUID().toString());
		log.setAmount(amount);
		log.setAmountAvailable(userBalance.getAmountAvailable());
		log.setAmountFrozen(userBalance.getAmountFrozen());
		log.setDescription(description);
		Date date=new Date();
		log.setCreatTime(date);
		if(depositId>0){
			log.setDepositId(depositId);
		}
		if(withdrawalId>0){
			log.setWithdrawalId(withdrawalId);
		}
		if(transfersId>0){
			log.setTransfersId(transfersId);
		}
		log.setCurrencyType(userBalance.getCurrencyType());
		log.setUser(userBalance.getUser());
		
		this.saveOrUpdate(log);
	}
}
