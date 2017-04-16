package com.maxivetech.backoffice.dao.impl;


import java.io.File;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.dao.AttachmentDao;
import com.maxivetech.backoffice.entity.Attachments;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;

@Repository
public class AttachmentDaoImpl extends _BaseDaoImpl implements AttachmentDao {
	@Override
	public Class<?> classModel() {return Attachments.class;}

	@Override
	public List<Attachments> getFileImage(String ownertype,int id) {
		List<Attachments> list = this.createCriteria()
				.add(Restrictions.eq("ownerType",ownertype))
				.add(Restrictions.eq("ownerId",id))
				.list();
		return list != null && list.size() > 0 ? list : list;
	}
	
	@Override
	public List<Attachments> getAllTypeFile(Users user,String ownerType) {
		List<Attachments> list = this.createCriteria()
				.add(Restrictions.eq("user", user))
				.add(Restrictions.eq("ownerType",ownerType))
				.list();
		return list != null && list.size() > 0 ? list : list;
	}
	
	@Override
	public boolean deleteAttachment(int id){
		Attachments a=(Attachments) this.getById(id);
		if(a==null){
			return true;
		}else{
		    File file=new File(BackOffice.getInst().UPLOAD_REAL_ROOT+a.getPath());
			if(file.exists()){
				file.delete();
			}	
		    deleteById(id);
		    this.commit();
		    return true;
		}
	}
}
