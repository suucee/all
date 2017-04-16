package com.maxivetech.backoffice.dao;

import java.util.List;

import com.maxivetech.backoffice.entity.RebateAgents;
import com.maxivetech.backoffice.entity.RebateLevels;
import com.maxivetech.backoffice.entity.RebateVipGrades;
import com.maxivetech.backoffice.entity.Rebates;
import com.maxivetech.backoffice.entity.Users;

public interface RebateAgentDao extends _BaseDao {
	public List<RebateAgents> getList(Users user);
	public RebateAgents getOne(Users user, Rebates rebate);
	public void saveOrUpdate(Users user, Rebates rebate, double money);
}
