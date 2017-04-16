package com.maxivetech.backoffice.dao.impl;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.CDDChecksDao;
import com.maxivetech.backoffice.entity.CDDChecks;
import com.maxivetech.backoffice.entity.Deposits;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.entity.Withdrawals;

@Repository
public class CDDChecksDaoImpl  extends _BaseDaoImpl implements CDDChecksDao{

	@Override
	public Class<?> classModel() {
		return CDDChecks.class;
	}

	@Override
	public CDDChecks lastCDDChecks(Users user, Deposits  deposit,
			Withdrawals withdrawal) {
		List<CDDChecks> ck = null;
		if(user != null){
			ck = this.createCriteria()
			.add(Restrictions.eq("user", user))
			.addOrder(Order.desc("timestamp"))
			.setMaxResults(1)
			.list();
		}
		if(deposit != null){
			ck = this.createCriteria()
			.add(Restrictions.eq("deposit", deposit))
			.addOrder(Order.desc("timestamp"))
			.setMaxResults(1)
			.list();
		}
		if(withdrawal != null){
			ck = this.createCriteria()
			.add(Restrictions.eq("withdrawal", withdrawal))
			.addOrder(Order.desc("timestamp"))
			.setMaxResults(1)
			.list();
		}
		
		return ck.size() > 0 ? ck.get(0) : null;
	}

}
