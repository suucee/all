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

import com.maxivetech.backoffice.pojo.PojoRebates;

/**
 * @author Rui.Xu
 *
 */
@Entity
@Table(name = "rebate_records")
public class RebateRecords implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Users user;

	@Column(name = "amount", nullable = false)
	private double amount;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creat_time", nullable = false)
	private Date creatTime;

	@Column(name = "comment", length = 200, nullable = false)
	private String comment;

	@Column(name = "mt4_order")
	private int mt4Order;

	@Column(name = "login")
	private int login;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_user_id", nullable = false)
	private Users orderUser;

	@Column(name = "rebate_name", length = 30, nullable = false)
	private String rebateName;

	@Column(name = "volume", nullable = false)
	private int volume = 0;

	@Column(name = "send", nullable = false, columnDefinition = "bit(1) default 0")
	private boolean send = false;

	public boolean isSend() {
		return send;
	}

	public void setSend(boolean send) {
		this.send = send;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getMt4Order() {
		return mt4Order;
	}

	public void setMt4Order(int mt4Order) {
		this.mt4Order = mt4Order;
	}

	public int getLogin() {
		return login;
	}

	public void setLogin(int login) {
		this.login = login;
	}

	public Users getOrderUser() {
		return orderUser;
	}

	public void setOrderUser(Users sourceUser) {
		this.orderUser = sourceUser;
	}

	public String getRebateName() {
		return rebateName;
	}

	public void setRebateName(String rebateName) {
		this.rebateName = rebateName;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
