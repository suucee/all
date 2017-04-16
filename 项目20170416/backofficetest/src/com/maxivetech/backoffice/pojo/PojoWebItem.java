package com.maxivetech.backoffice.pojo;

import java.util.Date;

public class PojoWebItem {
	private String  link;
	private String  content;
	private Date   countTime;
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCountTime() {
		return countTime;
	}
	public void setCountTime(Date countTime) {
		this.countTime = countTime;
	}
}
