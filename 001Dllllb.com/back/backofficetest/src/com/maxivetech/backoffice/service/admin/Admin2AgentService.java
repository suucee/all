package com.maxivetech.backoffice.service.admin;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;






import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.util.Page;


public interface Admin2AgentService {
	public HashMap<String,Object>  getPageAgent(int pageNo, int pageSize, String urlFormat, String scheme, String keyword, HttpSession session);

}
