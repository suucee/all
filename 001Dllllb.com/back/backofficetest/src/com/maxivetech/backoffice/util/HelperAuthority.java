package com.maxivetech.backoffice.util;

import javax.servlet.http.HttpSession;

import com.maxivetech.backoffice.pojo.PojoSession;
import com.maxivetech.backoffice.service.user.impl.SessionServiceImpl;

public class HelperAuthority {
	private static PojoSession _getCurrent(HttpSession session) {
		return SessionServiceImpl.getCurrent(session);
	}
	public static boolean isUser(HttpSession session) {
		return _getCurrent(session).getRole().equals("User");
	}
	public static int getId(HttpSession session) {
		return _getCurrent(session).getId();
	}
	public static int getLevel(HttpSession session) {
		return _getCurrent(session).getLevel();
	}
	public static boolean isComplianceOfficer(HttpSession session) {
		return _getCurrent(session).getRole().equals("ComplianceOfficer");
	}
	public static boolean isFinancialStaff(HttpSession session) {
		return _getCurrent(session).getRole().equals("FinancialStaff");
	}
	public static boolean isFinancialSuperior(HttpSession session) {
		return _getCurrent(session).getRole().equals("FinancialSuperior");
	}
	public static boolean isCustomerServiceStaff(HttpSession session) {
		return _getCurrent(session).getRole().equals("CustomerServiceStaff");
	}
	public static boolean isOperationsManager(HttpSession session) {
		return _getCurrent(session).getRole().equals("OperationsManager");
	}
	public static boolean isWebmaster(HttpSession session) {
		return _getCurrent(session).getRole().equals("Webmaster");
	}
	public static boolean is(HttpSession session, String role) {
		return _getCurrent(session).getRole().equals(role);
	}

}