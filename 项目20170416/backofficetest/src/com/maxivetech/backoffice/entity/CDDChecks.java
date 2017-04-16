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

/**
 * @author Rui.Xu
 *
 */
@Entity
@Table(name = "cdd_checks")
public class CDDChecks implements Serializable {
	private static final long serialVersionUID = 1L;
    private static final String ownerType="cddcheck"; 

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "admin_id", nullable = false)
	private Admins admin;

	@Transient
	private int adminId = 0;
	@Transient
	private String adminName = "";

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = true)
	private Users user;

	@Transient
	private int _userId = 0;
	@Transient
	private String _userShowName = "";
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "deposit_id", nullable = true)
	private Deposits deposit;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "withdrawal_id", nullable = true)
	private Withdrawals withdrawal;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_bank_account_id", nullable = true)
	private UserBankAccounts userBankAccounts;
	
	//PENDING
	//ACCEPTED
	//REJECTED
	@Column(name = "result", length = 20, nullable = false)
	private String result;
    
	
	@Column(name = "tag", length = 20, nullable = false)
	private String tag;

	@Column(name = "comment", nullable = false)
	private String comment;
	
	@Column(name = "snapshot", nullable = false)
	private String snapshot;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "timestamp", nullable = false)
	private Date timestamp;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "reminder_timestamp", nullable = true)
	private Date reminderTimestamp = null;

	@Column(name = "url", length = 200, nullable = false)
	private String url;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public Admins getAdmin() {
		return admin;
	}

	public void setAdmin(Admins admin) {
		this.admin = admin;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public Deposits getDeposit() {
		return deposit;
	}

	public void setDeposit(Deposits deposit) {
		this.deposit = deposit;
	}

	
	public Withdrawals getWithdrawal() {
		return withdrawal;
	}

	public void setWithdrawal(Withdrawals withdrawal) {
		this.withdrawal = withdrawal;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(String snapshot) {
		this.snapshot = snapshot;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Date getReminderTimestamp() {
		return reminderTimestamp;
	}

	public void setReminderTimestamp(Date reminderTimestamp) {
		this.reminderTimestamp = reminderTimestamp;
	}

	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}



	public int get_userId() {
		return _userId;
	}

	public void set_userId(int _userId) {
		this._userId = _userId;
	}

	public String get_userShowName() {
		return _userShowName;
	}

	public void set_userShowName(String _userShowName) {
		this._userShowName = _userShowName;
	}

	public UserBankAccounts getUserBankAccounts() {
		return userBankAccounts;
	}

	public void setUserBankAccounts(UserBankAccounts userBankAccounts) {
		this.userBankAccounts = userBankAccounts;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public static String getOwnertype() {
		return ownerType;
	}
	
	
	
}
