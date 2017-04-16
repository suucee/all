package com.suucee.www.service;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suucee.www.entity.Columns;


public interface LinkService {
	public void start(Columns column, HashMap<String, Object> map,
			HttpServletRequest req, HttpServletResponse resp);
}
