package com.maxivetech.backoffice.pojo;

import com.maxivetech.backoffice.entity.Users;

public class PojoRebates {

	private int userId;
	private String email;
	private String mobile;
	private double rebate;
	
	public PojoRebates(Users user, double rebate) {
		this.userId = user.getId();
		this.email = user.getEmail();
		this.mobile = user.getMobile();
		this.rebate = rebate;
	}


	public int getUserId() {
		return userId;
	}


	public void setUserId(int userId) {
		this.userId = userId;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getMobile() {
		return mobile;
	}


	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	public double getRebate() {
		return rebate;
	}


	public void setRebate(double rebate) {
		this.rebate = rebate;
	}
}
