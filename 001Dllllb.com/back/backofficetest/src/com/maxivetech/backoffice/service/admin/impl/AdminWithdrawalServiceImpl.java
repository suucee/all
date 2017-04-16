package com.maxivetech.backoffice.service.admin.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.AdminDao;
import com.maxivetech.backoffice.dao.NotifyDao;
import com.maxivetech.backoffice.dao.UserBalanceDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.dao.WithdrawalDao;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Notify;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.entity.Withdrawals;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.pojo.PojoSum;
import com.maxivetech.backoffice.sendmail.PostmanWithdrawal;
import com.maxivetech.backoffice.service.admin.AdminWithdrawalService;
import com.maxivetech.backoffice.service.user.impl.SessionServiceImpl;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperDate;
import com.maxivetech.backoffice.util.HelperMoney;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class AdminWithdrawalServiceImpl implements AdminWithdrawalService {
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
	@Autowired
	private NotifyDao notifyDao;


	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer,
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.RiskManagementCommissioner, Role.CustomerServiceStaff})
	@Override
	public Page<Withdrawals> getPage(int userId,int pageNo, int pageSize, String urlFormat,
			String scheme, String start, String end, HttpSession session) {

		//WHERE
		ArrayList<Criterion> wheres = new ArrayList<Criterion>();
		if(userId!=0){
			Users users= (Users) userDao.getById(userId);
			wheres.add(Restrictions.eq("user", users));
		}
		switch (scheme) {
		case "pending":
			wheres.add(Restrictions.eq("state", "WAITING"));
			break;
		case "audited":
			wheres.add(Restrictions.eq("state", "AUDITED"));
			break;
		case "pending_supervisor":
			wheres.add(Restrictions.eq("state", "PENDING_SUPERVISOR"));
			break;
		case "rejected":
			wheres.add(Restrictions.eq("state", "REJECTED"));
			break;
		case "remitted":
			wheres.add(Restrictions.eq("state", "REMITTED"));
			break;
		case "returned":
			wheres.add(Restrictions.eq("state", "BACK"));
			break;
		case "canceled":
			wheres.add(Restrictions.eq("state", "CANCELED"));
			break;
		case "all":
		default:
		}
		
		Date startDate = HelperDate.parse(start, "yyyy-MM-dd");
		Date endDate = HelperDate.parse(end, "yyyy-MM-dd");
		if (startDate != null) {
			wheres.add(Restrictions.ge("creatTime", startDate));
		}
		if (endDate != null) {
			endDate.setTime(endDate.getTime() + 24 * 60 * 60 * 1000);
			wheres.add(Restrictions.lt("creatTime", endDate));
		}
		
		//ORDER BY
		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("creatTime"));
		
		//PAGE
		Page<Withdrawals> page = (Page<Withdrawals>) withdrawalDao.getPage(
				wheres, orders, null, pageSize, pageNo, null);
		page.generateButtons(urlFormat);
		
		//清理
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
	public List<PojoSum> getSummarizes(HttpSession session) {

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
		case "PENDING_SUPERVISOR"://只有大额出金、已汇款、汇款拒绝三种
			withdrawal.setState(state);
			withdrawal.setAuditedTime(new Date());
			withdrawal.setAuditedMemo(memo);
			withdrawalDao.saveOrUpdate(withdrawal);
			String content = "";
			if(state.equals("REMITTED")){//已汇款
				//插入通知
				content="你的金额为"+HelperMoney.formatMoney(withdrawal.getAmount())+"的出金申请通过审核，财务已经汇款至您的银行卡！";
			}else if(state.equals("REJECTED")){//汇款拒绝
				content="你的金额为"+HelperMoney.formatMoney(withdrawal.getAmount())+"的出金申请被拒绝！";
			}
			notifyDao.insertNotify(content, null, withdrawal.getUser(),id,Notify.getWithdrawaltype());
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
