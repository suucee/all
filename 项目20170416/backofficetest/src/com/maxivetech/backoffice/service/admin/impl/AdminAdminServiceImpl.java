package com.maxivetech.backoffice.service.admin.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import com.maxivetech.backoffice.dao.AdminLogDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.entity.AdminLogs;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.pojo.PojoSession;
import com.maxivetech.backoffice.sendmail.HelperSendmail;
import com.maxivetech.backoffice.service.admin.AdminAdminService;
import com.maxivetech.backoffice.service.admin.AdminUserService;
import com.maxivetech.backoffice.service.user.SessionService;
import com.maxivetech.backoffice.service.user.impl.SessionServiceImpl;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperPassword;
import com.maxivetech.backoffice.util.HelperSalty;
import com.maxivetech.backoffice.util.InvalidOperationPasswordException;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class AdminAdminServiceImpl implements AdminAdminService {
	@Autowired
	private AdminDao adminDao;
	@Autowired
	private AdminLogDao adminLogDao;

	@CheckRole(role = {Role.OperationsManager})
	@Override
	public Page<Admins> getPage(int pageNo, int pageSize, String urlFormat,
			String scheme, String keyword, HttpSession session) {
		
		//WHERE
		List<Criterion> wheres = new ArrayList<Criterion>();
		//WHERE 角色
		switch (scheme) {
		case "compliance_officer":
			wheres.add(Restrictions.eq("role", "ComplianceOfficer"));
			break;
		case "financial_superior":
			wheres.add(Restrictions.eq("role", "FinancialSuperior"));
			break;
		case "financial_staff":
			wheres.add(Restrictions.eq("role", "FinancialStaff"));
			break;
		case "customer_service_staff":
			wheres.add(Restrictions.eq("role", "CustomerServiceStaff"));
			break;
		case "webmaster":
			wheres.add(Restrictions.eq("role", "Webmaster"));
			break;
		case "risk_management_commissioner":
			wheres.add(Restrictions.eq("role", "RiskManagementCommissioner"));
			break;
		case "operations_manager":
			wheres.add(Restrictions.eq("role", "OperationsManager"));
			break;
		case "disabled":
			wheres.add(Restrictions.eq("isDisabled", true));
		}
		
		//ORDER BY
		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("creatTime"));
		
		//查询
		Page<Admins> page = (Page<Admins>) adminDao.getPage(wheres, orders, null, pageSize, pageNo, null);
		page.generateButtons(urlFormat);
		
		//清洗
		for (Admins admin : page.getList()) {
			admin.setPassword("");
			admin.setOperationPassword("");
			admin.setSalty("");
			
		}
		
		return page;
	}

	@CheckRole(role = {Role.OperationsManager})
	@Override
	public boolean setDisabled(int adminId, boolean isDisabled, HttpSession session) {

		Admins admin = (Admins) adminDao.getById(adminId);
		if (admin != null) {
			admin.setDisabled(isDisabled);
			
			adminDao.update(admin);
			adminDao.commit();
			
			return true;
		}
		
		return false;
	}

	@CheckRole(role = {Role.OperationsManager})
	@Override
	public List<PojoCounts> getCounts(HttpSession session) {

		
		//TODO:弄個多少分鐘緩存，省得各種查數據庫
		List<PojoCounts> list = new ArrayList<PojoCounts>();
		
		//all
		{
		    String hql = "select count(*) from Admins where isDisabled=0";
		    long count = (long)adminDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("all", count));
		}
		//operations_manager
		{
		    String hql = "select count(*) from Admins where role='OperationsManager' AND isDisabled=0";
		    long count = (long)adminDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("operations_manager", count));
		}
		//compliance_officer
		{
		    String hql = "select count(*) from Admins where role='ComplianceOfficer' AND isDisabled=0";
		    long count = (long)adminDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("compliance_officer", count));
		}
		//financial_staff
		{
		    String hql = "select count(*) from Admins where role='FinancialStaff' AND isDisabled=0";
		    long count = (long)adminDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("financial_staff", count));
		}
		//financial_superior
		{
		    String hql = "select count(*) from Admins where role='FinancialSuperior' AND isDisabled=0";
		    long count = (long)adminDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("financial_superior", count));
		}
		//customer_service_staff
		{
		    String hql = "select count(*) from Admins where role='CustomerServiceStaff' AND isDisabled=0";
		    long count = (long)adminDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("customer_service_staff", count));
		}
		//webmaster
		{
		    String hql = "select count(*) from Admins where role='Webmasters' AND isDisabled=0";
		    long count = (long)adminDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("webmaster", count));
		}
		//disabled
		{
		    String hql = "select count(*) from Admins where isDisabled=1";
		    long count = (long)adminDao.getSession().createQuery(hql).uniqueResult();  
		    list.add(new PojoCounts("disabled", count));
		}
		
		
		return list;
	}

	@CheckRole(role = {Role.OperationsManager})
	@Override
	public Admins getOne(int adminId, HttpSession session) {

		Admins admin = (Admins) adminDao.getById(adminId);
		
		// 清理
		if (admin != null) {
			admin.setOperationPassword("");
			admin.setPassword("");
			admin.setSalty("");
			
			return admin;
		}
		
		return null;
	}


	@CheckRole(role = {Role.OperationsManager})
	@Override
	public String edit(int adminId,String action,String memo,String operationPassword, HttpSession session) {
		
		Admins admins= (Admins) adminDao.getById(SessionServiceImpl.getCurrent(session).getId());
		if(!HelperPassword.verifyPassword(operationPassword, admins.getOperationPassword(), admins.getSalty())){
			return "密码错误";
		}
		Admins admin = (Admins) adminDao.getById(adminId);
		if (admin != null && !admin.getRole().equals("OperationsManager")) {
			if((admin.isDisabled()&&action.equals("DISABLE"))
					||(!admin.isDisabled()&&action.equals("ENABLE"))){
				return "不合法的操作";
			}else {
				switch (action) {
				case "DISABLE":
					admin.setDisabled(true);
					break;
				case "ENABLE":
					admin.setDisabled(false);
					break;
				default:
					return "不合法的操作";
				}
			}
			
			adminDao.update(admin);
			
			//添加日志
			AdminLogs al = new AdminLogs();
			al.setAction(action);
			al.setAdmin(admins);
			al.setCreatTime(new Date());
			al.setDescription(memo);
			al.setId(adminId);
			al.setIpAddress(String.valueOf(session.getAttribute("RemoteAddr")));
			adminLogDao.save(al);
			
			adminDao.commit();
			
			return "操作成功";
		}
		
		return "操作失败";
	}

	@CheckRole(role = {Role.OperationsManager})
	@Override
	public int add(String role, String account, String password,String  showName,String operationPassword, HttpSession session) {
		
		//检查操作密码
		Admins admins= (Admins) adminDao.getById(SessionServiceImpl.getCurrent(session).getId());
		if(!HelperPassword.verifyPassword(operationPassword, admins.getOperationPassword(), admins.getSalty())){
			throw new InvalidOperationPasswordException();
		}
		
		//检查Account重复
		if (adminDao.findByAccount(account) != null) {
			throw new RuntimeException("账号“"+account+"”已经存在！");
		}
		
		//生成随机密码
		String randomSalty = HelperSalty.getCharAndNumr(6);
		
		//添加admin
		Admins admin = new Admins();
		admin.setCreatTime(new Date());
		admin.setLastLogonTime(new Date());
		admin.setDisabled(false);
		admin.setAccount(account);
		admin.setShowName(showName);
		admin.setOperationPassword("");
		admin.setSalty(randomSalty);
		admin.setPassword(HelperPassword.beforeSave(password, randomSalty));
		admin.setRole(role);
		
		int ret = (Integer) adminDao.save(admin);
		adminDao.commit();
		
		return ret;
	}
}
