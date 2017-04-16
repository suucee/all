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
import com.maxivetech.backoffice.service.admin.Admin2CheckWithdrawalService;
import com.maxivetech.backoffice.service.admin.AdminCheckService;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperDate;
import com.maxivetech.backoffice.util.HelperMoney;
import com.maxivetech.backoffice.util.HelperPassword;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class Admin2CheckWithdrawalServiceImpl implements Admin2CheckWithdrawalService {

	@Autowired
	private CDDChecksDao cddChecksDao;
	@Autowired
	private AdminDao adminDao;
	@Autowired
	private WithdrawalDao withdrawalDao;
	@Autowired
	private AttachmentDao attachmentDao;
	@Autowired
	private SettingDao settingDao;
	@Autowired
	private NotifyDao notifyDao;

	@Override
	public Withdrawals getOneWithdrawals(int id, HttpSession session) {
		Withdrawals wd = (Withdrawals) withdrawalDao.getById(id);
		if(wd==null){
			throw new RuntimeException("该出金单不存在。");
		}
		wd.set_userId(wd.getUser().getId());
		wd.setUser(null);
		return wd;
	}


	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer})
	@CheckOperationPassword(parameterIndex = 5)
	@Override
	public boolean doCheck(int id, String scheme, String admin_comment, String user_comment,String reminder , String dopassword,
			String attach_id, HttpSession session) {

		Admins admin = (Admins) adminDao.getById(HelperAuthority.getId(session));
		boolean isright = HelperPassword.verifyPassword(dopassword, admin.getOperationPassword(), admin.getSalty());
		CDDChecks cdd = new CDDChecks();
		Date date = new Date();
		Withdrawals wd = (Withdrawals) withdrawalDao.getById(id);
		// 出金金额大于等于3000状态设置待财务主管审核
		if (wd.getState().equals("WAITING") || wd.getState().equals("AUDITED") || wd.getState().equals("REJECTED")
				|| wd.getState().equals("PENDING_SUPERVISOR")) {
			String content="";
			switch (scheme) {
			case "ACCEPTED":
				if (wd.getAmount() >= settingDao.getDouble("LargeAmountWithdrawal")) {
					wd.setState("PENDING_SUPERVISOR");
				} else {
					wd.setState("AUDITED");
//					content="你的金额为"+wd.getAmount()+"的出金申请已经通过审核！";
//					notifyDao.insertNotify(content, null, wd.getUser(),wd.getId(),Notify.getWithdrawaltype());
				}
				cdd.setResult("ACCEPTED");
				break;
				
			// 驳回
			case "REJECTED":
				wd.setState("REJECTED");
				cdd.setResult("REJECTED");
				content="你的金额为"+HelperMoney.formatMoney(wd.getAmount())+"的出金申请已经被驳回！";
				notifyDao.insertNotify(content, null, wd.getUser(),wd.getId(),Notify.getWithdrawaltype());
				break;
			default:
				throw new RuntimeException("审核失败。（非法审核参数）");
			}

			wd.setAuditedTime(date);
			wd.setAuditedMemo(user_comment);
			wd.setDateTime(date);//设置支付时间
			withdrawalDao.saveOrUpdate(wd);

			cdd.setAdmin(admin);
			cdd.setComment(admin_comment);
			cdd.setWithdrawal(wd);
			cdd.setSnapshot("");
			cdd.setTag("");
			cdd.setUrl("");
			cdd.setTimestamp(date);cdd.setReminderTimestamp(HelperDate.parse(reminder, "yyyy-MM-dd HH:mm:ss"));
			cddChecksDao.save(cdd);
			if (!attach_id.equals("")) {
				String[] s = attach_id.split(",");
				for (String ss : s) {
					int aid = 0;
					try {
						aid = Integer.valueOf(ss);
					} catch (Exception e) {
					}
					if (aid != 0) {
						Attachments attachments = (Attachments) attachmentDao.getById(aid);
						attachments.setOwnerId(cdd.getId());
						attachments.setOwnerType("cddcheck");
						attachmentDao.saveOrUpdate(attachments);
					}
				}
			}
			cddChecksDao.commit();
		} else {
			throw new RuntimeException("审核失败！");
		}
		return true;

	}
}
