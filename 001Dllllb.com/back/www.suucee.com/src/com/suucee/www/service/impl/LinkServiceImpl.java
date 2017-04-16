package com.suucee.www.service.impl;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.suucee.www.entity.Columns;
import com.suucee.www.service.ColumnService;
import com.suucee.www.service.LinkService;

@Service
@Transactional
public class LinkServiceImpl implements LinkService {

	@Override
	public void start(Columns column, HashMap<String, Object> map,
			HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub

	}

}
