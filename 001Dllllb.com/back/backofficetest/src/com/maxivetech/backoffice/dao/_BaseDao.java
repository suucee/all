package com.maxivetech.backoffice.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;

import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.UserQuestions;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.util.Page;

public abstract interface _BaseDao {
	public Session getSession();
	public void commit();
	public void saveOrUpdate(Object object);
	public Serializable save(Object object);
	public void update(Object object);
	public void deleteById(int id);
	public void delete(Object object);
	public Object getById(int id);
	public Criteria createCriteria();
	public Criteria getAll(List<Criterion> wheres, List<Order> orders, Projection projs, int pageSize, int pageNo);
	public Page<?> getPage(List<Criterion> wheres, List<Order> orders, Projection projs, int pageSize, int pageNo, Class<?> pojo);
	public Page<HashMap<String, Object>> getHashPageByHQL(String hqlstr, int pageSize, int pageNo,String urlFormat);
	public Page<?> getPojoPageByHQL(String hqlstr,  int pageSize, int pageNo,String urlFormat, Class<?> pojo);
	public Page<HashMap<String, Object>> getHashPageBySQL(String sqlstr, int pageSize, int pageNo,String urlFormat);
	public Page<?> getPojoPageBySQL(String sqlstr,  int pageSize, int pageNo,String urlFormat, Class<?> pojo);
	
}
