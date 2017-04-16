package com.maxivetech.backoffice.service.admin;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.util.Page;




@Service
public interface Admin2ReportService {
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

	
	
}
