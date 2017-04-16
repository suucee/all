package com.maxivetech.backoffice.dao;

import com.maxivetech.backoffice.entity.UserQuestions;
import com.maxivetech.backoffice.entity.Users;

public interface UserQuestionsDao extends _BaseDao {

	public UserQuestions findUserQuestionsByUser(Users user);
	
}
