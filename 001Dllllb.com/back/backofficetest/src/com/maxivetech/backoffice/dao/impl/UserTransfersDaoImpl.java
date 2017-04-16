package com.maxivetech.backoffice.dao.impl;

import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.UserTransfersDao;
import com.maxivetech.backoffice.entity.Transfers;

@Repository
public class UserTransfersDaoImpl extends _BaseDaoImpl implements UserTransfersDao {
	@Override
	public Class<?> classModel() {
		return Transfers.class;
	}

}
