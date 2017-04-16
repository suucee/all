package com.maxivetech.backoffice.util;

import org.springframework.util.DigestUtils;

import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Users;


public class HelperPassword {

	public HelperPassword() {
		// TODO Auto-generated constructor stub
	}
	public static boolean verifyPassword(String password, String md5Password, String salty) {
		return beforeSave(password, salty).equals(md5Password);
	}
	public static boolean verifyPaymentPassword(String password, Users user) {
		return user != null && beforeSave(password, user.getSalty()).equals(user.getPaymentPassword());
	}
	public static boolean verifyOperationPassword(String password, Admins admin) {
		return admin != null && beforeSave(password, admin.getSalty()).equals(admin.getOperationPassword());
	}
	
	public static String beforeSave(String password, String salty) {
		return DigestUtils.md5DigestAsHex((password + salty).getBytes());
	}
}
