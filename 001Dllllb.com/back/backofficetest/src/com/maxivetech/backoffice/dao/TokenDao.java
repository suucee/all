package com.maxivetech.backoffice.dao;

import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Tokens;
import com.maxivetech.backoffice.entity.Users;

public interface TokenDao extends _BaseDao {
	public Tokens findByUUID(String uuid);
	public Tokens findByUser(Users user);
	public Tokens findByAdmin(Admins admin);
	public void deleteToken(Tokens token);
}
