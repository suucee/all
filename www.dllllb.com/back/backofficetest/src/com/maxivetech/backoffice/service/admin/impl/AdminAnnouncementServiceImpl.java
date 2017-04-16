package com.maxivetech.backoffice.service.admin.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.AdminDao;
import com.maxivetech.backoffice.dao.AnnouncementDao;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Announcement;
import com.maxivetech.backoffice.service.admin.AdminAnnouncementService;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperDate;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class AdminAnnouncementServiceImpl implements AdminAnnouncementService {

	@Autowired
	private AdminDao adminDao;

	@Autowired
	private AnnouncementDao announcementDao;
	
	@CheckRole(role = {Role.OperationsManager, Role.CustomerServiceStaff,Role.Webmaster})
	@Override
	public boolean addOrUpdate(int id, String title, String content,int sort, String publishTime, boolean top, boolean display,
			HttpSession session) {
		try{
			Admins admins = (Admins) adminDao.getById(HelperAuthority.getId(session));
			if(admins==null){
				throw new RuntimeException("对不起，您暂无发布公告的权限。");
			}
			Announcement announcement = null;
			if(id == 0){
				announcement = new Announcement();
			}else{
				announcement = getOne(id);
			}
			
			if(announcement == null){
				announcement = new Announcement();
			}
			
			announcement.setTitle(title);
			announcement.setContent(content);
			
			//创建时间
			if(announcement.getCreateTime()!=null){
				announcement.setCreateTime(announcement.getCreateTime());
			}else{
				announcement.setCreateTime(new Date());
			}
			
			//修改时间
			announcement.setModifyTime(new Date());
			
			//发布时间
			Date publishDate = HelperDate.parse(publishTime, "yyyy-MM-dd HH:mm:ss");
			if(publishDate == null){//没有指定发布时间，就是立即发布
				publishDate = new Date();
			}
			announcement.setPublishTime(publishDate);
			
			
			announcement.setTop(top);
			announcement.setSort(sort);
			announcement.setDisplay(display);
			announcement.setAdmins(admins);
			
			announcementDao.saveOrUpdate(announcement);
			announcementDao.commit();
			
		}catch(Exception e){
			return false;
		}
		return true;
	}

	@Override
	public Announcement getOne(int id) {
		Announcement announcement = null;
		try{
			announcement = (Announcement) announcementDao.getById(id);
			announcement.setAdminsShowName(announcement.getAdmins().getShowName());
			announcement.setAdminsRole(announcement.getAdmins().getRole());
		}catch(Exception e){
			return null;
		}
		return (announcement==null?null:announcement);
	}

	@Override
	public Page<Announcement> getPage(int pageNo, int pageSize, String urlFormat, String oederBy) {
		//WHERE
		ArrayList<Criterion> wheres = new ArrayList<Criterion>();
		
		//ORDER BY
		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("top"));//1为置顶
		switch (oederBy) {
			case "create":
				orders.add(Order.asc("createTime"));
				break;
			case "publish":
				orders.add(Order.desc("publishTime"));
				break;
			case "modify":
				orders.add(Order.desc("modifyTime"));
				break;
			case "published":
				wheres.add(Restrictions.le("publishTime", new Date()));
				break;
			case "waitting"://发布时间大于当前时间
				wheres.add(Restrictions.gt("publishTime", new Date()));
				break;
			default:
				orders.add(Order.asc("sort"));
				break;
		}
		
		Page<Announcement> page =  (Page<Announcement>) announcementDao.getPage(wheres, orders, null, pageSize, pageNo, null);
		
		for (Announcement announcement : page.getList()) {
			announcement.setAdminsShowName(announcement.getAdmins().getShowName());
			announcement.setAdminsRole(announcement.getAdmins().getRole());
		}
		
		page.generateButtons(urlFormat);
		
		return page;
	}

	@Override
	/**
	 * 将状态改变为相反的
	 * @param id
	 * @param top 原来的状态
	 * @return
	 */
	public boolean updateTop(int id, boolean top) {
		Announcement announcement = (Announcement) announcementDao.getById(id);
		if(announcement!=null){
			announcement.setTop(!top);
			if(!top){//原来不是置顶的话，设置为置顶
				announcement.setSort(0);
			}
			announcementDao.saveOrUpdate(announcement);
			announcementDao.commit();
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean updateDisplay(int id, boolean display) {
		Announcement announcement = (Announcement) announcementDao.getById(id);
		if(announcement!=null){
			announcement.setDisplay(!display);
			announcementDao.saveOrUpdate(announcement);
			announcementDao.commit();
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean delete(int id) {
		try{
			announcementDao.deleteById(id);
			announcementDao.commit();
			return true;
		}catch(Exception e){
			return false;
		}
	}

	@Override
	public boolean updateSortNum(int id, int sort) {
		Announcement announcement = (Announcement) announcementDao.getById(id);
		if(announcement!=null){
			if(sort != 0){
				announcement.setTop(false);
			}
			announcement.setSort(sort);
			announcementDao.saveOrUpdate(announcement);
			announcementDao.commit();
			return true;
		}
		else
			return false;
	}

	@Override
	/**
	 * delete,hide,top,show,noTop
	 */
	public boolean batchDeal(String operationName, String idstr) {
		String[] ids = idstr.split(",");
		try {
			switch (operationName) {
			case "delete":
				for (String string : ids) {
					try {
						announcementDao.deleteById(Integer.parseInt(string));
					} catch (Exception e) {
						continue;
					}
				}
				break;
			case "hide":
				for (String string : ids) {
					Announcement announcement = (Announcement) getOne(Integer.parseInt(string));
					if (announcement != null) {
						announcement.setDisplay(false);
					} else {
						continue;
					}
				}
				break;
			case "top":
				for (String string : ids) {
					Announcement announcement = (Announcement) getOne(Integer.parseInt(string));
					if (announcement != null) {
						announcement.setTop(true);
					} else {
						continue;
					}
				}
				break;
			case "show":
				for (String string : ids) {
					Announcement announcement = (Announcement) getOne(Integer.parseInt(string));
					if (announcement != null) {
						announcement.setDisplay(true);
					} else {
						continue;
					}
				}
				break;
			case "noTop":// 发布时间大于当前时间
				for (String string : ids) {
					Announcement announcement = (Announcement) getOne(Integer.parseInt(string));
					if (announcement != null) {
						announcement.setTop(false);
					} else {
						continue;
					}
				}
				break;
			default:
				return false;
			}
			announcementDao.commit();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
