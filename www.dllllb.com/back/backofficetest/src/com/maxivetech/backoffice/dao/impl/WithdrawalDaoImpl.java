package com.maxivetech.backoffice.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.WithdrawalDao;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.entity.Withdrawals;



@Repository
public class WithdrawalDaoImpl extends _BaseDaoImpl implements WithdrawalDao {
	@Override
	public Class<?> classModel() {return Withdrawals.class;}
	
	@Override
	public Users getUserinfo(int id) {
		Criteria cri=getSession().createCriteria(Users.class);
		Users user=(Users) cri.add(Restrictions.eq("id", id)).uniqueResult();
		return user;
	}

}
