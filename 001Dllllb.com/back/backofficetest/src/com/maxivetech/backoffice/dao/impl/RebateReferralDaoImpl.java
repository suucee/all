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
import com.maxivetech.backoffice.dao.RebateReferralDao;
import com.maxivetech.backoffice.entity.RebateLevels;
import com.maxivetech.backoffice.entity.RebateReferrals;
import com.maxivetech.backoffice.entity.Rebates;


@Repository
public class RebateReferralDaoImpl extends _BaseDaoImpl implements RebateReferralDao {
	@Override
	public Class<?> classModel() {return RebateReferrals.class;}

	@Override
	@Cacheable(value = "rebateReferralList")
	public List<RebateReferrals> getList() {
		List<RebateReferrals> list = this.createCriteria()
			.list();
	
		return list;
	}

	@Override
	@CacheEvict(value = "rebateReferralList")
	public void update(RebateReferrals rebateReferral) {
		this.update(rebateReferral);
	}

	
}
