package com.maxivetech.backoffice.service.admin.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.RebateDao;
import com.maxivetech.backoffice.dao.RebateRecordDao;
import com.maxivetech.backoffice.dao.UserBalanceDao;
import com.maxivetech.backoffice.dao.UserBalanceLogDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.entity.Rebates;
import com.maxivetech.backoffice.entity.RebateRecords;
import com.maxivetech.backoffice.entity.UserBalances;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoRebateReportUsers;
import com.maxivetech.backoffice.pojo.PojoRebates;
import com.maxivetech.backoffice.pojo.PojoUserAndMonth;
import com.maxivetech.backoffice.pojo.PojoUserBalances;
import com.maxivetech.backoffice.pojo.PojoVolumes;
import com.maxivetech.backoffice.service.admin.AdminRebateService;
import com.maxivetech.backoffice.service.user.impl.SessionServiceImpl;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperDate;

@Service
@Transactional
public class AdminRebateServiceImpl implements AdminRebateService {
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserBalanceDao userBalanceDao;
	@Autowired
	private UserBalanceLogDao userBalanceLogDao;
	@Autowired
	private RebateDao rebateDao;
	@Autowired
	private RebateRecordDao rebateRecordDao;

	private String _makeSQLWhereTime(int year, int month) {
		StringBuffer sqlWhere = new StringBuffer("");
		Date startDate = null;
		Date endDate = null;

		if (year != 0 && month == 0) { // 年
			String startYY = year + "-1-1 00:00:00";
			String endYY = (year + 1) + "-1-1 00:00:00";
			System.out.println(startYY + "/" + endYY);

			startDate = HelperDate.parse(startYY, "yyyy-MM-dd HH:mm:ss");
			endDate = HelperDate.parse(endYY, "yyyy-MM-dd HH:mm:ss");

		} else if (year != 0 && month != 0) { // 月
			String startYm = year + "-" + month + "-1 00:00:00";
			String endYm = year + "-" + (month + 1) + "-1 00:00:00";
			System.out.println(startYm + "//" + endYm);

			startDate = HelperDate.parse(startYm, "yyyy-MM-dd HH:mm:ss");
			endDate = HelperDate.parse(endYm, "yyyy-MM-dd HH:mm:ss");
		}

		sqlWhere.append(" AND " + (startDate.getTime() / 1000) + " <= MTT.close_time" + " AND MTT.close_time < "
				+ (endDate.getTime() / 1000));

		return sqlWhere.toString();
	}

	private String _makeSQLWhereTime4Report(int year, int month) {
		StringBuffer sqlWhere = new StringBuffer("");
		Date startDate = null;
		Date endDate = null;

		if (year != 0 && month == 0) { // 年
			String startYY = year + "-1-1 00:00:00";
			String endYY = (year + 1) + "-1-1 00:00:00";
			System.out.println(startYY + "/" + endYY);

			startDate = HelperDate.parse(startYY, "yyyy-MM-dd HH:mm:ss");
			endDate = HelperDate.parse(endYY, "yyyy-MM-dd HH:mm:ss");

		} else if (year != 0 && month != 0) { // 月
			String startYm = year + "-" + month + "-1 00:00:00";
			String endYm = year + "-" + (month + 1) + "-1 00:00:00";
			System.out.println(startYm + "//" + endYm);

			startDate = HelperDate.parse(startYm, "yyyy-MM-dd HH:mm:ss");
			endDate = HelperDate.parse(endYm, "yyyy-MM-dd HH:mm:ss");
		}

		sqlWhere.append(" AND " + (startDate.getTime() / 1000) + " <= UNIX_TIMESTAMP(RR.creat_time)"
				+ " AND UNIX_TIMESTAMP(RR.creat_time) < " + (endDate.getTime() / 1000));

		return sqlWhere.toString();
	}

