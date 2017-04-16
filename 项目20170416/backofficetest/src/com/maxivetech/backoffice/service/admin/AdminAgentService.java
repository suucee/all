package com.maxivetech.backoffice.service.admin;

import java.util.List;

import javax.servlet.http.HttpSession;






import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.util.Page;


public interface AdminAgentService {
	public Page<Users> getPage(int pageNo, int pageSize, String urlFormat, String scheme, String keyword, HttpSession session);
	
	public void setAgent(int userId, int vipGrade, String operationPassword, HttpSession session);
	public void setAgent(int userId, int vipGrade, String operationPassword, double[] rebates, HttpSession session);
	
	public void setStaff(int userId, String scheme, String operationPassword, HttpSession session);
}
