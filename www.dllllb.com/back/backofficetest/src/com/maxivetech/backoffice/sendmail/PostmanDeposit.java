package com.maxivetech.backoffice.sendmail;

import java.util.Date;
import java.util.HashMap;

import com.maxivetech.backoffice.entity.Deposits;
import com.maxivetech.backoffice.entity.UserBalances;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.util.HelperMoney;


/**
 * 发送入金相关的邮件
 *
 */
public class PostmanDeposit extends Postman {

	public PostmanDeposit(String email) {
		super("cn", 8, email);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 入金成功
	 * @param lang
	 * @param email
	 * @param deposit
	 * @param userBalance
	 */
	public void deposited(Deposits deposit, UserBalances userBalance) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		String orderNum = deposit.getOrderNum();
		Date date = null;
		if(orderNum != null && orderNum.contains("[D]")){
			date = deposit.getAuditedTime();//代为入金的处理时间
		}else {
			date = deposit.getPaymentTime();//在线入金的支付时间
		}
		map.put("user_id", deposit.getUser().getId());
		map.put("id", deposit.getId());
		map.put("audited_time", this.formatDate(date));
		map.put("amount", HelperMoney.formatMoney(deposit.getAmount()));
		map.put("amount_available", this.formatMoney(userBalance == null ? 0.00 : userBalance.getAmountAvailable()));
		map.put("currency", deposit.getCurrency());
		map.put("state", "已到账");
		
		this.sendAsync("deposit.html", map);
	}
	
	/**
	 * 入金被驳回
	 * @param lang
	 * @param email
	 * @param deposit
	 * @param userBalance
	 * @param memo
	 */
	public void rejected(Deposits deposit, UserBalances userBalance) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		String orderNum = deposit.getOrderNum();
		Date date = null;
		if(orderNum != null && orderNum.contains("[D]")){
			date = deposit.getAuditedTime();//代为入金的处理时间
		}else {
			date = deposit.getPaymentTime();//在线入金的支付时间
		}
		map.put("user_id", deposit.getUser().getId());
		map.put("id", deposit.getId());
		map.put("audited_time", this.formatDate(date));
		map.put("amount", HelperMoney.formatMoney(deposit.getAmount()));
		map.put("amount_available", this.formatMoney(userBalance == null ? 0.00 : userBalance.getAmountAvailable()));
		map.put("currency", deposit.getCurrency());
		map.put("audited_memo", deposit.getAuditedMemo());
		map.put("state", "已驳回");
		
		this.sendAsync("deposit_refused.html", map);
	}
	
	/**
	 * 入金成功
	 * @param lang
	 * @param email
	 * @param deposit
	 * @param userBalance
	 */
	public void depositedmanager(Deposits deposit, UserProfiles up,UserBalances userBalance) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("username", up.getUserName());
		map.put("id", deposit.getId());
		map.put("audited_time", this.formatDate(deposit.getAuditedTime()));
		map.put("amount", this.formatMoney(deposit.getAmount()));
		map.put("amount_available", this.formatMoney(userBalance == null ? 0 : userBalance.getAmountAvailable()));
		map.put("currency", deposit.getCurrency());
		map.put("state", "已到账");
		
		this.sendAsync("deposit_manager.html", map);
	}
}
