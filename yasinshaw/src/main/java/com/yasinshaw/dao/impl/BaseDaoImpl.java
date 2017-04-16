package com.yasinshaw.dao.impl;

import java.io.Serializable;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.yasinshaw.dao.BaseDao;

@Repository
public abstract class BaseDaoImpl implements BaseDao {
	@Autowired
	SessionFactory sessionFactory;
	
	abstract public Class<?> classModel();
	
	@Override
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	public void closeSession() {
		this.getSession().close();
	}
	@Override
	public Criteria createCriteria() {
		return this.getSession().createCriteria(this.classModel().toString());
//		return this.getSession().createCriteria(this.classModel());
	}
	
	@Override
	public void commit() {
		this.getSession().flush();
	}

	@Override
	public void saveOrUpdate(Object object) {
		this.getSession().saveOrUpdate(object);
	}

	@Override
	public Serializable save(Object object) {
		Serializable s = this.getSession().save(object);
		return s;
	}

	@Override
	public void update(Object object) {
		this.getSession().update(object);
	}

	@Override
	public void deleteById(int id) {
		this.getSession().delete(this.getById(id));
	}

	@Override
	public void delete(Object object) {
		this.getSession().delete(object);
	}

	@Override
	public Object getById(int id) {
		return this.getSession().get(this.classModel(), id);
	}

}
