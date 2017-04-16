package com.maxivetech.backoffice.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.EmailValidationDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.entity.EmailValidation;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;

@Repository
public class EmailValidationDaoImpl extends _BaseDaoImpl implements EmailValidationDao {
	@Override
	public Class<?> classModel() {
		return EmailValidation.class;
	}

	@Override
	public EmailValidation findByEmial(Users user) {
		List<EmailValidation> list = this.createCriteria()
				.addOrder(Order.desc("passEmailTime"))
				.add(Restrictions.eq("user", user)).setMaxResults(1).list();
		return list != null && list.size() > 0 ? list.get(0) : null;
	}

}
