package com.maxivetech.backoffice.service.user;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

@Service
public interface User2RankService {
	public HashMap<String, Object> getTheRankNextUser(String keyWord,HttpSession session);

	public HashMap<String, Object> getMonthRebate(HttpSession session);

	public HashMap<String, Object> getRebateUserAndMT4(HttpSession session);

	public HashMap<String, Object> getUserRank(String keyWord, HttpSession session);
}
