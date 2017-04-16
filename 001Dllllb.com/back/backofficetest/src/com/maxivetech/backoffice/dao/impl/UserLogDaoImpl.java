package com.maxivetech.backoffice.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.util.DigestUtils;

import com.maxivetech.backoffice.dao.AdminDao;
import com.maxivetech.backoffice.dao.AdminLogDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserLogDao;
import com.maxivetech.backoffice.entity.AdminLogs;
import com.maxivetech.backoffice.entity.UserLogs;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.util.HelperPassword;


@Repository
public class UserLogDaoImpl extends _BaseDaoImpl implements UserLogDao {
	@Override
	public Class<?> classModel() {return AdminLogs.class;}

	@Override
	public void log(Users user, String action, String description) {
		if (user == null) {
			return;
		}
		UserLogs log = new UserLogs();
		
		log.setAction(action);
		log.setUser(user);
		log.setCreatTime(new Date());
		log.setDescription(description);
		log.setIpAddress("");
		
		this.save(log);
	}

	@Override
	public void log(int userId, String action, String description) {
		this.log((Users)this.getSession().get(Users.class, userId), action, description);
	}


}
