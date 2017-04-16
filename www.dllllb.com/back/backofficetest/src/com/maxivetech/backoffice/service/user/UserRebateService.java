package com.maxivetech.backoffice.service.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.entity.RebateRecords;
import com.maxivetech.backoffice.pojo.PojoRebateReportUsers;
import com.maxivetech.backoffice.pojo.PojoRebates;
import com.maxivetech.backoffice.pojo.PojoUserBalances;
import com.maxivetech.backoffice.pojo.PojoVolumes;
import com.maxivetech.backoffice.util.HelperDate;
import com.maxivetech.backoffice.util.Page;


@Service
public interface UserRebateService {
	//返佣统计
	public List<PojoRebateReportUsers> reportUsers(int year, int month, String startDate, String endDate, HttpSession session);
	public List<PojoRebateReportUsers> reportSourceUsers(int year, int month, String startDate, String endDate, HttpSession session);
	public List<PojoRebateReportUsers> reportSourceLogins(int year, int month, String startDate, String endDate, HttpSession session);
	
}
