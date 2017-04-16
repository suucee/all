package com.maxivetech.backoffice.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.dao.UserDao;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoMT4Rebate;
import com.maxivetech.backoffice.pojo.PojoMonthRebate;
import com.maxivetech.backoffice.pojo.PojoRankUserCensus;
import com.maxivetech.backoffice.pojo.PojoTree;
import com.maxivetech.backoffice.pojo.PojoUserRebate;
import com.sun.net.httpserver.HttpsConfigurator;

@Repository
public class UserDaoImpl extends _BaseDaoImpl implements UserDao {
	@Override
	public Class<?> classModel() {
		return Users.class;
	}

	@Override
	public Users findByEmail(String email) {
		List<Users> list = this.createCriteria()
				.add(Restrictions.eq("email", email)).setMaxResults(1).list();

		return list != null && list.size() > 0 ? list.get(0) : null;
	}

	@Override
	public Users findByMobile(String mobile) {
		List<Users> list = this.createCriteria()
				.add(Restrictions.eq("mobile", mobile)).setMaxResults(1).list();

		return list != null && list.size() > 0 ? list.get(0) : null;
	}

	@Override
	public Users findByUserCode(String userCode) {
		List<Users> list = this.createCriteria()
				.add(Restrictions.eq("referralCode", userCode))
				.setMaxResults(1).list();

		return list != null && list.size() > 0 ? list.get(0) : null;
	}

	
	@Override
	public List<HashMap<String,Object>> findByUserKeyWord(String keyword) {
		String upperKeyWord=keyword.toUpperCase();
		StringBuilder onehqlbuilder = new StringBuilder("select");
		onehqlbuilder.append(" u.id as user_id,");
		onehqlbuilder.append(" u.email as email,");
		onehqlbuilder.append(" u.state as state,");
		onehqlbuilder.append(" u.up_id as up_id,");
		onehqlbuilder.append(" u.path as path,");
		onehqlbuilder.append(" u.level as level,");
		onehqlbuilder.append(" u.referral_code as user_code,");
		onehqlbuilder.append(" u.registration_time as registrationTime");
		onehqlbuilder.append(" from users u");
		onehqlbuilder.append(" left join user__profiles uf");
		onehqlbuilder.append(" on uf.user_id=u.id");
		onehqlbuilder.append(" where");
		onehqlbuilder.append(" instr(u.referral_code,?)>0  or");
		onehqlbuilder.append(" instr(u.email ,?)>0  or");
		onehqlbuilder.append(" instr(u.mobile,?)>0  or");
		onehqlbuilder.append(" instr(uf.user_ename,?)>0  or");
		onehqlbuilder.append(" instr(uf.name,?)>0");
     	String onehql = onehqlbuilder.toString();
		Query query = this.getSession().createSQLQuery(onehql)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		query.setString(0, upperKeyWord);
		query.setString(1, keyword);
		query.setString(2, keyword);
		query.setString(3, keyword);
		query.setString(4, keyword);
		List<HashMap<String, Object>> users = query.list();
		return users!=null&&users.size()>0?users:null;
		
	}

	@Override
	public HashMap<String, Object> getOneHashUser(int user_id) {
		StringBuilder onehqlbuilder = new StringBuilder("select");
		onehqlbuilder.append(" u.id as user_id,");
		onehqlbuilder.append(" u.email as email,");
		onehqlbuilder.append(" u.state as state,");
		onehqlbuilder.append(" u.up_id as up_id,");
		onehqlbuilder.append(" u.referral_code as user_code,");
		onehqlbuilder.append(" uf.name as user_name,");
		onehqlbuilder.append(" u.registration_time as registrationTime");
		onehqlbuilder.append(" from users u");
		onehqlbuilder.append(" left join user__profiles uf");
		onehqlbuilder.append(" on uf.user_id=u.id");
		onehqlbuilder.append(" where  u.id=?");
     	String onehql = onehqlbuilder.toString();
		Query query = this.getSession().createSQLQuery(onehql)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		query.setInteger(0, user_id);
		List<HashMap<String, Object>> users = query.list();
		return users != null && users.size() > 0 ? users.get(0) : null;
	}

