package com.maxivetech.backoffice.dao;

import java.util.List;

import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.RebateRecords;
import com.maxivetech.backoffice.entity.Rebates;
import com.maxivetech.backoffice.entity.Settings;
import com.maxivetech.backoffice.entity.Users;

public interface RebateRecordDao extends _BaseDao {
	public List<RebateRecords> getList(int order, Users onlyUser);
}
