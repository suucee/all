package com.suucee.www.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;




@Controller
@RequestMapping("/mobile/")
public class MobileControllor {
	@RequestMapping(value="addprofile.do",method=RequestMethod.POST)
	public void add(
			@RequestParam("userName") String userName,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception{
			
	}
}