	@Override
	public List<HashMap<String, Object>> findRankUserList(String userpath, int userlevel, int plusLevel) {
		List<HashMap<String, Object>> userlist = new ArrayList<HashMap<String, Object>>();
			StringBuilder onehqlbuilder = new StringBuilder("select");
			onehqlbuilder.append(" u.id as user_id,");
			onehqlbuilder.append(" u.email as email,");
			onehqlbuilder.append(" u.mobile as mobile,");
			onehqlbuilder.append(" u.state as state,");
			onehqlbuilder.append(" u.up_id as up_id,");
			onehqlbuilder.append(" u.referral_code as user_code,");
			onehqlbuilder.append(" u.vip_grade as vip_grade,");
			onehqlbuilder.append(" u.level as level,");
			onehqlbuilder.append(" uf.name as user_name,");
			onehqlbuilder.append(" u.registration_time as registrationTime");
			onehqlbuilder.append(" from users u");
			onehqlbuilder.append(" left join (select * from (select * from user__profiles order by id desc) ufp group by user_id order  by user_id  desc)  uf");
			onehqlbuilder.append(" on uf.user_id=u.id");
			if (plusLevel == 0) {
			   onehqlbuilder.append(" where disable = 0 and instr(u.path,?) = 1");
			} else {
			   onehqlbuilder.append(" where disable = 0 and instr(u.path,?) = 1 and level <= ?");
			}
			onehqlbuilder.append(" ORDER BY level");
		
			String onesql = onehqlbuilder.toString();
			Query query = this.getSession()
					.createSQLQuery(onesql)
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			query.setString (0, userpath);
			if (plusLevel != 0){
				query.setInteger(1, (userlevel + plusLevel));
			}
			userlist= query.list();
			
		for (HashMap<String, Object> map : userlist) {
			map.put("_vipGradeName", BackOffice.getInst().companyHook.getVipGradeShowName((int)map.get("vip_grade"), (int)map.get("level")));
		}
			
		return userlist;
	}

	
	@Override
	public List<HashMap<String, Object>> findRankUserList(String userpath, int userlevel, int plusLevel,String keyWord) {
		List<HashMap<String, Object>> userlist = new ArrayList<HashMap<String, Object>>();
			StringBuilder onehqlbuilder = new StringBuilder("select");
			onehqlbuilder.append(" u.id as user_id,");
			onehqlbuilder.append(" u.email as email,");
			onehqlbuilder.append(" u.mobile as mobile,");
			onehqlbuilder.append(" u.state as state,");
			onehqlbuilder.append(" u.up_id as up_id,");
			onehqlbuilder.append(" u.referral_code as user_code,");
			onehqlbuilder.append(" u.vip_grade as vip_grade,");
			onehqlbuilder.append(" u.level as level,");
			onehqlbuilder.append(" uf.name as user_name,");
			onehqlbuilder.append(" u.registration_time as registrationTime");
			onehqlbuilder.append(" from users u");
			onehqlbuilder.append(" left join (select * from (select * from user__profiles order by id desc) ufp group by user_id order  by user_id  desc)  uf");
			onehqlbuilder.append(" on uf.user_id=u.id");
			onehqlbuilder.append(" where disable = 0 ");
			if (plusLevel == 0) {
			   onehqlbuilder.append("  and instr(u.path,?) = 1");
			} else {
			   onehqlbuilder.append("  and instr(u.path,?) = 1 and level <= ?");
			}
			if(!keyWord.equals("")){
				 onehqlbuilder.append(" and (u.email  like '%"+keyWord+"%' or u.email  like '%"+keyWord+"%' or u.mobile  like '%"+keyWord+"%' or uf.name  like '%"+keyWord+"%' or u.id  like '%"+keyWord+"%')");
			}
			onehqlbuilder.append(" ORDER BY level");
		
			String onesql = onehqlbuilder.toString();
			System.out.println(onesql);
			Query query = this.getSession()
					.createSQLQuery(onesql)
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			query.setString (0, userpath);
			if (plusLevel != 0){
				query.setInteger(1, (userlevel + plusLevel));
			}
			userlist= query.list();
			
		for (HashMap<String, Object> map : userlist) {
			map.put("_vipGradeName", BackOffice.getInst().companyHook.getVipGradeShowName((int)map.get("vip_grade"), (int)map.get("level")));
		}
			
		return userlist;
	}
	
