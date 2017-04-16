package com.suucee.www.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.maxivetech.backoffice.BackOffice;
import com.suucee.www.dao.ColumnDao;
import com.suucee.www.entity.Columns;
import com.suucee.www.service.ColumnService;
import com.suucee.www.service.LinkService;
import com.suucee.www.service.NewsService;
import com.suucee.www.util.HelperColumn;



@RequestMapping("/")
@Controller
public class IndexController {


	@Autowired
	ColumnDao columnDao;
	@Autowired
	ColumnService columnService;
	@Autowired
	NewsService newsService;
	@Autowired
	LinkService linkService;
	
	/**
	 * 昵称，url都为空，通过column+id去访问
	 * @param id
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "column-{id}.do")
	protected ModelAndView channel1(
			@PathVariable int id,
			HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		return this._start(id, null, 0, 0, req, res);
	}
	/**
	 * 有昵称，通过昵称+.do去访问
	 * @param alias
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "{alias}.do")
	protected ModelAndView channel2(
			@PathVariable String alias,
			HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		return this._start(0, alias, 0, 0, req, res);
	}
	
	
	/**
	 * 无昵称新闻类
	 * @param id
	 * @param pageNo
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "column-{id}/page-{pageNo}.do")
	protected ModelAndView list1(
			@PathVariable int id,
			@PathVariable int pageNo,
			HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		return this._start(id, null, pageNo, 0, req, res);
	}

	/**
	 * 有昵称的新闻类
	 * @param alias
	 * @param pageNo
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "{alias}/page-{pageNo}.do")
	protected ModelAndView list2(
			@PathVariable String alias,
			@PathVariable int pageNo,
			HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		return this._start(0, alias, pageNo, 0, req, res);
	}

	/**
	 * 无昵称，查看新闻类容
	 * @param id
	 * @param contentId
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "column-{id}/{contenttype}/{contentId}.do")
	protected ModelAndView detail1(
			@PathVariable int id,
			@PathVariable int contentId,
			HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		return this._start(id, null, contentId, contentId, req, res);
	}

	/**
	 * 有昵称，查看新闻具体内容
	 * @param alias
	 * @param contentId
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "{alias}/{contenttype}/{contentId}.do")
	protected ModelAndView detail2(
			@PathVariable String alias,
			@PathVariable int contentId,
			HttpServletRequest req,
			HttpServletResponse res) throws Exception {

		return this._start(0, alias, 0, contentId, req, res);
	}
	
	private ModelAndView _start(int columnId, String alias, int pageNo, int contentId,
			HttpServletRequest req,
			HttpServletResponse res) {
		//��ȡ��Ŀ
		Columns column = null;
		if (columnId > 0) {
			column = (Columns) columnDao.getById(columnId);
		}
		if (alias != null && alias.length() > 0) {
			column = (Columns) columnDao.findByAlias(alias);
		}
		
		if (column != null) {
			String template = HelperColumn.getTemplate(column, pageNo, contentId);
			
			if (template != null && !template.equals("")) {
				//׼������
				HashMap<String, Object> map = new HashMap<String, Object>();
				String path = req.getContextPath();
				String basePath = req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()+path+"/";
				Columns topColumn=column;
				while (topColumn.getParent()!=null){
					topColumn=topColumn.getParent();
				}
				//��һ���������ݴ���
				map.put("_pageNo", pageNo);
				map.put("_contentId", contentId);
				map.put("_template", template);
				map.put("_basePath", BackOffice.getInst().URL_ROOT);
				map.put("_column", column);
				map.put("_topColumn", topColumn);
				//�ڶ�����Ͷ��ColumnService
				columnService.start(column, map, req, res);
				
				//����������contentTypeͶ����Ӧ��Service
				switch (column.getContentType()) {
					case "news":
						newsService.start(column, map, req, res);
						break;
					case "link":
						linkService.start(column, map, req, res);
						break;
					default:
						break;
				}
				return new ModelAndView(template, map);
			}
			
		}
		
		return new ModelAndView("/404");
	}
}
