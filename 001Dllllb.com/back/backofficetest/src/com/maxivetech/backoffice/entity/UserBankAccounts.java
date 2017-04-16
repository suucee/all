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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * @author dane
 *
 */
@Entity
@Table(name = "user__bank_accounts")
public class UserBankAccounts implements Serializable {
	private static final long serialVersionUID = 1L;
    private static final String ownerType="back"; 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private Users user;
	
	@Column(name = "account_name", length = 100, nullable = false)
	private String accountName;

	@Column(name = "account_no", length = 50, nullable = false)
	private String accountNo;
	
	@Column(name = "swift_code",  nullable = false,columnDefinition="varchar(128) default '' ")
	private String swiftCode="";
 
	@Column(name = "bank_name",  nullable = false,columnDefinition="varchar(128) default '' ")
	private String bankName="";

	@Column(name = "iban_code",  nullable = false,columnDefinition="varchar(128) default '' ")
	private String ibanCode="";
	
	@Column(name = "bank_branch",  nullable = false,columnDefinition="varchar(50) default '' ")
	private String bankBranch="";
	
	@Column(name = "bank_address", nullable = false,columnDefinition="varchar(128) default '' ")
	private String bankAddress="";
	
	@Column(name = "intermediary_bank_name", length = 35, nullable = true)
	private String intermediaryBankName;
	
	@Column(name = "intermediary_bank_branch", length = 35, nullable = true)
	private String intermediaryBankBranch;

	@Column(name = "intermediary_bank_bic_swift_code", length = 11, nullable = true)
	private String intermediaryBankBicSwiftCode;

	@Column(name = "intermediary_bank_address", length = 93, nullable = true)
	private String intermediaryBankAddress;
	

	@Column(name = "currency_type", length = 3, nullable = true)
	private String currencyType;

	@Column(name = "country_code", length = 2, nullable = true)
	private String countryCode;
	
	@Column(name = "sort_num", nullable = true)
	private int sortNum;
	// 是否绑定
	@Column(name = "isdefault", nullable = false,columnDefinition="bit(1) default 0 ")
	private boolean isDefault;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time", nullable = false,columnDefinition="datetime")
	private Date updateTime=new Date();

	//备注
	@Column(name = "comment",length=1000,nullable=true)
	private String bankComment;
	
	public String getBankComment() {
		return bankComment;
	}

	public void setBankComment(String bankComment) {
		this.bankComment = bankComment;
	}

	//WAITING	待審核
	//AUDITED	審核通過
	//REJECTED	駁回
	//DELTED 已删除
	@Column(name = "state", nullable = false,columnDefinition="varchar(10) default 'WAITING' ")
	private String state="WAITING";
	
	public static String getOwnertype() {
		return ownerType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	public int getSortNum() {
		return sortNum;
	}

	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public String getSwiftCode() {
		return swiftCode;
	}

	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}

	public String getBankAddress() {
		return bankAddress;
	}

	public void setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
	}

	public String getIntermediaryBankName() {
		return intermediaryBankName;
	}

	public void setIntermediaryBankName(String intermediaryBankName) {
		this.intermediaryBankName = intermediaryBankName;
	}

	public String getIntermediaryBankBranch() {
		return intermediaryBankBranch;
	}

	public void setIntermediaryBankBranch(String intermediaryBankBranch) {
		this.intermediaryBankBranch = intermediaryBankBranch;
	}

	public String getIntermediaryBankBicSwiftCode() {
		return intermediaryBankBicSwiftCode;
	}

	public void setIntermediaryBankBicSwiftCode(String intermediaryBankBicSwiftCode) {
		this.intermediaryBankBicSwiftCode = intermediaryBankBicSwiftCode;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}
	

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public String getIntermediaryBankAddress() {
		return intermediaryBankAddress;
	}

	public void setIntermediaryBankAddress(String intermediaryBankAddress) {
		this.intermediaryBankAddress = intermediaryBankAddress;
	}


	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getIbanCode() {
		return ibanCode;
	}

	public void setIbanCode(String ibanCode) {
		this.ibanCode = ibanCode;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}


	
}
