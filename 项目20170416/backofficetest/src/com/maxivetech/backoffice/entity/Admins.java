/**
 * 
 */
package com.maxivetech.backoffice.entity;

import java.io.Serializable;
import java.sql.Timestamp;
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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author Rui.Xu
 *
 */
@Entity
@Table(name = "admins")
public class Admins implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	/**
	 * ComplianceOfficer	合规人员
	 * FinancialStaff		财务
	 * FinancialSuperior	财务主管
	 * CustomerServiceStaff	客服
	 * Webmaster			网站管理员
	 * OperationsManager 	运维经理
	 * RiskManagementCommissioner	风控专员
	 */
	@Column(name = "role", length = 50, nullable = false)
	private String role;

	@Column(name = "is_disabled", nullable = false)
	private boolean isDisabled;

	@Column(name = "account", length = 50, nullable = false)
	private String account;

	@Column(name = "password", length = 32, nullable = false)
	private String password;

	@Column(name = "operation_password", length = 32, nullable = false)
	private String operationPassword;

	@Column(name = "salty", length = 6, nullable = false)
	private String salty;

	@Column(name = "show_name", length = 50, nullable = false)
	private String showName;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creat_time", nullable = false)
	private Date creatTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_logon_time", nullable = true)
	private Date lastLogonTime = null;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Date getLastLogonTime() {
		return lastLogonTime;
	}

	public void setLastLogonTime(Date lastLogonTime) {
		this.lastLogonTime = lastLogonTime;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOperationPassword() {
		return operationPassword;
	}

	public void setOperationPassword(String operationPassword) {
		this.operationPassword = operationPassword;
	}

	public String getSalty() {
		return salty;
	}

	public void setSalty(String salty) {
		this.salty = salty;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public Date getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean isDisabled() {
		return isDisabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

}
