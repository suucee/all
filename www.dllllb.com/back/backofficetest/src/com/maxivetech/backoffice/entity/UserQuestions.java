package com.maxivetech.backoffice.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ConstructorResult;
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

import org.hibernate.annotations.UpdateTimestamp;

/**
 * @author wmy
 *
 */
@Entity
@Table(name = "user_questions")
public class UserQuestions implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = true)
	private Users user;
	
	@Column(name = "user_name", length = 100, nullable = false)
	private String userName;
	
	/*@Column(name = "user_companey", length = 100, nullable = false)
	private String userCompaney;
	
	public String getUserCompaney() {
		return userCompaney;
	}
	public void setUserCompaney(String userCompaney) {
		this.userCompaney = userCompaney;
	}*/
	
	@Column(name = "email", length =50 , nullable = false)
	private String userEmail;

	@Column(name = "mobile", length =20 ,nullable = false)
	private String userPhone;
	
	@Column(name = "question_type",length =20 , nullable = false)
	private String questionType;
	
    @Column(name = "questionContext",length =1000,nullable = false,columnDefinition="" )
	private String questionContext;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creat_time", nullable = false)
	private Date creatTime;
   
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "done_time", length = 50,nullable = true)
	private Date doneTime;
	@Column(name = "state",length = 50, nullable = false)
	private String state;
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
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getUserPhone() {
		return userPhone;
	}
	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}
	public String getQuestionType() {
		return questionType;
	}
	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}
	public String getQuestionContext() {
		return questionContext;
	}
	public void setQuestionContext(String questionContext) {
		this.questionContext = questionContext;
	}
	public Date getCreatTime() {
		return creatTime;
	}
	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}
	public Date getDoneTime() {
		return doneTime;
	}
	public void setDoneTime(Date doneTime) {
		this.doneTime = doneTime;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}