package com.maxivetech.backoffice.service.user;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.entity.Attachments;
import com.maxivetech.backoffice.entity.UserBankAccounts;
import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.Users;
import com.maxivetech.backoffice.pojo.PojoSession;
import com.maxivetech.backoffice.util.Page;


public interface UserService {
	public boolean modifyPassword(String oldPassword, String newPassword, HttpSession session);
	/**
	 * 获取用户详细信息
	 * @param session
	 * @return
	 */
	public UserProfiles getProfiles( HttpSession session);
	/**
	 * 获取用户资料证明图片
	 * @param session
	 * @return
	 */
	public List<Attachments> getproFileImage(HttpSession session);
	/**
	 *获取用户绑定的银行卡信息
	 * @param session
	 * @return
	 */
	public UserBankAccounts  getBank( HttpSession session);
//	public boolean delImg(int id);
	public boolean removeImg(int id, HttpSession session);
	/**
	 * 重新提交资料
	 * @param name
	 * @param ename
	 * @param cardType
	 * @param cardID
	 * @param countryCode
	 * @param address
	 * @param company
	 * @param userIndustry
	 * @param userYearsIncom
	 * @param position
	 * @param bankName
	 * @param bankNo
	 * @param cardholder_Name
	 * @param countryAdress
	 * @param session
	 * @return
	 */
	public String updateProfile(String name, String ename, String cardType,
			String cardID, String countryCode, String address, String company,
			String userIndustry, String userYearsIncom, String position,
			String bankName, String bankNo, String cardholder_Name,
			String countryAdress,String swiftCode,String ibanCode,String bankBranch,String bankAddress,String attachment_id, HttpSession session);
	public boolean offEmail(HttpSession session);
	public String state(HttpSession session);
	public boolean checkEmail(HttpSession session);
	public boolean modifyOperationPassword(String oldPassword, String newPassword, HttpSession session);
	public boolean operationPwd(HttpSession session);
	public boolean checkOperationPwd(HttpSession session);
	public List<Attachments> getBankImageList(HttpSession session);
}
