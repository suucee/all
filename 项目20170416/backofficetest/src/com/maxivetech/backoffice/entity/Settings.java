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
import org.hibernate.annotations.UpdateTimestamp;

/**
 * @author Rui.Xu
 *
 */
@Entity
@Table(name = "settings")
public class Settings implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "`key`", length = 50, unique = true, nullable = false)
	private String key;

	@Column(name = "name", length = 20, nullable = false)
	private String name;
	
	@Column(name = "groups", length = 50, nullable = false)
	private String groups;

	@Column(name = "type", length = 20, nullable = false)
	private String type;

	@Column(name = "string_value", length = 200, nullable = false)
	private String stringValue;
	
	@Column(name = "int_value", nullable = false)
	private int intValue;
	
	@Column(name = "double_value", nullable = false)
	private double doubleValue;
	
	@Column(name = "description", length = 100, nullable = false)
	private String description;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_time")
	private Date updatedTime;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroups() {
		return groups;
	}

	public void setGroup(String groups) {
		this.groups = groups;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	public double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(double doubleValue) {
		this.doubleValue = doubleValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
