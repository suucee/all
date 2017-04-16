/**
 * 
 */
package com.maxivetech.backoffice;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * @author Rui.Xu
 * 
 */
@Entity
@Table(name = "users")
public class Users implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String ownerType="agentAgreement";
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	/**
	 * UNVERIFIED 未认证 AUDITING 审核中 VERIFIED 认证已通示  REJECTED 认证未通过
	 */
	@Column(name = "state", nullable = false)
	private String state;

	@Transient
	private String _name = "";
	@Transient
	private String _vipGradeName = "";
	@Transient
	private int _up_id;
	
	public int get_up_id() {
		return _up_id;
	}


	public void set_up_id(int _up_id) {
		this._up_id = _up_id;
	}


	public String get_name() {
		return _name;
	}


	public void set_name(String _name) {
		this._name = _name;
	}


	public String get_vipGradeName() {
		return _vipGradeName;
	}


	public void set_vipGradeName(String _vipGradeName) {
		this._vipGradeName = _vipGradeName;
	}


	//状态：是否冻结;
	@Column(name = "is_frozen", nullable = false)
	private boolean isFrozen = false;
	
	@Column(name = "email", length = 50, nullable = false, unique = false)
	private String email;
	
	@Column(name = "mobile", length = 20, nullable = false, unique = true)
	private String mobile;
	
	@Column(name = " school", length = 100, nullable = false, unique = true)
	private String school;
	
	//校区
	@Column(name = " campus", length = 100, nullable = false, unique = true)
	private String campus;

	@Column(name = "password", length = 32, nullable = false)
	private String password;

	@Column(name = "payment_password", length = 32, nullable = false)
	private String paymentPassword;
	
	//用户盐值
	@Column(name = "salty", length = 6, nullable = false)
	private String salty;	
	
	@Column(name = "path", length = 1000, nullable = true)
	private String path;
   
	
	//用户注册时间
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "registration_time", nullable = false)
	private Date registrationTime;
	
	//最后登录时间
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_logon_time", nullable = true)
	private Date lastLogonTime = null;

	//用户上级
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "up_id", nullable = true)
	private Users parent;

	@Column(name = "disable", nullable = false)
	private boolean disable = false;
	
	/**
	 * 是否是由于输错密码被锁定_backoffice2.0 新增
	 */
	@Column(name = "pwd_locked",nullable = false,columnDefinition=" bit(1) default 0 comment'是否是由于输错密码被锁定'")
	private boolean pwdLocked = false;
	
	/**
	 * 输错密码次数_backoffice2.0 新增
	 */
	@Column(name = "pwd_error_count",nullable = false,columnDefinition="int default 0 comment'输错密码次数' ")
	private int pwdErrorCount = 0;
	
	/**
	 * 重新计时后第一次输错密码时间_backoffice2.0 新增
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pwd_fisrt_error_time",nullable = true)
	private Date pwdFirstErrorTime = new Date();
	
	@Column(name = "referral_code", unique = true, length = 5, nullable = true)
	private String referralCode;
	
	@Column(name = "serial_number", length = 50, nullable = false)
	private int serialNumber;

	@Column(name = "level", nullable = false)
	private int level;
	
	@Column(name = "vip_grade", nullable = false)
	private int vipGrade = 0;
	
	
	
	public String getReferralCode() {
		return referralCode;
	}


	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
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


	public static String getOwnertype() {
		return ownerType;
	}


	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public boolean isFrozen() {
		return isFrozen;
	}

	public void setFrozen(boolean isFrozen) {
		this.isFrozen = isFrozen;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPaymentPassword() {
		return paymentPassword;
	}

	public void setPaymentPassword(String paymentPassword) {
		this.paymentPassword = paymentPassword;
	}

	public String getSalty() {
		return salty;
	}

	public void setSalty(String salty) {
		this.salty = salty;
	}

	public Date getRegistrationTime() {
		return registrationTime;
	}

	public void setRegistrationTime(Date registrationTime) {
		this.registrationTime = registrationTime;
	}

	public Date getLastLogonTime() {
		return lastLogonTime;
	}

	public void setLastLogonTime(Date lastLogonTime) {
		this.lastLogonTime = lastLogonTime;
	}

	public Users getParent() {
		return parent;
	}

	public void setParent(Users parent) {
		this.parent = parent;
	}

	public int getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}


	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean isPwdLocked() {
		return pwdLocked;
	}

	public void setPwdLocked(boolean pwdLocked) {
		this.pwdLocked = pwdLocked;
	}

	public int getPwdErrorCount() {
		return pwdErrorCount;
	}

	public void setPwdErrorCount(int pwdErrorCount) {
		this.pwdErrorCount = pwdErrorCount;
	}

	public Date getPwdFirstErrorTime() {
		return pwdFirstErrorTime;
	}

	public void setPwdFirstErrorTime(Date pwdFirstErrorTime) {
		this.pwdFirstErrorTime = pwdFirstErrorTime;
	}


	public String getSchool() {
		return school;
	}


	public void setSchool(String school) {
		this.school = school;
	}


	public String getCampus() {
		return campus;
	}


	public void setCampus(String campus) {
		this.campus = campus;
	}


	public boolean isDisable() {
		return disable;
	}


	public void setDisable(boolean disable) {
		this.disable = disable;
	}



}
