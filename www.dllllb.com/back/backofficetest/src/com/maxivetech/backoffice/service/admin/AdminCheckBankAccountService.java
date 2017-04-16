package com.maxivetech.backoffice.service.admin;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Criterion;

import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.CDDChecks;
import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoBankAccount;
import com.maxivetech.backoffice.pojo.PojoCDDcheckBankAccount;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.util.Page;


public interface AdminCheckBankAccountService {
	public Page<PojoBankAccount> getPage(int pageNo, int pageSize, String urlFormat, String scheme, String keyword, HttpSession session);

	public HashMap<String, Object> getOne(int userBankAccountId, HttpSession session);

	public boolean doCheck(int id, String state, String comment, String user_comment,String remindertime, String tag, String dopassword, String attach_id,HttpSession session);

	public Page<PojoCDDcheckBankAccount> getBankCheckList(int pageNo, int pageSize,String urlFormat, String scheme, String keyword, HttpSession session);
	
}
