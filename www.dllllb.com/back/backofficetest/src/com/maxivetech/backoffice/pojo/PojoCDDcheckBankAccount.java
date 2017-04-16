package com.maxivetech.backoffice.pojo;

import java.util.Date;

public class PojoCDDcheckBankAccount {
	private int cddcheckId;
	private int userBankAccountId;
	private String result;
	private String  accountName;
	private String  accountNo;
	private String  countryCode;
	private String bankName;
	private int userId;
	private String state;
	private boolean  isDefault;
	private String  name;
	private Date checkTime;
	private String adminName;
	private String path;
	public int getUserBankAccountId() {
		return userBankAccountId;
	}
	public void setUserBankAccountId(int userBankAccountId) {
		this.userBankAccountId = userBankAccountId;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Date getCheckTime() {
		return checkTime;
	}
	public void setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
	}
	public int getCddcheckId() {
		return cddcheckId;
	}
	public void setCddcheckId(int cddcheckId) {
		this.cddcheckId = cddcheckId;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getAdminName() {
		return adminName;
	}
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}


	
}
