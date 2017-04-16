package com.maxivetech.backoffice.service.admin;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.entity.Deposits;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.pojo.PojoDepositUser;
import com.maxivetech.backoffice.pojo.PojoSum;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.Page;



@Service
public interface Admin2DepositService {
	/**
	 * 代为入金
	 */
	String add(int userId, String orderNum, double amount, String payDate, String userComment, String operationPassword,
			HttpSession session) throws ForbiddenException;
	
	public HashMap<String, Object> lookupUser(String mobile, HttpSession session);
	
	/**
	 * 获取记录
	 * @param userId 用户的编号，默认为0
	 * @param pageNo
	 * @param pageSize
	 * @param urlFormat
	 * @param scheme
	 * @param start
	 * @param end
	 * @param keyword
	 * @param replacement 是否是代为入金的记录
	 * @param session
	 * @return
	 */
	public Page<Deposits> getPage(int userId,int pageNo, int pageSize, String urlFormat,
			String scheme, String start, String end, String keyword, boolean replacement, HttpSession session);
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
//	public List<PojoSum> getSummarizes(HttpSession session);

	List<PojoSum> getSummarizes(String startDateParam, String endDateParam, HttpSession session);

	/**
	 * 根据关键字搜索用户
	 * @param keyword关键字，可以是手机号，身份证，姓名，邮箱
	 * @param session
	 * @return
	 */
	List<PojoDepositUser>findUser(String keyword, HttpSession session);

	
}
