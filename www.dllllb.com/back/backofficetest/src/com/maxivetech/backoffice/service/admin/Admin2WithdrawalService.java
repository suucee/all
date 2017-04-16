package com.maxivetech.backoffice.service.admin;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.entity.Withdrawals;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.pojo.PojoSum;
import com.maxivetech.backoffice.util.Page;


@Service
public interface Admin2WithdrawalService {
//	public Page<Withdrawals> getPage(int userId,int pageNo, int pageSize, String urlFormat,
//			String scheme, String start, String end, HttpSession session);
	
	public List<PojoCounts> getCounts(HttpSession session);
	
	public Withdrawals getOne(int id, HttpSession session);
	
	
	public String changeState(int id,String nowState,String operationPassword, String state, String memo, HttpSession session);
	/**
	 * 财务获取出金概述
	 * @param session
	 * @return
	 */
//	public List<PojoSum> getSummarizes(HttpSession session);
	
	Page<Withdrawals> getPage(int userId, int pageNo, int pageSize, String urlFormat, String scheme, String start,
			String end, String keyword, HttpSession session);
	/**
	 * 财务获取出金概述
	 * @param startDate 可以指定开始时间
	 * @param endDate 可以指定结束时间
	 * @param session
	 * @return
	 */
	List<PojoSum> getSummarizes(String startDate, String endDate, HttpSession session);
}