	private List<PojoVolumes> _getVolumeList(int year, int month, String range, String sqlWhere) {
		String sqlTime = this._makeSQLWhereTime(year, month);

		List<PojoVolumes> listRet = new ArrayList<PojoVolumes>();

		List<Rebates> listRebate = rebateDao.getList();
		int i = 0;
		for (Rebates rebate : listRebate) {
			StringBuilder sb = new StringBuilder();

			sb.append("SELECT");
			sb.append(" U.id AS userId");
			sb.append(", U.email AS email");
			sb.append(", U.mobile AS mobile");
			sb.append(", UP.name AS name");
			sb.append(", U.vip_grade AS vipGrade");
			sb.append(", U.level AS level");
			sb.append(", '' AS vipGradeName");
			sb.append(", " + rebate.getId() + " AS rebateId");
			sb.append(", SUM(MTT.volume) AS volume");
			sb.append(" FROM users U");
			sb.append(" LEFT JOIN user__profiles UP ON U.id=UP.user_id");
			switch (range) {
			case "ALL": // 自己+客户
				sb.append(" INNER JOIN users U2 ON INSTR(U2.path, U.path)=1");
				sb.append(" INNER JOIN mt4_users MTU ON MTU.user_id = U2.id");
				break;
			case "CUSTOMER": // 仅客户
				sb.append(" INNER JOIN users U2 ON INSTR(U2.path, U.path)=1 AND U2.level > U.level");
				sb.append(" INNER JOIN mt4_users MTU ON MTU.user_id = U2.id");
				break;
			case "SELF": // 仅自己的
			default:
				sb.append(" INNER JOIN mt4_users MTU ON MTU.user_id = U.id");
				break;
			}

			sb.append(" INNER JOIN mt4_trades MTT ON MTT.login = MTU.login");
			sb.append(" WHERE ");
			sb.append(sqlWhere);
			sb.append(" AND MTT.cmd < 2");
			sb.append(sqlTime); // 时间
			sb.append(rebate.getSqlWhere()); // 分类
			sb.append(" GROUP BY U.id");
			sb.append(" ORDER BY volume DESC");

			List<PojoVolumes> list = rebateDao.getSession().createSQLQuery(sb.toString())
					.addScalar("userId", IntegerType.INSTANCE).addScalar("email", StringType.INSTANCE)
					.addScalar("mobile", StringType.INSTANCE).addScalar("name", StringType.INSTANCE)
					.addScalar("vipGrade", IntegerType.INSTANCE).addScalar("level", IntegerType.INSTANCE)
					.addScalar("vipGradeName", StringType.INSTANCE).addScalar("rebateId", IntegerType.INSTANCE)
					.addScalar("volume", IntegerType.INSTANCE)
					.setResultTransformer(Transformers.aliasToBean(PojoVolumes.class)).list();

			// 写入volumes数组
			for (int k = 0; k < list.size(); k++) {
				int l = 0;
				for (; l < listRet.size(); l++) {
					if (listRet.get(l).getUserId() == list.get(k).getUserId()) {
						listRet.get(l).getVolumes()[i] += list.get(k).getVolume();
						break;
					}
				}
				if (l == listRet.size()) {
					listRet.add(list.get(k));
					listRet.get(listRet.size() - 1).setVolumes(new int[listRebate.size()]);
					listRet.get(listRet.size() - 1).getVolumes()[i] = list.get(k).getVolume();
				}
			}

			i++;
		}

		for (PojoVolumes item : listRet) {
			item.setVipGradeName(
					BackOffice.getInst().companyHook.getVipGradeShowName(item.getVipGrade(), item.getLevel()));
			item.setVolume(0);
			for (int x = 0; x < listRebate.size(); x++) {
				item.setVolume(item.getVolumes()[x]);
			}
		}

		// 排序（倒序）
		listRet.sort(new Comparator<PojoVolumes>() {
			@Override
			public int compare(PojoVolumes o1, PojoVolumes o2) {
				return o2.getVolume() - o1.getVolume();
			}
		});

		return listRet;
	}

	@CheckRole(role = { Role.OperationsManager })
	@Override
	public List<PojoVolumes> getStaffVolumeList(int year, int month, HttpSession session) {
		return this._getVolumeList(year, month, "ALL", " U.level IN (2, 3)");
	}

	@CheckRole(role = { Role.OperationsManager, Role.ComplianceOfficer, Role.CustomerServiceStaff, Role.FinancialStaff,
			Role.FinancialSuperior, Role.RiskManagementCommissioner })
	@Override
	public List<PojoVolumes> getAgentVolumeList(int year, int month, HttpSession session) {
		return this._getVolumeList(year, month,
				BackOffice.getInst().companyHook.isAgentMT4TradeInVolume() ? "ALL" : "CUSTOMER", " U.vip_grade > 0");
	}

	@CheckRole(role = { Role.OperationsManager, Role.ComplianceOfficer, Role.CustomerServiceStaff, Role.FinancialStaff,
			Role.FinancialSuperior, Role.RiskManagementCommissioner, Role.User })
	@Override
	public List<PojoVolumes> getCustomerVolumeList(int year, int month, HttpSession session) {
		Users user = SessionServiceImpl.getCurrentUser(session);

		return this._getVolumeList(year, month, "SELF",
				" U.id<>" + user.getId() + " AND INSTR(U.path, '" + user.getPath() + "')=1");
	}

