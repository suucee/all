package com.maxivetech.backoffice.service.admin;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.maxivetech.backoffice.pojo.PojoWebItem;

public interface Admin2IndexService {

	public List<PojoWebItem> getWebItem(HttpSession session);
	
}
