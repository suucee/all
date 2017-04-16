package com.maxivetech.backoffice.service.user;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.entity.Attachments;
import com.maxivetech.backoffice.entity.Deposits;
import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.entity.Withdrawals;
import com.maxivetech.backoffice.pojo.PojoCapital;
import com.maxivetech.backoffice.pojo.PojoSession;
import com.maxivetech.backoffice.util.Page;


public interface User2Service {
	public HashMap<String,Object> getUserLogin(HttpSession session);
	public String updateProfile(String name, String ename, String cardType, String cardID, String countryCode, String address,
			String company, String userIndustry, String userYearsIncom, String position, HttpSession session);
	public HashMap<String, Object> getUserDetail(int user_id, HttpSession session);
	public Page<Withdrawals> getPageWithdrawal(int user_id, int pageNo, int pageSize, String urlFormat, HttpSession session);
	public Page<Deposits> getPageDeposits(int user_id, int pageNo, int pageSize, String urlFormat, HttpSession session);
	
	public HashMap<String, Object> getUserEmailAndName(HttpSession session);
	
	
	public HashMap<String, Object> getParentStuffInfo(HttpSession session);
	public HashMap<String, Object> getParentStuffInfoById(int userId);
	public HashMap<String, Object> getBankAccount(int user_id, int pageNo, int pageSize, String urlFormat,
			HttpSession session);
	public HashMap<String, Object> getUserCount(int userId, HttpSession session);
	Users getUserInfo(HttpSession session);
	Page<HashMap<String, Object>> getUserPageCapital(int user_id, int pageNo, int pageSize, String urlFormat, HttpSession session);
}
