package com.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@Controller
public class TestController {
	
	//index
	@RequestMapping("/")
	public String test(){
		return "this is index page";
	}
}
