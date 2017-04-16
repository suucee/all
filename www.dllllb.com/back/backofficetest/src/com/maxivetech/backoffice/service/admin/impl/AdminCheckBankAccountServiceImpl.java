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
import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.AdminDao;
import com.maxivetech.backoffice.dao.AttachmentDao;
import com.maxivetech.backoffice.dao.CDDChecksDao;
import com.maxivetech.backoffice.dao.UserBankAccountDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Attachments;
import com.maxivetech.backoffice.entity.CDDChecks;
import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.pojo.PojoBankAccount;
import com.maxivetech.backoffice.pojo.PojoCDDcheckBankAccount;
import com.maxivetech.backoffice.service.admin.AdminCheckBankAccountService;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperPassword;
import com.maxivetech.backoffice.util.Page;


@Service
@Transactional
public class AdminCheckBankAccountServiceImpl implements AdminCheckBankAccountService {
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
	@Override
	public Page<PojoBankAccount> getPage(int pageNo, int pageSize,String urlFormat, String scheme, String keyword, HttpSession session) {
		StringBuilder selectBuilder=new StringBuilder();
		StringBuilder columnsBuilder=new StringBuilder();
		StringBuilder countstrBuilder=new StringBuilder();
		StringBuilder whereBuilder=new StringBuilder();
		selectBuilder.append(" select"); 
		columnsBuilder.append(" uba.id as userBankAccountId,");
		columnsBuilder.append(" uba.account_name as accountName,");
		columnsBuilder.append(" uba.account_no as accountNo,");
		columnsBuilder.append(" uba.country_code countryCode,");
		columnsBuilder.append(" uba.bank_name as bankName,");
		columnsBuilder.append(" uba.user_id as userId,");
		columnsBuilder.append(" uba.state as state,");
		columnsBuilder.append(" uba.isdefault isDefault,");
		columnsBuilder.append(" uba.update_time updateTime,");
		columnsBuilder.append(" up.name as name");
		countstrBuilder.append(" count(*)");
		whereBuilder.append(" from user__bank_accounts uba ");
		whereBuilder.append(" left join users u on u.id=uba.user_id");
		whereBuilder.append(" left join (select upf.*  from user__profiles upf,(select max(upf1.id) as id,upf1.user_id from user__profiles upf1 group by upf1.user_id) upf2 where upf.id=upf2.id) up on  up.user_id=uba.user_id");
		whereBuilder.append(" where");
		whereBuilder.append(" uba.state!='DELETED'");
		if(!scheme.equals("")&&!scheme.equals("ALL")){
		    whereBuilder.append(" and uba.state='"+scheme+"' ");
		}
		if(!keyword.equals("")){
			whereBuilder.append(" and (");
			whereBuilder.append(" up.name like '%"+keyword+"%'");
			whereBuilder.append(" or uba.account_name like '%"+keyword+"%'");
			whereBuilder.append(" or uba.account_no like '%"+keyword+"%'");
			whereBuilder.append(" )");
		}
		whereBuilder.append(" order by UNIX_TIMESTAMP(uba.update_time) desc");
		String select=selectBuilder.toString();
		String columns=columnsBuilder.toString();
		String countstr=countstrBuilder.toString();
		String where=whereBuilder.toString();
		System.out.println(select+columns+where);
		String count =userBankAccountDao.getSession().createSQLQuery(select+countstr+where).uniqueResult().toString();
		List<PojoBankAccount> list=userBankAccountDao.getSession().createSQLQuery(select+columns+where)
				.addScalar("userBankAccountId", IntegerType.INSTANCE)
				.addScalar("accountName", StringType.INSTANCE)
				.addScalar("accountNo", StringType.INSTANCE)
				.addScalar("countryCode", StringType.INSTANCE)
				.addScalar("bankName", StringType.INSTANCE)
				.addScalar("userId", IntegerType.INSTANCE)
				.addScalar("state", StringType.INSTANCE)
				.addScalar("isDefault", BooleanType.INSTANCE)
				.addScalar("name", StringType.INSTANCE)
				.addScalar("updateTime", TimestampType.INSTANCE)
			    .setMaxResults(pageSize)
			    .setFirstResult((pageNo - 1) * pageSize)
			    .setResultTransformer(Transformers.aliasToBean(PojoBankAccount.class))
				.list();
		Page<PojoBankAccount> page=new Page<PojoBankAccount>(Integer.parseInt(count), pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		return page;
	}
    
	
	@Override
	public HashMap<String,Object> getOne(int userBankAccountId, HttpSession session){
		HashMap<String,Object> result=new  HashMap<String,Object>();
		UserBankAccounts uba=(UserBankAccounts) userBankAccountDao.getById(userBankAccountId);
		UserProfiles up=userProfilesDao.findUserProfilesByUser(uba.getUser());
		List<Attachments> userImg=null;
		if(up!=null){
			up.setUser(null);
		    userImg=attachmentDao.getFileImage(UserProfiles.getOwnertype(),up.getId());
		}
		List<Attachments> bankImg=null;
		if(uba!=null){
			result.put("user_state",uba.getUser().getState());
			result.put("user_id",uba.getUser().getId());
			result.put("email",uba.getUser().getEmail());
			result.put("mobile",uba.getUser().getMobile());
			uba.setUser(null);
		    bankImg=attachmentDao.getFileImage(UserBankAccounts.getOwnertype(),uba.getId());
		}else{
			result.put("user_state","");
			result.put("user_id","");
			result.put("email","");
			result.put("mobile","");
		}
		if(userImg!=null){
			for(Attachments a :userImg){
				a.setUser(null);
			}
		}
		if(bankImg!=null){
			for(Attachments a :bankImg){
				a.setUser(null);
			}
		}
		result.put("userBankAccount", uba);
		result.put("userProfiles", up);
		result.put("userImg",userImg);
		result.put("bankImg",bankImg);
		return result;
	}
	
	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer})
	@Override
	public boolean doCheck(int id, String state,String comment, String user_comment, String remindertime, String tag, String dopassword, String attach_id, HttpSession session) {
		UserBankAccounts uba=(UserBankAccounts) userBankAccountDao.getById(id);
		Admins admin = (Admins) adminDao.getById(HelperAuthority.getId(session));
		boolean isright = HelperPassword.verifyPassword(dopassword, admin.getOperationPassword(), admin.getSalty());
		if(!isright){
			throw new RuntimeException("操作密码错误！");
		}
		if(uba.getState().equals(state)){
			throw new RuntimeException("状态为改变！");
		}
		uba.setState(state);
		userBankAccountDao.saveOrUpdate(uba);
		CDDChecks cdd = new CDDChecks();
		Date date = new Date();
		cdd.setAdmin(admin);
		cdd.setComment(comment);
		cdd.setUserBankAccounts(uba);
		cdd.setSnapshot("");
		cdd.setTag(tag);
		cdd.setUrl("");
		cdd.setResult(state);
		cdd.setTimestamp(date);
		if (!remindertime.equals("0")) {
			date = new Date((date.getTime() + Long.parseLong(remindertime)));
			cdd.setReminderTimestamp(date);
		}
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
	@Override
	public Page<PojoCDDcheckBankAccount> getBankCheckList(int pageNo, int pageSize,String urlFormat, String scheme, String keyword,HttpSession session){

		StringBuilder selectBuilder=new StringBuilder();
		StringBuilder columnsBuilder=new StringBuilder();
		StringBuilder countstrBuilder=new StringBuilder();
		StringBuilder whereBuilder=new StringBuilder();
		selectBuilder.append(" select"); 
		columnsBuilder.append(" atta.path as path,");
		columnsBuilder.append(" cdd.id as cddcheckId,");
		columnsBuilder.append(" cdd.result as result,");
		columnsBuilder.append(" uba.id as userBankAccountId,");
		columnsBuilder.append(" uba.account_name as accountName,");
		columnsBuilder.append(" uba.account_no as accountNo,");
		columnsBuilder.append(" uba.country_code countryCode,");
		columnsBuilder.append(" uba.bank_name as bankName,");
		columnsBuilder.append(" uba.user_id as userId,");
		columnsBuilder.append(" uba.state as state,");
		columnsBuilder.append(" uba.isdefault as isDefault,");
		columnsBuilder.append(" cdd.timestamp as checkTime,");
		columnsBuilder.append(" ad.show_name as adminName,");
		columnsBuilder.append(" up.name as name");
		countstrBuilder.append(" count(*)");
		whereBuilder.append(" from cdd_checks cdd ");
		whereBuilder.append(" left join admins ad on ad.id=cdd.admin_id");
		whereBuilder.append(" left join user__bank_accounts uba on uba.id=cdd.user_bank_account_id");
		whereBuilder.append(" left join users u on u.id=uba.user_id");
		whereBuilder.append(" left join (select upf.*  from user__profiles upf,(select max(upf1.id) as id,upf1.user_id from user__profiles upf1 group by upf1.user_id) upf2 where upf.id=upf2.id) up on  up.user_id=uba.user_id");
		whereBuilder.append(" left join (select att.*  from attachments att,(select max(att1.id) as id,att1.owner_id from attachments att1 where att1.owner_type='cddcheck' group by att1.owner_id) att2 where att.id=att2.id) atta on  atta.owner_id=cdd.id ");
		whereBuilder.append(" where");
		whereBuilder.append(" uba.state!='DELETED'");
		if(!scheme.equals("")&&!scheme.equals("ALL")){
		    whereBuilder.append(" and cdd.result='"+scheme+"' ");
		}
		if(!keyword.equals("")){
			whereBuilder.append(" and (");
			whereBuilder.append(" up.name like '%"+keyword+"%'");
			whereBuilder.append(" or uba.account_name like '%"+keyword+"%'");
			whereBuilder.append(" or uba.account_no like '%"+keyword+"%'");
			whereBuilder.append(" )");
		}
		 whereBuilder.append(" order by cdd.id desc");
		String select=selectBuilder.toString();
		String columns=columnsBuilder.toString();
		String countstr=countstrBuilder.toString();
		String where=whereBuilder.toString();
		System.out.println(select+columns+where);
		String count =userBankAccountDao.getSession().createSQLQuery(select+countstr+where).uniqueResult().toString();
		List<PojoCDDcheckBankAccount> list=userBankAccountDao.getSession().createSQLQuery(select+columns+where)
				.addScalar("path", StringType.INSTANCE)
				.addScalar("cddcheckId", IntegerType.INSTANCE)
				.addScalar("userBankAccountId", IntegerType.INSTANCE)
				.addScalar("result", StringType.INSTANCE)
				.addScalar("accountName", StringType.INSTANCE)
				.addScalar("accountNo", StringType.INSTANCE)
				.addScalar("countryCode", StringType.INSTANCE)
				.addScalar("bankName", StringType.INSTANCE)
				.addScalar("userId", IntegerType.INSTANCE)
				.addScalar("state", StringType.INSTANCE)
				.addScalar("isDefault", BooleanType.INSTANCE)
				.addScalar("adminName", StringType.INSTANCE)
				.addScalar("name", StringType.INSTANCE)
				.addScalar("checkTime",  TimestampType.INSTANCE)
			    .setMaxResults(pageSize)
			    .setFirstResult((pageNo - 1) * pageSize)
			    .setResultTransformer(Transformers.aliasToBean(PojoCDDcheckBankAccount.class))
				.list();
		Page<PojoCDDcheckBankAccount> page=new Page<PojoCDDcheckBankAccount>(Integer.parseInt(count), pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		return page;

	}

}
