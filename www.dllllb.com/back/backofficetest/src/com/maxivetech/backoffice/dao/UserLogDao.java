package com.maxivetech.backoffice.dao;

import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Users;

public interface UserLogDao extends _BaseDao {
	public void log(Users user, String action, String description);
	public void log(int userId, String action, String description);

}
