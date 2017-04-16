package com.maxivetech.backoffice.dao;

import java.util.List;

import com.maxivetech.backoffice.entity.RebateLevels;

public interface RebateLevelDao extends _BaseDao {
	public List<RebateLevels> getList();
	public void update(RebateLevels rebateLevel);
}