	@Override
	public HashMap<String, Object> getOneUserBySQL(String sql){
		Query query = this.getSession()
				.createSQLQuery(sql)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<HashMap<String, Object>> result =query.list();
		return result !=null&&result.size()>0?result.get(0):null;
	}

	@Override
	public List<Users> getCustomers(Users user) {
		return this.createCriteria()
			.add(Restrictions.like("path", user.getPath() + "%"))
			.list();
	}
	
	
	@Override
	public List<PojoTree> findTreeUserList(String userpath, int userlevel, int plusLevel,boolean allocation) {
		List<PojoTree> userlist = new ArrayList<PojoTree>();
			StringBuilder onehqlbuilder = new StringBuilder("select");
			onehqlbuilder.append(" u.id as id,");
			onehqlbuilder.append(" u.up_id as pId,");
			onehqlbuilder.append(" u.vip_grade as vipGrade,");
			onehqlbuilder.append(" u.level as userLevel,");
			onehqlbuilder.append(" u.email as email,");
			onehqlbuilder.append(" u.mobile as mobile,");
			onehqlbuilder.append(" u.tags as tags,");
			onehqlbuilder.append(" uf.name as name,");
			onehqlbuilder.append(" uf.ename as ename");
			onehqlbuilder.append(" from users u");
			onehqlbuilder.append(" left join (select * from (select * from user__profiles order by id desc) ufp group by user_id order  by user_id  desc)  uf");
			onehqlbuilder.append(" on uf.user_id=u.id");
			if(allocation){
				 onehqlbuilder.append(" where allocation = 1");
			}else{
				 onehqlbuilder.append(" where allocation = 0");
			}
			if (plusLevel == 0) {
			   onehqlbuilder.append("  and disable = 0 and instr(u.path,?) = 1");
			} else {
			   onehqlbuilder.append("  and disable = 0 and instr(u.path,?) = 1 and level <= ?");
			}
			onehqlbuilder.append(" ORDER BY level");
			String onesql = onehqlbuilder.toString();
		    System.out.println(onesql); 
			Query query = this.getSession()
					.createSQLQuery(onesql)
					.addScalar("id", IntegerType.INSTANCE)
					.addScalar("pId", IntegerType.INSTANCE)
					.addScalar("vipGrade", IntegerType.INSTANCE)
					.addScalar("userLevel", IntegerType.INSTANCE)
					.addScalar("name", StringType.INSTANCE)
					.addScalar("email", StringType.INSTANCE)
					.addScalar("mobile", StringType.INSTANCE)
					.addScalar("tags", StringType.INSTANCE)
					.addScalar("ename", StringType.INSTANCE)
					.setResultTransformer(Transformers.aliasToBean(PojoTree.class));
			query.setString (0, userpath);
			if (plusLevel != 0){
				query.setInteger(1, (userlevel + plusLevel));
			}
			userlist= query.list();
			
		for (PojoTree pt : userlist) {
			pt.setGrade(BackOffice.getInst().companyHook.getVipGradeShowName(pt.getVipGrade(),pt.getUserLevel()));
		}
			
		return userlist;
	}
	
	
	
	
	//v2.0
	
