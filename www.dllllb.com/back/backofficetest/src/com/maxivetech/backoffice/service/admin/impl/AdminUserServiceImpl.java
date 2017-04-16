package com.maxivetech.backoffice.service.admin.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
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
import com.maxivetech.backoffice.dao.AttachmentDao;
import com.maxivetech.backoffice.dao.SettingDao;
import com.maxivetech.backoffice.dao.TokenDao;
import com.maxivetech.backoffice.dao.UserBalanceDao;
import com.maxivetech.backoffice.dao.UserBalanceLogDao;
import com.maxivetech.backoffice.dao.UserBankAccountDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.entity.Attachments;
import com.maxivetech.backoffice.entity.Tokens;
import com.maxivetech.backoffice.entity.UserBalanceLogs;
import com.maxivetech.backoffice.entity.UserBalances;
import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.pojo.PojoSession;
import com.maxivetech.backoffice.sendmail.PostmanAgent;
import com.maxivetech.backoffice.sendmail.PostmanUser;
import com.maxivetech.backoffice.service.admin.AdminUserService;
import com.maxivetech.backoffice.service.user.impl.SessionServiceImpl;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperHttp;
import com.maxivetech.backoffice.util.HelperPassword;
import com.maxivetech.backoffice.util.HelperSalty;
import com.maxivetech.backoffice.util.Page;


