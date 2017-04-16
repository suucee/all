/**
 * 
 */
package com.maxivetech.backoffice.entity;

import java.io.Serializable;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;


/**
 * @author Rui.Xu
 *
 */
@Entity
@Table(name = "withdrawals")
public class Withdrawals implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private Users user;
	
	//WAITING	待審核
	//AUDITED	審核通過
	//REJECTED	駁回
	//REMITTED	已汇出
	//BACK	银行退回
	//CANCELED	客戶取消
	//PENDING_SUPERVISOR 待财务主管审核
	@Column(name = "state", length = 20, nullable = false)
	private String state;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creat_time", nullable = false)
	private Date creatTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "audited_time", nullable = true)
	private Date auditedTime = null;
	
	
	@Column(name = "account_name", length = 35, nullable = false)
	private String accountName = "";
	@Column(name = "address1", length = 31, nullable = false)
	private String address1 = "";
	@Column(name = "address2", length = 31, nullable = false)
	private String address2 = "";
	@Column(name = "address3", length = 31, nullable = false)
	private String address3 = "";
	
	@Column(name = "country", length = 50, nullable = false)
	private String country = "";
	@Column(name = "account_number", length = 30, nullable = false)
	private String accountNumber = "";
	@Column(name = "bank_name", length = 35, nullable = false)
	private String bankName = "";
	@Column(name = "branch", length = 31, nullable = false)
	private String branch = "";
	@Column(name = "swift_code",  nullable = false,columnDefinition="varchar(128) default ''")
	private String swiftCode = "";

	@Column(name = "iban_code", nullable = false,columnDefinition="varchar(128) default ''")
	private String ibanCode="";
	
	@Column(name = "bank_branch",  nullable = false,columnDefinition="varchar(50) default '' ")
	private String bankBranch="";
	
	@Column(name = "bank_address",  nullable = false,columnDefinition="varchar(128) default '' ")
	private String bankAddress="";
	
	
	
	
	
	@Column(name = "intermediary_bank_name", length = 35, nullable = false)
	private String intermediaryBankName = "";
	@Column(name = "intermediary_bank_branch", length = 35, nullable = false)
	private String intermediaryBankBranch = "";
	@Column(name = "intermediary_bank_bic_swift_code", length = 11, nullable = false)
	private String intermediaryBankBicSwiftCode = "";
	
	@Column(name = "amount", nullable = false)
	private double amount;
	@Column(name = "currency", length = 3, nullable = false)
	private String currency;
	@Column(name = "remittance", length = 200, nullable = false)
	private String remittance;
	
	@Column(name = "type", length = 50, nullable = false)
	private String type = "";

	@Temporal(TemporalType.DATE)
	@Column(name = "expiry_date", nullable = true)
	private Date expiryDate = null;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_time", nullable = true)
	private Date dateTime = null;
	

	@Column(name = "postcode", length = 50, nullable = false)
	private String postcode = "";
	@Column(name = "sender_reference", length = 50, nullable = false)
	private String senderReference = "";
	@Column(name = "internal_reference", length = 50, nullable = false)
	private String internalReference = "";
	@Column(name = "bank_reference", length = 50, nullable = false)
	private String bankReference = "";
	
	@Column(name = "audited_memo", nullable = false)
	private String auditedMemo = "";
	@Column(name = "user_memo", nullable = false)
	private String userMemo = "";
	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "canceled_time", nullable = true)
	private Date canceledTime = null;
	
	@Column(name = "user_comment",length=1000,nullable=true)
	private String userComment;
	
	public String getUserComment() {
		return userComment;
	}
	public void setUserComment(String userComment) {
		this.userComment = userComment;
	}
	@Transient
	private int _userId;

	@Transient
	private String _userEmail;
	@Transient
	private String _userName;

	@Column(name = "exchange_rate", nullable = true,columnDefinition=" double default 6.6582")
	private double  exchangeRate=6.6582;
	
	
	public double getExchangeRate() {
		return exchangeRate;
	}
	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
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
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getAddress3() {
		return address3;
	}
	public void setAddress3(String address3) {
		this.address3 = address3;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public String getSwiftCode() {
		return swiftCode;
	}
	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}
	public String getIbanCode() {
		return ibanCode;
	}
	public void setIbanCode(String ibanCode) {
		this.ibanCode = ibanCode;
	}
	public String getBankBranch() {
		return bankBranch;
	}
	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
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
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getRemittance() {
		return remittance;
	}
	public void setRemittance(String remittance) {
		this.remittance = remittance;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public String getInternalReference() {
		return internalReference;
	}
	public void setInternalReference(String internalReference) {
		this.internalReference = internalReference;
	}
	public String getBankReference() {
		return bankReference;
	}
	public void setBankReference(String bankReference) {
		this.bankReference = bankReference;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Date getCreatTime() {
		return creatTime;
	}
	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}
	public Date getAuditedTime() {
		return auditedTime;
	}
	public void setAuditedTime(Date auditedTime) {
		this.auditedTime = auditedTime;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getSenderReference() {
		return senderReference;
	}
	public void setSenderReference(String senderReference) {
		this.senderReference = senderReference;
	}
	public String getAuditedMemo() {
		return auditedMemo;
	}
	public void setAuditedMemo(String auditedMemo) {
		this.auditedMemo = auditedMemo;
	}
	public String getUserMemo() {
		return userMemo;
	}
	public void setUserMemo(String userMemo) {
		this.userMemo = userMemo;
	}
	public Date getCanceledTime() {
		return canceledTime;
	}
	public void setCanceledTime(Date canceledTime) {
		this.canceledTime = canceledTime;
	}
	public int get_userId() {
		return _userId;
	}
	public void set_userId(int _userId) {
		this._userId = _userId;
	}
	public String get_userEmail() {
		return _userEmail;
	}
	public void set_userEmail(String _userEmail) {
		this._userEmail = _userEmail;
	}
	public String get_userName() {
		return _userName;
	}
	public void set_userName(String _userName) {
		this._userName = _userName;
	}
	
	
}
