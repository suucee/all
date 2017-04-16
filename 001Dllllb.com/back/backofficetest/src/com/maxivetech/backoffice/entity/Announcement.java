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

@Entity
@Table(name = "announcement")
public class Announcement implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(name = "title", nullable = true)
	private String title;
	
	@Column(name = "content", nullable = true, columnDefinition="longtext")
	private String content;
	
	//0和置顶等效，1-10作为序号
	@Column(name = "sort", nullable = false, columnDefinition="int")
	private int sort = 10;
	
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modify_time", nullable = false)
	private Date modifyTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "publish_time", nullable = true)
	private Date publishTime = null;
	
	
	@Column(name = "top", nullable = false, columnDefinition="bit(1) DEFAULT 0 COMMENT '是否置顶：1=true=置顶，0=false=不置顶' ")
	private boolean top = false;
	
	@Column(name = "display", nullable = false,columnDefinition="bit(1)  DEFAULT 1")
	private boolean display = true;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "admins_id",  nullable = true)
	private Admins admins;
	
	@Transient
	private String adminsShowName;
	
	@Transient
	private String adminsRole;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date creatTime) {
		this.createTime = creatTime;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public boolean isTop() {
		return top;
	}

	public void setTop(boolean top) {
		this.top = top;
	}


	public Admins getAdmins() {
		return admins;
	}

	public void setAdmins(Admins admins) {
		this.admins = admins;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getAdminsShowName() {
		return adminsShowName;
	}

	public void setAdminsShowName(String adminsShowName) {
		this.adminsShowName = adminsShowName;
	}

	public String getAdminsRole() {
		return adminsRole;
	}

	public void setAdminsRole(String adminsRole) {
		this.adminsRole = adminsRole;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	
	
	
}
