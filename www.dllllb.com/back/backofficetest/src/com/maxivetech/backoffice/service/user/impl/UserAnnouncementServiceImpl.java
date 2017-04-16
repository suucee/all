package com.maxivetech.backoffice.service.user.impl;

import java.util.ArrayList;
import java.util.Date;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.dao.AnnouncementDao;
import com.maxivetech.backoffice.entity.Announcement;
import com.maxivetech.backoffice.service.user.UserAnnouncementService;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class UserAnnouncementServiceImpl implements UserAnnouncementService {

	@Autowired
	private AnnouncementDao announcementDao;
	
	@Override
	public Page<Announcement> getPage(int pageNo, int pageSize, String urlFormat, String oederBy, String keyword) {
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
			default:
				orders.add(Order.asc("sort"));
				break;
		}
		//只检索发布时间小于等于当前时间的公告
		wheres.add(Restrictions.le("publishTime", new Date()));
		
		//只检索可见公告
		wheres.add(Restrictions.eq("display", true));
		
		
		if(keyword != null && keyword.length()>0){
			wheres.add(Restrictions.like("title", "%"+keyword+"%"));
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
}
