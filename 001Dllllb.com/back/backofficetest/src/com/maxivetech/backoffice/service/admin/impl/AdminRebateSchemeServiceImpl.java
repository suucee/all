package com.maxivetech.backoffice.service.admin.impl;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.RebateAgentDao;
import com.maxivetech.backoffice.dao.RebateDao;
import com.maxivetech.backoffice.dao.RebateLevelDao;
import com.maxivetech.backoffice.dao.RebateReferralDao;
import com.maxivetech.backoffice.dao.RebateVipGradeDao;
import com.maxivetech.backoffice.dao.RebateVipGradeMt4GroupDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.entity.RebateAgents;
import com.maxivetech.backoffice.entity.RebateLevels;
import com.maxivetech.backoffice.entity.RebateReferrals;
import com.maxivetech.backoffice.entity.RebateVipGradeMt4Groups;
import com.maxivetech.backoffice.entity.RebateVipGrades;
import com.maxivetech.backoffice.entity.Rebates;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.service.admin.AdminRebateSchemeService;

@Service
@Transactional
public class AdminRebateSchemeServiceImpl implements AdminRebateSchemeService {
	@Autowired
	private RebateDao rebateDao;
	@Autowired
	private RebateLevelDao rebateLevelDao;
	@Autowired
	private RebateReferralDao rebateReferralDao;
	@Autowired
	private RebateVipGradeDao rebateVipGradeDao;
	@Autowired
	private RebateVipGradeMt4GroupDao rebateVipGradeMt4GroupDao;
	@Autowired
	private RebateAgentDao rebateAgentDao;
	@Autowired
	private UserDao userDao;

	@Override
	public List<Rebates> getList(HttpSession session) {
		return rebateDao.getList();
	}

	@CheckRole(role = {Role.OperationsManager})
	@Override
	public List<RebateLevels> getLevelList(HttpSession session) {
		return rebateLevelDao.getList();
	}

	@CheckRole(role = {Role.OperationsManager})
	@Override
	public List<RebateReferrals> getReferralList(HttpSession session) {
		return rebateReferralDao.getList();
	}

	@CheckRole(role = {Role.OperationsManager})
	@Override
	public List<RebateVipGrades> getVipGradeList(HttpSession session) {
		List<RebateVipGrades> list = rebateVipGradeDao.getList();
		for (RebateVipGrades item : list) {
			item.set_vipGradeName(BackOffice.getInst().companyHook.getVipGradeShowName(item.getVipGrade(), 0));
		}
		
		return list;
	}
	
	@CheckRole(role = {Role.OperationsManager})
	@Override
	public List<RebateVipGradeMt4Groups> getVipGradeMt4GroupList(HttpSession session) {
		List<RebateVipGradeMt4Groups> list = rebateVipGradeMt4GroupDao.getList();
		for (RebateVipGradeMt4Groups item : list) {
			item.set_vipGradeName(BackOffice.getInst().companyHook.getVipGradeShowName(item.getVipGrade(), 0));
		}
		
		return list;
	}

	@CheckRole(role = {Role.OperationsManager})
	@Override
	public void updateLevel(int rebateLevelId, double money, HttpSession session) {
		RebateLevels rebateLevel = (RebateLevels)rebateLevelDao.getById(rebateLevelId);
		if (rebateLevel != null) {
			rebateLevel.setMoney(money);
			rebateLevel.setUpdatedTime(new Date());
			rebateLevelDao.update(rebateLevel);
			rebateLevelDao.commit();
		} else {
			throw new RuntimeException("未找到指定对象！");
		}
	}

	@CheckRole(role = {Role.OperationsManager})
	@Override
	public void updateReferral(int rebateReferralId, double money1,
			double money2, HttpSession session) {
		RebateReferrals rebateReferral = (RebateReferrals)rebateReferralDao.getById(rebateReferralId);
		if (rebateReferral != null) {
			rebateReferral.setMoney1(money1);
			rebateReferral.setMoney2(money2);
			rebateReferral.setUpdatedTime(new Date());
			rebateReferralDao.update(rebateReferral);
			rebateReferralDao.commit();
		} else {
			throw new RuntimeException("未找到指定对象！");
		}
	}

	@CheckRole(role = {Role.OperationsManager})
	@Override
	public void updateVipGrade(int rebateVipGradeId, double money, HttpSession session) {
		RebateVipGrades rebateVipGrade = (RebateVipGrades)rebateVipGradeDao.getById(rebateVipGradeId);
		if (rebateVipGrade != null) {
			rebateVipGrade.setMoney(money);
			rebateVipGrade.setUpdatedTime(new Date());
			rebateVipGradeDao.update(rebateVipGrade);
			rebateVipGradeDao.commit();
		} else {
			throw new RuntimeException("未找到指定对象！");
		}
	}

	@CheckRole(role = {Role.OperationsManager})
	@Override
	public void updateVipGradeMt4Group(int rebateVipGradeMt4GroupId, double money, HttpSession session) {
		RebateVipGradeMt4Groups rebateVipGradeMt4Group = (RebateVipGradeMt4Groups)rebateVipGradeMt4GroupDao.getById(rebateVipGradeMt4GroupId);
		if (rebateVipGradeMt4Group != null) {
			rebateVipGradeMt4Group.setMoney(money);
			rebateVipGradeMt4Group.setUpdatedTime(new Date());
			rebateVipGradeMt4GroupDao.update(rebateVipGradeMt4Group);
			rebateVipGradeMt4GroupDao.commit();
		} else {
			throw new RuntimeException("未找到指定对象！");
		}
	}

	@CheckRole(role = {Role.OperationsManager})
	@Override
	public List<RebateAgents> getRebateAgentList(int userId, HttpSession session) {
		Users user = (Users)userDao.getById(userId);
		if (user == null) {
			throw new RuntimeException("未找到代理！");
		}
		
		return rebateAgentDao.getList(user);
	}
	
}
