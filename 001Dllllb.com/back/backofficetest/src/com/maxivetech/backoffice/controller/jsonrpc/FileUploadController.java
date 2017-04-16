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

@RequestMapping("/fileupload")
@Controller
public class FileUploadController {

	@Autowired
	private AttachmentDao attachmentDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserProfilesDao userProfilesDao;
	@Autowired
	private UserBankAccountDao userBankAccountDao;
    /**
     * @param code
     * @param req
     * @param res
     * @throws Exception
     */
	/*
	@RequestMapping(value = "{code}.do")
	protected void image(
			@PathVariable("code") String code,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		res.setContentType("text/html");
		res.setCharacterEncoding("UTF-8");
		String realDir = BackOfficeConsts.getInst().UPLOAD_REAL_ROOT;

		try {
			String realPath = realDir + "/" + code;
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
				String state = "SUCCESS";
				String realFileName = "";
				while (fii.hasNext()) {
					FileItemStream fis = fii.next();
					try {
						if (!fis.isFormField() && fis.getName().length() > 0) {
							System.out.println(2);
							fileName = fis.getName();
							Pattern reg = Pattern.compile("[.]jpg|png|jpeg|gif$");
							Matcher matcher = reg.matcher(fileName);
							if (!matcher.find()) {
								state = "jpg|png|jpeg|gif file ONLY!";
								break;
							}
							String extname = fileName.substring(fileName.lastIndexOf("."), fileName.length()).toLowerCase();
							realFileName = UUID.randomUUID().toString() + extname;
							url = realPath + "/" + realFileName;
							System.out.println(url);
							BufferedInputStream in = new BufferedInputStream(fis.openStream());// // 获得文件输入流
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
								attach.setPath(realPath.substring(BackOfficeConsts.getInst().UPLOAD_REAL_ROOT.length())+ "/"+ realFileName);
								attach.setType("image/" + extname.substring(1).replace("jpg", "jpeg"));
								attach.setOwnerType("");
								attach.setOwnerId(0);
								attach.setSortNum(50);
								attachmentDao.save(attach);
								attachmentDao.commit();
								
								res.setStatus(200);
								String retxt = "{id:" + attach.getId() + ",src:'" +url+ "', status:'success'}";
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
	*/
	
