/**
 * 
 */
package com.maxivetech.backoffice.entity;

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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.map.annotate.JsonSerialize;
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
@Table(name = "user__balance_logs")
public class UserBalanceLogs implements Serializable {
	
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

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creat_time", nullable = false)
	private Date creatTime;
	

	@Column(name = "amount", nullable = false)
	private double amount;

	@Column(name = "description", length = 100, nullable = false)
	private String description;
	
	@Column(name = "amount_available", nullable = false)
	private double amountAvailable;
	
	@Column(name = "amount_frozen", nullable = false)
	private double amountFrozen;

	@Column(name = "deposit_id", nullable = false)
	private int depositId;
	
	@Column(name = "withdrawal_id", nullable = false)
	private int withdrawalId;
	
	@Column(name = "transfers_id", nullable = false)
	private int transfersId;
	
	
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Date getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public int getDepositId() {
		return depositId;
	}

	public void setDepositId(int depositId) {
		this.depositId = depositId;
	}

	public int getWithdrawalId() {
		return withdrawalId;
	}

	public void setWithdrawalId(int withdrawalId) {
		this.withdrawalId = withdrawalId;
	}

	public int getTransfersId() {
		return transfersId;
	}

	public void setTransfersId(int transfersId) {
		this.transfersId = transfersId;
	}

	
}
