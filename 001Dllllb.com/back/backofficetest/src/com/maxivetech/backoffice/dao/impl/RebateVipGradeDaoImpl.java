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
import com.maxivetech.backoffice.dao.RebateVipGradeDao;
import com.maxivetech.backoffice.entity.RebateVipGrades;
import com.maxivetech.backoffice.entity.Rebates;


@Repository
public class RebateVipGradeDaoImpl extends _BaseDaoImpl implements RebateVipGradeDao {
	@Override
	public Class<?> classModel() {return RebateVipGrades.class;}

	@Override
	@Cacheable(value = "rebateVipGradeList")
	public List<RebateVipGrades> getList() {
		List<RebateVipGrades> list = this.createCriteria()
			.addOrder(Order.asc("vipGrade"))
			.list();
	
		return list;
	}

	@Override
	@CacheEvict(value = "rebateVipGradeList")
	public void update(RebateVipGrades rebateLevel) {
		this.update(rebateLevel);
	}

	
}
