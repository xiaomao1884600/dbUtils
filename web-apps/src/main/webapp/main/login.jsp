<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>登录</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    

  </head>
  
  <body>
    <form action="<%=basePath %>Login?action=login" METHOD="POST">
    	<div>
    		<label>用户名:</label>
    		<input name="username"/>
    	</div>
    	<div>
    		<label>密码:</label>
    		<input name="passowrd" type="password"/>
    		<input type="submit" value="登录">
    	</div>
    </form>
  </body>
</html>
