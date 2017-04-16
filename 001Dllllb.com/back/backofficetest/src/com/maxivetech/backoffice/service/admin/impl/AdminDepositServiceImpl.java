package com.maxivetech.backoffice.service.admin.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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
import com.maxivetech.backoffice.dao.NotifyDao;
import com.maxivetech.backoffice.dao.SettingDao;
import com.maxivetech.backoffice.dao.UserBalanceDao;
import com.maxivetech.backoffice.dao.UserBalanceLogDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Deposits;
import com.maxivetech.backoffice.entity.Notify;
import com.maxivetech.backoffice.entity.UserBalances;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.entity.Withdrawals;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.pojo.PojoSum;
import com.maxivetech.backoffice.sendmail.PostmanDeposit;
import com.maxivetech.backoffice.service.admin.AdminDepositService;
import com.maxivetech.backoffice.service.user.impl.SessionServiceImpl;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperDate;
import com.maxivetech.backoffice.util.HelperMoney;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class AdminDepositServiceImpl implements AdminDepositService {
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
	@Autowired
	private NotifyDao notifyDao;

	@CheckRole(role = {Role.OperationsManager, Role.FinancialStaff})
	@Override
	public String add(String mobile, String orderNum,double amount,String payDate,String userComment,String operationPassword,HttpSession session)throws ForbiddenException{
		
		//验证操作密码
		Admins admin = (Admins) adminDao.getById(SessionServiceImpl.getCurrent(session).getId());
		
		String md5Password = DigestUtils.md5DigestAsHex((operationPassword + admin.getSalty()).getBytes());
		if(!admin.getOperationPassword().equals(md5Password)){
			return "密码错误";
		}
		
		//验证用户是否存在
		Users user = userDao.findByMobile(mobile);
		if (user == null){
			return "该用户不存在";
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
			String scheme, String start, String end, HttpSession session) {

		//WHERE
		ArrayList<Criterion> wheres = new ArrayList<Criterion>();
		if(userId!=0){
			Users users= (Users) userDao.getById(userId);
			wheres.add(Restrictions.eq("user", users));
		}
		switch (scheme) {
		case "pending_pay":
			wheres.add(Restrictions.eq("state", "PENDING_PAY"));
			break;
		case "deposited":
			wheres.add(Restrictions.eq("state", "DEPOSITED"));
			break;
		case "pending_audit":
			wheres.add(Restrictions.eq("state", "PENDING_AUDIT"));
			break;
		case "accepted":
			wheres.add(Restrictions.eq("state", "ACCEPTED"));
			break;
		case "pending_supervisor":
			wheres.add(Restrictions.eq("state", "PENDING_SUPERVISOR"));
			break;
		case "agent":
			wheres.add(Restrictions.like("orderNum", "[D]"+BackOffice.getInst().COMPANY_NAME+"%"));
			break;
		case "all":
		default:
		}
		
		Date startDate = HelperDate.parse(start, "yyyy-MM-dd HH:mm:ss");
		Date endDate = HelperDate.parse(end, "yyyy-MM-dd HH:mm:ss");
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
	public List<PojoSum> getSummarizes(HttpSession session) {

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

			// 清除
			d.setUser(null);

			return d;
		}
		return null;
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
			if(state.equals("DEPOSITED")){
				//插入通知
				String content="你的金额为"+HelperMoney.formatMoney(deposit.getAmount())+"的入金已经成功！";
				notifyDao.insertNotify(content, null, deposit.getUser(),id,Notify.getDeposittype());
			}
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
			userBalanceLogDao.addLog(ub, amountUSD, deposit.getId(), 0, 0, "[D]" + deposit.getUserComment());
			
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