<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    String name = request.getParameter("name");
    String pass = request.getParameter("pass");
    if (name.equals("admin") && pass.equals("1")) {
        session.setAttribute("yonghu", name);
        request.getRequestDispatcher("main.jsp").forward(request, response);
    } else {
        response.sendRedirect("login.jsp");
    }
%>
