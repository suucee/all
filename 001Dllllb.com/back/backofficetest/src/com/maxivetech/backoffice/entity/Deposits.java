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

@Entity
@Table(name = "deposits")
public class Deposits implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", unique = true, nullable = false)
	private Users user;
	
	@Column(name = "order_no", nullable = false)
	private String orderNum;
	
	@Column(name = "amount", nullable = false)
	private double amount;
	
	@Column(name = "currency", length = 3, nullable = false)
	private String currency; 
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creat_time", nullable = false)
	private Date creatTime;
	
	//DEPOSITED  已到账
	//PENDING_PAY 待付款
	//PENDING_AUDIT 待审核
	//PENDING_SUPERVISOR 待财务主管审核
	//ACCEPTED 已审核
	//REJECTED 拒绝
	@Column(name = "state")
	private String state;
	
	@Column(name = "user_comment",length=1000,nullable=true)
	private String userComment;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "payment_time")
	private Date paymentTime;
	
	@Column(name = "audited_memo")
	private String auditedMemo;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "audited_time")
	private Date auditedTime;
	
	@Transient
	private int _userId;

	@Transient
	private String _userEmail;
	@Transient
	private String _userName;

	public String getUserComment() {
		return userComment;
	}


	public void setUserComment(String userComment) {
		this.userComment = userComment;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}

	public Users getUser() {
		return user;
	}


	public void setUser(Users user) {
		this.user = user;
	}




	public String getCurrency() {
		return currency;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public String getOrderNum() {
		return orderNum;
	}




	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}




	public double getAmount() {
		return amount;
	}




	public void setAmount(double amount) {
		this.amount = amount;
	}




	public Date getCreatTime() {
		return creatTime;
	}




	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}




	public String getState() {
		return state;
	}




	public void setState(String state) {
		this.state = state;
	}




	public Date getPaymentTime() {
		return paymentTime;
	}




	public void setPaymentTime(Date paymentTime) {
		this.paymentTime = paymentTime;
	}



	public String getAuditedMemo() {
		return auditedMemo;
	}




	public void setAuditedMemo(String auditedMemo) {
		this.auditedMemo = auditedMemo;
	}




	public Date getAuditedTime() {
		return auditedTime;
	}




	public void setAuditedTime(Date auditedTime) {
		this.auditedTime = auditedTime;
	}




	public int get_userId() {
		return _userId;
	}


	public void set_userId(int _userId) {
		this._userId = _userId;
	}


	public String get_userEmail() {
		return _userEmail;
	}


	public void set_userEmail(String _userEmail) {
		this._userEmail = _userEmail;
	}


	public String get_userName() {
		return _userName;
	}


	public void set_userName(String _userName) {
		this._userName = _userName;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
