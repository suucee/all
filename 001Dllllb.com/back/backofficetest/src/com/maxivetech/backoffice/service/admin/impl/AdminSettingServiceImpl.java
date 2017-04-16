package com.maxivetech.backoffice.service.admin.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.maxivetech.backoffice.dao.SettingDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Settings;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.pojo.PojoSession;
import com.maxivetech.backoffice.sendmail.HelperSendmail;
import com.maxivetech.backoffice.service.admin.AdminAdminService;
import com.maxivetech.backoffice.service.admin.AdminSettingService;
import com.maxivetech.backoffice.service.admin.AdminUserService;
import com.maxivetech.backoffice.service.user.SessionService;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperPassword;
import com.maxivetech.backoffice.util.HelperSalty;
import com.maxivetech.backoffice.util.InvalidOperationPasswordException;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class AdminSettingServiceImpl implements AdminSettingService {
	@Autowired
	private SettingDao settingDao;

	@CheckRole(role = {Role.OperationsManager,Role.ComplianceOfficer,Role.CustomerServiceStaff,
			Role.FinancialStaff,Role.FinancialSuperior,Role.RiskManagementCommissioner,Role.Webmaster,Role.User})
	@Override
	public List<Settings> getList(HttpSession session) {
		//检查
		
		return settingDao.getList();
	}

	@CheckRole(role = {Role.OperationsManager})
	@Override
	public boolean edit(String key, String value, HttpSession session) {
		//检查
		
		Settings setting = settingDao.findByKey(key);
		if (setting != null) {
			switch (setting.getType()) {
			case "int":
				int intValue = 0;
				try {
					intValue = Integer.valueOf(value);
				} catch (Exception e) {
					
				}
				setting.setIntValue(intValue);
				break;
			case "double":
				double doubleValue = 0;
				try {
					doubleValue = Double.valueOf(value);
				} catch (Exception e) {
					
				}
				setting.setDoubleValue(doubleValue);
				break;
			case "String":
				setting.setStringValue(value);
				break;
			default:
				throw new RuntimeException("不支持的设置类型！");
			}
			setting.setUpdatedTime(new Date());
			settingDao.update(setting);
			settingDao.commit();
			return true;
		}
		return false;
	}

}
