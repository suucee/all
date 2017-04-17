package com.service.impl;

import javax.transaction.Transactional;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.BaseDao;
import com.service.BaseService;

@Service
@Transactional
public abstract class BaseServiceImpl implements BaseService {
	static Logger logger = Logger.getLogger(BaseServiceImpl.class.getName());
	abstract public Class<?> classModel();
	
	@Autowired
	BaseDao baseDao;
	
	@Override
	public boolean add(Object object) {
		try{
			baseDao.save(object);
			return true;
		}catch(Exception e){
			logger.error(e.getMessage());  
			return false;
		}
	}

	@Override
	public boolean update(Object object) {
		try{
			baseDao.update(object);
			return true;
		}catch(Exception e){
			logger.error(e.getMessage());  
			return false;
		}
	}

	@Override
	public boolean deleteById(int id) {
		try{
			baseDao.delete(id);
			return true;
		}catch(Exception e){
			logger.error(e.getMessage());  
			return false;
		}
	}

	@Override
	public boolean getById(int id) {
		// TODO Auto-generated method stub
		return false;
	}

}
