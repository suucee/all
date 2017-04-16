package com.maxivetech.backoffice.service.user;

import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.entity.UserBalanceLogs;
import com.maxivetech.backoffice.entity.UserBalances;
import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.entity.Attachments;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.Page;


@Service
public interface UserAccountService {
	/**
	 * 获取汇出中的金额总和
	 * @param currencyType
	 * @param session
	 * @return
	 */
	public double getWithdrawalsSum(String currencyType, HttpSession session);
	
	/**
	 * 获取余额列表
	 * @param session
	 * @return
	 */
	public List<UserBalances> getBalanceList(HttpSession session);
	/**
	 * 获取余额日志
	 * @param pageNo
	 * @param pageSize
	 * @param urlFormat
	 * @param session
	 * @return
	 */
	public Page<UserBalanceLogs> getBalanceLogs(int pageNo, int pageSize, String urlFormat, 
			String start, String end, HttpSession session);

	/**
	 * 获取银行账户列表
	 * @param session
	 * @return
	 */
	public List<UserBankAccounts> getBankAccountList(HttpSession session);
	/**
	 * 获取银行账户
	 * @param bankAccountId
	 * @param session
	 * @return
	 */
	public UserBankAccounts getBankAccount(int bankAccountId, HttpSession session);
	/**
	 * 删除银行账户
	 * @param id
	 * @param session
	 * @return
	 */
	public boolean deleteBankAccount(int bankAccountId, HttpSession session)
		throws ForbiddenException;

	/**
	 * 添加銀行賬戶
	 * @param bankName
	 * @param accountName
	 * @param accountNo
	 * @param currencyType
	 * @param country
	 * @param iban
	 * @param swiftCode
	 * @param sortingCode
	 * @param session
	 * @return
	 */
	public boolean addBankAccount(String bankName, String accountName,
			String accountNo, String countryCode,String swiftCode,String ibanCode,String bankBranch,String bankAddress,String attachment_id,HttpSession session)
			throws ForbiddenException;
	
	/**
	 * 修改銀行帳戶
	 * @param bankAccountId
	 * @param bankName
	 * @param accountName
	 * @param accountNo
	 * @param currencyType
	 * @param countryCode
	 * @param iban
	 * @param bicSwiftCode
	 * @param sortingCode
	 * @param session
	 * @return
	 */
	public boolean editBankAccount(int bankAccountId, String bankName,
			String accountName, String accountNo,
			String countryCode,String swiftCode,String ibanCode,String bankBranch,String bankAddress, HttpSession session)
			throws ForbiddenException;
	
	/**
	 * 从MT4服务器更新我的MT4账号信息
	 * @param session
	 */
	public void refreshMT4Users(HttpSession session);
	/**
	 * 获取所选转出账号的余额
	 * @param login
	 * @param session
	 */
	public double getMt4Balance(int login, HttpSession session);
    
	
	/**
	 * 获取所有图片
	 * @param id
	 * @param session
	 * @return
	 */
	public List<Attachments>  getBankImageList(int id,HttpSession session);
    /**
     * 删除银行卡图片
     * @param id
     * @param session
     * @return
     */
	public boolean deleteBankImage(int id, HttpSession session);
}
