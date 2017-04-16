package com.maxivetech.backoffice.dao;

import com.maxivetech.backoffice.entity.Users;


public interface WithdrawalDao extends _BaseDao {
	public Users getUserinfo(int id);
}
