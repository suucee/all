package com.yasinshaw.util;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Demo {  
	  
    static String sql = null;  
    static DB db1 = null;  
    static ResultSet ret = null;  
  
    public static void main(String[] args) {  
        sql = "select *from student";//SQL���  
        db1 = new DB(sql);//����DBHelper����  
  
        try {  
            ret = db1.pst.executeQuery();//ִ����䣬�õ������  
            while (ret.next()) {  
                String uid = ret.getString(1);  
                String uname = ret.getString(2);   
                String uage = ret.getString(3);  
                System.out.println(uid + "\t" + uname + "\t" + uage );  
            }//��ʾ����  
            ret.close();  
            db1.close();//�ر�����  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    }  
  
}