package com.maxivetech.backoffice.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.util.DigestUtils;

import com.maxivetech.backoffice.dao.UserBalanceDao;
import com.maxivetech.backoffice.entity.UserBalances;
import com.maxivetech.backoffice.entity.Users;

import aj.org.objectweb.asm.Type;

@Repository
public class UserBalanceDaoImpl extends _BaseDaoImpl implements UserBalanceDao {
	@Override
	public Class<?> classModel() {
		return UserBalances.class;
	}

	@Override
	public UserBalances getBalance(Users user, String currencyType) {
		if (user == null) {
			throw new NullPointerException("user不能爲NULL!");
		}

		List<UserBalances> list = this.getSession()
				.createCriteria(this.classModel())
				.add(Restrictions.eq("user", user))
				.add(Restrictions.eq("currencyType", currencyType))
				.setMaxResults(1).list();

		if (list != null && list.size() > 0) {
			// 已經存在，則返回

			return list.get(0);
		} else {
			// 不存在，則添加，餘額

			UserBalances ub = new UserBalances();

			// id和updatedTime自動生成
			Date date = new Date();
			ub.setCurrencyType(currencyType);
			ub.setUpdatedTime(date);
			ub.setUser(user);

			this.saveOrUpdate(ub);
			this.commit();

			return ub;
		}
	}

	@Override
	public boolean increaseAmountAvailable(Users user, String currencyType,
			double amount) {
		return this.increaseAmountAvailable(this.getBalance(user, currencyType), amount);
	}
	@Override
	public boolean increaseAmountAvailable(UserBalances ub, double amount) 
	{
		if (ub == null || amount != 0) {
			ub.setAmountAvailable(ub.getAmountAvailable() + amount);
			this.saveOrUpdate(ub);
			
			return true;
		} else {
			return false;
		}
	}
	

	@Override
	public boolean increaseAmountFrozen(Users user, String currencyType,
			double amount) {
		return this.increaseAmountFrozen(this.getBalance(user, currencyType), amount);
	}
	@Override
	public boolean increaseAmountFrozen(UserBalances ub, double amount) 
	{
		if (ub == null || amount != 0) {
			ub.setAmountFrozen(ub.getAmountFrozen() + amount);
			this.saveOrUpdate(ub);
			
			return true;
		} else {
			return false;
		}
	}
}
