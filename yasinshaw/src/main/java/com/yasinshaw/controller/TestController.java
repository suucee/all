package com.yasinshaw.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yasinshaw.entity.Student;

@RestController
//@Controller
public class TestController {
	
	//��ҳ
	@RequestMapping("/index")
	public String test(){
		return "index";
	}
	
	//����
	@RequestMapping(value = "/add", method = RequestMethod.POST, 
					produces="application/json;charset=UTF-8")
	public Student add(String name){
		Student student = new Student(1, name, 19);
		student.setName("�Ҹĳ�������");
		return student;
	}
	
	//��ѯ
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public String get(){
		return "�ҵõ�������";
	}
	
}
