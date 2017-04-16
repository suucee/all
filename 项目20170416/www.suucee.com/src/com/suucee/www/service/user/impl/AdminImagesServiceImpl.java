package com.suucee.www.service.user.impl;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.suucee.www.dao.ColumnDao;
import com.suucee.www.dao.ImagesDao;
import com.suucee.www.dao.LinkDao;
import com.suucee.www.dao.NewsDao;
import com.suucee.www.entity.Columns;
import com.suucee.www.entity.Images;
import com.suucee.www.entity.Links;
import com.suucee.www.entity.News;
import com.suucee.www.service.user.AdminImagesService;


@Service
@Transactional
public class AdminImagesServiceImpl implements AdminImagesService {
	@Autowired
	private ImagesDao imagesdao;
	@Autowired
	private ColumnDao columndao;
	@Autowired
	private LinkDao linkdao;
	@Autowired
	private NewsDao newskdao;

	@Override
	public int addImage(int id, String imgPath, String imgName, String imgType,
			HttpSession session) {

		Columns col = (Columns) columndao.getById(id);
		Links link = (Links) linkdao.getById(id);
		News news = (News) newskdao.getById(id);
		Images img = new Images();
		Date date = new Date();
		if (col != null) {
			img.setArchive(col.getArchive());
		}
		if (link != null) {
			img.setArchive(link.getArchive());
		}
		if (news != null) {
			img.setArchive(news.getArchive());
		}

		img.setCreatTime(date);
		img.setName(imgName);
		img.setPath(imgPath);
		img.setType(imgType);
		imagesdao.save(img);
		imagesdao.commit();
		return img.getId();

	}

	@Override
	public boolean removeProof(int id, HttpSession session) {
		imagesdao.deleteById(id);
		imagesdao.commit();
		
		return true;
	}

}
