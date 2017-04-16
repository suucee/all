package com.suucee.www.dao.impl;

import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.impl._BaseDaoImpl;
import com.suucee.www.dao.LinkDao;
import com.suucee.www.entity.Links;



@Repository
public class LinkDaoImpl extends _BaseDaoImpl implements LinkDao {
	@Override
	public Class<?> classModel() {return Links.class;}


}
