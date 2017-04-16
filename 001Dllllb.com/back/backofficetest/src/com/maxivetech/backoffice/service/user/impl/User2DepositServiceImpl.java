package com.maxivetech.backoffice.service.user.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.DepositDao;
import com.maxivetech.backoffice.dao.SettingDao;
import com.maxivetech.backoffice.dao.UserBalanceDao;
import com.maxivetech.backoffice.dao.UserBalanceLogDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.entity.Deposits;
import com.maxivetech.backoffice.entity.UserBalances;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.sendmail.PostmanDeposit;
import com.maxivetech.backoffice.service.user.User2DepositService;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperPassword;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class User2DepositServiceImpl implements User2DepositService {
	@Autowired
	private DepositDao depositDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserBalanceLogDao userBalanceLogDao;
	@Autowired
	private UserBalanceDao userBalanceDao;
	@Autowired
	private SettingDao settingDao;
	
	@CheckRole(role = {Role.User})
	@Override
	public int add(/*String orderNum, */double amount,HttpSession session)throws ForbiddenException{

		Users user= (Users) userDao.getById(SessionServiceImpl.getCurrent(session).getId());

		//最小检查
		{
			double minAmount = settingDao.getDouble("MinDepositAmountUSD");
			if (amount < minAmount) {
				throw new RuntimeException("最小入金金额为" + minAmount + " USD");
			}
		}
		//最大检查
		{
			double maxAmount = settingDao.getDouble("MaxDepositAmountUSD");
			if (amount > maxAmount) {
				throw new RuntimeException("最大入金金额为" + maxAmount + " USD");
			}
		}
		
		Deposits deposits = new Deposits();

		Date date = new Date();
	    /*//时间以yyyyMMDDHHmmss的方式表示 
	    SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMddHHmmss");
	    String TradeId=new String(formatter.format(date));*/
		deposits.setAuditedMemo("");
		deposits.setState("PENDING_PAY");
		deposits.setCurrency("USD");
		deposits.setAuditedTime(null);
		deposits.setPaymentTime(null);
		deposits.setOrderNum("");
		deposits.setCreatTime(date);
		deposits.setUser(user);
		deposits.setAmount(amount);
		

		depositDao.save(deposits);

		depositDao.commit();

		return deposits.getId();
	}

	
	@CheckRole(role = {Role.User})
	@Override
	public int addWithMT4(String toLogin, double amount,String paymentPassword, HttpSession session)throws ForbiddenException{

		Users user= (Users) userDao.getById(SessionServiceImpl.getCurrent(session).getId());
		
		if(!toLogin.equals("0")){//->mt4
			if(!HelperPassword.verifyPaymentPassword(paymentPassword, user)){
				throw new RuntimeException("对不起，您输入的支付密码有误，暂不能转入MT4账户，请尝试转入网页账户？");
			}
		}
		
		//最小检查
		{
			double minAmount = settingDao.getDouble("MinDepositAmountUSD");
			if (amount < minAmount) {
				throw new RuntimeException("最小入金金额为" + minAmount + " USD");
			}
		}
		//最大检查
		{
			double maxAmount = settingDao.getDouble("MaxDepositAmountUSD");
			if (amount > maxAmount) {
				throw new RuntimeException("最大入金金额为" + maxAmount + " USD");
			}
		}
		
		Deposits deposits = new Deposits();

		Date date = new Date();
	    /*//时间以yyyyMMDDHHmmss的方式表示 
	    SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMddHHmmss");
	    String TradeId=new String(formatter.format(date));*/
		deposits.setAuditedMemo("");
		deposits.setState("PENDING_PAY");
		deposits.setCurrency("USD");
		deposits.setAuditedTime(null);
		deposits.setPaymentTime(null);
		deposits.setOrderNum("");
		deposits.setCreatTime(date);
		deposits.setUser(user);
		deposits.setAmount(amount);
		
		if(!toLogin.equals("0")){//->mt4，在ok。do判断并且转账
			deposits.setUserComment("[TOMT4="+toLogin+"]");
		}

		depositDao.save(deposits);

		depositDao.commit();

		return deposits.getId();
	}
	
	
	@CheckRole(role = {Role.User})
	@Override
	public Page<Deposits> getPage(int pageNo, int pageSize, String urlFormat,
			HttpSession session)throws ForbiddenException {

		Users user= (Users) userDao.getById(SessionServiceImpl.getCurrent(session).getId());
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

	
	@CheckRole(role = {Role.User})
	@Override
	public Page<Deposits> getPageByTimeAndState(int pageNo, int pageSize,
			String urlFormat, String startDate, String endDate, String state,HttpSession session) {

		// 登录检查
		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		if (user == null) {
			return null;
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //格式化时间
	    Date sDate = null,eDate = null;
		ArrayList<Criterion> wheres = new ArrayList<Criterion>();
		wheres.add(Restrictions.eq("user", user));
		 //格式化startDate 开始时间为 Date类型
		java.sql.Timestamp staDate = null; 
		java.sql.Timestamp enDate = null;
		
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
		
		if(state.length()>0){
			wheres.add(Restrictions.eq("state", state));
		}
		
		if(staDate == null && enDate == null){//不限
		}
		else if(staDate != null && enDate == null){//有开始限制
			wheres.add(Restrictions.or(
					Restrictions.ge("creatTime", staDate),
					Restrictions.ge("auditedTime", staDate),
					Restrictions.ge("paymentTime", staDate)));
		}
		else if(staDate == null && enDate != null){//有结束限制
			wheres.add(Restrictions.or(
					Restrictions.le("creatTime", enDate),
					Restrictions.le("auditedTime", enDate),
					Restrictions.le("paymentTime", enDate)));
		}
		else if(staDate != null && enDate != null){//两端都有限制
			wheres.add(Restrictions.or(
					Restrictions.between("creatTime", staDate, enDate),
					Restrictions.between("auditedTime", staDate, enDate),
					Restrictions.between("paymentTime", staDate, enDate)));
		}

		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("creatTime"));

		Projection projs = null;

		Page<Deposits> page = (Page<Deposits>) depositDao.getPage(
				wheres, orders, projs, pageSize, pageNo, null);
		page.generateButtons(urlFormat);
		// 清除
		for (Deposits item : page.getList()) {
			item.setUser(null);
		}

		return page;
	}
	

	@CheckRole(role = {Role.User})
	@Override
	public int updatePayState(int id, String orderNum,double amount, HttpSession session)
			throws ForbiddenException {

		int uId=SessionServiceImpl.getCurrent(session).getId();
		Users user=(Users) userDao.getById(uId);
		
		Deposits deposit=(Deposits) depositDao.getById(id);
		
		if(deposit!=null){
			if (user.getId()==deposit.getUser().getId()
				&& !deposit.getState().equals("DEPOSITED")
				&& amount==deposit.getAmount()){
			
				deposit.setState("DEPOSITED");
				deposit.setPaymentTime(new Date());
				deposit.setOrderNum(orderNum);
				depositDao.saveOrUpdate(deposit);
				userBalanceDao.increaseAmountAvailable(deposit.getUser(), "USD", deposit.getAmount());
				UserBalances  userBalance=userBalanceDao.getBalance(deposit.getUser(), "USD");
				
				userBalanceLogDao.addLog(userBalance, deposit.getAmount(),deposit.getId(),0,0, "用户入金");
				depositDao.commit();
				//发邮件
				{
					PostmanDeposit postman = new PostmanDeposit(user.getEmail());
					postman.deposited(deposit, userBalance);
				}
				
				return deposit.getId();
			}
		}
		return 0;
	}

	@CheckRole(role = {Role.User})
	@Override
	public double getRate(HttpSession session) {
		return settingDao.getDouble("DepositRateCNY");
	}
}
