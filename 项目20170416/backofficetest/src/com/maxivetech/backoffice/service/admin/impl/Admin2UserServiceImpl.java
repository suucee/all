package com.maxivetech.backoffice.service.admin.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.annotation.CheckOperationPassword;
import com.maxivetech.backoffice.annotation.CheckRole;
import com.maxivetech.backoffice.annotation.Role;
import com.maxivetech.backoffice.dao.AdminDao;
import com.maxivetech.backoffice.dao.AttachmentDao;
import com.maxivetech.backoffice.dao.SettingDao;
import com.maxivetech.backoffice.dao.TokenDao;
import com.maxivetech.backoffice.dao.UserBalanceLogDao;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.dao.UserProfilesDao;
import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Attachments;
import com.maxivetech.backoffice.entity.Tokens;
import com.maxivetech.backoffice.entity.UserBalanceLogs;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoDepositUser;
import com.maxivetech.backoffice.pojo.PojoSession;
import com.maxivetech.backoffice.pojo.PojoTree;
import com.maxivetech.backoffice.service.admin.Admin2CheckUserService;
import com.maxivetech.backoffice.service.admin.Admin2UserService;
import com.maxivetech.backoffice.service.admin.AdminAgentService;
import com.maxivetech.backoffice.service.user.impl.SessionServiceImpl;
import com.maxivetech.backoffice.util.ForbiddenException;
import com.maxivetech.backoffice.util.HelperAuthority;
import com.maxivetech.backoffice.util.HelperDate;
import com.maxivetech.backoffice.util.HelperHttp;
import com.maxivetech.backoffice.util.HelperSalty;
import com.maxivetech.backoffice.util.Page;


@Service
@Transactional
public class Admin2UserServiceImpl implements Admin2UserService {
	@Autowired
	private AdminDao adminDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private TokenDao tokenDao;
	@Autowired
	private UserProfilesDao userProfilesDao;
	@Autowired
	private AttachmentDao attachmentDao;
    @Autowired
    private SettingDao settingDao;
    @Autowired
    private Admin2CheckUserService admin2CheckUserService;
    @Autowired
    private AdminAgentService adminAgentService;
    @Autowired
    private UserBalanceLogDao userBalanceLogDao;
	@Override
	public HashMap<String,Object> getTreeList(HttpSession session) {
		HashMap<String,Object> result=new HashMap<String,Object>();
		int user_id=1;
		if(HelperAuthority.isUser(session)){
			user_id=HelperAuthority.getId(session);
		}
		Users user=(Users) userDao.getById(user_id);
		List<PojoTree> customers=userDao.findTreeUserList(user.getPath(), user.getLevel(), 0,false);
		List<PojoTree> nextCustomers=userDao.findTreeUserList(user.getPath(), user.getLevel(), 1,false);
		List<PojoTree> allocationCustomers=userDao.findTreeUserList(user.getPath(), user.getLevel(), 1,true);
		result.put("rootId", user_id);
		result.put("customers", customers);
		result.put("nextCustomers", nextCustomers);
		result.put("allocationCustomers", allocationCustomers);
		return result;
		
	}
	
	@Override
	public  boolean saveUserTags(int user_id,String tags,HttpSession session){
		if(tags.length()>10){
			throw new RuntimeException("标签长度为10个字！");
		}
		Users user=(Users) userDao.getById(user_id);
		userDao.saveOrUpdate(user);
		userDao.commit();
		return true;
	}
	
