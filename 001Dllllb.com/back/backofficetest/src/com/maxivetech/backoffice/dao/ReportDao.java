package com.maxivetech.backoffice.dao;

import org.hibernate.Session;


public interface ReportDao {
	public Session getSession();
	/**
	 * 查询条数
	 * @param hql
	 * @return
	 */
	public int counts(String hql);

}
