package com.maxivetech.backoffice.dao;

import java.util.HashMap;
import java.util.List;

import com.maxivetech.backoffice.entity.EmailValidation;
import com.maxivetech.backoffice.entity.Users;

public interface EmailValidationDao extends _BaseDao {
	public EmailValidation findByEmial(Users user);
}
