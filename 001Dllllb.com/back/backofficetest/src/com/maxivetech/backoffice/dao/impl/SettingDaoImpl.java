package com.maxivetech.backoffice.dao.impl;
import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.maxivetech.backoffice.dao.SettingDao;
import com.maxivetech.backoffice.entity.Settings;


@Repository
public class SettingDaoImpl extends _BaseDaoImpl implements SettingDao {
	@Override
	public Class<?> classModel() {return Settings.class;}

	@Override
	@CacheEvict(value = "settings", key = "#key")
	public void set(String key, String stringValue) {
		Settings setting = this.findByKey(key);
		if (setting != null) {
			setting.setType("String");
			setting.setStringValue(stringValue);
			this.update(setting);
		}
	}

	@Override
	@CacheEvict(value = "settings", key = "#key")
	public void set(String key, int intValue) {
		Settings setting = this.findByKey(key);
		if (setting != null) {
			setting.setType("int");
			setting.setIntValue(intValue);
			this.update(setting);
		}
	}

	@Override
	@CacheEvict(value = "settings", key = "#key")
	public void set(String key, double doubleValue) {
		Settings setting = this.findByKey(key);
		if (setting != null) {
			setting.setType("double");
			setting.setDoubleValue(doubleValue);
			this.update(setting);
		}
	}

	@Override
	public String getString(String key) {
		Settings setting = this.findByKey(key);
		return setting != null ? setting.getStringValue() : "";
	}

	@Override
	public int getInt(String key) {
		Settings setting = this.findByKey(key);
		return setting != null ? setting.getIntValue() : 0;
	}

	@Override
	public double getDouble(String key) {
		Settings setting = this.findByKey(key);
		return setting != null ? setting.getDoubleValue() : 0;
	}

	@Override
	public List<Settings> getList() {
		List<Settings> list = this.createCriteria()
			.addOrder(Order.asc("groups"))
			.addOrder(Order.asc("name"))
			.list();
	
		return list;
	}

	@Override
	@Cacheable(value = "settings", key = "#key")
	public Settings findByKey(String key) {
		List<Settings> list = this.createCriteria()
			.add(Restrictions.eq("key", key))
			.list();
		
		return list != null && list.size() > 0 ? list.get(0) : null;
	}


}
