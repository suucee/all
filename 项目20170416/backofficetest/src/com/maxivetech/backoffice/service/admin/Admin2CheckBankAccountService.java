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


public interface Admin2CheckBankAccountService {

	public HashMap<String,Object> getOneUserBankAccounts(int userBankAccountId, HttpSession session);

	public boolean doCheck(int id, String scheme, String admin_comment, String user_comment, String reminder , String dopassword,
			String attach_id, HttpSession session);

}
