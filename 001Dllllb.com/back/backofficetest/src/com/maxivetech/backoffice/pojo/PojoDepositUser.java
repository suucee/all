package com.maxivetech.backoffice.pojo;

public class PojoDepositUser {
	
	private int userId;
	private String username;
	private String email;
	private String mobile;
	private String idcard;
	
	
	public PojoDepositUser() {
		super();
	}
	public PojoDepositUser(int userId,String username, String email, String mobile, String idcard) {
		super();
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.mobile = mobile;
		this.idcard = idcard;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
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
	public String getIdcard() {
		return idcard;
	}
	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}
	
	
}
