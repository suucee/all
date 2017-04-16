package com.maxivetech.backoffice.service.admin.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.AdminDao;
import com.maxivetech.backoffice.dao.UserBalanceDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.dao.WithdrawalDao;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.entity.Withdrawals;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.pojo.PojoSum;
import com.maxivetech.backoffice.sendmail.PostmanWithdrawal;
import com.maxivetech.backoffice.service.admin.Admin2WithdrawalService;
import com.maxivetech.backoffice.service.admin.AdminWithdrawalService;
import com.maxivetech.backoffice.service.user.impl.SessionServiceImpl;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperDate;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class Admin2WithdrawalServiceImpl implements Admin2WithdrawalService {
	@Autowired
	private WithdrawalDao withdrawalDao;
	@Autowired
	private AdminDao adminDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserProfilesDao userProfileDao;
	@Autowired
	private UserBalanceDao userBalanceDao;

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer,
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.RiskManagementCommissioner, Role.CustomerServiceStaff})
	@Override
	public Page<Withdrawals> getPage(int userId,int pageNo, int pageSize, String urlFormat,
			String scheme, String startDate, String endDate, String keyword, HttpSession session) {

		//WHERE
		ArrayList<Criterion> wheres = new ArrayList<Criterion>();
		if(userId!=0){
			Users users= (Users) userDao.getById(userId);
			wheres.add(Restrictions.eq("user", users));
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //格式化时间
		java.sql.Timestamp staDate = null, enDate = null; 
		Date sDate = null,eDate = null;
		if(startDate.length()>0){
			try {
				sDate = sdf.parse(startDate);
				staDate = new Timestamp(sDate.getTime());
			} catch (ParseException e) {
				staDate = null;
			}
		}
		
		if(endDate.length()>0){
			try {
				eDate=sdf.parse(endDate);   //格式化endDate 结束时间为 Date类型
				enDate = new Timestamp(eDate.getTime());
			} catch (ParseException e) {
				enDate = null;
			}
		}
		
		if(scheme.length()>0){
			wheres.add(Restrictions.eq("state", scheme));
		}
		
		
		if(staDate == null && enDate == null){//不限
		}
		else if(staDate != null && enDate == null){//有开始限制
			wheres.add(Restrictions.or(
					Restrictions.ge("creatTime", staDate),
					Restrictions.ge("auditedTime", staDate),
					Restrictions.ge("canceledTime", staDate)));
		}
		else if(staDate == null && enDate != null){//有结束限制
			wheres.add(Restrictions.or(
					Restrictions.le("creatTime", enDate),
					Restrictions.le("auditedTime", enDate),
					Restrictions.le("canceledTime", enDate)));
		}
		else if(staDate != null && enDate != null){//两端都有限制
			wheres.add(Restrictions.or(
					Restrictions.between("creatTime", staDate, enDate),
					Restrictions.between("auditedTime", staDate, enDate),
					Restrictions.between("canceledTime", staDate, enDate)));
		}
		
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
		Page<Withdrawals> page = (Page<Withdrawals>) withdrawalDao.getPage(
				wheres, orders, null, pageSize, pageNo, null);
		page.generateButtons(urlFormat);
		
		//设置好出金的用户id和用户邮箱，用户名
		for (Withdrawals withdrawal : page.getList()) {
			withdrawal.set_userId(withdrawal.getUser().getId());
			withdrawal.set_userEmail(withdrawal.getUser().getEmail());
			withdrawal.set_userName(userProfileDao.getUserName(withdrawal.getUser()));
			withdrawal.setUser(null);
		}
		return page;
	}

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer,
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.RiskManagementCommissioner, Role.CustomerServiceStaff})
	@Override
	public List<PojoCounts> getCounts(HttpSession session) {

		//TODO:弄個多少分鐘緩存，省得各種查數據庫
		List<PojoCounts> list = new ArrayList<PojoCounts>();
		
		//all
		{
		    String hql = "select count(*) from Withdrawals";
		    long count = (long)withdrawalDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("all", count));
		}
		//waiting
		{
		    String hql = "select count(*) from Withdrawals where state='WAITING'";
		    long count = (long)withdrawalDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("pending", count));
		}
		//audited
		{
		    String hql = "select count(*) from Withdrawals where state='AUDITED'";
		    long count = (long)withdrawalDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("audited", count));
		}
		//rejected
		{
		    String hql = "select count(*) from Withdrawals where state='REJECTED'";
		    long count = (long)withdrawalDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("rejected", count));
		}
		//pending_supervisor
		{
			String hql = "select count(*) from Withdrawals where state='PENDING_SUPERVISOR'";
			long count = (long)withdrawalDao.getSession().createQuery(hql).uniqueResult();  
			list.add(new PojoCounts("pending_supervisor", count));
		}
		//remitted
		{
		    String hql = "select count(*) from Withdrawals where state='REMITTED'";
		    long count = (long)withdrawalDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("remitted", count));
		}
		//returned
		{
		    String hql = "select count(*) from Withdrawals where state='BACK'";
		    long count = (long)withdrawalDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("returned", count));
		}
		//canceled
		{
			String hql = "select count(*) from Withdrawals where state='CANCELED'";
		    long count = (long)withdrawalDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("canceled", count));
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
		    String hql = "select sum(amount) from Withdrawals w where w.state='REMITTED'";
		    Object obj = withdrawalDao.getSession().createQuery(hql).uniqueResult(); 
			if(obj!=null){
				double sum = (double) obj;
				 list.add(new PojoSum("total", sum));
			}else {
				list.add(new PojoSum("total", 0.00));
			}
		   
		}
		//待汇出
		{
			String hql = "select sum(amount) from Withdrawals w where w.state='AUDITED'";
			Object obj = withdrawalDao.getSession().createQuery(hql).uniqueResult(); 
			if(obj!=null){
				double sum = (double) obj;
				list.add(new PojoSum("tbr", sum));
			}else {
				list.add(new PojoSum("tbr", 0.00));
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
			
			String hql = "select sum(amount) from Withdrawals w where w.state='REMITTED' and w.auditedTime>='"+startDate+"' and w.auditedTime<'"+endDate+"'";
				Object obj = withdrawalDao.getSession().createQuery(hql).uniqueResult(); 
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
			
			String hql = "select sum(amount) from Withdrawals w where w.state='REMITTED' and w.auditedTime>='"+startDate+"' and w.auditedTime<'"+endDate+"'";
			Object obj = withdrawalDao.getSession().createQuery(hql).uniqueResult(); 
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
			
			String hql = "select sum(amount) from Withdrawals w where w.state='REMITTED' and w.auditedTime>='"+startDate+"' and w.auditedTime<'"+endDate+"'";
			Object obj = withdrawalDao.getSession().createQuery(hql).uniqueResult(); 
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
			
			String hql = "select sum(amount) from Withdrawals w where w.state='REMITTED' and w.auditedTime>='"+startDate+"' and w.auditedTime<'"+endDate+"'";
			Object obj = withdrawalDao.getSession().createQuery(hql).uniqueResult(); 
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
			
			String hql = "select sum(amount) from Withdrawals w where w.state='REMITTED' and w.auditedTime>='"+startDate+"' and w.auditedTime<'"+endDate+"'";
			Object obj = withdrawalDao.getSession().createQuery(hql).uniqueResult(); 
			if(obj!=null){
				double sum = (double) obj;
				list.add(new PojoSum("tm", sum));
			}else {
				list.add(new PojoSum("tm", 0.00));
			}
			
		}
		
		//这一年的每个月，月份从0开始
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
				String sql = "SELECT SUM(amount) from Withdrawals w where "+
							 "w.state='REMITTED' AND(w.auditedTime BETWEEN '"+startDate+"' and '"+endDate+"')";
				Object obj = withdrawalDao.getSession().createQuery(sql).uniqueResult(); 
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
			String hql = "select sum(amount) from Withdrawals w where w.state='REMITTED'";//默认是所有的，即total
			if(startDateParam.length()==0 && endDateParam.length()==0){
				return list;
			}else if(startDateParam.length()>0 && endDateParam.length()==0){
				hql = "select sum(amount) from Withdrawals w where w.state='REMITTED' AND w.auditedTime>='"+startDateParam+"'";
			}else if(startDateParam.length()==0 && endDateParam.length()>0){
				hql = "select sum(amount) from Withdrawals w where w.state='REMITTED' AND w.auditedTime<='"+endDateParam+"'";
			}else if(startDateParam.length()>0 && endDateParam.length()>0){
				hql = "select sum(amount) from Withdrawals w where w.state='REMITTED' AND(w.auditedTime BETWEEN '"+startDateParam+"' and '"+endDateParam+"')";
			}
		    Object obj = withdrawalDao.getSession().createQuery(hql).uniqueResult(); 
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
	public Withdrawals getOne(int id, HttpSession session) {
		
		 Withdrawals ret = (Withdrawals) withdrawalDao.getById(id); 
		 if (ret != null ) { 
			//通过所有者验证
		  
		  //清除 
			 ret.setUser(null);
		  
		  return ret;
		  }
		return null;
	}


	@CheckRole(role = {Role.OperationsManager,
			Role.FinancialSuperior, Role.FinancialStaff})
	@Override
	public String changeState(int id,String nowState,String operationPassword, String state, String memo, HttpSession session) {

		//判断是否是待财务主管审核
		if(HelperAuthority.isFinancialStaff(session)&&nowState.equals("PENDING_SUPERVISOR")){
			return "请报财务主管处理";
		}
		Admins admin=(Admins) adminDao.getById(SessionServiceImpl.getCurrent(session).getId());
		
		String md5Password = DigestUtils.md5DigestAsHex((operationPassword + admin.getSalty()).getBytes());
		if(!admin.getOperationPassword().equals(md5Password)){
			return "密码错误";
		}
		Withdrawals withdrawal = (Withdrawals) withdrawalDao.getById(id);
		if (withdrawal == null) {return "操作失败";}
		if (withdrawal.getState().equals(state)) {return "操作失败";}	//状态一样重复操作
		
		//更新状态
		switch (withdrawal.getState()) {
		
		case "AUDITED":
		case "REMITTED":
		case "BACK":
		case "PENDING_SUPERVISOR":
			withdrawal.setState(state);
			withdrawal.setAuditedTime(new Date());
			withdrawal.setAuditedMemo(memo);
			withdrawalDao.saveOrUpdate(withdrawal);
			break;
		case "CANCELED":
		case "WAITING":
		case "REJECTED":
		default:
			return "操作失败";
		
		}
		
				
		//发邮件准备
		Users user = withdrawal.getUser();
		PostmanWithdrawal postman = new PostmanWithdrawal(user.getEmail());

		try {
			switch (state) {
			case "REMITTED":	//匯出
				//发邮件
				{
					postman.remitted(withdrawal, userBalanceDao.getBalance(user, withdrawal.getCurrency()));
				}
				break;
			case "RETURNED":		//銀行退回
				//发邮件
				{
					postman.returned(withdrawal, userBalanceDao.getBalance(user, withdrawal.getCurrency()));
				}
				break;
			case "REJECTED":	//驳回
				//发邮件
				{
					postman.rejected(withdrawal, userBalanceDao.getBalance(user, withdrawal.getCurrency()));
				}
				break;
			}

			withdrawalDao.commit();
				
			return "操作成功";
		} catch (Exception e) {
			return "操作失败";
		}
	}
}
