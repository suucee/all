package com.maxivetech.backoffice.service.admin;

import javax.servlet.http.HttpSession;

/**
 * 
 * @author 锐
 * 1、管理员和用户一样，不能删除，最多是禁用（禁止登录）
 * 2、showName、角色都不能修改
 * 3、操作密码由运维人员登录后自行设置，管理员不可知
 */
public interface Admin2AdminService {
	public String edit(int adminId,String action,String memo, HttpSession session);

	int add(String role, String account, String password, String userOperationPassword, String showName,
			String operationPassword, HttpSession session);
	
}
