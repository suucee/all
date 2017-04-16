package com.maxivetech.backoffice.dao;

import java.util.List;

import com.maxivetech.backoffice.entity.Attachments;
import com.maxivetech.backoffice.entity.Users;

public interface AttachmentDao extends _BaseDao {

	public List<Attachments> getFileImage(String ownertype,int id);
	public List<Attachments> getAllTypeFile(Users user,String ownerType) ;
	public boolean deleteAttachment(int id);
}
