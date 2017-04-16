package com.maxivetech.backoffice.controller.jsonrpc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.http.Consts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.dao.AttachmentDao;
import com.maxivetech.backoffice.dao.UserBankAccountDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.entity.Attachments;
import com.maxivetech.backoffice.entity.CDDChecks;
import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.service.user.impl.SessionServiceImpl;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.MongoTimeoutException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.sun.jna.platform.win32.Netapi32Util.User;

@RequestMapping("/fileupload2")
@Controller
public class FileUpload2Controller {

	@Autowired
	private AttachmentDao attachmentDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserProfilesDao userProfilesDao;
	@Autowired
	private UserBankAccountDao userBankAccountDao;
   
	
	@RequestMapping(value = "{owerType}-{id}.do")
	protected void uploadFile(
			@PathVariable int  id,
			@PathVariable String owerType ,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		res.setContentType("text/html");
		res.setCharacterEncoding("UTF-8");
		String realDir = BackOffice.getInst().UPLOAD_REAL_ROOT;

		try {
			String realPath = realDir + "/"+owerType;
			Date now = new Date();
			// 判断"类型"路径是否存在，不存在则创建
			File dir = new File(realPath);
			if (!dir.isDirectory())
				dir.mkdir();
			// 判断"年月"路径是否存在，不存在则创建
			realPath += "/" + new SimpleDateFormat("yyyyMM").format(now);
			dir = new File(realPath);
			if (!dir.isDirectory())
				dir.mkdir();
			// 判断"日"路径是否存在，不存在则创建
			realPath += "/" + new SimpleDateFormat("dd").format(now);
			dir = new File(realPath);
			if (!dir.isDirectory())
				dir.mkdir();
			
			if (ServletFileUpload.isMultipartContent(req)) {
				DiskFileItemFactory dff = new DiskFileItemFactory();
				dff.setRepository(dir);
				dff.setSizeThreshold(10240000);
				ServletFileUpload sfu = new ServletFileUpload(dff);
				FileItemIterator fii = null;
				fii = sfu.getItemIterator(req);
				String url = ""; // 图片地址
				String fileName = "";
				String fileType= "";
				String realFileName = "";
				while (fii.hasNext()) {
					FileItemStream fis = fii.next();
					try {
						if (!fis.isFormField() && fis.getName().length() > 0) {
							fileName = fis.getName();
							fileType=fis.getContentType();
							switch (owerType) {
							case "user":
								Pattern reg1 = Pattern.compile("[.]jpg|png|jpeg|gif$");
								Matcher matcher1 = reg1.matcher(fileName);
								if (!matcher1.find()) {
									String retxt = "{result:'文件格式不正确',status:'failed'}";
									res.getWriter().print(retxt);
									return;
								}
								List<Attachments> attachmentsList1=attachmentDao.getFileImage(owerType, id);
								if(id!=0&&attachmentsList1.size()>=7){
									String retxt = "{result:'你已经已经上传了3张图片，不允许再次上传',status:'failed'}";
									res.getWriter().print(retxt);
									return;
								}
								 
								break;
							case "back":
								Pattern reg2 = Pattern.compile("[.]jpg|png|jpeg|gif$");
								Matcher matcher2 = reg2.matcher(fileName);
								if (!matcher2.find()) {
									String retxt = "{result:'文件格式不正确',status:'failed'}";
									res.getWriter().print(retxt);
									break;
								}
								List<Attachments> attachmentsList2=attachmentDao.getFileImage(owerType, id);
								if(id!=0&&attachmentsList2.size()>=3){
									String retxt = "{result:'你已经已经上传了3张图片，不允许再次上传',status:'failed'}";
									res.getWriter().print(retxt);
									return;
								}
								break;
							case "cddcheck":
								fileType=fis.getContentType();
								Pattern reg3 = Pattern.compile("[.]jpg|png|jpeg|gif|txt|doc|docx|xls|xlsx|ppt|pptx|zip|rar$");
								Matcher matcher3 = reg3.matcher(fileName);
								if (!matcher3.find()) {
									String retxt = "{result:'文件格式不正确',status:'failed'}";
									res.getWriter().print(retxt);
									return;
								}
								break;
							case "agent_agreement":
								fileType=fis.getContentType();
								Pattern reg4 = Pattern.compile("[.]jpg|png|jpeg|gif|txt|doc|docx|xls|xlsx|ppt|pptx|zip|rar$");
								Matcher matcher4 = reg4.matcher(fileName);
								if (!matcher4.find()) {
									String retxt = "{result:'文件格式不正确',status:'failed'}";
									res.getWriter().print(retxt);
									return;
								}
								break;
							default:
								String retxt = "{result:'文件归属不正确',status:'failed'}";
								res.getWriter().print(retxt);
								return;
							}
							
							
							
							String extname = fileName.substring(fileName.lastIndexOf("."), fileName.length()).toLowerCase();
							realFileName = UUID.randomUUID().toString() + extname;
							url = realPath + "/" + realFileName;
							System.out.println(url);
							BufferedInputStream in = new BufferedInputStream(fis.openStream());// 获得文件输入流
							FileOutputStream a = new FileOutputStream(new File(url));
							BufferedOutputStream output = new BufferedOutputStream(a);
							Streams.copy(in, output, true);	// 开始把文件写到你指定的上传文件夹
						
							//检查文件是否写好
							File fileNew = new File(url);
							if (fileNew.exists() && fileNew.isFile()){  
								
								//录入数据库
								Attachments attach = new Attachments();
								attach.setCreatTime(now);
								attach.setFilesize(fileNew.length());
								attach.setName(realFileName);
								attach.setPath(realPath.substring(BackOffice.getInst().UPLOAD_REAL_ROOT.length())+ "/"+ realFileName);
								attach.setType(fileType);
								attach.setOwnerId(id);
								attach.setSortNum(50);
								switch (owerType) {
								case "user":
									attach.setOwnerType(UserProfiles.getOwnertype());
									if(id!=0){
										UserProfiles uProfiles=(UserProfiles) userProfilesDao.getById(id);
										attach.setUser(uProfiles.getUser());
									}
									break;
								case "back":
									attach.setOwnerType(UserBankAccounts.getOwnertype());
									if(id!=0){
										UserBankAccounts userBankAccounts=(UserBankAccounts) userBankAccountDao.getById(id);
										attach.setUser(userBankAccounts.getUser());
									}
									break;
								case "cddcheck":
									attach.setOwnerType(CDDChecks.getOwnertype());
									break;
								case "agent_agreement":
									attach.setOwnerType(Users.getOwnertype());
									Users user=new Users();
									user.setId(id);
									attach.setUser(user);
									break;
								default:
									
									break;
								}
								attachmentDao.save(attach);
								attachmentDao.commit();
								
								res.setStatus(200);
								String retxt = "{id:" + attach.getId() + ",src:'" +attach.getPath()+ "', status:'success'}";
								res.getWriter().print(retxt);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}
	
}
