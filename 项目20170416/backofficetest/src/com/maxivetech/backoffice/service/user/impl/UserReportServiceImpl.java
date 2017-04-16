package com.maxivetech.backoffice.service.user.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.DepositDao;
import com.maxivetech.backoffice.dao.ReportDao;
import com.maxivetech.backoffice.dao.UserBalanceDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.service.user.UserReportService;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperDate;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class UserReportServiceImpl implements UserReportService {
	@Autowired
	private DepositDao depositDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserBalanceDao userbalanceDao;
	@Autowired
	private ReportDao reportDao;

	@CheckRole(role = {Role.User})
	@Override
	public Page<HashMap<String, Object>> getDepositPage(int pageNo,
			int pageSize, String urlFormat, HttpSession session) {

		Users user = (Users) userDao.getById(HelperAuthority.getId(session));
		String hql = "select DATE_FORMAT(creat_time, '%Y-%m') as period ,"
				   +" sum(amount) as amount,count(*) as count "
				   +" from Deposits  where state='DEPOSITED' "
				   +" and user_id="+user.getId()
				   +" GROUP BY period order by period desc";
		List<HashMap<String, Object>> list = depositDao.getSession()
				.createSQLQuery(hql)
				.addScalar("period",StringType.INSTANCE)
				.addScalar("amount",DoubleType.INSTANCE)
				.addScalar("count",IntegerType.INSTANCE)
				.setMaxResults(pageSize)
				.setFirstResult((pageNo - 1) * pageSize)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		String counthql = "select count(distinct DATE_FORMAT(creat_time, '%Y-%m')) from Deposits  where state='DEPOSITED' and user_id="+user.getId();
		//返回的类型是BigInteger
		BigInteger bigVal = (BigInteger) depositDao.getSession()
				.createSQLQuery(counthql).uniqueResult();
		int totalRows=0;
		//BigInteger 转 int
		if(bigVal!=null){
			totalRows = bigVal.intValue();	
		}
		Page<HashMap<String, Object>> page = new Page<HashMap<String, Object>>(
				totalRows, pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		return page;
	}

	@CheckRole(role = {Role.User})
	@Override
	public Page<HashMap<String, Object>> getWithdrawalPage(int pageNo,
			int pageSize, String urlFormat, HttpSession session) {

		Users user = (Users) userDao.getById(HelperAuthority.getId(session));
		String hql = "select DATE_FORMAT(creat_time, '%Y-%m') as period ,"
				   +" sum(amount) as amount,count(*) as count "
				   +" from Withdrawals  where state='REMITTED' "
				   +" and user_id = "+user.getId()
				   +" GROUP BY period order by period desc";
		List<HashMap<String, Object>> list = depositDao.getSession()
				.createSQLQuery(hql)
				.addScalar("period",StringType.INSTANCE)
				.addScalar("amount",DoubleType.INSTANCE)
				.addScalar("count",IntegerType.INSTANCE)
				.setMaxResults(pageSize)
				.setFirstResult((pageNo - 1) * pageSize)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		String counthql = "select count(distinct DATE_FORMAT(creat_time, '%Y-%m')) from Withdrawals  where state='REMITTED' and user_id = "+user.getId();
		//返回的类型是BigInteger
		BigInteger bigVal = (BigInteger) depositDao.getSession()
				.createSQLQuery(counthql).uniqueResult();
		int totalRows=0;
		//BigInteger 转 int
		if(bigVal!=null){
			totalRows = bigVal.intValue();	
		}

		Page<HashMap<String, Object>> page = new Page<HashMap<String, Object>>(
				totalRows, pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		return page;
	}

	@CheckRole(role = {Role.User})
	@Override
	public Page<HashMap<String, Object>> getCustomerDepositPage(int pageNo,
			int pageSize, String urlFormat, String startYear, String startMonth,
			HttpSession session) {

		Users user = (Users) userDao.getById(HelperAuthority.getId(session));
		StringBuilder hqlBuilder = new StringBuilder();
		
		hqlBuilder.append("select d.user.id as uid,d.user.email as email, MAX(d.paymentTime) as lastTime,count(d.user) as depositcounts,SUM(d.amount)  as amountSum from Deposits d ");
		switch (user.getLevel()) {
		case 0:
		case 1:
		case 2:
			hqlBuilder
					.append(" where d.state='DEPOSITED' and instr(d.user.path,'"
							+ user.getPath()
							+ "')=1 and d.user.level>"
							+ user.getLevel());
			break;
		default:
			hqlBuilder
					.append(" where d.state='DEPOSITED' and instr(d.user.path,'"
							+ user.getPath()
							+ "')=1  and d.user.level>"
							+ user.getLevel()
							+ " and d.user.level<="
							+ (user.getLevel() + 2));
			break;
		}
		if (!startYear.equals("0") && startMonth.equals("0")) {
			String startYY = startYear + "-1-1 00:00:00";
			String endYY = String.valueOf(Integer.parseInt(startYear) + 1)
					+ "-1-1 00:00:00";
			hqlBuilder.append(" and '" + startYY
					+ "' <= d.creatTime and d.creatTime < '" + endYY + "'");
		}

		if (!startMonth.equals("0") && !startYear.equals("0")) {
			String startYm = startYear + "-" + startMonth + "-1 00:00:00";
			String endYm = startYear + "-"
					+ String.valueOf(Integer.parseInt(startMonth) + 1)
					+ "-1 00:00:00";
			hqlBuilder.append(" and '" + startYm
					+ "' <= d.creatTime and d.creatTime < '" + endYm + "'");
		}
		hqlBuilder.append(" group by d.user order by SUM(d.amount) desc");
		String hql = hqlBuilder.toString();
		List<HashMap<String, Object>> list = depositDao.getSession()
				.createQuery(hql).setMaxResults(pageSize)
				.setFirstResult((pageNo - 1) * pageSize)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		String depositHql = "select count(distinct d.user) from Deposits d where d.state='DEPOSITED'";
		int totalRows = reportDao.counts(depositHql);
		Page<HashMap<String, Object>> page = new Page<HashMap<String, Object>>(
				totalRows, pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		return page;
	}

	@CheckRole(role = {Role.User})
	@Override
	public Page<HashMap<String, Object>> getCustomerWithdrawalPage(int pageNo,
			int pageSize, String urlFormat, String startYear, String startMonth,
			HttpSession session) {

		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		StringBuffer where = new StringBuffer();
		if (!startYear.equals("0") && startMonth.equals("0")) {
			String startYY = startYear + "-1-1 00:00:00";
			String endYY = String.valueOf(Integer.parseInt(startYear) + 1)
					+ "-1-1 00:00:00";
			where.append(" and '" + startYY
					+ "' <= w.creatTime and w.creatTime < '" + endYY + "'");
		}

		if (!startMonth.equals("0") && !startYear.equals("0")) {
			String startYm = startYear + "-" + startMonth + "-1 00:00:00";
			String endYm = startYear + "-"
					+ String.valueOf(Integer.parseInt(startMonth) + 1)
					+ "-1 00:00:00";
			where.append(" and '" + startYm
					+ "' <= w.creatTime and w.creatTime < '" + endYm + "'");
		}

		if (user.getLevel() == 1 || user.getLevel() == 2
				|| user.getLevel() == 3) {
			where.append("and instr(w.user.path,'" + user.getPath()
					+ "')=1 and w.user.level > " + user.getLevel());
		} else {
			where.append("and instr(w.user.path,'" + user.getPath()
					+ "')=1 and w.user.level > " + user.getLevel()
					+ " and w.user.level <= " + user.getLevel() + 2);

		}
		String hql = "select w.user.id as uid,w.user.email as email, MAX(w.auditedTime) as lastTime,count(w.user) as withdrawalcounts, SUM(w.amount)  as amountSum from Withdrawals  w where w.state='REMITTED' "
				+ where + " group by w.user order by SUM(w.amount) desc";
		List<HashMap<String, Object>> list = depositDao.getSession()
				.createQuery(hql).setMaxResults(pageSize)
				.setFirstResult((pageNo - 1) * pageSize)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		String WithdrawalHql = "select count(distinct w.user) from Withdrawals w where w.state='REMITTED' "
				+ where;
		int totalRows = reportDao.counts(WithdrawalHql);
		Page<HashMap<String, Object>> page = new Page<HashMap<String, Object>>(
				totalRows, pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		return page;
	}

	@CheckRole(role = {Role.User})
	@Override
	public Page<HashMap<String, Object>> getRebatePage(int pageNo,
			int pageSize, String urlFormat, String startYear,
			String startMonth, HttpSession session) {

		// 获取到当前登录的用户ID
		int userId = SessionServiceImpl.getCurrent(session).getId();

		StringBuffer sqlWhere = new StringBuffer("");
		if (!startYear.equals("0") && startMonth.equals("0")) {
			String startYY = startYear + "-1-1 00:00:00";
			String endYY = String.valueOf(Integer.parseInt(startYear) + 1)
					+ "-1-1 00:00:00";
			sqlWhere.append(" and '" + startYY
					+ "' <= r.creat_time and r.creat_time < '" + endYY + "'");
		}

		if (!startMonth.equals("0") && !startYear.equals("0")) {
			String startYm = startYear + "-" + startMonth + "-1 00:00:00";
			String endYm = startYear + "-"
					+ String.valueOf(Integer.parseInt(startMonth) + 1)
					+ "-1 00:00:00";
			sqlWhere.append(" and '" + startYm
					+ "' <= r.creat_time and r.creat_time < '" + endYm + "'");
		}

		String sql = "select u.id as uid," + "u.email as email,"
				+ " u.mobile as mobile," + "r.amount as amount,"
				+ " r.creat_time  as creatTime,r.comment as comment"
				+ " from rebate_records  r  "
				+ " left join users u on r.user_id=u.id"
				+ " where r.user_id = " + userId + sqlWhere
				+ " order by r.creat_time desc";
		List<HashMap<String, Object>> list = depositDao.getSession()
				.createSQLQuery(sql).addScalar("uid", IntegerType.INSTANCE)
				.addScalar("email", StringType.INSTANCE)
				.addScalar("mobile", StringType.INSTANCE)
				.addScalar("amount", DoubleType.INSTANCE)
				.addScalar("creatTime", TimestampType.INSTANCE)
				.addScalar("comment", StringType.INSTANCE)
				.setMaxResults(pageSize)
				.setFirstResult((pageNo - 1) * pageSize)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		String countSQL = "select count(*) from rebate_records r"
				+ " where r.user_id = " + userId + sqlWhere;
		// 返回的类型是BigInteger
		BigInteger bigVal = (BigInteger) depositDao.getSession()
				.createSQLQuery(countSQL).uniqueResult();
		int totalRows = 0;
		// BigInteger 转 int
		if (bigVal != null) {
			totalRows = bigVal.intValue();
		}
		Page<HashMap<String, Object>> page = new Page<HashMap<String, Object>>(
				totalRows, pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		return page;

	}

	@CheckRole(role = {Role.User})
	@Override
	public double getRebateSum(String startYear, String startMonth,
			HttpSession session) {

		// 获取到当前登录的用户ID
		int userId = SessionServiceImpl.getCurrent(session).getId();

		StringBuffer sqlWhere = new StringBuffer("");
		if (!startYear.equals("0") && startMonth.equals("0")) {
			String startYY = startYear + "-1-1 00:00:00";
			String endYY = String.valueOf(Integer.parseInt(startYear) + 1)
					+ "-1-1 00:00:00";
			sqlWhere.append(" and '" + startYY
					+ "' <= r.creat_time and r.creat_time < '" + endYY + "'");
		}

		if (!startMonth.equals("0") && !startYear.equals("0")) {
			String startYm = startYear + "-" + startMonth + "-1 00:00:00";
			String endYm = startYear + "-"
					+ String.valueOf(Integer.parseInt(startMonth) + 1)
					+ "-1 00:00:00";
			sqlWhere.append(" and '" + startYm
					+ "' <= r.creat_time and r.creat_time < '" + endYm + "'");
		}

		String sql = "select sum(r.amount) as amountSum"
				+ " from rebate_records  r  "
				+ " left join users u on r.user_id=u.id"
				+ " where r.user_id = " + userId + sqlWhere;
		Object obj = depositDao.getSession().createSQLQuery(sql)
				.addScalar("amountSum", DoubleType.INSTANCE).uniqueResult();
		double amountSum = 0.00;
		if (obj != null) {
			amountSum = (double) obj;
		}
		return amountSum;

	}

	@CheckRole(role = {Role.User})
	@Override
	public double getDepositSum(HttpSession session) {

		// 获取到当前登录的用户ID
		int userId = SessionServiceImpl.getCurrent(session).getId();
		
		String sql = "select sum(d.amount) as amountSum"
				+ " from deposits d where d.state='DEPOSITED' "
				+ " and d.user_id = " + userId;
		Object obj = depositDao.getSession().createSQLQuery(sql)
				.addScalar("amountSum", DoubleType.INSTANCE).uniqueResult();
		double amountSum = 0.00;
		if (obj != null) {
			amountSum = (double) obj;
		}
		return amountSum;
		
	}

	@CheckRole(role = {Role.User})
	@Override
	public double getWithdrawalSum(HttpSession session) {

		// 获取到当前登录的用户ID
		int userId = SessionServiceImpl.getCurrent(session).getId();
		
		String sql = "select sum(w.amount) as amountSum"
				+ " from Withdrawals w where w.state='REMITTED' "
				+ " and w.user_id = " + userId;
		Object obj = depositDao.getSession().createSQLQuery(sql)
				.addScalar("amountSum", DoubleType.INSTANCE).uniqueResult();
		double amountSum = 0.00;
		if (obj != null) {
			amountSum = (double) obj;
		}
		return amountSum;
		
	}

	@Override
	public HashMap<String, Object> getUserOrder(int pageNo, int pageSize, String urlFormat, String start, String end,
			HttpSession session) {
		// TODO Auto-generated method stub
		return null;
	}

}
