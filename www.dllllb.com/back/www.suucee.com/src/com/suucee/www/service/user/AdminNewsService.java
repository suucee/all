package com.suucee.www.service.user;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.util.Page;
import com.suucee.www.entity.News;


@Service
public interface AdminNewsService {
	public Page<News> getPage(int columnId,int pageNo, int pageSize, String bodyFormat, 
			/* String keyword,*/ HttpSession session);
	public int add(int columnId,String title,String body,int sortNum,Boolean isBold,Boolean isTop,Boolean isShow,String creatTime,String seoTitle,String seoKeywords,
			String seoDescription, HttpSession session);
	public boolean update(int id, int columnId,String title,int sortNum,Boolean isBold,Boolean isTop,Boolean isShow,String body,String creatTime,String seoTitle,String seoKeywords,
			String seoDescription, HttpSession session);
	public boolean remove(int id, HttpSession session);
	public News getById(int id, HttpSession session);
	public boolean updateSortNum(int id, int sortNum, HttpSession session);
	public boolean updateTop(int id, HttpSession session);
	public boolean updateShow(int id, HttpSession session);
	public boolean updateBold(int id, HttpSession session);

}
