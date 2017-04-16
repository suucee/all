package com.maxivetech.backoffice.service.admin;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.maxivetech.backoffice.entity.RebateRecords;
import com.maxivetech.backoffice.pojo.PojoRebateReportUsers;
import com.maxivetech.backoffice.pojo.PojoRebates;
import com.maxivetech.backoffice.pojo.PojoUserAndMonth;
import com.maxivetech.backoffice.pojo.PojoUserBalances;
import com.maxivetech.backoffice.pojo.PojoVolumes;
import com.maxivetech.backoffice.util.Page;


@Service
public interface AdminRebateService {
	//手数
	public List<PojoVolumes> getStaffVolumeList(int year, int month, HttpSession session);
	public List<PojoVolumes> getAgentVolumeList(int year, int month, HttpSession session);
	public List<PojoVolumes> getCustomerVolumeList(int year, int month, HttpSession session);
	//返佣统计
	public List<PojoRebateReportUsers> reportUsers(int year, int month, String startDate, String endDate, HttpSession session);
	public List<PojoRebateReportUsers> reportSourceUsers(int year, int month, String startDate, String endDate, HttpSession session);
	public List<PojoRebateReportUsers> reportSourceLogins(int year, int month, String startDate, String endDate, HttpSession session);
	
	//发放返佣
	public void sendRebate(int[] userIds, HttpSession session);
	//返佣列表
	public List<PojoUserBalances> getRebateBalanceList(HttpSession session);
	/**
	 * 获取返佣报表
	 * @param pageNo
	 * @param pageSize
	 * @param urlFormat
	 * @param session
	 * @return
	 */
	//public List<PojoRebates> getRebatePage(int year, int month, HttpSession session);

	/**
	 * 按月发钱
	 * @param year
	 * @param month
	 * @param session
	 * @return
	 */
	//public List<RebateRecords> rebateByMonth(int year, int month, int[] userIdArray, double[] adjustAmountArray, HttpSession session);
	  public HashMap<String, Object> getRebateBalanceList1(HttpSession session);
	  public void sendRebate1(PojoUserAndMonth[] params, HttpSession session);
     
}
