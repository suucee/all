package com.maxivetech.backoffice.pojo;

import com.maxivetech.backoffice.entity.Users;

public class PojoUserBalances {
	private int userId;
	private int id;
	private String userName;
	private String userEmail;
	private String userMobile;
	private String vipGradeShowName;
	private int vipGrade;
	private int level;
	private double amountAvailable;
	private double amountFrozen;
	private double amount;
	private String month;
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getUserMobile() {
		return userMobile;
	}
	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}
	public String getVipGradeShowName() {
		return vipGradeShowName;
	}
	public void setVipGradeShowName(String vipGradeShowName) {
		this.vipGradeShowName = vipGradeShowName;
	}
	public int getVipGrade() {
		return vipGrade;
	}
	public void setVipGrade(int vipGrade) {
		this.vipGrade = vipGrade;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public double getAmountAvailable() {
		return amountAvailable;
	}
	public void setAmountAvailable(double amountAvailable) {
		this.amountAvailable = amountAvailable;
	}
	public double getAmountFrozen() {
		return amountFrozen;
	}
	public void setAmountFrozen(double amountFrozen) {
		this.amountFrozen = amountFrozen;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
}
