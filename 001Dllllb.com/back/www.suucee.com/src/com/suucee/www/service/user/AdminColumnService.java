package com.suucee.www.service.user;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.suucee.www.entity.Columns;


@Service
public interface AdminColumnService {
	public List<Columns> getList(int upId, HttpSession session);
	public String update(int id,int upId,String alias,String name,int sortNum,String contentType,String channelTemplate,String listTemplate,String contentTemplate, String url,String seoTitle,String seoKeywords,String seoDescription,String body, HttpSession session);
	public boolean remove(int id, HttpSession session);
	public String addUpColumn( String name,String alias,String contentType, HttpSession session);
	public Columns getById(int id, HttpSession session);
	public boolean updateSortNum(int id, int sortNum, HttpSession session);
	public List<Columns> getLinkColumnList(HttpSession session);
	public List<Columns> getArticlesColumnList(HttpSession session);
	public int addChild(int upId, String name, String contentType, HttpSession session);

}
