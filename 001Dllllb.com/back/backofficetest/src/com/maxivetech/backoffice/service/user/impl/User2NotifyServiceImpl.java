package com.maxivetech.backoffice.service.user.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.dao.NotifyDao;
import com.maxivetech.backoffice.entity.Notify;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.service.user.User2NotifyService;
import com.maxivetech.backoffice.util.Page;

@Service
@Transactional
public class User2NotifyServiceImpl implements User2NotifyService {
	@Autowired
	private NotifyDao notifyDao;

	@Override
	public Page<Notify> getPage(int pageNo, int pageSize, String urlFormat, HttpSession session) {
		Users users=SessionServiceImpl.getCurrentUser(session);
		Page<Notify>  page=notifyDao.getMyNotifyList(null, users, pageNo, pageSize, urlFormat);
		return page;
	}
	
	@Override
	public Page<Notify> getPageAll(int pageNo, int pageSize, String urlFormat, HttpSession session) {
		Users users=SessionServiceImpl.getCurrentUser(session);
		List<Criterion> wheres = new ArrayList<Criterion>();
	    wheres.add(Restrictions.eq("users", users));
		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("createTime"));
		Page<Notify> page = (Page<Notify>) notifyDao.getPage(wheres, orders, null, pageSize, pageNo, null);
		page.generateButtons(urlFormat);
		return page;
	}
	
	@Override
    public boolean readNotify(int id,HttpSession session){
    	Notify notify=(Notify) notifyDao.getById(id);
    	if(notify==null){
    		throw new RuntimeException("通知不存在!");
    	}
    	notify.setRead(true);
    	notifyDao.saveOrUpdate(notify);
    	notifyDao.commit();
    	return true;
    }
}
