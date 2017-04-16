package com.maxivetech.backoffice.dao;

import java.util.List;

import com.maxivetech.backoffice.entity.RebateLevels;
import com.maxivetech.backoffice.entity.RebateVipGradeMt4Groups;
import com.maxivetech.backoffice.entity.RebateVipGrades;

public interface RebateVipGradeMt4GroupDao extends _BaseDao {
	public List<RebateVipGradeMt4Groups> getList();
	public void update(RebateVipGradeMt4Groups rebateVipGradeMt4Group);
}
