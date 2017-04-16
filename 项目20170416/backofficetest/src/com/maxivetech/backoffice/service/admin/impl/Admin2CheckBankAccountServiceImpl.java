package com.maxivetech.backoffice.service.admin.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.annotation.CheckOperationPassword;
import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.AdminDao;
import com.maxivetech.backoffice.dao.AttachmentDao;
import com.maxivetech.backoffice.dao.CDDChecksDao;
import com.maxivetech.backoffice.dao.NotifyDao;
import com.maxivetech.backoffice.dao.UserBankAccountDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Attachments;
import com.maxivetech.backoffice.entity.CDDChecks;
import com.maxivetech.backoffice.entity.Notify;
import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.pojo.PojoBankAccount;
import com.maxivetech.backoffice.pojo.PojoCDDcheckBankAccount;
import com.maxivetech.backoffice.service.admin.Admin2CheckBankAccountService;
import com.maxivetech.backoffice.service.admin.AdminCheckBankAccountService;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperDate;
import com.maxivetech.backoffice.util.HelperPassword;
import com.maxivetech.backoffice.util.Page;


@Service
@Transactional
public class Admin2CheckBankAccountServiceImpl implements Admin2CheckBankAccountService {
    @Autowired
    private UserBankAccountDao userBankAccountDao;
	@Autowired
	private UserProfilesDao userProfilesDao;
	@Autowired
	private AttachmentDao attachmentDao;
	@Autowired 
	private UserDao userDao;
	@Autowired
	private AdminDao adminDao;
	@Autowired
	private CDDChecksDao cddChecksDao;
	@Autowired
	private NotifyDao notifyDao;
	@Override
	public HashMap<String,Object> getOneUserBankAccounts(int userBankAccountId, HttpSession session){
		HashMap<String,Object> result=new  HashMap<String,Object>();
		UserBankAccounts uba=(UserBankAccounts) userBankAccountDao.getById(userBankAccountId);
		List<Attachments> list=attachmentDao.getFileImage(UserBankAccounts.getOwnertype(), userBankAccountId);
		result.put("userBankAccount", uba);
		result.put("attach", list);
		return result;
	}
	
	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer})
	@CheckOperationPassword(parameterIndex = 5)
	@Override
	public boolean doCheck(int id, String scheme,String admin_comment, String bank_comment,String reminder , String dopassword, String attach_id, HttpSession session) {
		UserBankAccounts uba=(UserBankAccounts) userBankAccountDao.getById(id);
		Admins admin = (Admins) adminDao.getById(HelperAuthority.getId(session));
		if(uba.getState().equals(scheme)){
			throw new RuntimeException("状态未改变！");
		}
		uba.setBankComment(bank_comment);
		CDDChecks cdd = new CDDChecks();
		String content="";
		switch (scheme) {
		case "ACCEPTED":
			uba.setState("AUDITED");
			cdd.setResult("ACCEPTED");
			content="你卡号为"+uba.getAccountNo()+"的银行卡已经通过审核！";
			break;
		// 驳回
		case "REJECTED":
			uba.setState("REJECTED");
			cdd.setResult("REJECTED");
			content="你卡号为"+uba.getAccountNo()+"的银行卡被驳回";
			if(!bank_comment.equals("")){
				content += "，驳回理由：" + bank_comment;
			}else{
				content += "。";
			}
			break;
		default:
			throw new RuntimeException("非法审核参数！");
		}
		notifyDao.insertNotify(content, null,uba.getUser(),uba.getId(),Notify.getBankaccounttype());
		
		userBankAccountDao.saveOrUpdate(uba);
		Date date = new Date();
		cdd.setAdmin(admin);
		cdd.setComment(admin_comment);
		cdd.setUserBankAccounts(uba);
		cdd.setSnapshot("");
		cdd.setTag("");
		cdd.setUrl("");
		cdd.setTimestamp(date);cdd.setReminderTimestamp(HelperDate.parse(reminder, "yyyy-MM-dd HH:mm:ss"));
		cddChecksDao.save(cdd);
		if(!attach_id.equals("")){
		     String[] str=attach_id.split(",");
			 int c=0;
		     for (int i = 0; i < str.length; i++) {
			     try {
					c=Integer.parseInt(str[i]);
				} catch (Exception e) {
					break;
				}
			    Attachments a=(Attachments) attachmentDao.getById(c);
			    if(a!=null){
			    	a.setOwnerId(cdd.getId());
			    	a.setOwnerType(CDDChecks.getOwnertype());
			    	attachmentDao.saveOrUpdate(a);
			    }
		     }
		 }
		cddChecksDao.commit();
		return true;
	}
}
