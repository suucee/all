package com.maxivetech.backoffice.util;

public class ForbiddenException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ForbiddenException() {
		super("您没有此操作的权限！");
	}

}
