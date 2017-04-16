package com.maxivetech.backoffice.dao.impl;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.TokenDao;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Tokens;
import com.maxivetech.backoffice.entity.Users;


@Repository
public class TokenDaoImpl extends _BaseDaoImpl implements TokenDao {
	@Override
	public Class<?> classModel() {return Tokens.class;}

	@Override
	@Cacheable(value = "token", key = "#uuid")
	public Tokens findByUUID(String uuid) {
		List<Tokens> list = (List<Tokens>) this.createCriteria()
			.add(Restrictions.eq("uuid", uuid))
			.list();;
		
		return list != null && list.size() > 0 ? list.get(0) : null;
	}

	@Override
	public Tokens findByUser(Users user) {
		List<Tokens> list = (List<Tokens>) this.createCriteria()
			.add(Restrictions.eq("user", user))
			.list();
		
		return list != null && list.size() > 0 ? list.get(0) : null;
	}

	@Override
	public Tokens findByAdmin(Admins admin) {
		List<Tokens> list = (List<Tokens>) this.createCriteria()
				.add(Restrictions.eq("admin", admin))
				.list();

		return list != null && list.size() > 0 ? list.get(0) : null;
	}

	@Override
	@CacheEvict(value = "token", key = "#token.uuid")
	public void deleteToken(Tokens token) {
		if (token != null) {
			this.delete(token);
		}
	}



}
