package com.maxivetech.backoffice.dao.impl;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.RebateDao;
import com.maxivetech.backoffice.dao.RebateRecordDao;
import com.maxivetech.backoffice.entity.RebateRecords;
import com.maxivetech.backoffice.entity.Rebates;
import com.maxivetech.backoffice.entity.Users;


@Repository
public class RebateRecordDaoImpl extends _BaseDaoImpl implements RebateRecordDao {
	@Override
	public Class<?> classModel() {return RebateRecords.class;}

	@Override
	@Cacheable(value = "orderRebateList", key = "#order")
	public List<RebateRecords> getList(int order, Users user) {
		if (user != null) {
			return this.createCriteria()
				.add(Restrictions.eq("mt4Order", order))
				.add(Restrictions.eq("user", user))
				.addOrder(Order.asc("id"))
				.list();
		} else {
			return this.createCriteria()
					.add(Restrictions.eq("mt4Order", order))
					.addOrder(Order.asc("id"))
					.list();
		}
	}


}
