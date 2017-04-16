package com.maxivetech.backoffice.pojo;

import java.util.Date;

public class PojoRankUserCensus {
     private Integer userId;
     private Integer upId;
     private Integer vipGrade;
     private Integer level;
     private Double  depositAmount;
     private Double  withdrawalAmount;
     private Double rebateAmount;
     private Double volume;
     private String vipGradeName;
     private String state;
     private String userName;
     private Date registrationTime;
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getUpId() {
		return upId;
	}
	public void setUpId(Integer upId) {
		this.upId = upId;
	}
	public Integer getVipGrade() {
		return vipGrade;
	}
	public void setVipGrade(Integer vipGrade) {
		this.vipGrade = vipGrade;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Double getDepositAmount() {
		return depositAmount;
	}
	public void setDepositAmount(Double depositAmount) {
		this.depositAmount = depositAmount;
	}
	public Double getWithdrawalAmount() {
		return withdrawalAmount;
	}
	public void setWithdrawalAmount(Double withdrawalAmount) {
		this.withdrawalAmount = withdrawalAmount;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getRegistrationTime() {
		return registrationTime;
	}
	public void setRegistrationTime(Date registrationTime) {
		this.registrationTime = registrationTime;
	}
	public String getVipGradeName() {
		return vipGradeName;
	}
	public void setVipGradeName(String vipGradeName) {
		this.vipGradeName = vipGradeName;
	}
	public Double getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(Double rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	public Double getVolume() {
		return volume;
	}
	public void setVolume(Double volume) {
		this.volume = volume;
	}
     
}
