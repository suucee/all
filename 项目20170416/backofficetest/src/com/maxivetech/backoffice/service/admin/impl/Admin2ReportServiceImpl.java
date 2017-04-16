package com.maxivetech.backoffice.service.admin.impl;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.DepositDao;
import com.maxivetech.backoffice.dao.ReportDao;
import com.maxivetech.backoffice.dao.UserBalanceDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.service.admin.Admin2ReportService;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class Admin2ReportServiceImpl implements Admin2ReportService {
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

		String hql = "select ub.user_id as uid,u.email as email,ub.id as id, up.name as name, "
		        +"   ub.amount_available as amountAvailable ,ub.amount_frozen as amountFrozen,"
				+"   ub.updated_time as updatedTime  from user__balances ub "
				+" left join users u  on u.id=ub.user_id"
				+" left join user__profiles up  on u.id=up.user_id"
				+"   order by (ub.amount_available+ub.amount_frozen) desc";
		List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) userbalanceDao
				.getSession().createSQLQuery(hql)
				.addScalar("uid", IntegerType.INSTANCE)
				.addScalar("email", StringType.INSTANCE)
				.addScalar("id", IntegerType.INSTANCE)
				.addScalar("updatedTime", DateType.INSTANCE)
				.addScalar("name", StringType.INSTANCE)
				.addScalar("amountAvailable", DoubleType.INSTANCE)
				.addScalar("amountFrozen", DoubleType.INSTANCE)
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
	public Page<HashMap<String, Object>> getDepositPage(int pageNo,
			int pageSize, String urlFormat, String start, String end,
			HttpSession session) {

		String sql = "select d.user_id as uid,u.email as email,up.name as name, MAX(d.payment_time) as lastTime,"
		        +" count(d.user_id) as depositcounts,SUM(d.amount)  as amountSum from deposits d"
				+" left join users u  on u.id=d.user_id"
				+" left join user__profiles up  on d.user_id=up.user_id"
				+" where d.state='DEPOSITED' group by d.user_id order by SUM(d.amount) desc";
		List<HashMap<String, Object>> list = depositDao.getSession()
				.createSQLQuery(sql)
				.addScalar("amountSum", DoubleType.INSTANCE)
				.addScalar("uid", IntegerType.INSTANCE)
				.addScalar("email", StringType.INSTANCE)
				.addScalar("name", StringType.INSTANCE)
				.addScalar("lastTime", DateType.INSTANCE)
				.addScalar("depositcounts", IntegerType.INSTANCE)
				.setMaxResults(pageSize)
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

		String sql = " select w.user_id as uid,u.email as email, MAX(w.audited_time) as lastTime,up.name as name, "
		        +" count(w.user_id) as withdrawalcounts, SUM(w.amount)  as amountSum from withdrawals  w "
				+" left join users u  on u.id=w.user_id"
				+" left join user__profiles up  on u.id=up.user_id"
				+" where w.state='REMITTED' group by w.user_id order by SUM(w.amount) desc";
		List<HashMap<String, Object>> list = depositDao.getSession()
				.createSQLQuery(sql)
				.addScalar("amountSum", DoubleType.INSTANCE)
				.addScalar("uid", IntegerType.INSTANCE)
				.addScalar("email", StringType.INSTANCE)
				.addScalar("lastTime", DateType.INSTANCE)
				.addScalar("name", StringType.INSTANCE)
				.addScalar("withdrawalcounts", IntegerType.INSTANCE)
				.setMaxResults(pageSize)
				.setFirstResult((pageNo - 1) * pageSize)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		String WithdrawalHql = "select count(distinct w.user) from Withdrawals w where w.state='REMITTED'";
		int totalRows = reportDao.counts(WithdrawalHql);
		Page<HashMap<String, Object>> page = new Page<HashMap<String, Object>>(
				totalRows, pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		return page;
	}

	

}
