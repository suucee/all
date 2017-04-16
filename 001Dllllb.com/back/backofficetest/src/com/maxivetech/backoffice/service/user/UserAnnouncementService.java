package com.maxivetech.backoffice.service.user;

import com.maxivetech.backoffice.entity.Announcement;
import com.maxivetech.backoffice.util.Page;

public interface UserAnnouncementService {
	Announcement getOne(int id);
	
	Page<Announcement> getPage(int pageNo, int pageSize, String urlFormat, String oederBy, String keyword);
}
