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
import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.AdminDao;
import com.maxivetech.backoffice.dao.AttachmentDao;
import com.maxivetech.backoffice.dao.CDDChecksDao;
import com.maxivetech.backoffice.dao.DepositDao;
import com.maxivetech.backoffice.dao.SettingDao;
import com.maxivetech.backoffice.dao.UserBankAccountDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.dao.WithdrawalDao;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Attachments;
import com.maxivetech.backoffice.entity.CDDChecks;
import com.maxivetech.backoffice.entity.Deposits;
import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.entity.Withdrawals;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.sendmail.PostmanUserProfile;
import com.maxivetech.backoffice.service.admin.AdminCheckService;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperPassword;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class AdminCheckServiceImpl implements AdminCheckService {

	@Autowired
	private CDDChecksDao cddChecksDao;
	@Autowired
	private DepositDao depositDao;
	@Autowired
	private AdminDao adminDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserProfilesDao userProfilesDao;
	@Autowired
	private WithdrawalDao withdrawalDao;
	@Autowired
	private AttachmentDao attachmentDao;
	@Autowired
	private UserBankAccountDao userBankAccountDao;
	@Autowired
	private SettingDao settingDao;

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer})
	@Override
	public Page<Users> getAllUser(int pageNo, int pageSize, String urlFormat, HttpSession session) {
		ArrayList<Criterion> wheres = new ArrayList<>();
		wheres.add(Restrictions.eq("state", "AUDITING"));
		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("registrationTime"));
		if (pageNo == 0) {
			pageNo = 1;
		}
		Page<Users> userarr = (Page<Users>) userDao.getPage(wheres, orders, null, pageSize, pageNo, null);
		for (Users user : userarr.getList()) {
			user.setParent(null);
			user.setPassword("");
			user.setPaymentPassword("");
			user.setSalty("");
			
			user.set_name(userProfilesDao.getUserName(user));
		}
		userarr.generateButtons(urlFormat);
		return userarr;
	}

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer})
	@Override
	public Page<Deposits> getAllDeposits(int pageNo, int pageSize, String urlFormat, HttpSession session) {
		ArrayList<Criterion> wheres = new ArrayList<>();
		wheres.add(Restrictions.eq("state", "PENDING_AUDIT"));
		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("creatTime"));
		if (pageNo == 0) {
			pageNo = 1;
		}
		Page<Deposits> depositsarr = (Page<Deposits>) depositDao.getPage(wheres, orders, null, pageSize, pageNo, null);
		for (Deposits ds : depositsarr.getList()) {
			Users user = ds.getUser();
			user.setParent(null);
			user.setPassword("");
			user.setPaymentPassword("");
			user.setSalty("");
			
			user.set_name(userProfilesDao.getUserName(user));
			ds.setUser(user);
		}
		depositsarr.generateButtons(urlFormat);
		return depositsarr;
	}

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer})
	@Override
	public Page<Withdrawals> getAllWithdrawals(int pageNo, int pageSize, String urlFormat, HttpSession session) {
		ArrayList<Criterion> wheres = new ArrayList<>();
		wheres.add(Restrictions.eq("state", "WAITING"));
		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("creatTime"));
		if (pageNo == 0) {
			pageNo = 1;
		}
		Page<Withdrawals> withdrawalarr = (Page<Withdrawals>) withdrawalDao.getPage(wheres, orders, null, pageSize, pageNo, null);
		withdrawalarr.generateButtons(urlFormat);
		for (Withdrawals w : withdrawalarr.getList()) {
			Users user = w.getUser();
			user.setParent(null);
			user.setPassword("");
			user.setPaymentPassword("");
			user.setSalty("");
			user.set_name(userProfilesDao.getUserName(user));
			w.setUser(user);
		}
		return withdrawalarr;
	}

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer})
	@Override
	public Page<CDDChecks> getReminderCheck(int pageNo, int pageSize, String urlFormat, String scheme, HttpSession session) {
		ArrayList<Criterion> wheres = new ArrayList<Criterion>();
		wheres.add(Restrictions.isNotNull("reminderTimestamp"));
		wheres.add(Restrictions.ge("reminderTimestamp", new Date()));
		if (scheme.equals("my")) {
			Admins admins = (Admins) adminDao.getById(HelperAuthority.getId(session));
			wheres.add(Restrictions.eq("admin", admins));
		}
		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("timestamp"));
		if (pageNo == 0) {
			pageNo = 1;
		}
		Page<CDDChecks> cddcheckarr = (Page<CDDChecks>) cddChecksDao.getPage(wheres, orders, null, pageSize, pageNo, null);
		for (CDDChecks cdd : cddcheckarr.getList()) {
			if (cdd.getUser() != null) {
				Users user = cdd.getUser();
				user.setParent(null);
				user.setPassword(null);
				user.setPaymentPassword(null);
				cdd.setUser(user);
			}
			if (cdd.getDeposit() != null) {
				Deposits deposits = cdd.getDeposit();
				Users user = deposits.getUser();
				user.setParent(null);
				user.setPassword(null);
				user.setPaymentPassword(null);
				deposits.setUser(user);
				cdd.setDeposit(deposits);
			}
			if (cdd.getWithdrawal() != null) {
				Withdrawals withdrawals = cdd.getWithdrawal();
				Users user = withdrawals.getUser();
				user.setParent(null);
				user.setPassword(null);
				user.setPaymentPassword(null);
				withdrawals.setUser(user);
				cdd.setWithdrawal(withdrawals);
			}
		}
		cddcheckarr.generateButtons(urlFormat);
		return cddcheckarr;
	}

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer})
	@Override
	public HashMap<String, Object> getAllChecks(int pageNo, int pageSize, String urlFormat, String scheme, HttpSession session) {

		HashMap<String, Object> result = new HashMap<String, Object>();
		ArrayList<Criterion> wheres = new ArrayList<>();
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select");
//		sqlBuilder.append(" ds.amount as deposit_amount,");
//		sqlBuilder.append(" wd.amount as withdrawal_amount,");;
	    sqlBuilder.append(" wd.account_name as withdrawal_account_name,");
		sqlBuilder.append(" ad.show_name as admin_name,");
		sqlBuilder.append(" cdd.id as id,");
		sqlBuilder.append(" cdd.reminder_timestamp as reminderTimestamp,");
		sqlBuilder.append(" cdd.timestamp as timestamp,");
		sqlBuilder.append(" cdd.result as result,");
		sqlBuilder.append(" u.id as user_id,");
		sqlBuilder.append(" u.vip_grade as user_vip_grade,");
		sqlBuilder.append(" u.level as u_level,");
		sqlBuilder.append(" u.email as user_email,");
		sqlBuilder.append(" u.state as user_state,");
		sqlBuilder.append(" u.registration_time as user_registrationTime,");
		sqlBuilder.append(" up.name as _name,");
		sqlBuilder.append(" ds.email as deposit_email,");
		sqlBuilder.append(" ds.vip_grade as deposit_vip_grade,");
		sqlBuilder.append(" ds.level as deposit_level,");
		sqlBuilder.append(" ds.creat_time as deposit_creatTime,");
		sqlBuilder.append(" ds.state as deposit_state,");
		sqlBuilder.append(" ds.id as deposit_id,");
		sqlBuilder.append(" ds.uid as deposit_uid,");
		sqlBuilder.append(" wd.email as withdrawal_email,");
		sqlBuilder.append(" wd.uid as withdrawal_uid,");
		sqlBuilder.append(" wd.vip_grade as withdrawal_vip_grade,");
		sqlBuilder.append(" wd.level as withdrawal_level,");
		sqlBuilder.append(" wd.state as withdrawal_state,");
		sqlBuilder.append(" wd.creat_time as withdrawal_creatTime,");
		sqlBuilder.append(" wd.id as withdrawal_id,");
		sqlBuilder.append(" att.path as attach_path");
		sqlBuilder.append(" from cdd_checks cdd");
		sqlBuilder.append(" left join ( select us.id as uid,us.email,us.level,us.vip_grade,dsp.* from deposits as dsp left join users as us on us.id=dsp.user_id) as ds on ds.id=cdd.deposit_id");
		sqlBuilder.append(" left join ( select us.id as uid,us.email,us.level,us.vip_grade,wth.* from withdrawals as wth left join users as us on us.id=wth.user_id) as wd on wd.id=cdd.withdrawal_id");
		sqlBuilder.append(" left join users u  on u.id=cdd.user_id");
		sqlBuilder.append(" left join user__profiles up on u.id=up.user_id");
		sqlBuilder.append(" left join admins ad  on ad.id=cdd.admin_id");
		sqlBuilder.append(" left join attachments att  on att.owner_id=cdd.id and owner_type='cddcheck'");		
		sqlBuilder.append(" where cdd.user_bank_account_id is null");
		switch (scheme) {
		case "my":
			Admins admins = (Admins) adminDao.getById(HelperAuthority.getId(session));
			sqlBuilder.append("  and cdd.admin_id=" + admins.getId());
			break;
		case "accept_user":
			sqlBuilder.append(" and cdd.user_id is not null  and cdd.user_id!=0 and cdd.result='ACCEPTED'");
			break;
		case "reject_user":
			sqlBuilder.append(" and cdd.user_id is not null  and cdd.user_id!=0 and cdd.result='REJECTED'");
			break;
		case "accept_withdrawal":
			sqlBuilder.append(" and cdd.withdrawal_id is not null  and cdd.withdrawal_id!=0 and cdd.result='ACCEPTED'");
			break;
		case "reject_withdrawal":
			sqlBuilder.append(" and cdd.withdrawal_id is not null  and cdd.withdrawal_id!=0 and cdd.result='REJECTED'");
			break;
		case "accept_deposit":
			sqlBuilder.append(" and cdd.deposit_id is not null  and cdd.deposit_id!=0 and cdd.result='ACCEPTED'");
			break;
		case "reject_deposit":
			sqlBuilder.append(" and cdd.deposit_id is not null  and cdd.deposit_id!=0 and cdd.result='REJECTED'");
			break;

		default:
			break;
		}
		
		sqlBuilder.append("  order by cdd.timestamp desc"); 
		String sqlsString = sqlBuilder.toString();
		System.out.println(sqlsString);
		
		Query query=cddChecksDao.getSession().createSQLQuery(sqlsString);
		int totalRows=query.list().size();
		query.setFirstResult((pageNo - 1) * pageSize)
				.setMaxResults(pageSize)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<HashMap<String, Object>> list=query.list();
		Page<HashMap<String, Object>> page = new Page<HashMap<String, Object>>(
				totalRows, pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		result.put("cddcheckarr", page);
		result.put("imgurl", BackOffice.getInst().UPLOAD_URL_ROOT);
		
		for (HashMap<String, Object> map : page.getList()) {
			if (map.get("_name") == null) {
				if (map.get("deposit_uid") != null && (int)map.get("deposit_uid") > 0) {
					map.put("_name", userProfilesDao.getUserName((Users)userDao.getById((int)map.get("deposit_uid"))));
				} else if (map.get("withdrawal_uid") != null && (int)map.get("withdrawal_uid") > 0) {
					map.put("_name", userProfilesDao.getUserName((Users)userDao.getById((int)map.get("withdrawal_uid"))));
				}
			}
			
			
			if (map.get("user_vip_grade") != null && (int)map.get("user_vip_grade") > 0) {
				map.put("_vipGradeName", BackOffice.getInst().companyHook.getVipGradeShowName((int)map.get("user_vip_grade"), (int)map.get("u_level")));			
			} else if (map.get("deposit_vip_grade") != null && (int)map.get("deposit_vip_grade") > 0) {
				map.put("_vipGradeName", BackOffice.getInst().companyHook.getVipGradeShowName((int)map.get("deposit_vip_grade"), (int)map.get("deposit_level")));			
			} else if (map.get("withdrawal_vip_grade") != null && (int)map.get("withdrawal_vip_grade") > 0) {
				map.put("_vipGradeName", BackOffice.getInst().companyHook.getVipGradeShowName((int)map.get("withdrawal_vip_grade"), (int)map.get("withdrawal_level")));			
			} else {
				map.put("_vipGradeName", "");
			}
		}
		
		return result;
	}


	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer})
	@Override
	public Map<String, Object> getOneUserCheck(int id, HttpSession session) {
		Users user = (Users) userDao.getById(id);
		UserProfiles userProfiles = userProfilesDao.findUserProfilesByUser(user);
		user.setParent(null);
		user.setPassword(null);
		user.setPaymentPassword(null);
		
		Map<String, Object> hMap = new HashMap<String, Object>();
		if (userProfiles == null) {
			userProfiles = new UserProfiles();
		}else{
			userProfiles.setUser(null);
			List<Attachments> attachments = new ArrayList<Attachments>();
			attachments = attachmentDao.getAllTypeFile(user, UserProfiles.getOwnertype());
			if (attachments != null) {
				for (int i = 0; i < attachments.size(); i++) {
					Attachments aa = attachments.get(i);
					aa.setUser(null);
					attachments.set(i, aa);
				}
				
				if (attachments.size() == 0) {
					hMap.put("attach", null);
				} else {
					hMap.put("attach", attachments);
				}
			}
			
		}
		UserBankAccounts ubank = userBankAccountDao.getBank(user);
		if (ubank != null) {
			ubank.setUser(null);
		}
		
		CDDChecks cdd = cddChecksDao.lastCDDChecks(user, null, null);
		if (cdd != null){
			hMap.put("cdd", cdd);
			cdd.setDeposit(null);
			cdd.setWithdrawal(null);
		}
		hMap.put("bank_msg", ubank);
		hMap.put("user_msg", userProfiles);
		hMap.put("user", user);
		hMap.put("imgurl", BackOffice.getInst().UPLOAD_URL_ROOT);
		
		return hMap;
	}

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer})
	@Override
	public Map<String, Object> getOneDeposits(int id, HttpSession session) {
				
		Deposits ds = (Deposits) depositDao.getById(id);
		Users user = ds.getUser();
		ds.setUser(null);
		UserProfiles userProfiles = userProfilesDao.findUserProfilesByUser(user);
		user.setParent(null);
		user.setPassword(null);
		user.setPaymentPassword(null);
		
		Map<String, Object> hMap = new HashMap<String, Object>();
		if (userProfiles == null) {
			userProfiles = new UserProfiles();
		}else{
		userProfiles.setUser(null);
		List<Attachments> attachments = new ArrayList<Attachments>();
		attachments = attachmentDao.getAllTypeFile(user, UserProfiles.getOwnertype());
		
		for (int i = 0; i < attachments.size(); i++) {
			Attachments aa = attachments.get(i);
			aa.setUser(null);
			attachments.set(i, aa);
		}
		
		if (attachments.size() == 0) {
			hMap.put("attach", null);
		} else {
			hMap.put("attach", attachments);
		}
		}
		UserBankAccounts ubank = userBankAccountDao.getBank(user);
		if (ubank != null) {
			ubank.setUser(null);
		}
		ds.setUser(null);
		CDDChecks cdd = cddChecksDao.lastCDDChecks(null, ds, null);
		if(cdd != null){
			hMap.put("cdd", cdd);
			cdd.setUser(null);
			cdd.setWithdrawal(null);
		}
		hMap.put("bank_msg", ubank);
		hMap.put("ds", ds);
		hMap.put("user_msg", userProfiles);
		hMap.put("user", user);
		hMap.put("imgurl", BackOffice.getInst().UPLOAD_URL_ROOT);
		return hMap;
	}

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer})
	@Override
	public Map<String, Object> getOneWithdrawals(int id, HttpSession session) {
		
		Withdrawals wd = (Withdrawals) withdrawalDao.getById(id);
		Users user = wd.getUser();
		wd.setUser(null);
		
		user.setParent(null);
		user.setPassword(null);
		user.setPaymentPassword(null);
		user.setSalty(null);
		
		Map<String, Object> hMap = new HashMap<String, Object>();
		UserProfiles userProfiles = userProfilesDao.findUserProfilesByUser(user);
		if (userProfiles != null ) {
			userProfiles.setUser(null);
			
			List<Attachments> attachments = new ArrayList<Attachments>();
			attachments = attachmentDao.getAllTypeFile(user, UserProfiles.getOwnertype());
			
			for (int i = 0; i < attachments.size(); i++) {
				Attachments aa = attachments.get(i);
				aa.setUser(null);
				attachments.set(i, aa);
			}
			
			if (attachments.size() == 0) {
				hMap.put("attach", null);
			} else {
				hMap.put("attach", attachments);
			}
		}
		
		UserBankAccounts ubank = userBankAccountDao.getBank(user);
		if (ubank != null) {
			ubank.setUser(null);
		}
		CDDChecks cdd = cddChecksDao.lastCDDChecks(null, null, wd);
		if (cdd != null){
			hMap.put("cdd", cdd);
			cdd.setUser(null);
			cdd.setDeposit(null);
			cdd.setWithdrawal(null);
		}
		
		hMap.put("bank_msg", ubank);
		hMap.put("wd", wd);
		hMap.put("user_msg", userProfiles);
		hMap.put("user", user);
		hMap.put("imgurl", BackOffice.getInst().UPLOAD_URL_ROOT);
		
		return hMap;
	}

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer})
	@Override
	public int doCheck(int id, String type, String dotype, String comment, String user_comment, String remindertime, String tag, String dopassword, int attach_id, HttpSession session) {

		int issuccess = 1;// 1操作成功，2操作密码错误，3操作失败,4用户资料为空
		Admins admin = (Admins) adminDao.getById(HelperAuthority.getId(session));
		Attachments attachments = (Attachments) attachmentDao.getById(attach_id);
		boolean isright = HelperPassword.verifyPassword(dopassword, admin.getOperationPassword(), admin.getSalty());
		if (isright) {
			CDDChecks cdd = new CDDChecks();
			Date date = new Date();
			switch (type) {
			case "deposits":
				Deposits ds = (Deposits) depositDao.getById(id);
				if (ds.getState().equals("PENDING_AUDIT") ||  ds.getState().equals("ACCEPTED") || ds.getState().equals("REJECTED")) {
					ds.setAuditedTime(date);
					ds.setAuditedMemo(comment);
					System.out.println(dotype);
					
					switch (dotype) {
					case "ACCEPTED":	//只有代为入金才需要审核
						ds.setState("PENDING_SUPERVISOR");
						cdd.setResult("ACCEPTED");
						cdd.setResult("REJECTED");
						break;
					case "REJECTED":
						ds.setState("REJECTED");
						break;
					default:
						throw new RuntimeException("非法审核参数！");
					}
					
					ds.setUserComment(user_comment);
					depositDao.saveOrUpdate(ds);
					
					cdd.setAdmin(admin);
					cdd.setComment(comment);
					cdd.setDeposit(ds);
					cdd.setSnapshot("");
					cdd.setTag(tag);
					cdd.setUrl("");
					cdd.setTimestamp(date);
					if (!remindertime.equals("0")) {
						date = new Date((date.getTime() + Long.parseLong(remindertime)));
						cdd.setReminderTimestamp(date);
					}
					cddChecksDao.save(cdd);
					if(attachments != null){
						attachments.setOwnerId(cdd.getId());
						attachments.setOwnerType("cddcheck");
						attachmentDao.saveOrUpdate(attachments);
					}
					cddChecksDao.commit();
				} else {
					issuccess = 3;
				}
				break;
			case "users":
				Users users = (Users) userDao.getById(id);
				if (true) {
					UserProfiles uProfiles = userProfilesDao.findUserProfilesByUser(users);
					if (uProfiles != null) {
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
						switch (dotype) {
						case "ACCEPTED":
							users.setState("VERIFIED");
							cdd.setResult("ACCEPTED");
							break;
						case "REJECTED":
							users.setState("REJECTED");
							cdd.setResult("REJECTED");
							break;
						default:
							throw new RuntimeException("非法审核参数！");
						}

						userDao.saveOrUpdate(users);
						
						cdd.setAdmin(admin);
						cdd.setComment(comment);
						cdd.setUser(users);
						cdd.setSnapshot("");
						cdd.setTag(tag);
						cdd.setUrl("");
						cdd.setTimestamp(date);
						if (!remindertime.equals("0")) {
							date = new Date((date.getTime() + Long.parseLong(remindertime)));
							cdd.setReminderTimestamp(date);
						}
						cddChecksDao.save(cdd);
						if(attachments != null){
							attachments.setOwnerId(cdd.getId());
							attachments.setOwnerType("cddcheck");
							attachmentDao.saveOrUpdate(attachments);
						}
						cddChecksDao.commit();
						
						//发邮件
						{
							PostmanUserProfile postman = new PostmanUserProfile(users.getEmail());
							switch (dotype) {
							case "ACCEPTED":	//资料通过
								postman.verified();
								break;
							case "REJECTED":	//资料批回（带原因）
								postman.rejected(uProfiles.getUserComment());
								break;
							}
						}
					} else {
						issuccess = 4;
					}
				} else {
					issuccess = 3;
				}
				break;
			case "withdrawals":
				Withdrawals wd = (Withdrawals) withdrawalDao.getById(id);
				// 出金金额大于等于3000状态设置待财务主管审核
				if (wd.getState().equals("WAITING") || wd.getState().equals("AUDITED") || wd.getState().equals("REJECTED") || wd.getState().equals("PENDING_SUPERVISOR")) {
					switch (dotype) {
					case "ACCEPTED":
						if (wd.getAmount() >= settingDao.getDouble("LargeAmountWithdrawal")) {
							wd.setState("PENDING_SUPERVISOR");
						} else {
							wd.setState("AUDITED");
						}
						cdd.setResult("ACCEPTED");
						break;
						// 驳回
					case "REJECTED":
						wd.setState("REJECTED");
						cdd.setResult("REJECTED");
						break;
					default:
						throw new RuntimeException("非法审核参数！");
					}

					wd.setAuditedTime(date);
					wd.setAuditedMemo(user_comment);
					withdrawalDao.saveOrUpdate(wd);
					
					cdd.setAdmin(admin);
					cdd.setComment(comment);
					cdd.setWithdrawal(wd);
					cdd.setSnapshot("");
					cdd.setTag(tag);
					cdd.setUrl("");
					cdd.setTimestamp(date);
					if (!remindertime.equals("0")) {
						date = new Date((date.getTime() + Long.parseLong(remindertime)));
						cdd.setReminderTimestamp(date);
					}
					cddChecksDao.save(cdd);
					if(attachments !=null){
						attachments.setOwnerId(cdd.getId());
						attachments.setOwnerType("cddcheck");
						attachmentDao.saveOrUpdate(attachments);
					}
					cddChecksDao.commit();
				} else {
					issuccess = 3;
				}
				break;
			default:
				break;
			}
		} else {
			issuccess = 2;
		}
		return issuccess;
	}

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer})
	@Override
	public List<PojoCounts> getAllCount(HttpSession session) {
		List<PojoCounts> list = new ArrayList<PojoCounts>();
		Admins admins = (Admins) adminDao.getById(HelperAuthority.getId(session));
		{
			String hql = "select count(*) from CDDChecks where reminderTimestamp is not null and reminderTimestamp >NOW()";
			long count = (long) depositDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("allreminder", count));
		}
		{
			String hql = "select count(*) from CDDChecks cdd where cdd.reminderTimestamp is not null and cdd.reminderTimestamp >NOW() and cdd.admin.id=" + admins.getId();
			long count = (long) depositDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("myreminder", count));
		}
		{
			String hql = "select count(*) from Deposits where state='PENDING_AUDIT'";
			long count = (long) depositDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("deposits", count));
		}
		{
			String hql = "select count(*) from Withdrawals where state='WAITING'";
			long count = (long) withdrawalDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("withdrawals", count));
		}
		{
			String hql = "select count(*) from Users where state='AUDITING'";
			long count = (long) userDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("users", count));
		}
		{
			String hql = "select count(*) from   CDDChecks where deposit!=null and result='ACCEPTED'";
			long count = (long) depositDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("accept_deposits", count));
		}
		{
			String hql = "select count(*) from  CDDChecks where deposit!=null and result='REJECTED'";
			long count = (long) depositDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("reject_deposits", count));
		}
		{
			String hql = "select count(*) from  CDDChecks where withdrawal!=null and result='ACCEPTED'";
			long count = (long) withdrawalDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("accept_withdrawals", count));
		}
		{
			String hql = "select count(*) from  CDDChecks where withdrawal!=null and result='REJECTED'";
			long count = (long) withdrawalDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("reject_withdrawals", count));
		}

		{
			String hql = "select count(*) from  CDDChecks where user!=null and result='ACCEPTED'";
			long count = (long) userDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("accept_users", count));
		}
		{
			String hql = "select count(*) from CDDChecks where user!=null and  result='REJECTED'";
			long count = (long) userDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("reject_users", count));
		}

		{
			String hql = "select count(*) from  CDDChecks";
			long count = (long) userDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("allchecks", count));
		}
		{
			String hql = "select count(*) from CDDChecks cdd where cdd.admin.id=" + admins.getId();
			long count = (long) userDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("mychecks", count));
		}
		{
			String hql = "select count(*) from UserBankAccounts uba where uba.state!='DELETED'";
			long count = (long) userDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("bank_account", count));
		}
		{
			String hql = "select count(*) from CDDChecks cdd where cdd.userBankAccounts!=NULL";
			long count = (long) userDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("bank_check_account", count));
		}
		{
			String hql = "select count(*) from UserBankAccounts uba where uba.state='WAITING'";
			long count = (long) userDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("pending_bank", count));
		}
		return list;
	}

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer})
	@Override
	public int freezeOrDisableUser(int id, String scheme, HttpSession session) {

		Users user = (Users) userDao.getById(id);
		if (scheme.equals("disable")) {
			user.setDisable(!user.isDisable());
		}
		if (scheme.equals("freeze")) {
			user.setFrozen(!user.isFrozen());
		}
		userDao.saveOrUpdate(user);
		userDao.commit();
		return 0;
	}


	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer})
	@Override
	public boolean cancelReminber(int id, HttpSession session) {

		boolean result = true;
		CDDChecks cddChecks = (CDDChecks) cddChecksDao.getById(id);
		cddChecks.setReminderTimestamp(null);
		cddChecksDao.saveOrUpdate(cddChecks);
		cddChecksDao.commit();
		return result;
	}

}
