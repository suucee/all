package com.maxivetech.backoffice.service.admin.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.pojo.PojoWebItem;
import com.maxivetech.backoffice.service.admin.Admin2IndexService;
import com.maxivetech.backoffice.util.HelperAuthority;

@Service
@Transactional
public class Admin2IndexServiceImpl implements Admin2IndexService {
	@Autowired
	private UserDao userDao;

	@Override
	public List<PojoWebItem> getWebItem(HttpSession session) {
		List<PojoWebItem> list = new ArrayList<PojoWebItem>();
		{
			PojoWebItem pojoWebItem = new PojoWebItem();
			String sql = "select sum(amount) from deposits where state='DEPOSITED'";
			String count = String.valueOf(userDao.getSession().createSQLQuery(sql).uniqueResult());
			if(count.equals("null"))count="0";
			pojoWebItem.setContent("累计入金（USD）： " + count);
			pojoWebItem.setCountTime(new Date());
			pojoWebItem.setLink("");
			list.add(pojoWebItem);
		}
		{
			PojoWebItem pojoWebItem = new PojoWebItem();
			String sql = "select sum(amount) from withdrawals where state='REMITTED'";
			String count = String.valueOf(userDao.getSession().createSQLQuery(sql).uniqueResult());
			if(count.equals("null"))count="0";
			pojoWebItem.setContent("累计出金（USD）： " + count);
			pojoWebItem.setCountTime(new Date());
			pojoWebItem.setLink("");
			list.add(pojoWebItem);
		}
		{
			PojoWebItem pojoWebItem = new PojoWebItem();
			String sql = "select count(*) from users";
			String count = String.valueOf(userDao.getSession().createSQLQuery(sql).uniqueResult());
			if(count.equals("null"))count="0";
			pojoWebItem.setContent("网站当前注册用户" + count + "人！");
			pojoWebItem.setCountTime(new Date());
			pojoWebItem.setLink("./../admin/user_list.html");
			list.add(pojoWebItem);
		}
		{
			PojoWebItem pojoWebItem = new PojoWebItem();
			String sql = "select count(*) from users where state='VERIFIED'";
			String count = String.valueOf(userDao.getSession().createSQLQuery(sql).uniqueResult());
			if(count.equals("null"))count="0";
			pojoWebItem.setContent("已经有" + count + "人通过审核！");
			pojoWebItem.setCountTime(new Date());
			pojoWebItem.setLink("./../admin/user_list.html?scheme=verified");
			list.add(pojoWebItem);
		}
		if (HelperAuthority.isComplianceOfficer(session) || HelperAuthority.isOperationsManager(session)) {
			{
				PojoWebItem pojoWebItem = new PojoWebItem();
				String hql = "select count(*) from Withdrawals where state='WAITING'";
				String count = String.valueOf(userDao.getSession().createQuery(hql).uniqueResult());
				if(!count.equals("0")){
					pojoWebItem.setContent("当前有" + count + "条出金申请等待审核！");
					pojoWebItem.setCountTime(new Date());
					pojoWebItem.setLink("./../admin/compliance_index_withdrawal.html");
					list.add(pojoWebItem);
				}
			}
			{
				PojoWebItem pojoWebItem = new PojoWebItem();
				String hql = "select count(*) from Users where state='AUDITING'";
				String count = String.valueOf(userDao.getSession().createQuery(hql).uniqueResult());
				if(!count.equals("0")){
				pojoWebItem.setContent("当前有" + count + "条用户资料等待审核！");
				pojoWebItem.setCountTime(new Date());
				pojoWebItem.setLink("./../admin/compliance_index.html");
				list.add(pojoWebItem);
				}
			}
			{
				PojoWebItem pojoWebItem = new PojoWebItem();
				String hql = "select count(*) from UserBankAccounts uba where uba.state='WAITING'";
				String count = String.valueOf(userDao.getSession().createQuery(hql).uniqueResult());
				if(!count.equals("0")){
				pojoWebItem.setContent("当前有" + count + "张出金银行卡等待审核！");
				pojoWebItem.setCountTime(new Date());
				pojoWebItem.setLink("./../admin/compliance_index_bank.html");
				list.add(pojoWebItem);
				}
			}
		}
		if (HelperAuthority.isFinancialStaff(session) || HelperAuthority.isFinancialSuperior(session)
				|| HelperAuthority.isOperationsManager(session)) {
			{
				PojoWebItem pojoWebItem = new PojoWebItem();
				String hql = "select count(*) from Withdrawals where state='AUDITED'";
				String count = String.valueOf(userDao.getSession().createQuery(hql).uniqueResult());
				if(!count.equals("0")){
				pojoWebItem.setContent("当前有" + count + "条出金申请等待财务汇款确认！");
				pojoWebItem.setCountTime(new Date());
				pojoWebItem.setLink("./../admin/finance_index.html");
				list.add(pojoWebItem);
				}
			}
		}
		if (HelperAuthority.isFinancialSuperior(session) || HelperAuthority.isOperationsManager(session)) {
			{
				PojoWebItem pojoWebItem = new PojoWebItem();
				String hql = "select count(*) from Withdrawals where state='PENDING_SUPERVISOR'";
				String count = String.valueOf(userDao.getSession().createQuery(hql).uniqueResult());
				if(!count.equals("0")){
				pojoWebItem.setContent("当前有" + count + "条大额出金申请等待财务主管审核！");
				pojoWebItem.setCountTime(new Date());
				pojoWebItem.setLink("./../admin/finance_index.html");
				list.add(pojoWebItem);
				}
			}
		}
		//代为入金通知
		if (HelperAuthority.isFinancialSuperior(session)|| HelperAuthority.isOperationsManager(session)) {
			{
				PojoWebItem pojoWebItem = new PojoWebItem();
				String hql = "select count(*) from Deposits where state='PENDING_SUPERVISOR'";
				String count = String.valueOf(userDao.getSession().createQuery(hql).uniqueResult());
				if(!count.equals("0")){
				pojoWebItem.setContent("当前有" + count + "条代为入金等待审核！");
				pojoWebItem.setCountTime(new Date());
				pojoWebItem.setLink("./../admin/finance_deposit.html");
				list.add(pojoWebItem);
				}
			}
		}
		return list;
	}

}
