package com.yasinshaw.service;

//基础service,实现增删改查功能
public interface BaseService {
	public boolean add(Object object);
	public boolean update(Object object);
	public boolean deleteById(int id);
	public boolean getById(int id);
}
