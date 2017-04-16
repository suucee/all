package com.yasinshaw.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yasinshaw.entity.Student;

@RestController
//@Controller
public class TestController {
	
	//首页
	@RequestMapping("/index")
	public String test(){
		return "index";
	}
	
	//新增
	@RequestMapping(value = "/add", method = RequestMethod.POST, 
					produces="application/json;charset=UTF-8")
	public Student add(String name){
		Student student = new Student(1, name, 19);
		student.setName("我改成了张三");
		return student;
	}
	
	//查询
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public String get(){
		return "我得到了张三";
	}
	
}
