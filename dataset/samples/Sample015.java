package org.example.app.user;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/sample015")
public class Sample015 extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String op = request.getParameter("op");
        UserDao2 userDao = new UserDao2();

        if ("selectAll".equals(op)) {
            response.getWriter().write(userDao.selectAll());

        } else if ("add".equals(op)) {
            String name = request.getParameter("name");
            String pass = request.getParameter("pass");
            String xingming = request.getParameter("xingming");
            String zhuanye = request.getParameter("zhuanye");
            String banji = request.getParameter("banji");
            String xingbie = request.getParameter("xingbie");
            String tel = request.getParameter("tel");
            UserBean userBean = new UserBean();
            userBean.setName(name);
            userBean.setPass(pass);
            userBean.setXingbie(xingbie);
            userBean.setXingming(xingming);
            userBean.setBanji(banji);
            userBean.setZhuanye(zhuanye);
            userBean.setTel(tel);
            userDao.save(userBean);

        } else if ("selectUser".equals(op)) {
            String xingming = request.getParameter("xingming");
            response.getWriter().write(userDao.seletcUser(xingming));
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
