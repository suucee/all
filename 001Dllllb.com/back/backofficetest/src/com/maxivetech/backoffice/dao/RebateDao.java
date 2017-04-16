package com.maxivetech.backoffice.dao;

import java.util.List;

import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Rebates;
import com.maxivetech.backoffice.entity.Settings;
import com.maxivetech.backoffice.entity.Users;

public interface RebateDao extends _BaseDao {
	public Rebates findByName(String name);
	public List<Rebates> getList();
}
