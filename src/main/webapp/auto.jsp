<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Login Example</title>
</head>
<body>
<html:form action="/auto/suc" focus="userName">
    Username : <html:text property="userName" />
    <br>
    Password : <html:password property="password" />
    <br>
    <html:submit value="login" />
</html:form>

</body>
</html>
