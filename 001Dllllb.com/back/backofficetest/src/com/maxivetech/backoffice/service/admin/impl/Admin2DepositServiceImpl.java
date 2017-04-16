package com.maxivetech.backoffice.service.admin.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.type.DoubleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.AdminDao;
import com.maxivetech.backoffice.dao.DepositDao;
import com.maxivetech.backoffice.dao.SettingDao;
import com.maxivetech.backoffice.dao.UserBalanceDao;
import com.maxivetech.backoffice.dao.UserBalanceLogDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Deposits;
import com.maxivetech.backoffice.entity.UserBalances;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.entity.Withdrawals;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.pojo.PojoDepositUser;
import com.maxivetech.backoffice.pojo.PojoSum;
import com.maxivetech.backoffice.sendmail.PostmanDeposit;
import com.maxivetech.backoffice.service.admin.Admin2DepositService;
import com.maxivetech.backoffice.service.admin.AdminDepositService;
import com.maxivetech.backoffice.service.user.impl.SessionServiceImpl;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperDate;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class Admin2DepositServiceImpl implements Admin2DepositService {
	@Autowired
	private DepositDao depositDao;
	@Autowired
	private AdminDao adminDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserProfilesDao userProfileDao;
	@Autowired
	private UserBalanceDao userBalanceDao;
	@Autowired
	private UserBalanceLogDao userBalanceLogDao;
	@Autowired
	private SettingDao settingDao;
	
	
	
//	@CheckRole(role = {Role.FinancialSuperior, Role.FinancialStaff})
	@Override
	public List<PojoDepositUser> findUser(String keyword, HttpSession session) {
		keyword = "%"+keyword+"%";
		ArrayList<Criterion> wheres = new ArrayList<Criterion>();
		List<Users> usersList = new ArrayList<>();
		List<PojoDepositUser> pojoDepositUserList = new ArrayList<>();
		
		ArrayList<Criterion> wheres2Users = new ArrayList<Criterion>();
		wheres2Users.add(Restrictions.or(
				Restrictions.like("email", keyword),
				Restrictions.like("mobile", keyword)));
		usersList = userDao.getAll(wheres2Users, null, null, 0, 0).list();

		ArrayList<Criterion> wheres2UserProfile = new ArrayList<Criterion>();
		wheres2UserProfile.add(Restrictions.or(
				Restrictions.like("userName", keyword),
				Restrictions.like("userEName", keyword),
				Restrictions.like("userIdCard", keyword)));
		List<UserProfiles> userProfilesList = userProfileDao.getAll(wheres2UserProfile, null, null, 0, 0).list();
		if(userProfilesList.size()>0){
			for (UserProfiles userProfiles : userProfilesList) {
				usersList.add(userProfiles.getUser());
			}
		}
		
		//封装到pojoDepositUserList传下
		for (Users users : usersList) {
			UserProfiles userProfiles = userProfileDao.findUserProfilesByUser(users);
			String idcard = userProfiles.getUserIdCard();//传下身份证
			String username = userProfiles.getUserName();
			pojoDepositUserList.add(new PojoDepositUser(users.getId(),username, users.getEmail(), users.getMobile(),idcard));
		}
		
		return pojoDepositUserList;
	}
	
	

	@CheckRole(role = {Role.OperationsManager, Role.FinancialStaff, Role.FinancialSuperior})
	@Override
	public String add(int userId, String orderNum,double amount,String payDate,String userComment,String operationPassword,HttpSession session)throws ForbiddenException{
		
		//验证操作密码
		Admins admin = (Admins) adminDao.getById(SessionServiceImpl.getCurrent(session).getId());
		
		String md5Password = DigestUtils.md5DigestAsHex((operationPassword + admin.getSalty()).getBytes());
		if(!admin.getOperationPassword().equals(md5Password)){
			return "对不起，您输入的操作密码错误。";
		}
		
		//验证用户是否存在
		Users user = (Users) userDao.getById(userId);
		if (user == null){
			return "对不起，该用户不存在。";
		}
		
		Deposits deposits = new Deposits();

		Date date = HelperDate.parse(payDate, "yyyy-MM-dd HH:mm:ss");
		deposits.setAuditedMemo("");
		deposits.setUserComment(userComment);
		deposits.setState("PENDING_SUPERVISOR");
		deposits.setAuditedTime(null);
		deposits.setPaymentTime(date);
		deposits.setCreatTime(new Date());
		deposits.setUser(user);
		deposits.setAmount(amount);
		deposits.setCurrency("USD");
		deposits.setOrderNum("[D]"+orderNum);
		depositDao.save(deposits);

		depositDao.commit();

		return "提交成功";
	}
	
	
	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer,
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.RiskManagementCommissioner, Role.CustomerServiceStaff})
	@Override
	public Page<Deposits> getPage(int userId,int pageNo, int pageSize, String urlFormat,
			String state, String start, String end, String keyword, boolean replacement,  HttpSession session) {

		//WHERE
		ArrayList<Criterion> wheres = new ArrayList<Criterion>();
		if(userId!=0){
			Users users= (Users) userDao.getById(userId);
			wheres.add(Restrictions.eq("user", users));
		}
		
		if(replacement==true){
			wheres.add(Restrictions.like("orderNum","%[D]%"));
		}
		if(state.length() > 0){
			wheres.add(Restrictions.eq("state",state));
		}
		
		//时间
		Date startDate = HelperDate.parse(start, "yyyy-MM-dd HH:mm:ss");
		Date endDate = HelperDate.parse(end, "yyyy-MM-dd HH:mm:ss");
		if (startDate != null) {
			wheres.add(Restrictions.or(
					Restrictions.ge("creatTime", startDate),
					Restrictions.ge("paymentTime", startDate),
					Restrictions.ge("auditedTime", startDate)));
		}
		if (endDate != null) {
			endDate.setTime(endDate.getTime() + 24 * 60 * 60 * 1000);
			wheres.add(Restrictions.or(
					Restrictions.lt("creatTime", endDate),
					Restrictions.lt("paymentTime", endDate),
					Restrictions.lt("auditedTime", endDate)));
		}
		
		
		//关键字
		if(keyword.trim().length()>0){
			keyword ="%"+keyword.trim()+"%";
			
			//根据姓名（需要去UserProfiles表）把用户搜索出来，
			List <Criterion> wheres2UserProfile = new ArrayList<Criterion>();
			wheres2UserProfile.add(Restrictions.or(Restrictions.like("userName", keyword)));
			List<UserProfiles> userProfilesList = userProfileDao.getAll(wheres2UserProfile, null, null, 0, 0).list();
			
			
			//根据邮箱和电话（需要去Users表）把用户搜索出来，
			List<Criterion>wheres2Users = new ArrayList<Criterion>();
			wheres2Users.add(Restrictions.or(
					Restrictions.like("email", keyword),
					Restrictions.like("mobile", keyword)));
			List<Users> usersList = userDao.getAll(wheres2Users, null, null, 0, 0).list();
			
			//当有多个用户时（从个人资料里面检出），分别设置查询条件
			List <SimpleExpression> simpleExpressionList = new ArrayList<>();
			if(userProfilesList != null && userProfilesList.size()>0){//有数据
				for(UserProfiles userProfile : userProfilesList){
					simpleExpressionList.add(Restrictions.eq("user", userProfile.getUser()));
				}
			}
			//当有多个用户时（从用户列表里面检出），分别设置查询条件
			if(usersList != null && usersList.size()>0){//有数据
				for(Users user : usersList){
					simpleExpressionList.add(Restrictions.eq("user", user));
				}
			}
			
			//再根据用户，设置为OR条件，搜索出金单
			if(simpleExpressionList.size()>0){
				LogicalExpression logicalExpression = Restrictions.or(simpleExpressionList.get(0),simpleExpressionList.get(0));
				for (SimpleExpression simpleExpression : simpleExpressionList) {
					logicalExpression = Restrictions.or(logicalExpression, simpleExpression);
				}
				wheres.add(logicalExpression);
			}
		}
		
		
		//ORDER BY
		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("creatTime"));
		
		//PAGE
		Page<Deposits> page = (Page<Deposits>) depositDao.getPage(
				wheres, orders, null, pageSize, pageNo, null);
		page.generateButtons(urlFormat);
		
		//清理
		for (Deposits deposit : page.getList()) {
			deposit.set_userId(deposit.getUser().getId());
			deposit.set_userEmail(deposit.getUser().getEmail());
			deposit.set_userName(userProfileDao.getUserName(deposit.getUser()));
			deposit.setUser(null);
		}
		return page;
	}
	
	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer,
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.RiskManagementCommissioner, Role.CustomerServiceStaff})
	@Override
	public List<PojoCounts> getCounts(HttpSession session) {

		List<PojoCounts> list = new ArrayList<PojoCounts>();
		
		//all
		{
		    String hql = "select count(*) from Deposits";
		    long count = (long)depositDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("all", count));
		}
		//pending_pay
		{
		    String hql = "select count(*) from Deposits where state='PENDING_PAY'";
		    long count = (long)depositDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("pending_pay", count));
		}
		//deposited
		{
		    String hql = "select count(*) from Deposits where state='DEPOSITED'";
		    long count = (long)depositDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("deposited", count));
		}
		//pending_audit
		{
		    String hql = "select count(*) from Deposits where state='PENDING_AUDIT'";
		    long count = (long)depositDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("pending_audit", count));
		}
		//pending_supervisor
		{
			String hql = "select count(*) from Deposits where state='PENDING_SUPERVISOR'";
			long count = (long)depositDao.getSession().createQuery(hql).uniqueResult();  
			list.add(new PojoCounts("pending_supervisor", count));
		}
		//accepted
		{
			String hql = "select count(*) from Deposits where state='ACCEPTED'";
			long count = (long)depositDao.getSession().createQuery(hql).uniqueResult();  
			list.add(new PojoCounts("accepted", count));
		}
		return list;
	
}
	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer,
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.RiskManagementCommissioner, Role.CustomerServiceStaff})
	@Override
	public List<PojoSum> getSummarizes(String startDateParam, String endDateParam, HttpSession session) {

		List<PojoSum> list = new ArrayList<PojoSum>();
		
		//total
		{
		    String hql = "select sum(amount) from Deposits d where d.state='DEPOSITED'";
		    Object obj = depositDao.getSession().createQuery(hql).uniqueResult(); 
			if(obj!=null){
				double sum = (double) obj;
				 list.add(new PojoSum("total", sum));
			}else {
				list.add(new PojoSum("total", 0.00));
			}
		   
		}
		//today
		{	
			Date date=new Date();
			//获取今天0时0分0秒
			SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd 00:00:00");
			String startDate =df.format(new Date());
			//获取明天0时0分0秒
			long time=date.getTime()+24*60*60*1000;
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd 00:00:00");
			String endDate =sdf.format(time);
			
			String hql = "select sum(amount) from Deposits d where d.state='DEPOSITED' and d.paymentTime>='"+startDate+"' and d.paymentTime<'"+endDate+"'";
				Object obj = depositDao.getSession().createQuery(hql).uniqueResult(); 
				if(obj!=null){
					double sum = (double) obj;
					list.add(new PojoSum("td", sum));
				}else {
					list.add(new PojoSum("td", 0.00));
				}
				
		  }
		//yesterday
		{	
			Date date=new Date();
			//获取昨天0时0分0秒
			long time=date.getTime()-24*60*60*1000;
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd 00:00:00");
			String startDate =sdf.format(time);
			//获取今天0时0分0秒
			SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd 00:00:00");
			String endDate =df.format(new Date());
			
			String hql = "select sum(amount) from Deposits d where d.state='DEPOSITED' and d.paymentTime>='"+startDate+"' and d.paymentTime<'"+endDate+"'";
			Object obj = depositDao.getSession().createQuery(hql).uniqueResult(); 
			if(obj!=null){
				double sum = (double) obj;
				list.add(new PojoSum("yd", sum));
			}else {
				list.add(new PojoSum("yd", 0.00));
			}
			
		}
		//最近7天
		{	
			Date date=new Date();
			//获取六天前的0时0分0秒
			long startTime=date.getTime()-6*24*60*60*1000;
			SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd 00:00:00");
			String startDate =df.format(startTime);
			//获取明天0时0分0秒
			long time=date.getTime()+24*60*60*1000;
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd 00:00:00");
			String endDate =sdf.format(time);
			
			String hql = "select sum(amount) from Deposits d where d.state='DEPOSITED' and d.paymentTime>='"+startDate+"' and d.paymentTime<'"+endDate+"'";
			Object obj = depositDao.getSession().createQuery(hql).uniqueResult(); 
			if(obj!=null){
				double sum = (double) obj;
				list.add(new PojoSum("7d", sum));
			}else {
				list.add(new PojoSum("7d", 0.00));
			}
			
		}
		//最近30天
		{	
			Date date=new Date();
			//获取29天前的0时0分0秒
			Calendar cal=Calendar.getInstance();
			cal.setTime(date);
			cal.add(cal.DATE, -29);
			SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd 00:00:00");
			String startDate =df.format(cal.getTime());
			//获取明天0时0分0秒
			long time=date.getTime()+24*60*60*1000;
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd 00:00:00");
			String endDate =sdf.format(time);
			
			String hql = "select sum(amount) from Deposits d where d.state='DEPOSITED' and d.paymentTime>='"+startDate+"' and d.paymentTime<'"+endDate+"'";
			Object obj = depositDao.getSession().createQuery(hql).uniqueResult(); 
			if(obj!=null){
				double sum = (double) obj;
				list.add(new PojoSum("30d", sum));
			}else {
				list.add(new PojoSum("30d", 0.00));
			}
			
		}
		//本月
		{	
			Date date=new Date();
			//获取本月1号0时0分0秒
			SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-1 00:00:00");
			String startDate =df.format(date);
			//获取明天0时0分0秒
			long time=date.getTime()+24*60*60*1000;
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd 00:00:00");
			String endDate =sdf.format(time);
			
			String hql = "select sum(amount) from Deposits d where d.state='DEPOSITED' and d.paymentTime>='"+startDate+"' and d.paymentTime<'"+endDate+"'";
			Object obj = depositDao.getSession().createQuery(hql).uniqueResult(); 
			if(obj!=null){
				double sum = (double) obj;
				list.add(new PojoSum("tm", sum));
			}else {
				list.add(new PojoSum("tm", 0.00));
			}
			
		}
		
		
		//这一年的每个月，月份从0开始的
		{
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			for(int i = 0 ; i < 12; i++){
				calendar.set(year, i, 1);
				//2016-08-01 00:00:00
				String startDate =  new SimpleDateFormat("YYYY-MM-dd 00:00:00").format(calendar.getTime());
				calendar.add(Calendar.MONTH, +1);//加一个月
				//2016-09-01 00:00:00
				String endDate =  new SimpleDateFormat("YYYY-MM-dd 00:00:00").format(calendar.getTime());
				String sql = "SELECT SUM(amount) from Deposits d where "+
							 "d.state='DEPOSITED' AND (d.paymentTime BETWEEN '"+startDate+"' and '"+endDate+"')";
				Object obj = depositDao.getSession().createQuery(sql).uniqueResult(); 
				if(obj!=null){
					double sum = (double) obj;
					list.add(new PojoSum("EM_"+year+"_"+i, sum));
				}else {
					list.add(new PojoSum("EM_"+year+"_"+i, 0.00));
				}
			}
		}
		
		//between startDate and endDate
		{
			String hql = "select sum(amount) from Deposits d where d.state='DEPOSITED'";//默认是所有的，即total
			if(startDateParam.length()==0 && endDateParam.length()==0){
				return list;
			}else if(startDateParam.length()>0 && endDateParam.length()==0){
				hql = "select sum(amount) from Deposits d where d.state='DEPOSITED' AND d.paymentTime>='"+startDateParam+"'";
			}else if(startDateParam.length()==0 && endDateParam.length()>0){
				hql = "select sum(amount) from Deposits d where d.state='DEPOSITED' AND d.paymentTime<='"+endDateParam+"'";
			}else if(startDateParam.length()>0 && endDateParam.length()>0){
				hql = "select sum(amount) from Deposits d where d.state='DEPOSITED' AND(d.paymentTime BETWEEN '"+startDateParam+"' and '"+endDateParam+"')";
			}
		    Object obj = depositDao.getSession().createQuery(hql).uniqueResult(); 
			if(obj!=null){
				double sum = (double) obj;
				if(startDateParam.length()==0){
					startDateParam = "开始";//到最开始
				}
				if(endDateParam.length()==0){
					endDateParam = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date());//到现在
				}
				list.add(new PojoSum("IN_"+startDateParam+"_"+endDateParam, sum));
			}else {
				list.add(new PojoSum("IN_"+startDateParam+"_"+endDateParam, 0.00));
			}
			
		}
		
		
		return list;
	}

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer,
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.RiskManagementCommissioner, Role.CustomerServiceStaff})
	@Override
	public Deposits getOne(int id, HttpSession session) {

		Deposits d = (Deposits) depositDao.getById(id);
		if (d != null) {
			// 通过所有者验证
			d.set_userName(userProfileDao.getUserName(d.getUser()));
			d.set_userEmail(d.getUser().getEmail());
			d.set_userId(d.getUser().getId());
			// 清除
			d.setUser(null);

			return d;
		}else{
			throw new RuntimeException("该出金单不存在。");
		}
	}

	@CheckRole(role = {Role.OperationsManager, Role.FinancialSuperior})
	@Override
	public synchronized String changeState(int id, String operationPassword, String state,
			String memo, HttpSession session) {

		// 验证操作密码
		Admins admin = (Admins) adminDao.getById(SessionServiceImpl.getCurrent(
				session).getId());

		String md5Password = DigestUtils
				.md5DigestAsHex((operationPassword + admin.getSalty())
						.getBytes());
		if (!admin.getOperationPassword().equals(md5Password)) {
			return "密码错误";
		}

		Deposits deposit = (Deposits) depositDao.getById(id);
		// 验证入金单是否存在
		if (deposit == null) {
			return "该入金单不存在";
		}
		if (deposit.getState().equals(state)) {
			return "操作失败";
		} // 状态一样重复操作
		
		
		switch (deposit.getState()) {
		case "PENDING_SUPERVISOR":
			deposit.setState(state);
			deposit.setAuditedTime(new Date());
			deposit.setAuditedMemo(memo);
			depositDao.saveOrUpdate(deposit);
			break;

		default:
			return "操作失败";
		}

		// 通过审核
		switch (state) {
		case "DEPOSITED":
			Users user = deposit.getUser();
			double rate = settingDao.getDouble("DepositRateCNY");
			if (rate <= 0) {
				throw new RuntimeException("汇率错误！");
			}

			// 更改用户余额
			//double amountUSD = (long)(deposit.getAmount() / rate * 100) / 100.0;
			double amountUSD = deposit.getAmount();
			UserBalances ub = userBalanceDao.getBalance(user, "USD");
			userBalanceDao.increaseAmountAvailable(ub, amountUSD);

			// 生成用户余额变动日志
			userBalanceLogDao.addLog(ub, amountUSD, deposit.getId(), 0, 0, "代为入金" + deposit.getUserComment());
			
			//发邮件（代为入金成功）
			{
				PostmanDeposit postman = new PostmanDeposit(user.getEmail());
				postman.deposited(deposit, ub);
			}
		}
		
		depositDao.commit();
		
		return "操作成功";
	}

	@CheckRole(role = {Role.OperationsManager, Role.FinancialSuperior, Role.FinancialStaff})
	@Override
	public HashMap<String, Object> lookupUser(String mobile, HttpSession session) {

		
		//验证用户是否存在
		Users user = userDao.findByMobile(mobile);
		if (user == null){
			throw new RuntimeException("用户不存在");
		}
		
		//输出
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("name", userProfileDao.getUserName(user));
		map.put("email", user.getEmail());
		
		return map;
	}
}