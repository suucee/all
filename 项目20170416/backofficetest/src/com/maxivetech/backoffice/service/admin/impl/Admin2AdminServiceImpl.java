package com.maxivetech.backoffice.service.admin.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.AdminDao;
import com.maxivetech.backoffice.dao.AdminLogDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.entity.AdminLogs;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.pojo.PojoSession;
import com.maxivetech.backoffice.sendmail.HelperSendmail;
import com.maxivetech.backoffice.service.admin.Admin2AdminService;
import com.maxivetech.backoffice.service.admin.AdminAdminService;
import com.maxivetech.backoffice.service.admin.AdminUserService;
import com.maxivetech.backoffice.service.user.SessionService;
import com.maxivetech.backoffice.service.user.impl.SessionServiceImpl;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperPassword;
import com.maxivetech.backoffice.util.HelperSalty;
import com.maxivetech.backoffice.util.InvalidOperationPasswordException;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class Admin2AdminServiceImpl implements Admin2AdminService {
	@Autowired
	private AdminDao adminDao;
	@Autowired
	private AdminLogDao adminLogDao;
	@Autowired
	private UserDao userDao;



	@CheckRole(role = {Role.OperationsManager})
	@Override
	public String edit(int adminId,String action,String memo,HttpSession session) {
		
		Admins admins= (Admins) adminDao.getById(SessionServiceImpl.getCurrent(session).getId());
		Admins admin = (Admins) adminDao.getById(adminId);
		if (admin != null && !admin.getRole().equals("OperationsManager")) {
			if((admin.isDisabled()&&action.equals("DISABLE"))
					||(!admin.isDisabled()&&action.equals("ENABLE"))){
				return "不合法的操作";
			}else {
				switch (action) {
				case "DISABLE":
					admin.setDisabled(true);
					break;
				case "ENABLE":
					admin.setDisabled(false);
					break;
				default:
					return "不合法的操作";
				}
			}
			
			adminDao.update(admin);
			
			//添加日志
			AdminLogs al = new AdminLogs();
			al.setAction(action);
			al.setAdmin(admins);
			al.setCreatTime(new Date());
			al.setDescription(memo);
			al.setId(adminId);
			al.setIpAddress(String.valueOf(session.getAttribute("RemoteAddr")));
			adminLogDao.save(al);
			
			adminDao.commit();
			
			return "操作成功";
		}
		
		return "操作失败";
	}
	
	
	
	@CheckRole(role = {Role.OperationsManager})
	@Override
	public int add(String role, String account, String password, String userOperationPassword, String showName,String operationPassword, HttpSession session) {
		
		//检查操作密码
		Admins admins= (Admins) adminDao.getById(SessionServiceImpl.getCurrent(session).getId());
		if(!HelperPassword.verifyPassword(operationPassword, admins.getOperationPassword(), admins.getSalty())){
			throw new InvalidOperationPasswordException();
		}
		
		if (!Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", account)) {
			throw new RuntimeException("邮箱格式不正确！");
		}
		
		
		//检查Account重复
		if (adminDao.findByAccount(account) != null) {
			throw new RuntimeException("已经有管理员使用该邮箱了！");
		}
		
		//防止和用户邮箱重复，这样的话，登录时无法判定是用户还是管理员
		if(userDao.findByEmail(account) != null){
			throw new RuntimeException("已经有客户使用该邮箱注册了！");
		}
		
		
		//生成随机密码
		String randomSalty = HelperSalty.getCharAndNumr(6);
		
		//添加admin
		Admins admin = new Admins();
		admin.setCreatTime(new Date());
		admin.setLastLogonTime(new Date());
		admin.setDisabled(false);
		admin.setAccount(account);
		admin.setShowName(showName);
		admin.setOperationPassword("");
		admin.setSalty(randomSalty);
		admin.setPassword(HelperPassword.beforeSave(password, randomSalty));
		admin.setOperationPassword(HelperPassword.beforeSave(userOperationPassword, randomSalty));
		admin.setRole(role);
		
		int ret = (Integer) adminDao.save(admin);
		adminDao.commit();
		
		return ret;
	}
}
