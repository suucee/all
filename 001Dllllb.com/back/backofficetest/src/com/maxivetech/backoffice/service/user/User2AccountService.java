package com.maxivetech.backoffice.service.user;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.util.ForbiddenException;


@Service
public interface User2AccountService {
	
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
	
}
