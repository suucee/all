package com.maxivetech.backoffice.service.admin.impl;

import java.util.ArrayList;
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
import com.maxivetech.backoffice.entity.Rebates;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.service.admin.AdminAgentService;
import com.maxivetech.backoffice.util.HelperRandomCode;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class AdminAgentServiceImpl implements AdminAgentService {
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
	public Page<Users> getPage(int pageNo, int pageSize, String urlFormat,
			String scheme, String keyword, HttpSession session) {
		
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
		
		//清洗
		for (Users user : page.getList()) {
			user.setPassword("");
			user.setPaymentPassword("");
			user.setPassword("");
			
			user.set_name(userProfileDao.getUserName(user));
			user.set_vipGradeName(BackOffice.getInst().companyHook.getVipGradeShowName(user.getVipGrade(), user.getLevel()));
		}
		
		return page;
	}

	@CheckRole(role = {Role.OperationsManager})
	@CheckOperationPassword(parameterIndex = 2)
	@Override
	public void setAgent(int userId, int vipGrade, String operationPassword, HttpSession session) {
		if (!BackOffice.getInst().companyHook.isSupportedVipGrade(vipGrade)) {
			throw new RuntimeException("不支持的等级");
		}
		
		
		Users user = (Users) userDao.getById(userId);
		if (user != null) {
			if (user.getLevel() <= 3 && vipGrade > 0) {
				throw new RuntimeException("不能将公司、经理、员工设置为代理角色。");
			}
			
			if (vipGrade > 0 && user.getReferralCode() == null) {
				String code = "";
				do {
					code = HelperRandomCode.getARandomCode(5);
				} while (userDao.findByUserCode(code) != null);
				
				//XXX:推荐码用完了会死循环
				user.setReferralCode(code);
			}
			
			user.setVipGrade(vipGrade);
			
			userDao.update(user);
			userDao.commit();
		} else {
			throw new RuntimeException("未找到指定用户");
		}
	}

	@CheckRole(role = {Role.OperationsManager})
	@CheckOperationPassword(parameterIndex = 2)
	@Override
	public void setAgent(int userId, int vipGrade, String operationPassword, double []rebates, HttpSession session) {
		if (!BackOffice.getInst().companyHook.isSupportedVipGrade(vipGrade)) {
			throw new RuntimeException("不支持的等级");
		}
		
		
		Users user = (Users) userDao.getById(userId);
		if (user != null) {
			if (user.getLevel() <= 3 && vipGrade > 0) {
				throw new RuntimeException("不能将公司、经理、员工设置为代理角色。");
			}
			
			if (vipGrade > 0 && user.getReferralCode() == null) {
				String code = "";
				do {
					code = HelperRandomCode.getARandomCode(5);
				} while (userDao.findByUserCode(code) != null);
				
				//XXX:推荐码用完了会死循环
				user.setReferralCode(code);
			}
			
			user.setVipGrade(vipGrade);
			
			//记内返返佣比例
			if (rebates != null)
			{
				int rebateId = 0;
				for (double money : rebates) {
					rebateId ++;
					Rebates rebate = (Rebates)rebateDao.getById(rebateId);
					rebateAgentDao.saveOrUpdate(user, rebate, money);
				}
			}
			
			userDao.update(user);
			userDao.commit();
		} else {
			throw new RuntimeException("未找到指定用户");
		}
	}

	@CheckRole(role = {Role.OperationsManager})
	@CheckOperationPassword(parameterIndex = 2)
	@Override
	public void setStaff(int userId, String scheme, String operationPassword, HttpSession session) {
		Users user = (Users) userDao.getById(userId);
		
		if (user != null) {
			if (user.getLevel() > 4 && !scheme.equals("Non")) {
//				throw new RuntimeException("当前用户层级为"+user.getLevel()+"，不满足设为公司/老总/总监/经理/员工");
				throw new RuntimeException("不能将此用户设置为公司、经理或者员工，请尝试修改此用户的推荐人后，再重新设置。");
			}
			
			Users parent = user.getParent();
			if (user.getVipGrade() > 0) {
				throw new RuntimeException("不能够直接将代理设为公司、经理或者员工");
			}
			
			switch (scheme) {
			case "Non":
				if (parent != null) {
					user.setLevel(Math.max(user.getLevel(), 4));
				} else {
					throw new RuntimeException("公司账户不能设为客户");
				}
				break;
			case "Company":
				break;
			case "Boss":
				if (parent != null && parent.getLevel() == -1) {
					user.setLevel(0);
				} else {
					throw new RuntimeException("老总须挂靠在公司下");
				}
				break;
			case "Director":
				if (parent != null && parent.getLevel() <= 0) {
					user.setLevel(1);
				} else {
					throw new RuntimeException("总监须挂靠在公司或老总下");
				}
				break;
			case "Manager":
				if (parent != null && parent.getLevel() <= 1) {
					user.setLevel(2);
				} else {
					throw new RuntimeException("经理须挂靠在公司、老总或总监下");
				}
				break;
			case "Staff":
				if (parent != null && (parent.getLevel() <= 2)) {
					user.setLevel(3);
				} else {
					throw new RuntimeException("员工须挂靠在公司、老总、总监或经理下");
				}
				break;
			default:
				throw new RuntimeException("不正确的设置值");
			}
			
			userDao.update(user);
			userDao.commit();
		} else {
			throw new RuntimeException("未找到指定用户");
		}
	}

}
