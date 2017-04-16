/**
 * 
 */
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

import org.hibernate.annotations.UpdateTimestamp;



/**
 * @author dane
 *
 */
@Entity
@Table(name = "user__balances")
public class UserBalances implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Users user;
	
	@Column(name = "currency_type", length = 3, nullable = false)
	private String currencyType;

	@Column(name = "amount_available", nullable = false)
	private double amountAvailable = 0;
	
	@Column(name = "amount_frozen", nullable = false)
	private double amountFrozen = 0;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_time", nullable = false)
	private Date updatedTime;

	
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

	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	public double getAmountAvailable() {
		return amountAvailable;
	}

	public void setAmountAvailable(double amountAvailable) {
		this.amountAvailable = amountAvailable;
	}

	public double getAmountFrozen() {
		return amountFrozen;
	}

	public void setAmountFrozen(double amountFrozen) {
		this.amountFrozen = amountFrozen;
	}


	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
