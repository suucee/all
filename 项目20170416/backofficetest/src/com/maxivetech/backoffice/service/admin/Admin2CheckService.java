package com.maxivetech.backoffice.service.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.maxivetech.backoffice.entity.CDDChecks;
import com.maxivetech.backoffice.entity.Deposits;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.entity.Withdrawals;
import com.maxivetech.backoffice.pojo.PojoCddCheck;
import com.maxivetech.backoffice.pojo.PojoCddCheckReminder;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.util.Page;
public interface Admin2CheckService {
	public List<PojoCounts> getCddCheckCounts(HttpSession session);
	public Page<PojoCddCheck> getCddchekList(int pageNo, int pageSize, String urlFormat, String scheme, String keyWord,
			String state, HttpSession session);
	public Page<PojoCddCheckReminder> getCddchekReminderList(int pageNo, int pageSize, String urlFormat, String scheme,
			String keyWord, String start, String end, HttpSession session);
	public boolean cancelReminder(int id, HttpSession session);
	public List<PojoCounts> getAllCount(HttpSession session);
	public CDDChecks getOneCddCheck(int id, HttpSession session);

}
