package com.maxivetech.backoffice.service.user;

import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpSession;

import org.springframework.http.client.support.HttpAccessor;
import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.entity.Withdrawals;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.Page;

@Service
public interface UserWithdrawalService {
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
			int bankAccountId,String payPassword, HttpSession session)
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
}
