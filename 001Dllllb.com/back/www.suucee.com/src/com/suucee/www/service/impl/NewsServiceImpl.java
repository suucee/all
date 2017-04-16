package com.suucee.www.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

import com.maxivetech.backoffice.util.Page;
import com.suucee.www.dao.ColumnDao;
import com.suucee.www.dao.LinkDao;
import com.suucee.www.dao.NewsDao;
import com.suucee.www.entity.Columns;
import com.suucee.www.entity.Links;
import com.suucee.www.entity.News;
import com.suucee.www.service.ColumnService;
import com.suucee.www.service.NewsService;

@Service
@Transactional
public class NewsServiceImpl implements NewsService {
	@Autowired
	private NewsDao newsDao;
	@Autowired
	private ColumnDao columnDao;
	@Autowired
	private LinkDao linkDao;

	@Override
	public void start(Columns column, HashMap<String, Object> map,
			HttpServletRequest req, HttpServletResponse resp) {
		this.getInside(map);
		switch ((String) map.get("_template")) {
		case "/news_list":
			this.newsList(map);
			this.sideNav(map);
			this.allNews(map);
			break;
		case "/news_content":
			this.sideNav(map);
			map.put("news", this.getById((int) ((map.get("_contentId")))));
			break;

		default:
			try {
				resp.sendError(404);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	public News getById(int id) {

		return (News) newsDao.getById(id);
	}
	 public void allNews(HashMap<String, Object> map) {
		 // 查询所有新闻列表
		 Columns _column = (Columns) map.get("_column");
		 ArrayList<Criterion> wheres = new ArrayList<>();
		 Date date = new Date();
		 wheres.add(Restrictions.le("creatTime", date));
		 wheres.add(Restrictions.eq("toShow", true));
		 
		 ArrayList<Order> orders = new ArrayList<Order>();
		 orders.add(Order.desc("toTop"));
		 orders.add(Order.asc("sortNum"));
		 orders.add(Order.desc("creatTime"));

		 int pageNo = (int) map.get("_pageNo");
			if (pageNo == 0) {
				pageNo = 1;
			}
			Page<News> news = (Page<News>) newsDao.getPage(wheres, orders, null,
					14, pageNo, null);
			news.generateButtons("news/page-??.do");
			map.put("allNews", news);
	 }
	public void getConsult(HashMap<String, Object> map) {
		 // 获取 内页图片
		 ArrayList<Criterion> wheres = new ArrayList<>();
		 Columns linkColumn = (Columns) columnDao.findByAlias("_innerBanner");
		 wheres.add(Restrictions.eq("column", linkColumn));
		 wheres.add(Restrictions.eq("column", ""));
		 wheres.add(Restrictions.eq("toShow", true));
		 
		 ArrayList<Order> orders = new ArrayList<Order>();
		 orders.add(Order.desc("toTop"));
			orders.add(Order.asc("sortNum"));
			orders.add(Order.desc("creatTime"));
		 List<Links> links = linkDao.getAll(wheres, orders, null, 3, 1).list();
		 map.put("innerImgLinks", links);
	 }

	public void sideNav(HashMap<String, Object> map) {
		// 查询市场新闻列表
		ArrayList<Criterion> wheres = new ArrayList<>();
		Columns Newscolumn = (Columns) columnDao.findByAlias("market_news");
		wheres.add(Restrictions.eq("column", Newscolumn));
		Date date = new Date();
		wheres.add(Restrictions.le("creatTime", date));
		wheres.add(Restrictions.eq("toShow", true));
		
		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("toTop"));
		orders.add(Order.asc("sortNum"));
		orders.add(Order.desc("creatTime"));
		List<News> news = newsDao.getAll(wheres, orders, null, 7, 1).list();
		map.put("marketNewsList", news);
	}

	public void newsList(HashMap<String, Object> map) {
		// 获取当前column
		Columns _column = (Columns) map.get("_column");
		// 查询市场新闻列表
		ArrayList<Criterion> wheres = new ArrayList<>();
		Columns Newscolumn = (Columns) columnDao
				.findByAlias(_column.getAlias());
		wheres.add(Restrictions.eq("column", Newscolumn));
		Date date = new Date();
		wheres.add(Restrictions.le("creatTime", date));
		wheres.add(Restrictions.eq("toShow", true));
		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("toTop"));
		orders.add(Order.asc("sortNum"));
		orders.add(Order.desc("creatTime"));
		int pageNo = (int) map.get("_pageNo");
		if (pageNo == 0) {
			pageNo = 1;
		}
		Page<News> news = (Page<News>) newsDao.getPage(wheres, orders, null,
				18, pageNo, null);
		news.generateButtons(_column.getAlias() + "/page-??.do");
		map.put("newsList", news);
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
		 List<Links> links = linkDao.getAll(wheres, orders, null, 3, 1).list();
		 map.put("innerImgLinks", links);
	 }
}