	@Override
	public List<PojoRankUserCensus> findRankUserCensusList(Users user, int plusLevel,String keyWord) {
		List<PojoRankUserCensus> userlist = new ArrayList<PojoRankUserCensus>();
			StringBuilder onehqlbuilder = new StringBuilder("select");
			onehqlbuilder.append(" u.id as userId,");
			onehqlbuilder.append(" u.state as state,");
			onehqlbuilder.append(" u.up_id as upId,");
			onehqlbuilder.append(" u.vip_grade as vipGrade,");
			onehqlbuilder.append(" u.level as level,");
			onehqlbuilder.append(" uf.name as userName,");
			onehqlbuilder.append(" de.amount as  depositAmount,");
			onehqlbuilder.append(" wi.amount as  withdrawalAmount,");
			onehqlbuilder.append(" rr.amount as  rebateAmount,");
			onehqlbuilder.append(" rr.volume as  volume,");
			onehqlbuilder.append(" u.registration_time as registrationTime");
			onehqlbuilder.append(" from users u");
			onehqlbuilder.append(" left join (select * from (select * from user__profiles order by id desc) ufp group by user_id order  by user_id  desc)  uf  on uf.user_id=u.id");
			onehqlbuilder.append(" left join (select sum(amount) as amount,user_id from deposits where state='DEPOSITED' group  by user_id ) de on de.user_id=u.id");
			onehqlbuilder.append(" left join (select sum(amount) as amount,user_id from withdrawals where state='REMITTED' group  by user_id ) wi on wi.user_id=u.id");
			onehqlbuilder.append(" left join (select sum(r.amount) as amount,sum(r.volume) as volume,r.order_user_id from (select * from rebate_records where user_id="+user.getId()+") r group by order_user_id) rr on rr.order_user_id=u.id");
			onehqlbuilder.append(" where disable = 0 and instr(u.path,?) = 1");
			if(!keyWord.equals("")){
				onehqlbuilder.append("  and (uf.name  like  '%"+keyWord+"%'  or  u.email like  '%"+keyWord+"%'  or  u.mobile like  '%"+keyWord+"%' )");
			}
			if (plusLevel != 0) {
			    onehqlbuilder.append("   and level <= ?");
			}
			onehqlbuilder.append(" ORDER BY level");
		
			String onesql = onehqlbuilder.toString();
			System.out.println(onesql);
			Query query = this.getSession()
					.createSQLQuery(onesql)
					.addScalar("userId",IntegerType.INSTANCE)
					.addScalar("upId",IntegerType.INSTANCE)
					.addScalar("vipGrade",IntegerType.INSTANCE)
					.addScalar("level",IntegerType.INSTANCE)
					.addScalar("userName",StringType.INSTANCE)
					.addScalar("state",StringType.INSTANCE)
					.addScalar("depositAmount",DoubleType.INSTANCE)
					.addScalar("withdrawalAmount",DoubleType.INSTANCE)
					.addScalar("rebateAmount",DoubleType.INSTANCE)
					.addScalar("volume",DoubleType.INSTANCE)
					.addScalar("registrationTime",TimestampType.INSTANCE)
					.setResultTransformer(Transformers.aliasToBean(PojoRankUserCensus.class));
			query.setString (0, user.getPath());
			if (plusLevel != 0){
				query.setInteger(1, (user.getLevel() + plusLevel));
			}
			userlist= query.list();
			
		for (PojoRankUserCensus pojo : userlist) {
			pojo.setVipGradeName(BackOffice.getInst().companyHook.getVipGradeShowName(pojo.getVipGrade(), pojo.getLevel()));
		}
			
		return userlist;
	}
	
	@Override
	public List<PojoMonthRebate> getMonthRebate(Users user){
		List<PojoMonthRebate> result = new ArrayList<PojoMonthRebate>();
		StringBuilder sqlBuilder=new StringBuilder();
		sqlBuilder.append(" SELECT");
		sqlBuilder.append(" convert(creat_time,char(7)) as monthstr,");
		sqlBuilder.append(" sum(amount)as rebateCount");
		sqlBuilder.append(" FROM rebate_records ");
		sqlBuilder.append(" where user_id="+user.getId()+" group by monthstr  order by convert(creat_time,char(7)) desc ");
		String sql=sqlBuilder.toString();
		Query query = this.getSession()
				.createSQLQuery(sql)
				.addScalar("monthstr",StringType.INSTANCE)
				.addScalar("rebateCount",DoubleType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(PojoMonthRebate.class));
		result=query.list();
		return result;
	}
	
