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
@Table(name = "users_simulation")
public class UsersSimulation implements Serializable {
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
	
	

	@Column(name = "email", length = 50, nullable = false, unique = true)
	private String email;
	@Column(name = "mobile", length = 20, nullable = false, unique = true)
	private String mobile;
	
	@Column(name = "password", length = 32, nullable = false)
	private String password;
	
	@Column(name = "salty", length = 6, nullable = false)
	private String salty;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "registration_time", nullable = false)
	private Date registrationTime;



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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalty() {
		return salty;
	}

	public void setSalty(String salty) {
		this.salty = salty;
	}

	public Date getRegistrationTime() {
		return registrationTime;
	}

	public void setRegistrationTime(Date registrationTime) {
		this.registrationTime = registrationTime;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
