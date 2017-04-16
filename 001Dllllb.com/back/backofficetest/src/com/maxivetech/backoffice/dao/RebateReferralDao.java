package com.maxivetech.backoffice.dao;

import java.util.List;

import com.maxivetech.backoffice.entity.RebateReferrals;

public interface RebateReferralDao extends _BaseDao {
	public List<RebateReferrals> getList();
	public void update(RebateReferrals rebateReferral);
}
