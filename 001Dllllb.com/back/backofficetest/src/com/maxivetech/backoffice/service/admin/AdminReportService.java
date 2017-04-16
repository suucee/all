package com.maxivetech.backoffice.service.admin;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.util.Page;




@Service
public interface AdminReportService {
	/**
	 * 获取用户实时余额列表
	 * @param pageNo
	 * @param pageSize
	 * @param urlFormat
	 * @param session
	 * @return
	 */
	public  Page<HashMap<String, Object>> getBalancePage(int pageNo, int pageSize, String urlFormat,
			HttpSession session);
	/**
	 * 获取用户历史余额列表
	 * @param pageNo
	 * @param pageSize
	 * @param urlFormat
	 * @param session
	 * @return
	 */
	public  Page<HashMap<String, Object>> getBalanceLogPage(int pageNo, int pageSize, String urlFormat,
			String start, HttpSession session);
	/**
	 * 获取用户入金列表
	 * @param pageNo
	 * @param pageSize
	 * @param urlFormat
	 * @param session
	 * @return
	 */
	public  Page<HashMap<String, Object>> getDepositPage(int pageNo, int pageSize, String urlFormat,
			String start, String end, HttpSession session);
	
	/**
	 * 获取用户出金列表
	 * @param pageNo
	 * @param pageSize
	 * @param urlFormat
	 * @param session
	 * @return
	 */
	public  Page<HashMap<String, Object>> getWithdrawalPage(int pageNo, int pageSize, String urlFormat,
			String start, String end, HttpSession session);
	/**
	 * 获取返佣报表
	 * @param pageNo
	 * @param pageSize
	 * @param urlFormat
	 * @param session
	 * @return
	 */
	public  Page<HashMap<String, Object>> getRebatePage(int pageNo, int pageSize, String urlFormat,
			String year, String month, HttpSession session);
	
	
}
