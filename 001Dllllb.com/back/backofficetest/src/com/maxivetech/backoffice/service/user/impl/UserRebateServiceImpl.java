package com.maxivetech.backoffice.service.user.impl;

import java.util.ArrayList;
import java.util.Comparator;
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
import com.maxivetech.backoffice.pojo.PojoUserBalances;
import com.maxivetech.backoffice.pojo.PojoVolumes;
import com.maxivetech.backoffice.service.admin.AdminRebateService;
import com.maxivetech.backoffice.service.user.UserRebateService;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperDate;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class UserRebateServiceImpl implements UserRebateService {
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
	private String _makeSQLWhereTime4Report(int year, int month) {
		StringBuffer sqlWhere = new StringBuffer("");
		Date startDate = null;
		Date endDate = null;
		
		if (year != 0 && month == 0) {	//年
			String startYY = year + "-1-1 00:00:00";
			String endYY = (year + 1) + "-1-1 00:00:00";
			System.out.println(startYY + "/" + endYY);
			
			startDate = HelperDate.parse(startYY, "yyyy-MM-dd HH:mm:ss");
			endDate = HelperDate.parse(endYY, "yyyy-MM-dd HH:mm:ss");

		} else if (year != 0 && month != 0) {	//月
			String startYm = year + "-" + month + "-1 00:00:00";
			String endYm = year + "-" + (month + 1) + "-1 00:00:00";
			System.out.println(startYm + "//" + endYm);
			
			startDate = HelperDate.parse(startYm, "yyyy-MM-dd HH:mm:ss");
			endDate = HelperDate.parse(endYm, "yyyy-MM-dd HH:mm:ss");
		}
		
		sqlWhere.append(" AND " + (startDate.getTime() / 1000)
				+ " <= UNIX_TIMESTAMP(RR.creat_time)"
				+ " AND UNIX_TIMESTAMP(RR.creat_time) < " + (endDate.getTime() / 1000));
		
		return sqlWhere.toString();
	}

	

	@CheckRole(role = {Role.User})
	@Override
	public List<PojoRebateReportUsers> reportUsers(int year, int month,
			String startDate, String endDate, HttpSession session) {
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
		//下级
		{
			Users user = SessionServiceImpl.getCurrentUser(session);
			sb.append(" AND INSTR(U.path, '"+user.getPath()+"')=1");
		}
		sb.append(this._makeSQLWhereTime4Report(year, month));
		sb.append(" GROUP BY userId");
		sb.append(" ORDER BY rebate DESC");
		
		List<PojoRebateReportUsers> list = rebateDao.getSession().createSQLQuery(sb.toString())
			.addScalar("userId", IntegerType.INSTANCE)
			.addScalar("userName", StringType.INSTANCE)
			.addScalar("userEmail", StringType.INSTANCE)
			.addScalar("userMobile", StringType.INSTANCE)
			.addScalar("vipGradeShowName", StringType.INSTANCE)
			.addScalar("level", IntegerType.INSTANCE)
			.addScalar("vipGrade", IntegerType.INSTANCE)
			.addScalar("rebate", DoubleType.INSTANCE)
			.addScalar("volume", IntegerType.INSTANCE)
			.setResultTransformer(Transformers.aliasToBean(PojoRebateReportUsers.class))
			.list();;
			
		for (PojoRebateReportUsers pojo : list) {
			pojo.setVipGradeShowName(BackOffice.getInst().companyHook.getVipGradeShowName(pojo.getVipGrade(), pojo.getLevel()));
		}
		
		return list;
	}

	@CheckRole(role = {Role.User})
	@Override
	public List<PojoRebateReportUsers> reportSourceUsers(int year, int month,
			String startDate, String endDate, HttpSession session) {
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
		//下级
		{
			Users user = SessionServiceImpl.getCurrentUser(session);
			sb.append(" AND RR.user_id="+user.getId());
			sb.append(" AND INSTR(U.path, '"+user.getPath()+"')=1");
		}
		sb.append(this._makeSQLWhereTime4Report(year, month));
		sb.append(" GROUP BY userId");
		sb.append(" ORDER BY rebate DESC");
		
		List<PojoRebateReportUsers> list = rebateDao.getSession().createSQLQuery(sb.toString())
			.addScalar("userId", IntegerType.INSTANCE)
			.addScalar("userName", StringType.INSTANCE)
			.addScalar("userEmail", StringType.INSTANCE)
			.addScalar("userMobile", StringType.INSTANCE)
			.addScalar("vipGradeShowName", StringType.INSTANCE)
			.addScalar("level", IntegerType.INSTANCE)
			.addScalar("vipGrade", IntegerType.INSTANCE)
			.addScalar("rebate", DoubleType.INSTANCE)
			.addScalar("volume", IntegerType.INSTANCE)
			.setResultTransformer(Transformers.aliasToBean(PojoRebateReportUsers.class))
			.list();;
			
		for (PojoRebateReportUsers pojo : list) {
			pojo.setVipGradeShowName(BackOffice.getInst().companyHook.getVipGradeShowName(pojo.getVipGrade(), pojo.getLevel()));
		}
		
		return list;
	}

	@CheckRole(role = {Role.User})
	@Override
	public List<PojoRebateReportUsers> reportSourceLogins(int year, int month,
			String startDate, String endDate, HttpSession session) {
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
		//下级
		{
			Users user = SessionServiceImpl.getCurrentUser(session);
			sb.append(" AND RR.user_id="+user.getId());
			sb.append(" AND INSTR(U.path, '"+user.getPath()+"')=1");
		}
		sb.append(this._makeSQLWhereTime4Report(year, month));
		sb.append(" GROUP BY RR.login");
		sb.append(" ORDER BY rebate DESC");
		
		List<PojoRebateReportUsers> list = rebateDao.getSession().createSQLQuery(sb.toString())
			.addScalar("userId", IntegerType.INSTANCE)
			.addScalar("userName", StringType.INSTANCE)
			.addScalar("userEmail", StringType.INSTANCE)
			.addScalar("userMobile", StringType.INSTANCE)
			.addScalar("vipGradeShowName", StringType.INSTANCE)
			.addScalar("level", IntegerType.INSTANCE)
			.addScalar("vipGrade", IntegerType.INSTANCE)
			.addScalar("rebate", DoubleType.INSTANCE)
			.addScalar("volume", IntegerType.INSTANCE)
			.addScalar("login", IntegerType.INSTANCE)
			.setResultTransformer(Transformers.aliasToBean(PojoRebateReportUsers.class))
			.list();;
			
		for (PojoRebateReportUsers pojo : list) {
			pojo.setVipGradeShowName(BackOffice.getInst().companyHook.getVipGradeShowName(pojo.getVipGrade(), pojo.getLevel()));
		}
		
		return list;
	}


}
