package com.service;

//����service,ʵ����ɾ�Ĳ鹦��
public interface BaseService {
	public boolean add(Object object);
	public boolean update(Object object);
	public boolean deleteById(int id);
	public boolean getById(int id);
}