	@CheckRole(role = { Role.OperationsManager })
	@Override
	public List<PojoRebateReportUsers> reportUsers(int year, int month, String startDate, String endDate,
			HttpSession session) {
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT ");
		sb.append(" U.id AS userId,");
		sb.append(" UP.name AS userName,");
		sb.append(" U.email AS userEmail,");
		sb.append(" U.mobile AS userMobile,");
		sb.append(" '' AS vipGradeShowName,");
		sb.append(" U.level AS level,");
		sb.append(" U.vip_grade AS vipGrade,");
		sb.append(" SUM(RR.amount) AS rebate,");
		sb.append(" SUM(RR.volume) AS volume");
		sb.append(" FROM rebate_records RR");
		sb.append(" LEFT JOIN users U ON RR.user_id=U.id");
		sb.append(" LEFT JOIN user__profiles UP ON UP.user_id=U.id");
		sb.append(" WHERE 1");
		sb.append(this._makeSQLWhereTime4Report(year, month));
		sb.append(" GROUP BY userId");
		sb.append(" ORDER BY rebate DESC");

		List<PojoRebateReportUsers> list = rebateDao.getSession().createSQLQuery(sb.toString())
				.addScalar("userId", IntegerType.INSTANCE).addScalar("userName", StringType.INSTANCE)
				.addScalar("userEmail", StringType.INSTANCE).addScalar("userMobile", StringType.INSTANCE)
				.addScalar("vipGradeShowName", StringType.INSTANCE).addScalar("level", IntegerType.INSTANCE)
				.addScalar("vipGrade", IntegerType.INSTANCE).addScalar("rebate", DoubleType.INSTANCE)
				.addScalar("volume", IntegerType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(PojoRebateReportUsers.class)).list();
		;

		for (PojoRebateReportUsers pojo : list) {
			pojo.setVipGradeShowName(
					BackOffice.getInst().companyHook.getVipGradeShowName(pojo.getVipGrade(), pojo.getLevel()));
		}

		return list;
	}

	@CheckRole(role = { Role.OperationsManager })
	@Override
	public List<PojoRebateReportUsers> reportSourceUsers(int year, int month, String startDate, String endDate,
			HttpSession session) {
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT ");
		sb.append(" U.id AS userId,");
		sb.append(" UP.name AS userName,");
		sb.append(" U.email AS userEmail,");
		sb.append(" U.mobile AS userMobile,");
		sb.append(" '' AS vipGradeShowName,");
		sb.append(" U.level AS level,");
		sb.append(" U.vip_grade AS vipGrade,");
		sb.append(" SUM(RR.amount) AS rebate,");
		sb.append(" SUM(RR.volume) AS volume");
		sb.append(" FROM rebate_records RR");
		sb.append(" LEFT JOIN users U ON RR.order_user_id=U.id");
		sb.append(" LEFT JOIN user__profiles UP ON UP.user_id=U.id");
		sb.append(" WHERE 1");
		sb.append(this._makeSQLWhereTime4Report(year, month));
		sb.append(" GROUP BY userId");
		sb.append(" ORDER BY rebate DESC");

		List<PojoRebateReportUsers> list = rebateDao.getSession().createSQLQuery(sb.toString())
				.addScalar("userId", IntegerType.INSTANCE).addScalar("userName", StringType.INSTANCE)
				.addScalar("userEmail", StringType.INSTANCE).addScalar("userMobile", StringType.INSTANCE)
				.addScalar("vipGradeShowName", StringType.INSTANCE).addScalar("level", IntegerType.INSTANCE)
				.addScalar("vipGrade", IntegerType.INSTANCE).addScalar("rebate", DoubleType.INSTANCE)
				.addScalar("volume", IntegerType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(PojoRebateReportUsers.class)).list();
		;

		for (PojoRebateReportUsers pojo : list) {
			pojo.setVipGradeShowName(
					BackOffice.getInst().companyHook.getVipGradeShowName(pojo.getVipGrade(), pojo.getLevel()));
		}

		return list;
	}

