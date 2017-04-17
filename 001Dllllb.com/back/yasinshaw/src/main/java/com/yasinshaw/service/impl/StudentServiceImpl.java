package com.yasinshaw.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yasinshaw.dao.StudentDao;
import com.yasinshaw.entity.Student;
import com.yasinshaw.service.StudentService;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

	@Autowired
	StudentDao studentDao;
	
	@Override
	public Student get(int id) {
		return (Student) studentDao.getById(id);
	}

	@Override
	public void update(Student student) {
		studentDao.update(student);
	}

	@Override
	public void delete(int id) {
		studentDao.deleteById(id);
	}

	@Override
	public void add(Student student) {
		studentDao.save(student);
	}
	
}