	@Override
	public List<HashMap<String, Object>> getList(HttpSession session) {

		List<HashMap<String, Object>> list = userDao.createCriteria()
			.setProjection(Projections.projectionList()
					.add(Projections.property("id"), "id")
					.add(Projections.property("email"), "email")
					.add(Projections.property("mobile"), "mobile")
					.add(Projections.property("level"), "level")
					.add(Projections.property("vipGrade"), "vipGrade")
					.add(Projections.property("path"), "path")
				)
			.addOrder(Order.asc("path"))
			.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
			.list();
		
		for (HashMap<String, Object> map : list) {
			UserProfiles up = userProfilesDao.findUserProfilesByUser((Users)userDao.getById((int)map.get("id")));
			map.put("name", "["+BackOffice.getInst().companyHook.getVipGradeShowName((int)map.get("vipGrade"), (int)map.get("level")) + "]" + (up != null ? up.getUserName() : ""));
		}
		
		return list;
	}
	
	
	@Override
	public Users getOne(int userId, HttpSession session) {
		
		Users user = (Users) userDao.getById(userId);
		if(user == null){
			return null;
		}
		//清理
		{
			user.setPassword("");
			user.setPaymentPassword("");
			user.setSalty("");
			user.set_name(userProfilesDao.getUserName(user));
			user.set_vipGradeName(BackOffice.getInst().companyHook.getVipGradeShowName(user.getVipGrade(), user.getLevel()));
			
			if (user.getParent() != null) {
				user.set_up_id(user.getParent().getId());
				user.setParent(null);
			}
		}
		
		return user;
	}
	
	
	@Override
	public Page<PojoDepositUser> findUsersByKeyword(int pageNo, int pageSize, String urlFormat, String keyword, HttpSession session){
		ArrayList<Criterion> wheres = new ArrayList<>();
		
		Users userById = null;
		if(keyword.trim().length()>0 && StringUtils.isNumeric(keyword)){//尝试id搜索
			try{
				userById = (Users) userDao.getById(Integer.parseInt(keyword));
			}catch(Exception e){}
		}
		
		if(keyword.trim().length()>0){
			keyword ="%"+keyword.trim()+"%";
			
			//根据姓名（需要去UserProfiles表）把用户搜索出来，
			List <Criterion> wheres2UserProfile = new ArrayList<Criterion>();
			wheres2UserProfile.add(Restrictions.or(
					Restrictions.like("userName", keyword),
					Restrictions.like("userEName", keyword),
					Restrictions.like("userIdCard", keyword)));
			List<UserProfiles> userProfilesList = userProfilesDao.getAll(wheres2UserProfile, null, null, 0, 0).list();
			
			
			//根据邮箱和电话（需要去Users表）把用户搜索出来，
			List<Criterion>wheres2Users = new ArrayList<Criterion>();
			wheres2Users.add(Restrictions.or(
					Restrictions.like("email", keyword),
					Restrictions.like("mobile", keyword)));
			List<Users> usersList = userDao.getAll(wheres2Users, null, null, 0, 0).list();
			
			//当有多个用户时（从个人资料里面检出），分别设置查询条件
			List <SimpleExpression> simpleExpressionList = new ArrayList<>();
			if(userProfilesList != null && userProfilesList.size()>0){//有数据
				for(UserProfiles userProfile : userProfilesList){
					simpleExpressionList.add(Restrictions.eq("id", userProfile.getUser().getId()));
				}
			}
			//当有多个用户时（从用户列表里面检出），分别设置查询条件
			if(usersList != null && usersList.size()>0){//有数据
				for(Users user : usersList){
					simpleExpressionList.add(Restrictions.eq("id", user.getId()));
				}
			}
			
			//再根据用户，设置为OR条件，搜索出金单
			if(simpleExpressionList.size()>0){
				LogicalExpression logicalExpression = Restrictions.or(simpleExpressionList.get(0),simpleExpressionList.get(0));
				for (SimpleExpression simpleExpression : simpleExpressionList) {
					logicalExpression = Restrictions.or(logicalExpression, simpleExpression);
				}
				wheres.add(logicalExpression);
			}
			
		}
		
		// ORDER BY
		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.asc("id"));

		// PAGE
		Page<Users> page = (Page<Users>)userDao.getPage(wheres, orders, null, pageSize, pageNo, null);
		
