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
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * @author Rui.Xu
 *
 */
@Entity
@Table(name = "rebate_referrals")
public class RebateReferrals implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rebate_id", nullable = false)
	private Rebates rebate;

	@Column(name = "money1", nullable = false)
	private double money1;
	
	@Column(name = "money2", nullable = false)
	private double money2;
	
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



	public Rebates getRebate() {
		return rebate;
	}

	public void setRebate(Rebates rebate) {
		this.rebate = rebate;
	}


	public double getMoney1() {
		return money1;
	}

	public void setMoney1(double money1) {
		this.money1 = money1;
	}

	public double getMoney2() {
		return money2;
	}

	public void setMoney2(double money2) {
		this.money2 = money2;
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
