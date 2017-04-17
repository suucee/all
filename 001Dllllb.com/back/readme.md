jdk:jdk1.8.0_31

相关的配置文件全部在src/main/resources/application.properties里

需要自己在本地新建数据库，数据库名为:dllllb
数据库用户密码均为：suucee

直接用java运行com.ApplicationMain.java即可启动项目，无需使用tomcat等web容器。
也可以使用mvn spring-boot:run 来启动项目

浏览器访问localhost:8080，如果显示：this is index page。则说明项目调通。
