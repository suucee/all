package com.maxivetech.backoffice.service.user;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.entity.Withdrawals;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.Page;

@Service
public interface User2WithdrawalService {
	/**
	 * 提交
	 * @param currencyType
	 * @param amount
	 * @param remittance
	 * @param bankAccountId
	 * @param session
	 * @return
	 */
	public boolean add(String currencyType, double amount, String remittance,
			int bankAccountId,String payPassword, double rate, HttpSession session)
			throws ForbiddenException;
	
	/**
	 * 列表
	 * @param pageNo
	 * @param pageSize
	 * @param session
	 * @return
	 */
	public Page<Withdrawals> getPage(int pageNo, int pageSize,
			String urlFormat, HttpSession session);
	
	/**
	 * 获取一个
	 * @param id
	 * @param session
	 * @return
	 */
	public Withdrawals getById(int id, HttpSession session);
	
	/**
	 * 取消
	 * @param id
	 * @param session
	 * @return
	 */
	public boolean cancel(int id, HttpSession session)
			throws ForbiddenException;
	/**
	 * 查询下线出金记录
	 * @param userId
	 * @param pageNo
	 * @param pageSize
	 * @param scheme
	 * @param urlFormat
	 * @param session
	 * @return
	 */
	Page<Withdrawals> getPageByUser(int userId, int pageNo, int pageSize,
			String scheme, String urlFormat, HttpSession session);
	

	public double getRate(HttpSession session);

	Page<Withdrawals> getPageByTimeAndState(int pageNo, int pageSize, String urlFormat, String startDate,
			String endDate, String state, HttpSession session);

	/**
	 * 可以由MT4转账到银行卡（1、转到网页；2、网页转到银行卡）
	 * @param currencyType
	 * @param amount
	 * @param userMemo
	 * @param bankAccountId
	 * @param paymentPassword
	 * @param rate
	 * @param fromLogin
	 * @param session
	 * @return
	 * @throws ForbiddenException
	 */
	boolean addWithMt4(String currencyType, double amount, String userMemo, int bankAccountId, String paymentPassword,
			double rate, int fromLogin, HttpSession session) throws ForbiddenException;
}
