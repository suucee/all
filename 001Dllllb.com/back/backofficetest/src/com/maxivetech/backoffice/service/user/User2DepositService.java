package com.maxivetech.backoffice.service.user;


import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.entity.Deposits;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.Page;



@Service
public interface User2DepositService {
	/**
	 * 提交订单
	 * @param currencyType
	 * @param amount
	 * @param remittance
	 * @param userAccountId
	 * @param session
	 * @return
	 */
	public int add(/*String orderNum,*/double amount,HttpSession session)throws ForbiddenException;
	/**
	 * 支付成功修改订单状态
	 * @param currencyType
	 * @param amount
	 * @param remittance
	 * @param userAccountId
	 * @param session
	 * @return
	 */
	public int updatePayState(int id, String orderNum,double amount,HttpSession session)throws ForbiddenException;
	
	/**
	 * 列表
	 * @param pageNo
	 * @param pageSize
	 * @param urlFormat
	 * @param session
	 * @return
	 */
	public Page<Deposits> getPage(int pageNo, int pageSize, String urlFormat, HttpSession session)throws ForbiddenException;
	
	public double getRate(HttpSession session);
	
	/**
	 * 根据时间限制和状态来搜索入金状态
	 * @param pageNo
	 * @param pageSize
	 * @param urlFormat
	 * @param startDate
	 * @param endDate
	 * @param state
	 * @param session
	 * @return
	 */
	Page<Deposits> getPageByTimeAndState(int pageNo, int pageSize, String urlFormat, String startDate, String endDate,
			String state, HttpSession session);
	
	int addWithMT4(String toLogin, double amount, String paymentPassword, HttpSession session)
			throws ForbiddenException;

}
