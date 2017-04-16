package com.maxivetech.backoffice.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
//v2.0创建通知
@Entity
@Table(name = "notify")
public class Notify implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private  static final String BankAccountType="bankAccount";
	private  static final String UserType="user";
	private  static final String WithdrawalType="withdrawal";
	private  static final String DepositType="deposit";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@Column(name = "content", nullable = true, columnDefinition="text")
	private String content;
		
	@Column(name = "notify_type", nullable = true)
	private String notifyType;

	@Column(name = "notify_id", nullable = true)
	private int notifyId;

	@Column(name = "is_read", nullable = false, columnDefinition="bit(1) DEFAULT 0 COMMENT '是否置顶：1=true=置顶，0=false=不置顶' ")
	private boolean read=false;
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", nullable = false)
	private Date createTime;

	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "admins_id",  nullable = true)
	private Admins admins;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "users_id",  nullable = true)
	private Users  users;



	public static String getBankaccounttype() {
		return BankAccountType;
	}

	public static String getUsertype() {
		return UserType;
	}

	public static String getWithdrawaltype() {
		return WithdrawalType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Admins getAdmins() {
		return admins;
	}

	public void setAdmins(Admins admins) {
		this.admins = admins;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public String getNotifyType() {
		return notifyType;
	}

	public void setNotifyType(String notifyType) {
		this.notifyType = notifyType;
	}

	public int getNotifyId() {
		return notifyId;
	}

	public void setNotifyId(int notifyId) {
		this.notifyId = notifyId;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public static String getDeposittype() {
		return DepositType;
	}	
	
	
}
