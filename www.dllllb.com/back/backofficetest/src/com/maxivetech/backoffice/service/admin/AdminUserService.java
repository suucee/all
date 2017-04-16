package com.maxivetech.backoffice.service.admin;

import java.util.List;
import java.util.HashMap;

import javax.servlet.http.HttpSession;










import com.maxivetech.backoffice.entity.Attachments;
import com.maxivetech.backoffice.entity.UserBalanceLogs;
import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoCounts;
import com.maxivetech.backoffice.pojo.PojoSession;
import com.maxivetech.backoffice.util.Page;


public interface AdminUserService {
	public Page<Users> getPage(int pageNo, int pageSize, String urlFormat, int upId, String state, String keyword, HttpSession session);
	
	/**
	 * 查询所有的用户
	 * @param pageNo
	 * @param pageSize
	 * @param urlFormat
	 * @param state
	 * @param keyword
	 * @param session
	 * @return
	 */
	public Page<HashMap<String, Object>> getPage(int pageNo, int pageSize, String urlFormat,
			String state, String keyword, HttpSession session);
	/**
	 * 获取条数
	 * @param session
	 * @return
	 */
	public List<PojoCounts> getCounts(HttpSession session);
	/**
	 * 获取用户银行卡账号
	 * @param userId
	 * @param pageNo
	 * @param pageSize
	 * @param urlFormat
	 * @param session
	 * @return
	 */
	public Page<UserBankAccounts> getUserBankAccountList(int userId, int pageNo,
			int pageSize, String urlFormat, HttpSession session);
	/**
	 * 获取用户余额变动日志
	 * @param userId
	 * @param pageNo
	 * @param pageSize
	 * @param urlFormat
	 * @param session
	 * @return
	 */
	public Page<UserBalanceLogs> getUserBalanceLogs(int userId, int pageNo, int pageSize,
			String urlFormat, HttpSession session);
    /**
     * 获取用户资料
     * @param user_id
     * @param session
     * @return
     */
	

	public HashMap<String, Object> getUserProfile(int user_id,HttpSession session);
	/**
	 * 获取用户图片
	 * @param id
	 * @param session
	 * @return
	 */
	List<Attachments> getUserImage(int id, HttpSession session);  

	public boolean updateProfile(int userId, String name, String ename, String cardType,
			String cardID, String countryCode, String address, String company,
			String userIndustry, String userYearsIncom, String position,
			String bankName, String bankNo, String cardholder_Name,
			String countryAdress,String swiftCode,String ibanCode,String bankBranch,String bankAddress,String attachment_id, HttpSession session);
	public Users getOne(int userId, HttpSession session);
	/**
	 * 重置用户密码并发送邮件
	 * @param login
	 * @param session
	 * @return
	 */
	public boolean resetUserPassword(int userId, HttpSession session);
	public UserBankAccounts getBank(int userId, HttpSession session);
	public boolean removeImg(int id, HttpSession session);
	public List<Attachments> getproFileImage(int userId, HttpSession session);
	public UserProfiles getProfile(int userId, HttpSession session);
	
	public void modifyAccount(int userId, String email, String mobile, int upId, HttpSession session);
	public List<HashMap<String, Object>> getList(HttpSession session);
	
	public HashMap<String, Object> findByMobile(String mobile, HttpSession session);
	
	public PojoSession holdUser(int userId, HttpSession session);

	public List<Attachments> getBankImageList(int id, HttpSession session);

	public Page<HashMap<String, Object>> getApplyAgent(int pageNo, int pageSize, String urlFormat, String keyword,
			HttpSession session);

	public boolean sendAagreementEmail(int id, HttpSession session);


}
