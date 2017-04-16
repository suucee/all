package com.maxivetech.backoffice.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UsersSimulationDao;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.entity.UsersSimulation;

@Repository
public class UsersSimulationDaoImpl extends _BaseDaoImpl implements UsersSimulationDao {
	@Override
	public Class<?> classModel() {
		return UsersSimulation.class;
	}

}
