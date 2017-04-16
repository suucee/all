package com.maxivetech.backoffice.service.user;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.util.Page;

@Service
public interface UserRankService {
	public Page<Users> getAllUser(int pageNo, int pageSize, String urlFormat,
			HttpSession session);

	public HashMap<String, Object> getTheUserRank(int id, HttpSession session);

	public HashMap<String, Object> getTheRankNextUser(HttpSession session);
	public HashMap<String, Object> getStaffRank(HttpSession session);
	
	public UserProfiles getOneUserInfo(int id, HttpSession session);

	public List<HashMap<String, Object>> findUserByReferralCode(String code,
			HttpSession session);

	public String getReferralCode(HttpSession session);

	public HashMap<String, Object> getRankUserOrder(int pageNo, int pageSize,
			int rank_user_id, String urlFormat, String start, String end,
			HttpSession session);
	/**
	 * 查看客户下线返佣报表
	 * @param pageNo
	 * @param pageSize
	 * @param urlFormat
	 * @param startYear
	 * @param startMonth
	 * @param session
	 * @return
	 */
	public Page<HashMap<String, Object>> getUserRebatePage(int pageNo, int pageSize,
			String urlFormat, String startYear, String startMonth,
			HttpSession session);
}
