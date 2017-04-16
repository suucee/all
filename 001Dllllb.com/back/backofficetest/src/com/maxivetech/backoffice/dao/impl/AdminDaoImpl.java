package com.maxivetech.backoffice.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.util.HelperPassword;


@Repository
public class AdminDaoImpl extends _BaseDaoImpl implements AdminDao {
	@Override
	public Class<?> classModel() {return Admins.class;}

	@Override
	public Admins findByAccount(String account) {
		
		List<Admins> list = this.createCriteria()
			.add(Restrictions.eq("account", account))
			.setMaxResults(1)
			.list();
		
		return list != null && list.size() > 0 ? list.get(0) : null;
	}
	

}
