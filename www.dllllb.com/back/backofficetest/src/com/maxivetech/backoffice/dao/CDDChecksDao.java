package com.maxivetech.backoffice.dao;


import com.maxivetech.backoffice.entity.CDDChecks;
import com.maxivetech.backoffice.entity.Deposits;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.entity.Withdrawals;


public interface CDDChecksDao extends _BaseDao{
	/**
	 * 按条件查询最新合规操作记录
	 * 
	 */
	 
	public CDDChecks lastCDDChecks(Users user, Deposits  deposit,
			Withdrawals withdrawal);
}
