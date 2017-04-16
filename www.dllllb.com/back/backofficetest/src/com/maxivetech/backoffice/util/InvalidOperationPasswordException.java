package com.maxivetech.backoffice.util;

public class InvalidOperationPasswordException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidOperationPasswordException() {
		super("错误的操作密码！");
	}

}
