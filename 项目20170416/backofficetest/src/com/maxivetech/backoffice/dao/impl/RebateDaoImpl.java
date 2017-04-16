package com.maxivetech.backoffice.dao.impl;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.RebateDao;
import com.maxivetech.backoffice.entity.Rebates;


@Repository
public class RebateDaoImpl extends _BaseDaoImpl implements RebateDao {
	@Override
	public Class<?> classModel() {return Rebates.class;}

	@Override
	public List<Rebates> getList() {
		List<Rebates> list = this.createCriteria()
			.addOrder(Order.asc("sortNum"))
			.addOrder(Order.asc("name"))
			.list();
	
		return list;
	}

	@Override
	public Rebates findByName(String name) {
		List<Rebates> list = this.createCriteria()
			.add(Restrictions.eq("name", name))
			.list();
		
		return list != null && list.size() > 0 ? list.get(0) : null;
	}


}
