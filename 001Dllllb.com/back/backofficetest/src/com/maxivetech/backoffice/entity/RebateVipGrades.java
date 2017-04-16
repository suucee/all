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
@Table(name = "rebate_vip_grades")
public class RebateVipGrades implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rebate_id", nullable = false)
	private Rebates rebate;

	@Column(name = "vip_grade", nullable = false)
	private int vipGrade;

	@Column(name = "money", nullable = false)
	private double money;
	
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_time", nullable = false)
	private Date updatedTime;

	@Transient
	private String _vipGradeName = "";
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}



	public int getVipGrade() {
		return vipGrade;
	}

	public void setVipGrade(int vipGrade) {
		this.vipGrade = vipGrade;
	}

	public Rebates getRebate() {
		return rebate;
	}

	public void setRebate(Rebates rebate) {
		this.rebate = rebate;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String get_vipGradeName() {
		return _vipGradeName;
	}

	public void set_vipGradeName(String _vipGradeName) {
		this._vipGradeName = _vipGradeName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
}
