package com.maxivetech.backoffice.sendmail;

import java.util.HashMap;

import com.maxivetech.backoffice.entity.UserBalances;
import com.maxivetech.backoffice.entity.Withdrawals;


public class PostmanWithdrawal extends Postman {
	public PostmanWithdrawal(String email) {
		super("cn", 8, email);
	}

	public void waiting(Withdrawals withdrawal, UserBalances userBalance) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("user_id", withdrawal.getUser().getId());
		map.put("id", withdrawal.getId());
		map.put("remitted_time", this.formatDate(withdrawal.getAuditedTime()));
		map.put("amount", this.formatMoney(withdrawal.getAmount()));
		map.put("amount_available", this.formatMoney(userBalance == null ? 0 : userBalance.getAmountAvailable()));
		map.put("currency", withdrawal.getCurrency());
		map.put("audited_memo", withdrawal.getAuditedMemo());
		map.put("bank_name", withdrawal.getBankName());
		map.put("account_name", withdrawal.getAccountName());
		map.put("account_number", withdrawal.getAccountNumber());
		map.put("state", "申请出金");
		
		this.sendAsync("withdrawal.html", map);
	}
	
	public void remitted(Withdrawals withdrawal, UserBalances userBalance) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("user_id", withdrawal.getUser().getId());
		map.put("id", withdrawal.getId());
		map.put("remitted_time", this.formatDate(withdrawal.getAuditedTime()));
		map.put("amount", this.formatMoney(withdrawal.getAmount()));
		map.put("amount_available", this.formatMoney(userBalance == null ? 0 : userBalance.getAmountAvailable()));
		map.put("currency", withdrawal.getCurrency());
		map.put("audited_memo", withdrawal.getAuditedMemo());
		map.put("bank_name", withdrawal.getBankName());
		map.put("account_name", withdrawal.getAccountName());
		map.put("account_number", withdrawal.getAccountNumber());
		map.put("state", "已汇出");
		
		this.sendAsync("withdrawal.html", map);
	}

	public void rejected(Withdrawals withdrawal, UserBalances userBalance) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("user_id", withdrawal.getUser().getId());
		map.put("id", withdrawal.getId());
		map.put("remitted_time", this.formatDate(withdrawal.getAuditedTime()));
		map.put("amount", this.formatMoney(withdrawal.getAmount()));
		map.put("amount_available", this.formatMoney(userBalance == null ? 0 : userBalance.getAmountAvailable()));
		map.put("currency", withdrawal.getCurrency());
		map.put("audited_memo", withdrawal.getAuditedMemo());
		map.put("bank_name", withdrawal.getBankName());
		map.put("account_name", withdrawal.getAccountName());
		map.put("account_number", withdrawal.getAccountNumber());
		map.put("state", "被拒绝");
		
		this.sendAsync("withdrawal_refused.html", map);
	}
	
	public void returned(Withdrawals withdrawal, UserBalances userBalance) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("user_id", withdrawal.getUser().getId());
		map.put("id", withdrawal.getId());
		map.put("remitted_time", this.formatDate(withdrawal.getAuditedTime()));
		map.put("amount", this.formatMoney(withdrawal.getAmount()));
		map.put("amount_available", this.formatMoney(userBalance == null ? 0 : userBalance.getAmountAvailable()));
		map.put("currency", withdrawal.getCurrency());
		map.put("audited_memo", withdrawal.getAuditedMemo());
		map.put("bank_name", withdrawal.getBankName());
		map.put("account_name", withdrawal.getAccountName());
		map.put("account_number", withdrawal.getAccountNumber());
		map.put("state", "被银行退回");
		
		this.sendAsync("withdrawal_refused.html", map);
	}
	

	public void canceled(Withdrawals withdrawal, UserBalances userBalance) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("user_id", withdrawal.getUser().getId());
		map.put("id", withdrawal.getId());
		map.put("remitted_time", this.formatDate(withdrawal.getAuditedTime()));
		map.put("amount", this.formatMoney(withdrawal.getAmount()));
		map.put("amount_available", this.formatMoney(userBalance == null ? 0 : userBalance.getAmountAvailable()));
		map.put("currency", withdrawal.getCurrency());
		map.put("audited_memo", withdrawal.getAuditedMemo());
		map.put("bank_name", withdrawal.getBankName());
		map.put("account_name", withdrawal.getAccountName());
		map.put("account_number", withdrawal.getAccountNumber());
		map.put("state", "已撤消");
		
		this.sendAsync("withdrawal_refused.html", map);
	}
	
}