package com.dao.impl;

import org.springframework.stereotype.Repository;

import com.dao.StudentDao;
import com.entity.Student;

@Repository
public class StudentDaoImpl extends BaseDaoImpl implements StudentDao {
	@Override
	public Class<?> classModel() {
		return Student.class;
	}
	
}
