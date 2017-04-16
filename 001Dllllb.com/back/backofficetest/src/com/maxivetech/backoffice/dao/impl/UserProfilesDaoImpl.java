package com.maxivetech.backoffice.dao.impl;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;


@Repository
public class UserProfilesDaoImpl extends _BaseDaoImpl implements UserProfilesDao {
	@Override
	public Class<?> classModel() {return UserProfiles.class;}

	@Override
	public UserProfiles findUserProfilesByUser(Users user) {
		List<UserProfiles> list = this.createCriteria()
				.add(Restrictions.eq("user", user))
				.addOrder(Order.desc("id"))
				.setMaxResults(1)
				.list();
		return list != null && list.size() > 0 ? list.get(0) : null;
	}

	@Override
	public String getUserName(Users user) {
		UserProfiles profile = this.findUserProfilesByUser(user);
		
		return profile != null ? profile.getUserName() : "(暂无姓名)";
	}

	
}
