package com.maxivetech.backoffice.util;

public class InvalidPaymentPasswordException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidPaymentPasswordException() {
		super("错误的支付密码！");
	}

}
