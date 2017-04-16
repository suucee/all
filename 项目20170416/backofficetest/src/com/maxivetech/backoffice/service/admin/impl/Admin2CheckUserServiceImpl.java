package com.maxivetech.backoffice.service.admin.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.annotation.CheckOperationPassword;
import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.AdminDao;
import com.maxivetech.backoffice.dao.AttachmentDao;
import com.maxivetech.backoffice.dao.CDDChecksDao;
import com.maxivetech.backoffice.dao.DepositDao;
import com.maxivetech.backoffice.dao.NotifyDao;
import com.maxivetech.backoffice.dao.SettingDao;
import com.maxivetech.backoffice.dao.UserBankAccountDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.dao.WithdrawalDao;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Attachments;
import com.maxivetech.backoffice.entity.CDDChecks;
import com.maxivetech.backoffice.entity.Deposits;
import com.maxivetech.backoffice.entity.Notify;
import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.entity.Withdrawals;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.sendmail.PostmanUserProfile;
import com.maxivetech.backoffice.service.admin.Admin2CheckService;
import com.maxivetech.backoffice.service.admin.Admin2CheckUserService;
import com.maxivetech.backoffice.service.admin.AdminCheckService;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperDate;
import com.maxivetech.backoffice.util.HelperPassword;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class Admin2CheckUserServiceImpl implements Admin2CheckUserService {

	@Autowired
	private CDDChecksDao cddChecksDao;
	@Autowired
	private AdminDao adminDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserProfilesDao userProfilesDao;
	@Autowired
	private AttachmentDao attachmentDao;
	@Autowired
	private NotifyDao notifyDao;
    
	

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer})
	@CheckOperationPassword(parameterIndex = 5)
	@Override
	public boolean doCheck(int id, String scheme, String admin_comment, String user_comment,String reminder ,String dopassword, String attach_id, HttpSession session) {
		Admins admin = (Admins) adminDao.getById(HelperAuthority.getId(session));
		CDDChecks cdd = new CDDChecks();
		Date date = new Date();
		Users users = (Users) userDao.getById(id);
		UserProfiles uProfiles = userProfilesDao.findUserProfilesByUser(users);
		if (uProfiles == null) {
			throw new RuntimeException("用户资料为空！");
		}
		uProfiles.setUserComment(user_comment);
		userProfilesDao.saveOrUpdate(uProfiles);
		if (users.getParent() != null) {
			String hql = "from Users where parent.id=" + users.getParent().getId() + "  order by serialNumber desc";
			List<Users> usList = userDao.getSession().createQuery(hql).setMaxResults(1).list();
			if (usList != null && users.getSerialNumber() == 0) {
				users.setSerialNumber((usList.get(0).getSerialNumber() + 1));
			} else if (usList == null) {
				users.setSerialNumber(1);
			}
		}
		String content="";
		switch (scheme) {
		case "ACCEPTED":
			users.setState("VERIFIED");
			cdd.setResult("ACCEPTED");
			content="你的资料已经通过审核！";
			break;
		case "REJECTED":
			users.setState("REJECTED");
			cdd.setResult("REJECTED");
			content = "你的资料被拒绝";
			if(! user_comment.trim().equals("")){
				content += "，拒绝理由：" + user_comment.trim();
			}
			content += "。";
			break;
		default:
			throw new RuntimeException("非法审核参数！");
		}
		notifyDao.insertNotify(content, null,users,users.getId(),Notify.getUsertype());
		userDao.saveOrUpdate(users);
		cdd.setAdmin(admin);
		cdd.setComment(admin_comment);
		cdd.setUser(users);
		cdd.setSnapshot("");
		cdd.setTag("");
		cdd.setUrl("");
		cdd.setTimestamp(date);
		cdd.setReminderTimestamp(HelperDate.parse(reminder, "yyyy-MM-dd HH:mm:ss"));
		cddChecksDao.save(cdd);
		if (!attach_id.equals("")) {
            String[] s=attach_id.split(",");
            for (String ss:s) {
            	int aid=0;
            	try {
    				aid=Integer.valueOf(ss);
				} catch (Exception e) {
				}
            	if(aid!=0){
        			Attachments attachments = (Attachments) attachmentDao.getById(aid);
        			attachments.setOwnerId(cdd.getId());
        			attachments.setOwnerType("cddcheck");
        			attachmentDao.saveOrUpdate(attachments);
            	}
			}
		}
		cddChecksDao.commit();
		// 发邮件
		{
			PostmanUserProfile postman = new PostmanUserProfile(users.getEmail());
			switch (scheme) {
			case "ACCEPTED": // 资料通过
				postman.verified();
				break;
			case "REJECTED": // 资料批回（带原因）
				postman.rejected(uProfiles.getUserComment());
				break;
			}
		}
		return true;
	}	
}
