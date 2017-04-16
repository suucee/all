package com.maxivetech.backoffice.service.admin.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.annotation.CheckOperationPassword;
import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.RebateAgentDao;
import com.maxivetech.backoffice.dao.RebateDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.entity.RebateAgents;
import com.maxivetech.backoffice.entity.Rebates;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.service.admin.Admin2AgentService;
import com.maxivetech.backoffice.service.admin.AdminAgentService;
import com.maxivetech.backoffice.util.HelperRandomCode;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class Admin2AgentServiceImpl implements Admin2AgentService {
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserProfilesDao userProfileDao;
	@Autowired
	private RebateAgentDao rebateAgentDao;
	@Autowired
	private RebateDao rebateDao;

	@CheckRole(role = {Role.OperationsManager})
	@Override
	public HashMap<String,Object> getPageAgent(int pageNo, int pageSize, String urlFormat,
			String scheme, String keyword, HttpSession session) {
		HashMap<String,Object>  result=new HashMap<String,Object>();
		//WHERE
		List<Criterion> wheres = new ArrayList<Criterion>();
		//WHERE 角色
		wheres.add(Restrictions.eq("disable", false));
		wheres.add(Restrictions.gt("vipGrade", 0));
		
		//ORDER BY
		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("vipGrade"));
		
		//查询
		Page<Users> page = (Page<Users>) userDao.getPage(wheres, orders, null, pageSize, pageNo, null);
		page.generateButtons(urlFormat);
		List<Criterion> wheres1 = new ArrayList<Criterion>();
		//wheres1.add(Restrictions.in("user", page.getList()));
		
		List<Order> orders1 = new ArrayList<Order>();
		
		Criterion c=Restrictions.isNotNull("user");
		//清洗
		for (Users user : page.getList()) {
			c=Restrictions.or(c,Restrictions.eq("user", user));
			user.setPassword("");
			user.setPaymentPassword("");
			user.setPassword("");
			
			user.set_name(userProfileDao.getUserName(user));
			user.set_vipGradeName(BackOffice.getInst().companyHook.getVipGradeShowName(user.getVipGrade(), user.getLevel()));
		}
		wheres1.add(c);
		List<RebateAgents> ral=rebateAgentDao.getAll(wheres1, orders1, null, 0, 0).list();
		result.put("page", page);
		result.put("rebateAgent", ral);
		return result;
	}
}