	@CheckRole(role = { Role.OperationsManager })
	@Override
	public List<PojoRebateReportUsers> reportSourceLogins(int year, int month, String startDate, String endDate,
			HttpSession session) {
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT ");
		sb.append(" U.id AS userId,");
		sb.append(" UP.name AS userName,");
		sb.append(" U.email AS userEmail,");
		sb.append(" U.mobile AS userMobile,");
		sb.append(" '' AS vipGradeShowName,");
		sb.append(" U.level AS level,");
		sb.append(" U.vip_grade AS vipGrade,");
		sb.append(" SUM(RR.amount) AS rebate,");
		sb.append(" SUM(RR.volume) AS volume,");
		sb.append(" RR.login AS login");
		sb.append(" FROM rebate_records RR");
		sb.append(" LEFT JOIN users U ON RR.order_user_id=U.id");
		sb.append(" LEFT JOIN user__profiles UP ON UP.user_id=U.id");
		sb.append(" WHERE 1");
		sb.append(this._makeSQLWhereTime4Report(year, month));
		sb.append(" GROUP BY RR.login");
		sb.append(" ORDER BY rebate DESC");

		List<PojoRebateReportUsers> list = rebateDao.getSession().createSQLQuery(sb.toString())
				.addScalar("userId", IntegerType.INSTANCE).addScalar("userName", StringType.INSTANCE)
				.addScalar("userEmail", StringType.INSTANCE).addScalar("userMobile", StringType.INSTANCE)
				.addScalar("vipGradeShowName", StringType.INSTANCE).addScalar("level", IntegerType.INSTANCE)
				.addScalar("vipGrade", IntegerType.INSTANCE).addScalar("rebate", DoubleType.INSTANCE)
				.addScalar("volume", IntegerType.INSTANCE).addScalar("login", IntegerType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(PojoRebateReportUsers.class)).list();
		;

		for (PojoRebateReportUsers pojo : list) {
			pojo.setVipGradeShowName(
					BackOffice.getInst().companyHook.getVipGradeShowName(pojo.getVipGrade(), pojo.getLevel()));
		}

		return list;
	}

	@CheckRole(role = { Role.OperationsManager })
	@Override
	public synchronized void sendRebate(int[] userIds, HttpSession session) {
		if (userIds != null && userIds.length > 0) {
			for (int userId : userIds) {
				// 获取用户
				Users user = (Users) userDao.getById(userId);
				if (user == null) {
					continue;
				}

				// 获取返佣金额
				UserBalances ub = userBalanceDao.getBalance(user, "USD");
				if (ub == null) {
					continue;
				}
				double amount = ub.getAmountFrozen();

				userBalanceDao.increaseAmountFrozen(ub, -amount);
				userBalanceDao.increaseAmountAvailable(ub, amount);
				userBalanceLogDao.addLog(ub, amount, 0, 0, 0, "返佣发放");
			}

			userDao.commit();
		}
	}

	@CheckRole(role = { Role.OperationsManager })
	@Override
	public List<PojoUserBalances> getRebateBalanceList(HttpSession session) {
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT ");
		sb.append(" U.id AS userId,");
		sb.append(" UP.name AS userName,");
		sb.append(" U.email AS userEmail,");
		sb.append(" U.mobile AS userMobile,");
		sb.append(" U.level AS level,");
		sb.append(" U.vip_grade AS vipGrade,");
		sb.append(" UB.amount_available AS amountAvailable,");
		sb.append(" UB.amount_frozen AS amountFrozen");
		sb.append(" FROM users U");
		sb.append(" LEFT JOIN user__profiles UP ON UP.user_id=U.id");
		sb.append(" INNER JOIN user__balances UB ON UB.user_id=U.id AND UB.currency_type='USD'");
		sb.append(" WHERE UB.amount_frozen <> 0");
		sb.append(" ORDER BY amountFrozen DESC");

		List<PojoUserBalances> list = rebateDao.getSession().createSQLQuery(sb.toString())
				.addScalar("userId", IntegerType.INSTANCE).addScalar("userName", StringType.INSTANCE)
				.addScalar("userEmail", StringType.INSTANCE).addScalar("userMobile", StringType.INSTANCE)
				.addScalar("level", IntegerType.INSTANCE).addScalar("vipGrade", IntegerType.INSTANCE)
				.addScalar("amountAvailable", DoubleType.INSTANCE).addScalar("amountFrozen", DoubleType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(PojoUserBalances.class)).list();
		;

		for (PojoUserBalances pojo : list) {
			pojo.setVipGradeShowName(
					BackOffice.getInst().companyHook.getVipGradeShowName(pojo.getVipGrade(), pojo.getLevel()));
		}

		return list;
	}

