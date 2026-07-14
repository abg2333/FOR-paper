package org.example.app.user;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/sample012")
public class Sample012 extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String op = request.getParameter("op");
        UserDao2 userDao = new UserDao2();

        if ("selectAll".equals(op)) {
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(userDao.selectAll());

        } else if ("add".equals(op)) {
            String name = request.getParameter("name");
            String pass = request.getParameter("pass");
            String xingming = request.getParameter("xingming");
            String zhuanye = request.getParameter("zhuanye");
            String banji = request.getParameter("banji");
            String xingbie = request.getParameter("xingbie");
            String tel = request.getParameter("tel");

            String trimmedXingming = xingming.trim();

            UserBean userBean = new UserBean();
            userBean.setName(name);
            userBean.setPass(pass);
            userBean.setXingbie(xingbie);
            userBean.setXingming(trimmedXingming);
            userBean.setBanji(banji);
            userBean.setZhuanye(zhuanye);
            userBean.setTel(tel);
            userDao.save(userBean);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
