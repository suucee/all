package com.maxivetech.backoffice.service.admin;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Settings;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.pojo.PojoSession;
import com.maxivetech.backoffice.util.Page;

public interface AdminSettingService {
	public List<Settings> getList(HttpSession session);
	public boolean edit(String key, String value, HttpSession session);
}