	// GCT需求添加 按月份返佣接口2016-09-18 14:53

	@CheckRole(role = { Role.OperationsManager })
	@Override
	public HashMap<String, Object> getRebateBalanceList1(HttpSession session) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();	
        sb.append("SELECT ");
        sb.append(" U.id AS userId,");
        sb.append(" UP.name AS userName,");
        sb.append(" U.email AS userEmail,");
        sb.append(" U.mobile AS userMobile,");
        sb.append(" U.level AS level,");
        sb.append(" U.vip_grade AS vipGrade,");
        sb.append(" rr.amount as amount,");
		sb.append(" UB.amount_frozen AS amountFrozen,");
        sb.append(" UB.amount_available AS amountAvailable");
        sb.append(" FROM users U");
        sb.append(" LEFT JOIN user__profiles UP ON UP.user_id=U.id");
        sb.append(" INNER JOIN user__balances UB ON UB.user_id=U.id AND UB.currency_type='USD'");
        sb.append(" LEFT JOIN (select sum(amount) as amount,rr1.user_id,rr1.send from rebate_records rr1  where  rr1.send=0  group by user_id) rr ON rr.user_id=U.id");
        sb.append(" WHERE  rr.amount IS NOT NULL");
        sb.append(" ORDER BY U.id ASC");
        System.out.println(sb.toString());
		List<PojoUserBalances> listall = rebateDao.getSession().createSQLQuery(sb.toString())
				.addScalar("userId", IntegerType.INSTANCE)
				.addScalar("userName", StringType.INSTANCE)
				.addScalar("userEmail", StringType.INSTANCE)
				.addScalar("userMobile", StringType.INSTANCE)
				.addScalar("level", IntegerType.INSTANCE)
				.addScalar("vipGrade", IntegerType.INSTANCE)
				.addScalar("amountFrozen", DoubleType.INSTANCE)
				.addScalar("amountAvailable", DoubleType.INSTANCE)
				.addScalar("amount", DoubleType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(PojoUserBalances.class)).list();
		for (PojoUserBalances pojo : listall) {
			pojo.setVipGradeShowName(BackOffice.getInst().companyHook.getVipGradeShowName(pojo.getVipGrade(), pojo.getLevel()));
		}
		
		
		
