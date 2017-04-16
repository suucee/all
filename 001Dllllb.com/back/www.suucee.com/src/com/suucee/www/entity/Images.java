/**
 * 
 */
package com.suucee.www.entity;

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
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;


/**
 * @author Rui.Xu
 *
 */
@Entity
@Table(name = "images")
public class Images implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "archive_id", nullable = true)
	private Archives archive;

	@Column(name = "type", length = 50, nullable = false)
	private String type;
	@Column(name = "name", length = 50, nullable = false)
	private String name;
	@Column(name = "path", length = 200, nullable = false)
	private String path;
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creat_time", nullable = false)
	private Date creatTime;
	
	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public Archives getArchive() {
		return archive;
	}



	public void setArchive(Archives archive) {
		this.archive = archive;
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getPath() {
		return path;
	}



	public void setPath(String path) {
		this.path = path;
	}



	public Date getCreatTime() {
		return creatTime;
	}



	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
