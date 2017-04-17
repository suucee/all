package com.yasinshaw.service;

import com.yasinshaw.entity.Student;

public interface StudentService {
	
	public Student get(int id);
	
	public void update(Student student);
	
	public void delete(int id);
	
	public void add(Student student);
	
}
