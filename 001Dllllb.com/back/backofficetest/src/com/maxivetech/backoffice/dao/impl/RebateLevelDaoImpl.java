package com.maxivetech.backoffice.dao.impl;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.RebateDao;
import com.maxivetech.backoffice.dao.RebateLevelDao;
import com.maxivetech.backoffice.entity.RebateLevels;
import com.maxivetech.backoffice.entity.Rebates;


@Repository
public class RebateLevelDaoImpl extends _BaseDaoImpl implements RebateLevelDao {
	@Override
	public Class<?> classModel() {return RebateLevels.class;}

	@Override
	@Cacheable(value = "rebateLevelList")
	public List<RebateLevels> getList() {
		List<RebateLevels> list = this.createCriteria()
			.addOrder(Order.asc("level"))
			.list();
	
		return list;
	}

	@Override
	@CacheEvict(value = "rebateLevelList")
	public void update(RebateLevels rebateLevel) {
		this.update(rebateLevel);
	}

	
}
