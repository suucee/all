package com.maxivetech.backoffice.service.user.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.BackOffice;
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
import com.maxivetech.backoffice.service.user.UserDepositService;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class UserDepositServiceImpl implements UserDepositService {
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
	public Page<Deposits> getPage(int pageNo, int pageSize, String urlFormat,
			HttpSession session)throws ForbiddenException {

		Users user= (Users) userDao.getById(SessionServiceImpl.getCurrent(session).getId());
		ArrayList<Criterion> wheres=new ArrayList<>();
		wheres.add(Restrictions.eq("user", user));
		wheres.add(Restrictions.ne("state", "PENDING_PAY"));	//待付款不显示
		
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
