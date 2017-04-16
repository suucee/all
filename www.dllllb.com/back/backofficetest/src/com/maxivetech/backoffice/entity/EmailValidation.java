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
import javax.persistence.Transient;

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
@Table(name = "email_validation")
public class EmailValidation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	/**
	 * UNVERIFIED	未认证
	 * AUDITING		审核中
	 * VERIFIED		认证已通示
	 * REJECTED		认证未通过
	 */
	@Column(name = "state", nullable = false)
	private String state;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Users user;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "email_time", nullable = true)
	private Date emailTime = null;
	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pass_email_time", nullable = true)
	private Date passEmailTime = null;


	@Column(name = "email_num", length = 36, nullable = true)
	private String emailNum;


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public Users getUser() {
		return user;
	}


	public void setUser(Users user) {
		this.user = user;
	}


	public Date getEmailTime() {
		return emailTime;
	}


	public void setEmailTime(Date emailTime) {
		this.emailTime = emailTime;
	}


	public Date getPassEmailTime() {
		return passEmailTime;
	}


	public void setPassEmailTime(Date passEmailTime) {
		this.passEmailTime = passEmailTime;
	}


	public String getEmailNum() {
		return emailNum;
	}


	public void setEmailNum(String emailNum) {
		this.emailNum = emailNum;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

}
