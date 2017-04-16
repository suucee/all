package com.suucee.www.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.suucee.www.entity.Columns;


public interface ColumnService {
	public void start(Columns column, HashMap<String, Object> map,
			HttpServletRequest req, HttpServletResponse resp);

}
