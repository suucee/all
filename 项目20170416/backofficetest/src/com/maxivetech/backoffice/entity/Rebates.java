/**
 * 
 */
package com.maxivetech.backoffice.entity;

import java.io.Serializable;
import java.sql.Timestamp;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * @author Rui.Xu
 *
 */
@Entity
@Table(name = "rebates")
public class Rebates implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;

	@Column(name = "name", length = 50, unique = true, nullable = false)
	private String name;
	
	@Column(name = "sort_num", nullable = false)
	private int sortNum;

	@Column(name = "sql_where", length = 500, nullable = false)
	private String sqlWhere;

	@Column(name = "description", nullable = false)
	private String description;

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



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public int getSortNum() {
		return sortNum;
	}



	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}



	public String getSqlWhere() {
		return sqlWhere;
	}



	public void setSqlWhere(String sqlWhere) {
		this.sqlWhere = sqlWhere;
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
