package com.maxivetech.backoffice.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.AnnouncementDao;
import com.maxivetech.backoffice.dao.NotifyDao;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Announcement;
import com.maxivetech.backoffice.entity.Notify;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.util.Page;

@Repository
public class NotifyDaoImpl extends _BaseDaoImpl implements NotifyDao{

	@Override
	public Class<?> classModel() {return Notify.class;}

	@Override
	public boolean insertNotify(String content,Admins admins,Users users,int notifyId,String notifyType) {
		Notify notify=new Notify();
		notify.setAdmins(admins);
		notify.setUsers(users);
		notify.setContent(content);
		notify.setCreateTime(new Date());
		notify.setNotifyType(notifyType);
		notify.setRead(false);
		notify.setNotifyId(notifyId);
		this.saveOrUpdate(notify);
		return true;
	}

	@Override
	public Page<Notify> getMyNotifyList(Admins admins,Users users,int pageNo, int pageSize, String urlFormat) {
		List<Criterion> wheres = new ArrayList<Criterion>();
		if(admins!=null){
			wheres.add(Restrictions.eq("admins", admins));
		}
		if(users!=null){
	    	wheres.add(Restrictions.eq("users", users));
		}
		wheres.add(Restrictions.eq("read", false));
		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("createTime"));
		Page<Notify> page = (Page<Notify>) this.getPage(wheres, orders, null, pageSize, pageNo, null);
		page.generateButtons(urlFormat);
		return page;
	}

}
