package com.maxivetech.backoffice.dao;

import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Users;

public interface AdminLogDao extends _BaseDao {
	public void log(Admins admin, String action, String description);
	public void log(int adminId, String action, String description);
	
}