		ArrayList<PojoDepositUser> usersList = new ArrayList<PojoDepositUser>();
		if(userById != null){
			usersList.add(new PojoDepositUser(userById.getId(),
						userProfilesDao.findUserProfilesByUser(userById).getUserName(), 
						userById.getEmail(), 
						userById.getMobile(), 
						userProfilesDao.findUserProfilesByUser(userById).getUserIdCard()));
			
		}
		for (Users user : page.getList()) {
			usersList.add(
					new PojoDepositUser(user.getId(),
						userProfilesDao.findUserProfilesByUser(user).getUserName(), 
						user.getEmail(), 
						user.getMobile(), 
						userProfilesDao.findUserProfilesByUser(user).getUserIdCard()));
		}
		Page<PojoDepositUser> pojoDepositUser = new Page<>(page.getTotalRows(), page.getPageSize(), page.getCurrentPage(),usersList);
		pojoDepositUser.generateButtons(urlFormat);
		return pojoDepositUser;
	}
	
	
	@Override
	@CheckRole(role = {Role.OperationsManager, Role.CustomerServiceStaff})
	@CheckOperationPassword(parameterIndex=0)
	public boolean adminUpdateProfile(String dopassword, int userId,String email,String mobile,String name, String ename, String cardType,
			String cardID, String countryCode, String address, String company,
			String userIndustry, String userYearsIncom, String position,String attachment_id,
			String scheme,
			int vipGrade,
			int level, String staffScheme,
			int upId,
			HttpSession session) {
		Users user = (Users) userDao.getById(userId);
		if (user == null) {
			throw new RuntimeException("用户不存在");
		}
		if (user.isDisable()||user.isFrozen()){
			throw new RuntimeException("账户已被冻结或禁用");
		}
		
		//检查Email格式
		if (!Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", email)) {
			throw new RuntimeException("电子邮箱地址不正确。");
		}
		//检查Email存在
		Users userByEmail = userDao.findByEmail(email);
		if (userByEmail != null && userByEmail.getId() != user.getId()) {
			throw new RuntimeException("该电子邮箱地址已经被占用！");
		}
		
		//检查手机号格式（此处是+[地区代码].[8-11位数字]）
		if (mobile == null || !Pattern.matches("^\\+[0-9]+\\.[0-9]{8,11}$", mobile)) {
			throw new RuntimeException("手机号不正确(须+[地区代码].[8-11位数字])！");
		}
		//检查手机号存在
		Users userByMobile = userDao.findByMobile(mobile);
		if (userByMobile != null && userByMobile.getId() != user.getId()) {
			throw new RuntimeException("该手机号已经被占用！");
		}
		
		// 修改用户基本信息表
		UserProfiles up = userProfilesDao.findUserProfilesByUser(user);
		
		if (up == null/* || user.getState().equals("VERIFIED")*/){
			up = new UserProfiles();
		}
		
		if (up != null) {
			List<Attachments> ac = attachmentDao.getFileImage(UserProfiles.getOwnertype(), up.getId());
			// 修改用户信息
			/*
			if ((ac == null || ac.size() < 3)) {
				up.getUser().setState("UNVERIFIED");
			}*/
			
			if(HelperAuthority.isOperationsManager(session)){//如果是运维经理。直接更改状态邮箱手机
				
				user.setEmail(email);
				user.setMobile(mobile);
				
				//状态的直接更改
				if(!scheme.equals(user.getState())){//状态发生更改
					if(scheme.equals("VERIFIED")){//前台将资料更改为已审核VERIFIED，但是用的审核接口中需要将state设置为：ACCEPTED
						scheme = "ACCEPTED";
					}
					boolean check = admin2CheckUserService.doCheck(userId, scheme, "", "", "", dopassword, attachment_id, session);
					if(! check){
						throw new RuntimeException("无法更改该用户的当前资料状态");
					}
				}
				
				
				if(staffScheme.length() > 0){//有可能设置为职工
					//员工级别
					if(level != user.getLevel()){//level发生更改
						adminAgentService.setStaff(userId, staffScheme, dopassword, session);
					}
				}else{//有可能设置代理级别
					if(vipGrade == 0){//设置为普通用户
						if(level > 3){//代理设置为普通用户
							adminAgentService.setAgent(userId, 0, dopassword, session);
						}else{//将员工设置为普通用户
							adminAgentService.setStaff(userId, "Non", dopassword, session);
						}
					}else{
						adminAgentService.setAgent(userId, vipGrade, dopassword, session);
					}
				}
				
				
				if(upId != user.getParent().getId()){//推荐人发生更改
					setParent(userId, upId, session);
				}
				
			}
			else{//客服，修改重要信息需要重新审核
				if (!up.getUser().getState().equals("VERIFIED") && 
						ac != null && 
						ac.size() >= settingDao.getInt("UserProfileMinImageNum")) {
					up.getUser().setState("AUDITING");
				}
				if(!up.getUserName().equals(name)||
						!user.getEmail().equals(email)||
						!user.getMobile().equals(mobile)||
						!up.getUserEName().equals(ename)||
						!up.getCardType().equals(cardType)||
						!up.getUserIdCard().equals(cardID)||
						!up.getUserNationality().equals(countryCode)||
						!up.getUserEsidentialAddress().equals(address)){
					user.setState("AUDITING");
				 	userDao.saveOrUpdate(user);
				}
			}
			up.setCardType(cardType);
			up.setCompany(company);
			up.setCreatTime(new Date());
			up.setPosition(position);
			up.setUpdatedTime(new Date());
			up.setUser(user);
			up.setUserComment("");
			up.setUserEName(ename);
			up.setUserEsidentialAddress(address);
			up.setUserIdCard(cardID);
			up.setUserIndustry(userIndustry);
			up.setUserName(name);
			up.setUserNationality(countryCode);
			up.setUserYearsIncom(userYearsIncom);
			
			userProfilesDao.saveOrUpdate(up);
			if(!attachment_id.equals("")){
			     String[] str=attachment_id.split(",");
				 int c=0;
			     for (int i = 0; i < str.length; i++) {
				     try {
						c=Integer.parseInt(str[i]);
					} catch (Exception e) {
						break;
					}
				    Attachments a=(Attachments) attachmentDao.getById(c);
				    if(a!=null){
				    	a.setOwnerId(up.getId());
				    	a.setOwnerType(UserProfiles.getOwnertype());
				    	a.setUser(up.getUser());
				    	attachmentDao.saveOrUpdate(a);
				    }
			     }
			 }
			userProfilesDao.commit();
			return true;
		}
		return false;
	}
	
	
	
	
	
	
	
	
	@Override
	public boolean updateProfile(int userId, String name, String ename, String cardType,
			String cardID, String countryCode, String address, String company,
			String userIndustry, String userYearsIncom, String position,String attachment_id, HttpSession session) {

		Users user = (Users) userDao.getById(userId);
		if (user == null) {
			throw new RuntimeException("用户不存在");
		}
		if (user.isDisable()||user.isFrozen()){
			throw new RuntimeException("账户已被冻结或禁用");
		}
		
		// 修改用户基本信息表
		UserProfiles up = userProfilesDao.findUserProfilesByUser(user);
		
		if (up == null/* || user.getState().equals("VERIFIED")*/){
			up = new UserProfiles();
		}
		
		if (up != null) {
			List<Attachments> ac = attachmentDao.getFileImage(UserProfiles.getOwnertype(), up.getId());
			// 修改用户信息
			/*
			if ((ac == null || ac.size() < 3)) {
				up.getUser().setState("UNVERIFIED");
			}*/
			if (!up.getUser().getState().equals("VERIFIED") && 
					ac != null && 
					ac.size() >= settingDao.getInt("UserProfileMinImageNum")) {
				up.getUser().setState("AUDITING");
			}
			if(!up.getUserName().equals(name)||
					!up.getUserEName().equals(ename)||
					!up.getCardType().equals(cardType)||
					!up.getUserIdCard().equals(cardID)||
					!up.getUserNationality().equals(countryCode)||
					!up.getUserEsidentialAddress().equals(address)){
				user.setState("AUDITING");
			 	userDao.saveOrUpdate(user);
			}
			up.setCardType(cardType);
			up.setCompany(company);
			up.setCreatTime(new Date());
			up.setPosition(position);
			up.setUpdatedTime(new Date());
			up.setUser(user);
			up.setUserComment("");
			up.setUserEName(ename);
			up.setUserEsidentialAddress(address);
			up.setUserIdCard(cardID);
			up.setUserIndustry(userIndustry);
			up.setUserName(name);
			up.setUserNationality(countryCode);
			up.setUserYearsIncom(userYearsIncom);
			
			userProfilesDao.saveOrUpdate(up);
			if(!attachment_id.equals("")){
			     String[] str=attachment_id.split(",");
				 int c=0;
			     for (int i = 0; i < str.length; i++) {
				     try {
						c=Integer.parseInt(str[i]);
					} catch (Exception e) {
						break;
					}
				    Attachments a=(Attachments) attachmentDao.getById(c);
				    if(a!=null){
				    	a.setOwnerId(up.getId());
				    	a.setOwnerType(UserProfiles.getOwnertype());
				    	a.setUser(up.getUser());
				    	attachmentDao.saveOrUpdate(a);
				    }
			     }
			 }
			userProfilesDao.commit();
			return true;
		}
		return false;
	}
	

	@CheckRole(role = {Role.OperationsManager})
	@Override
	public void modifyAccount(int userId, String email, String mobile,HttpSession session) {
		//检查权限
		if (!HelperAuthority.isOperationsManager(session)){
			throw new ForbiddenException();
		}
		
		Users user = (Users)userDao.getById(userId);
		if (user == null) {
			throw new RuntimeException("没有找到指定用户！");
		}
		//检查Email格式
		if (!Pattern.matches("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,10}$", email)) {
			throw new RuntimeException("非法的Email地址！");
		}
		//检查Email存在
		Users userByEmail = userDao.findByEmail(email);
		if (userByEmail != null && userByEmail.getId() != user.getId()) {
			throw new RuntimeException("Email已经被占用！");
		}
		//检查手机号格式
		if (mobile == null || !Pattern.matches("^\\+[0-9]+\\.[0-9]{8,11}$", mobile)) {
			throw new RuntimeException("非法的手机号(须+[地区代码].[8-11位数字])！");
		}
		//检查手机号存在
		Users userByMobile = userDao.findByMobile(mobile);
		if (userByMobile != null && userByMobile.getId() != user.getId()) {
			throw new RuntimeException("手机号已经被占用！");
		}
		user.setEmail(email);
		user.setMobile(mobile);
		userDao.update(user);
		userDao.commit();
	}
	@CheckRole(role = {Role.OperationsManager})
	@Override
	public void setParent(int userId,int upId,HttpSession session) {
		//检查推荐人
		Users user = (Users)userDao.getById(userId);
		Users userUp = (Users) userDao.getById(upId);
		if (userUp != null && userUp.getPath().indexOf(user.getPath()) >= 0) {
			throw new RuntimeException("推荐人不能是自己的客户！");
		}
		if (userId != 1)  {
			String oldPath = user.getPath();
			String newPath = userUp.getPath() + user.getId() + ",";
			user.setPath(newPath);
			user.setParent(userUp);
			if (user.getLevel() > 3) {	//以前是客户，不管挂到哪还是客户
				user.setLevel(Math.max(userUp.getLevel() + 1, 4));
			} else {	//以前是公司/经理/员工
				if (userUp.getLevel() + 1 > 3) {	//更新后将不是公司/经理/员工了
					throw new RuntimeException("不能把经理、员工直接移为其他推荐人的客户，请先将他设置为“普通用户”后再尝试移动。");
				} else {	//更新后仍然是公司/经理/员工
					user.setLevel(userUp.getLevel() + 1);
				}
			}
			userDao.update(user);
			userDao.getSession().createSQLQuery("UPDATE users SET path=REPLACE(path, '"+oldPath+"', '"+newPath+"') WHERE INSTR(path, '"+oldPath+"') = 1;").executeUpdate();
			userDao.commit();
		}
	}

	@Override
	public boolean removeImg(int userid, int id, HttpSession session) {

		Users user=(Users) userDao.getById(userid);
		
		if(user==null){
			return false;
		}
		attachmentDao.deleteById(id);
		
		UserProfiles up = userProfilesDao.findUserProfilesByUser(user);
		if (up != null) {
			List<Attachments> att=attachmentDao.getFileImage(UserProfiles.getOwnertype(), up.getId());
			System.out.println(att.size());
			//如果资料图片少于三张，状态改成资料不全,因为还未commit，所以该<4
//			if (att.size() < 1 + settingDao.getInt("UserProfileMinImageNum")){
//		    	user.setState("UNVERIFIED");
//		    	userDao.saveOrUpdate(user);
//		    }
		}
		
		attachmentDao.commit();
		
		return true;
	}

	@CheckRole(role = {Role.OperationsManager, Role.CustomerServiceStaff})
	@Override
	public PojoSession holdUser(int userId, String backurl, HttpSession session) {
		Users user = (Users)userDao.getById(userId);
		if (user == null) {throw new RuntimeException("找不到用户！");}
		if (user.isDisable()) {throw new RuntimeException("该用户已经被禁用！");}
		
		// 用户登录成功
		PojoSession pojo = new PojoSession(user);
		pojo.setRedirectUrl(backurl);
		pojo.setHoldAdminId(HelperAuthority.getId(session));
		session.setAttribute(SessionServiceImpl.SESSION_KEY, pojo);
		session.setAttribute(SessionServiceImpl.SESSION_USER_KEY, user);
		session.setAttribute(SessionServiceImpl.SESSION_ADMIN_KEY, null);

		tokenDao.deleteToken(tokenDao.findByUser(user));
		
		String ipAddress = (String) session.getAttribute("RemoteAddr");
		Tokens token = new Tokens();
		token.setAdmin(null);
		token.setCreatTime(new Date());
		token.setExpirationTime(null);
		token.setIpAddress(ipAddress == null ? "" : ipAddress);
		token.setLastAuthorizationTime(null);
		token.setUser(user);
		token.setUuid(UUID.randomUUID().toString());
		tokenDao.save(token);
		tokenDao.commit();

		pojo.setToken(token.getUuid());
		
		return pojo;
	}
	
	@CheckRole(role = {Role.OperationsManager, Role.ComplianceOfficer, 
			Role.FinancialSuperior, Role.FinancialStaff,
			Role.CustomerServiceStaff, Role.RiskManagementCommissioner})
	@Override
	public Page<HashMap<String, Object>> getPage(int pageNo, int pageSize, String urlFormat,
			 String state, String keyword, HttpSession session) {

		StringBuffer wheres = new StringBuffer();
		
		//关键字匹配
		if(!keyword.equals("")){
			wheres.append(" and  (instr(u.email,'"+keyword+"')>0 "
					+" or instr(u.mobile,'"+keyword+"')>0"
					+" or instr(u.referral_code,'"+keyword+"')>0 "
					+" or instr(up.name,'"+keyword+"')>0 "
					+" or instr(m.login,'"+keyword+"')>0 ) ");
		}
		switch (state) {
		
		case "all_stuff":
			wheres.append("and u.level <= 3");
			break;
		case "all_agent":
			wheres.append("and u.vip_grade > 0");
			break;
			
		case "unverified"://未认证
			wheres.append("and u.state = 'UNVERIFIED' and u.disable = 0");
			break;
		case "auditing"://待审核
			wheres.append("and u.state = 'AUDITING' and u.disable = 0");
			break;
		case "verified"://审核通过
			wheres.append("and u.state = 'VERIFIED' and u.disable = 0");
			break;
		case "rejected"://已驳回
			wheres.append("and u.state = 'REJECTED' and u.disable = 0");
			break;
		case "frozen"://已冻结
			wheres.append("and u.is_frozen = 1");
			break;
		case "disabled"://已禁用
			wheres.append("and u.disable = 1");
			break;

		default:
			break;
		}
		StringBuilder sqlBuilder=new StringBuilder();
		sqlBuilder.append(" select ");
		sqlBuilder.append("  u.id as uId, ");
		sqlBuilder.append(" u.state as state,  ");
		sqlBuilder.append("  u.email as email,  ");
		sqlBuilder.append("  u.mobile as mobile,  ");
		sqlBuilder.append("  u.disable as disable,  ");
		sqlBuilder.append("  u.is_frozen as is_frozen,  ");
		sqlBuilder.append("  u.vip_grade as vip_grade,  ");
		sqlBuilder.append("  u.level as level, ");
		sqlBuilder.append("  u.path as path, ");
		sqlBuilder.append("  up.name as _userName,  ");
		sqlBuilder.append("  u.referral_code as referralCode, ");
		sqlBuilder.append("  de.amount as deposits, ");
		sqlBuilder.append("  st.name as staff_name, ");
		sqlBuilder.append("  st.id as staff_id, ");
		sqlBuilder.append("  u.registration_time as registrationTime  ");
		sqlBuilder.append("  from users u  ");
		sqlBuilder.append("  left join user__profiles up on u.id=up.user_id   ");
		sqlBuilder.append("  left join mt4_users m on m.user_id=u.id   ");
		sqlBuilder.append("  left join ( select sum(dep.amount) as amount,dep.user_id as user_id  from deposits dep where dep.state='DEPOSITED' group by dep.user_id) de  ");
		sqlBuilder.append("  on de.user_id=u.id ");
		sqlBuilder.append("  left join (select up1.name,u1.id,u1.path,u1.level from users u1 left join user__profiles  up1 on up1.user_id=u1.id ) st  ");
		sqlBuilder.append("  on  (instr(u.path,st.path)=1 and st.level=3 and u.level>3) ");
		sqlBuilder.append("  or  (instr(u.path,st.path)=1 and st.level=2 and u.level=3) ");
		sqlBuilder.append("  or  (instr(u.path,st.path)=1 and st.level=1 and u.level=2) ");
		sqlBuilder.append("  where u.id is not null ");
		sqlBuilder.append(wheres);
		sqlBuilder.append("  GROUP BY u.id ORDER BY u.registration_time DESC ");
		System.out.println(sqlBuilder.toString());
		List<HashMap<String, Object>> list = userDao.getSession()
					.createSQLQuery(sqlBuilder.toString())
					.addScalar("uId" ,IntegerType.INSTANCE)
					.addScalar("state",StringType.INSTANCE)
					.addScalar("email",StringType.INSTANCE)
					.addScalar("mobile",StringType.INSTANCE)
					.addScalar("disable",BooleanType.INSTANCE)
					.addScalar("is_frozen",BooleanType.INSTANCE)
					.addScalar("vip_grade", IntegerType.INSTANCE)
					.addScalar("level", IntegerType.INSTANCE)
					.addScalar("_userName", StringType.INSTANCE)
					.addScalar("referralCode",StringType.INSTANCE)
					.addScalar("deposits", IntegerType.INSTANCE)
					.addScalar("staff_name",StringType.INSTANCE)
					.addScalar("staff_id", IntegerType.INSTANCE)
					.addScalar("registrationTime",TimestampType.INSTANCE)
					.setMaxResults(pageSize)
					.setFirstResult((pageNo-1)*pageSize)
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
		String countSQL = "select count(distinct u.id) from users u"
				   + " left join user__profiles up on u.id = up.user_id "
				   + " left join mt4_users m on m.user_id = u.id "
				   + " where u.id is not null "
				   + wheres;
		//返回的类型是BigInteger
		BigInteger bigVal = (BigInteger) userDao.getSession()
				.createSQLQuery(countSQL).uniqueResult();
		
		int totalRows=0;
		//BigInteger 转 int
		if (bigVal != null){
			totalRows = bigVal.intValue();	
		}
		Page<HashMap<String, Object>> page = new Page<HashMap<String, Object>>(
				totalRows, pageSize, pageNo, list);
		page.generateButtons(urlFormat);
		
		//
		for (HashMap<String, Object> map : page.getList()) {
			Integer agentId=null;
			String  agentName=null;
			if(map.get("uId")!=null){
				int id=(int)map.get("uId");
			    Users user=(Users) userDao.getById(id);
			    while(true){
				    if(user.getParent()!=null){
					    if(user.getParent().getVipGrade()!=0){
						    agentId=user.getParent().getId();
						    agentName=userProfilesDao.getUserName(user.getParent());
						    break;
					    }else{
					    	user=user.getParent();
					    }
				    }else{
				    	break;
				    }
			    }
			    map.put("agentId", agentId);
			    map.put("agentName", agentName);
			}
			map.put("_vipGradeName", BackOffice.getInst().companyHook.getVipGradeShowName((int)map.get("vip_grade"), (int)map.get("level")));
		}
		
		return page;
	}
	
	@Override
	public HashMap<String,Object> getAgentCoustomers(int agentUserId){
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		HashMap<String,Object> result=new HashMap<String,Object>();
		StringBuffer wheres = new StringBuffer();
		wheres.append("  and  u.id="+agentUserId);
		StringBuilder sqlBuilder=new StringBuilder();
		sqlBuilder.append(" select ");
		sqlBuilder.append("  u.id as uId, ");
		sqlBuilder.append("  u.up_id as up_id, ");
		sqlBuilder.append(" u.state as state,  ");
		sqlBuilder.append("  u.email as email,  ");
		sqlBuilder.append("  u.mobile as mobile,  ");
		sqlBuilder.append("  u.disable as disable,  ");
		sqlBuilder.append("  u.is_frozen as is_frozen,  ");
		sqlBuilder.append("  u.vip_grade as vip_grade,  ");
		sqlBuilder.append("  u.level as level, ");
		sqlBuilder.append("  u.path as path, ");
		sqlBuilder.append("  up.name as _userName,  ");
		sqlBuilder.append("  u.referral_code as referralCode, ");
		sqlBuilder.append("  de.amount as deposits, ");
		sqlBuilder.append("  st.name as staff_name, ");
		sqlBuilder.append("  st.id as staff_id, ");
		sqlBuilder.append("  u.registration_time as registrationTime  ");
		sqlBuilder.append("  from users u  ");
		sqlBuilder.append("  left join user__profiles up on u.id=up.user_id   ");
		sqlBuilder.append("  left join mt4_users m on m.user_id=u.id   ");
		sqlBuilder.append("  left join ( select sum(dep.amount) as amount,dep.user_id as user_id  from deposits dep where dep.state='DEPOSITED' group by dep.user_id) de  ");
		sqlBuilder.append("  on de.user_id=u.id ");
		sqlBuilder.append("  left join (select up1.name,u1.id,u1.path,u1.level from users u1 left join user__profiles  up1 on up1.user_id=u1.id ) st  ");
		sqlBuilder.append("  on  (instr(u.path,st.path)=1 and st.level=3 and u.level>3) ");
		sqlBuilder.append("  or  (instr(u.path,st.path)=1 and st.level=2 and u.level=3) ");
		sqlBuilder.append("  or  (instr(u.path,st.path)=1 and st.level=1 and u.level=2) ");
		sqlBuilder.append("  where u.id is not null ");
		sqlBuilder.append(wheres);
		sqlBuilder.append("  GROUP BY u.id  ");

		System.out.println(sqlBuilder.toString());
		HashMap<String, Object> agentitem = (HashMap<String, Object>) userDao.getSession()
					.createSQLQuery(sqlBuilder.toString())
					.addScalar("uId" ,IntegerType.INSTANCE)
					.addScalar("up_id" ,IntegerType.INSTANCE)
					.addScalar("state",StringType.INSTANCE)
					.addScalar("email",StringType.INSTANCE)
					.addScalar("mobile",StringType.INSTANCE)
					.addScalar("disable",BooleanType.INSTANCE)
					.addScalar("is_frozen",BooleanType.INSTANCE)
					.addScalar("vip_grade", IntegerType.INSTANCE)
					.addScalar("level", IntegerType.INSTANCE)
					.addScalar("_userName", StringType.INSTANCE)
					.addScalar("referralCode",StringType.INSTANCE)
					.addScalar("deposits", IntegerType.INSTANCE)
					.addScalar("staff_name",StringType.INSTANCE)
					.addScalar("staff_id", IntegerType.INSTANCE)
					.addScalar("registrationTime",TimestampType.INSTANCE)
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.uniqueResult();

		Integer agentId=null;
		String  agentName=null;
		if(agentitem.get("uId")!=null){
			int id=(int)agentitem.get("uId");
		    Users user=(Users) userDao.getById(id);
		    while(true){
			    if(user.getParent()!=null){
				    if(user.getParent().getVipGrade()!=0){
					    agentId=user.getParent().getId();
					    agentName=userProfilesDao.getUserName(user.getParent());
					    break;
				    }else{
				    	user=user.getParent();
				    }
			    }else{
			    	break;
			    }
		    }
		    agentitem.put("agentId", agentId);
		    agentitem.put("agentName", agentName);
		}
		agentitem.put("_vipGradeName", BackOffice.getInst().companyHook.getVipGradeShowName((int)agentitem.get("vip_grade"), (int)agentitem.get("level")));
		
		Users agent=(Users) userDao.getById(agentUserId);
		StringBuffer wheres1 = new StringBuffer();
		wheres1.append("  and  instr(u.path,'"+agent.getPath()+"')=1 and u.path!='"+agent.getPath()+"'");
		StringBuilder sqlBuilder1=new StringBuilder();
		sqlBuilder1.append(" select ");
		sqlBuilder1.append("  u.id as uId, ");
		sqlBuilder1.append("  u.up_id as up_id, ");
		sqlBuilder1.append(" u.state as state,  ");
		sqlBuilder1.append("  u.email as email,  ");
		sqlBuilder1.append("  u.mobile as mobile,  ");
		sqlBuilder1.append("  u.disable as disable,  ");
		sqlBuilder1.append("  u.is_frozen as is_frozen,  ");
		sqlBuilder1.append("  u.vip_grade as vip_grade,  ");
		sqlBuilder1.append("  u.level as level, ");
		sqlBuilder1.append("  u.path as path, ");
		sqlBuilder1.append("  up.name as _userName,  ");
		sqlBuilder1.append("  u.referral_code as referralCode, ");
		sqlBuilder1.append("  de.amount as deposits, ");
		sqlBuilder1.append("  st.name as staff_name, ");
		sqlBuilder1.append("  st.id as staff_id, ");
		sqlBuilder1.append("  u.registration_time as registrationTime  ");
		sqlBuilder1.append("  from users u  ");
		sqlBuilder1.append("  left join user__profiles up on u.id=up.user_id   ");
		sqlBuilder1.append("  left join mt4_users m on m.user_id=u.id   ");
		sqlBuilder1.append("  left join ( select sum(dep.amount) as amount,dep.user_id as user_id  from deposits dep where dep.state='DEPOSITED' group by dep.user_id) de  ");
		sqlBuilder1.append("  on de.user_id=u.id ");
		sqlBuilder1.append("  left join (select up1.name,u1.id,u1.path,u1.level from users u1 left join user__profiles  up1 on up1.user_id=u1.id ) st  ");
		sqlBuilder1.append("  on  (instr(u.path,st.path)=1 and st.level=3 and u.level>3) ");
		sqlBuilder1.append("  or  (instr(u.path,st.path)=1 and st.level=2 and u.level=3) ");
		sqlBuilder1.append("  or  (instr(u.path,st.path)=1 and st.level=1 and u.level=2) ");
		sqlBuilder1.append("  where u.id is not null ");
		sqlBuilder1.append(wheres1);
		sqlBuilder1.append("  GROUP BY u.id ORDER BY u.id asc ");

		System.out.println(sqlBuilder1.toString());
		List<HashMap<String, Object>> coustomers = userDao.getSession()
					.createSQLQuery(sqlBuilder1.toString())
					.addScalar("uId" ,IntegerType.INSTANCE)
					.addScalar("up_id" ,IntegerType.INSTANCE)
					.addScalar("state",StringType.INSTANCE)
					.addScalar("email",StringType.INSTANCE)
					.addScalar("mobile",StringType.INSTANCE)
					.addScalar("disable",BooleanType.INSTANCE)
					.addScalar("is_frozen",BooleanType.INSTANCE)
					.addScalar("vip_grade", IntegerType.INSTANCE)
					.addScalar("level", IntegerType.INSTANCE)
					.addScalar("_userName", StringType.INSTANCE)
					.addScalar("referralCode",StringType.INSTANCE)
					.addScalar("deposits", IntegerType.INSTANCE)
					.addScalar("staff_name",StringType.INSTANCE)
					.addScalar("staff_id", IntegerType.INSTANCE)
					.addScalar("registrationTime",TimestampType.INSTANCE)
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();

		for (HashMap<String, Object> map : coustomers) {
			Integer agentId1=null;
			String  agentName1=null;
			if(map.get("uId")!=null){
				int id=(int)map.get("uId");
			    Users user=(Users) userDao.getById(id);
			    while(true){
				    if(user.getParent()!=null){
					    if(user.getParent().getVipGrade()!=0){
						    agentId1=user.getParent().getId();
						    agentName1=userProfilesDao.getUserName(user.getParent());
						    break;
					    }else{
					    	user=user.getParent();
					    }
				    }else{
				    	break;
				    }
			    }
			    map.put("agentId", agentId1);
			    map.put("agentName", agentName1);
			}
			map.put("_vipGradeName", BackOffice.getInst().companyHook.getVipGradeShowName((int)map.get("vip_grade"), (int)map.get("level")));
		}
		result.put("agent", agentitem);
		result.put("coustomers", coustomers);
		return result;
	}
	
	
	@Override
	public Page<UserBalanceLogs> getBalanceLogs(int pageNo, int pageSize, String urlFormat, HttpSession session) {

		Users user = (Users) userDao.getById(SessionServiceImpl.getCurrent(
				session).getId());
		if (user == null) {
			return null;
		}

		ArrayList<Criterion> wheres = new ArrayList<Criterion>();
		wheres.add(Restrictions.eq("user", user));

		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("creatTime"));

		Page<UserBalanceLogs> page = (Page<UserBalanceLogs>) userBalanceLogDao.getPage(wheres, orders, null, pageSize, pageNo, null);
		page.generateButtons(urlFormat);

		// 清理
		for (UserBalanceLogs ubl : page.getList()) {
			ubl.setUser(null);
		}

		return page;
	}

	@Override
	public boolean addMT4User(int userId, HttpSession session) {
		// TODO Auto-generated method stub
		return false;
	}

}
