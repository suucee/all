package com.maxivetech.backoffice.service.admin;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.util.Page;

public interface AdminBranchCompanyService {
    public Page<HashMap<String,Object>> getAllBranchCompany(int pageNo, int pageSize, String urlFormat,HttpSession session);
    public HashMap<String, Object> getOneBranchCompany(int id, HttpSession session);
    public boolean saveOrUpdate(int id, String companyName, String mobile,
			String email, String password, String bankName,
			String bankAccountName, String accountNo, HttpSession session);
}
