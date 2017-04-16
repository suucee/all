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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author Rui.Xu
 * 
 */
@Entity
@Table(name = "columns")
public class Columns implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name = "id", nullable = false)
	private Archives archive;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = { CascadeType.ALL })
	@OrderBy("sortNum")
	private List<Columns> children;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "up_id", nullable = true)
	private Columns parent = null;

	@Column(name = "alias", length = 100, nullable = true)
	private String alias = "";

	@Column(name = "name", length = 50, nullable = false)
	private String name = "";

	@Column(name = "content_type", length = 50, nullable = false)
	private String contentType = "";

	@Column(name = "sort_num", nullable = false)
	private int sortNum = 50;

	/*
	 * @Column(name = "action", length = 100, nullable = false) private String
	 * action = "";
	 */

	@Column(name = "channe_template", length = 100, nullable = false)
	private String channelTemplate = "";

	@Column(name = "list_template", length = 100, nullable = false)
	private String listTemplate = "";

	@Column(name = "content_template", length = 100, nullable = false)
	private String contentTemplate = "";

	@Column(name = "url", length = 200, nullable = false)
	private String url = "";

	@Column(name = "body", nullable = false)
	private String body = "";

	@Column(name = "seo_title", length = 80, nullable = false)
	private String seoTitle = "";

	@Column(name = "seo_keywords", length = 200, nullable = false)
	private String seoKeywords = "";

	@Column(name = "seo_description", length = 200, nullable = false)
	private String seoDescription = "";

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creat_time", nullable = false)
	private Date creatTime;
	
	//是否可删
	@Column(name = "state", nullable = false)
	private boolean state;
	@Transient
	private int _parentId;
	
	public Archives getArchive() {
		return archive;
	}

	public void setArchive(Archives archive) {
		this.archive = archive;
	}

	public List<Columns> getChildren() {
		return children;
	}

	public void setChildren(List<Columns> children) {
		this.children = children;
	}

	public Columns getParent() {
		return parent;
	}

	public void setParent(Columns parent) {
		this.parent = parent;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public int getSortNum() {
		return sortNum;
	}

	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}

	/*
	 * public String getAction() { return action; }
	 * 
	 * 
	 * 
	 * public void setAction(String action) { this.action = action; }
	 */

	public String getUrl() {
		if (!url.equals("")) {
			return url;
		}else if (channelTemplate=="/news_list") {
			return this.alias+"/page-??.do";
		}
		else if(alias!=null) {
			return this.alias+".do";
		}else {
			return "column-"+this.archive.getId()+".do";
		}

	}

	public void setUrl(String url) {

		this.url = url;
	}

	public String getChannelTemplate() {
		return channelTemplate;
	}

	public void setChannelTemplate(String channelTemplate) {
		this.channelTemplate = channelTemplate;
	}

	public String getListTemplate() {
		return listTemplate;
	}

	public void setListTemplate(String listTemplate) {
		this.listTemplate = listTemplate;
	}

	public String getContentTemplate() {
		return contentTemplate;
	}

	public void setContentTemplate(String contentTemplate) {
		this.contentTemplate = contentTemplate;
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

	public int get_parentId() {
		return _parentId;
	}

	public void set_parentId(int _parentId) {
		this._parentId = _parentId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