	@RequestMapping(value = "{name}.do")
	protected void userProfile(
			@PathVariable String name,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		res.setContentType("text/html");
		res.setCharacterEncoding("UTF-8");
		try {
			String filePath="";
			if(name.equals("cmsImg")){
				filePath = "cms_img";
			}
			if(name.equals("userProfile")){
				filePath = "user_profile";
			}
			String realPath = BackOffice.getInst().UPLOAD_REAL_ROOT+"/"+filePath;
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
				String state = "SUCCESS";
				String realFileName = "";
				String retxt="";
				while(fii.hasNext()) {
					FileItemStream fts=fii.next();
					try {
						if (!fts.isFormField() && fts.getName().length() > 0) {
							fileName = fts.getName();
							Pattern reg = Pattern.compile("[.]jpg|png|jpeg|gif$");
							Matcher matcher = reg.matcher(fileName);
							if (!matcher.find()) {
								state = "jpg|png|jpeg|gif file ONLY!";
								break;
							}
							System.out.println(fileName);
							String extname = fileName.substring(fileName.lastIndexOf("."), fileName.length()).toLowerCase();
							realFileName = UUID.randomUUID().toString() + extname;
							url = realPath + "/" + realFileName;
							BufferedInputStream in = new BufferedInputStream(((FileItemStream) fts).openStream());// 获得文件输入流
							FileOutputStream a = new FileOutputStream(new File(url));
							BufferedOutputStream output = new BufferedOutputStream(a);
							Streams.copy(in, output, true);	// 开始把文件写到你指定的上传文件夹
						
							//检查文件是否写好
							File fileNew = new File(url);
							if (fileNew.exists() && fileNew.isFile()){  
								System.out.println("上传成功！");
								String[] rf=realPath.split("/");
								retxt =filePath+"/"+rf[rf.length-2]+"/"+rf[rf.length-1]+"/"+realFileName;
								
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				res.setStatus(200);
				res.getWriter().print(retxt);
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}
	
	
	
	
	
	@RequestMapping(value = "check.do")
	protected void checkFile(
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		res.setContentType("text/html");
		res.setCharacterEncoding("UTF-8");
		String realDir = BackOffice.getInst().UPLOAD_REAL_ROOT;

		try {
			String realPath = realDir + "/cddcheck";
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
				String state = "SUCCESS";
				String realFileName = "";
				while (fii.hasNext()) {
					FileItemStream fis = fii.next();
					try {
						if (!fis.isFormField() && fis.getName().length() > 0) {
							fileName = fis.getName();
							fileType=fis.getContentType();
							Pattern reg = Pattern.compile("[.]jpg|png|jpeg|gif|txt|doc|docx|xls|xlsx|ppt|pptx|zip|rar$");
							Matcher matcher = reg.matcher(fileName);
							if (!matcher.find()) {
								state = "jpg|png|jpeg|gif|txt|doc|docx|xls|xlsx|ppt|pptx file ONLY!";
								break;
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
								//attach.setType("image/" + extname.substring(1).replace("jpg", "jpeg"));
								attach.setOwnerType("");
								attach.setOwnerId(0);
								attach.setSortNum(50);
								attachmentDao.save(attach);
								attachmentDao.commit();
								
								res.setStatus(200);
								String retxt = "{id:" + attach.getId() + ",src:'" +url+ "', status:'success'}";
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
	@RequestMapping(value = "userProfile.do")
	protected void userProfile(
			HttpServletRequest req, HttpServletResponse res,HttpSession session)
			throws Exception {
		res.setContentType("text/html");
		res.setCharacterEncoding("UTF-8");
		try {
			String filePath="user_profile";
			
			String realPath =BackOffice.getInst().UPLOAD_REAL_ROOT+"/user_profile";
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
				String fileType = "";
				String state = "SUCCESS";
				String realFileName = "";
				String retxt="";
				while(fii.hasNext()) {
					FileItemStream fts=fii.next();
					try {
						if (!fts.isFormField() && fts.getName().length() > 0) {
							fileName = fts.getName();
							fileType=fts.getContentType();
							Pattern reg = Pattern.compile("[.]jpg|png|jpeg|gif$");
							Matcher matcher = reg.matcher(fileName);
							if (!matcher.find()) {
								state = "jpg|png|jpeg|gif file ONLY!";
								break;
							}
							String extname = fileName.substring(fileName.lastIndexOf("."), fileName.length()).toLowerCase();
							realFileName = UUID.randomUUID().toString() + extname;
							url = realPath + "/" + realFileName;
							BufferedInputStream in = new BufferedInputStream(((FileItemStream) fts).openStream());// 获得文件输入流
							FileOutputStream a = new FileOutputStream(new File(url));
							BufferedOutputStream output = new BufferedOutputStream(a);
							Streams.copy(in, output, true);	// 开始把文件写到你指定的上传文件夹
						
							//检查文件是否写好
							File fileNew = new File(url);
							if (fileNew.exists() && fileNew.isFile()){
								int userId=SessionServiceImpl.getCurrent(session).getId();
								Users user=(Users) userDao.getById(userId);
								UserProfiles userProfiles=userProfilesDao.findUserProfilesByUser(user);
								
								Attachments attach = new Attachments();
								//判断userprofile是否为null
								if(userProfiles==null){
									UserProfiles up=new UserProfiles();
									up.setCardType("");
									up.setCompany("");
									up.setCreatTime(new Date());
									up.setPosition("");
									up.setUpdatedTime(null);
									up.setUser(user);
									up.setUserComment("");
									up.setUserEName("");
									up.setUserEsidentialAddress("");
									up.setUserIdCard("");
									up.setUserIndustry("");
									up.setUserName("");
									up.setUserNationality("");
									up.setUserYearsIncom("");
									up.getUser().setState("UNVERIFIED");
									
									userProfilesDao.save(userProfiles);
									userProfilesDao.commit();
								}
								
								List<Attachments> ac=attachmentDao.getFileImage(UserProfiles.getOwnertype(), userId);
									
								if(ac==null||ac.size()<=7){
								attach.setOwnerId(userProfiles.getId());
								attach.setCreatTime(now);
								attach.setFilesize(fileNew.length());
								attach.setName(realFileName);
								attach.setPath(realPath.substring(BackOffice.getInst().UPLOAD_REAL_ROOT.length())+ "/"+ realFileName);
								attach.setType(fileType);
								//attach.setType("image/" + extname.substring(1).replace("jpg", "jpeg"));
								attach.setOwnerType(UserProfiles.getOwnertype());
								attach.setUser(user);
								
									
								attach.setSortNum(50);
								attachmentDao.save(attach);
								attachmentDao.commit();
								}
								res.setStatus(200);
								retxt = "{id:" + attach.getId() + ",src:'" +url+ "', status:'success'}";
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
	@RequestMapping(value = "userProfile-{userId}.do")
	protected void adminUploadUserProfile(
			@PathVariable int  userId,
			HttpServletRequest req, HttpServletResponse res,HttpSession session)
					throws Exception {
		res.setContentType("text/html");
		res.setCharacterEncoding("UTF-8");
		try {
			String filePath="user_profile";
			
			String realPath =BackOffice.getInst().UPLOAD_REAL_ROOT+"/user_profile";
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
				String fileType = "";
				String state = "SUCCESS";
				String realFileName = "";
				String retxt="";
				while(fii.hasNext()) {
					FileItemStream fts=fii.next();
					try {
						if (!fts.isFormField() && fts.getName().length() > 0) {
							fileName = fts.getName();
							fileType=fts.getContentType();
							Pattern reg = Pattern.compile("[.]jpg|png|jpeg|gif$");
							Matcher matcher = reg.matcher(fileName);
							if (!matcher.find()) {
								state = "jpg|png|jpeg|gif file ONLY!";
								break;
							}
							String extname = fileName.substring(fileName.lastIndexOf("."), fileName.length()).toLowerCase();
							realFileName = UUID.randomUUID().toString() + extname;
							url = realPath + "/" + realFileName;
							BufferedInputStream in = new BufferedInputStream(((FileItemStream) fts).openStream());// 获得文件输入流
							FileOutputStream a = new FileOutputStream(new File(url));
							BufferedOutputStream output = new BufferedOutputStream(a);
							Streams.copy(in, output, true);	// 开始把文件写到你指定的上传文件夹
							
							//检查文件是否写好
							File fileNew = new File(url);
							if (fileNew.exists() && fileNew.isFile()){
								Users user=(Users) userDao.getById(userId);
								UserProfiles userProfiles=userProfilesDao.findUserProfilesByUser(user);
								
								Attachments attach = new Attachments();
								//判断userprofile是否为null
								if(userProfiles==null){
									UserProfiles up=new UserProfiles();
									up.setCardType("");
									up.setCompany("");
									up.setCreatTime(new Date());
									up.setPosition("");
									up.setUpdatedTime(null);
									up.setUser(user);
									up.setUserComment("");
									up.setUserEName("");
									up.setUserEsidentialAddress("");
									up.setUserIdCard("");
									up.setUserIndustry("");
									up.setUserName("");
									up.setUserNationality("");
									up.setUserYearsIncom("");
									up.getUser().setState("UNVERIFIED");
									
									userProfilesDao.save(userProfiles);
									userProfilesDao.commit();
								}
								
								List<Attachments> ac=attachmentDao.getFileImage(UserProfiles.getOwnertype(), userId);
								
								if(ac==null||ac.size()<=7){
									attach.setOwnerId(userProfiles.getId());
									attach.setCreatTime(now);
									attach.setFilesize(fileNew.length());
									attach.setName(realFileName);
									attach.setPath(realPath.substring(BackOffice.getInst().UPLOAD_REAL_ROOT.length())+ "/"+ realFileName);
									attach.setType(fileType);
									//attach.setType("image/" + extname.substring(1).replace("jpg", "jpeg"));
									attach.setOwnerType(UserProfiles.getOwnertype());
									attach.setUser(user);
									
									
									attach.setSortNum(50);
									attachmentDao.save(attach);
									attachmentDao.commit();
								}
								res.setStatus(200);
								retxt = "{id:" + attach.getId() + ",src:'" +url+ "', status:'success'}";
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
	
	@RequestMapping(value = "bankFile-{id}.do")
	protected void backFile(
			@PathVariable int  id,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		res.setContentType("text/html");
		res.setCharacterEncoding("UTF-8");
		String realDir = BackOffice.getInst().UPLOAD_REAL_ROOT;

		try {
			String realPath = realDir + "/bank";
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
							Pattern reg = Pattern.compile("[.]jpg|png|jpeg|gif$");
							Matcher matcher = reg.matcher(fileName);
							if (!matcher.find()) {
								String retxt = "{result:'文件格式不正确',status:'failed'}";
								res.getWriter().print(retxt);
								break;
							}
							List<Attachments> attachmentsList=attachmentDao.getFileImage(UserBankAccounts.getOwnertype(), id);
							if(id!=0&&attachmentsList.size()>=3){
								String retxt = "{result:'你已经已经上传了3张图片，不允许再次上传',status:'failed'}";
								res.getWriter().print(retxt);
								break;
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
								//attach.setType("image/" + extname.substring(1).replace("jpg", "jpeg"));
								attach.setOwnerType(UserBankAccounts.getOwnertype());
								attach.setOwnerId(id);
								attach.setSortNum(50);
								if(id!=0){
									UserBankAccounts userBankAccounts=(UserBankAccounts) userBankAccountDao.getById(id);
									attach.setUser(userBankAccounts.getUser());
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
	
	
	
	
	@RequestMapping(value = "{}-{id}.do")
	protected void uploadFile(
			@PathVariable int  id,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		res.setContentType("text/html");
		res.setCharacterEncoding("UTF-8");
		String realDir = BackOffice.getInst().UPLOAD_REAL_ROOT;

		try {
			String realPath = realDir + "/bank";
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
							Pattern reg = Pattern.compile("[.]jpg|png|jpeg|gif$");
							Matcher matcher = reg.matcher(fileName);
							if (!matcher.find()) {
								String retxt = "{result:'文件格式不正确',status:'failed'}";
								res.getWriter().print(retxt);
								break;
							}
							List<Attachments> attachmentsList=attachmentDao.getFileImage(UserBankAccounts.getOwnertype(), id);
							if(id!=0&&attachmentsList.size()>=3){
								String retxt = "{result:'你已经已经上传了3张图片，不允许再次上传',status:'failed'}";
								res.getWriter().print(retxt);
								break;
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
								attach.setOwnerType(UserBankAccounts.getOwnertype());
								attach.setOwnerId(id);
								attach.setSortNum(50);
								if(id!=0){
									UserBankAccounts userBankAccounts=(UserBankAccounts) userBankAccountDao.getById(id);
									attach.setUser(userBankAccounts.getUser());
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
