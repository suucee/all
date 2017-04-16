package com.suucee.www.service.user.impl;

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

import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.suucee.www.dao.ColumnDao;
import com.suucee.www.entity.Archives;
import com.suucee.www.entity.Columns;
import com.suucee.www.entity.Images;
import com.suucee.www.service.user.AdminColumnService;

@Service
@Transactional
public class AdminColumnServiceImpl implements AdminColumnService {
	@Autowired
	private ColumnDao columnDao;

	@Override
	public List<Columns> getList(int upId, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)&&!HelperAuthority.isOperationsManager(session)) {
				throw new ForbiddenException();
		}
		ArrayList<Criterion> wheres = new ArrayList<Criterion>();
		if (upId == 0) {
			wheres.add(Restrictions.isNull("parent"));
		} else {
			wheres.add(Restrictions.eq("parent", upId));
		}
		ArrayList<Order> orders = new ArrayList<Order>();

		orders.add(Order.asc("sortNum"));
		List<Columns> list = columnDao.getAll(wheres, orders, null, 0, 0)
				.list();

		for (Columns column : list) {
			column.setParent(null);
			for (Columns c : column.getChildren()) {
				c.setParent(null);
				c.getArchive().setImages(null);
			}
			column.getArchive().setImages(null);
		}
		return list;
	}

	@Override
	public List<Columns> getLinkColumnList(HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)
				&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		ArrayList<Criterion> wheres = new ArrayList<Criterion>();
		wheres.add(Restrictions.eq("contentType", "link"));

		ArrayList<Order> order = new ArrayList<>();
		order.add(Order.desc("creatTime"));

		List<Columns> list = columnDao.getAll(wheres, order, null, 0, 0).list();

		for (Columns column : list) {
			column.setParent(null);
			for (Columns c : column.getChildren()) {
				c.setParent(null);
				c.setChildren(null);
				c.getArchive().setImages(null);
			}
			column.getArchive().setImages(null);
		}
		return list;
	}

	@Override
	public List<Columns> getArticlesColumnList(HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)
				&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		ArrayList<Criterion> wheres = new ArrayList<Criterion>();
		wheres.add(Restrictions.eq("contentType", "news"));

		ArrayList<Order> order = new ArrayList<>();
		order.add(Order.desc("creatTime"));

		List<Columns> list = columnDao.getAll(wheres, order, null, 0, 0).list();

		for (Columns column : list) {
			column.setParent(null);
			for (Columns c : column.getChildren()) {
				c.setParent(null);
				c.getArchive().setImages(null);
			}
			column.getArchive().setImages(null);
		}
		return list;
	}

	@Override
	public String addUpColumn(String name,String alias,String contentType,HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)
				&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		Archives archive = new Archives();
		archive.setType("column");
		int id = (Integer) columnDao.save(archive);
		Columns column = new Columns();
		column.setParent(null);
		column.setArchive(archive);
		
		Columns c = columnDao.findByAlias(alias);
		if (alias == null) {// 判断接收到的alias是不是NULL
			column.setAlias(null);
		} else if (alias.equals("")) {// 判断接收到的alias是不是""
			column.setAlias(null);
		} else if (c != null) {
				return "别名已存在";
		} else {
			column.setAlias(alias);
		}
		switch (contentType) {
		case "news":
			column.setChannelTemplate("/news_list");
			column.setListTemplate("/news_list");
			column.setContentTemplate("/news_content");
			break;
		case "link":
			column.setChannelTemplate("");
			column.setListTemplate("");
			column.setContentTemplate("");
			break;
		case "information":
		default:
			column.setChannelTemplate("/information_2_col");
			column.setListTemplate("");
			column.setContentTemplate("");
			break;
		}
		column.setContentType(contentType);
		column.setName(name);
		column.setSeoDescription("");
		column.setSeoKeywords("");
		column.setSeoTitle("");
		column.setSortNum(50);
		column.setUrl("");
		column.setCreatTime(new Date());
		column.setBody("");
		columnDao.save(column);
		columnDao.commit();
		return "添加成功";
	}

	@Override
	public int addChild(int upId, String name, String contentType,
			HttpSession session) {
		Archives archive = new Archives();
		archive.setType("column");
		int id = (Integer) columnDao.save(archive);
		Columns column = new Columns();
		if (upId == 0) {
			column.setParent(null);
		} else {
			Columns c = (Columns) columnDao.getById(upId);
			column.setParent(c);
		}
		column.setArchive(archive);
		column.setAlias(null);
		column.setBody("");
		switch (contentType) {
		case "news":
			column.setChannelTemplate("/news_list");
			column.setListTemplate("/news_list");
			column.setContentTemplate("/news_content");
			break;
		case "link":
			column.setChannelTemplate("");
			column.setListTemplate("");
			column.setContentTemplate("");
			break;
		case "information":
		default:
			column.setChannelTemplate("/information_2_col");
			column.setListTemplate("");
			column.setContentTemplate("");
			break;
		}
		column.setContentType(contentType);
		column.setName(name);
		column.setSeoDescription("");
		column.setSeoKeywords("");
		column.setSeoTitle("");
		column.setSortNum(50);
		column.setUrl("");
		column.setCreatTime(new Date());
		columnDao.save(column);
		columnDao.commit();
		return id;
	}

	@Override
	public Columns getById(int id, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)
				&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		Columns c = (Columns) columnDao.getById(id);
		// 数据清洗
		if (c.getParent() != null) {
			c.set_parentId(c.getParent().getArchive().getId());
		} else {
			c.set_parentId(0);
		}
		c.setChildren(null);
		c.setParent(null);

		// 清洗image的数据
		for (Images image : c.getArchive().getImages()) {
			image.setArchive(null);
		}
		return c;
	}

	@Override
	public String update(int id, int upId, String alias, String name,
			int sortNum, String contentType, String channelTemplate,
			String listTemplate, String contentTemplate, String url,
			String seoTitle, String seoKeywords, String seoDescription,
			String body, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)
				&&!HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		Columns column = (Columns) columnDao.getById(id);
		if (upId == 0) {
			column.setParent(null);
		} else {
			Columns c = (Columns) columnDao.getById(upId);
			column.setParent(c);
		}
		Columns c = columnDao.findByAlias(alias);
		if (alias == null) {// 判断接收到的alias是不是NULL
			column.setAlias(null);
		} else if (alias.equals("")) {// 判断接收到的alias是不是""
			column.setAlias(null);
		} else if (c != null&&c.getArchive().getId()!=column.getArchive().getId()) {
				return "别名已存在";
		} else {
			column.setAlias(alias);
		}
		column.setContentType(contentType);
		column.setName(name);
		column.setSeoDescription(seoDescription);
		column.setSeoKeywords(seoKeywords);
		column.setSeoTitle(seoTitle);
		column.setSortNum(sortNum);
		column.setChannelTemplate(channelTemplate);
		column.setListTemplate(listTemplate);
		column.setContentTemplate(contentTemplate);
		column.setBody(body);
		column.setUrl(url);
		Date date = new Date();
		column.setCreatTime(date);
		columnDao.saveOrUpdate(column);
		columnDao.commit();
		return "修改成功！";
	}

	@Override
	public boolean updateSortNum(int id, int sortNum, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)
				&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		Columns column = (Columns) columnDao.getById(id);
		column.setSortNum(sortNum);
		columnDao.saveOrUpdate(column);
		columnDao.commit();
		return true;
	}

	@Override
	public boolean remove(int id, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)
				&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		columnDao.deleteById(id);
		columnDao.commit();
		return true;
	}
}
