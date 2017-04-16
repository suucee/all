package com.maxivetech.backoffice.hooks;

import com.maxivetech.backoffice.dao.RebateAgentDao;
import com.maxivetech.backoffice.dao.RebateDao;
import com.maxivetech.backoffice.dao.RebateRecordDao;
import com.maxivetech.backoffice.dao.SettingDao;
import com.maxivetech.backoffice.dao.UserBalanceDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.entity.Rebates;
import com.maxivetech.backoffice.entity.Users;

public  class CompanyHook implements CompanyHookable {
	
	@Override
	public String replaceMT4GroupShowName(String group)
	{
		return group;
	}

	@Override
	public String getMT4Group(String scheme) {
		return null;
	}

	@Override
	public boolean isCanCreateMT4User(int userId, String group) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isSupportedVipGrade(int vipGrade) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCanSetVipGrade(int userId, int vipGrade) {
		return true;
	}

	@Override
	public String getVipGradeShowName(int vipGrade, int level) {
		if (level <= 3) {
			switch (level) {
			case 1:
				return "公司";
			case 2:
				return "经理";
			case 3:
				return "员工";
			}
		} else {

		}
		return "";
	}

	@Override
	public boolean isReferralCodeAvailable(int vipGrade, int level) {
		return true;
	}

	@Override
	public boolean isAgentMT4TradeInVolume() {
		return true;
	}


	
	@Override
	public String getMT4Scheme(String scheme, Users agent) {
		// TODO Auto-generated method stub
		return scheme;
	}

}

 