package com.maxivetech.backoffice.dao.impl;


import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.DepositDao;
import com.maxivetech.backoffice.entity.Deposits;

@Repository
public class DepositDaoImpl extends _BaseDaoImpl implements DepositDao {
	@Override
	public Class<?> classModel() {
		return Deposits.class;
	}

	/**
	 * 查詢入金詳情
	 */
	@Override
	public Deposits getDepositIfo(int id) {
		Criteria cri = getSession().createCriteria(Deposits.class);
		Deposits deposit = (Deposits) cri.add(Restrictions.eq("id", id))
				.uniqueResult();
		return deposit;
	}

}
