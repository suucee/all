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

public interface CompanyHookable {
	public String replaceMT4GroupShowName(String group);

	public String getMT4Group(String scheme);
	public String getMT4Scheme(String scheme, Users user);
	public boolean isCanCreateMT4User(int userId, String group);
	
	public boolean isSupportedVipGrade(int vipGrade);
	public boolean isCanSetVipGrade(int userId, int vipGrade);
	public String getVipGradeShowName(int vipGrade, int level);
	public boolean isReferralCodeAvailable(int vipGrade, int level);
	
	/**
	 * 控制计算代理业绩时是否包括代理自身开的MT4账号的订单。
	 * @return
	 */
	public boolean isAgentMT4TradeInVolume();
	
}
