package com.maxivetech.backoffice.dao;

import javax.swing.text.AbstractDocument.Content;

import org.hibernate.sql.Insert;

import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Notify;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.util.Page;
import com.sun.jna.platform.win32.Netapi32Util.User;

public interface NotifyDao extends _BaseDao {
	public boolean insertNotify(String  content,Admins admins,Users users,int notifyId,String notifyType);
	
	
	public Page<Notify> getMyNotifyList(Admins admins,Users users,int pageNo, int pageSize, String urlFormat);
	

}
