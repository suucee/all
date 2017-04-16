package com.maxivetech.backoffice.service.admin;


import java.util.HashMap;

import javax.servlet.http.HttpSession;
public interface Admin2RiskService {
	public  HashMap<String,Object>  getAllUserCount(int pageNo, int pageSize, String urlFormat, String keyword,
			HttpSession session);
}
