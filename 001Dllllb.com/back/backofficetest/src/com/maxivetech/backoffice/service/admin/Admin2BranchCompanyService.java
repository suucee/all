package com.maxivetech.backoffice.service.admin;

import javax.servlet.http.HttpSession;

public interface Admin2BranchCompanyService {
	/**
	 * 添加新的员工
	 * @param name
	 * @param countryCode
	 * @param mobile
	 * @param password
	 * @param session
	 * @return
	 */
    boolean addStaff(String name,String countryCode,String mobile,String password, HttpSession session);
}
