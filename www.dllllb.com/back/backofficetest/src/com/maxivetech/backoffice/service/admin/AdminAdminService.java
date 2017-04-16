package com.maxivetech.backoffice.service.admin;

import java.util.List;

import javax.servlet.http.HttpSession;



import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.util.Page;

/**
 * 
 * @author 锐
 * 1、管理员和用户一样，不能删除，最多是禁用（禁止登录）
 * 2、showName、角色都不能修改
 * 3、操作密码由运维人员登录后自行设置，管理员不可知
 */
public interface AdminAdminService {
	public Page<Admins> getPage(int pageNo, int pageSize, String urlFormat, String scheme, String keyword, HttpSession session);
	
	public boolean setDisabled(int adminId, boolean isDisabled, HttpSession session);
	
	public List<PojoCounts> getCounts(HttpSession session);
	
	public Admins getOne(int adminId, HttpSession session);
	
	public String edit(int adminId,String action,String memo,String operationPassword, HttpSession session);
	
	public int add(String role, String account, String password,String  showName,String operationPassword, HttpSession session);
}
