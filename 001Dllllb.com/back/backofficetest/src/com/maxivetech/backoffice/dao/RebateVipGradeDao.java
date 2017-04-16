package com.maxivetech.backoffice.dao;

import java.util.List;

import com.maxivetech.backoffice.entity.RebateLevels;
import com.maxivetech.backoffice.entity.RebateVipGrades;

public interface RebateVipGradeDao extends _BaseDao {
	public List<RebateVipGrades> getList();
	public void update(RebateVipGrades rebateLevel);
}
