<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-nested" prefix="nested"%>
<html:html>
    <head>
        <title>success.jsp</title>
        <html:base />
    </head>


        <br>
       <%= request.getAttribute("message") %>
    <br>
    <a href="/auto/myAuto.do" >back</a>

</html:html>