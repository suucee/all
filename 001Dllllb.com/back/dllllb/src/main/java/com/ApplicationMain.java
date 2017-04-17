package com;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.springframework.boot.SpringApplication;  
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;  
import org.springframework.context.annotation.Configuration;  
  
@Configuration//类级别的注解，一般这个注解，我们用来标识main方法所在的类 
@EnableAutoConfiguration//启用自动配置  
@ComponentScan//类级别的注解，自动扫描加载所有的Spring组件包括Bean注入，一般用在main方法所在的类上 
public class ApplicationMain {  
    public static void main(String[] args) throws Exception {  
        //启动Spring Boot项目的唯一入口  
        SpringApplication.run(ApplicationMain.class, args);  
    }  
    @Bean  
    public SessionFactory sessionFactory(HibernateEntityManagerFactory hemf){  
        return hemf.getSessionFactory();  
    } 
}  