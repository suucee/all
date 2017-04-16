package com.maxivetech.backoffice.service.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.maxivetech.backoffice.entity.CDDChecks;
import com.maxivetech.backoffice.entity.Deposits;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.entity.Withdrawals;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.util.Page;
public interface Admin2CheckUserService {

	public boolean doCheck(int id, String scheme, String admin_comment, String user_comment,String reminder ,  String dopassword,
			String attach_id, HttpSession session);


}