	@Override
	public List<PojoMonthRebate> getUserMonthRebate(Users user,Users orderUser){
		List<PojoMonthRebate> result = new ArrayList<PojoMonthRebate>();
		StringBuilder sqlBuilder=new StringBuilder();
		sqlBuilder.append(" SELECT");
		sqlBuilder.append(" convert(creat_time,char(7)) as monthstr,");
		sqlBuilder.append(" sum(amount)as rebateCount");
		sqlBuilder.append(" FROM rebate_records ");
		sqlBuilder.append(" where user_id="+user.getId()+" and order_user_id="+orderUser.getId()+" group by monthstr  order by convert(creat_time,char(7)) desc ");
		String sql=sqlBuilder.toString();
		Query query = this.getSession()
				.createSQLQuery(sql)
				.addScalar("monthstr",StringType.INSTANCE)
				.addScalar("rebateCount",DoubleType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(PojoMonthRebate.class));
		result=query.list();
		return result;
	}
	@Override
	public List<PojoMT4Rebate> getMT4Rebate(Users user){
		List<PojoMT4Rebate>  result=new ArrayList<PojoMT4Rebate>();
		StringBuilder sqlBuilder=new StringBuilder();
		sqlBuilder.append(" SELECT");
		sqlBuilder.append(" sum(amount) as rebate,");
		sqlBuilder.append(" sum(volume) as volume,");
		sqlBuilder.append(" rr.login as login,");
		sqlBuilder.append(" rr.order_user_id   as OrderUserId");
		sqlBuilder.append(" FROM rebate_records  rr");
		sqlBuilder.append(" left join (select * from (select * from user__profiles order by id desc) ufp group by user_id order  by user_id  desc)  uf on uf.user_id=rr.order_user_id");
		sqlBuilder.append(" where rr.user_id=1 group by rr.login;");
		String sql=sqlBuilder.toString();
		Query query = this.getSession()
				.createSQLQuery(sql)
				.addScalar("OrderUserId",IntegerType.INSTANCE)
				.addScalar("rebate",DoubleType.INSTANCE)
				.addScalar("volume",DoubleType.INSTANCE)
				.addScalar("login",IntegerType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(PojoMT4Rebate.class));
		result=query.list();
		return result;
	}
	@Override
	public List<PojoUserRebate> getUserRebate(Users user){
		List<PojoUserRebate>  result=new ArrayList<PojoUserRebate>();
		StringBuilder sqlBuilder=new StringBuilder();
		sqlBuilder.append(" SELECT");
		sqlBuilder.append(" sum(amount) as rebate,");
		sqlBuilder.append(" sum(volume) as volume,");
		sqlBuilder.append(" uf.name as name,");
		sqlBuilder.append(" rr.order_user_id  as OrderUserId");
		sqlBuilder.append(" FROM rebate_records  rr");
		sqlBuilder.append(" left join (select * from (select * from user__profiles order by id desc) ufp group by user_id order  by user_id  desc)  uf on uf.user_id=rr.order_user_id");
		sqlBuilder.append(" where rr.user_id="+user.getId()+" group by rr.order_user_id;");
		String sql=sqlBuilder.toString();
		Query query = this.getSession()
				.createSQLQuery(sql)
				.addScalar("name",StringType.INSTANCE)
				.addScalar("volume",DoubleType.INSTANCE)
				.addScalar("rebate",DoubleType.INSTANCE)
				.addScalar("OrderUserId",IntegerType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(PojoUserRebate.class));
		result=query.list();
		return result;
	}
	
	
	
}
