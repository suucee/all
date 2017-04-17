package com.yasinshaw.util;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Demo {  
	  
    static String sql = null;  
    static DB db1 = null;  
    static ResultSet ret = null;  
  
    public static void main(String[] args) {  
        sql = "select *from student";//SQL语句  
        db1 = new DB(sql);//创建DBHelper对象  
  
        try {  
            ret = db1.pst.executeQuery();//执行语句，得到结果集  
            while (ret.next()) {  
                String uid = ret.getString(1);  
                String uname = ret.getString(2);   
                String uage = ret.getString(3);  
                System.out.println(uid + "\t" + uname + "\t" + uage );  
            }//显示数据  
            ret.close();  
            db1.close();//关闭连接  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    }  
  
}