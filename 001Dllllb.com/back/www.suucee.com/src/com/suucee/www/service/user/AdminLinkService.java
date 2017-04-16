package com.suucee.www.service.user;


import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.util.Page;
import com.suucee.www.entity.Links;

@Service
public interface AdminLinkService {
	public Page<Links> getPage(int columnId,int pageNo, int pageSize, String urlFormat, HttpSession session);
	public int add(int columnId,String title,String url,int sortNum,Boolean isBold,Boolean isTop, HttpSession session);
	public boolean update(int id, int columnId,String title,int sortNum,String url,Boolean isBold,Boolean isTop, HttpSession session);
	public boolean remove(int id, HttpSession session);
	public Links getById(int id, HttpSession session);
	public boolean updateSortNum(int id, int sortNum, HttpSession session);
	public boolean updateTop(int id, HttpSession session);
	public boolean updateShow(int id, HttpSession session);
	public boolean updateBold(int id, HttpSession session);

}
