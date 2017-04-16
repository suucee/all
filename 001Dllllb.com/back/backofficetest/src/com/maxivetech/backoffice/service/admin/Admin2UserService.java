package com.maxivetech.backoffice.service.admin;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.maxivetech.backoffice.entity.UserBalanceLogs;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoDepositUser;
import com.maxivetech.backoffice.pojo.PojoSession;
import com.maxivetech.backoffice.util.Page;


public interface Admin2UserService {
	public  HashMap<String,Object>  getTreeList(HttpSession session);

	Page<PojoDepositUser> findUsersByKeyword(int pageNo, int pageSize, String urlFormat, String keyword,
			HttpSession session);

	boolean saveUserTags(int user_id, String tags, HttpSession session);

	public boolean updateProfile(int userId, String name, String ename, String cardType, String cardID, String countryCode,
			String address, String company, String userIndustry, String userYearsIncom, String position,
			String attachment_id, HttpSession session);

	public void modifyAccount(int userId, String email, String mobile, HttpSession session);

	public void setParent(int userId, int upId, HttpSession session);
	
	
	public boolean removeImg(int userid, int id, HttpSession session);
	
	public PojoSession holdUser(int userId, String backurl, HttpSession session);

	public Page<HashMap<String, Object>> getPage(int pageNo, int pageSize, String urlFormat, String state, String keyword,
			HttpSession session);

	public HashMap<String, Object> getAgentCoustomers(int agentId);

	public boolean addMT4User(int userId, HttpSession session);

	Users getOne(int userId, HttpSession session);

	List<HashMap<String, Object>> getList(HttpSession session);

	boolean adminUpdateProfile(String dopassword, int userId, String email, String mobile, String name, String ename,
			String cardType, String cardID, String countryCode, String address, String company, String userIndustry,
			String userYearsIncom, String position, String attachment_id, String scheme, int vipGrade, int level,
			String staffScheme, int upId, HttpSession session);


	Page<UserBalanceLogs> getBalanceLogs(int pageNo, int pageSize, String urlFormat, HttpSession session);


	
}
