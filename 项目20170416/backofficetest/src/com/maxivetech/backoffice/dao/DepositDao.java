package com.maxivetech.backoffice.dao;


import com.maxivetech.backoffice.entity.Deposits;


public interface DepositDao extends _BaseDao {
	public Deposits getDepositIfo(int id);
}
