package com.maxivetech.backoffice.service.admin.impl;

import java.sql.Timestamp;
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
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
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
import com.maxivetech.backoffice.pojo.PojoCddCheck;
import com.maxivetech.backoffice.pojo.PojoCddCheckReminder;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.sendmail.PostmanUserProfile;
import com.maxivetech.backoffice.service.admin.Admin2CheckService;
import com.maxivetech.backoffice.service.admin.AdminCheckService;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperDate;
import com.maxivetech.backoffice.util.HelperPassword;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class Admin2CheckServiceImpl implements Admin2CheckService {

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
    
	
	@Override
	public List<PojoCounts> getCddCheckCounts(HttpSession session){
		List<PojoCounts> list = new ArrayList<PojoCounts>();
		Admins admins = (Admins) adminDao.getById(HelperAuthority.getId(session));
		{
			String hql = "select count(*) from CDDChecks where user is not null  and  admin.id="+admins.getId();
			long count = (long) depositDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("my_user", count));
		}
		{
			String hql = "select count(*) from CDDChecks where withdrawal is not null  and  admin.id="+admins.getId();
			long count = (long) depositDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("my_withdrawal", count));
		}
		{
			String hql = "select count(*) from CDDChecks where userBankAccounts is not null  and  admin.id="+admins.getId();
			long count = (long) depositDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("my_bank", count));
		}
		{
			String hql = "select count(*) from CDDChecks where user is not null";
			long count = (long) depositDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("all_user", count));
		}
		{
			String hql = "select count(*) from CDDChecks where withdrawal is not null";
			long count = (long) depositDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("all_withdrawal", count));
		}
		{
			String hql = "select count(*) from CDDChecks where userBankAccounts is not null";
			long count = (long) depositDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("all_bank", count));
		}
		return list;
	}
	
	
	
	@Override
	public List<PojoCounts> getAllCount(HttpSession session) {
		List<PojoCounts> list = new ArrayList<PojoCounts>();
		Admins admins = (Admins) adminDao.getById(HelperAuthority.getId(session));
		
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
			String hql = "select count(*) from UserBankAccounts uba where uba.state='WAITING'";
			long count = (long) userDao.getSession().createQuery(hql).uniqueResult();
			list.add(new PojoCounts("banks", count));
		}
		return list;
	}

	
	
	
	@Override
	public Page<PojoCddCheck> getCddchekList(int pageNo, int pageSize, String urlFormat, String scheme,String keyWord,String state, HttpSession session){
		Admins admins = (Admins) adminDao.getById(HelperAuthority.getId(session));
		StringBuilder selectBuilder=new StringBuilder("");
		StringBuilder columnBuilder=new StringBuilder("");
		StringBuilder countBuilder=new StringBuilder("");
		StringBuilder joinBuilder=new StringBuilder("");
		StringBuilder whereBuilder=new StringBuilder("");
		StringBuilder limitBuilder=new StringBuilder("");
		selectBuilder.append(" SELECT ");
		countBuilder.append("  count(*)");
		columnBuilder.append(" if(cdd.withdrawal_id is not  null,cdd.withdrawal_id,if(cdd.user_id is not  null,cdd.user_id,if(cdd.user_bank_account_id is  not null,cdd.user_bank_account_id,if(cdd.deposit_id is  not null,cdd.deposit_id,'')) ) )as id, ");
		columnBuilder.append(" ad.show_name as adminName, ");
		columnBuilder.append(" cdd.id as cid, ");
		columnBuilder.append(" up.name as userName, ");
		columnBuilder.append(" cdd.timestamp as checkTime, ");
		columnBuilder.append(" att.path as attachment, ");
		columnBuilder.append(" cdd.result as state, ");
		columnBuilder.append(" if(cdd.withdrawal_id is not null,'出金',if(cdd.user_id is not null,'用户',if(cdd.user_bank_account_id is not null,'银行卡',if(cdd.deposit_id is not null,'入金','')) ) ) as checkType ");
		joinBuilder.append(" FROM  cdd_checks cdd ");
		joinBuilder.append(" left join  admins ad on  ad.id=cdd.admin_id ");
		joinBuilder.append(" left join deposits as ds on ds.id=cdd.deposit_id ");
		joinBuilder.append(" left join  withdrawals as wd on wd.id=cdd.withdrawal_id ");
		joinBuilder.append(" left join users u  on u.id=cdd.user_id ");
		joinBuilder.append(" left join attachments att  on att.owner_type='cddcheck' and att.owner_id=cdd.id ");
		joinBuilder.append(" left join  user__bank_accounts as uba on uba.id=cdd.user_bank_account_id  ");
		joinBuilder.append(" left join (select upf.*  from user__profiles upf,(select max(upf1.id) as id,upf1.user_id from user__profiles upf1 group by upf1.user_id) upf2 where upf.id=upf2.id) up ");
		joinBuilder.append(" on up.user_id=u.id or up.user_id=ds.user_id or wd.user_id=up.user_id  or uba.user_id=up.user_id");
		switch (scheme) {
		case "my_check_user":
			whereBuilder.append("  where");
			whereBuilder.append("   cdd.admin_id="+admins.getId());
			whereBuilder.append("  and cdd.user_id is not null");
			break;
		case "my_check_withdrawal":
			whereBuilder.append("  where");
			whereBuilder.append("   cdd.admin_id="+admins.getId());
			whereBuilder.append("  and cdd.withdrawal_id is not null");
			break;
		case "my_check_bank":
			whereBuilder.append("  where");
			whereBuilder.append("   admin_id="+admins.getId());
			whereBuilder.append("  and cdd.user_bank_account_id is not null");
			break;
		case "all_check_user":
			whereBuilder.append("  where");
			whereBuilder.append("  cdd.user_id is not null");
			break;
		case "all_check_withdrawal":
			whereBuilder.append("  where");
			whereBuilder.append("  cdd.withdrawal_id is not null");
			break;
		case "all_check_bank":
			whereBuilder.append("  where");
			whereBuilder.append("  cdd.user_bank_account_id is not null");
			break;
		default:
			break;
		}
		
		if(state.equals("all")){
			whereBuilder.append("");
		}
		if(state.equals("accepted")){
			whereBuilder.append("  and result='ACCEPTED'");
		}
		if(state.equals("rejected")){
			whereBuilder.append("  and result='REJECTED'");
		}
		whereBuilder.append(" order by cdd.id desc");
		if(!keyWord.equals("")){
			whereBuilder.append("   and (up.name  like  '"+keyWord+"'  or u.email  like  '"+keyWord+"' or u.mobile  like  '"+keyWord+"')");
		}
		limitBuilder.append("  limit "+pageSize+" offset "+(pageNo-1)*pageSize);
		String countSql=selectBuilder.toString()+countBuilder.toString()+joinBuilder.toString()+whereBuilder.toString();
		String selectSql=selectBuilder.toString()+columnBuilder.toString()+joinBuilder.toString()+whereBuilder.toString()+limitBuilder.toString();
		String countStr=String.valueOf(cddChecksDao.getSession().createSQLQuery(countSql).uniqueResult());
		int totalRows=Integer.parseInt(countStr);
		System.out.println(selectSql);
		List<PojoCddCheck>  list=cddChecksDao.getSession().createSQLQuery(selectSql)
				.addScalar("id",IntegerType.INSTANCE)
				.addScalar("cid",IntegerType.INSTANCE)
				.addScalar("adminName",StringType.INSTANCE)
				.addScalar("userName",StringType.INSTANCE)
				.addScalar("state",StringType.INSTANCE)
				.addScalar("checkTime", TimestampType.INSTANCE)
				.addScalar("checkType",StringType.INSTANCE)
				.addScalar("attachment",StringType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(PojoCddCheck.class))
				.list();
		System.out.println(selectSql);
		Page<PojoCddCheck> page=new Page<>(totalRows, pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		return page;
	}
	
	
	@Override
	public Page<PojoCddCheckReminder> getCddchekReminderList(int pageNo, int pageSize, String urlFormat, String scheme,String keyWord,String start,String end, HttpSession session){
		Admins admins = (Admins) adminDao.getById(HelperAuthority.getId(session));
		StringBuilder selectBuilder=new StringBuilder("");
		StringBuilder columnBuilder=new StringBuilder("");
		StringBuilder countBuilder=new StringBuilder("");
		StringBuilder joinBuilder=new StringBuilder("");
		StringBuilder whereBuilder=new StringBuilder("");
		StringBuilder limitBuilder=new StringBuilder("");
		selectBuilder.append(" SELECT ");
		countBuilder.append("  count(*)");
		columnBuilder.append(" if(cdd.withdrawal_id is not  null,cdd.withdrawal_id,if(cdd.user_id is not  null,cdd.user_id,if(cdd.user_bank_account_id is  not null,cdd.user_bank_account_id,if(cdd.deposit_id is  not null,cdd.deposit_id,'')) ) ) as id, ");
		columnBuilder.append(" ad.show_name as adminName, ");
		columnBuilder.append(" cdd.id as cid, ");
		columnBuilder.append(" up.name as userName, ");
		columnBuilder.append(" u.email as email, ");
		columnBuilder.append(" cdd.reminder_timestamp as reminderTime, ");
		columnBuilder.append(" cdd.comment as comment, ");
		columnBuilder.append(" cdd.result as state, ");
		columnBuilder.append(" if(cdd.withdrawal_id is not  null,'出金',if(cdd.user_id is not  null,'用户',if(cdd.user_bank_account_id is  not null,'银行卡',if(cdd.deposit_id is  not null,'入金','')) ) ) as checkType ");
		joinBuilder.append(" FROM  cdd_checks cdd ");
		joinBuilder.append(" left join  admins ad on  ad.id=cdd.admin_id ");
		joinBuilder.append(" left join deposits as ds on ds.id=cdd.deposit_id ");
		joinBuilder.append(" left join  withdrawals as wd on wd.id=cdd.withdrawal_id ");
		joinBuilder.append(" left join users u  on u.id=cdd.user_id or u.id=wd.user_id or ds.user_id=u.id ");
		joinBuilder.append(" left join (select upf.*  from user__profiles upf,(select max(upf1.id) as id,upf1.user_id from user__profiles upf1 group by upf1.user_id) upf2 where upf.id=upf2.id) up ");
		joinBuilder.append(" on up.user_id=u.id or up.user_id=ds.user_id or wd.user_id=up.user_id");

		whereBuilder.append("  where cdd.reminder_timestamp is not null  ");
		if(scheme.equals("all")){
			whereBuilder.append("");
		}
		if(scheme.equals("accepted")){
			whereBuilder.append("  and result='ACCEPTED'");
		}
		if(scheme.equals("rejected")){
			whereBuilder.append("  and result='REJECTED'");
		}
		if(scheme.equals("my_all")){
			whereBuilder.append("  and admin_id="+admins.getId());
		}
		if(scheme.equals("my_accepted")){
			whereBuilder.append("  and admin_id="+admins.getId());
			whereBuilder.append("  and result='ACCEPTED'");
		}
		if(scheme.equals("my_rejected")){
			whereBuilder.append("  and admin_id="+admins.getId());
			whereBuilder.append("  and result='REJECTED'");
		}
		if(!keyWord.equals("")){
			whereBuilder.append("   and (up.name  like  '"+keyWord+"'  or u.email  like  '"+keyWord+"' or u.mobile  like  '"+keyWord+"')");
		}
//		limitBuilder.append(" order by  cdd.reminderTimestamp  desc  limit "+pageSize+" offset "+(pageNo-1)*pageSize);
		String countSql=selectBuilder.toString()+countBuilder.toString()+joinBuilder.toString()+whereBuilder.toString();
		String selectSql=selectBuilder.toString()+columnBuilder.toString()+joinBuilder.toString()+whereBuilder.toString()+limitBuilder.toString();
		String countStr=String.valueOf(cddChecksDao.getSession().createSQLQuery(countSql).uniqueResult());
		int totalRows=Integer.parseInt(countStr);
		System.out.println(selectSql);
		List<PojoCddCheckReminder>  list=cddChecksDao.getSession().createSQLQuery(selectSql)
				.addScalar("id",IntegerType.INSTANCE)
				.addScalar("cid",IntegerType.INSTANCE)
				.addScalar("adminName",StringType.INSTANCE)
				.addScalar("userName",StringType.INSTANCE)
				.addScalar("state",StringType.INSTANCE)
				.addScalar("reminderTime",TimestampType.INSTANCE)
				.addScalar("checkType",StringType.INSTANCE)
				.addScalar("email",StringType.INSTANCE)
				.addScalar("comment",StringType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(PojoCddCheckReminder.class))
				.list();
		Page<PojoCddCheckReminder> page=new Page<>(totalRows, pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		return page;
	}

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer})
	@Override
	public boolean cancelReminder(int id,HttpSession session){
		CDDChecks cddChecks=(CDDChecks) cddChecksDao.getById(id);
		cddChecks.setReminderTimestamp(null);
		cddChecksDao.saveOrUpdate(cddChecks);
		cddChecksDao.commit();
		return true;
	}
	@Override
	public CDDChecks  getOneCddCheck(int id,HttpSession session) {
		CDDChecks cddChecks=(CDDChecks) cddChecksDao.getById(id);
		cddChecks.setUser(null);
		cddChecks.setDeposit(null);
		cddChecks.setWithdrawal(null);
		cddChecks.setUserBankAccounts(null);
		return cddChecks;
	}
	
	
}
