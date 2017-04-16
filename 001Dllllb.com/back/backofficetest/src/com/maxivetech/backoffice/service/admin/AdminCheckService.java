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
public interface AdminCheckService {
     public  Page<Users> getAllUser(int pageNo, int pageSize, String urlFormat, HttpSession session);
     public  Page<Deposits> getAllDeposits(int pageNo, int pageSize,String urlFormat,HttpSession session);
     public  Page<Withdrawals> getAllWithdrawals(int pageNo, int pageSize, String urlFormat, HttpSession session);
     public  Page<CDDChecks> getReminderCheck(int pageNo, int pageSize, String urlFormat, String scheme, HttpSession session);
     public  HashMap<String , Object> getAllChecks(int pageNo, int pageSize,String urlFormat, String scheme,HttpSession session);
     public  Map<String, Object> getOneUserCheck(int id,HttpSession session);
     public  Map<String, Object> getOneDeposits(int id,HttpSession session);
     public  Map<String, Object> getOneWithdrawals(int id,HttpSession session);
     public  int  doCheck(int id,String type,String dotype,String  comment,String user_comment,String remindertime,String tag,String dopassword, int attach_id,HttpSession session);
	 public  int freezeOrDisableUser(int id, String scheme ,HttpSession session);
	 public  boolean cancelReminber(int id,HttpSession session);
	 public  List<PojoCounts> getAllCount(HttpSession session);

}
