package com.maxivetech.backoffice.service.admin;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.entity.RebateAgents;
import com.maxivetech.backoffice.entity.RebateLevels;
import com.maxivetech.backoffice.entity.RebateRecords;
import com.maxivetech.backoffice.entity.RebateReferrals;
import com.maxivetech.backoffice.entity.RebateVipGradeMt4Groups;
import com.maxivetech.backoffice.entity.RebateVipGrades;
import com.maxivetech.backoffice.entity.Rebates;
import com.maxivetech.backoffice.util.Page;


@Service
public interface AdminRebateSchemeService {
	public List<Rebates> getList(HttpSession session);
	
	public List<RebateLevels> getLevelList(HttpSession session);
	public List<RebateReferrals> getReferralList(HttpSession session);
	public List<RebateVipGrades> getVipGradeList(HttpSession session);
	public List<RebateVipGradeMt4Groups> getVipGradeMt4GroupList(HttpSession session);
	
	public List<RebateAgents> getRebateAgentList(int userId, HttpSession session);
	
	public void updateLevel(int rebateLevelId, double money, HttpSession session);
	public void updateReferral(int rebateReferralId, double money1, double money2, HttpSession session);
	public void updateVipGrade(int vipGradeId, double money, HttpSession session);
	public void updateVipGradeMt4Group(int rebateVipGradeMt4GroupId, double money, HttpSession session);
}
