package com.maxivetech.backoffice.service.user;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.util.Page;




@Service
public interface UserReportService {
	/**
	 * 计算用户入金报表
	 * @param pageNo
	 * @param pageSize
	 * @param urlFormat
	 * @param session
	 * @return
	 */
	public  Page<HashMap<String, Object>> getDepositPage(int pageNo, int pageSize, String urlFormat,
			 HttpSession session);
	
	/**
	 * 计算用户出金报表
	 * @param pageNo
	 * @param pageSize
	 * @param urlFormat
	 * @param session
	 * @return
	 */
	public  Page<HashMap<String, Object>> getWithdrawalPage(int pageNo, int pageSize, String urlFormat,
			HttpSession session);
	/**
	 * 获取用户下线入金列表
	 * @param pageNo
	 * @param pageSize
	 * @param urlFormat
	 * @param session
	 * @return
	 */
	public  Page<HashMap<String, Object>> getCustomerDepositPage(int pageNo, int pageSize, String urlFormat,
			String start, String end, HttpSession session);
	
	/**
	 * 获取用户下线出金列表
	 * @param pageNo
	 * @param pageSize
	 * @param urlFormat
	 * @param session
	 * @return
	 */
	public  Page<HashMap<String, Object>> getCustomerWithdrawalPage(int pageNo, int pageSize, String urlFormat,
			String start, String end, HttpSession session);
	
	
	
	
	public HashMap<String, Object> getUserOrder(int pageNo, int pageSize, String urlFormat, String start, String end,HttpSession session); 


	public Page<HashMap<String, Object>> getRebatePage(int pageNo,int pageSize, String urlFormat, String startYear, String startMonth,HttpSession session);
	/**
	 * 计算用户自己返佣总和
	 * @param startYear
	 * @param startMonth
	 * @param session
	 * @return
	 */
	public double getRebateSum(String startYear, String startMonth, HttpSession session);

	public double getDepositSum(HttpSession session);
	
	public double getWithdrawalSum(HttpSession session);

}
