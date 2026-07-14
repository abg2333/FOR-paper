<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
<style>
    .main{
        text-align: center;
        background-color: #ffffff;
        width: 800px;
        height: 385px;
        position: absolute;
        margin: auto;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
    }
    .subMain{
        text-align: center;
        background-color: #ffffff;
        background-image: url("imags/login.png");
        width: 800px;
        height: 325px;
        border-radius: 10px;
    }
</style>
</head>
<body background="imags/login_back.jpg">

<div class="main">
 <table border="0" style="width: 100%;height: 100%">
     <tr style="height: 60px">
         <td>
            <img src="imags/login_logo_shiyou.png">
         </td>
     </tr>
     <tr>
         <td>
            <div class="subMain">
             <table border="0" style="width: 100%;height: 100%">
             <tr>
                 <td width="50%">

                     <form action="<%=request.getContextPath()%>/LoginServlet" method="get">
                     <table border="0" style="width: 100%;height: 100%">
                         <tr style="height: 50px">
                             <td width="110px"></td>
                             <td width="70px"> 用户名：</td>
                             <td><input type="text" name="name" /></td>
                         </tr>
                         <tr style="height: 50px">
                             <td></td>
                             <td> 密&nbsp;&nbsp;&nbsp;码：</td>
                             <td><input type="password" name="pass" /></td>
                         </tr>
                         <tr style="height: 50px">
                             <td></td>
                             <td align="center"><input type="submit" value="登录"></td>
                             <td align="center"><input type="reset" value="取消"></td>
                         </tr>
                     </table>
                     </form>
                 </td>
                 <td></td>
             </tr>
             </table>
            </div>
         </td>
     </tr>
 </table>
</div>
</body>
</html>
