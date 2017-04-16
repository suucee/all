package com.suucee.www.dao.impl;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.impl._BaseDaoImpl;
import com.suucee.www.dao.NewsDao;
import com.suucee.www.entity.News;


@Repository
public class NewsDaoImpl extends _BaseDaoImpl implements NewsDao {
	@Override
	public Class<?> classModel() {return News.class;}


}
