package com.maxivetech.backoffice.dao.impl;

import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.AnnouncementDao;
import com.maxivetech.backoffice.entity.Announcement;

@Repository
public class AnnouncementDaoImpl extends _BaseDaoImpl implements AnnouncementDao{

	@Override
	public Class<?> classModel() {return Announcement.class;}

}
