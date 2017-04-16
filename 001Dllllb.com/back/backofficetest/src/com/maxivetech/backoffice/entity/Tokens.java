/**
 * 
 */
package com.maxivetech.backoffice.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

/**
 * @author Rui.Xu
 *
 */
@Entity
@Table(name = "tokens")
public class Tokens implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "uuid", unique = true, nullable = false)
	private String uuid;

	@Column(name = "ip_address", length = 50, nullable = false)
	private String ipAddress;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = true, unique = true)
	
	private Users user = null;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "admin_id", nullable = true, unique = true)
	private Admins admin = null;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creat_time", nullable = false)
	private Date creatTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_authorization_time", nullable = true)
	private Date lastAuthorizationTime = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expiration_time", nullable = true)
	private Date expirationTime = null;

	public String getUuid() {
		return uuid;
	}



	public void setUuid(String uuid) {
		this.uuid = uuid;
	}



	public String getIpAddress() {
		return ipAddress;
	}



	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}



	public Users getUser() {
		return user;
	}



	public void setUser(Users user) {
		this.user = user;
	}



	public Admins getAdmin() {
		return admin;
	}



	public void setAdmin(Admins admin) {
		this.admin = admin;
	}



	public Date getCreatTime() {
		return creatTime;
	}



	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}



	public Date getLastAuthorizationTime() {
		return lastAuthorizationTime;
	}



	public void setLastAuthorizationTime(Date lastAuthorizationTime) {
		this.lastAuthorizationTime = lastAuthorizationTime;
	}



	public Date getExpirationTime() {
		return expirationTime;
	}



	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
}
