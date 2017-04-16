package com.suucee.www.dao;

import com.maxivetech.backoffice.dao._BaseDao;
import com.suucee.www.entity.Columns;


public interface ColumnDao extends _BaseDao {
	/**
	 * 
	 * @param alias
	 * @return
	 */
	public Columns findByAlias(String alias);
}
