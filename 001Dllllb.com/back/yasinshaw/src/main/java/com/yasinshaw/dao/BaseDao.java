package com.yasinshaw.dao;

import java.io.Serializable;
import org.hibernate.Criteria;
import org.hibernate.Session;

public interface BaseDao {
	public Session getSession();
	public void commit();
	public void saveOrUpdate(Object object);
	public Serializable save(Object object);
	public void update(Object object);
	public void deleteById(int id);
	public void delete(Object object);
	public Object getById(int id);
	public Criteria createCriteria();
}
