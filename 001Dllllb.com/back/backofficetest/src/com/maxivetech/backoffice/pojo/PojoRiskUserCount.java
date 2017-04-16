package com.maxivetech.backoffice.pojo;

public class PojoRiskUserCount {
	private int userId;
	private String  userEmail;
	private String  name;
	private Double  depositsAmount;
	private Double  withdrawalsAmount;
	private Double  mt4BalanceSum;
    private Double amountAvailable;
    private Integer login;
    private Integer depositCounts;
    private Integer withdrawalsCounts;
    
    
    
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getDepositsAmount() {
		return depositsAmount;
	}
	public void setDepositsAmount(Double depositsAmount) {
		this.depositsAmount = depositsAmount;
	}
	public Double getWithdrawalsAmount() {
		return withdrawalsAmount;
	}
	public void setWithdrawalsAmount(Double withdrawalsAmount) {
		this.withdrawalsAmount = withdrawalsAmount;
	}
	public Double getMt4BalanceSum() {
		return mt4BalanceSum;
	}
	public void setMt4BalanceSum(Double mt4BalanceSum) {
		this.mt4BalanceSum = mt4BalanceSum;
	}
	public Double getAmountAvailable() {
		return amountAvailable;
	}
	public void setAmountAvailable(Double amountAvailable) {
		this.amountAvailable = amountAvailable;
	}
	public Integer getLogin() {
		return login;
	}
	public void setLogin(Integer login) {
		this.login = login;
	}
	public Integer getDepositCounts() {
		return depositCounts;
	}
	public void setDepositCounts(Integer depositCounts) {
		this.depositCounts = depositCounts;
	}
	public Integer getWithdrawalsCounts() {
		return withdrawalsCounts;
	}
	public void setWithdrawalsCounts(Integer withdrawalsCounts) {
		this.withdrawalsCounts = withdrawalsCounts;
	}
    
}
