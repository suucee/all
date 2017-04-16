package com.maxivetech.backoffice.patch;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * 为数据库中users的索引打补丁（去除唯一性约束,改为normal索引）
 *
 */
public class UsersIndexPatch {
	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Resource
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public UsersIndexPatch(){
		
	}
	
	/**
	 * 注解-PostConstruct：在容器初始化完成之后执行（以免Session还没有初始化）
	 */
	@PostConstruct
	public  void patch(){
		System.out.println("准备更改users表中的索引：email...");
		Transaction  transaction = null;
		try{
			if(sessionFactory != null){//Normal
				transaction  = sessionFactory.openSession().beginTransaction();
				//（去除唯一性约束,改为normal索引）
				String sql = "ALTER TABLE users DROP INDEX email , ADD INDEX email (email) USING BTREE";
				sessionFactory.openSession().createSQLQuery(sql).executeUpdate();
				
				transaction.commit();
				System.out.println("更改成功");
			}
		}catch(Exception e){
			System.out.println("更改失败，原因如下");
			e.printStackTrace();
			if(sessionFactory != null && transaction != null){
				transaction.rollback();
			}
		}
	}
	
}
