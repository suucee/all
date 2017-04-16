package com.maxivetech.backoffice.dao;

import com.maxivetech.backoffice.entity.UserProfiles;
import com.maxivetech.backoffice.entity.UserQuestions;
import com.maxivetech.backoffice.entity.Users;

public interface UserProfilesDao extends _BaseDao {

	public UserProfiles findUserProfilesByUser(Users user);
	public String getUserName(Users user);
}
