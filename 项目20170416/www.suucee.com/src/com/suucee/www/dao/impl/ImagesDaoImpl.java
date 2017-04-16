package com.suucee.www.dao.impl;

import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.impl._BaseDaoImpl;
import com.suucee.www.dao.ImagesDao;
import com.suucee.www.entity.Images;



@Repository
public class ImagesDaoImpl extends _BaseDaoImpl implements ImagesDao {
	@Override
	public Class<?> classModel() {return Images.class;}


}
