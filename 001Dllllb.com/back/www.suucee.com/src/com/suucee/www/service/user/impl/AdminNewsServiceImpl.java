package com.suucee.www.service.user.impl;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperDate;
import com.maxivetech.backoffice.util.Page;
import com.suucee.www.dao.ColumnDao;
import com.suucee.www.dao.NewsDao;
import com.suucee.www.entity.Archives;
import com.suucee.www.entity.Columns;
import com.suucee.www.entity.Images;
import com.suucee.www.entity.News;
import com.suucee.www.service.user.AdminNewsService;

@Service
@Transactional
public class AdminNewsServiceImpl implements AdminNewsService {
	@Autowired
	private ColumnDao columnDao;
	@Autowired
	private NewsDao newsDao;

	@Override
	public Page<News> getPage(int columnId,int pageNo, int pageSize, String urlFormat,
			HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		ArrayList<Criterion> wheres=new ArrayList<>();
		if(columnId!=0){
			Columns column=(Columns) columnDao.getById(columnId);
			wheres.add(Restrictions.eq("column", column));
		}
		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("toTop"));
		orders.add(Order.asc("sortNum"));
		orders.add(Order.desc("creatTime"));
		Page<News> news = (Page<News>) newsDao.getPage(wheres, orders, null,
				pageSize, pageNo, null);
		news.generateButtons(urlFormat);
		Columns column = null;
		for (News article : news.getList()) {
			column = article.getColumn();
			if (column != null) {
				article.set_columnName(column.getName());
				article.set_columnId(column.getArchive().getId());
			}
			article.setColumn(null);
			article.getArchive().setImages(null);
		}

		return news;
	}

	@Override
	public int add(int columnId, String title, String body, int sortNum,
			Boolean isBold, Boolean isTop, Boolean isShow, String creatTime,
			String seoTitle,String seoKeywords,
			String seoDescription,HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		Archives archive = new Archives();
		archive.setType("article");
		int id = (Integer) newsDao.save(archive);

		Columns column = (Columns) columnDao.getById(columnId);

		News news = new News();
		Date date = new Date();
		news.setArchive(archive);
		news.setColumn(column);
		news.setTitle(title);
		news.setBody(body);
		news.setSortNum(sortNum);
		news.setToBold(isBold);
		news.setToTop(isTop);
		news.setToShow(isShow);
		news.setSeoTitle(seoTitle);
		news.setSeoKeywords(seoKeywords);
		news.setSeoDescription(seoDescription);
		if (HelperDate.parse(creatTime, "yyyy-MM-dd HH:mm:ss") != null) {
			news.setCreatTime(HelperDate.parse(creatTime, "yyyy-MM-dd HH:mm:ss"));
		} else {
			news.setCreatTime(date);
		}
		newsDao.save(news);
		newsDao.commit();
		return id;
	}

	@Override
	public News getById(int id, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		// 数据清洗

		News news = (News) newsDao.getById(id);
		Columns column = null;
		if (news.getColumn() != null) {
			column = news.getColumn();
			news.set_columnId(column.getArchive().getId());
			news.set_columnName(column.getName());
			news.setColumn(null);
		}
		for (Images image : news.getArchive().getImages()) {
			image.setArchive(null);
		}

		return news;
	}

	@Override
	public boolean update(int id, int columnId, String title, int sortNum,
			Boolean isBold, Boolean isTop, Boolean isShow, String body,
			String creatTime,String seoTitle,String seoKeywords,
			String seoDescription, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		News news = (News) newsDao.getById(id);
		Columns column = (Columns) columnDao.getById(columnId);
		news.setColumn(column);
		news.setSortNum(sortNum);
		news.setTitle(title);
		news.setToBold(isBold);
		news.setToTop(isTop);
		news.setToShow(isShow);
		news.setBody(body);
		news.setSeoTitle(seoTitle);
		news.setSeoKeywords(seoKeywords);
		news.setSeoDescription(seoDescription);
		if (HelperDate.parse(creatTime, "yyyy-MM-dd HH:mm:ss") != null) {
			news.setCreatTime(HelperDate.parse(creatTime, "yyyy-MM-dd HH:mm:ss"));
		}

		newsDao.saveOrUpdate(news);
		newsDao.commit();

		return true;
	}

	@Override
	public boolean updateSortNum(int id, int sortNum, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		News news = (News) newsDao.getById(id);
		news.setSortNum(sortNum);
		newsDao.saveOrUpdate(news);
		newsDao.commit();

		return true;
	}

	@Override
	public boolean updateTop(int id, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		News news = (News) newsDao.getById(id);
		if (news.isToTop()) {
			news.setToTop(false);
		} else {
			news.setToTop(true);
		}
		newsDao.saveOrUpdate(news);
		newsDao.commit();

		return true;
	}

	@Override
	public boolean updateShow(int id, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		News news = (News) newsDao.getById(id);
		if (news.isToShow()) {
			news.setToShow(false);
		} else {
			news.setToShow(true);
		}
		newsDao.saveOrUpdate(news);
		newsDao.commit();

		return true;
	}

	@Override
	public boolean updateBold(int id, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		News news = (News) newsDao.getById(id);
		if (news.isToBold()) {
			news.setToBold(false);
		} else {
			news.setToBold(true);
		}
		newsDao.saveOrUpdate(news);
		newsDao.commit();

		return true;
	}

	@Override
	public boolean remove(int id, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		newsDao.deleteById(id);
		newsDao.commit();

		return true;
	}

}
