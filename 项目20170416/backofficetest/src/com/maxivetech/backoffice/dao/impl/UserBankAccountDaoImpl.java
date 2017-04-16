package com.maxivetech.backoffice.dao.impl;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.UserBankAccountDao;
import com.maxivetech.backoffice.entity.Attachments;
import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.Users;


@Repository
public class UserBankAccountDaoImpl extends _BaseDaoImpl implements UserBankAccountDao {
	@Override
	public Class<?> classModel() {return UserBankAccounts.class;}

	@Override
	public UserBankAccounts getBank(Users user) {
		List<UserBankAccounts> list = this.createCriteria()
				.add(Restrictions.eq("user", user))
				.add(Restrictions.eq("isDefault", true))
				.addOrder(Order.desc("sortNum"))
				.setMaxResults(1)
				.list();
		return list != null && list.size() > 0 ? list.get(0) : null;
	}
	@Override
	public List<UserBankAccounts> getAllBank(Users user) {
		List<UserBankAccounts> list = this.createCriteria()
				.add(Restrictions.eq("user", user))
				.add(Restrictions.ne("state", "DELETED"))
				.addOrder(Order.asc("state"))
				.list();
		//return list != null && list.size() > 0 ? list : null;
		//
		return list;
	}

}
