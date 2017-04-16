package com.suucee.www.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.suucee.www.dao.ColumnDao;
import com.suucee.www.dao.ImagesDao;
import com.suucee.www.dao.LinkDao;
import com.suucee.www.dao.NewsDao;
import com.suucee.www.entity.Columns;
import com.suucee.www.entity.Links;
import com.suucee.www.entity.News;
import com.suucee.www.service.ColumnService;

@Service
@Transactional
public class ColumnServiceImpl implements ColumnService {
	@Autowired
	private ColumnDao columnDao;
	@Autowired
	private NewsDao newsDao;
	@Autowired
	private LinkDao linkDao ;
	@Autowired
	private ImagesDao imgDao ;

	@Override
	public void start(Columns column, HashMap<String, Object> map,
			HttpServletRequest req, HttpServletResponse resp) {
		map.put("mainNav", this._getMainNav());
		
		switch ((String)map.get("_template")) {

		case "/question":
		case "/information_2_col":
		case "/real_account":
			this.getInside(map);
			break;
		case "/index":
			this.financialList(map);
			this.noticeList(map);
			this.getBanner(map);
			this.getPartner(map);
			break;
		}
	}

	public List<Columns> _getMainNav() {
		ArrayList<Criterion> wheres = new ArrayList<Criterion>();

		wheres.add(Restrictions.isNull("parent"));

		ArrayList<Order> orders = new ArrayList<>();

		orders.add(Order.asc("sortNum"));

		List<Columns> list = columnDao.getAll(wheres, orders, null, 0, 1)
				.list();

		return list;
	}

	 public void financialList(HashMap<String, Object> map) {
			// 查询市场新闻列表
			ArrayList<Criterion> wheres = new ArrayList<>();
			Columns Newscolumn = (Columns) columnDao.findByAlias("_financial_information");
			wheres.add(Restrictions.eq("column", Newscolumn));
			Date date = new Date();
			wheres.add(Restrictions.le("creatTime", date));
			wheres.add(Restrictions.eq("toShow", true));
			
			ArrayList<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("toTop"));
			orders.add(Order.asc("sortNum"));
			orders.add(Order.desc("creatTime"));
			List<News> news = newsDao.getAll(wheres, orders, null, 4, 1).list();
			map.put("financialList", news);
		}
	 public void noticeList(HashMap<String, Object> map) {
		 // 查询市场新闻列表
		 ArrayList<Criterion> wheres = new ArrayList<>();
		 Columns Newscolumn = (Columns) columnDao.findByAlias("_notice");
		 wheres.add(Restrictions.eq("column", Newscolumn));
		 Date date = new Date();
		 wheres.add(Restrictions.le("creatTime", date));
		 wheres.add(Restrictions.eq("toShow", true));
		 
		 ArrayList<Order> orders = new ArrayList<Order>();
		 orders.add(Order.desc("toTop"));
		 orders.add(Order.asc("sortNum"));
		 orders.add(Order.desc("creatTime"));
		 List<News> news = newsDao.getAll(wheres, orders, null, 7, 1).list();
		 map.put("noticeList", news);
	 }
	 public void getBanner(HashMap<String, Object> map) {
		 // 获取首页banner
		 ArrayList<Criterion> wheres = new ArrayList<>();
		 Columns linkColumn = (Columns) columnDao.findByAlias("_indexBanner");
		 wheres.add(Restrictions.eq("column", linkColumn));
		 wheres.add(Restrictions.eq("toShow", true));
		 
		 ArrayList<Order> orders = new ArrayList<Order>();
		 orders.add(Order.desc("toTop"));
			orders.add(Order.asc("sortNum"));
			orders.add(Order.desc("creatTime"));
		 List<Links> links = linkDao.getAll(wheres, orders, null, 4, 0).list();
		 map.put("bannerLinks", links);
	 }
	 public void getPartner(HashMap<String, Object> map) {
		 // 获取合作伙伴
		 ArrayList<Criterion> wheres = new ArrayList<>();
		 Columns linkColumn = (Columns) columnDao.findByAlias("_partner");
		 wheres.add(Restrictions.eq("column", linkColumn));
		 wheres.add(Restrictions.eq("toShow", true));
		 
		 ArrayList<Order> orders = new ArrayList<Order>();
		 orders.add(Order.desc("toTop"));
			orders.add(Order.asc("sortNum"));
			orders.add(Order.desc("creatTime"));
	
		 List<Links> links = linkDao.getAll(wheres, orders, null, 0, 0).list();
	
		 map.put("partnerLinks", links);
	 }
	 public void getInside(HashMap<String, Object> map) {
		 // 获取 内页图片
		 ArrayList<Criterion> wheres = new ArrayList<>();
		 Columns linkColumn = (Columns) columnDao.findByAlias("_innerBanner");
		 wheres.add(Restrictions.eq("column", linkColumn));
		 wheres.add(Restrictions.eq("toShow", true));
		 
		 ArrayList<Order> orders = new ArrayList<Order>();
		 orders.add(Order.desc("toTop"));
			orders.add(Order.asc("sortNum"));
			orders.add(Order.desc("creatTime"));
		 List<Links> links = linkDao.getAll(wheres, orders, null, 0, 0).list();
		 map.put("innerImgLinks", links);
	 }
}
