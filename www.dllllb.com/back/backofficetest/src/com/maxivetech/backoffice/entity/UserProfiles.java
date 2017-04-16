/**
 * 
 */
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

/**
 * @author dane
 * 
 */
@Entity
@Table(name = "user__profiles")
public class UserProfiles implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	private static final String ownerType="user";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private Users user;

	@Column(name = "name", length = 100, nullable = false)
	private String userName;
	// 英文名
	@Column(name = "ename", length = 100, nullable = true)
	private String userEName;
	
	@Column(name = "create_time", length = 100, nullable = true)
	private Date creatTime;
	
	@Column(name = "update_time", length = 100, nullable = true)
	private Date updatedTime;
	
	// 卡号类型
	@Column(name = "card_type", length = 100, nullable = false)
	private String cardType;
	
	@Column(name = "idcard", length = 100, nullable = false)
	private String userIdCard;
	// 居住地址
	@Column(name = "esidentialaddress", length = 100, nullable = false)
	private String userEsidentialAddress;
	// 国籍
	@Column(name = "nationality", length = 100, nullable = false)
	private String userNationality;
	// 公司名称
	@Column(name = "company", length = 100, nullable = true)
	private String company;
	// 所属行业
	@Column(name = "industry", length = 100, nullable = true)
	private String userIndustry;
	// 所属行业
	@Column(name = "`position`", length = 100, nullable = true)
	private String position;
	// 年收入
	@Column(name = "years_income", length = 100, nullable = true)
	private String userYearsIncom;
	
	//备注
	@Column(name = "comment",length=1000,nullable=true)
	private String userComment;
	
	
	public static String getOwnertype() {
		return ownerType;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public Users getUser() {
		return user;
	}


	public void setUser(Users user) {
		this.user = user;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getUserEName() {
		return userEName;
	}


	public void setUserEName(String userEName) {
		this.userEName = userEName;
	}


	public Date getCreatTime() {
		return creatTime;
	}


	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}


	public Date getUpdatedTime() {
		return updatedTime;
	}


	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}


	public String getCardType() {
		return cardType;
	}


	public void setCardType(String cardType) {
		this.cardType = cardType;
	}


	public String getUserIdCard() {
		return userIdCard;
	}


	public void setUserIdCard(String userIdCard) {
		this.userIdCard = userIdCard;
	}


	public String getUserEsidentialAddress() {
		return userEsidentialAddress;
	}


	public void setUserEsidentialAddress(String userEsidentialAddress) {
		this.userEsidentialAddress = userEsidentialAddress;
	}


	public String getUserNationality() {
		return userNationality;
	}


	public void setUserNationality(String userNationality) {
		this.userNationality = userNationality;
	}


	public String getCompany() {
		return company;
	}


	public void setCompany(String company) {
		this.company = company;
	}


	public String getPosition() {
		return position;
	}


	public void setPosition(String position) {
		this.position = position;
	}


	public String getUserIndustry() {
		return userIndustry;
	}


	public void setUserIndustry(String userIndustry) {
		this.userIndustry = userIndustry;
	}


	public String getUserYearsIncom() {
		return userYearsIncom;
	}


	public void setUserYearsIncom(String userYearsIncom) {
		this.userYearsIncom = userYearsIncom;
	}


	public String getUserComment() {
		return userComment;
	}


	public void setUserComment(String userComment) {
		this.userComment = userComment;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
