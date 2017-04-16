package com.maxivetech.backoffice.pojo;

import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Users;

public class PojoSession {

	private int id = 0;
	private String name = "";
	private String role = "";
	private String state = "";
	private String tel = "";
	private String token = "";
	private int level = 0;
	private boolean isFrozen = false;
	private boolean disable = false;
	private int vipGrade = 0;
	private String referralCode = null;
	private int holdAdminId = 0;
	private String redirectUrl = "";

	public PojoSession(Users user) {
		this(user.getId(), user.getEmail(), user.getState(), user.getMobile(), "User", user.getLevel(), user.isFrozen(),
				user.isDisable(), user.getVipGrade(), user.getReferralCode());
	}

	public PojoSession(Admins admin) {
		this(admin.getId(), admin.getShowName(), "", "", admin.getRole(), -1, false, admin.isDisabled(), 0, null);
	}

	public PojoSession(int id, String name, String state, String tel, String role, int level, boolean isFrozen,
			boolean isDisabled, int vipGrade, String referralCode) {
		this.setId(id);
		this.setName(name);
		this.setState(state);
		this.setTel(tel);
		this.setRole(role);
		this.setLevel(level);
		this.setFrozen(isFrozen);
		this.setDisable(isDisabled);
		this.setVipGrade(vipGrade);
		this.setReferralCode(referralCode);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isFrozen() {
		return isFrozen;
	}

	public void setFrozen(boolean isFrozen) {
		this.isFrozen = isFrozen;
	}

	public boolean isDisable() {
		return disable;
	}

	public void setDisable(boolean disable) {
		this.disable = disable;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getVipGrade() {
		return vipGrade;
	}

	public void setVipGrade(int vipGrade) {
		this.vipGrade = vipGrade;
	}

	public String getReferralCode() {
		return referralCode;
	}

	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
	}

	public int getHoldAdminId() {
		return holdAdminId;
	}

	public void setHoldAdminId(int holdAdminId) {
		this.holdAdminId = holdAdminId;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

}
