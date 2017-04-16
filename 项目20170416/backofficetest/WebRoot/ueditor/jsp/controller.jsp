<%@page import="com.maxivetech.backoffice.BackOffice"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="com.baidu.ueditor.ActionEnter"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%

    request.setCharacterEncoding( "utf-8" );
	response.setHeader("Content-Type" , "text/html");
	
	String rootPath = application.getRealPath( "/" );
	
	String saveRootPath = BackOffice.getInst().UPLOAD_REAL_ROOT;

	out.write( new ActionEnter( request, saveRootPath, rootPath ).exec() );
	
%>