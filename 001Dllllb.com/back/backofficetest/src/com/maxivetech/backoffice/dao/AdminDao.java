package com.maxivetech.backoffice.dao;

import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Users;

public interface AdminDao extends _BaseDao {
	public Admins findByAccount(String account);
	
}
