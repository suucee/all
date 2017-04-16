package com.maxivetech.backoffice.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.ReportDao;

@Repository
public class ReportDaoImpl extends HibernateDaoSupport implements ReportDao {

	@Autowired
	public void setSessionFactoryOverride(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public Session getSession() {
		return this.getSessionFactory().getCurrentSession();
	}

	public int counts(String hql) {
		Object totalRows = this.getSession().createQuery(hql).uniqueResult();
		if (totalRows != null) {
			return Integer.valueOf(totalRows.toString());
		}
		    return 0;
	}

}
