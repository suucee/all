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

@Entity
@Table(name = "transfers")
public class Transfers implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false, length = 16)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Users user;

	@Column(name = "from_login", nullable = false)
	private int fromLogin;

	@Column(name = "to_login", nullable = false)
	private int toLogin;

	@Column(name = "amount", nullable = false)
	private double amount;

	@Column(name = "currency_type", nullable = false, length = 16)
	private String currencyType;

	@Column(name = "create_time", nullable = false)
	private Date creatTime;

	
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


	public int getFromLogin() {
		return fromLogin;
	}


	public void setFromLogin(int fromLogin) {
		this.fromLogin = fromLogin;
	}


	public int getToLogin() {
		return toLogin;
	}


	public void setToLogin(int toLogin) {
		this.toLogin = toLogin;
	}


	public double getAmount() {
		return amount;
	}


	public void setAmount(double amount) {
		this.amount = amount;
	}


	public String getCurrencyType() {
		return currencyType;
	}


	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
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

}
