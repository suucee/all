package com.maxivetech.backoffice.dao;

import java.util.List;

import com.maxivetech.backoffice.entity.Admins;
import com.maxivetech.backoffice.entity.Settings;
import com.maxivetech.backoffice.entity.Users;

public interface SettingDao extends _BaseDao {
	public Settings findByKey(String key);
	
	public void set(String key, String stringValue);
	public void set(String key, int intValue);
	public void set(String key, double doubleValue);
	
	public String getString(String key);
	public int getInt(String key);
	public double getDouble(String key);
	
	public List<Settings> getList();
}
