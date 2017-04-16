package com.maxivetech.backoffice.service.user;

import javax.servlet.http.HttpSession;
import com.maxivetech.backoffice.entity.Notify;
import com.maxivetech.backoffice.util.Page;


public interface User2NotifyService {
	public Page<Notify> getPage(int pageNo, int pageSize,String urlFormat, HttpSession session);

	public boolean readNotify(int id, HttpSession session);

	public Page<Notify> getPageAll(int pageNo, int pageSize, String urlFormat, HttpSession session);
}
