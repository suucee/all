package com.maxivetech.backoffice.service.admin;


import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.maxivetech.backoffice.pojo.PojoResetPassword;
import com.maxivetech.backoffice.pojo.PojoRiskUserCount;
import com.maxivetech.backoffice.pojo.PojoUserMT4rades;
import com.maxivetech.backoffice.util.Page;
public interface AdminRiskService {
	public List<PojoResetPassword> getAll(String group,String keyWord,HttpSession session);
	public List<String> getGroupList(HttpSession session);
	public boolean  resetMT4Password(int[] login,String password,String passwordInvestor, String dopassword, HttpSession session);
	public boolean closeAllorders(int[] login, String dopassword, HttpSession session);
	public HashMap<String, Object> getAllMt4Orders(HttpSession session);
	public HashMap<String, Object> getMt4OrdersCountUser(HttpSession session);
	public HashMap<String, Object> getMt4OrdersCountSymbol(HttpSession session);
	public HashMap<String, Object> getMt4OrdersCountMt4Login(HttpSession session);
	public  HashMap<String,Object>  getAllUserCount(int pageNo, int pageSize, String urlFormat, String keyword,HttpSession session);
	public Page<HashMap<String, Object>> getAllUserList(int pageNo, int pageSize, String urlFormat, String keyword,String scheme,HttpSession session);
	public boolean setUserAllow(int uid, String allowtype, HttpSession session);
	public HashMap<String,Object>  getAllMT4UserList(int pageNo, int pageSize, String urlFormat, String keyword,
			String scheme,HttpSession session);
	public boolean setUserAllowTransfer(int mid, HttpSession session);
	public boolean setGroupAllowTransfer(String group, String allowtype, HttpSession session);
	
	
	
}
