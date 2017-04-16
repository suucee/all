package com.maxivetech.backoffice.service.admin;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.maxivetech.backoffice.entity.Announcement;
import com.maxivetech.backoffice.util.Page;

public interface AdminAnnouncementService {
	
	/**
	 * 新增一条通知
	 * @param title
	 * @param content
	 * @param createTime
	 * @param publish_time
	 * @param top
	 * @param hide
	 * @param session
	 * @return
	 */
	boolean addOrUpdate(int id, String title, String content, int sort, String publish_time, boolean top, boolean display,  HttpSession session);

	Announcement getOne(int id);
	
	Page<Announcement> getPage(int pageNo, int pageSize, String urlFormat, String oederBy);

	boolean updateTop(int id, boolean top);
	boolean updateSortNum(int id, int sort);
	boolean updateDisplay(int id, boolean display);
	
	boolean delete(int id);
	
	/**
	 * 
	 * @param operationName delete,hide,top,show,noTop
	 * @param idstr
	 * @return
	 */
	boolean batchDeal(String operationName, String idstr);
}
