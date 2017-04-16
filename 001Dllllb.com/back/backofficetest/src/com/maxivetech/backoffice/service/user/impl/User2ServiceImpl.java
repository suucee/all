package com.maxivetech.backoffice.service.user.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.AttachmentDao;
import com.maxivetech.backoffice.dao.DepositDao;
import com.maxivetech.backoffice.dao.SettingDao;
import com.maxivetech.backoffice.dao.UserBalanceDao;
import com.maxivetech.backoffice.dao.UserBalanceLogDao;
import com.maxivetech.backoffice.dao.UserBankAccountDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.dao.WithdrawalDao;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Attachments;
import com.maxivetech.backoffice.entity.Deposits;
import com.maxivetech.backoffice.entity.UserBalanceLogs;
import com.maxivetech.backoffice.entity.UserBalances;
import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.entity.Withdrawals;
import com.maxivetech.backoffice.pojo.PojoCapital;
import com.maxivetech.backoffice.pojo.PojoMonthRebate;
import com.maxivetech.backoffice.service.user.User2Service;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class User2ServiceImpl implements User2Service {
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserBalanceDao userBalanceDao;
	@Autowired
	private UserProfilesDao userProfilesDao;
	@Autowired
	private UserBankAccountDao userBankAccountDao;
	@Autowired
	private WithdrawalDao withdrawalDao;
	@Autowired
	private SettingDao settingDao;
    @Autowired
    private AttachmentDao attachmentDao;
    @Autowired
    private DepositDao depositDao;
    @Autowired
    private UserBalanceLogDao userBalanceLogDao;
    
    
	@Override
	public HashMap<String, Object> getUserLogin(HttpSession session) {
		HashMap<String, Object> result=new HashMap<String, Object>();
		String name="";
		int id=0;
		if(HelperAuthority.isUser(session)){
			Users user=SessionServiceImpl.getCurrentUser(session);
			name=userProfilesDao.getUserName(user);
			String level=BackOffice.getInst().companyHook.getVipGradeShowName(user.getVipGrade(), user.getLevel());
			name+="("+level+")";
			id=user.getId();
		}else{
			Admins admin=SessionServiceImpl.getCurrentAdmin(session);
			id=admin.getId();
			name=admin.getShowName();
			if(HelperAuthority.isComplianceOfficer(session)){
				name+="(合规)";
			}else if(HelperAuthority.isCustomerServiceStaff(session)){
				name+="(客服)";
			}else if(HelperAuthority.isFinancialStaff(session)){
				name+="(财务)";
			}else if(HelperAuthority.isFinancialSuperior(session)){
				name+="(财务主管)";
			}else if(HelperAuthority.isOperationsManager(session)){
				name+="(运维经理)";
			}else if(HelperAuthority.isWebmaster(session)){
				name+="(网站管理员)";
			}
		}
		result.put("name", name);
		result.put("id", id);
		return result;
	}
	
	@CheckRole(role = {Role.User})
	@Override
	public String updateProfile(String name, String ename, String cardType,
			String cardID, String countryCode, String address, String company,
			String userIndustry, String userYearsIncom, String position,
		    HttpSession session) {

		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		if (user.isDisable() || user.isFrozen()){
			return "账户已被冻结或禁用";
		}
		// 修改用户基本信息表
		UserProfiles up=userProfilesDao.findUserProfilesByUser(user);
		
		if (up == null){
			up = new UserProfiles();
			up.setUser(user);
			up.setCreatTime(new Date());
		}
		List<Attachments> ac = null;		
		if (!user.getState().equals("VERIFIED")) {
			ac = attachmentDao.getFileImage(UserProfiles.getOwnertype(), up.getId());
			// 修改用户信息
			if ((ac == null || ac.size() < settingDao.getInt("UserProfileMinImageNum"))) {
				user.setState("UNVERIFIED");
			}
			if ((ac != null && ac.size() >= settingDao.getInt("UserProfileMinImageNum"))) {
				user.setState("AUDITING");
			}
			userDao.saveOrUpdate(user);
			up.setCardType(cardType);
			up.setCompany(company);
			up.setCreatTime(new Date());
			up.setPosition(position);
			up.setUpdatedTime(null);
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
			userBankAccountDao.commit();
			
			return "提交成功";
		}
		
		return "提交失败";
	}
    @Override 
	public HashMap<String, Object> getUserDetail(int user_id,HttpSession session){
		 HashMap<String, Object> result=new  HashMap<String, Object>();
		 Users users=(Users) userDao.getById(user_id);
		 
		 UserProfiles profiles = userProfilesDao.findUserProfilesByUser(users);
		 List<Attachments> attach =null;
		 if(profiles!=null){
			   attach = attachmentDao.getFileImage(profiles.getOwnertype(),profiles.getId());
		 }
		 List<Attachments> bankImg=new ArrayList<Attachments>();
		 List<UserBankAccounts> list = userBankAccountDao.getAllBank(users);
		 for (UserBankAccounts item : list) {
			 item.setUser(null);
			 List<Attachments> images=attachmentDao.getFileImage(UserBankAccounts.getOwnertype(),item.getId());
			 if(images!=null&&images.size()>0){
				 bankImg.addAll(images);
			 }
		 }
		 double withdrawalsSum=0.00;
		 if (users!= null) {
			 users.setPassword("");
			 users.setPaymentPassword("");
			 users.setSalty("");
			 if(users.getParent() != null){
				 users.set_up_id(users.getParent().getId());
				 users.getParent().set_name(userProfilesDao.getUserName(users.getParent()));
				 users.getParent().setPassword("");
				 users.getParent().setPaymentPassword("");
				 users.getParent().setSalty("");
				 users.getParent().setParent(null);;
			 }
			 Object o = userProfilesDao.findUserProfilesByUser(users);
			 String userProfilesComment = "";
			 if(o != null){
				 userProfilesComment =  ((UserProfiles)o).getUserComment();
			 }
			 String vipGradeShowName = BackOffice.getInst().companyHook.getVipGradeShowName(users.getVipGrade(),users.getLevel());
			 users.set_vipGradeName(vipGradeShowName);
			 
			Object object = withdrawalDao
					.createCriteria()
					.setProjection(Projections.projectionList().add(Projections.sum("amount")))
					.add(Restrictions.eq("user", users))
					.add(Restrictions.eq("currency", "USD"))
					.add(Restrictions.ne("state", "REMITTED"))
					.add(Restrictions.ne("state", "CANCELED")).uniqueResult();
			if (object != null) {
				withdrawalsSum=(double) object;
			}
		 }
		UserBalances ub=  userBalanceDao.getBalance(users, "USD");
		
		ArrayList<Criterion> wheres=new ArrayList<Criterion>();
		wheres.add(Restrictions.eq("user", users));
		wheres.add(Restrictions.eq("isDeleted", false));
		ArrayList<Order> orders=new ArrayList<Order>();
		orders.add(Order.asc("login"));
		
		List<PojoMonthRebate> mrl = null;
		if(HelperAuthority.isUser(session)&&HelperAuthority.getId(session)!=user_id){
			Users me=(Users) userDao.getById(HelperAuthority.getId(session));
			mrl=userDao.getUserMonthRebate(me,users);
		}else{
			mrl=userDao.getMonthRebate(users);
		}
		double rebateCount = 0;
		for (PojoMonthRebate p : mrl) {
			rebateCount += p.getRebateCount();
		}
		
		 result.put("users", users);
		 result.put("usersParentStuffInfoMap", getParentStuffInfoById(user_id));
		 result.put("profiles", profiles);
		 result.put("attach", attach);
		 result.put("userBankAccounts", list);
		 result.put("bankImg", bankImg);
		 result.put("withdrawalsSum", withdrawalsSum);
		 result.put("userBalances", ub);
		 result.put("mt4UsersList", null);
		result.put("monthRebate", mrl);
		result.put("rebateCount", rebateCount);
		 return result;
	}
    
    
    
	@Override
	public Page<Withdrawals> getPageWithdrawal(int user_id,int pageNo, int pageSize,String urlFormat, HttpSession session) {
		// 登录检查
		Users user = (Users) userDao.getById(user_id);
		if (user == null) {
			return null;
		}
		ArrayList<Criterion> wheres = new ArrayList<Criterion>();
		wheres.add(Restrictions.eq("user", user));
		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("creatTime"));
		Projection projs = null;
		Page<Withdrawals> page = (Page<Withdrawals>) withdrawalDao.getPage(
				wheres, orders, projs, pageSize, pageNo, null);
		page.generateButtons(urlFormat);
		// 清除
		for (Withdrawals item : page.getList()) {
			item.setUser(null);
		}
		return page;
	}
	@Override
	public Page<Deposits> getPageDeposits(int user_id,int pageNo, int pageSize, String urlFormat,
			HttpSession session) {
		Users user= (Users) userDao.getById(user_id);
		ArrayList<Criterion> wheres=new ArrayList<>();
		wheres.add(Restrictions.eq("user", user));
		ArrayList<Order> order=new ArrayList<>();
		order.add(Order.desc("creatTime"));
		Page<Deposits> deposits=(Page<Deposits>) depositDao.getPage(wheres, order, null, pageSize, pageNo, null);
		deposits.generateButtons(urlFormat);
		for(Deposits d:deposits.getList()){
			d.setUser(null);
		}
		return deposits;
	}

	@Override
	public HashMap<String, Object> getUserEmailAndName(HttpSession session) {
		int userId = SessionServiceImpl.getCurrentUser(session).getId();
		Users user = (Users) userDao.getById(userId);
		String email = user.getEmail();
		String name=userProfilesDao.getUserName(user);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("email", email);
		map.put("name", name);
		map.put("mobile", user.getMobile());
		return map;
	}
	
	@Override
	public HashMap<String, Object> getParentStuffInfoById(int userId) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Users user = (Users) userDao.getById(userId);
		if(user == null){
			return null;
		}
		String path = user.getPath();
		String [] parentsId = path.split(",");
		//倒序遍历
		for(int i = parentsId.length-1; i>=0; i--){
			String parentId = parentsId[i];
			try{
				if(Integer.parseInt(parentId)>0){
					Users parent = (Users) userDao.getById(Integer.parseInt(parentId));
					if(parent != null && parent.getLevel() <= 3){
						parent.setPassword("");
						parent.setPaymentPassword("");
						parent.setSalty("");
						map.put("users", parent);
						
						UserProfiles userProfiles = userProfilesDao.findUserProfilesByUser(parent);
						if(userProfiles != null){
							userProfiles.getUser().setPassword("");
							userProfiles.getUser().setPaymentPassword("");
							userProfiles.getUser().setSalty("");
							map.put("profiles",userProfiles);
						}
						
						return map;
					}
				}
			}catch (Exception e){
				return null;
			}
		}
		
		return null;
	}
	

	@Override
	public HashMap<String, Object> getParentStuffInfo(HttpSession session) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Users user=SessionServiceImpl.getCurrentUser(session);
		String path = user.getPath();
		String [] parentsId = path.split(",");
		//倒序遍历
		for(int i = parentsId.length-1; i>=0; i--){
			String parentId = parentsId[i];
			try{
				if(Integer.parseInt(parentId)>0){
					Users parent = (Users) userDao.getById(Integer.parseInt(parentId));
					if(parent != null && parent.getLevel() <= 3){
						parent.setPassword("");
						parent.setPaymentPassword("");
						parent.setSalty("");
						map.put("users", parent);
						
						UserProfiles userProfiles = userProfilesDao.findUserProfilesByUser(parent);
						if(userProfiles != null){
							userProfiles.getUser().setPassword("");
							userProfiles.getUser().setPaymentPassword("");
							userProfiles.getUser().setSalty("");
							map.put("profiles",userProfiles);
						}
						
						return map;
					}
				}
			}catch (Exception e){
				return null;
			}
		}
		
		return null;
	}

	@Override
	public Page<HashMap<String, Object>> getUserPageCapital(int user_id,int pageNo, int pageSize, String urlFormat,HttpSession session) {
		Page<HashMap<String,Object>> page=null;
		StringBuilder sqlBuilder=new StringBuilder();
		sqlBuilder.append("  select * from ");
		sqlBuilder.append("  (select id, 'deposits' as type, amount as amount,state as state,if(convert(order_no,char(3))='[D]',audited_time,if(payment_time is not null,payment_time,audited_time)) as deal_time,creat_time as create_time ,'入金到网页账户' as description ");
		sqlBuilder.append("  from deposits   where   user_id="+user_id);
		sqlBuilder.append("  UNION ALL ");
		sqlBuilder.append("   (select *  from (select id, 'log' as type, amount as amount,'success' as state,creat_time as deal_time,creat_time as create_time,description from user__balance_logs where withdrawal_id=0  and  deposit_id=0 and user_id="+user_id+" ) l)  ");
		sqlBuilder.append("  UNION ALL ");
		sqlBuilder.append("  select id, 'withdrawals' as type, amount as amount,state as state,audited_time as deal_time,creat_time as create_time ,'网页账户出金' as description from withdrawals ");
		sqlBuilder.append("  where user_id="+user_id+" order by 6 desc ) c ");
		System.out.println(sqlBuilder.toString());
		List<HashMap<String, Object>>  list=userDao.getSession().createSQLQuery(sqlBuilder.toString())
				.addScalar("id", IntegerType.INSTANCE)
				.addScalar("type", StringType.INSTANCE)
				.addScalar("amount", DoubleType.INSTANCE)
				.addScalar("state", StringType.INSTANCE)
				.addScalar("deal_time", TimestampType.INSTANCE)
				.addScalar("create_time", TimestampType.INSTANCE)
				.addScalar("description", StringType.INSTANCE)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.setMaxResults(pageSize)
				.setFirstResult((pageNo-1)*pageSize)
				.list();

		StringBuilder sqlBuilder1=new StringBuilder();
		sqlBuilder1.append("  select count(*) from ");
		sqlBuilder1.append("  (select id, 'deposits' as type, amount as amount,state as state,if(convert(order_no,char(3))='[D]',audited_time,if(payment_time is not null,payment_time,audited_time)) as deal_time,creat_time as create_time ,'账户入金' as description ");
		sqlBuilder1.append("  from deposits   where  user_id="+user_id);
		sqlBuilder1.append("  UNION ALL ");
		sqlBuilder1.append("   (select *  from (select id, 'log' as type, amount as amount,'success' as state,creat_time as deal_time,creat_time as create_time,description from user__balance_logs where withdrawal_id=0  and  deposit_id=0 and user_id="+user_id+" ) l)  ");
		sqlBuilder1.append("  UNION ALL ");
		sqlBuilder1.append("  select id, 'withdrawals' as type, amount as amount,state as state,audited_time as deal_time,creat_time as create_time  ,'账户出金' as description from withdrawals ");
		sqlBuilder1.append("  where user_id="+user_id+" order by 6 desc ) c  ");
		BigInteger bigVal = (BigInteger) userDao.getSession()
				.createSQLQuery(sqlBuilder1.toString()).uniqueResult();
		
		int totalRows=0;
		//BigInteger 转 int
		if (bigVal != null){
			totalRows = bigVal.intValue();	
		}
		page=new Page<>(totalRows, pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		return page;
	}
	
	@Override
	public HashMap<String, Object> getBankAccount(int user_id,int pageNo, int pageSize, String urlFormat,HttpSession session){
		HashMap<String, Object> result=new HashMap<>();
		List<Attachments> bankImg=new ArrayList<Attachments>();
		 Users user=(Users) userDao.getById(user_id);
		 ArrayList<Criterion> wheres=new ArrayList<>();
		 wheres.add(Restrictions.eq("user", user));
		 ArrayList<Order> orders=new ArrayList<>();
		 Page<UserBankAccounts> page = (Page<UserBankAccounts>) userBankAccountDao.getPage(wheres, orders, null, pageSize, pageNo, null);
		 page.generateButtons(urlFormat);
		 for (UserBankAccounts item : page.getList()) {
			 item.setUser(null);
			 List<Attachments> images=attachmentDao.getFileImage(UserBankAccounts.getOwnertype(),item.getId());
			 if(images!=null&&images.size()>0){
				 bankImg.addAll(images);
			 }
		 }
		 result.put("page", page);
		 result.put("bankImg", bankImg);
		return result;
	}
	
	
	@Override
	public  HashMap<String,Object> getUserCount(int userId,HttpSession session){
		HashMap<String,Object> result=new HashMap<String,Object>();
		String sql1="SELECT count(*) FROM user__bank_accounts where state='WAITING' and user_id="+userId;
		BigInteger bigVal1 = (BigInteger) userDao.getSession().createSQLQuery(sql1).uniqueResult();
		int totalRows1=0;
		if (bigVal1 != null){
			totalRows1 = bigVal1.intValue();	
		}
		String sql2="SELECT count(*) FROM withdrawals where state='WAITING' and user_id="+userId;
		BigInteger bigVal2 = (BigInteger) userDao.getSession().createSQLQuery(sql2).uniqueResult();
		int totalRows2=0;
		if (bigVal2 != null){
			totalRows1 = bigVal2.intValue();	
		}
		String sql3="SELECT count(*) FROM withdrawals where state='AUDITED' and user_id="+userId;
		BigInteger bigVal3 = (BigInteger) userDao.getSession().createSQLQuery(sql3).uniqueResult();
		int totalRows3=0;
		if (bigVal3 != null){
			totalRows3 = bigVal3.intValue();	
		}
		String sql4="SELECT count(*) FROM deposits where instr(order_no,'[D]')=1 and user_id="+userId;
		BigInteger bigVal4 = (BigInteger) userDao.getSession().createSQLQuery(sql4).uniqueResult();
		int totalRows4=0;
		if (bigVal4 != null){
			totalRows4 = bigVal1.intValue();	
		}
       result.put("countBankAccount", bigVal1);
       result.put("countWithdownals", bigVal2);
       result.put("countWithdownalsFinance", bigVal3);
       result.put("countDeposits", bigVal4);
       return result;
	}
	
	@Override
	public Users getUserInfo(HttpSession session){
		Users user = SessionServiceImpl.getCurrentUser(session);
		if(user != null){
			user.set_vipGradeName(BackOffice.getInst().companyHook.getVipGradeShowName(user.getVipGrade(), user.getLevel()));
			if(user.getParent() != null){
				user.set_up_id(user.getParent().getId());
			}
			user.setPassword("");
			user.setPaymentPassword("");user.setSalty("");
			return user;
		}
		return null;
	}
//	
//	@Deprecated
//	public Page<PojoCapital> getUserPageCapital2(int user_id,int pageNo, int pageSize, String urlFormat,HttpSession session) {
//		List<PojoCapital> pojoCapitalList = new ArrayList<PojoCapital>();
//		List<Order> orders = new ArrayList<Order>();
//		orders.add(Order.desc("creatTime"));
//		
//		
//		//1、获取和这个用户有关的所有出金入金并且状态不是处理完毕的单子
//		List<Criterion> depositWheres = new ArrayList<Criterion>();
//		depositWheres.add(Restrictions.ne("state", "DEPOSITED"));
//		depositWheres.add(Restrictions.eq("user",(Users)userDao.getById(user_id)));
//		
//		List<Deposits> depositsList = depositDao.getAll(depositWheres, orders, null, 0, 0).list();
//		for (Deposits deposits : depositsList) {
//			PojoCapital pojoCapital = new PojoCapital();
//			pojoCapital.setId(deposits.getId());
//			pojoCapital.setState(deposits.getState());
//			pojoCapital.setType("deposits");
//			pojoCapital.setAmount(deposits.getAmount());
//			pojoCapital.setCreate_time(deposits.getCreatTime());
//			if(deposits.getOrderNum().contains("[D]")){//代为入金——/处理时间
//				pojoCapital.setDescription("代为入金");
//				pojoCapital.setDeal_time(deposits.getAuditedTime());
//			}else{
//				pojoCapital.setDescription("账户入金，汇率："+settingDao.getDouble("DepositRateCNY"));//入金——支付时间
//				pojoCapital.setDeal_time(deposits.getPaymentTime());
//			}
//			pojoCapitalList.add(pojoCapital);
//		}
//		
//		
//		List<Criterion> withdrawalWheres = new ArrayList<Criterion>();
//		withdrawalWheres.add(Restrictions.ne("state", "REMITTED"));
//		withdrawalWheres.add(Restrictions.eq("user",(Users)userDao.getById(user_id)));
//		List<Withdrawals> withdrawalsList= withdrawalDao.getAll(depositWheres, orders, null, 0, 0).list();
//		for (Withdrawals withdrawals : withdrawalsList) {
//			PojoCapital pojoCapital = new PojoCapital();
//			pojoCapital.setId(withdrawals.getId());
//			pojoCapital.setState(withdrawals.getState());
//			pojoCapital.setType("withdrawals");
//			pojoCapital.setAmount(withdrawals.getAmount());
//			pojoCapital.setCreate_time(withdrawals.getCreatTime());
//			pojoCapital.setDescription("账户出金，汇率："+withdrawals.getExchangeRate());//入金——支付时间
//			pojoCapital.setDeal_time(withdrawals.getAuditedTime());
//			pojoCapitalList.add(pojoCapital);
//		}
//		
//		//2、获取和这个用户有关的余额日志（都是已经处理完的）
//		List<Criterion> logsWheres = new ArrayList<Criterion>();
//		logsWheres.add(Restrictions.eq("user", (Users)userDao.getById(user_id)));
//		List<UserBalanceLogs> logsList= userBalanceLogDao.getAll(logsWheres, orders, null, 0, 0).list();
//		for (UserBalanceLogs userBalanceLogs : logsList) {
//			PojoCapital pojoCapital = new PojoCapital();
//			pojoCapital.setId(userBalanceLogs.getId());
//			pojoCapital.setState("SUCCESS");
//			pojoCapital.setType("log");
//			pojoCapital.setAmount(userBalanceLogs.getAmount());
//			pojoCapital.setCreate_time(userBalanceLogs.getCreatTime());
//			pojoCapital.setDeal_time(userBalanceLogs.getCreatTime());
//			pojoCapital.setDescription(userBalanceLogs.getDescription());
//			pojoCapitalList.add(pojoCapital);
//		}
//		
//		Collections.sort(pojoCapitalList, new Comparator<PojoCapital>(){
//			@Override
//			public int compare(PojoCapital pojoCapital0,PojoCapital pojoCapital1){
//				 int flag = pojoCapital0.getCreate_time().compareTo(pojoCapital1.getCreate_time());
//				 return -flag;//由于是按照时间倒序排列，所以返回负数
//			 }
//		});	
//		
//		Page<PojoCapital> page = new Page<PojoCapital>(pojoCapitalList.size(), pageSize, pageNo, pojoCapitalList);
//		page.generateButtons(urlFormat);
//		return page;
//	}

}


















