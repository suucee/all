package com.maxivetech.backoffice.service.admin.impl;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

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
import com.maxivetech.backoffice.service.admin.AdminReportService;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperDate;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class AdminReportServiceImpl implements AdminReportService {
	@Autowired
	private DepositDao depositDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserBalanceDao userbalanceDao;
	@Autowired
	private ReportDao reportDao;

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer,
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.RiskManagementCommissioner, Role.CustomerServiceStaff})
	@Override
	public Page<HashMap<String, Object>> getBalancePage(int pageNo,
			int pageSize, String urlFormat, HttpSession session) {

		String hql = "select ub.user.id as uid,ub.user.email as email,ub.id as id,ub.amountAvailable as amountAvailable ,ub.amountFrozen as amountFrozen,ub.updatedTime as updatedTime  from UserBalances ub "
				+ "  order by (ub.amountAvailable+ub.amountFrozen) desc";
		List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) userbalanceDao
				.getSession().createQuery(hql)
				.setFirstResult((pageNo - 1) * pageSize)
				.setMaxResults(pageSize)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		String usercounts = "select count(*) from Users";
		int totalRows = reportDao.counts(usercounts);
		Page<HashMap<String, Object>> page = new Page<HashMap<String, Object>>(
				totalRows, pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		return page;
	}

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer,
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.RiskManagementCommissioner, Role.CustomerServiceStaff})
	@Override
	public Page<HashMap<String, Object>> getBalanceLogPage(int pageNo,
			int pageSize, String urlFormat, String start, HttpSession session) {

		Date startDate = HelperDate.parse(start, "yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String date = sdf.format(startDate);

		StringBuffer sqlWhere = new StringBuffer("");

		if (startDate != null) {
			sqlWhere.append("where creatTime <='" + date + "'");
		}
		String hql = "select ubl.user.id as uid,ubl.user.email as email,ubl.id as id,ubl.amountAvailable as amountAvailable ,ubl.amountFrozen as amountFrozen,MAX(ubl.creatTime) as creatTime  from UserBalanceLogs ubl  "
				+ sqlWhere
				+ "  group by ubl.user order by (ubl.amountAvailable+ubl.amountFrozen) desc";
		List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) userbalanceDao
				.getSession().createQuery(hql)
				.setFirstResult((pageNo - 1) * pageSize)
				.setMaxResults(pageSize)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		String usercounts = "select count(distinct ubl.user) from  UserBalanceLogs ubl "
				+ sqlWhere;
		int totalRows = reportDao.counts(usercounts);
		System.out.println(totalRows);
		Page<HashMap<String, Object>> page = new Page<HashMap<String, Object>>(
				totalRows, pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		return page;
	}

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer,
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.RiskManagementCommissioner, Role.CustomerServiceStaff})
	@Override
	public Page<HashMap<String, Object>> getDepositPage(int pageNo,
			int pageSize, String urlFormat, String start, String end,
			HttpSession session) {

		String hql = "select d.user.id as uid,d.user.email as email, MAX(d.paymentTime) as lastTime,count(d.user) as depositcounts,SUM(d.amount)  as amountSum from Deposits d where d.state='DEPOSITED' group by d.user order by SUM(d.amount) desc";
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

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer,
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.RiskManagementCommissioner, Role.CustomerServiceStaff})
	@Override
	public Page<HashMap<String, Object>> getWithdrawalPage(int pageNo,
			int pageSize, String urlFormat, String start, String end,
			HttpSession session) {

		String hql = "select w.user.id as uid,w.user.email as email, MAX(w.auditedTime) as lastTime,count(w.user) as withdrawalcounts, SUM(w.amount)  as amountSum from Withdrawals  w where w.state='REMITTED' group by w.user order by SUM(w.amount) desc";
		List<HashMap<String, Object>> list = depositDao.getSession()
				.createQuery(hql).setMaxResults(pageSize)
				.setFirstResult((pageNo - 1) * pageSize)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		String WithdrawalHql = "select count(distinct w.user) from Withdrawals w where w.state='REMITTED'";
		int totalRows = reportDao.counts(WithdrawalHql);
		Page<HashMap<String, Object>> page = new Page<HashMap<String, Object>>(
				totalRows, pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		return page;
	}

	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer,
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.RiskManagementCommissioner, Role.CustomerServiceStaff})
	@Override
	public Page<HashMap<String, Object>> getRebatePage(int pageNo,
			int pageSize, String urlFormat, String startYear, String startMonth,
			HttpSession session) {

		StringBuffer sqlWhere = new StringBuffer("");
		if(!startYear.equals("0") && startMonth.equals("0")){
			String startYY=startYear+"-1-1 00:00:00";
			String endYY=String.valueOf(Integer.parseInt(startYear)+1)+"-1-1 00:00:00";
			sqlWhere.append(" and '"+startYY+"' <= r.creat_time and r.creat_time < '"+endYY+"'");
		}
		if(!startMonth.equals("0")&&!startYear.equals("0")){
			String startYm=startYear+"-"+startMonth+"-1 00:00:00";
			String endYm=startYear+"-"+String.valueOf(Integer.parseInt(startMonth)+1)+"-1 00:00:00";
			sqlWhere.append(" and '"+startYm+"' <= r.creat_time and r.creat_time < '"+endYm+"'");
		}
		
			String sql = "select u.id as uid," + "u.email as email,"
					+ " u.mobile as mobile," + "r.amount as amount,"
					+ " r.creat_time  as creatTime,r.comment as comment"
					+ " from rebate_records  r  "
					+ " left join users u on r.user_id=u.id"
					+ " where r.user_id is not null "
					+ sqlWhere
					+ " order by amount desc";
			List<HashMap<String, Object>> list = depositDao.getSession()
					.createSQLQuery(sql)
					.addScalar("uid",IntegerType.INSTANCE)
					.addScalar("email",StringType.INSTANCE)
					.addScalar("mobile",StringType.INSTANCE)
					.addScalar("amount",DoubleType.INSTANCE)
					.addScalar("creatTime",TimestampType.INSTANCE)
					.addScalar("comment",StringType.INSTANCE)
					.setMaxResults(pageSize)
					.setFirstResult((pageNo - 1) * pageSize)
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			String countSQL = "select count(*) from rebate_records r"
					+ " where r.user_id is not null "
					+ sqlWhere;
			//返回的类型是BigInteger
			BigInteger bigVal = (BigInteger) depositDao.getSession()
					.createSQLQuery(countSQL).uniqueResult();
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

}
