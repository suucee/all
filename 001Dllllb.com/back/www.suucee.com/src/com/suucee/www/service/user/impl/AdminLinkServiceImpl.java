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
import com.maxivetech.backoffice.util.Page;
import com.suucee.www.dao.ColumnDao;
import com.suucee.www.dao.LinkDao;
import com.suucee.www.entity.Archives;
import com.suucee.www.entity.Columns;
import com.suucee.www.entity.Images;
import com.suucee.www.entity.Links;
import com.suucee.www.service.user.AdminLinkService;


@Service
@Transactional
public class AdminLinkServiceImpl implements AdminLinkService {
	@Autowired
	private ColumnDao columnDao;
	@Autowired
	private LinkDao linkDao;

	@Override
	public Page<Links> getPage(int columnId,int pageNo, int pageSize, String urlFormat,
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
		Page<Links> links = (Page<Links>) linkDao.getPage(wheres, orders, null,
				pageSize, pageNo, null);
		links.generateButtons(urlFormat);

		Columns column = null;

		for (Links link : links.getList()) {
			column = link.getColumn();
			if (column != null) {
				link.set_columnName(column.getName());
				link.set_columnId(column.getArchive().getId());
			}
			link.setColumn(null);
			if(link.getArchive().getImages().size()>0){
				link.set_imgPath(link.getArchive().getImages().get(0).getPath());
			}
			link.getArchive().setImages(null);
		}

		return links;
	}

	@Override
	public int add(int columnId, String title, String url, int sortNum,
			Boolean isBold, Boolean isTop, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		Archives archive = new Archives();
		archive.setType("link");
		int id = (Integer) linkDao.save(archive);

		Columns column = (Columns) columnDao.getById(columnId);

		Links link = new Links();
		Date date = new Date();
		link.setArchive(archive);
		link.setColumn(column);
		link.setTitle(title);
		link.setUrl(url);
		link.setSortNum(sortNum);
		link.setToBold(isBold);
		link.setToTop(isTop);
		link.setCreatTime(date);
		linkDao.save(link);
		linkDao.commit();
		return id;
	}

	@Override
	public Links getById(int id, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		// 数据清洗
		Links link = (Links) linkDao.getById(id);
		
		Columns column = null;
		if(link.getColumn()!=null){
			column=link.getColumn();
			link.set_columnId(column.getArchive().getId());
			link.set_columnName(column.getName());
			link.setColumn(null);
		}
		for (Images image : link.getArchive().getImages()) {
			image.setArchive(null);
		}
		return link;
	}

	@Override
	public boolean update(int id, int columnId, String title, int sortNum,
			String url, Boolean isBold, Boolean isTop, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		Links link = (Links) linkDao.getById(id);
		Columns column = (Columns) columnDao.getById(columnId);
		link.setColumn(column);
		link.setSortNum(sortNum);
		link.setTitle(title);
		link.setToBold(isBold);
		link.setUrl(url);
		link.setToTop(isTop);
		linkDao.saveOrUpdate(link);
		linkDao.commit();
		return true;
	}

	@Override
	public boolean updateSortNum(int id, int sortNum, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		Links link = (Links) linkDao.getById(id);
		link.setSortNum(sortNum);
		linkDao.saveOrUpdate(link);
		linkDao.commit();
		return true;
	}

	@Override
	public boolean updateTop(int id, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		Links link = (Links) linkDao.getById(id);
		if (link.isToTop()) {
			link.setToTop(false);
		} else {
			link.setToTop(true);
		}
		linkDao.saveOrUpdate(link);
		linkDao.commit();
		return true;
	}

	@Override
	public boolean updateShow(int id, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		Links link = (Links) linkDao.getById(id);
		if (link.isToShow()) {
			link.setToShow(false);
		} else {
			link.setToShow(true);
		}
		linkDao.saveOrUpdate(link);
		linkDao.commit();
		return true;
	}

	@Override
	public boolean updateBold(int id, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		Links link = (Links) linkDao.getById(id);
		if (link.isToBold()) {
			link.setToBold(false);
		} else {
			link.setToBold(true);
		}
		linkDao.saveOrUpdate(link);
		linkDao.commit();
		return true;
	}

	@Override
	public boolean remove(int id, HttpSession session) {
		// 检查权限
		if (!HelperAuthority.isWebmaster(session)&& !HelperAuthority.isOperationsManager(session)) {
			throw new ForbiddenException();
		}
		linkDao.deleteById(id);
		linkDao.commit();
		return true;
	}

}