        result.put("listAll",listall);
        StringBuilder sbm = new StringBuilder();
        sbm.append("SELECT ");
        sbm.append(" U.id AS userId,");
        sbm.append(" UP.name AS userName,");
        sbm.append(" U.email AS userEmail,");
        sbm.append(" U.mobile AS userMobile,");
        sbm.append(" U.level AS level,");
        sbm.append(" U.vip_grade AS vipGrade,");
        sbm.append(" rr.amount as amount,");
        sbm.append(" rr.month as month,");
        sbm.append(" UB.amount_available AS amountAvailable");
        sbm.append(" FROM users U");
        sbm.append(" LEFT JOIN user__profiles UP ON UP.user_id=U.id");
        sbm.append(" INNER JOIN user__balances UB ON UB.user_id=U.id AND UB.currency_type='USD'");
        sbm.append(" LEFT JOIN (select sum(amount) as amount,CONVERT(rr1.creat_time,char(7)) as month,rr1.user_id,rr1.send from rebate_records rr1   where  rr1.send=0  group by user_id,CONVERT(rr1.creat_time,char(7))) rr ON rr.user_id=U.id ");
        sbm.append(" WHERE  rr.amount IS NOT NULL");
        sbm.append(" ORDER BY rr.month DESC");
        System.out.println(sbm.toString());
		List<PojoUserBalances> listmonth = rebateDao.getSession().createSQLQuery(sbm.toString())
				.addScalar("userId", IntegerType.INSTANCE)
				.addScalar("userName", StringType.INSTANCE)
				.addScalar("userEmail", StringType.INSTANCE)
				.addScalar("userMobile", StringType.INSTANCE)
				.addScalar("level", IntegerType.INSTANCE)
				.addScalar("vipGrade", IntegerType.INSTANCE)
				.addScalar("amountAvailable", DoubleType.INSTANCE)
				.addScalar("amount", DoubleType.INSTANCE)
				.addScalar("month", StringType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(PojoUserBalances.class)).list();
		for (PojoUserBalances pojo : listmonth) {
			pojo.setVipGradeShowName(BackOffice.getInst().companyHook.getVipGradeShowName(pojo.getVipGrade(), pojo.getLevel()));
		}
        result.put("listMonth",listmonth);
		List<String>  monthlist=rebateDao.getSession().createSQLQuery("select CONVERT(creat_time,char(7)) as month from rebate_records group by month order by month desc")
				.addScalar("month",StringType.INSTANCE)
				.list();
        result.put("monthlist",monthlist);
		return result;
	}

	
	@CheckRole(role = { Role.OperationsManager })
	@Override
	public synchronized void sendRebate1(PojoUserAndMonth[] params,HttpSession session) {
		if(params.length>0){
			 StringBuilder sbm = new StringBuilder();
		        sbm.append("SELECT ");
		        sbm.append(" U.id AS userId,");
		        sbm.append(" UP.name AS userName,");
		        sbm.append(" U.email AS userEmail,");
		        sbm.append(" U.mobile AS userMobile,");
		        sbm.append(" U.level AS level,");
		        sbm.append(" U.vip_grade AS vipGrade,");
		        sbm.append(" rr.amount as amount,");
		        sbm.append(" rr.month as month,");
		        sbm.append(" rr.id as id,");
		        sbm.append(" UB.amount_available AS amountAvailable");
		        sbm.append(" FROM users U");
		        sbm.append(" LEFT JOIN user__profiles UP ON UP.user_id=U.id");
		        sbm.append(" INNER JOIN user__balances UB ON UB.user_id=U.id AND UB.currency_type='USD'");
		        sbm.append(" LEFT JOIN (select amount as amount,CONVERT(rr1.creat_time,char(7)) as month,rr1.user_id,rr1.id,rr1.send from rebate_records rr1  where  rr1.send=0 ) rr ON rr.user_id=U.id ");
		        sbm.append(" WHERE  rr.amount IS NOT NULL ");
		        System.out.println(sbm.toString());
				List<PojoUserBalances> listmonth = rebateDao.getSession().createSQLQuery(sbm.toString())
						.addScalar("id", IntegerType.INSTANCE)
						.addScalar("userId", IntegerType.INSTANCE)
						.addScalar("userName", StringType.INSTANCE)
						.addScalar("userEmail", StringType.INSTANCE)
						.addScalar("userMobile", StringType.INSTANCE)
						.addScalar("level", IntegerType.INSTANCE)
						.addScalar("vipGrade", IntegerType.INSTANCE)
						.addScalar("amountAvailable", DoubleType.INSTANCE)
						.addScalar("amount", DoubleType.INSTANCE)
						.addScalar("month", StringType.INSTANCE)
						.setResultTransformer(Transformers.aliasToBean(PojoUserBalances.class)).list();
		   
			for (PojoUserAndMonth pua : params) {
				String[] month = pua.getMonths();
				if (month[0].equals("all")) {
					double amount = 0;
					for (PojoUserBalances pub : listmonth) {
						if (pub.getUserId() == pua.getUserId()) {
							RebateRecords rr = (RebateRecords) rebateRecordDao.getById(pub.getId());
							amount += pub.getAmount();
							rr.setSend(true);
							rebateRecordDao.saveOrUpdate(rr);
						}
					}
					
					UserBalances uba = userBalanceDao.getBalance((Users) userDao.getById(pua.getUserId()), "USD");
					userBalanceDao.increaseAmountAvailable(uba, amount);
					userBalanceLogDao.addLog(uba, amount, 0, 0, 0, "返佣发放");
					userBalanceDao.increaseAmountFrozen(uba, -amount);
					userBalanceDao.saveOrUpdate(uba);
				} else {
					for (String s : month) {
						double amount = 0;
						for (PojoUserBalances pub : listmonth) {
							if (pub.getUserId() == pua.getUserId() && pub.getMonth().equals(s)) {
								RebateRecords rr = (RebateRecords) rebateRecordDao.getById(pub.getId());
								amount += pub.getAmount();
								rr.setSend(true);
								rebateRecordDao.saveOrUpdate(rr);
							}
						}

						UserBalances uba = userBalanceDao.getBalance((Users) userDao.getById(pua.getUserId()), "USD");
						userBalanceDao.increaseAmountAvailable(uba, amount);
						userBalanceLogDao.addLog(uba, amount, 0, 0, 0, month + "月返佣发放");
						userBalanceDao.increaseAmountFrozen(uba, -amount);
						userBalanceDao.saveOrUpdate(uba);
					}
				}
			}
			rebateDao.commit();
		} else {
			throw new RuntimeException("你未选择任何用户！");
		}
		
	}
}
