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
import com.maxivetech.backoffice.dao.RebateVipGradeMt4GroupDao;
import com.maxivetech.backoffice.entity.RebateVipGradeMt4Groups;
import com.maxivetech.backoffice.entity.RebateVipGrades;
import com.maxivetech.backoffice.entity.Rebates;


@Repository
public class RebateVipGradeMt4GroupDaoImpl extends _BaseDaoImpl implements RebateVipGradeMt4GroupDao {
	@Override
	public Class<?> classModel() {return RebateVipGradeMt4Groups.class;}

	@Override
	@Cacheable(value = "rebateVipGradeMt4GroupList")
	public List<RebateVipGradeMt4Groups> getList() {
		List<RebateVipGradeMt4Groups> list = this.createCriteria()
			.addOrder(Order.asc("vipGrade"))
			.addOrder(Order.asc("mt4Group"))
			.list();
	
		return list;
	}

	@Override
	@CacheEvict(value = "rebateVipGradeMt4GroupList")
	public void update(RebateVipGradeMt4Groups rebateVipGradeMt4Group) {
		this.update(rebateVipGradeMt4Group);
	}

	
}
