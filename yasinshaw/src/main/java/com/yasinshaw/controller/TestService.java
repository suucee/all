package com.yasinshaw.controller;

import org.springframework.stereotype.Service;

@Service
public class TestService {
	public String sayHello(String name){
		return name+" Hello!";
	}
}
