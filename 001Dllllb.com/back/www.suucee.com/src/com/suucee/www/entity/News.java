package com.suucee.www.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "news")
public class News implements Serializable {

	/**
	 * @author dane
	 * 
	 */

	private static final long serialVersionUID = 1L;

	@Id
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id", nullable = false)
	private Archives archive;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "column_id", nullable = false)
	private Columns column;

	@Column(name = "title", length = 100, nullable = false)
	private String title = "";

	@Column(name = "body", length = 200, nullable = false)
	private String body = "";
	
	@Column(name = "seo_title", length = 80, nullable = false)
	private String seoTitle = "";

	@Column(name = "seo_keywords", length = 200, nullable = false)
	private String seoKeywords = "";

	@Column(name = "seo_description", length = 200, nullable = false)
	private String seoDescription = "";
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creat_time", nullable = false)
	private Date creatTime;

	@Column(name = "sort_num", nullable = false)
	private int sortNum = 50;

	@Column(name = "is_bold", nullable = false)
	private boolean toBold = false;

	@Column(name = "is_top", nullable = false)
	private boolean toTop = false;

	@Column(name = "is_show", nullable = false)
	private boolean toShow = true;

	@Transient
	private String _columnName = "";
	
	@Transient
	private int _columnId = 0;

	public Archives getArchive() {
		return archive;
	}

	public void setArchive(Archives archive) {
		this.archive = archive;
	}

	public Columns getColumn() {
		return column;
	}

	public void setColumn(Columns column) {
		this.column = column;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSeoTitle() {
		return seoTitle;
	}

	public void setSeoTitle(String seoTitle) {
		this.seoTitle = seoTitle;
	}

	public String getSeoKeywords() {
		return seoKeywords;
	}

	public void setSeoKeywords(String seoKeywords) {
		this.seoKeywords = seoKeywords;
	}

	public String getSeoDescription() {
		return seoDescription;
	}

	public void setSeoDescription(String seoDescription) {
		this.seoDescription = seoDescription;
	}

	public Date getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}

	public int getSortNum() {
		return sortNum;
	}

	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}

	public boolean isToBold() {
		return toBold;
	}

	public void setToBold(boolean toBold) {
		this.toBold = toBold;
	}

	public boolean isToTop() {
		return toTop;
	}

	public void setToTop(boolean toTop) {
		this.toTop = toTop;
	}

	public boolean isToShow() {
		return toShow;
	}

	public void setToShow(boolean toShow) {
		this.toShow = toShow;
	}


	public String get_columnName() {
		return _columnName;
	}

	public void set_columnName(String _columnName) {
		this._columnName = _columnName;
	}

	public int get_columnId() {
		return _columnId;
	}

	public void set_columnId(int _columnId) {
		this._columnId = _columnId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
