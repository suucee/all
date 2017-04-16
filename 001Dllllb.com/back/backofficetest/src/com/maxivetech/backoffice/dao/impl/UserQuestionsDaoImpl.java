package com.maxivetech.backoffice.dao.impl;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.UserQuestionsDao;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.UserQuestions;
import com.maxivetech.backoffice.entity.Users;



@Repository
public class UserQuestionsDaoImpl extends _BaseDaoImpl implements UserQuestionsDao{

	@Override
	public Class<?> classModel() {
		return UserQuestions.class;
	}

	@Override
	public UserQuestions findUserQuestionsByUser(Users user) {
		List<UserQuestions> list = this.createCriteria()
				.add(Restrictions.eq("user", user))
				.addOrder(Order.desc("creatTime"))
				.setMaxResults(1)
				.list();
		return list != null && list.size() > 0 ? list.get(0) : null;
	
	}
}