@Service
@Transactional
public class AdminUserServiceImpl implements AdminUserService {
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserBankAccountDao userBankAccountDao;
	@Autowired
	private UserBalanceLogDao userBalanceLogDao;
	@Autowired
	private UserBalanceDao userBalanceDao;
	@Autowired
	private UserProfilesDao userProfilesDao;
	@Autowired
	private AttachmentDao attachmentDao;
	@Autowired
	private SettingDao settingDao;
	@Autowired
	private TokenDao tokenDao;

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer, 
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.CustomerServiceStaff, Role.RiskManagementCommissioner})
	@Override
	public Page<Users> getPage(int pageNo, int pageSize, String urlFormat,
			int upId, String state, String keyword, HttpSession session) {
		
		
		//WHERE
		List<Criterion> wheres = new ArrayList<Criterion>();
		//WHERE上级
		if (upId == -1) {	//不限上级
			
		} else if (upId > 0) {	//指定上级
			wheres.add(Restrictions.eq("parent", userDao.getById(upId)));
		} else {	//列出总公司
			wheres.add(Restrictions.isNull("parent"));
		}
		//WHERE状态
		if (state != null && state.trim().length() > 0) {
			wheres.add(Restrictions.eq("state", state.trim()));
		}
		
		//ORDER BY
		List<Order> orders = new ArrayList<Order>();
		
		//查询
		Page<Users> page = (Page<Users>) userDao.getPage(wheres, orders, null, pageSize, pageNo, null);
		page.generateButtons(urlFormat);
		
		//清洗
		for (Users user : page.getList()) {
			user.setPassword("");
			user.setPaymentPassword("");
			user.setSalty("");
			
			
			user.setParent(null);
		}
		
		return page;
	}
	

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer, 
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.CustomerServiceStaff, Role.RiskManagementCommissioner})
	@Override
	public Page<HashMap<String, Object>> getPage(int pageNo, int pageSize, String urlFormat,
			 String state, String keyword, HttpSession session) {

		StringBuffer wheres = new StringBuffer();
		
		//关键字匹配
		if(!keyword.equals("")){
			wheres.append(" and  (instr(u.email,'"+keyword+"')>0 "
					+" or instr(u.mobile,'"+keyword+"')>0"
					+" or instr(u.referral_code,'"+keyword+"')>0 "
					+" or instr(up.name,'"+keyword+"')>0 "
					+" or instr(m.login,'"+keyword+"')>0 ) ");
		}
		switch (state) {
		case "unverified"://未认证
			wheres.append("and u.state = 'UNVERIFIED' and u.disable = 0");
			break;
		case "auditing"://待审核
			wheres.append("and u.state = 'AUDITING' and u.disable = 0");
			break;
		case "verified"://审核通过
			wheres.append("and u.state = 'VERIFIED' and u.disable = 0");
			break;
		case "rejected"://已驳回
			wheres.append("and u.state = 'REJECTED' and u.disable = 0");
			break;
		case "frozen"://已冻结
			wheres.append("and u.is_frozen = 1");
			break;
		case "disabled"://已禁用
			wheres.append("and u.disable = 1");
			break;

		default:
			break;
		}
		
		String hql = "select"
				   + " u.id as uId,"
				   + " u.state as state,"
				   + " u.email as email,"
				   + " u.mobile as mobile,"
				   + " u.disable as disable,"
				   + " u.is_frozen as is_frozen,"
				   + " u.vip_grade as vip_grade,"
				   + " u.level as level,"
				   + " up.name as _userName,"
				   + " u.referral_code as referralCode,"
				   + " u.registration_time as registrationTime"
				   + " from users u"
				   + " left join user__profiles up on u.id=up.user_id "
				   + " left join mt4_users m on m.user_id=u.id "
				   + " where u.id is not null "
				   + wheres
				   + " GROUP BY u.id"
				   + " ORDER BY u.registration_time DESC";
	
			List<HashMap<String, Object>> list = userDao.getSession()
					.createSQLQuery(hql)
					.addScalar("uId" ,IntegerType.INSTANCE)
					.addScalar("state",StringType.INSTANCE)
					.addScalar("disable",BooleanType.INSTANCE)
					.addScalar("is_frozen",BooleanType.INSTANCE)
					.addScalar("vip_grade", IntegerType.INSTANCE)
					.addScalar("level", IntegerType.INSTANCE)
					.addScalar("_userName", StringType.INSTANCE)
					.addScalar("email",StringType.INSTANCE)
					.addScalar("mobile",StringType.INSTANCE)
					.addScalar("referralCode",StringType.INSTANCE)
					.addScalar("registrationTime",TimestampType.INSTANCE)
					.setMaxResults(pageSize)
					.setFirstResult((pageNo-1)*pageSize)
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
		
		String countSQL = "select count(distinct u.id) from users u"
				   + " left join user__profiles up on u.id = up.user_id "
				   + " left join mt4_users m on m.user_id = u.id "
				   + " where u.id is not null "
				   + wheres;
		//返回的类型是BigInteger
		BigInteger bigVal = (BigInteger) userDao.getSession()
				.createSQLQuery(countSQL).uniqueResult();
		
		int totalRows=0;
		//BigInteger 转 int
		if (bigVal != null){
			totalRows = bigVal.intValue();	
		}
		Page<HashMap<String, Object>> page = new Page<HashMap<String, Object>>(
				totalRows, pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		
		//
		for (HashMap<String, Object> map : page.getList()) {
			map.put("_vipGradeName", BackOffice.getInst().companyHook.getVipGradeShowName((int)map.get("vip_grade"), (int)map.get("level")));
		}
		
		return page;
	}
	
	
	
	
	
	
	

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer, 
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.CustomerServiceStaff, Role.RiskManagementCommissioner})
	@Override
	public Page<HashMap<String, Object>> getApplyAgent(int pageNo, int pageSize, String urlFormat, String keyword, HttpSession session) {

		StringBuffer wheres = new StringBuffer();
		
		//关键字匹配
		if(!keyword.equals("")){
			wheres.append(" and  (instr(u.email,'"+keyword+"')>0 "
					+" or instr(u.mobile,'"+keyword+"')>0"
					+" or instr(u.referral_code,'"+keyword+"')>0 "
					+" or instr(up.name,'"+keyword+"')>0 "
					+" or instr(m.login,'"+keyword+"')>0 ) ");
		}
		wheres.append(" and u.apply_agent=1 and u.disable = 0 ");
		String hql = "select"
				   + " u.id as uId,"
				   + " u.state as state,"
				   + " u.email as email,"
				   + " u.mobile as mobile,"
				   + " u.disable as disable,"
				   + " u.is_frozen as is_frozen,"
				   + " u.vip_grade as vip_grade,"
				   + " u.level as level,"
				   + " up.name as _userName,"
				   + " u.referral_code as referralCode,"
				   + " att.path as path, "
				   + " att.id as attach_id, "
				   + " u.registration_time as registrationTime"
				   + " from users u"
				   + " left join (select max(id) as id,path,owner_type,owner_id  from attachments group by owner_type,owner_id order by id desc ) as  att on att.owner_id=u.id  and att.owner_type='agentAgreement'"
				   + " left join user__profiles up on u.id=up.user_id "
				   + " left join mt4_users m on m.user_id=u.id "
				   + " where u.id is not null "
				   + wheres
				   + " GROUP BY u.id"
				   + " ORDER BY u.id DESC";
	        System.out.println(hql);
			List<HashMap<String, Object>> list = userDao.getSession()
					.createSQLQuery(hql)
					.addScalar("uId" ,IntegerType.INSTANCE)
					.addScalar("state",StringType.INSTANCE)
					.addScalar("disable",BooleanType.INSTANCE)
					.addScalar("is_frozen",BooleanType.INSTANCE)
					.addScalar("vip_grade", IntegerType.INSTANCE)
					.addScalar("level", IntegerType.INSTANCE)
					.addScalar("_userName", StringType.INSTANCE)
					.addScalar("email",StringType.INSTANCE)
					.addScalar("path",StringType.INSTANCE)
					.addScalar("attach_id", IntegerType.INSTANCE)
					.addScalar("mobile",StringType.INSTANCE)
					.addScalar("referralCode",StringType.INSTANCE)
					.addScalar("registrationTime",TimestampType.INSTANCE)
					.setMaxResults(pageSize)
					.setFirstResult((pageNo-1)*pageSize)
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
		
		String countSQL = "select count(distinct u.id) from users u"
				   + " left join user__profiles up on u.id = up.user_id "
				   + " left join mt4_users m on m.user_id = u.id "
				   + " where u.id is not null "
				   + wheres;
		//返回的类型是BigInteger
		BigInteger bigVal = (BigInteger) userDao.getSession()
				.createSQLQuery(countSQL).uniqueResult();
		
		int totalRows=0;
		//BigInteger 转 int
		if (bigVal != null){
			totalRows = bigVal.intValue();	
		}
		Page<HashMap<String, Object>> page = new Page<HashMap<String, Object>>(
				totalRows, pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		
		//
		for (HashMap<String, Object> map : page.getList()) {
			map.put("_vipGradeName", BackOffice.getInst().companyHook.getVipGradeShowName((int)map.get("vip_grade"), (int)map.get("level")));
		}
		
		return page;
	}

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer, 
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.CustomerServiceStaff, Role.RiskManagementCommissioner})
	@Override
	public boolean sendAagreementEmail(int id,HttpSession session){
	   Attachments attach=(Attachments) attachmentDao.getById(id);
	   if(attach==null){
		   throw new RuntimeException("协议不存在请上传后再发送邮件！");
	   }
	   Users user=attach.getUser();
	   PostmanAgent pa=new PostmanAgent(user.getEmail());
	   String downloadUrl=BackOffice.getInst().URL_ROOT+"upload"+attach.getPath();
	   pa.sendAgentAgreement(user.getId(), downloadUrl);	
		return true;
	}
	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer, 
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.CustomerServiceStaff, Role.RiskManagementCommissioner})
	@Override
	public List<PojoCounts> getCounts(HttpSession session) {
		//TODO:弄個多少分鐘緩存，省得各種查數據庫
		List<PojoCounts> list = new ArrayList<PojoCounts>();
		
		//all
		{
		    String hql = "select count(*) from Users";
		    long count = (long)userDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("all", count));
		}
		//unverified
		{
		    String hql = "select count(*) from Users where state='UNVERIFIED' and disable = 0";
		    long count = (long)userDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("unverified", count));
		}
		//auditing
		{
		    String hql = "select count(*) from Users where state='AUDITING' and disable = 0";
		    long count = (long)userDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("auditing", count));
		}
		//rejected
		{
		    String hql = "select count(*) from Users where state='REJECTED' and disable = 0";
		    long count = (long)userDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("rejected", count));
		}
		//verified
		{
			String hql = "select count(*) from Users where state='VERIFIED' and disable = 0";
			long count = (long)userDao.getSession().createQuery(hql).uniqueResult();  
			list.add(new PojoCounts("verified", count));
		}
		//frozen
		{
		    String hql = "select count(*) from Users where isFrozen = 1 ";
		    long count = (long)userDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("frozen", count));
		}
		//disabled
		{
		    String hql = "select count(*) from Users where disable = 1 ";
		    long count = (long)userDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("disabled", count));
		}

		return list;
	}
	
	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer, 
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.CustomerServiceStaff, Role.RiskManagementCommissioner})
	@Override
	public Page<UserBankAccounts> getUserBankAccountList(int userId,int pageNo, int pageSize,
			String urlFormat,HttpSession session) {
		
		Users user = (Users) userDao.getById(userId);
		if (user == null) {
			return null;
		}

		ArrayList<Criterion> wheres = new ArrayList<Criterion>();
		wheres.add(Restrictions.eq("user", user));

		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("state"));

		Page<UserBankAccounts> page = (Page<UserBankAccounts>) userBankAccountDao
				.getPage(wheres, orders, null, pageSize, pageNo, null);
		page.generateButtons(urlFormat);

		// 清理
		for (UserBankAccounts uba : page.getList()) {
			uba.setUser(null);
		}

		return page;
	}
	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer, 
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.CustomerServiceStaff, Role.RiskManagementCommissioner})
	@Override
	public List<Attachments> getBankImageList(int id,HttpSession session){
		Users users = (Users) userDao.getById(id);
		UserBankAccounts BankAccounts = userBankAccountDao
				.getBank(users);
        List<Attachments> alist=null;
        if(BankAccounts!=null){
              alist=attachmentDao.getFileImage(UserBankAccounts.getOwnertype(), BankAccounts.getId());
        }
		return alist;
	}
	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer, 
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.CustomerServiceStaff, Role.RiskManagementCommissioner})
	@Override
	public Page<UserBalanceLogs> getUserBalanceLogs(int userId,int pageNo, int pageSize,
			String urlFormat,HttpSession session) {

		Users user = (Users) userDao.getById(userId);
		if (user == null) {
			return null;
		}
		
		ArrayList<Criterion> wheres = new ArrayList<Criterion>();
		wheres.add(Restrictions.eq("user", user));
		
		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("creatTime"));
		
		Page<UserBalanceLogs> page = (Page<UserBalanceLogs>) userBalanceLogDao
				.getPage(wheres, orders, null, pageSize, pageNo, null);
		page.generateButtons(urlFormat);
		
		// 清理
		for (UserBalanceLogs ubl : page.getList()) {
			ubl.setUser(null);
		}
		
		return page;
	}
	
	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer, 
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.CustomerServiceStaff, Role.RiskManagementCommissioner})
	@Override
	public HashMap<String,Object> getUserProfile(int user_id, HttpSession session){
	    HashMap<String, Object>	result=new HashMap<String,Object>();
		
	    Users users=(Users) userDao.getById(user_id);
	    UserProfiles userProfiles=userProfilesDao.findUserProfilesByUser(users);
	    if(users!=null){
	    	users.setParent(null);
	    	result.put("user", users);
	    }
	    if (userProfiles!=null) {
	    	 userProfiles.setUser(null);
	    	 result.put("userProfile", userProfiles);
		}
	   
	    return result;
	}
	
	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer, 
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.CustomerServiceStaff, Role.RiskManagementCommissioner})
	@Override
	public List<Attachments> getUserImage(int id,
			HttpSession session) {
		
		Users user=(Users) userDao.getById(id);
		UserProfiles userProfiles=userProfilesDao.findUserProfilesByUser(user);
		if(userProfiles!=null){
			List<Attachments> attach = attachmentDao.getFileImage(
					UserProfiles.getOwnertype(),userProfiles.getId());
			return attach;
		}
		return null;
	}
	
	@CheckRole(role = {Role.OperationsManager, Role.CustomerServiceStaff})
	@Override
	public boolean updateProfile(int userId, String name, String ename, String cardType,
			String cardID, String countryCode, String address, String company,
			String userIndustry, String userYearsIncom, String position,
			String bankName, String bankNo, String cardholder_Name,
			String countryAdress,String swiftCode,String ibanCode,String bankBranch,String bankAddress,String attachment_id, HttpSession session) {

		Users user = (Users) userDao.getById(userId);
		if (user == null) {
			throw new RuntimeException("用户不存在");
		}
		if (user.isDisable()||user.isFrozen()){
			throw new RuntimeException("账户已被冻结或禁用");
		}
		
		// 修改用户基本信息表
		UserProfiles up = userProfilesDao.findUserProfilesByUser(user);
		
		if (up == null/* || user.getState().equals("VERIFIED")*/){
			up = new UserProfiles();
		}
		
		if (up != null) {
			List<Attachments> ac = attachmentDao.getFileImage(UserProfiles.getOwnertype(), up.getId());
			// 修改用户信息
			/*
			if ((ac == null || ac.size() < 3)) {
				up.getUser().setState("UNVERIFIED");
			}*/
			
			if (!up.getUser().getState().equals("VERIFIED") && 
					ac != null && 
					ac.size() >= settingDao.getInt("UserProfileMinImageNum")) {
				up.getUser().setState("AUDITING");
			}
			up.setCardType(cardType);
			up.setCompany(company);
			up.setCreatTime(new Date());
			up.setPosition(position);
			up.setUpdatedTime(new Date());
			up.setUser(user);
			up.setUserComment("");
			up.setUserEName(ename);
			up.setUserEsidentialAddress(address);
			up.setUserIdCard(cardID);
			up.setUserIndustry(userIndustry);
			up.setUserName(name);
			up.setUserNationality(countryCode);
			up.setUserYearsIncom(userYearsIncom);
			
			userProfilesDao.saveOrUpdate(up);
			
			// 绑定银行卡信息
			UserBankAccounts uba = userBankAccountDao.getBank(user);
			if (uba == null){
				UserBankAccounts ub=new UserBankAccounts();
				ub.setAccountName(name);
				ub.setAccountNo(bankNo);
				ub.setBankName(bankName);
				ub.setCurrencyType("USD");
				ub.setCountryCode(countryCode);
				ub.setSwiftCode("");
				ub.setDefault(true);
				ub.setIntermediaryBankAddress("");
				ub.setIntermediaryBankBicSwiftCode("");
				ub.setIntermediaryBankBranch("");
				ub.setIntermediaryBankName("");
				ub.setSwiftCode("");
				ub.setUpdateTime(new Date());
				ub.setSortNum(1);
				ub.setUser(user);
				ub.setIbanCode(ibanCode);
				ub.setSwiftCode(swiftCode);
				ub.setBankBranch(bankBranch);
				ub.setBankAddress(bankAddress);
				ub.setUpdateTime(new Date());
				userBankAccountDao.save(ub);
				if(!attachment_id.equals("")){
				     String[] str=attachment_id.split(",");
					 int c=0;
				     for (int i = 0; i < str.length; i++) {
					     try {
							c=Integer.parseInt(str[i]);
						} catch (Exception e) {
							break;
						}
					    Attachments a=(Attachments) attachmentDao.getById(c);
					    if(a!=null){
					    	a.setOwnerId(ub.getId());
					    	a.setOwnerType(UserBankAccounts.getOwnertype());
					    	a.setUser(ub.getUser());
					    	attachmentDao.saveOrUpdate(a);
					    }
				     }
				 }
			} else {
				uba.setAccountNo(bankNo);
				uba.setBankName(bankName);
				uba.setBankAddress(countryAdress);
				uba.setCountryCode(countryCode);
				uba.setIbanCode(ibanCode);
				uba.setSwiftCode(swiftCode);
				uba.setBankBranch(bankBranch);
				uba.setBankAddress(bankAddress);
				
				userBankAccountDao.saveOrUpdate(uba);
			}
			
			userBankAccountDao.commit();
			return true;
		}
		return false;
	}

	@CheckRole(role = {Role.OperationsManager, Role.CustomerServiceStaff})
	@Override
	public Users getOne(int userId, HttpSession session) {
		
		Users user = (Users) userDao.getById(userId);
		//清理
		{
			user.setPassword("");
			user.setPaymentPassword("");
			user.setSalty("");
			user.set_name(userProfilesDao.getUserName(user));
			user.set_vipGradeName(BackOffice.getInst().companyHook.getVipGradeShowName(user.getVipGrade(), user.getLevel()));
			
			if (user.getParent() != null) {
				user.set_up_id(user.getParent().getId());
				user.setParent(null);
			}
		}
		
		return user;
	}

	@CheckRole(role = {Role.OperationsManager, Role.CustomerServiceStaff})
	@Override
	public UserBankAccounts getBank(int userId, HttpSession session) {

		Users user = (Users) userDao.getById(userId);
		UserBankAccounts bankAccount = userBankAccountDao.getBank(user);
		
		return bankAccount;
	}

	@CheckRole(role = {Role.OperationsManager, Role.CustomerServiceStaff})
	@Override
	public boolean removeImg(int id, HttpSession session) {

		attachmentDao.deleteById(id);
		attachmentDao.commit();
		
		return true;
	}

	@CheckRole(role = {Role.OperationsManager, Role.CustomerServiceStaff})
	@Override
	public List<Attachments> getproFileImage(int userId, HttpSession session) {

		Users user = (Users) userDao.getById(userId);
		UserProfiles profile = userProfilesDao.findUserProfilesByUser(user);
		if (profile != null) {
			List<Attachments> list = attachmentDao.getFileImage(UserProfiles.getOwnertype(), profile.getId());
			return list;
		}
		
		return null;
	}

	@CheckRole(role = {Role.OperationsManager, Role.CustomerServiceStaff})
	@Override
	public UserProfiles getProfile(int userId, HttpSession session) {

		Users user = (Users) userDao.getById(userId);
		UserProfiles profile = userProfilesDao.findUserProfilesByUser(user);
		return profile;
	}
	
	@CheckRole(role = {Role.OperationsManager})
	@Override
	public void modifyAccount(int userId, String email, String mobile, int upId,
			HttpSession session) {
		//检查权限
		if (!HelperAuthority.isOperationsManager(session)){
			throw new ForbiddenException();
		}
		
		Users user = (Users)userDao.getById(userId);
		if (user == null) {
			throw new RuntimeException("没有找到指定用户！");
		}
		//检查Email格式
		if (!Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", email)) {
			throw new RuntimeException("非法的Email地址！");
		}
		//检查Email存在
		Users userByEmail = userDao.findByEmail(email);
		if (userByEmail != null && userByEmail.getId() != user.getId()) {
			throw new RuntimeException("Email已经被占用！");
		}
		//检查手机号格式
		if (mobile == null || !Pattern.matches("^\\+[0-9]+\\.[0-9]{8,11}$", mobile)) {
			throw new RuntimeException("非法的手机号(须+[地区代码].[8-11位数字])！");
		}
		//检查手机号存在
		Users userByMobile = userDao.findByMobile(mobile);
		if (userByMobile != null && userByMobile.getId() != user.getId()) {
			throw new RuntimeException("手机号已经被占用！");
		}
		//检查推荐人
		Users userUp = (Users) userDao.getById(upId);
		if (userUp != null && userUp.getPath().indexOf(user.getPath()) >= 0) {
			throw new RuntimeException("推荐人不能是自己的客户！");
		}
		//
		
		if (userId == 1) {	//1号公司客户不改path,parent,level
			user.setEmail(email);
			user.setMobile(mobile);
			userDao.update(user);
		} else {
			String oldPath = user.getPath();
			String newPath = userUp.getPath() + user.getId() + ",";
			user.setPath(newPath);
			user.setParent(userUp);
			if (user.getLevel() > 3) {	//以前是客户，不管挂到哪还是客户
				user.setLevel(Math.max(userUp.getLevel() + 1, 4));
			} else {	//以前是公司/经理/员工
				if (userUp.getLevel() + 1 > 3) {	//更新后将不是公司/经理/员工了
					throw new RuntimeException("不能把经理/员工移为客户，如需降为客户需先在“经理/员工”管理中将其降为（直属）客户。");
				} else {	//更新后仍然是公司/经理/员工
					user.setLevel(userUp.getLevel() + 1);
				}
			}
			user.setEmail(email);
			user.setMobile(mobile);
			userDao.update(user);
			
			userDao.getSession().createSQLQuery("UPDATE users SET path=REPLACE(path, '"+oldPath+"', '"+newPath+"') WHERE INSTR(path, '"+oldPath+"') = 1;").executeUpdate();
		}
		
		userDao.commit();
	}

	@CheckRole(role = {Role.OperationsManager, Role.CustomerServiceStaff})
	@Override
	public List<HashMap<String, Object>> getList(HttpSession session) {

		List<HashMap<String, Object>> list = userDao.createCriteria()
			.setProjection(Projections.projectionList()
					.add(Projections.property("id"), "id")
					.add(Projections.property("email"), "email")
					.add(Projections.property("mobile"), "mobile")
					.add(Projections.property("level"), "level")
					.add(Projections.property("vipGrade"), "vipGrade")
					.add(Projections.property("path"), "path")
				)
			.addOrder(Order.asc("path"))
			.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
			.list();
		
		for (HashMap<String, Object> map : list) {
			UserProfiles up = userProfilesDao.findUserProfilesByUser((Users)userDao.getById((int)map.get("id")));
			map.put("name", "["+BackOffice.getInst().companyHook.getVipGradeShowName((int)map.get("vipGrade"), (int)map.get("level")) + "]" + (up != null ? up.getUserName() : ""));
		}
		
		return list;
	}


	
	@CheckRole(role = {Role.OperationsManager, 
			Role.CustomerServiceStaff})
	@Override
	public boolean resetUserPassword(int userId, HttpSession session) {
		
		//检查用户
		Users user = (Users) userDao.getById(userId);
		if (user == null) {
			throw new RuntimeException("用户不存在！");
		}
		//检查用户禁用状态
		if (user.isDisable()) {
			throw new RuntimeException("操作失败！该用户已经被禁用！");
		}
		
		if(user.getEmail() == null || user.getEmail().equals("")){
			throw new RuntimeException("由于重置密码需要发送邮件，但是客户并未绑定邮箱，所以无法重置密码！");
		}
		
		{
			String password = HelperSalty.getCharAndNumr(6);
			String paymentPassword = HelperSalty.getCharAndNumr(6);
			//更新数据库
			user.setPassword(HelperPassword.beforeSave(password, user.getSalty()));
			user.setPaymentPassword(HelperPassword.beforeSave(paymentPassword, user.getSalty()));
			userDao.update(user);
			userDao.commit();
			
			//发邮件
			{
				PostmanUser postman = new PostmanUser(user.getEmail());
				postman.resetUserPassword(user.getEmail(), password, paymentPassword);
			}
		}
		
		return true;
	}
	
	
	@CheckRole(role = {Role.OperationsManager, 
			Role.FinancialStaff, 
			Role.FinancialSuperior})
	@Override
	public HashMap<String, Object> findByMobile(String mobile,
			HttpSession session) {
		
		Users user = userDao.findByMobile(mobile);
		if (user == null) {
			throw new RuntimeException("没有找到此账号！");
		}
		
		
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("_name", userProfilesDao.getUserName(user));
		map.put("email", user.getEmail());
		
		UserBalances ub = userBalanceDao.getBalance(user, "USD");
		map.put("_amountAvailable", ub == null ? 0 : ub.getAmountAvailable());
		
		return map;
	}

	@CheckRole(role = {Role.OperationsManager})
	@Override
	public PojoSession holdUser(int userId, HttpSession session) {
		Users user = (Users)userDao.getById(userId);
		if (user == null) {throw new RuntimeException("找不到用户！");}
		if (user.isDisable()) {throw new RuntimeException("该用户已经被禁用！");}
		
		// 用户登录成功
		PojoSession pojo = new PojoSession(user);
		pojo.setHoldAdminId(HelperAuthority.getId(session));
		session.setAttribute(SessionServiceImpl.SESSION_KEY, pojo);
		session.setAttribute(SessionServiceImpl.SESSION_USER_KEY, user);
		session.setAttribute(SessionServiceImpl.SESSION_ADMIN_KEY, null);

					tokenDao.deleteToken(tokenDao.findByUser(user));
					
					String ipAddress = (String) session.getAttribute("RemoteAddr");
					Tokens token = new Tokens();
					token.setAdmin(null);
					token.setCreatTime(new Date());
					token.setExpirationTime(null);
					token.setIpAddress(ipAddress == null ? "" : ipAddress);
					token.setLastAuthorizationTime(null);
					token.setUser(user);
					token.setUuid(UUID.randomUUID().toString());
					tokenDao.save(token);
					tokenDao.commit();

					pojo.setToken(token.getUuid());
					
					return pojo;
	}
}
