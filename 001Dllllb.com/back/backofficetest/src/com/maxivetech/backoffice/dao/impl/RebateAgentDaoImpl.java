package com.maxivetech.backoffice.dao.impl;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.RebateAgentDao;
import com.maxivetech.backoffice.dao.RebateDao;
import com.maxivetech.backoffice.dao.RebateLevelDao;
import com.maxivetech.backoffice.dao.RebateVipGradeDao;
import com.maxivetech.backoffice.entity.RebateAgents;
import com.maxivetech.backoffice.entity.RebateVipGrades;
import com.maxivetech.backoffice.entity.Rebates;
import com.maxivetech.backoffice.entity.Users;


@Repository
public class RebateAgentDaoImpl extends _BaseDaoImpl implements RebateAgentDao {
	@Override
	public Class<?> classModel() {return RebateAgents.class;}

	@Override
	//@Cacheable(value = "rebateAgentList", key = "#user.id")
	public List<RebateAgents> getList(Users user) {
		List<RebateAgents> list = this.createCriteria()
			.add(Restrictions.eq("user", user))
			.list();
	
		return list;
	}

	@Override
	@Cacheable(value = "rebateAgentOne", key = "#user.id-#rebate.id")
	public RebateAgents getOne(Users user, Rebates rebate) {
		List<RebateAgents> list = this.createCriteria()
				.add(Restrictions.eq("user", user))
				.add(Restrictions.eq("rebate", rebate))
				.list();
		
		return list != null && list.size() > 0 ? list.get(0) : null;
	}

	@Override
	@CacheEvict(value = "rebateAgentOne", key = "#user.id-#rebate.id")
	public void saveOrUpdate(Users user, Rebates rebate, double money) {
		RebateAgents ra = getOne(user, rebate);
		if (ra == null) {
			ra = new RebateAgents();
			
			ra.setUser(user);
			ra.setRebate(rebate);
			ra.setMoney(money);
			ra.setUpdatedTime(new Date());
			this.save(ra);
		} else {
			ra.setMoney(money);
			ra.setUpdatedTime(new Date());
			this.update(ra);
		}
	}
	
}
