<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
<style>
    html,body{
        width: 100%;
        height: 100%;
        margin: 0px;
        padding: 0px;
    }
    .top{
        width: 100%;
        height: 60px;
        background-color: #1e90ff;
        border-bottom:solid 0px #a9a9a9 ;
    }
    .mid{
        width: 100%;
        height: 40px;
        background-color: #004080;
        border-bottom:solid 0px #a9a9a9 ;
    }
    span{
        color: #ffffff;
    }
</style>
</head>
<body>
 <div class="top">
   <table border="0" style="width: 100%;height: 100%">
       <tr>
           <td>
               <img src="imags/login_logo_shiyou.png">
           </td>
           <td width="20%" align="center">

               欢迎您：<%= session.getAttribute("yonghu")%>
           </td>
       </tr>
   </table>
 </div>
 <div class="mid">
     &nbsp;&nbsp;&nbsp;
     <a href="#" style="line-height: 40px">
         <span>用户管理</span>
     </a>
 </div>
</body>
</html>
