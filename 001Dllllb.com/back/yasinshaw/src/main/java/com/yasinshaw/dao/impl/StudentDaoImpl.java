package com.yasinshaw.dao.impl;

import org.springframework.stereotype.Repository;

import com.yasinshaw.dao.StudentDao;
import com.yasinshaw.entity.Student;

@Repository
public class StudentDaoImpl extends BaseDaoImpl implements StudentDao {
	@Override
	public Class<?> classModel() {
		return Student.class;
	}
	
}
