package com.maxivetech.backoffice.pojo;

import com.maxivetech.backoffice.entity.Rebates;

public class PojoVolumes {
	private int userId;
	private String email;
	private String mobile;
	private String name;
	private int vipGrade;
	private int level;
	private String vipGradeName;
	private int rebateId;
	private int volume;
	private int[] volumes;
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getRebateId() {
		return rebateId;
	}
	public void setRebateId(int rebateId) {
		this.rebateId = rebateId;
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
	public String getVipGradeName() {
		return vipGradeName;
	}
	public void setVipGradeName(String vipGradeName) {
		this.vipGradeName = vipGradeName;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	public int[] getVolumes() {
		return volumes;
	}
	public void setVolumes(int[] volumes) {
		this.volumes = volumes;
	}

	
	
}