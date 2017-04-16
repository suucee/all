package com.maxivetech.backoffice.pojo;

import com.maxivetech.backoffice.entity.Users;

public class PojoRebateReportUsers {
	private int userId;
	private String userName;
	private String userEmail;
	private String userMobile;
	private String vipGradeShowName;
	private int vipGrade;
	private int level;
	private int volume;
	private int login;
	private double rebate;
	
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
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	public double getRebate() {
		return rebate;
	}
	public void setRebate(double rebate) {
		this.rebate = rebate;
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
	public int getLogin() {
		return login;
	}
	public void setLogin(int login) {
		this.login = login;
	}
	
}
