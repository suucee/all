package com.maxivetech.backoffice.dao.impl;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao._BaseDao;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.util.Page;


@Repository
abstract public class _BaseDaoImpl implements _BaseDao {
	@Autowired
	private SessionFactory sessionFactory;
	
	abstract public Class<?> classModel();
	
	@Override
	public Session getSession() { 
		return this.sessionFactory.getCurrentSession();
	}

	public Page<?> getPage(List<Criterion> wheres, List<Order> orders, Projection projs, int pageSize, int pageNo, Class<?> pojo) {
		Criteria cri = this.getSession().createCriteria(this.classModel());
		
		//过滤
		if (wheres != null) {
			for (Criterion where : wheres) {
				cri.add(where);
			}
		}
		//
		cri.setMaxResults(1);
		cri.setProjection(Projections.rowCount());
		int totalRows = ((Long)cri.list().get(0)).intValue();
		
		cri = this.getAll(wheres, orders, projs, pageSize, pageNo);
		if (pojo != null) {
			cri = cri.setResultTransformer(Transformers.aliasToBean(pojo));
		}
		List<?> list = cri.list();
		
		return new Page(totalRows, pageSize, pageNo, list);
	}
	
	public Criteria createCriteria() {
		return this.getSession().createCriteria(this.classModel());
	}
	
	public Criteria getAll(List<Criterion> wheres, List<Order> orders, Projection projs, int pageSize, int pageNo) {
		Criteria cri = this.getSession().createCriteria(this.classModel());
		
		//过滤
		if (wheres != null && wheres.size() > 0) {
			for (Criterion where : wheres) {
				cri.add(where);
			}
		}
		//排序
		if (orders != null && orders.size() > 0) {
			for (Order order : orders) {
				cri.addOrder(order);
			}
		}
		//
		if (pageSize > 0) {
			cri.setMaxResults(pageSize);
			if (pageNo >= 1) {
				cri.setFirstResult((pageNo - 1) * pageSize);
			}
		}
		//
		if (projs != null) {
			cri.setProjection(projs);
		}
		
		return cri;
	}
	
	public List<?> getAll(String hql) {
		return this.getSession().createQuery(hql).list();
	}

	

	@Override
	public void saveOrUpdate(Object object) {
		this.getSession().saveOrUpdate(object);
	}
	@Override
	public Serializable save(Object object) {
		return this.getSession().save(object);
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
	@Override
	public void commit() {
		this.getSession().flush();
	}
	
	/**
	 *通过HQL语句查询返回分页的 HashMap
	 *@param hqlstr hql语句
	 *@param pageSize 页面记录数
	 *@param pageNo 页面编码
	 *@param urlFormat 分页调用链接
	 */
	
	@Override
	public Page<HashMap<String, Object>> getHashPageByHQL(String hqlstr, int pageSize,int pageNo,String urlFormat) {
		Query query=this.getSession().createQuery(hqlstr);
		int totalRows=query.list().size();
				query.setFirstResult((pageNo - 1) * pageSize)
				.setMaxResults(pageSize)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<HashMap<String, Object>> list=query.list();
		Page<HashMap<String, Object>> page = new Page<HashMap<String, Object>>(
				list.size(), pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		return page;
	}
	/**
	 *通过HQL语句查询返回分页的 pojo
	 *@param hqlstr hql语句
	 *@param pageSize 页面记录数
	 *@param pageNo 页面编码
	 *@param urlFormat 分页调用链接
	 *@param pojo 返回pojo类型
	 */
	@Override
	public Page<?> getPojoPageByHQL(String hqlstr, int pageSize, int pageNo,String urlFormat,Class<?> pojo) {
		Query query=this.getSession().createQuery(hqlstr);
		int totalRows=query.list().size();
		query.setFirstResult((pageNo - 1) * pageSize)
				.setMaxResults(pageSize)
				.setResultTransformer(Transformers.aliasToBean(pojo));
		List<?> list=query.list();
		Page<?> page = new Page(list.size(), pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		return page;
	}
	
	/**
	 *通过SQL语句查询返回分页的 HashMap
	 *@param hqlstr sql语句
	 *@param pageSize 页面记录数
	 *@param pageNo 页面编码
	 *@param urlFormat 分页调用链接
	 */
	
	@Override
	public Page<HashMap<String, Object>> getHashPageBySQL(String sqlstr, int pageSize,int pageNo,String urlFormat) {
		Query query=this.getSession().createSQLQuery(sqlstr);
		int totalRows=query.list().size();
		query.setFirstResult((pageNo - 1) * pageSize)
				.setMaxResults(pageSize)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<HashMap<String, Object>> list=query.list();
		Page<HashMap<String, Object>> page = new Page<HashMap<String, Object>>(
				list.size(), pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		return page;
	}
	/**
	 *通过SQL语句查询返回分页的 pojo
	 *@param hqlstr sql语句
	 *@param pageSize 页面记录数
	 *@param pageNo 页面编码
	 *@param urlFormat 分页调用链接
	 *@param pojo 返回pojo类型
	 */
	@Override
	public Page<?> getPojoPageBySQL(String sqlstr, int pageSize, int pageNo,String urlFormat,Class<?> pojo) {
		Query query=this.getSession().createSQLQuery(sqlstr);
		int totalRows=query.list().size();
		query.setFirstResult((pageNo - 1) * pageSize)
				.setMaxResults(pageSize)
				.setResultTransformer(Transformers.aliasToBean(pojo));
		List<?> list=query.list();
		Page<?> page = new Page(list.size(), pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		return page;
	}
	
	
	
}
