package com.maxivetech.backoffice.dao;

import java.util.HashMap;
import java.util.List;

import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoMT4Rebate;
import com.maxivetech.backoffice.pojo.PojoMonthRebate;
import com.maxivetech.backoffice.pojo.PojoRankUserCensus;
import com.maxivetech.backoffice.pojo.PojoTree;
import com.maxivetech.backoffice.pojo.PojoUserRebate;

public interface UserDao extends _BaseDao {
	public Users findByMobile(String mobile);
	public Users findByEmail(String email);
	public Users findByUserCode(String userCode);
	public List<HashMap<String,Object>> findByUserKeyWord(String keyword);
	public HashMap<String, Object> getOneHashUser(int user_id);
	public List<HashMap<String, Object>> findRankUserList(String userpath, int userlevel, int level);
	public List<HashMap<String, Object>> findRankUserList(String userpath, int userlevel, int plusLevel, String keyWord);
	public HashMap<String, Object> getOneUserBySQL(String sql);
	public List<Users> getCustomers(Users user);
	public List<PojoTree> findTreeUserList(String userpath, int userlevel, int plusLevel,boolean allocation);
	public List<PojoRankUserCensus> findRankUserCensusList(Users user, int plusLevel,String keyWord);
	public List<PojoMonthRebate> getMonthRebate(Users user);
	public List<PojoMT4Rebate> getMT4Rebate(Users user);
	public List<PojoUserRebate> getUserRebate(Users user);
	public List<PojoMonthRebate> getUserMonthRebate(Users user, Users orderUser);
}
