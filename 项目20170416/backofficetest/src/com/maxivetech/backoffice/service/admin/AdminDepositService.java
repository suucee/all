package com.maxivetech.backoffice.service.admin;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.entity.Deposits;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.pojo.PojoSum;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.Page;



@Service
public interface AdminDepositService {
	/**
	 * 代为入金
	 * @param currencyType
	 * @param amount
	 * @param remittance
	 * @param userAccountId
	 * @param session
	 * @return
	 */
	public String add(String mobile, String orderNum,double amount,String payDate,String userComment,String operationPassword, HttpSession session)throws ForbiddenException;
	
	public HashMap<String, Object> lookupUser(String mobile, HttpSession session);
	
	/**
	 * 列表
	 * @param pageNo
	 * @param pageSize
	 * @param urlFormat
	 * @param session
	 * @return
	 */
	public Page<Deposits> getPage(int userId,int pageNo, int pageSize, String urlFormat,
			String scheme, String start, String end, HttpSession session);
	/**
	 * 获取条数
	 */
	public List<PojoCounts> getCounts(HttpSession session);
	/**
	 * 获取一条代入金记录
	 * @param id
	 * @param session
	 * @return
	 */
	public Deposits getOne(int id, HttpSession session);
	/**
	 * 财务主管审核代入金单
	 * @param id
	 * @param nowState
	 * @param operationPassword
	 * @param state
	 * @param memo
	 * @param session
	 * @return
	 */
	public String changeState(int id,String operationPassword, String state, String memo, HttpSession session);
	/**
	 * 财务入金概述
	 * @param session
	 * @return
	 */
	public List<PojoSum> getSummarizes(HttpSession session);
}
