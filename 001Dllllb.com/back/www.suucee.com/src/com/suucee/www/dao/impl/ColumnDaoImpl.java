package com.suucee.www.dao.impl;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.impl._BaseDaoImpl;
import com.suucee.www.dao.ColumnDao;
import com.suucee.www.entity.Columns;



@Repository
public class ColumnDaoImpl extends _BaseDaoImpl implements ColumnDao {
	@Override
	public Class<?> classModel() {return Columns.class;}

	@Override
	public Columns findByAlias(String alias) {
		List<Columns> list = this.createCriteria()
			.add(Restrictions.eq("alias", alias))
			.setMaxResults(1)
			.list();

		return list.size() > 0 ? list.get(0) : null;
	}


}
