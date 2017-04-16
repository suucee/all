package com.maxivetech.backoffice.service.user;


import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.entity.Deposits;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.Page;



@Service
public interface UserDepositService {
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

}
